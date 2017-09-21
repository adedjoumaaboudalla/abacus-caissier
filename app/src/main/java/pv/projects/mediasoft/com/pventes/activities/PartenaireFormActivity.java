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
import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.model.PointVente;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class PartenaireFormActivity extends AppCompatActivity{

    private static final String FILEPATH = "filepath";
    public static final String PP = "PP";
    public static final String PM = "PM";
    public static final String CLT = "CLT";
    public static final String FIN = "FIN";
    public static final String FRS = "FRS";
    EditText libelle = null ;
    EditText code = null ;
    EditText unite = null ;
    EditText prixV = null ;
    EditText prixA = null ;
    CheckBox switchCompat = null ;

    Button valider = null ;
    ImageButton canBtn = null ;
    ImageButton venteBtn = null ;
    ImageButton achatBtn = null ;

    boolean checked = false ;
    PartenaireDAO partenaireDAO = null ;
    AddPartenaireTask addPartenaireTask = null ;
    private ProgressDialog mProgressBar;
    final static int PROGRESS_DIALOG_ID = 0 ;
    private int MAX_SIZE;

    
    ImageView imageV = null ;

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private String filePath = null;
    private Partenaire partenaire;
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
    private EditText nom;
    private EditText prenom;
    private EditText raisonsocial;
    private EditText email;
    private RadioGroup sexe;
    private RadioGroup typegroup;
    private EditText contact;
    private EditText adresse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partenaire_form);

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
                partenaire = partenaireDAO.getOne(id);
                if (partenaire != null) {
                    init(partenaire);
                    //if (produit.getImage() != null && !produit.getImage().equals("null") && !produit.getImage().equals("")) loadLocalImage(produit.getImage(),imageV);
                    //imageV.setImageBitmap(Utiles.loadImageFromExternalStorage(this,produit.getImage(),dossier));
                }
                else Toast.makeText(getApplicationContext(),  getString(R.string.echec), Toast.LENGTH_SHORT).show();
            }

            if (intent.getBooleanExtra("CHOICE",false)) typegroup.setVisibility(View.GONE);
        }
    }


    private void initialisation() {
        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        raisonsocial = (EditText) findViewById(R.id.raisonsocial);
        sexe = (RadioGroup) findViewById(R.id.sexegroup);
        typegroup = (RadioGroup) findViewById(R.id.typegroup);
        email = (EditText) findViewById(R.id.email);
        contact = (EditText) findViewById(R.id.contact);
        adresse = (EditText) findViewById(R.id.adresse);
        partenaireDAO = new PartenaireDAO(this) ;


        valider = (Button) findViewById(R.id.valider);
        canBtn = (ImageButton) findViewById(R.id.cam);
        venteBtn = (ImageButton) findViewById(R.id.btnvente);
        achatBtn = (ImageButton) findViewById(R.id.btnachat);
        imageV = (ImageView) findViewById(R.id.image);

        imageV.setImageResource(R.mipmap.ic_product);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPartenaire();
            }
        });



        imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    private void init(Partenaire partenaire) {
        nom.setText(partenaire.getNom());
        prenom.setText(partenaire.getPrenom());
        raisonsocial.setText(partenaire.getRaisonsocial());
        contact.setText(partenaire.getContact());
        adresse.setText(partenaire.getAdresse());
        email.setText(partenaire.getEmail());

        sexe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId!=R.id.pm) {
                    raisonsocial.setVisibility(View.GONE);
                }
                else raisonsocial.setVisibility(View.VISIBLE);
            }
        });

        if (partenaire.getTypepersonne().equals("PM")) sexe.check(R.id.pm);
        else if (partenaire.getSexe().equals("Masculin")) sexe.check(R.id.m);
        else  sexe.check(R.id.f);

        if (partenaire.getTypepartenaire().equals(CLT)) typegroup.check(R.id.clt);
        else if (partenaire.getTypepartenaire().equals(FIN)) typegroup.check(R.id.fin);
        else  typegroup.check(R.id.frs);
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
        getMenuInflater().inflate(R.menu.menu_partenaire_form, menu);
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
                        addPartenaireTask.cancel(true);
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

    private void addPartenaire() {
        if (isValid()){
            showDialog(PROGRESS_DIALOG_ID);
            if (partenaire==null)partenaire = new Partenaire() ;
            RadioButton rb = ((RadioButton)findViewById(sexe.getCheckedRadioButtonId())) ;
            partenaire.setNom(nom.getText().toString());
            PointVente pv = new PointVenteDAO(PartenaireFormActivity.this).getLast() ;
            partenaire.setPointvente_id(pv.getId());
            partenaire.setPrenom(prenom.getText().toString());
            partenaire.setContact(contact.getText().toString());
            partenaire.setRaisonsocial(raisonsocial.getText().toString());
            partenaire.setEmail(email.getText().toString());
            partenaire.setAdresse(adresse.getText().toString());
            partenaire.setSexe(rb.getText().toString());

            if (sexe.getCheckedRadioButtonId()==R.id.pm)partenaire.setTypepersonne(PM);
            else {
                partenaire.setTypepersonne(PP);
            }

            if (typegroup.getCheckedRadioButtonId()==R.id.clt)partenaire.setTypepartenaire(CLT);
            else if (typegroup.getCheckedRadioButtonId()==R.id.fin) partenaire.setTypepartenaire(FIN);
            else partenaire.setTypepartenaire(FRS);
            partenaire.setUtilisateur_id(pv.getUtilisateur());

            addPartenaireTask = new AddPartenaireTask(partenaire) ;
            addPartenaireTask.execute() ;
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValid() {
        if (sexe.getCheckedRadioButtonId()==R.id.pm && raisonsocial.getText().length() <= 1) return false ;
        if (sexe.getCheckedRadioButtonId()==R.id.m && nom.getText().length() <= 0) return false ;
        if (sexe.getCheckedRadioButtonId()==R.id.f && prenom.getText().length() <= 0) return false ;
        if (contact.getText().length() <= 0) return false ;

        return true;
    }

    public class AddPartenaireTask extends AsyncTask<Void, Integer, String> {

        String lib = null ;
        String code = null ;
        String unite = null ;
        String prixVente = null ;
        String prixAchat = null ;
        Partenaire partenaire ;

        public AddPartenaireTask(Partenaire p) {
            partenaire = p ;
        }

        @Override
        protected String doInBackground(Void... params) {
            Date d = new Date() ;

            partenaire.setEtat(0);
            long result = 0 ;

            if (partenaire.getId()==0)  result =  partenaireDAO.add(partenaire) ;
            else   result =  partenaireDAO.update(partenaire) ;

            return String.valueOf(result);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            valider.setEnabled(true);
            partenaire = partenaireDAO.getLast() ;
            if (Long.valueOf(result)>0) {
                if (filePath!=null) {
                    /*partenaire = partenaireDAO.getLast() ;
                    try {
                        Uri path = Utiles.saveImageExternalStorage(photo, PartenaireFormActivity.this, partenaire.getId()+".jpg", Utiles.PV_PARTENAIRE_IMAGE_DIR) ;
                        partenaire.setImage(path.getPath());
                        partenaireDAO.update(partenaire) ;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    */
                }
                if (partenaire.getId_externe()==0)Toast.makeText(getApplicationContext(),getString(R.string.partenaire_success),Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(),getString(R.string.partenaire_update),Toast.LENGTH_LONG).show();

                clean();
            }
            else
                Toast.makeText(getApplicationContext(),getString(R.string.echec_ajout),Toast.LENGTH_LONG).show();
            dismissDialog(PROGRESS_DIALOG_ID);
        }





        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (partenaireDAO==null)partenaireDAO = new PartenaireDAO(getApplicationContext()) ;
        }
    }

    private void clean() {
        nom.setText("");
        prenom.setText("");
        raisonsocial.setText("");
        contact.setText("");
        email.setText("");
        adresse.setText("");
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
            prix += v + Integer.parseInt(prixV.getText().toString()) ;
            prixV.setText(String.valueOf(prix));
        }
        // On met à jour les etiquettes
        tv.setText(String.valueOf(val));
        tv.setVisibility(View.VISIBLE);

        total.setText(String.valueOf(prix));
    }


}
