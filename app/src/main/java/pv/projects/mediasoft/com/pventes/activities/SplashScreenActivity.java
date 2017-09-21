package pv.projects.mediasoft.com.pventes.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import pv.projects.mediasoft.com.pventes.MainActivity;
import pv.projects.mediasoft.com.pventes.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }


    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                long i = System.currentTimeMillis() ;
                while (System.currentTimeMillis() - i < 3000) ;
                fermer() ;
            }
        }).start();

    }




    private void fermer() {
        Log.e("POS","0") ;

        // Demande de Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Cela signifie que la permission à déjà était
                //demandé et l'utilisateur l'a refusé
                //Vous pouvez aussi expliquer à l'utilisateur pourquoi
                //cette permission est nécessaire et la redemander
            } else {
                //Sinon demander la permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST);
            }
            Log.e("POS","1") ;
        }
        else if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {

            } else {
                //Sinon demander la permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST);
            }
            Log.e("POS","2") ;
        }
        else if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)) {

            } else {
                //Sinon demander la permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST);
            }
            Log.e("POS","3") ;
        }


        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class) ;
        startActivityForResult(intent,0);
        finish();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission est garantie
                } else {
                    // La permission est refusée
                }
                Log.e("POS","A") ;
                fermer();
            } ; break ;
            case 0 : {
                Log.e("POS","F") ;
                finish();
            }
        }
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) return true ;
        return super.onKeyDown(keyCode, event);
    }

}
