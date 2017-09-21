package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.PointVente;

public class CompteBanqueFormActivity extends AppCompatActivity {

    private ScrollView sc = null ;
    private AlertDialog.Builder builder;
    private AlertDialog alert;
    private Button total =null ;
    private Button init =null ;
    private Button close = null;
    private RelativeLayout rv = null ;
    private Bitmap photo = null ;
    private EditText compte;
    private EditText banque;
    private Button valider;
    private ProgressDialog mProgressBar;
    final static int PROGRESS_DIALOG_ID = 0 ;
    private int MAX_SIZE;
    private CompteBanque compteBanque;
    private CompteBanqueDAO compteBanqueDAO;
    private AddCompteBanqueTask addCompteBanqueTask;
    CheckBox cheque = null ;
    CheckBox cartebank = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte_banque_form);
        setupToolbar() ;
        initialisation() ;

        Intent intent = getIntent() ;
        if (intent!=null && intent.hasExtra("ID")){
            long id = intent.getLongExtra("ID",0) ;
            compteBanque = compteBanqueDAO.getOne(id) ;
            init(compteBanque);
        }
    }


    private void initialisation() {
        compte = (EditText) findViewById(R.id.nocompte);
        banque = (EditText) findViewById(R.id.banque);
        compteBanqueDAO = new CompteBanqueDAO(this) ;
        cheque = (CheckBox) findViewById(R.id.cheque);
        cartebank = (CheckBox) findViewById(R.id.cartebanque);

        valider = (Button) findViewById(R.id.valider);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCompteBanque();
            }
        });
    }


    private void init(CompteBanque compteBanque) {
        compte.setText(compteBanque.getCode());
        banque.setText(compteBanque.getLibelle());

        if (compteBanque.getCheque() == 1)cheque.setChecked(true) ;
        if (compteBanque.getCartebanque() == 1)cartebank.setChecked(true) ;
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
        getMenuInflater().inflate(R.menu.menu_partenaire_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        if (mProgressBar ==null){
            mProgressBar = new ProgressDialog(this) ;
            mProgressBar.setCancelable(false);
            mProgressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    try {
                        addCompteBanqueTask.cancel(true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            mProgressBar.setTitle(getString(R.string.send_loding));
            mProgressBar.setMessage(getString(R.string.wait));
            mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressBar.setMax(MAX_SIZE);
        }

        return mProgressBar ;
    }

    private void addCompteBanque() {
        if (isValid()){
            showDialog(PROGRESS_DIALOG_ID);
            if (compteBanque==null)compteBanque = new CompteBanque() ;
            compteBanque.setCode(compte.getText().toString());
            PointVente pv = new PointVenteDAO(CompteBanqueFormActivity.this).getLast() ;
            compteBanque.setLibelle(banque.getText().toString());
            compteBanque.setEtat(0);
            compteBanque.setUtilisateur_id(pv.getUtilisateur());
            if (cheque.isChecked()) compteBanque.setCheque(1);
            if (cartebank.isChecked()) compteBanque.setCartebanque(1);

            if (compteBanqueDAO.getOne(compteBanque.getCode()) == null && compteBanque.getId_externe()==0){
                addCompteBanqueTask = new AddCompteBanqueTask(compteBanque) ;
                addCompteBanqueTask.execute() ;
            }
            else if (compteBanque.getId_externe()!=0){
                addCompteBanqueTask = new AddCompteBanqueTask(compteBanque) ;
                addCompteBanqueTask.execute() ;
            }
            else{
                Toast.makeText(CompteBanqueFormActivity.this, R.string.cptexist, Toast.LENGTH_SHORT).show();
                dismissDialog(PROGRESS_DIALOG_ID);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValid() {
        if (compte.getText().length() <= 0) return false ;
        if (banque.getText().length() <= 0) return false ;

        return true;
    }

    public class AddCompteBanqueTask extends AsyncTask<Void, Integer, String> {

        String lib = null ;
        String code = null ;
        CompteBanque compteBanque ;

        public AddCompteBanqueTask(CompteBanque p) {
            compteBanque = p ;
        }

        @Override
        protected String doInBackground(Void... params) {
            Date d = new Date() ;

            compteBanque.setEtat(0);
            long result = 0 ;

            if (compteBanque.getId_externe()==0)  result =  compteBanqueDAO.add(compteBanque) ;
            else   result =  compteBanqueDAO.update(compteBanque) ;

            return String.valueOf(result);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            valider.setEnabled(true);

                if (compteBanque.getId_externe()==0)Toast.makeText(getApplicationContext(),getString(R.string.compteBanque_success),Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(),getString(R.string.compteBanque_update),Toast.LENGTH_LONG).show();

                clean();
                dismissDialog(PROGRESS_DIALOG_ID);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (compteBanqueDAO==null)compteBanqueDAO = new CompteBanqueDAO(getApplicationContext()) ;
        }
    }

    private void clean() {
        compte.setText("");
        banque.setText("");
        compteBanque = null ;
    }

}
