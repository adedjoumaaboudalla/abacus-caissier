package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.CollationElementIterator;
import java.text.ParseException;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CategorieProduitDAO;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.model.CategorieProduit;
import pv.projects.mediasoft.com.pventes.model.PointVente;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class ProduitFormActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnLongClickListener {

private static final String FILEPATH = "filepath";
    private static final int REQUEST_CATEGORIE = 2;
    public static final String CATEGORIE = "categorie";
    private static final int CHOOSE_CODE_BAR_REQUEST = 3;
    EditText libelle = null ;
    EditText code = null ;
    EditText unite = null ;
    EditText prixV = null ;
    EditText prixA = null ;
    CheckBox switchCompat = null ;

    Button valider = null ;
    ImageButton canBtn = null ;
    Button venteBtn = null ;
    Button achatBtn = null ;

    boolean checked = false ;
    ProduitDAO produitDAO = null ;
    AddProduitTask addProduitTask = null ;
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

    ImageView imageV = null ;

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private String filePath = null;
    private Produit produit;
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



    CategorieProduitDAO categorieProduitDAO = null ;
    CategorieProduit categorieProduit = null ;
    private ImageButton catBtn;
    private ImageButton btncam;
    private EditText catEditText;
    private RadioGroup radioGroup;
    private DeviseDAO deviseDAO;
    private String libeler = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produit_form);

        setupToolbar() ;
        initialisation() ;

        if (savedInstanceState!=null) {
            filePath = savedInstanceState.getString(FILEPATH) ;
            if (filePath!=null)outPutFile = new File(filePath) ;
            processImage();
        }

        Intent intent = null ;
        intent = getIntent() ;
        if (intent!=null){
            long id = intent.getLongExtra("ID", 0) ;
            //int type = intent.getIntExtra("TYPE",0) ;
            dossier = Utiles.PV_PRODUIT_IMAGE_DIR ;
            //if (type==0) dossier = Utiles.FLUFF_PHOTO_PROFILS ;
            Log.e("DEBUG", String.valueOf(id)) ;
            if (id != 0){
                produit = produitDAO.getOne(id);
                if (produit != null) {
                    init(produit);
                    //if (produit.getImage() != null && !produit.getImage().equals("null") && !produit.getImage().equals("")) loadLocalImage(produit.getImage(),imageV);
                    //imageV.setImageBitmap(Utiles.loadImageFromExternalStorage(this,produit.getImage(),dossier));
                }
                else Toast.makeText(getApplicationContext(),  getString(R.string.echec), Toast.LENGTH_SHORT).show();
            }
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
                if (achat) {
                    total.setText("0");
                    prixA.setText("0");
                }
                else {
                    total.setText("0");
                    prixV.setText("0");
                }
                total.setText("0");
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel() ;
            }
        });

        String p = prixA.getText().toString() ;
        if (p.length()==0) prixA.setText("0");
        p = prixV.getText().toString() ;
        if (p.length()==0) prixV.setText("0");

        if (achat) total.setText(prixA.getText().toString());
        else total.setText(prixV.getText().toString());

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
        libelle = (EditText) findViewById(R.id.libelle);
        unite = (EditText) findViewById(R.id.uniteTV);
        code = (EditText) findViewById(R.id.codeTV);
        prixA = (EditText) findViewById(R.id.prixATV);
        prixV = (EditText) findViewById(R.id.prixV);
        catBtn = (ImageButton) findViewById(R.id.btncat);
        btncam = (ImageButton) findViewById(R.id.btncam);
        switchCompat = (CheckBox) findViewById(R.id.modifiable);
        switchCompat.setChecked(false);
        catEditText = (EditText) findViewById(R.id.categorie);
        produitDAO = new ProduitDAO(this) ;
        categorieProduitDAO = new CategorieProduitDAO(this) ;

        switchCompat.setOnCheckedChangeListener(this);

        valider = (Button) findViewById(R.id.valider);
        canBtn = (ImageButton) findViewById(R.id.cam);
        venteBtn = (Button) findViewById(R.id.btnvente);
        achatBtn = (Button) findViewById(R.id.btnachat);
        imageV = (ImageView) findViewById(R.id.image);

        imageV.setImageResource(R.mipmap.ic_product);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduit();
            }
        });
        deviseDAO = new DeviseDAO(this) ;
        if (deviseDAO.getReference()!=null) venteBtn.setText(deviseDAO.getReference().getCodedevise());
        if (deviseDAO.getReference()!=null) achatBtn.setText(deviseDAO.getReference().getCodedevise());

        venteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money_init() ;
                achat = false ;
                builder = new AlertDialog.Builder(ProduitFormActivity.this) ;
                builder.setView(rv) ;
                alert = builder.show() ;
            }
        });

        achatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                achat = true ;
                money_init() ;
                builder = new AlertDialog.Builder(ProduitFormActivity.this) ;
                builder.setView(rv) ;
                alert = builder.show() ;

            }
        });


        catBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProduitFormActivity.this,CategorieProduitActivity.class) ;
                startActivityForResult(intent,REQUEST_CATEGORIE);
            }
        });


        btncam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProduitFormActivity.this, ScannerActivity.class);
                startActivityForResult(intent, CHOOSE_CODE_BAR_REQUEST);
            }
        });


        canBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageOption();
            }
        });

        imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        radioGroup = (RadioGroup) findViewById(R.id.typegroup);
    }


    private void init(Produit produit) {
        libeler = produit.getLibelle() ;
        libelle.setText(produit.getLibelle());
        code.setText(produit.getCode());
        unite.setText(produit.getUnite());
        prixA.setText(String.valueOf(produit.getPrixA()));
        prixV.setText(String.valueOf(produit.getPrixV()));
        categorieProduit = categorieProduitDAO.getOne(produit.getCategorie_id()) ;
        catEditText.setText(categorieProduit.getLibelle());
        if (produit!= null && produit.getModifiable()==1) switchCompat.setChecked(true);
        if (produit!= null && produit.getModifiable()==0) switchCompat.setChecked(false);

        if (produit.getAffichable().equals(ProduitDAO.VENTE)) ((RadioButton)findViewById(R.id.vente)).setChecked(true);
        if (produit.getAffichable().equals(ProduitDAO.ACHAT_VENTE)) ((RadioButton)findViewById(R.id.achat_vente)).setChecked(true);
        if (produit.getAffichable().equals(ProduitDAO.ACHAT)) ((RadioButton)findViewById(R.id.achat)).setChecked(true);

        btncam.setEnabled(false);
        code.setEnabled(false);
        libelle.setEnabled(false);
    }




    private void selectImageOption() {
        final CharSequence[] items = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.delete), getString(R.string.annuler)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.addfoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.camera))) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    mImageCaptureUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals(getString(R.string.gallery))) {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                }else if (items[item].equals(getString(R.string.delete))) {

                    if(filePath!=null){
                        filePath = null;
                        imageV.setImageDrawable(null);
                    }


                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != imageReturnedIntent) {

            Uri selectedImage = imageReturnedIntent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();

            outPutFile = new File(filePath) ;
            Log.e("PATH", filePath) ;

            //Bitmap img = BitmapFactory.decodeFile(filePath);
            //img = Utiles.getResizedBitmap(img, 750, 1200);
            //imageView.setImageBitmap(img);

            processImage();

        } else if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {

            Log.e("Camera Image URI : ", String.valueOf(mImageCaptureUri));
            //cropingIMG();
            outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
            processImage();
        } else if (requestCode == CROPING_CODE  && resultCode == RESULT_OK) {

            try {
                if(outPutFile.exists()){
                    photo = Utiles.saveScaledPhotoToFile(Utiles.decodeFile(outPutFile));
                    filePath = outPutFile.getAbsolutePath();
                    Log.e("PATH", filePath) ;
                    imageV.setImageBitmap(photo);
                }
                else {
                    Toast.makeText(getApplicationContext(),  getString(R.string.echec), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_CATEGORIE  && resultCode == RESULT_OK) {
            long id = imageReturnedIntent.getLongExtra(CATEGORIE,0) ;
            if (id>0){
                if (categorieProduitDAO == null) categorieProduitDAO = new CategorieProduitDAO(this) ;
                categorieProduit = categorieProduitDAO.getOne(id) ;
                if (categorieProduit!=null) catEditText.setText(categorieProduit.getLibelle());
            }
        }
        else if (requestCode == CHOOSE_CODE_BAR_REQUEST  && resultCode == RESULT_OK) {
            if (imageReturnedIntent==null) return;
            String cobare = imageReturnedIntent.getStringExtra(AccueilActivity.CODEBAR);
            Log.e("Nocompte", cobare) ;
            if (cobare!=null) {
                code.setText(cobare);
            }
            else Toast.makeText(ProduitFormActivity.this, R.string.echeclecture, Toast.LENGTH_SHORT).show();
        }
    }



    public void processImage(){
        try {
            if(outPutFile.exists()){
                photo = Utiles.saveScaledPhotoToFile(Utiles.decodeFile(outPutFile));
                filePath = outPutFile.getAbsolutePath();
                Log.e("PATH", filePath) ;
                imageV.setImageBitmap(photo);
            }
            else {
                Toast.makeText(getApplicationContext(),  getString(R.string.echec), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        getMenuInflater().inflate(R.menu.menu_produit_form, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(FILEPATH, filePath);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FILEPATH,filePath);
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
                        addProduitTask.cancel(true);
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

    private void addProduit() {
        if (isValid()){
            showDialog(PROGRESS_DIALOG_ID);
            String aff = produitDAO.ACHAT_VENTE ;
            if (radioGroup.getCheckedRadioButtonId()==R.id.vente) aff = produitDAO.VENTE ;
            if (radioGroup.getCheckedRadioButtonId()==R.id.achat) aff = produitDAO.ACHAT ;
            addProduitTask = new AddProduitTask(unite.getText().toString(),code.getText().toString(),libelle.getText().toString(),prixV.getText().toString(),prixA.getText().toString(),categorieProduit.getId(),aff) ;
            addProduitTask.execute() ;
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValid() {
        if (libelle.getText().length() <= 1) return false ;
        if (prixV.getText().length() <= 0) return false ;
        if (prixA.getText().length() <= 0) return false ;

        if ( code.getText().toString().length()>0 && produitDAO.getOneByCode(code.getText().toString())!=null && produit.getId()==0) {
            Toast.makeText(this, R.string.code_existant, Toast.LENGTH_SHORT).show();
            return false ;
        }

        if (produitDAO.getOneByLibelle(libelle.getText().toString())!=null && produit.getId()==0) {
            Toast.makeText(this, R.string.produit_existant, Toast.LENGTH_SHORT).show();
            return false ;
        }

        if (!libeler.equals(produit.getLibelle()) && produit.getId()!=0){
            if (produitDAO.getOneByLibelle(libelle.getText().toString())!=null) {
                Toast.makeText(this, R.string.produit_existant, Toast.LENGTH_SHORT).show();
                return false ;
            }
        }

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checked=isChecked ;
    }



    public class AddProduitTask extends AsyncTask<Void, Integer, String> {

        String lib = null ;
        String code = null ;
        String unite = null ;
        String prixVente = null ;
        String prixAchat = null ;
        long cat = 1 ;
        String affichable ;

        public AddProduitTask(String unit,String cod,String libelle,String prixV,String priA,long id,String aff) {
            lib = libelle ;
            code = cod ;
            unite = unit ;
            prixVente = prixV ;
            prixAchat = priA ;
            cat = id ;
            affichable = aff ;
        }

        @Override
        protected String doInBackground(Void... params) {
            Date d = new Date() ;
            if (produit==null) produit = new Produit() ;
            produit.setLibelle(lib);
            PointVente pv = new PointVenteDAO(ProduitFormActivity.this).getLast() ;
            if (checked) produit.setModifiable(1);
            else produit.setModifiable(0);
            produit.setPrixV(Double.valueOf(prixVente));
            produit.setPrixA(Double.valueOf(prixAchat));
            SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            produit.setCode(code);
            produit.setUnite(unite);
            produit.setEtat(0);
            produit.setCategorie_id(cat);
            produit.setUtilisateur(pv.getUtilisateur());
            produit.setAffichable(affichable);

            // Important A revoir
            //if (preferences.getLong(LicenceActivity.CAISSE_ID,-1)==-1 || preferences.getLong(LicenceActivity.POINT_VENTE_ID,-1)==-1 || preferences.getLong(LicenceActivity.USER_ID,-1)==-1) return false ;

            long result =  0 ;
            if (produit.getId()==0)result = produitDAO.add(produit) ;
            else result = produitDAO.update(produit) ;

            return String.valueOf(result);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            valider.setEnabled(true);
            if (Long.valueOf(result)>0) {
                if (filePath!=null) {
                    if (produit.getId()==0) produit = produitDAO.getLast() ;
                    try {
                        Uri path = Utiles.saveImageExternalStorage(photo, ProduitFormActivity.this, produit.getId()+".jpg", Utiles.PV_PRODUIT_IMAGE_TMP_DIR) ;
                        produit.setImage(path.getPath());
                        produitDAO.update(produit) ;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (produit.getId_externe()==0)Toast.makeText(getApplicationContext(),getString(R.string.produit_success),Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(),getString(R.string.produit_update),Toast.LENGTH_LONG).show();
                //produit.setId_externe(produit.getId());
                //produitDAO.update(produit) ;
                clean();
            }
            else
                Toast.makeText(getApplicationContext(),getString(R.string.echec_ajout),Toast.LENGTH_LONG).show();
            dismissDialog(PROGRESS_DIALOG_ID);
        }





        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (produitDAO==null)produitDAO = new ProduitDAO(getApplicationContext()) ;
        }
    }

    private void clean() {
        libelle.setText("");
        prixV.setText("0");
        prixA.setText("0");
        unite.setText("");
        code.setText("");
        produit = null ;
    }


    public void diminuer(int v, TextView tv){
        double val = 0 ;
        double prix = 0 ;
        if (tv.getVisibility()!=View.GONE) val = Double.parseDouble(tv.getText().toString());
        String p = prixA.getText().toString() ;
        if (p.length()==0) prixA.setText("0");
        p = prixV.getText().toString() ;
        if (p.length()==0) prixV.setText("0");

        // Si le total afficher est superieur à zero
        if (!total.getText().equals("0")){

            prix = Double.parseDouble(total.getText().toString()) ;
            if (prix>v) prix -= v ;

            // Si c'est achat donc on rempli la case prix achat
            if (achat){
                prixA.setText(String.valueOf(prix));
            }
            // Sinon on rempli la case prix vente
            else {
                prixV.setText(String.valueOf(prix));
            }
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

        String p = prixA.getText().toString() ;
        if (p.length()==0) prixA.setText("0");
        p = prixV.getText().toString() ;
        if (p.length()==0) prixV.setText("0");
        val++ ;
        // Si c'est achat donc on rempli la case prix achat
        if (achat){
            prix += v + Double.parseDouble(prixA.getText().toString()) ;
            prixA.setText(String.valueOf(prix));
        }
        // Sinon on rempli la case prix vente
        else {
            prix += v + Double.parseDouble(prixV.getText().toString()) ;
            prixV.setText(String.valueOf(prix));
        }
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
