package pv.projects.mediasoft.com.pventes.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.TypeOperationDAO;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.TypeOperation;

public class DiversFormActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String FILEPATH = "filepath";
    EditText montant = null ;
    EditText description = null ;

    Button valider = null ;
    Button venteBtn = null ;

    boolean checked = false ;
    OperationDAO operationDAO = null ;
    TypeOperationDAO typeOperationDAO = null ;
    AddOperationTask addOperationTask = null ;
    private ProgressDialog mProgressBar;
    final static int PROGRESS_DIALOG_ID = 0 ;
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


    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private String filePath = null;
    private Operation operation;
    private String dossier;
    private boolean achat = false;
    private ScrollView sc = null ;
    private AlertDialog.Builder builder;
    private AlertDialog alert;
    private Button total =null ;
    private Button init =null ;
    private Button close = null;
    private RelativeLayout rv = null ;
    private Bitmap photo = null ;
    private Spinner spinner = null ;
    private ArrayAdapter mAdapter;
    private ArrayList<TypeOperation> to;


    private Spinner spinner_banque;
    private long cpt_bk = 0 ;
    private ArrayList<CompteBanque> cb;
    private EditText numcheque;
    private String numerocheque = "";

    private static String MODE = "ESPECE";
    private static final String ESPECE = "ESPECE";
    private static final String CHEQUE = "CHEQUE";
    private static final String VIREMENT = "VIREMENT";
    private DeviseDAO deviseDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divers_form);

        setupToolbar() ;
        initialisation() ;

        if (savedInstanceState!=null) {
            filePath = savedInstanceState.getString(FILEPATH) ;
            if (filePath!=null)outPutFile = new File(filePath) ;
        }

        Intent intent = null ;
        intent = getIntent() ;
        if (intent!=null){

        }
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
                montant.setText("0");
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel() ;
            }
        });

        String p = montant.getText().toString() ;
        if (p.length()==0) montant.setText("0");
        total.setText(montant.getText().toString());

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

    private void initialisation() {
        description = (EditText) findViewById(R.id.description);
        spinner = (Spinner) findViewById(R.id.type);
        montant = (EditText) findViewById(R.id.mte1);

        deviseDAO = new DeviseDAO(this) ;

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0) {
                    description.setText(to.get(position-1).getLibelle());
                    setTitle(to.get(position-1).getLibelle());
                }
                else{
                    setTitle(R.string.title_activity_divers_form);
                    description.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        operationDAO = new OperationDAO(this) ;
        typeOperationDAO = new TypeOperationDAO(this) ;
        to = typeOperationDAO.getAllDivers();
        ArrayList<String> typeoperations = new ArrayList<String>();
        for (int i = 0; i < to.size(); i++) {
            typeoperations.add(to.get(i).getLibelle());
        }

        typeoperations.add(0,getString(R.string.choosetype));


        mAdapter = new ArrayAdapter(DiversFormActivity.this, android.R.layout.simple_list_item_1, typeoperations);
        spinner.setAdapter(mAdapter);
        spinner.setSelection(0);

        valider = (Button) findViewById(R.id.valider);
        venteBtn = (Button) findViewById(R.id.btnvente);
        if (deviseDAO.getReference()!=null) venteBtn.setText(deviseDAO.getReference().getCodedevise());

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOperation();
            }
        });

        venteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money_init() ;
                achat = false ;
                builder = new AlertDialog.Builder(DiversFormActivity.this) ;
                builder.setView(rv) ;
                alert = builder.show() ;
            }
        });


        spinner_banque = (Spinner) findViewById(R.id.banque);
        RadioGroup modegroupe = (RadioGroup) findViewById(R.id.modegroup);
        final LinearLayout lignecheque = (LinearLayout) findViewById(R.id.lignecheque);
        numcheque = (EditText) findViewById(R.id.numcheque);

        modegroupe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                lignecheque.setVisibility(View.GONE);
                if (checkedId == R.id.espece) {
                    spinner_banque.setVisibility(View.GONE);
                    MODE = ESPECE ;
                }
                else  {
                    CompteBanqueDAO compteBanqueDAO = new CompteBanqueDAO(DiversFormActivity.this) ;
                    ArrayList<String> banque = new ArrayList<String>();


                    if (checkedId == R.id.cheque){
                        MODE = CHEQUE ;
                        lignecheque.setVisibility(View.VISIBLE);
                        cb = compteBanqueDAO.getCheque();
                        for (int i = 0; i < cb.size(); i++) {
                            banque.add(cb.get(i).getLibelle() + " - " + cb.get(i).getCode() );
                        }
                    }

                    else if (checkedId == R.id.cartebanque){
                        MODE = VIREMENT ;
                        Log.e(MODE,VIREMENT) ;
                        cb = compteBanqueDAO.getCarteBanque();
                        for (int i = 0; i < cb.size(); i++) {
                            banque.add(cb.get(i).getLibelle() + " - " + cb.get(i).getCode() );
                        }
                    }


                    banque.add(0,getString(R.string.choosebank));

                    mAdapter = new ArrayAdapter(DiversFormActivity.this, android.R.layout.simple_list_item_1, banque);
                    spinner_banque.setAdapter(mAdapter);
                    spinner_banque.setSelection(0);
                    spinner_banque.setVisibility(View.VISIBLE);
                }
            }
        });



        CompteBanqueDAO compteBanqueDAO = new CompteBanqueDAO(DiversFormActivity.this) ;
        cb = compteBanqueDAO.getAll();
        ArrayList<String> banque = new ArrayList<String>();
        for (int i = 0; i < cb.size(); i++) {
            banque.add(cb.get(i).getLibelle() + " - " + cb.get(i).getCode() );
        }

        banque.add(0,getString(R.string.choosebank));

        mAdapter = new ArrayAdapter(DiversFormActivity.this, android.R.layout.simple_list_item_1, banque);
        spinner_banque.setAdapter(mAdapter);
        spinner_banque.setSelection(0);


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
        getMenuInflater().inflate(R.menu.menu_operation_form, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        if (mProgressBar ==null){
            mProgressBar = new ProgressDialog(this) ;
            mProgressBar.setCancelable(true);
            mProgressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    try {
                        addOperationTask.cancel(true);
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

    private void addOperation() {
        if (isValid()){
            showDialog(PROGRESS_DIALOG_ID);
            addOperationTask = new AddOperationTask(description.getText().toString(),montant.getText().toString(),to.get(spinner.getSelectedItemPosition()-1), cpt_bk) ;
            addOperationTask.execute() ;
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValid() {
        if (montant.getText().length() <= 0) return false ;
        if (Double.valueOf(montant.getText().toString())<=0) return false ;

        if (spinner.getSelectedItemPosition()==0) return false ;
        if (spinner_banque.getSelectedItemPosition()==0 && spinner_banque.getVisibility()==View.VISIBLE && cb.size()>0) {
            Toast.makeText(DiversFormActivity.this, getString(R.string.baquerror), Toast.LENGTH_LONG).show();
            return false;
        }
        else if (cb.size()==0) cpt_bk = 0 ;
        else if (spinner_banque.getVisibility()!=View.VISIBLE) cpt_bk = 0 ;
        else {
            cpt_bk = cb.get(spinner_banque.getSelectedItemPosition()-1).getId_externe() ;
            numerocheque = numcheque.getText().toString() ;
        }

        if (MODE.equals(CHEQUE) && numerocheque.length()==0){
            Toast.makeText(DiversFormActivity.this, getString(R.string.saisirnumcheque), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public class AddOperationTask extends AsyncTask<Void, Integer, String> {

        String description = "" ;
        String code = null ;
        String unite = null ;
        String montant = null ;
        String prixAchat = null ;
        Operation operation ;
        private TypeOperationDAO typeOperationDAO;
        private CaisseDAO caisseDAO;
        TypeOperation to = null ;
        long cpt = 0 ;

        public AddOperationTask(String desc, String mte, TypeOperation typeOperation, long cpt_bk) {
            if (desc!=null)description = desc ;
            montant = mte ;
            to = typeOperation ;
            this.cpt = cpt_bk ;
        }

        @Override
        protected String doInBackground(Void... params) {
            Date d = new Date() ;
            operation = new Operation() ;
            operation.setDescription(description);
            operation.setMontant(Double.valueOf(montant));
            operation.setCaisse_id(caisseDAO.getFirst().getId());
            operation.setTypeOperation_id(to.getCode());

            if (to.getCode().equals(OperationDAO.CH_EXC) || to.getCode().equals(OperationDAO.CH_FN)) operation.setEntree(0);
            else  operation.setEntree(1);

            // Important A revoir
            //if (preferences.getLong(LicenceActivity.CAISSE_ID,-1)==-1 || preferences.getLong(LicenceActivity.POINT_VENTE_ID,-1)==-1 || preferences.getLong(LicenceActivity.USER_ID,-1)==-1) return false ;

            operation.setEtat(0);
            operation.setComptebanque_id(cpt);
            operation.setModepayement(MODE);
            operation.setNumcheque(numerocheque);

            operation.setToken(caisseDAO.getFirst().getCode()+"/"+operation.getMontant()+"/"+System.currentTimeMillis());
            long result =  operationDAO.add(operation) ;

            return String.valueOf(result);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            valider.setEnabled(true);
            if (Long.valueOf(result)>0) {
                clean();
                Toast.makeText(DiversFormActivity.this,getString(R.string.divers_success),Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(DiversFormActivity.this,getString(R.string.echec_ajout),Toast.LENGTH_LONG).show();
            dismissDialog(PROGRESS_DIALOG_ID);
        }





        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (operationDAO==null)operationDAO = new OperationDAO(DiversFormActivity.this) ;
            if (caisseDAO==null)caisseDAO = new CaisseDAO(DiversFormActivity.this) ;
            if (typeOperationDAO==null)typeOperationDAO = new TypeOperationDAO(DiversFormActivity.this) ;
        }
    }

    private void clean() {
        description.setText("");
        montant.setText("0");
        spinner.setSelection(0);
    }


    public void diminuer(int v, TextView tv){
        double val = 0 ;
        double prix = 0 ;
        if (tv.getVisibility()!=View.GONE) val = Double.parseDouble(tv.getText().toString());
        String p = montant.getText().toString() ;
        if (p.length()==0) montant.setText("0");


        // Si le total afficher est superieur à zero
        if (!total.getText().equals("0")){

            prix = Double.parseDouble(total.getText().toString()) ;
            if (prix>v) prix -= v ;

            montant.setText(String.valueOf(prix));
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
        int prix = 0 ;
        if (tv.getVisibility()!=View.GONE) val = Double.parseDouble(tv.getText().toString());

        String p = montant.getText().toString() ;
        if (p.length()==0) montant.setText("0");

        val++ ;
        prix += v + Double.parseDouble(montant.getText().toString()) ;
        montant.setText(String.valueOf(prix));

        // On met à jour les etiquettes
        tv.setText(String.valueOf(val));
        tv.setVisibility(View.VISIBLE);

        total.setText(String.valueOf(prix));
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


}
