package pv.projects.mediasoft.com.pventes.services;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import pv.projects.mediasoft.com.pventes.MainActivity;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;
import pv.projects.mediasoft.com.pventes.dao.CommercialDAO;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseOperationDAO;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.database.CommercialHelper;
import pv.projects.mediasoft.com.pventes.database.CompteBanqueHelper;
import pv.projects.mediasoft.com.pventes.database.DeviseOperationHelper;
import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.database.OperationHelper;
import pv.projects.mediasoft.com.pventes.database.PartenaireHelper;
import pv.projects.mediasoft.com.pventes.database.ProduitHelper;
import pv.projects.mediasoft.com.pventes.model.Caisse;
import pv.projects.mediasoft.com.pventes.model.Commercial;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.DeviseOperation;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.model.PointVente;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.utiles.Url;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

/**
 * Created by mediasoft on 17/11/2016.
 */
public class OperationSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int ID_NOTIFICATION = 1;
    public static final String SYNCAUTO = "SyncAuto";
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    Context context = null ;
    private long id;


    private int etape = 1;
    public static int sync = 0 ;
    private ProduitDAO produitDAO;
    private CompteBanqueDAO compteBanqueDAO;
    private OperationDAO operationDAO;
    private MouvementDAO mouvementDAO;
    private PartenaireDAO partenaireDAO;
    private CommercialDAO commercialDAO;
    private int PROGRESS = 0 ;
    private PointVenteDAO pointVenteDAO;
    private CaisseDAO caisseDAO;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public OperationSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context ;
        sync = 1 ;
        initialisation();
    }

    private void initialisation() {
        caisseDAO = new CaisseDAO(context);
        compteBanqueDAO = new CompteBanqueDAO(context);
        operationDAO = new OperationDAO(context) ;
        produitDAO = new ProduitDAO(context) ;
        mouvementDAO = new MouvementDAO(context) ;
        partenaireDAO = new PartenaireDAO(context) ;
        commercialDAO = new CommercialDAO(context) ;
        preferences = PreferenceManager.getDefaultSharedPreferences(context) ;
        editor = preferences.edit() ;
    }



    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.e("SYNC AUTO ADAPTER", String.valueOf(preferences.getBoolean(SYNCAUTO,false))) ;
        /*
        //if (preferences.getBoolean(SYNCAUTO,false)) return;
        etape = 1 ;
        sync = 1 ;

        // Activer la sync Auto
        editor = preferences.edit() ;
        editor.putBoolean(SYNCAUTO,true) ;
        editor.commit() ;
        Log.e("SYNC AUTO ADAPTER DEBUT", String.valueOf(preferences.getBoolean(SYNCAUTO,false))) ;

        // Annuler la sync Auto
        editor = preferences.edit() ;
        editor.remove(SYNCAUTO) ;
        editor.commit() ;
        Log.e("SYNC AUTO ADAPTER FIN", String.valueOf(preferences.getBoolean(SYNCAUTO,false))) ;

        */


        // Vérifier si la caisse est toujours active
        synchronisationCaisse();

        if (preferences.getBoolean("stateonoff",false)){
            synchronisationAttente();
        }

        etape = 1 ;
        startSync();


        sync = getSyncData() ;

        if (sync>0) {

            int icon = 0 ;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) icon = R.drawable.notif_icon;
            else  icon = R.mipmap.ic_launcher ;
            CharSequence tickerText = String.valueOf(sync) + " " + context.getString(R.string.operation);
            long when = System.currentTimeMillis();

            Intent notificationIntent = null;


            notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra("POS", 1);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = builder.setContentIntent(contentIntent)
                    .setSmallIcon(icon)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.echec_app_name)))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(tickerText))
                    .setTicker(tickerText)
                    .setWhen(when)
                    .setContentText(tickerText)
                    .setContentTitle(context.getString(R.string.echec_app_name))
                    .build();
            notification.defaults |= Notification.DEFAULT_LIGHTS;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            // Récupération du Notification Manager
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(ID_NOTIFICATION, notification);
        }
        else {
            etape=1 ;
            //Toast.makeText(context, R.string.sync_reussi, Toast.LENGTH_LONG).show();
        }

        Log.e("SYNC AUTO", String.valueOf(preferences.getBoolean(SYNCAUTO,false))) ;

        sync = 0 ;
    }
    
    
    private void startSync() {
        Log.e("SYNC","LANCER") ;

        int sync = getSyncData() ;
        Log.e("NBRE ", String.valueOf(sync)) ;

        if (sync==0 && etape==1) {
            //Toast.makeText(context, R.string.nosyncdata, Toast.LENGTH_SHORT).show();
            return;
        }

        if (produitDAO.getNonSyncInterval(null,null).size() != 0 && etape == 1){
            Log.e("SYNC","PRODUIT LANCE") ;
            synchronisationProduit() ;
            return;
        }
        else if (etape==1)etape = 2 ;


        if (partenaireDAO.getNonSync().size() != 0 && etape == 2){
            Log.e("SYNC","PARTENAIRE LANCE") ;
            synchronisationPartenaire() ;
            return;
        }
        else if (etape==2) etape = 3 ;

        if (commercialDAO.getNonSync().size() != 0 && etape == 3){
            Log.e("SYNC","COMMERCIAL LANCE") ;
            synchronisationCommercial() ;
            return;
        }
        else if (etape==3)etape = 4 ;

        if (compteBanqueDAO.getNonSyncInterval().size() != 0 && etape == 4){
            Log.e("SYNC","COMMERCIAL LANCE") ;
            synchronisationCompteBanque() ;
            return;
        }
        else if (etape==4)etape = 5 ;


        if (operationDAO.getNonSync().size() != 0 && etape == 5){
            Log.e("SYNC","OPERATION LANCE") ;
            synchronisationOperation() ;
            return;
        }
        else if (etape==5)etape = 6 ;

        if (mouvementDAO.getNonSync().size() != 0 && etape == 6){
            Log.e("SYNC","MOUVEMENT LANCE") ;
            synchronisationMouvement() ;
            return;
        }
        else if (etape==6)etape = 7 ;

    }


    private int getSyncData() {
        return  compteBanqueDAO.getNonSyncInterval().size() +    produitDAO.getNonSyncInterval(null,null).size() +   partenaireDAO.getNonSync().size() + commercialDAO.getNonSync().size() +  mouvementDAO.getNonSync().size() +  operationDAO.getNonSync().size();
    }




    private void synchronisationCaisse() {

        CaisseDAO caisseDAO = new CaisseDAO(context);
        PointVenteDAO pointVenteDAO = new PointVenteDAO(context);
        PointVente pointVente = pointVenteDAO.getLast() ;

        FormBody.Builder formBuilder = new FormBody.Builder() ;
        if (pointVente!=null) formBuilder.add("pointvente" , String.valueOf(pointVente.getId()));

        String result = "";
        try {
            result = Utiles.POST(Url.getCheckCaisseValidation(context),formBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.split(":").length == 3 && result.contains("OK")) {
            Caisse caisse = caisseDAO.getFirst() ;
            caisse.setActif(Integer.parseInt(result.split(":")[1]));
            caisse.setRaison(result.split(":")[2]);
            caisseDAO.update(caisse) ;
        }

        startSync();
    }


    private void synchronisationProduit() {

        ProduitDAO produitDAO = null;
        MouvementDAO mouvementDAO = null;
        ArrayList<Produit> produits = null;


        if (produitDAO == null)  produitDAO = new ProduitDAO(context);
        if (mouvementDAO == null)    mouvementDAO = new MouvementDAO(context);
        produits = produitDAO.getNonSyncInterval(null, null);
        pointVenteDAO = new PointVenteDAO(context) ;


        Produit produit = null;
        int nbre = 0 ;
            for (int i = 0; i < produits.size(); ++i) {
                produit = produits.get(i);

                FormBody.Builder formBuilder = new FormBody.Builder() ;
                formBuilder.add(ProduitHelper.CODE, produit.getCode());
                formBuilder.add(ProduitHelper.AFFICHABLE, produit.getAffichable());
                formBuilder.add(ProduitHelper.UTILISATEUR_ID, String.valueOf(produit.getUtilisateur_id()));
                formBuilder.add(ProduitHelper.CATEGORIE_PRODUIT, String.valueOf(produit.getCategorie_id()));
                formBuilder.add(ProduitHelper.LIBELLE, String.valueOf(produit.getLibelle()));
                formBuilder.add(ProduitHelper.PRIXACHAT, String.valueOf(produit.getPrixA()));
                formBuilder.add(ProduitHelper.PRIXVENTE, String.valueOf(produit.getPrixV()));
                formBuilder.add(ProduitHelper.ID_EXTERNE, String.valueOf(produit.getId_externe()));
                formBuilder.add(ProduitHelper.UNITE, String.valueOf(produit.getUnite()));
                formBuilder.add(ProduitHelper.CREATED_AT, DAOBase.formatter.format(produit.getCreated_at()));
                formBuilder.add(ProduitHelper.MODIFIABLE, String.valueOf(produit.getModifiable()));
                formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId()));



                Log.e("ID EXTERNE", String.valueOf(produit.getId_externe()));
                String result = "" ;

                try {
                    result = Utiles.POST(Url.getPostProduitUrl(context),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("URL", Url.getPostProduitUrl(context));
                Log.e("RESULLLTT" + String.valueOf(i), result);


                if (result.split(":").length == 3 && result.contains("OK")) {
                    produit.setEtat(2);
                    produit.setCode(result.split(":")[1]);
                    // Si le produit est un produit qui ne se trouve pas sur le serveur
                    if (produit.getId_externe()==0){
                        produit.setId_externe(Long.valueOf(result.split(":")[2]));
                        // on met à jour le mouvement en remplacant l'id interne par l'id externe
                        mouvementDAO.updateMany(produit) ;
                    }
                    synchronisationProduitImage(produit);
                    produitDAO.update(produit);
                    nbre++ ;
                }
            }
        if (nbre==produits.size()) etape = 2 ;
        else  {
            etape = 1 ;
            return;
        }
        startSync();
    }

    private void synchronisationPartenaire() {

        PartenaireDAO partenaireDAO = null;
        MouvementDAO mouvementDAO = null;
        ArrayList<Partenaire> partenaires = null;


        if (partenaireDAO == null)    partenaireDAO = new PartenaireDAO(context);
        partenaires = partenaireDAO.getNonSync();
        pointVenteDAO = new PointVenteDAO(context) ;
        

        Partenaire partenaire = null;

        int nbre = 0 ;
            for (int i = 0; i < partenaires.size(); ++i) {
                partenaire = partenaires.get(i);

                FormBody.Builder formBuilder = new FormBody.Builder() ;
                formBuilder.add(PartenaireHelper.NOM, partenaire.getNom());
                formBuilder.add(PartenaireHelper.PRENOM, String.valueOf(partenaire.getPrenom()));
                formBuilder.add(PartenaireHelper.CONTACT, String.valueOf(partenaire.getContact()));
                formBuilder.add(PartenaireHelper.RAISONSOCIAL, String.valueOf(partenaire.getRaisonsocial()));
                formBuilder.add(PartenaireHelper.SEXE, String.valueOf(partenaire.getSexe()));
                formBuilder.add(PartenaireHelper.ID_EXTERNE, String.valueOf(partenaire.getId_externe()));
                formBuilder.add(PartenaireHelper.EMAIL, String.valueOf(partenaire.getEmail()));
                formBuilder.add(PartenaireHelper.CREATED_AT, DAOBase.formatter.format(partenaire.getCreated_at()));
                formBuilder.add(PartenaireHelper.TYPEPERSONNE, String.valueOf(partenaire.getTypepersonne()));
                formBuilder.add(PartenaireHelper.TYPEPARTENAIRE, String.valueOf(partenaire.getTypepartenaire()));
                formBuilder.add(PartenaireHelper.ADRESSE, String.valueOf(partenaire.getAdresse()));
                formBuilder.add(PartenaireHelper.UTILISATEUR_iD, String.valueOf(partenaire.getUtilisateur_id()));
                formBuilder.add(PartenaireHelper.POINTVENTE_iD, String.valueOf(partenaire.getPointvente_id()));
                //formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId()));

                Log.e("PV", String.valueOf(partenaire.getPointvente_id()));

                String result = "" ;

                try {
                    result = Utiles.POST(Url.getPostPartenaireUrl(context),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("URL", Url.getPostPartenaireUrl(context));
                Log.e("RESULLLTT" + String.valueOf(i), result);
                Log.e("CODE", String.valueOf(partenaire.getId_externe()));


                if (result.split(":").length == 2 && result.contains("OK")) {
                    partenaire.setEtat(2);
                    // Si le partenaire est un partenaire qui ne se trouve pas sur le serveur
                    if (partenaire.getId_externe()==0){
                        partenaire.setId_externe(Long.valueOf(result.split(":")[1]));

                        operationDAO.updateMany(partenaire) ;
                    }
                    partenaireDAO.update(partenaire);
                    nbre++ ;
                } 
            }

        if (nbre==partenaires.size()) etape = 3 ;
        else {
            etape = 2 ;
            return;
        }
        startSync();
    }

    private void synchronisationCommercial() {
        
        CommercialDAO commercialDAO = null;
        MouvementDAO mouvementDAO = null;
        ArrayList<Commercial> commercials = null;

        if (commercialDAO == null)    commercialDAO = new CommercialDAO(context);
        commercials = commercialDAO.getNonSync();
        pointVenteDAO = new PointVenteDAO(context) ;

         Commercial commercial = null;

        int nbre = 0 ;
            for (int i = 0; i < commercials.size(); ++i) {
                commercial = commercials.get(i);

                FormBody.Builder formBuilder = new FormBody.Builder() ;
                formBuilder.add(CommercialHelper.NOM, commercial.getNom());
                formBuilder.add(CommercialHelper.PRENOM, String.valueOf(commercial.getPrenom()));
                formBuilder.add(CommercialHelper.CONTACT, String.valueOf(commercial.getContact()));
                formBuilder.add(CommercialHelper.SEXE, String.valueOf(commercial.getSexe()));
                formBuilder.add(CommercialHelper.ID_EXTERNE, String.valueOf(commercial.getId_externe()));
                formBuilder.add(CommercialHelper.EMAIL, String.valueOf(commercial.getEmail()));
                formBuilder.add(CommercialHelper.CREATED_AT, DAOBase.formatter.format(commercial.getCreated_at()));
                formBuilder.add(CommercialHelper.ADRESSE, String.valueOf(commercial.getAdresse()));
                formBuilder.add(CommercialHelper.UTILISATEUR_iD, String.valueOf(commercial.getUtilisateur_id()));
                formBuilder.add(CommercialHelper.POINTVENTE_iD, String.valueOf(commercial.getPointvente_id()));
                //formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId()));

                Log.e("PV", String.valueOf(commercial.getPointvente_id()));


                String result = "" ;

                try {
                    result = Utiles.POST(Url.getPostCommercialUrl(context),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("URL", Url.getPostCommercialUrl(context));
                Log.e("RESULLLTT" + String.valueOf(i), result);
                Log.e("CODE", String.valueOf(commercial.getId_externe()));


                if (result.split(":").length == 2 && result.contains("OK")) {
                    commercial.setEtat(2);
                    // Si le commercial est un commercial qui ne se trouve pas sur le serveur
                    if (commercial.getId_externe()==0){
                        commercial.setId_externe(Long.valueOf(result.split(":")[1]));

                        operationDAO.updateMany(commercial) ;
                    }
                    commercialDAO.update(commercial);
                    nbre++ ;
                }
            }


        if (nbre==commercials.size()) etape = 4 ;
        else  {
            etape = 3 ;
            return;
        } ;

        startSync();
        
    }




    private void synchronisationCompteBanque() {

        CompteBanqueDAO compteBanqueDAO = null;
        MouvementDAO mouvementDAO = null;
        ArrayList<CompteBanque> compteBanques = null;

        if (compteBanqueDAO == null)    compteBanqueDAO = new CompteBanqueDAO(context);
        compteBanques = compteBanqueDAO.getNonSyncInterval();
        pointVenteDAO = new PointVenteDAO(context) ;

         CompteBanque compteBanque = null;

        int nbre = 0 ;
            for (int i = 0; i < compteBanques.size(); ++i) {
                compteBanque = compteBanques.get(i);

                FormBody.Builder formBuilder = new FormBody.Builder() ;
                formBuilder.add(CompteBanqueHelper.LIBELLE, compteBanque.getLibelle());
                formBuilder.add(CompteBanqueHelper.CODE, String.valueOf(compteBanque.getCode()));
                formBuilder.add(CompteBanqueHelper.CHEQUE, String.valueOf(compteBanque.getCheque()));
                formBuilder.add(CompteBanqueHelper.CARTEBANK, String.valueOf(compteBanque.getCartebanque()));
                formBuilder.add(CompteBanqueHelper.UTILISATEUR_ID, String.valueOf(compteBanque.getUtilisateur_id()));

                String result = "" ;

                try {
                    result = Utiles.POST(Url.getPostCompteBanqueUrl(context),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("URL", Url.getPostCompteBanqueUrl(context));
                Log.e("RESULLLTT" + String.valueOf(i), result);
                Log.e("CODES", String.valueOf(compteBanque.getUtilisateur_id() + " " + compteBanque.getCheque() + " " + compteBanque.getCartebanque() + " " + compteBanque.getCode() + " " +  compteBanque.getLibelle() + " - " + compteBanque.getId_externe()));
                Log.e("PV", String.valueOf(compteBanque.getUtilisateur_id()));


                if (result.split(":").length == 2 && result.contains("OK")) {
                    compteBanque.setEtat(2);
                    // Si le compteBanque est un compteBanque qui ne se trouve pas sur le serveur
                    if (compteBanque.getId_externe()==0){
                        compteBanque.setId_externe(Long.valueOf(result.split(":")[1]));

                        operationDAO.updateMany(compteBanque) ;
                    }
                    compteBanqueDAO.update(compteBanque);
                    nbre++ ;
                }
            }


        if (nbre==compteBanques.size()) etape = 5 ;
        else  {
            etape = 4 ;
            return;
        } ;

        startSync();

    }


    private void synchronisationOperation() {

        OperationDAO operationDAO = null;
        ArrayList<Operation> operations = null;
        ArrayList<DeviseOperation> deviseOperations = null;
        ArrayList<Mouvement> mouvements = null;
        MouvementDAO mouvementDAO;
        DeviseOperationDAO deviseOperationDAO;

        if (operationDAO == null)    operationDAO = new OperationDAO(context);
        operations = operationDAO.getNonSync();
        pointVenteDAO = new PointVenteDAO(context) ;
        mouvementDAO = new MouvementDAO(context) ;
        deviseOperationDAO = new DeviseOperationDAO(context) ;

        Operation operation = null;
        Mouvement mouvement = null ;

        Log.e("SIZE OP NON", String.valueOf(operations.size()));

        int nbre = 0 ;
            for (int i = 0; i < operations.size(); ++i) {
                operation = operations.get(i);
                if (operation.getOperation_id()>0){
                    Operation parent = operationDAO.getOne(operation.getOperation_id()) ;
                    if (parent!=null && parent.getEtat()==0) continue;
                    else if (parent!=null && parent.getEtat()>0){
                        operation.setOperation_id(parent.getId_externe());
                    }
                }
                FormBody.Builder formBuilder = new FormBody.Builder() ;
                formBuilder.add(OperationHelper.TABLE_KEY, String.valueOf(operation.getId_externe()));
                formBuilder.add(OperationHelper.REMISE, String.valueOf(operation.getRemise()));
                formBuilder.add(OperationHelper.MONTANT, String.valueOf(operation.getMontant()));
                formBuilder.add(OperationHelper.CAISSE_ID, String.valueOf(operation.getCaisse_id()));
                formBuilder.add(OperationHelper.TYPEOPERATION_ID, String.valueOf(operation.getTypeOperation_id()));
                formBuilder.add(OperationHelper.OPERATION_ID, String.valueOf(operation.getOperation_id()));
                formBuilder.add(OperationHelper.DATE_OPERATION, DAOBase.formatter.format(operation.getDateoperation()));
                formBuilder.add(OperationHelper.NBREPRODUIT, String.valueOf(operation.getNbreproduit()));
                formBuilder.add(OperationHelper.ANNULER, String.valueOf(operation.getAnnuler()));
                formBuilder.add(OperationHelper.PARTENAIRE_ID, String.valueOf(operation.getPartenaire_id()));
                formBuilder.add(OperationHelper.RECU, String.valueOf(operation.getRecu()));
                formBuilder.add(OperationHelper.ATTENTE, String.valueOf(operation.getAttente()));
                formBuilder.add(OperationHelper.ENTREE, String.valueOf(operation.getEntree()));
                formBuilder.add(OperationHelper.PAYER, String.valueOf(operation.getPayer()));
                formBuilder.add(OperationHelper.CLIENT, String.valueOf(operation.getClient()));
                formBuilder.add(OperationHelper.NUMCHEQUE, String.valueOf(operation.getNumcheque()));
                formBuilder.add(OperationHelper.MODEPAYEMENT, String.valueOf(operation.getModepayement()));
                formBuilder.add(OperationHelper.COMMERCIAL_ID, String.valueOf(operation.getCommercialid()));
                formBuilder.add(OperationHelper.COMPTEBANQUE_ID, String.valueOf(operation.getComptebanque_id()));
                formBuilder.add(OperationHelper.DESCRIPTION, operation.getDescription());
                formBuilder.add(OperationHelper.TOKEN, operation.getToken());
                formBuilder.add(OperationHelper.DATE_ECHEANCE, DAOBase.formatter.format(operation.getDateoperation()));
                formBuilder.add(OperationHelper.DATE_ANNULATION, DAOBase.formatter.format(operation.getDateannulation()));
                formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId()));

                Log.e("attente", String.valueOf(operation.getAttente())) ;
                Log.e("token",operation.getToken()) ;

                mouvements = mouvementDAO.getMany(operation.getId_externe()) ;
                formBuilder.add("nbre", String.valueOf(mouvements.size()));
                Log.e("SIZE MOUV NON", String.valueOf(mouvements.size()));


                for (int j = 0; j < mouvements.size(); ++j) {
                    mouvement = mouvements.get(j);

                    formBuilder.add(MouvementHelper.PRIXV+""+j, String.valueOf(mouvement.getPrixV()));
                    formBuilder.add(MouvementHelper.PRIXA+""+j, String.valueOf(mouvement.getPrixA()));
                    formBuilder.add(MouvementHelper.PRODUIT_ID+""+j, String.valueOf(mouvement.getProduit_id()));
                    formBuilder.add(MouvementHelper.PRODUIT+""+j, String.valueOf(mouvement.getProduit()));
                    formBuilder.add(MouvementHelper.ENTREE+""+j, String.valueOf(mouvement.getEntree()));
                    formBuilder.add(MouvementHelper.QUANTITE+""+j, String.valueOf(mouvement.getQuantite()));
                    formBuilder.add(MouvementHelper.DESCRIPTION+""+j, String.valueOf(mouvement.getDescription()));
                    formBuilder.add(MouvementHelper.ANNULER+""+j, String.valueOf(mouvement.getAnnuler()));
                    formBuilder.add(MouvementHelper.OPERATION_ID+""+j, String.valueOf(mouvement.getOperation_id()));
                    formBuilder.add(MouvementHelper.CREATED_AT+""+j, DAOBase.formatter.format(mouvement.getCreated_at()));
                }

                deviseOperations = deviseOperationDAO.getMany(operation.getId_externe()) ;
                formBuilder.add("nbredevises", String.valueOf(deviseOperations.size()));
                for (int j = 0; j < deviseOperations.size(); j++) {
                    DeviseOperation deviseOperation = deviseOperations.get(j) ;
                    formBuilder.add(DeviseOperationHelper.DEVISE_ID+""+j, String.valueOf(deviseOperation.getDevise_id()));
                    formBuilder.add(DeviseOperationHelper.MONTANT+""+j, String.valueOf(deviseOperation.getDevise_id()));
                }

                Log.e("Token",operation.getToken());
                String result = "" ;

                try {
                    result = Utiles.POST(Url.getPostOperationUrl(context),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("RESULLLTT" + String.valueOf(i), result);

                try {
                    if (result.split(":").length == 2 && result.contains("OK:")) {
                        operation.setId_externe(Long.valueOf(result.split(":")[1]));
                        operation.setEtat(2);
                        operationDAO.update(operation);
                        mouvementDAO.updateMany(operation) ;
                        deviseOperationDAO.updateMany(operation) ;
                        //operationDAO.updateMany(operation) ;
                        nbre++ ;
                    }
                    else break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        if (nbre==operations.size()) etape = 6 ;
        else  {
            etape = 5 ;
            return;
        } ;
        startSync();
    }

    private void synchronisationMouvement() {

        MouvementDAO mouvementDAO = null;
        ArrayList<Mouvement> mouvements = null;

        if (mouvementDAO == null)    mouvementDAO = new MouvementDAO(context);
        mouvements = mouvementDAO.getNonSync();
        pointVenteDAO = new PointVenteDAO(context) ;
        Mouvement mouvement = null;
        Produit produit = null ;

        int nbre = 0 ;
        Log.e("SIZE MOUV NON", String.valueOf(mouvements.size()));
            for (int i = 0; i < mouvements.size(); ++i) {
                mouvement = mouvements.get(i);
                FormBody.Builder formBuilder = new FormBody.Builder() ;
                //produit = produitDAO.getOneByIdExterne(mouvement.getProduit_id()) ;
                //if (produit.getEtat()==0) continue;
                formBuilder.add(MouvementHelper.PRIXV, String.valueOf(mouvement.getPrixV()));
                formBuilder.add(MouvementHelper.PRIXA, String.valueOf(mouvement.getPrixA()));
                formBuilder.add(MouvementHelper.PRODUIT_ID, String.valueOf(mouvement.getProduit_id()));
                formBuilder.add(MouvementHelper.PRODUIT, String.valueOf(mouvement.getProduit()));
                formBuilder.add(MouvementHelper.ENTREE, String.valueOf(mouvement.getEntree()));
                formBuilder.add(MouvementHelper.DESCRIPTION, String.valueOf(mouvement.getDescription()));
                formBuilder.add(MouvementHelper.QUANTITE, String.valueOf(mouvement.getQuantite()));
                formBuilder.add(MouvementHelper.ANNULER, String.valueOf(mouvement.getAnnuler()));
                formBuilder.add(MouvementHelper.OPERATION_ID, String.valueOf(mouvement.getOperation_id()));
                formBuilder.add(MouvementHelper.CREATED_AT, DAOBase.formatter.format(mouvement.getCreated_at()));
                formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId()));

                Log.e("prixV" + String.valueOf(i), String.valueOf(mouvement.getPrixV()));
                Log.e("prixA" + String.valueOf(i), String.valueOf(mouvement.getPrixA()));
                Log.e("Produit_ID" + String.valueOf(i), String.valueOf(mouvement.getProduit_id()));
                Log.e("Produit" + String.valueOf(i), String.valueOf(mouvement.getProduit()));
                Log.e("entree" + String.valueOf(i), String.valueOf(mouvement.getEntree()));
                Log.e("quantite" + String.valueOf(i), String.valueOf(mouvement.getQuantite()));
                Log.e("annuler" + String.valueOf(i), String.valueOf(mouvement.getAnnuler()));
                Log.e("operation_id" + String.valueOf(i), String.valueOf(mouvement.getOperation_id()));
                Log.e("created_at" + String.valueOf(i), DAOBase.formatter.format(mouvement.getCreated_at()));
                Log.e("pointvente_id" + String.valueOf(i), String.valueOf(pointVenteDAO.getLast().getId()));

                Log.e("URL" + String.valueOf(i), Url.getPostMouvementUrl(context));

                String result = "" ;

                try {
                    result = Utiles.POST(Url.getPostMouvementUrl(context),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("RESULT MOUVEMENT" + String.valueOf(i), result);

                try {
                    if (result.split(":").length == 2 && result.contains("OK:")) {
                        mouvement.setEtat(2);
                        mouvement.setRestant(Double.parseDouble(result.split(":")[1]));
                        mouvementDAO.update(mouvement);
                        nbre++ ;
                    }
                    else break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        if (nbre==mouvements.size()) etape = 7 ;
        else  {
            etape = 6 ;
            return;
        };

        startSync();
    }


    public void synchronisationProduitImage(Produit produit){

        ProduitDAO produitDAO = null;
        ArrayList<Produit> produits = null;
        ImageView imageView = new ImageView(context);
        if (produitDAO == null)    produitDAO = new ProduitDAO(context);

        if (produit.getImage()!=null && produit.getImage().contains(Utiles.PV_PRODUIT_IMAGE_TMP_DIR)) {

            try {
                //Picasso.with(MainActivity.this).load(Uri.parse(produit.getImmatriculation())).into(imageView);
                int reponse = Utiles.uploadFile(produit.getImage(),produit.getId_externe()+".jpeg",context) ;
                Log.e("REPONSE", String.valueOf(reponse)) ;
                Log.e("IMG1",produit.getImage()) ;
                Log.e("IMG1",produit.getId_externe()+".jpeg") ;
                if (reponse==200){
                    String name = null ;
                    try {
                        Uri path = Utiles.saveImageExternalStorage(BitmapFactory.decodeFile(produit.getImage()),context, produit.getId_externe()+".jpg", Utiles.PV_PRODUIT_IMAGE_DIR) ;
                        produit.setImage(path.getPath());
                        produitDAO.update(produit) ;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void synchronisationAttente() {

        FormBody.Builder formBuilder = new FormBody.Builder() ;
        formBuilder.add("pointevente", String.valueOf(caisseDAO.getFirst().getPointVente()));

        String result = " ";
        try {
            result = Utiles.POST(Url.getLoadAttente(context), formBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("REPONSEEEEEEEEEEEEEEEE", result);

        int nbre = 0 ;
        int size = 0 ;
        try {

            JSONObject obj = new JSONObject(result);
            // list_annonces = null;
            if (obj != null) {
                String reponse = obj.getString("reponse");

                if (!reponse.equals("OK")) return  ;

                //operationDAO.clean() ;
                //mouvementDAO.clean() ;


                JSONArray operationArr = obj.getJSONArray("operations");
                size = operationArr.length() ;
                JSONObject operationObj = null ;
                Operation operation = null ;
                //if (size>0) operationDAO.deleteAllAttente() ;
// Moves events into the expanded layout
                for (int i = 0; i < size; i++) {
                    operationObj = operationArr.getJSONObject(i);
                    operation = new Operation();
                    operation.setId_externe(operationObj.getLong("id"));
                    operation.setId(operationObj.getLong("id"));
                    operation.setAnnuler(operationObj.getInt("annuler"));
                    operation.setAttente(1);
                    operation.setToken(operationObj.getString("token"));
                    operation.setCaisse(operationObj.getLong("caisse_id"));
                    if (!operationObj.getString("attente").equals("null"))operation.setAttente(operationObj.getInt("attente"));
                    if (!operationObj.getString("operation_id").equals("null"))operation.setOperation_id(operationObj.getLong("operation_id"));
                    if (!operationObj.getString("compte_banque_id").equals("null"))operation.setComptebanque_id(operationObj.getLong("compte_banque_id"));
                    if (!operationObj.getString("commercial_id").equals("null"))operation.setCommercialid(operationObj.getLong("commercial_id"));
                    operation.setEntree(operationObj.getInt("entree"));
                    if (!operationObj.getString("partenaire_id").equals("null"))operation.setPartenaire_id(operationObj.getLong("partenaire_id"));
                    operation.setPayer(operationObj.getInt("payer"));
                    operation.setNbreproduit(operationObj.getInt("nbreproduit"));
                    operation.setTypeOperation_id(operationObj.getString("typeoperation"));
                    operation.setDescription(operationObj.getString("description"));
                    operation.setClient(operationObj.getString("client"));
                    operation.setMontant(operationObj.getDouble("montant"));
                    operation.setModepayement(operationObj.getString("modepayement"));
                    operation.setRecu(operationObj.getDouble("recu"));
                    operation.setRemise(operationObj.getDouble("remise"));
                    operation.setEtat(2);
                    try {
                        operation.setDateoperation(DAOBase.formatter.parse(operationObj.getString("created_at")));
                        operation.setCreated_at(DAOBase.formatter.parse(operationObj.getString("created_at")));
                        operation.setDateecheance(DAOBase.formatter2.parse(operationObj.getString("date_echeance")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        operation.setDateannulation(DAOBase.formatter.parse(operationObj.getString("date_annulation")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String text = "" ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.VENTE)) text = context.getString(R.string.vente) + " : " + Utiles.formatMtn(operation.getMontant()) ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.DEPENSE)) text = context.getString(R.string.depense) +  " : "  + Utiles.formatMtn(operation.getMontant()) ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.ACHAT)) text = context.getString(R.string.achat) +  " : "  + Utiles.formatMtn(operation.getMontant()) ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.CH_EXC)) text = context.getString(R.string.chargeexp) +  " : "  + Utiles.formatMtn(operation.getMontant()) ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.CH_FN)) text = context.getString(R.string.chargefin) +  " : "  + Utiles.formatMtn(operation.getMontant()) ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT)) text = context.getString(R.string.cmdclt) +  " : "  + Utiles.formatMtn(operation.getMontant()) ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS)) text = context.getString(R.string.cmdfr) +  " : "  + Utiles.formatMtn(operation.getMontant()) ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.DIVERS)) text = context.getString(R.string.divers) +  " : "  + Utiles.formatMtn(operation.getMontant()) ;
                    if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS)) text = context.getString(R.string.cmdfr) +  " : "  + Utiles.formatMtn(operation.getMontant()) ;

                    //inboxStyle.addLine(text);
                    if (operationDAO.getOneExterne(operation.getId_externe())!=null) {
                        int sup = operationDAO.deleteByIdExterne(operation.getId_externe()) ;
                        Log.e("DELETE SIZE", String.valueOf(sup)) ;
                    }
                    operationDAO.add(operation) ;

                    mouvementDAO.deletePV(operation.getId_externe()) ;
                }


                JSONArray mouvementArr = obj.getJSONArray("mouvements");
                size = mouvementArr.length() ;
                JSONObject mouvementObj = null ;
                Mouvement mouvement = null ;


                for (int i = 0; i < size; i++) {
                    mouvementObj = mouvementArr.getJSONObject(i);
                    mouvement = new Mouvement();
                    mouvement.setEtat(2);
                    mouvement.setId(mouvementObj.getLong("id"));
                    mouvement.setEntree(mouvementObj.getInt("entree"));
                    mouvement.setPrixA(mouvementObj.getDouble("prix_achat"));
                    mouvement.setPrixV(mouvementObj.getDouble("prix_vente"));
                    mouvement.setQuantite(mouvementObj.getDouble("quantite"));
                    mouvement.setRestant(mouvementObj.getDouble("restant"));
                    if(!mouvementObj.getString("cmup").equals("null"))mouvement.setCmup(mouvementObj.getDouble("cmup"));
                    mouvement.setProduit_id(mouvementObj.getLong("produit_id"));
                    mouvement.setOperation_id(mouvementObj.getLong("operation_id"));
                    mouvement.setProduit(mouvementObj.getString("produit"));
                    try {
                        mouvement.setCreated_at(DAOBase.formatter.parse(mouvementObj.getString("created_at")));
                        Log.e("DATE " + mouvement.getId(), DAOBase.formatter.format(mouvement.getCreated_at())) ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mouvementDAO.add(mouvement) ;
                }

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
