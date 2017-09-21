package pv.projects.mediasoft.com.pventes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;

import pv.projects.mediasoft.com.pventes.DeviceListActivity;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;
import pv.projects.mediasoft.com.pventes.model.Caisse;
import pv.projects.mediasoft.com.pventes.utiles.FileChooser;

public class ParametreActivity extends AppCompatActivity {

    public static final String LOGO = "logo";
    private static final int GALLERY_CODE = 2;
    private SearchView mSearchView;
    ListView lv = null;
    private LayoutInflater mInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre);

        setupToolbar();
        lv = (ListView) findViewById(R.id.list);

        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(this);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Abacus/logo.bmp";
        path = preferences.getString(LOGO,path) ;

        String[] image = path.split("/") ;

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,new String[]{getString(R.string.comptebanque),getString(R.string.produit), getString(R.string.partenaire), getString(R.string.commercials) , getString(R.string.changecmt), getString(R.string.devise), getString(R.string.changepasseadmin), getString(R.string.choisirlogo) + " ("+ image[image.length-1] + ")", getString(R.string.connexionble), getString(R.string.reglege), getString(R.string.aide), getString(R.string.licence), getString(R.string.help)}) ;
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 : {
                        security(3) ;
                    }
                    break;
                    case 1 : {
                        security(0) ;
                    }
                    break;
                    case 2 : {
                        security(1) ;
                    }
                    break;
                    case 3 : {
                        security(2) ;
                    }
                    break;
                    case 4 : {
                        Intent intent = new Intent(ParametreActivity.this,ConnexionActivity.class) ;
                        startActivity(intent);
                    }
                    break;
                    case 5 : {
                        security(4) ;
                    }
                    break;
                    case 6 : {
                        changePassword() ;
                    }
                    break;
                    case 7 : {
                        chooseLogo() ;
                    } ; break;
                    case 8 : {
                        Intent intent = new Intent(ParametreActivity.this, DeviceListActivity.class) ;
                        startActivity(intent);
                    } ; break;
                    case 9 : {
                        Intent intent = new Intent(ParametreActivity.this,ReglageActivity.class) ;
                        startActivity(intent);
                    }
                    break;
                }
            }
        });
    }




    public void chooseLogo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            File logo = new File(filePath) ;
            if (logo.exists()){
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ParametreActivity.this) ;
                SharedPreferences.Editor edit = preferences.edit() ;
                edit.putString(LOGO,filePath) ;
                edit.commit() ;
            }
        }
    }

    private boolean security(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.security,null);

        final EditText password = (EditText) scrollView.findViewById(R.id.password);
        Button valider = (Button) scrollView.findViewById(R.id.valider);
        Button annuler = (Button) scrollView.findViewById(R.id.annuler);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this) ;

        CaisseDAO caissierDAO = new CaisseDAO(this);
        final Caisse caisse = caissierDAO.getFirst() ;

        builder.setTitle(R.string.app_name) ;
        builder.setView(scrollView) ;
        final AlertDialog alert = builder.show();;

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals(preferences.getString("adminpass","admin"))) {
                    switch (pos){
                        case 0 : {
                            Intent intent = null;
                            intent = new Intent(ParametreActivity.this, ProduitActivity.class);
                            alert.dismiss();
                            startActivity(intent);
                        } break;
                        case 1 : {
                            Intent intent = null;
                            intent = new Intent(ParametreActivity.this, PartenaireActivity.class);
                            alert.dismiss();
                            startActivity(intent);
                        } break;
                        case 2 : {
                            Intent intent = null;
                            intent = new Intent(ParametreActivity.this, CommercialActivity.class);
                            alert.dismiss();
                            startActivity(intent);
                        } break;
                        case 3 : {
                            Intent intent = null;
                            intent = new Intent(ParametreActivity.this, CompteBanqueActivity.class);
                            alert.dismiss();
                            startActivity(intent);
                        } break;
                        case 4 : {
                            Intent intent = null;
                            intent = new Intent(ParametreActivity.this, DeviseActivity.class);
                            alert.dismiss();
                            startActivity(intent);
                        } break;
                    }
                }
                else
                    Toast.makeText(ParametreActivity.this, R.string.passincorrect, Toast.LENGTH_SHORT).show();
            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        builder.setTitle(R.string.app_name) ;
        return false;
    }


    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.changepass,null);

        final EditText password = (EditText) scrollView.findViewById(R.id.password);
        final EditText password1 = (EditText) scrollView.findViewById(R.id.password1);
        final EditText confirmed = (EditText) scrollView.findViewById(R.id.confirmed);
        Button valider = (Button) scrollView.findViewById(R.id.valider);
        Button annuler = (Button) scrollView.findViewById(R.id.annuler);


        final SharedPreferences preferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);

        builder.setTitle(R.string.app_name) ;
        builder.setView(scrollView) ;
        final AlertDialog alert = builder.show();;

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals(preferences.getString("adminpass","admin"))) {
                    if (password1.getText().toString().equals(confirmed.getText().toString())){
                        if (confirmed.getText().toString().length()>=6) {
                            SharedPreferences.Editor editor = preferences.edit() ;
                            editor.putString("adminpass",password1.getText().toString()) ;
                            editor.commit() ;
                            Toast.makeText(ParametreActivity.this, R.string.passwordmodifier, Toast.LENGTH_SHORT).show();
                            alert.dismiss();
                        }
                        else Toast.makeText(ParametreActivity.this, R.string.tropcourt, Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(ParametreActivity.this, R.string.passpareil, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(ParametreActivity.this, R.string.passincorrect, Toast.LENGTH_SHORT).show();
            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        builder.setTitle(R.string.app_name) ;
    }


    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_back);
        ab.setDisplayHomeAsUpEnabled(true);
    }


}
