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
public class AttenteSyncAdapter extends AbstractThreadedSyncAdapter {

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

    public AttenteSyncAdapter(Context context, boolean autoInitialize) {
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

        if (preferences.getBoolean("stateonoff",false)){
            synchronisationAttente();
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
                    if (operationDAO.getOneExterne(operation.getId_externe())!=null) operationDAO.delete(operation.getId_externe()) ;
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
