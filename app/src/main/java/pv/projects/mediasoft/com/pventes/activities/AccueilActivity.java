package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.zj.btsdk.BluetoothService;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;
import pv.projects.mediasoft.com.pventes.dao.CommercialDAO;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.database.CommercialHelper;
import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.database.OperationHelper;
import pv.projects.mediasoft.com.pventes.database.PartenaireHelper;
import pv.projects.mediasoft.com.pventes.database.ProduitHelper;
import pv.projects.mediasoft.com.pventes.fragment.VenteplusFormFragment;
import pv.projects.mediasoft.com.pventes.model.Caisse;
import pv.projects.mediasoft.com.pventes.model.Commercial;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.services.OperationSyncAdapter;
import pv.projects.mediasoft.com.pventes.utiles.Url;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class AccueilActivity extends AppCompatActivity implements VenteplusFormFragment.OnFragmentInteractionListener, View.OnClickListener, View.OnLongClickListener {

    public static final String CODEBAR = "codebare";
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView = null;
    AppBarLayout appBarLayout = null ;
    private SharedPreferences preferences;
    private CaisseDAO caissierDAO;
    public static final String VENTE = "Vente";



    ActionMode mMode = null ;
    private boolean mActionModeIsActive;
    VenteplusFormFragment venteplusFormFragment = null ;

    public final static int PROGRESS_DIALOG_ID = 0 ;
    private ProgressDialog mProgressBar;
    private int MAX_SIZE;

    RelativeLayout dixmille = null ;
    RelativeLayout cinqmille = null ;
    RelativeLayout deuxmille = null ;
    RelativeLayout mille = null ;
    RelativeLayout cinqcent = null ;
    RelativeLayout deuxcentcinquante = null ;
    RelativeLayout deuxcent = null ;
    RelativeLayout cent = null ;
    RelativeLayout vingcinq = null ;
    RelativeLayout cinquante = null ;

    private ScrollView sc = null ;
    private AlertDialog.Builder builder;
    private AlertDialog alert;
    private Button total =null ;
    private Button init =null ;
    private Button close = null;
    private RelativeLayout rv = null ;
    public EditText editText;
    private int etape = 1;
    private ProduitDAO produitDAO;
    private OperationDAO operationDAO;
    private MouvementDAO mouvementDAO;
    private PartenaireDAO partenaireDAO;
    private CommercialDAO commercialDAO;
    private int PROGRESS = 0 ;
    private PointVenteDAO pointVenteDAO;
    private int REUSSI = 0;
    private int ECHEC = 0;

    private SynchronisationProduit synchronisationProduit;
    private SynchronisationOperation synchronisationOperation;
    private SynchronisationMouvement synchronisationMouvement;
    private SynchronisationPartenaire synchronisationPartenaire;
    private SynchronisationCommercial synchronisationCommercial;
    private SynchronisationProduitImage synchronisationProduitImage;
    private SharedPreferences.Editor editor;
    TextView transfertdata = null ;
    private CompteBanqueDAO compteBanqueDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        initialisation();
        if (caissierDAO.getFirst()==null) {
            Intent intent = new Intent(AccueilActivity.this,ConnexionActivity.class) ;
            startActivity(intent);
            finish();
        }
        if (preferences.getBoolean("FIRST",true)){
            mDrawerLayout.openDrawer(GravityCompat.START);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("FIRST",false) ;
            editor.commit();
        }


        setupToolBar();
        setupFragments(savedInstanceState);
        setTitle(R.string.ventefic);

        setupNavigation();
    }

    private void initialisation() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drLayout) ;
        preferences = PreferenceManager.getDefaultSharedPreferences(this) ;
        caissierDAO = new CaisseDAO(this);
        compteBanqueDAO = new CompteBanqueDAO(this);
        operationDAO = new OperationDAO(this) ;
        produitDAO = new ProduitDAO(this) ;
        mouvementDAO = new MouvementDAO(this) ;
        partenaireDAO = new PartenaireDAO(this) ;
        commercialDAO = new CommercialDAO(this) ;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vente_form, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Caisse caisse = caissierDAO.getFirst() ;
        String raison = "" ;
        if (caisse!=null && caisse.getActif()==0){
            //if (caisse.getRaison().equals(CaisseDAO.CPT_EXPIRED)) raison = getString(R.string.cptexpired) ;
            AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
            builder.setTitle(getString(R.string.cptexpire)) ;
            builder.setMessage(raison) ;
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }) ;
            builder.show() ;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        /*
        else if (id == R.id.nouveau){
            //venteplusFormFragment.addLigneProduit();
        }
        else if (id == R.id.action_billet){
            if (editText!=null){
                money_init() ;
                builder = new AlertDialog.Builder(this) ;
                builder.setView(rv) ;
                alert = builder.show() ;
            }
        }
        */

        if (id == R.id.action_barcode) {
            venteplusFormFragment.readCodeBar(); ;
            return true;
        }
        if (id == R.id.action_dashboard) {
            showDashBoard() ;
            return true;
        }

        if (id == R.id.action_liste) {
            Intent intent = new Intent(AccueilActivity.this, OperationActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_add) {
            venteplusFormFragment.sendIntent() ;
            return true;
        }
        if (id == R.id.action_racourci) {
            venteplusFormFragment.showRacourci() ;
            return true;
        }

        if (id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDashBoard() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this) ;
        ScrollView sc = (ScrollView) getLayoutInflater().inflate(R.layout.dashboard,null);
        LinearLayout apro = (LinearLayout) sc.findViewById(R.id.menu_achat);
        LinearLayout dep = (LinearLayout) sc.findViewById(R.id.menu_depense);
        LinearLayout divers = (LinearLayout) sc.findViewById(R.id.menu_divers);
        LinearLayout banque = (LinearLayout) sc.findViewById(R.id.menu_banquetransaction);
        LinearLayout cmdfourn = (LinearLayout) sc.findViewById(R.id.menu_commande_fournisseur);
        LinearLayout payement = (LinearLayout) sc.findViewById(R.id.menu_payement);

        apro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle(getString(R.string.appro)) ;
                security(4) ;
            }
        });

        dep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle(getString(R.string.depense)) ;
                Intent intent = null ;
                intent = new Intent(AccueilActivity.this, DepenseFormActivity.class);
                startActivity(intent) ;
            }
        });
        divers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle(getString(R.string.divers)) ;
                Intent intent = null ;
                intent = new Intent(AccueilActivity.this, DiversFormActivity.class);
                startActivity(intent) ;
            }
        });
        banque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle(getString(R.string.banque)) ;
                Intent intent = null ;
                intent = new Intent(AccueilActivity.this, BanqueTransactionActiity.class);
                startActivity(intent) ;
            }
        });
        /*
        cmdclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                security(5) ;
            }
        });

        etat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null ;
                intent = new Intent(AccueilActivity.this, EtatActivity.class);
                startActivity(intent) ;
            }
        });
        */
        cmdfourn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle(getString(R.string.cmdfr)) ;
                security(6) ;
            }
        });
        payement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle(getString(R.string.payement)) ;
                Intent intent = null ;
                intent = new Intent(AccueilActivity.this, CreditPayementActivity.class);
                startActivity(intent) ;
            }
        });

        builder.setView(sc) ;
        builder.show() ;
    }


    private void showFragment(final Fragment fragment) {
        if (fragment == null)         return;
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }



    private void setupFragments(Bundle bundle) {
        final FragmentManager fm = getSupportFragmentManager();

        if(bundle == null) {
            Intent intent = getIntent();
            if(intent != null) {
                if (intent.hasExtra("LIVRE")) venteplusFormFragment = VenteplusFormFragment.newInstance("", null);
                else venteplusFormFragment = VenteplusFormFragment.newInstance(VENTE, null);
                showFragment(venteplusFormFragment);
            }

        }
        else{
            //this.venteFormFragment = (VenteFormFragment) fm.findFragmentByTag(VenteFormFragment.TAG);
            if (this.venteplusFormFragment == null){
                venteplusFormFragment = VenteplusFormFragment.newInstance(VENTE, null);
            }
            showFragment(venteplusFormFragment);
        }
    }


    private void setupNavigation() {

        //mTabLayout.setupWithViewPager(mViewPager);

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        TextView caisse = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.header);
        TextView solde = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.solde);

        TextView transfertdata =  (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.transfertdata);
        transfertdata.setText(getString(R.string.datatotransfert) + getSyncData());
        solde.setText(getString(R.string.solde) + Utiles.formatMtn(Utiles.getSolde(AccueilActivity.this)) + " F");

        if (caissierDAO.getFirst()!=null)caisse.setText(getString(R.string.caisseno) + caissierDAO.getFirst().getCode());
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.menu_rglmt: {
                        showReglementPopup() ;
                    }
                    break;
                    case R.id.stock: {
                        showStockPopup() ;
                    }
                    break;
                    case R.id.commande: {
                        showCommandePopup() ;
                    }
                    break;
                    case R.id.menu_op: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, OperationActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_placement: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, PlacementFormActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_dettebanque: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, EmpruntFormActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_immo: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, ImmoFormActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_achat: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        security(4) ;
                    }
                    break;
                    case R.id.menu_divers: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, DiversFormActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_banquetransaction: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, BanqueTransactionActiity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_depense: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, DepenseFormActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_parametre: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, ParametreActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_billetage: {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent intent = null;
                        intent = new Intent(AccueilActivity.this, BuillitageActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.menu_sync: {
                        Log.e("SYNC AUTO", String.valueOf(preferences.getBoolean(OperationSyncAdapter.SYNCAUTO,false))) ;
                        Log.e("SYNC AUTO", String.valueOf(OperationSyncAdapter.sync)) ;

                        if (OperationSyncAdapter.sync==1){
                            makeAlertDialog(getString(R.string.syncauto),false);
                        }
                        else {
                            // Activer la sync Auto
                            editor = preferences.edit() ;
                            editor.putBoolean(OperationSyncAdapter.SYNCAUTO,true) ;
                            editor.commit() ;
                            Log.e("SYNC DEBUT", String.valueOf(preferences.getBoolean(OperationSyncAdapter.SYNCAUTO,false))) ;
                            security(3) ;
                        }
                    }
                    break;
                }
                return true;
            }
        });
    }

    private void showFinancierPopup() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        final CharSequence[] items = {getString(R.string.menu_placement), getString(R.string.menu_dettebanque), getString(R.string.menu_immo), getString(R.string.annuler)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this) ;
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.menu_placement))) {
                }
                else if (items[item].equals(getString(R.string.menu_dettebanque))) {

                } else
                if (items[item].equals(getString(R.string.menu_immo))) {
                }
            }
        });
        builder.show() ;
    }


    private void showCommandePopup() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        final CharSequence[] items = {getString(R.string.menu_commande_client), getString(R.string.menu_commande_fournisseur), getString(R.string.annuler)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this) ;
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.menu_commande_client))) {
                    security(5) ;
                }
                else if (items[item].equals(getString(R.string.menu_commande_fournisseur))) {
                    security(6) ;
                }
            }
        });
        builder.show() ;
    }


    private void showReglementPopup() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        final CharSequence[] items = {getString(R.string.menu_creance), getString(R.string.menu_empruntpayement), getString(R.string.menu_payement), getString(R.string.annuler)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this) ;
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.menu_creance))) {
                    Intent intent = null;
                    intent = new Intent(AccueilActivity.this, CreditPayementActivity.class);
                    startActivity(intent);
                }
                else if (items[item].equals(getString(R.string.menu_empruntpayement))) {
                    Intent intent = null;
                    intent = new Intent(AccueilActivity.this, EmpruntPayementActivity.class);
                    startActivity(intent);
                }
                else if (items[item].equals(getString(R.string.menu_payement))) {
                    Intent intent = null;
                    intent = new Intent(AccueilActivity.this, DettePayementActivity.class);
                    startActivity(intent);
                }
            }
        });

        builder.show() ;
    }



    private void showStockPopup() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        final CharSequence[] items = {getString(R.string.sortiestock), getString(R.string.menu_inventaire), getString(R.string.annuler)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this) ;
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.sortiestock))) {
                    security(7) ;
                }
                else if (items[item].equals(getString(R.string.menu_inventaire))) {

                }
            }
        });
        builder.show() ;
    }

    private boolean security(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.security,null);

        final EditText password = (EditText) scrollView.findViewById(R.id.password);
        Button valider = (Button) scrollView.findViewById(R.id.valider);
        Button annuler = (Button) scrollView.findViewById(R.id.annuler);

        final Caisse caisse = caissierDAO.getFirst() ;

        builder.setTitle(R.string.app_name) ;
        builder.setView(scrollView) ;
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                // Annuler la sync
                editor = preferences.edit() ;
                editor.remove(OperationSyncAdapter.SYNCAUTO) ;
                editor.commit() ;
                Log.e("SYNC FIN", String.valueOf(preferences.getBoolean(OperationSyncAdapter.SYNCAUTO,false))) ;
            }
        }) ;

        alert = builder.show() ;

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals(preferences.getString("adminpass","admin"))) {

                    switch (pos){
                        case 0 : {
                            Intent intent = null;
                            intent = new Intent(AccueilActivity.this, ProduitActivity.class);
                            alert.dismiss();
                            startActivity(intent);
                        } break;
                        case 1 : {
                            Intent intent = null;
                            intent = new Intent(AccueilActivity.this, PartenaireActivity.class);
                            alert.dismiss();
                            startActivity(intent);
                        } break;
                        case 2 : {
                            Intent intent = null;
                            intent = new Intent(AccueilActivity.this, CommercialActivity.class);
                            alert.dismiss();
                            startActivity(intent);
                        } break;
                        case 3 : {
                            alert.dismiss();
                            startSync() ;
                        } break;
                        case 4 : {
                            Intent intent = null;
                            intent = new Intent(AccueilActivity.this, AchatActivity.class);
                            startActivity(intent);
                        } break;
                        case 5 : {
                            Intent intent = null;
                            intent = new Intent(AccueilActivity.this, CommandeActivity.class);
                            intent.putExtra("type",CommandeActivity.CLIENT) ;
                            startActivity(intent);
                        } break;
                        case 6 : {
                            Intent intent = null;
                            intent = new Intent(AccueilActivity.this, CommandeActivity.class);
                            intent.putExtra("type",CommandeActivity.FOURNISSEUR) ;
                            startActivity(intent);
                        } break;
                        case 7 : {
                            Intent intent = null;
                            intent = new Intent(AccueilActivity.this, SortieStockActivity.class);
                            intent.putExtra("type",SortieStockActivity.SORTE_STOCK) ;
                            startActivity(intent);
                        } break;
                    }
                    alert.dismiss();
                }
                else Toast.makeText(AccueilActivity.this, R.string.passincorrect, Toast.LENGTH_SHORT).show();

            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                // Annuler la sync
                editor = preferences.edit() ;
                editor.remove(OperationSyncAdapter.SYNCAUTO) ;
                editor.commit() ;
                Log.e("SYNC FIN", String.valueOf(preferences.getBoolean(OperationSyncAdapter.SYNCAUTO,false))) ;
            }
        });

        builder.setTitle(R.string.app_name) ;
        return false;
    }

    private void startSync() {

        try {
            dismissDialog(PROGRESS_DIALOG_ID);
        }
        catch (Exception e){
            e.getMessage() ;
        }

        int sync = getSyncData() ;


        if (sync==0 && etape==1) {
            // Annuler la sync
            editor = preferences.edit() ;
            editor.remove(OperationSyncAdapter.SYNCAUTO) ;
            editor.commit() ;
            Log.e("SYNC FIN", String.valueOf(preferences.getBoolean(OperationSyncAdapter.SYNCAUTO,false))) ;

            Toast.makeText(AccueilActivity.this, R.string.nosyncdata, Toast.LENGTH_SHORT).show();
            try {
                dismissDialog(PROGRESS_DIALOG_ID);
            }
            catch (Exception e){
                e.getMessage() ;
            }
            return;
        }

        if (!Utiles.isConnected(this)){
            try {
                dismissDialog(PROGRESS_DIALOG_ID);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(AccueilActivity.this, R.string.noconnexion, Toast.LENGTH_SHORT).show();
            return;
        }

        if (produitDAO.getNonSyncInterval(null,null).size() != 0 && etape == 1){
            Log.e("SYNC","PRODUIT LANCE") ;
            synchronisationProduit = new SynchronisationProduit();
            synchronisationProduit.execute();
            return;
        }
        else if (etape==1)etape = 2 ;


        if (partenaireDAO.getNonSync().size() != 0 && etape == 2){
            Log.e("SYNC","PARTENAIRE LANCE") ;
            synchronisationPartenaire = new SynchronisationPartenaire();
            synchronisationPartenaire.execute();
            return;
        }
        else if (etape==2) etape = 3 ;

        Log.e("NBRE COMERCIAL", String.valueOf(commercialDAO.getNonSync().size())) ;
        if (commercialDAO.getNonSync().size() != 0 && etape == 3){
            Log.e("SYNC","COMMERCIAL LANCE") ;
            synchronisationCommercial = new SynchronisationCommercial();
            synchronisationCommercial.execute();
            return;
        }
        else if (etape==3)etape = 4 ;


        if (operationDAO.getNonSync().size() != 0 && etape == 4){
            Log.e("SYNC","OPERATION LANCE") ;
            synchronisationOperation = new SynchronisationOperation();
            synchronisationOperation.execute();
            return;
        }
        else if (etape==4)etape = 5 ;

        if (mouvementDAO.getNonSync().size() != 0 && etape == 5){
            Log.e("SYNC","MOUVEMENT LANCE") ;
            synchronisationMouvement = new SynchronisationMouvement();
            synchronisationMouvement.execute();
            return;
        }
        else if (etape==5)etape = 6 ;


        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        builder.setMessage(getString(R.string.synctotal)) ;
        builder.setTitle(R.string.app_name) ;
        builder.setPositiveButton(getString(R.string.sync), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etape = 1 ;
                startSync();
            }
        }) ;
        builder.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Annuler la sync
                editor = preferences.edit() ;
                editor.remove(OperationSyncAdapter.SYNCAUTO) ;
                editor.commit() ;
                Log.e("SYNC FIN", String.valueOf(preferences.getBoolean(OperationSyncAdapter.SYNCAUTO,false))) ;
            }
        }) ;

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                // Annuler la sync
                editor = preferences.edit() ;
                editor.remove(OperationSyncAdapter.SYNCAUTO) ;
                editor.commit() ;
                Log.e("SYNC FIN", String.valueOf(preferences.getBoolean(OperationSyncAdapter.SYNCAUTO,false))) ;
            }
        }) ;

        sync = getSyncData() ;

        if (sync>0) {
            final AlertDialog alertdialog = builder.create();
            alertdialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                    alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
                }
            }) ;
            alertdialog.show();
        }
        else {
            etape=1 ;
            // Annuler la sync
            editor = preferences.edit() ;
            editor.remove(OperationSyncAdapter.SYNCAUTO) ;
            editor.commit() ;
            Log.e("SYNC FIN", String.valueOf(preferences.getBoolean(OperationSyncAdapter.SYNCAUTO,false))) ;
            Toast.makeText(AccueilActivity.this, R.string.sync_reussi, Toast.LENGTH_LONG).show();
        }

    }

    private int getSyncData() {
        return  compteBanqueDAO.getNonSyncInterval().size() + produitDAO.getNonSyncInterval(null,null).size() +   partenaireDAO.getNonSync().size() + commercialDAO.getNonSync().size() +  mouvementDAO.getNonSync().size() +  operationDAO.getNonSync().size();
    }


    private void setupToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }



    private void money_init(){
        rv = (RelativeLayout) getLayoutInflater().inflate(R.layout.buillet_layout, null);

        sc = (ScrollView) rv.findViewById(R.id.scroll);
        total = (Button) rv.findViewById(R.id.total);
        init = (Button) rv.findViewById(R.id.init);
        close = (Button) rv.findViewById(R.id.close);

        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total.setText("0");
                editText.setText("0");
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel() ;
            }
        });

        String p = editText.getText().toString() ;
        if (p.length()==0) editText.setText("0");

        total.setText(editText.getText().toString());

        dixmille = (RelativeLayout) sc.findViewById(R.id.dimille);
        dixmille.setOnClickListener(this);
        dixmille.setOnLongClickListener(this);
        cinqmille = (RelativeLayout) sc.findViewById(R.id.cinqmille);
        cinqmille.setOnClickListener(this);
        cinqmille.setOnLongClickListener(this);
        deuxmille = (RelativeLayout) sc.findViewById(R.id.deuxmille);
        deuxmille.setOnClickListener(this);
        deuxmille.setOnLongClickListener(this);
        mille = (RelativeLayout) sc.findViewById(R.id.mille);
        mille.setOnClickListener(this);
        mille.setOnLongClickListener(this);
        cinqcent = (RelativeLayout) sc.findViewById(R.id.cinqcent);
        cinqcent.setOnClickListener(this);
        cinqcent.setOnLongClickListener(this);
        deuxcentcinquante = (RelativeLayout) sc.findViewById(R.id.deuxcentcinquante);
        deuxcentcinquante.setOnClickListener(this);
        deuxcentcinquante.setOnLongClickListener(this);
        deuxcent = (RelativeLayout) sc.findViewById(R.id.deuxcent);
        deuxcent.setOnClickListener(this);
        deuxcent.setOnLongClickListener(this);
        cent = (RelativeLayout) sc.findViewById(R.id.cent);
        cent.setOnClickListener(this);
        cent.setOnLongClickListener(this);
        cinquante = (RelativeLayout) sc.findViewById(R.id.cinquante);
        cinquante.setOnClickListener(this);
        cinquante.setOnLongClickListener(this);
        vingcinq = (RelativeLayout) sc.findViewById(R.id.vingcinq);
        vingcinq.setOnClickListener(this);
        vingcinq.setOnLongClickListener(this);
    }


    /*

    @Override
    public void onFragmentInteraction(int checkedItems) {
        if (checkedItems==1 && mMode==null){
            changeContextual();
        }
        else if (checkedItems==0)  destroyActionMode();
        if(mMode != null) mMode.setTitle(String.valueOf(checkedItems) + getString(R.string.itemSelected));
    }




    public void changeContextual() {

        mMode = startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.item_menu, menu);
                mActionModeIsActive = true ;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_del:{
                        if (venteFormFragment != null){
                            venteFormFragment.deleteItem();
                        }
                        destroyActionMode();
                    }
                    break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mMode = null;
                if (venteFormFragment != null) venteFormFragment.destroyActionMode();
            }
        });
    }

    public void destroyActionMode(){
        mActionModeIsActive = false ;
        if(mMode!=null) mMode.finish();
        if (venteFormFragment != null) venteFormFragment.destroyActionMode();
    }
     */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(mActionModeIsActive) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                // handle your back button code here
                //destroyActionMode();
                return true; // consumes the back key event - ActionMode is not finished
            }
        }
        return super.dispatchKeyEvent(event);
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




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            // handle your back button code here

            final AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
            builder.setTitle(getString(R.string.app_name)) ;
            builder.setMessage(getString(R.string.exit_confirm)) ;
            builder.setPositiveButton(getString(R.string.quiter), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }) ;
            builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }) ;
            final AlertDialog alertdialog = builder.create();
            alertdialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                    alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
                }
            }) ;
            alertdialog.show();

        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dimille : {
                TextView tv = (TextView) dixmille.getChildAt(1);
                augmenter(10000,tv);
            } break;
            case R.id.cinqmille : {
                TextView tv = (TextView) cinqmille.getChildAt(1);
                augmenter(5000, tv);
            } break;
            case R.id.deuxmille : {
                TextView tv = (TextView) deuxmille.getChildAt(1);
                augmenter(2000, tv);
            } break;
            case R.id.mille : {
                TextView tv = (TextView) mille.getChildAt(1);
                augmenter(1000, tv);
            } break;
            case R.id.cinqcent : {
                TextView tv = (TextView) cinqcent.getChildAt(1);
                augmenter(500, tv);
            } break;
            case R.id.deuxcentcinquante : {
                TextView tv = (TextView) deuxcentcinquante.getChildAt(1);
                augmenter(250, tv);
            } break;
            case R.id.deuxcent : {
                TextView tv = (TextView) deuxcent.getChildAt(1);
                augmenter(200, tv);
            } break;
            case R.id.cent : {
                TextView tv = (TextView) cent.getChildAt(1);
                augmenter(100, tv);
            } break;
            case R.id.cinquante : {
                TextView tv = (TextView) cinquante.getChildAt(1);
                augmenter(50, tv);
            } break;
            case R.id.vingcinq : {
                TextView tv = (TextView) vingcinq.getChildAt(1);
                augmenter(25, tv);
            } break;
        }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.dimille : {
                TextView tv = (TextView) dixmille.getChildAt(1);
                diminuer(10000,tv);
            } break;
            case R.id.cinqmille : {
                TextView tv = (TextView) cinqmille.getChildAt(1);
                diminuer(5000,tv);
            } break;
            case R.id.deuxmille : {
                TextView tv = (TextView) deuxmille.getChildAt(1);
                diminuer(2000,tv);
            } break;
            case R.id.mille : {
                TextView tv = (TextView) mille.getChildAt(1);
                diminuer(1000,tv);
            } break;
            case R.id.cinqcent : {
                TextView tv = (TextView) cinqcent.getChildAt(1);
                diminuer(500,tv);
            } break;
            case R.id.deuxcentcinquante : {
                TextView tv = (TextView) deuxcentcinquante.getChildAt(1);
                diminuer(250,tv);
            } break;
            case R.id.deuxcent : {
                TextView tv = (TextView) deuxcent.getChildAt(1);
                diminuer(200,tv);
            } break;
            case R.id.cent : {
                TextView tv = (TextView) cent.getChildAt(1);
                diminuer(100,tv);
            } break;
            case R.id.cinquante : {
                TextView tv = (TextView) cinquante.getChildAt(1);
                diminuer(50,tv);
            } break;
            case R.id.vingcinq : {
                TextView tv = (TextView) vingcinq.getChildAt(1);
                diminuer(25,tv);
            } break;
        }
        return true;
    }

    public void diminuer(int v, TextView tv){
        double val = 0 ;
        double prix = 0 ;
        if (tv.getVisibility()!=View.GONE) val = Double.parseDouble(tv.getText().toString());
        String p = editText.getText().toString() ;
        if (p.length()==0) editText.setText("0");

        // Si le total afficher est superieur à zero
        if (!total.getText().equals("0")){

            prix = Double.parseDouble(total.getText().toString()) ;
            if (prix>v) prix -= v ;

            // Si c'est achat donc on rempli la case prix achat
            editText.setText(String.valueOf(prix));
            // S'il ya une etiquette sur les billets on les met à jour
            if (val>0){
                val-- ;
                tv.setText(String.valueOf(val));
                tv.setVisibility(View.VISIBLE);
            }

            total.setText(String.valueOf(prix));
        }

        if (val==0)tv.setVisibility(View.GONE);
    }


    public void augmenter(int v,TextView tv){
        double val = 0 ;
        double prix = 0 ;
        if (tv.getVisibility()!=View.GONE) val = Double.parseDouble(tv.getText().toString());

        String p = editText.getText().toString() ;
        if (p.length()==0) editText.setText("0");
        p = editText.getText().toString() ;
        val++ ;
        // Si c'est achat donc on rempli la case prix achat
        prix += v + Double.parseDouble(editText.getText().toString()) ;
        editText.setText(String.valueOf(prix));
        // On met à jour les etiquettes
        tv.setText(String.valueOf(val));
        tv.setVisibility(View.VISIBLE);

        total.setText(String.valueOf(prix));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    private void makeAlertDialog(String string, final boolean delete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this) ;
        builder.setTitle(R.string.app_name) ;
        builder.setMessage(string) ;
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }) ;

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
            }
        });
        alert.show();
    }

    // ------------------- TACHE ASYNCHROME POUR LA SYNCHRONISATION ------------------------------



    public class SynchronisationProduit extends AsyncTask<String, String, String> {

        ProduitDAO produitDAO = null;
        MouvementDAO mouvementDAO = null;
        ArrayList<Produit> produits = null;

        @Override
        protected String doInBackground(String... params) {
            Produit produit = null;

            for (int i = 0; i < produits.size(); ++i) {
                produit = produits.get(i);

                FormBody.Builder formBuilder = new FormBody.Builder() ;
                formBuilder.add(ProduitHelper.CODE, produit.getCode());
                formBuilder.add(ProduitHelper.UTILISATEUR_ID, String.valueOf(produit.getUtilisateur_id()));
                formBuilder.add(ProduitHelper.LIBELLE, String.valueOf(produit.getLibelle()));
                formBuilder.add(ProduitHelper.PRIXACHAT, String.valueOf(produit.getPrixA()));
                formBuilder.add(ProduitHelper.PRIXVENTE, String.valueOf(produit.getPrixV()));
                formBuilder.add(ProduitHelper.CATEGORIE_PRODUIT, String.valueOf(produit.getCategorie_id()));
                formBuilder.add(ProduitHelper.ID_EXTERNE, String.valueOf(produit.getId_externe()));
                formBuilder.add(ProduitHelper.UNITE, String.valueOf(produit.getUnite()));
                formBuilder.add(ProduitHelper.CREATED_AT, DAOBase.formatter.format(produit.getCreated_at()));
                formBuilder.add(ProduitHelper.MODIFIABLE, String.valueOf(produit.getModifiable()));
                formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId()));



                String result = "" ;


                try {
                    result = Utiles.POST(Url.getPostProduitUrl(AccueilActivity.this),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("URL", Url.getPostProduitUrl(AccueilActivity.this));
                Log.e("RESULLLTT" + String.valueOf(i), result);
                //Log.e("CODE", produit.getCode());

                if (result.split(":").length == 3 && result.contains("OK")) {
                    produit.setEtat(2);
                    produit.setCode(result.split(":")[1]);
                    // Si le produit est un produit qui ne se trouve pas sur le serveur
                    if (produit.getId_externe()==0){
                        produit.setId_externe(Long.valueOf(result.split(":")[2]));
                        // on met à jour le mouvement en remplacant l'id interne par l'id externe
                        mouvementDAO.updateMany(produit) ;
                    }
                    final Produit finalProduit = produit;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            synchronisationProduitImage = new SynchronisationProduitImage(finalProduit);
                            synchronisationProduitImage.execute();
                        }
                    });
                    produitDAO.update(produit);
                    REUSSI++;
                } else ECHEC++;

                publishProgress("ok");
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (produitDAO == null)    produitDAO = new ProduitDAO(AccueilActivity.this);
            if (mouvementDAO == null)    mouvementDAO = new MouvementDAO(AccueilActivity.this);
            produits = produitDAO.getNonSyncInterval(null, null);
            MAX_SIZE = produits.size();
            ECHEC = 0;
            REUSSI = 0;
            PROGRESS = 0;
            pointVenteDAO = new PointVenteDAO(AccueilActivity.this) ;
            showDialog(PROGRESS_DIALOG_ID);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            etape = 2 ;
            startSync();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PROGRESS++;
            mProgressBar.setProgress(PROGRESS);
        }


    }






    public class SynchronisationPartenaire extends AsyncTask<String, String, String> {

        PartenaireDAO partenaireDAO = null;
        MouvementDAO mouvementDAO = null;
        ArrayList<Partenaire> partenaires = null;

        @Override
        protected String doInBackground(String... params) {
            Partenaire partenaire = null;

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
                formBuilder.add(PartenaireHelper.ADRESSE, String.valueOf(partenaire.getAdresse()));
                formBuilder.add(PartenaireHelper.UTILISATEUR_iD, String.valueOf(partenaire.getUtilisateur_id()));
                formBuilder.add(PartenaireHelper.POINTVENTE_iD, String.valueOf(partenaire.getPointvente_id()));
                //formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId())));

                Log.e("PV", String.valueOf(partenaire.getPointvente_id()));


                String result = "" ;


                try {
                    result = Utiles.POST(Url.getPostPartenaireUrl(AccueilActivity.this),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("URL", Url.getPostPartenaireUrl(AccueilActivity.this));
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
                    REUSSI++;
                } else ECHEC++;

                publishProgress("ok");
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (partenaireDAO == null)    partenaireDAO = new PartenaireDAO(AccueilActivity.this);
            partenaires = partenaireDAO.getNonSync();
            MAX_SIZE = partenaires.size();
            ECHEC = 0;
            REUSSI = 0;
            PROGRESS = 0;
            pointVenteDAO = new PointVenteDAO(AccueilActivity.this) ;
            showDialog(PROGRESS_DIALOG_ID);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            etape = 3 ;
            startSync();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PROGRESS++;
            mProgressBar.setProgress(PROGRESS);
        }


    }




    public class SynchronisationCommercial extends AsyncTask<String, String, String> {

        CommercialDAO commercialDAO = null;
        MouvementDAO mouvementDAO = null;
        ArrayList<Commercial> commercials = null;

        @Override
        protected String doInBackground(String... params) {
            Commercial commercial = null;

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
                //formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId())));

                Log.e("PV", String.valueOf(commercial.getPointvente_id()));



                String result = "" ;


                try {
                    result = Utiles.POST(Url.getPostCommercialUrl(AccueilActivity.this),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("URL", Url.getPostCommercialUrl(AccueilActivity.this));
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
                    REUSSI++;
                } else ECHEC++;

                publishProgress("ok");
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (commercialDAO == null)    commercialDAO = new CommercialDAO(AccueilActivity.this);
            commercials = commercialDAO.getNonSync();
            MAX_SIZE = commercials.size();
            ECHEC = 0;
            REUSSI = 0;
            PROGRESS = 0;
            pointVenteDAO = new PointVenteDAO(AccueilActivity.this) ;
            showDialog(PROGRESS_DIALOG_ID);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            etape = 4 ;
            startSync();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PROGRESS++;
            mProgressBar.setProgress(PROGRESS);
        }


    }

    public class SynchronisationOperation extends AsyncTask<String, String, String> {

        OperationDAO operationDAO = null;
        ArrayList<Operation> operations = null;
        ArrayList<Mouvement> mouvements = null;
        private MouvementDAO mouvementDAO;

        @Override
        protected String doInBackground(String... params) {
            Operation operation = null;


            Log.e("SIZE", String.valueOf(operations.size()));

            for (int i = 0; i < operations.size(); ++i) {
                operation = operations.get(i);
                FormBody.Builder formBuilder = new FormBody.Builder() ;
                formBuilder.add(OperationHelper.TABLE_KEY, String.valueOf(operation.getId_externe()));
                formBuilder.add(OperationHelper.REMISE, String.valueOf(operation.getRemise()));
                formBuilder.add(OperationHelper.MONTANT, String.valueOf(operation.getMontant()));
                formBuilder.add(OperationHelper.CAISSE_ID, String.valueOf(operation.getCaisse_id()));
                formBuilder.add(OperationHelper.TYPEOPERATION_ID, String.valueOf(operation.getTypeOperation_id()));
                formBuilder.add(OperationHelper.DATE_OPERATION, DAOBase.formatter.format(operation.getDateoperation()));
                formBuilder.add(OperationHelper.NBREPRODUIT, String.valueOf(operation.getNbreproduit()));
                formBuilder.add(OperationHelper.ANNULER, String.valueOf(operation.getAnnuler()));
                formBuilder.add(OperationHelper.PARTENAIRE_ID, String.valueOf(operation.getPartenaire_id()));
                formBuilder.add(OperationHelper.RECU, String.valueOf(operation.getRecu()));
                formBuilder.add(OperationHelper.ENTREE, String.valueOf(operation.getEntree()));
                formBuilder.add(OperationHelper.ATTENTE, String.valueOf(operation.getAttente()));
                formBuilder.add(OperationHelper.PAYER, String.valueOf(operation.getPayer()));
                formBuilder.add(OperationHelper.CLIENT, String.valueOf(operation.getClient()));
                formBuilder.add(OperationHelper.COMMERCIAL_ID, String.valueOf(operation.getCommercialid()));
                formBuilder.add(OperationHelper.DESCRIPTION, String.valueOf(operation.getDescription()));
                formBuilder.add(OperationHelper.TOKEN, operation.getToken());
                formBuilder.add(OperationHelper.DATE_ECHEANCE, DAOBase.formatter.format(operation.getDateoperation()));
                formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId()));


                Log.e("Token",operation.getToken());
                Log.e("PV", String.valueOf(pointVenteDAO.getLast().getId()));
                Log.e("URL" + String.valueOf(i), Url.getPostOperationUrl(AccueilActivity.this));


                String result = "" ;


                try {
                    result = Utiles.POST(Url.getPostOperationUrl(AccueilActivity.this),formBuilder.build());
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
                        REUSSI++;
                    } else {
                        ECHEC++;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                publishProgress("ok");
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (operationDAO == null)    operationDAO = new OperationDAO(AccueilActivity.this);
            operations = operationDAO.getNonSync();
            MAX_SIZE = operations.size();
            ECHEC = 0;
            REUSSI = 0;
            PROGRESS = 0;
            pointVenteDAO = new PointVenteDAO(AccueilActivity.this) ;
            mouvementDAO = new MouvementDAO(AccueilActivity.this) ;
            showDialog(PROGRESS_DIALOG_ID);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            etape = 5 ;
            startSync();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PROGRESS++;
            mProgressBar.setProgress(PROGRESS);
        }


    }





    public class SynchronisationMouvement extends AsyncTask<String, String, String> {

        MouvementDAO mouvementDAO = null;
        ArrayList<Mouvement> mouvements = null;

        @Override
        protected String doInBackground(String... params) {
            Mouvement mouvement = null;

            for (int i = 0; i < mouvements.size(); ++i) {
                mouvement = mouvements.get(i);
                FormBody.Builder formBuilder = new FormBody.Builder() ;
                formBuilder.add(MouvementHelper.PRIXV, String.valueOf(mouvement.getPrixV()));
                formBuilder.add(MouvementHelper.PRIXA, String.valueOf(mouvement.getPrixA()));
                formBuilder.add(MouvementHelper.PRODUIT_ID, String.valueOf(mouvement.getProduit_id()));
                formBuilder.add(MouvementHelper.PRODUIT, String.valueOf(mouvement.getProduit()));
                formBuilder.add(MouvementHelper.ENTREE, String.valueOf(mouvement.getEntree()));
                formBuilder.add(MouvementHelper.ANNULER, String.valueOf(mouvement.getAnnuler()));
                formBuilder.add(MouvementHelper.QUANTITE, String.valueOf(mouvement.getQuantite()));
                formBuilder.add(MouvementHelper.OPERATION_ID, String.valueOf(mouvement.getOperation_id()));
                formBuilder.add(MouvementHelper.CREATED_AT, DAOBase.formatter.format(mouvement.getCreated_at()));
                formBuilder.add("pointvente_id", String.valueOf(pointVenteDAO.getLast().getId()));

                Log.e("URL" + String.valueOf(i), Url.getPostMouvementUrl(AccueilActivity.this));

                String result = "" ;


                try {
                    result = Utiles.POST(Url.getPostMouvementUrl(AccueilActivity.this),formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("RESULLLTT MOUVEMENT" + String.valueOf(i), result);

                try {
                    if (result.split(":").length == 2 && result.startsWith("OK:")) {
                        mouvement.setEtat(2);
                        mouvement.setRestant(Double.parseDouble(result.split(":")[1]));
                        mouvementDAO.update(mouvement);
                        REUSSI++;
                    }else {
                        ECHEC++;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                publishProgress("ok");
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mouvementDAO == null)    mouvementDAO = new MouvementDAO(AccueilActivity.this);
            mouvements = mouvementDAO.getNonSync();
            MAX_SIZE = mouvements.size();
            ECHEC = 0;
            REUSSI = 0;
            PROGRESS = 0;
            pointVenteDAO = new PointVenteDAO(AccueilActivity.this) ;
            showDialog(PROGRESS_DIALOG_ID);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            etape = 6 ;
            startSync();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PROGRESS++;
            mProgressBar.setProgress(PROGRESS);
        }

    }



    public class SynchronisationProduitImage extends AsyncTask<String, String, String> {

        ProduitDAO produitDAO = null;
        Produit produit ;
        ImageView imageView = new ImageView(getApplicationContext());

        public SynchronisationProduitImage(Produit finalProduit) {
            produit = finalProduit ;
        }

        @Override
        protected String doInBackground(String... params) {
            Produit produit = null;

            if (produit!=null && produit.getImage()!=null && produit.getImage().contains(Utiles.PV_PRODUIT_IMAGE_TMP_DIR)) {

                try {
                    //Picasso.with(MainActivity.this).load(Uri.parse(produit.getImmatriculation())).into(imageView);
                    int reponse = Utiles.uploadFile(produit.getImage(),produit.getId_externe()+".jpeg",AccueilActivity.this) ;
                    if (reponse==200){
                        String name = null ;
                        try {
                            Uri path = Utiles.saveImageExternalStorage(BitmapFactory.decodeFile(produit.getImage()), AccueilActivity.this, produit.getId_externe()+".jpg", Utiles.PV_PRODUIT_IMAGE_DIR) ;
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

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (produitDAO == null)    produitDAO = new ProduitDAO(AccueilActivity.this);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PROGRESS++;
        }
    }



}
