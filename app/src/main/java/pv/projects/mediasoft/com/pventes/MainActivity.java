package pv.projects.mediasoft.com.pventes;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pv.projects.mediasoft.com.pventes.activities.AccueilActivity;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;

public class MainActivity extends AppCompatActivity {


    private static final String AUTHORITY_OPERATION = "pv.projects.mediasoft.com.pventes.Operation";
    private static final String AUTHORITY_ATTENTE = "pv.projects.mediasoft.com.pventes.attente";
    private static final int MINUTE = 60;
    public static String ACCOUNT_TYPE = "";
    Account mAccount;
    // The account name
    public static final String ACCOUNT = "abacus account";
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    private CaisseDAO caisseDAO = null ;
    private ContentResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText passwordEdit = (EditText) findViewById(R.id.password);
        final TextView indic = (TextView) findViewById(R.id.indice);


        caisseDAO = new CaisseDAO(MainActivity.this) ;
        lanceSyncAdapter(this);

        Button valider = (Button) findViewById(R.id.valider);
        Button quitter = (Button) findViewById(R.id.annuler);
        final SharedPreferences preferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);

        if (!preferences.getBoolean("abacusverrouiller",false)){
            open() ;
        }

        if (valider != null) {
            valider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (passwordEdit.getText().toString().equals("")) return;
                    if (passwordEdit.getText().toString().equals(preferences.getString("password",""))){
                        open();
                    }
                    else {
                        indic.setVisibility(View.VISIBLE);
                        indic.setText(getString(R.string.indicetext) + preferences.getString("indicateur",""));
                        Snackbar.make(findViewById(R.id.drLayout), R.string.passincorrect , Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }

        if (quitter!=null)quitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void open() {
        Intent intent = new Intent(MainActivity.this, AccueilActivity.class) ;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }



    private void lanceSyncAdapter(Context context) {

        if (caisseDAO.getFirst()==null) return;
        ACCOUNT_TYPE = context.getString(R.string.accounttype) ;
        // Get the content resolver for your app
        mResolver = context.getContentResolver();
        /*
         * Turn on periodic syncing
         */

        mAccount = CreateSyncAccount(context);
        // Time en second



        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Log.e("ABACUS SYNC","ADD") ;
        ContentResolver.setSyncAutomatically(mAccount,AUTHORITY_OPERATION, true);
        ContentResolver.addPeriodicSync(mAccount,AUTHORITY_OPERATION, Bundle.EMPTY, SYNC_INTERVAL);

        Log.e("ABACUS SYNC","ADD") ;
        ContentResolver.setSyncAutomatically(mAccount,AUTHORITY_ATTENTE, true);
        ContentResolver.addPeriodicSync(mAccount,AUTHORITY_ATTENTE, Bundle.EMPTY, SECONDS_PER_MINUTE * 2);

        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        Log.e("ABACUS SYNC","LANCER") ;
        ContentResolver.requestSync(mAccount, AUTHORITY_OPERATION, settingsBundle);
        ContentResolver.requestSync(mAccount, AUTHORITY_ATTENTE, settingsBundle);
    }



    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(context.ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }

        return newAccount ;
    }


}
