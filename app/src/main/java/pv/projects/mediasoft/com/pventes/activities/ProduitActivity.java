package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.fragment.ProduitFragment;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.model.PointVente;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.utiles.FileChooser;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class ProduitActivity extends AppCompatActivity  implements ViewPager.OnPageChangeListener, ProduitFragment.OnFragmentInteractionListener{

    FloatingActionButton produitAdd = null ;

    ActionMode mMode = null ;
    private boolean mActionModeIsActive;
    ProduitFragment produitNonSyncFragment = null ;
    ProduitFragment reccurentProduitFragment = null ;
    ProduitFragment allProduitFragment = null ;

    public final static int PROGRESS_DIALOG_ID = 0 ;

    public ProgressDialog mProgressBar;
    private int MAX_SIZE;
    private static final int CHOOSE_PRODUIT_REQUEST = 1;


    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH) ;


    AlertDialog.Builder dateBox = null;
    Dialog detailBox = null;
    Button button = null ;
    private ArrayList<Mouvement> mouvements= null ;
    private LayoutInflater mInflater= null ;
    private TabLayout tabLayout= null ;
    private ViewPager viewPager= null ;
    private ProduitCollectionAdapter produitCollectionAdapter= null ;
    private int mPosition = 0 ;
    private SearchView mSearchView;
    private ProduitDAO produitDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produit);

        init() ;
        setupToolbar();
        setupFragments();
        setupTablayout();
    }


    private void init() {
        mInflater = LayoutInflater.from(this) ;
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        produitAdd = (FloatingActionButton) findViewById(R.id.fab);

        produitAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProduitActivity.this, ProduitFormActivity.class);
                startActivityForResult(intent, CHOOSE_PRODUIT_REQUEST);
            }
        });

        produitDAO = new ProduitDAO(ProduitActivity.this) ;
    }



    private void setupTablayout(){
        produitCollectionAdapter = new ProduitCollectionAdapter(getSupportFragmentManager()) ;
        viewPager.setAdapter(produitCollectionAdapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupFragments() {
        final FragmentManager fm = getSupportFragmentManager();
        //this.allProduitFragment = (ProduitFragment) fm.findFragmentByTag(ProduitFragment.TAG);
        if (this.allProduitFragment == null) {
            this.allProduitFragment = ProduitFragment.newInstance("1",null);
        }
        //this.reccurentProduitFragment = (ProduitFragment) fm.findFragmentByTag(ProduitFragment.TAG);
        if (this.reccurentProduitFragment == null) {
            this.reccurentProduitFragment = ProduitFragment.newInstance("2",null);
        }
        //this.produitNonSyncFragment = (ProduitFragment) fm.findFragmentByTag(ProduitFragment.TAG);
        if (this.produitNonSyncFragment == null) {
            this.produitNonSyncFragment = ProduitFragment.newInstance("3",null);
        }

    }



    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_back);
        ab.setDisplayHomeAsUpEnabled(true);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_produit, menu);

        // SearchView
        MenuItem itemSearch = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    allProduitFragment.filtrer(newText);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    produitNonSyncFragment.filtrer(newText);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    reccurentProduitFragment.filtrer(newText);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.menu_choose_file) {
            chooseExcelFile();
            return true;
        }
        else if (id == R.id.action_interval){
            dateBox = new AlertDialog.Builder(this) ;
            ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.dialogbox,null);
            dateBox.setView(scrollView) ;
            dateBox.setTitle(getString(R.string.datechoice)) ;

            final DatePicker debut = (DatePicker) scrollView.findViewById(R.id.dateDebut);
            final DatePicker fin = (DatePicker) scrollView.findViewById(R.id.dateFin);
            button = (Button) scrollView.findViewById(R.id.valider);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dateBox!=null){
                        String dateDebut = String.valueOf(debut.getYear()) + "-" + String.valueOf(debut.getMonth()+1) + "-" + String.valueOf(debut.getDayOfMonth()) ;
                        String dateFin = String.valueOf(fin.getYear()) + "-" + String.valueOf(fin.getMonth()+1) + "-" + String.valueOf(fin.getDayOfMonth()) ;
                        if (mPosition == 0) {
                            allProduitFragment.interval(dateDebut, dateFin) ;
                        }
                        else if (mPosition == 1){
                            reccurentProduitFragment.interval(dateDebut,dateFin) ;
                        }
                        else{
                            produitNonSyncFragment.interval(dateDebut,dateFin) ;
                        }

                        detailBox.dismiss();
                    }
                }
            });

            detailBox = dateBox.show();
        }

        else if (id == R.id.action_imp)
        {if (allProduitFragment.getProduits()!=null && allProduitFragment.getProduits().size()>0){
            imprimeDoc(allProduitFragment.getProduits());
        }
        else{
            Toast.makeText(ProduitActivity.this, "Aucun produit sur cette periode", Toast.LENGTH_LONG).show();
        }
        }

        return super.onOptionsItemSelected(item);
    }

    public void chooseExcelFile() {
        AlertDialog.Builder builder =  new AlertDialog.Builder(ProduitActivity.this) ;
        builder.setTitle(getString(R.string.choosefileexcel)) ;
        builder.setMessage(
                "code\n" +
                "libelle\n" +
                "prix achat\n" +
                "prix vente\n" +
                "unité") ;
        builder.setPositiveButton(R.string.choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                new FileChooser(ProduitActivity.this, new String[]{"xls", "xlsx"})
                        .setFileListener(new FileChooser.FileSelectedListener() {
                            @Override
                            public void fileSelected(File file) {
                                String filePath = file.getAbsolutePath();
                                Log.e("PATH",filePath) ;
                                ChooseFileTask chooseFileTask = new ChooseFileTask(filePath) ;
                                chooseFileTask.execute() ;
                            }
                        })
                        .showDialog();
            }
        }) ;
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }) ;
        builder.show();
    }



    private void imprimeDoc(final ArrayList<Produit> produits) {

        final CharSequence[] items = { getString(R.string.pdf), getString(R.string.xls), getString(R.string.fermer) };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(getString(R.string.action));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.pdf))) {
                    imprimePDFDoc(produits);
                    detailBox.dismiss();
                    dateBox = null;
                } else if (items[item].equals(getString(R.string.xls))) {

                    imprimeExcelDoc(produits);
                    detailBox.dismiss();
                    dateBox = null;
                } else {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.show();

    }


    private void imprimeExcelDoc(final ArrayList<Produit> produits) {
        final EditText edittext = new EditText(ProduitActivity.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(ProduitActivity.this) ;

        alert.setMessage("Document EXCEL");
        alert.setTitle("Enter le nom du fichier EXCEL");

        alert.setView(edittext);
        edittext.setText("Liste des produits");

        alert.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String name = edittext.getText().toString();
                //Utiles.createandDisplayProduitExcel(produits, name, ProduitActivity.this);
                ;
            }
        });

        alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }


    private void imprimePDFDoc(final ArrayList<Produit> produits) {
        final EditText edittext = new EditText(ProduitActivity.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(ProduitActivity.this) ;

        alert.setMessage("Document PDF");
        alert.setTitle("Enter le nom du fichier PDF");

        alert.setView(edittext);
        edittext.setText("Liste des produits");

        alert.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String name = edittext.getText().toString();
                //Utiles.createandDisplayProduitPdf(produits, name, ProduitActivity.this);
            }
        });

        alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }




    public ProduitActivity getContext(){
        return this ;
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
                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext()) ;
                        builder.setTitle(getString(R.string.app_name)) ;
                        builder.setMessage(getString(R.string.delconfirm)) ;
                        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog(PROGRESS_DIALOG_ID);

                                if (mPosition==0){
                                    //allProduitFragment.deleteItem();
                                }
                                else if (mPosition==1){
                                    //reccurentProduitFragment.deleteItem();
                                }
                                else if (mPosition==2){
                                    //produitNonSyncFragment.deleteItem();
                                }

                                destroyActionMode();
                            }
                        }) ;
                        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }) ;
                        builder.show() ;

                    }

                    break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mMode = null;
                if (mPosition==0){
                    if (allProduitFragment != null) allProduitFragment.destroyActionMode();
                }
                else if (mPosition==1){
                    if (reccurentProduitFragment != null) reccurentProduitFragment.destroyActionMode();
                }
                else if (mPosition==2){
                    if (produitNonSyncFragment != null) produitNonSyncFragment.destroyActionMode();
                }

            }
        });
    }

    public void destroyActionMode(){
        mActionModeIsActive = false ;
        if(mMode!=null) mMode.finish();
        if (mPosition==0){
            if (allProduitFragment != null) allProduitFragment.destroyActionMode();
        }
        else if (mPosition==1){
            if (reccurentProduitFragment != null) reccurentProduitFragment.destroyActionMode();
        }
        else if (mPosition==1){
            if (produitNonSyncFragment != null) produitNonSyncFragment.destroyActionMode();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(mActionModeIsActive) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                // handle your back button code here
                destroyActionMode();
                return true; // consumes the back key event - ActionMode is not finished
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPosition = position ;

        // Snackbar.make(findViewById(R.id.drLayout),String.valueOf(position),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position ;

        // Snackbar.make(findViewById(R.id.drLayout),String.valueOf(position),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFragmentInteraction(int checkedItems) {
        if (checkedItems==1 && mMode==null){
            changeContextual();
        }
        else if (checkedItems==0)  destroyActionMode();
        if(mMode != null) mMode.setTitle(String.valueOf(checkedItems) + getString(R.string.itemSelected));
    }


    private class  ProduitCollectionAdapter extends FragmentPagerAdapter {
        int pages = 3 ;

        public ProduitCollectionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 : {
                    return allProduitFragment ;
                }
                case 1 : {
                    return reccurentProduitFragment ;
                }
                case 2 : {
                    return produitNonSyncFragment ;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return pages;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 : {
                    return getString(R.string.tout) ;
                }
                case 1 : {
                    return getString(R.string.recurrent) ;
                }
                case 2 : {
                    return getString(R.string.nonSync) ;
                }
            }
            return super.getPageTitle(position);
        }
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


    public void dissmisAlert(){
        try {
            mProgressBar.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public class ChooseFileTask extends AsyncTask<Void,String,String> {
        int size = 0 ;
        String filePath = null ;
        public ChooseFileTask(String path) {
            this.filePath = path ;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PointVente pv = new PointVenteDAO(ProduitActivity.this).getLast() ;
                        try {
                            Workbook workbook = Workbook.getWorkbook(new File(filePath)) ;
                            Sheet sheet = workbook.getSheet(0) ;
                            size = sheet.getRows() ;
                            Produit produit = null ;
                            for (int i = 1; i < size; i++) {
                                Cell[] row = sheet.getRow(i);
                                produit = new Produit() ;
                                produit.setCode(row[0].getContents());
                                produit.setLibelle(row[1].getContents());
                                try {
                                    produit.setPrixA(Double.parseDouble(row[2].getContents()));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                try {
                                    produit.setPrixV(Double.parseDouble(row[3].getContents()));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                produit.setUnite(row[4].getContents());

                                produit.setUtilisateur_id(pv.getUtilisateur());

                                produitDAO.add(produit) ;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (BiffException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return String.valueOf(size) ;
            } catch (Exception ex) {
                Log.e("Import excel error", ex.getMessage());
            }
            return "0";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(PROGRESS_DIALOG_ID);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissDialog(PROGRESS_DIALOG_ID);
            Toast.makeText(ProduitActivity.this, result + " " + getString(R.string.produitajoute), Toast.LENGTH_SHORT).show();
        }
    }


}
