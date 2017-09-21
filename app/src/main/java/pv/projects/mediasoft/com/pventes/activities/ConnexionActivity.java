package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.FormBody;
import pv.projects.mediasoft.com.pventes.MainActivity;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.BilletDAO;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;
import pv.projects.mediasoft.com.pventes.dao.CategorieProduitDAO;
import pv.projects.mediasoft.com.pventes.dao.CommercialDAO;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.dao.ModePayementDAO;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.dao.TypeOperationDAO;
import pv.projects.mediasoft.com.pventes.model.Billet;
import pv.projects.mediasoft.com.pventes.model.Caisse;
import pv.projects.mediasoft.com.pventes.model.CategorieProduit;
import pv.projects.mediasoft.com.pventes.model.Commercial;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.Devise;
import pv.projects.mediasoft.com.pventes.model.ModePayement;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.model.PointVente;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.model.TypeOperation;
import pv.projects.mediasoft.com.pventes.utiles.Url;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class ConnexionActivity extends AppCompatActivity {

    private static final String MAUVAIS = "KO";
    private static final String BON = "OK";
    private static final int MY_PERMISSIONS_REQUEST = 1;
    Button quitter = null ;
    Button valider = null ;
    Button inscript = null ;
    private ProgressDialog mProgressBar;
    final static int PROGRESS_DIALOG_ID = 0 ;
    private int MAX_SIZE;

    EditText login = null ;
    EditText password = null ;
    private Toolbar mToolbar;
    private CommercialDAO commercialDAO;
    private DeviseDAO deviseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caisse_setting);

        //setupToolBar();
        quitter = (Button) findViewById(R.id.annuler);
        valider = (Button) findViewById(R.id.valider);
        inscript = (Button) findViewById(R.id.inscription);

        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utiles.isConnected(ConnexionActivity.this)){
                    Toast.makeText(ConnexionActivity.this, R.string.noconnexion, Toast.LENGTH_SHORT).show();
                    return;
                }

                LoadInitDataTask loadInitDataTask = new LoadInitDataTask(login.getText().toString(),password.getText().toString()) ;
                loadInitDataTask.execute(Url.getCheckAndInitCaisse(ConnexionActivity.this)) ;
            }
        });

        inscript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnexionActivity.this,WebViewActivity.class) ;
                startActivity(intent);
            }
        });

        quitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    private void setupToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId() ;
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        if (mProgressBar ==null){
            mProgressBar = new ProgressDialog(this) ;
            mProgressBar.setCancelable(false);
            mProgressBar.setTitle(getString(R.string.send_loding));
            mProgressBar.setMessage(getString(R.string.wait));
            mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressBar.setMax(MAX_SIZE);
        }

        return mProgressBar ;
    }


    public class LoadInitDataTask extends AsyncTask<String,Void,String> {
        Calendar calendar = Calendar.getInstance() ;
        private ProduitDAO produitDAO;
        String login = null ;
        String password = null ;
        private OperationDAO operationDAO;
        private CaisseDAO caisseDAO;
        private BilletDAO billetDAO;
        private MouvementDAO mouvementDAO;
        private ModePayementDAO modepayementDAO;
        private TypeOperationDAO typeOperationDAO;
        private CategorieProduitDAO categorieproduitDAO;
        private PointVenteDAO pointventeDAO;
        private CompteBanqueDAO compteBanqueDAO;
        private PartenaireDAO partenaireDAO;
        private String res;

        public LoadInitDataTask(String login, String password){
            this.login = login ;
            this.password = password ;
        }

        @Override
        protected String doInBackground(String... url) {

            FormBody.Builder formBuilder = new FormBody.Builder() ;
            formBuilder.add("login", login);
            formBuilder.add("password", password);

            Log.e("URL",url[0]) ;

            String result = "" ;


            try {
                result = Utiles.POST(url[0],formBuilder.build());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("REPONSEEEEEEEEEEEEEEEE", result);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(ConnexionActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            });

            int nbre = 0 ;
            int size = 0 ;
            try {

                JSONObject obj = new JSONObject(result);
                // list_annonces = null;
                if (obj != null) {
                    String reponse = obj.getString("reponse");

                    if (!reponse.equals("OK")) return reponse ;


                    caisseDAO.clean() ;
                    produitDAO.clean() ;
                    operationDAO.clean() ;
                    partenaireDAO.clean() ;
                    commercialDAO.clean() ;
                    categorieproduitDAO.clean() ;
                    typeOperationDAO.clean() ;
                    modepayementDAO.clean() ;
                    compteBanqueDAO.clean() ;
                    billetDAO.clean() ;
                    mouvementDAO.clean() ;
                    pointventeDAO.clean() ;
                    deviseDAO.clean() ;

                    JSONObject pointventeObj = obj.getJSONObject("pointvente");
                    size = pointventeObj.length() ;

                    // Si la base de données n'est pas vide et qu'il ya + de 1000 annonce on fait un clean
                    if (size >0) {

                        PointVente pointvente = new PointVente();
                        pointvente.setId(pointventeObj.getLong("id"));
                        pointvente.setLibelle(pointventeObj.getString("libelle"));
                        pointvente.setPays(pointventeObj.getString("pays"));
                        pointvente.setUtilisateur_id(pointventeObj.getLong("utilisateur_id"));
                        pointvente.setQuartier(pointventeObj.getString("quartier"));
                        pointvente.setTel(pointventeObj.getString("tel"));
                        pointvente.setVille(pointventeObj.getString("ville"));
                        try {
                            pointvente.setCreated_at(DAOBase.formatter.parse(pointventeObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        pointventeDAO.add(pointvente) ;
                    }


                    JSONArray partenaireArr = obj.getJSONArray("partenaires");
                    size = partenaireArr.length() ;
                    JSONObject partenaireObj = null ;
                    Partenaire partenaire = null ;
                    for (int i = 0; i < size; i++) {
                        partenaireObj = partenaireArr.getJSONObject(i);
                        partenaire = new Partenaire() ;

                        partenaire.setId_externe(partenaireObj.getLong("id"));
                        partenaire.setRaisonsocial(partenaireObj.getString("raisonsocial"));
                        partenaire.setContact(partenaireObj.getString("contact"));
                        partenaire.setUtilisateur_id(partenaireObj.getLong("client"));
                        partenaire.setAdresse(partenaireObj.getString("adresse"));
                        partenaire.setTypepersonne(partenaireObj.getString("typepersonne"));
                        partenaire.setTypepartenaire(partenaireObj.getString("typepartenaire"));
                        partenaire.setNom(partenaireObj.getString("nom"));
                        partenaire.setEtat(2);
                        partenaire.setPrenom(partenaireObj.getString("prenom"));
                        partenaire.setSexe(partenaireObj.getString("sexe"));
                        partenaire.setPointvente_id(partenaireObj.getLong("pointvente_id"));
                        try {
                            partenaire.setCreated_at(DAOBase.formatter.parse(partenaireObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        partenaireDAO.add(partenaire) ;
                    }



                    JSONArray commercialArr = obj.getJSONArray("commercials");
                    size = commercialArr.length() ;
                    JSONObject commercialObj = null ;
                    Commercial commercial = null ;
                    for (int i = 0; i < size; i++) {
                        commercialObj = commercialArr.getJSONObject(i);
                        commercial = new Commercial() ;

                        commercial.setId_externe(commercialObj.getLong("id"));
                        commercial.setContact(commercialObj.getString("contact"));
                        commercial.setAdresse(commercialObj.getString("adresse"));
                        commercial.setNom(commercialObj.getString("nom"));
                        commercial.setEtat(2);
                        commercial.setPrenom(commercialObj.getString("prenom"));
                        commercial.setSexe(commercialObj.getString("sexe"));
                        commercial.setPointvente_id(commercialObj.getLong("pointvente_id"));
                        try {
                            commercial.setCreated_at(DAOBase.formatter.parse(commercialObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        commercialDAO.add(commercial) ;
                    }


                    JSONArray produitArr = obj.getJSONArray("produits");
                    size = produitArr.length() ;
                    JSONObject produitObj = null ;
                    Produit produit = null ;
                    for (int i = 0; i < size; i++) {
                        produitObj = produitArr.getJSONObject(i);
                        produit = new Produit() ;

                        produit.setId_externe(produitObj.getLong("id"));
                        produit.setUnite(produitObj.getString("unite"));
                        produit.setLibelle(produitObj.getString("libelle"));
                        produit.setUtilisateur_id(produitObj.getLong("client_id"));
                        if (!produitObj.getString("categorieproduit_id").equals("null"))  produit.setCategorie_id(produitObj.getLong("categorieproduit_id"));
                        else produit.setCategorie_id(1);
                        produit.setCode(produitObj.getString("code"));
                        if (produitObj.has("affichable"))produit.setAffichable(produitObj.getString("affichable"));
                        produit.setImage(produitObj.getString("image"));
                        if (produitObj.getInt("prixmodifiable")!=0)produit.setModifiable(1);
                        else produit.setModifiable(0);
                        produit.setEtat(2);
                        produit.setPrixA(produitObj.getDouble("prixachat"));
                        produit.setPrixV(produitObj.getDouble("prixvente"));
                        //produit.setQuantite(produitObj.getInt("pointvente_id"));
                        try {
                            produit.setCreated_at(DAOBase.formatter.parse(produitObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        produitDAO.add(produit) ;
                    }

                    JSONArray categorieProduitArr = obj.getJSONArray("categorieproduits");
                    size = categorieProduitArr.length() ;
                    JSONObject categorieProduitObj = null ;
                    CategorieProduit categorieProduit = null ;
                    for (int i = 0; i < size; i++) {
                        categorieProduitObj = categorieProduitArr.getJSONObject(i);
                        categorieProduit = new CategorieProduit() ;

                        categorieProduit.setId(categorieProduitObj.getLong("id"));
                        categorieProduit.setCode(categorieProduitObj.getString("code"));
                        categorieProduit.setEtat(2);
                        categorieProduit.setLibelle(categorieProduitObj.getString("libelle"));

                        try {
                            categorieProduit.setCreated_at(DAOBase.formatter.parse(categorieProduitObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        categorieproduitDAO.add(categorieProduit) ;
                    }

                    JSONArray typeoperationArr = obj.getJSONArray("typeoperations");
                    size = typeoperationArr.length() ;
                    JSONObject typeoperationObj = null ;
                    TypeOperation typeoperation = null ;
                    for (int i = 0; i < size; i++) {
                        typeoperationObj = typeoperationArr.getJSONObject(i);
                        typeoperation = new TypeOperation() ;

                        typeoperation.setCode(typeoperationObj.getString("code"));
                        typeoperation.setLibelle(typeoperationObj.getString("libelle"));

                        try {
                            typeoperation.setCreated_at(DAOBase.formatter.parse(typeoperationObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        typeOperationDAO.add(typeoperation) ;
                    }

                    JSONArray modepayementArr = obj.getJSONArray("modepayements");
                    size = modepayementArr.length() ;
                    JSONObject modepayementObj = null ;
                    ModePayement modepayement = null ;
                    for (int i = 0; i < size; i++) {
                        modepayementObj = modepayementArr.getJSONObject(i);
                        modepayement = new ModePayement() ;

                        modepayement.setCode(modepayementObj.getString("code"));
                        modepayement.setLibelle(modepayementObj.getString("libelle"));

                        try {
                            modepayement.setCreated_at(DAOBase.formatter.parse(modepayementObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        modepayementDAO.add(modepayement) ;
                    }

                    JSONArray billetsArr = obj.getJSONArray("billets");
                    size = billetsArr.length() ;
                    JSONObject billetObj = null ;
                    Billet billet = null ;
                    for (int i = 0; i < size; i++) {
                        billetObj = billetsArr.getJSONObject(i);
                        billet = new Billet() ;

                        billet.setId(billetObj.getLong("id"));
                        billet.setLibelle(billetObj.getString("libelle"));
                        billet.setMontant(billetObj.getDouble("montant"));

                        billetDAO.add(billet) ;
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
                        mouvement.setQuantite(mouvementObj.getInt("quantite"));
                        mouvement.setRestant(mouvementObj.getInt("restant"));
                        mouvement.setProduit_id(mouvementObj.getLong("produit_id"));
                        mouvement.setOperation_id(mouvementObj.getLong("operation_id"));
                        mouvement.setProduit(mouvementObj.getString("produit"));
                        try {
                            mouvement.setCreated_at(DAOBase.formatter.parse(mouvementObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mouvementDAO.add(mouvement) ;
                    }


                    JSONArray compteBanqueArr = obj.getJSONArray("compte_banque");
                    size = compteBanqueArr.length() ;
                    JSONObject compteBanqueObj = null ;
                    CompteBanque compteBanque = null ;
                    for (int i = 0; i < size; i++) {
                        compteBanqueObj = compteBanqueArr.getJSONObject(i);
                        compteBanque = new CompteBanque();
                        compteBanque.setEtat(2);
                        compteBanque.setId(compteBanqueObj.getLong("id"));
                        compteBanque.setId_externe(compteBanqueObj.getLong("id"));
                        compteBanque.setUtilisateur_id(compteBanqueObj.getLong("utilisateur_id"));
                        compteBanque.setSolde(compteBanqueObj.getDouble("solde"));
                        compteBanque.setLibelle(compteBanqueObj.getString("libelle"));
                        compteBanque.setCode(compteBanqueObj.getString("code"));
                        compteBanque.setCheque(compteBanqueObj.getInt("cheque"));
                        compteBanque.setCartebanque(compteBanqueObj.getInt("cartebancaire"));

                        compteBanqueDAO.add(compteBanque) ;
                    }

                    JSONArray deviseArr = obj.getJSONArray("devises");
                    size = deviseArr.length() ;
                    JSONObject deviseObj = null ;
                    Devise devise = null ;
                    for (int i = 0; i < size; i++) {
                        deviseObj = deviseArr.getJSONObject(i);
                        devise = new Devise();
                        devise.setId(deviseObj.getLong("id"));
                        devise.setCoursmoyen(deviseObj.getDouble("coursmoyen"));
                        devise.setLibelledevise(deviseObj.getString("libelledevise"));
                        devise.setCodedevise(deviseObj.getString("codedevise"));
                        devise.setSymbole(deviseObj.getString("symbole"));
                        devise.setUnite(deviseObj.getString("unitedevise"));

                        devise.setDefaut(deviseObj.getInt("principale"));
                        deviseDAO.add(devise) ;
                    }


                    JSONArray operationArr = obj.getJSONArray("operations");
                    size = operationArr.length() ;
                    JSONObject operationObj = null ;
                    Operation operation = null ;
                    for (int i = 0; i < size; i++) {
                        operationObj = operationArr.getJSONObject(i);
                        operation = new Operation();
                        operation.setEtat(2);
                        operation.setId_externe(operationObj.getLong("id"));
                        operation.setId(operationObj.getLong("id"));
                        operation.setAnnuler(operationObj.getInt("annuler"));
                        operation.setAttente(0);
                        operation.setCaisse(operationObj.getLong("caisse_id"));
                        if (!operationObj.getString("operation_id").equals("null"))operation.setOperation_id(operationObj.getLong("operation_id"));
                        if (!operationObj.getString("commercial_id").equals("null"))
                            operation.setCommercialid(operationObj.getLong("commercial_id"));
                        operation.setEntree(operationObj.getInt("entree"));
                        if (!operationObj.getString("partenaire_id").equals("null"))
                            operation.setPartenaire_id(operationObj.getLong("partenaire_id"));
                        operation.setPayer(operationObj.getInt("payer"));
                        operation.setNbreproduit(operationObj.getInt("nbreproduit"));
                        operation.setTypeOperation_id(operationObj.getString("typeoperation"));
                        operation.setDescription(operationObj.getString("description"));
                        operation.setClient(operationObj.getString("client"));
                        operation.setMontant(operationObj.getDouble("montant"));
                        operation.setRecu(operationObj.getDouble("recu"));
                        operation.setRemise(operationObj.getDouble("remise"));
                        try {
                            operation.setDateoperation(DAOBase.formatter.parse(operationObj.getString("created_at")));
                            operation.setCreated_at(DAOBase.formatter.parse(operationObj.getString("created_at")));
                            operation.setDateecheance(DAOBase.formatter2.parse(operationObj.getString("date_echeance")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        operationDAO.add(operation);
                    }



                    JSONObject caisseObj = obj.getJSONObject("caisse");
                    size = caisseObj.length() ;
                    if (size >0) {
                        Caisse caisse = new Caisse();
                        caisse.setId(caisseObj.getLong("id"));
                        caisse.setSolde(obj.getDouble("solde"));
                        caisse.setCode(caisseObj.getString("code"));
                        caisse.setImei(caisseObj.getString("imei"));
                        caisse.setPointVente(caisseObj.getLong("pointvente_id"));
                        try {
                            caisse.setCreated_at(DAOBase.formatter.parse(caisseObj.getString("created_at")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        caisseDAO.add(caisse) ;
                    }


                    return BON ;

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return MAUVAIS ;
        }

            @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

                dismissDialog(PROGRESS_DIALOG_ID);
                if (result.equals(BON)) {
                    Toast.makeText(ConnexionActivity.this, R.string.load_success, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ConnexionActivity.this).edit();
                    editor.putBoolean("FIRST",true) ;
                    editor.commit();
                    Intent intent = new Intent(ConnexionActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    ConnexionActivity.this.finish();

                }
                else if (result.equals("error2")){
                    Toast.makeText(ConnexionActivity.this, R.string.aucuncaisse, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(ConnexionActivity.this, R.string.load_echec, Toast.LENGTH_SHORT).show();
            }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(PROGRESS_DIALOG_ID);
            produitDAO = new ProduitDAO(ConnexionActivity.this);
            commercialDAO = new CommercialDAO(ConnexionActivity.this);
            partenaireDAO = new PartenaireDAO(ConnexionActivity.this);
            pointventeDAO = new PointVenteDAO(ConnexionActivity.this);
            deviseDAO = new DeviseDAO(ConnexionActivity.this);
            compteBanqueDAO = new CompteBanqueDAO(ConnexionActivity.this);
            caisseDAO = new CaisseDAO(ConnexionActivity.this);
            operationDAO = new OperationDAO(ConnexionActivity.this);
            categorieproduitDAO = new CategorieProduitDAO(ConnexionActivity.this);
            typeOperationDAO = new TypeOperationDAO(ConnexionActivity.this);
            modepayementDAO = new ModePayementDAO(ConnexionActivity.this);
            billetDAO = new BilletDAO(ConnexionActivity.this);
            mouvementDAO = new MouvementDAO(ConnexionActivity.this);
        }
    }


}
