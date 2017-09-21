package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.fragment.CommandeFragment;
import pv.projects.mediasoft.com.pventes.fragment.VenteplusFormFragment;
import pv.projects.mediasoft.com.pventes.model.Produit;

public class VenteActivity extends AppCompatActivity implements VenteplusFormFragment.OnFragmentInteractionListener {

    private static final int CHOOSE_PRODUIT_REQUEST = 1;
    private Toolbar mToolbar;
    private VenteplusFormFragment venteplusFormFragment;
    public static final String PRODUIT = "produit";
    public static final String VENTE = "Vente";
    public final static int PROGRESS_DIALOG_ID = 0 ;
    private ProgressDialog mProgressBar;
    private int MAX_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vente);
        setupToolBar();
        setupFragments();
    }


    private void setupToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_back);
        ab.setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vente_plus_form, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            venteplusFormFragment.sendIntent() ;
            return true;
        }
        if (id == R.id.action_racourci) {
            venteplusFormFragment.showRacourci() ;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void setupFragments() {
        final FragmentManager fm = getSupportFragmentManager();

            Intent intent = getIntent();

            if(intent != null) {
                venteplusFormFragment  = VenteplusFormFragment.newInstance(VENTE, null);
                showFragment(venteplusFormFragment);
            }
    }




    private void showFragment(final Fragment fragment) {
        if (fragment == null)         return;
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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




}
