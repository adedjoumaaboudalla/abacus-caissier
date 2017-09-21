package pv.projects.mediasoft.com.pventes.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.btsdk.BluetoothService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.BilletDAO;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.model.Billet;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.utiles.PrintP800;
import pv.projects.mediasoft.com.pventes.utiles.PrintPDA;
import pv.projects.mediasoft.com.pventes.utiles.PrinterUtilsold;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class BuillitageActivity extends AppCompatActivity {
    LinearLayout parent = null ;
    LayoutInflater inflater = null ;
    BilletDAO billetDAO = null ;

    ArrayList<Billet> billets ;

    TextView vente = null ;
    TextView depense = null ;
    TextView entee = null ;
    TextView billetage = null ;
    private String societeNom = null;
    private String societeAdresse = null;

    OperationDAO operationDAO = null ;
    private double recetteTotal = 0;
    private SharedPreferences preferences;
    private ArrayList<Operation> operations;
    private String dateDebut;
    private String dateFin;
    private double mtn;
    private BluetoothService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buillitage);
        setupToolbar();


        parent = (LinearLayout) findViewById(R.id.parent);
        inflater = LayoutInflater.from(this) ;
        billetDAO = new BilletDAO(this) ;
        operationDAO = new OperationDAO(this) ;

        billetage = (TextView) findViewById(R.id.billetageTotal);
        vente = (TextView) findViewById(R.id.vente);
        depense = (TextView) findViewById(R.id.depense);
        entee = (TextView) findViewById(R.id.entre);
        preferences = PreferenceManager.getDefaultSharedPreferences(this) ;


        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(this);

        societeNom = preferences.getString("nomSociete", "INCONNUE") ;
        societeAdresse = preferences.getString("adresse", "INCONNUE") ;

        if (dateDebut==null) dateDebut= DAOBase.formatterj.format(new Date("01/01/2015"));
        if (dateFin==null) dateFin= DAOBase.formatterj.format(new Date());

        try {
            operations = operationDAO.getAll(1, DAOBase.formatterj.parse(dateDebut),DAOBase.formatterj.parse(dateFin));
            for (int i = 0; i<operations.size();++i){
                if (operations.get(i).getAnnuler()==1 || operations.get(i).getAttente()==1) continue;
                mtn += operations.get(i).getMontant() ;
            }
            Log.e("OPERATION SIZE", String.valueOf(operations.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        vente.setText(Utiles.formatMtn(mtn));




        try {
            operations = operationDAO.getAll(2,DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));
            mtn = 0 ;
            for (int i = 0; i<operations.size();++i){
                if (operations.get(i).getAnnuler()==1) continue;
                mtn += operations.get(i).getMontant() ;
            }
            Log.e("OPERATION SIZE", String.valueOf(operations.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        depense.setText(Utiles.formatMtn(mtn));


        try {
            operations = operationDAO.getAll(11,DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));
            mtn = 0 ;
            for (int i = 0; i<operations.size();++i){
                if (operations.get(i).getAnnuler()==1) continue;
                mtn += operations.get(i).getMontant() ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        entee.setText(Utiles.formatMtn(mtn));

        //recetteTotal = venteDAO.getTotal() + diversDAO.getTotal() + depenseDAO.getTotal() ;

        //setTitle("Total: " + String.valueOf(recetteTotal));

        recetteTotal = Utiles.getSolde(BuillitageActivity.this) ;
        vente.setText(String.valueOf(recetteTotal));
        billetage.setText("0");
        billetage() ;


        mService = new BluetoothService(this, mHandler);
    }

    private void billetage() {
        billets = billetDAO.getAll();

        for (int i = 0; i<billets.size(); ++i){
            LinearLayout billetageItem = (LinearLayout) inflater.inflate(R.layout.billetageitem, null, false);
            TextView billet = (TextView) billetageItem.getChildAt(0);
            TextView nbre = (TextView) billetageItem.getChildAt(1);
            TextView resultat = (TextView) billetageItem.getChildAt(2);

            nbre.setText("0");
            resultat.setText("0");
            billet.setText(billets.get(i).getLibelle());
            parent.addView(billetageItem);

            nbre.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    lancerBilletage(false) ;
                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        //À¶ÑÀÎ´´ò¿ª£¬´ò¿ªÀ¶ÑÀ
        if( mService.isBTopen() == false)
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 112);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) mService.stop();
        mService = null;
    }


    /**
     * ´´½¨Ò»¸öHandlerÊµÀý£¬ÓÃÓÚ½ÓÊÕBluetoothServiceÀà·µ»Ø»ØÀ´µÄÏûÏ¢
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //ÒÑÁ¬½Ó
                            //Toast.makeText(context, "Connect successful",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_CONNECTING:  //ÕýÔÚÁ¬½Ó
                            Log.d("À¶ÑÀµ÷ÊÔ","ÕýÔÚÁ¬½Ó.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //¼àÌýÁ¬½ÓµÄµ½À´
                        case BluetoothService.STATE_NONE:
                            Log.d("À¶ÑÀµ÷ÊÔ","µÈ´ýÁ¬½Ó.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //À¶ÑÀÒÑ¶Ï¿ªÁ¬½Ó
                    //Toast.makeText(getApplicationContext(), "Device connection was lost",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸
                    //Toast.makeText(getApplicationContext(), "Unable to connect device",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buillitage, menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_valid) {
            lancerBilletage(true) ;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void lancerBilletage(boolean valider) {
        int total = 0 ;
        String msg = "" ;
        Calendar cal = Calendar.getInstance() ;


        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(this);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();

        msg = "##############################";
        msg+= "\n";
        msg +=  "           BILLETAGE        " ;
        msg+= "\n";
        msg += "*******************************";
        msg+= "\n";
        msg +=  societeNom ;
        msg+= "\n";
        msg +=  societeAdresse ;
        msg+= "\n";
        msg += "################################";
        msg+= "\n";
        msg+= "Date      : "+ PrinterUtilsold.formatter.format(new Date());
        msg+= "\n";
        msg += "*******************************";
        msg+= "\n";

        if (billets !=null){
            for (int i = 0; i<billets.size(); ++i){
                LinearLayout billetageItem = (LinearLayout) parent.getChildAt(i);
                TextView nbre = (TextView) billetageItem.getChildAt(1);
                TextView resultat = (TextView) billetageItem.getChildAt(2);


                resultat.setTextColor(getResources().getColor(R.color.my_divider));
                double r = 0 ;

                if (nbre.getText().toString().length()!=0) r = Integer.valueOf(nbre.getText().toString()) * billets.get(i).getMontant() ;
                if (r>0) {
                    total += r ;
                    resultat.setTextColor(getResources().getColor(R.color.my_primary_dark));
                    if (Integer.valueOf(nbre.getText().toString())>1){
                        String s = new StringBuilder(billets.get(i).getLibelle()).insert(billets.get(i).getLibelle().indexOf("de") - 1,"s").toString() ;
                        msg +=   nbre.getText().toString()  + " " +  s   + " FCFA" ;
                    }
                    else msg +=   nbre.getText().toString()  + " " +   billets.get(i).getLibelle() ;
                    msg+= "\n";

                }
                resultat.setText(String.valueOf(r));
            }
            billetage.setText(String.valueOf(total));
            msg+= "\n";
            msg +=  "Recette totale : " + String.valueOf(recetteTotal)   + " FCFA" ;
            msg+= "\n";
        }
        CaisseDAO caisseDAO = new CaisseDAO(BuillitageActivity.this) ;

        if (total == recetteTotal) {
            msg += "*******************************";
            msg+= "\n";
            msg+= "Caisse   : "+ caisseDAO.getFirst().getCode() ;
            msg+= "\n";
            msg += "*******************************";
            msg += "\n";
            msg += "Xtela+   Copyright Media Soft";
            msg += "\n";
            msg += "################################";
            msg+= "\n";
            msg+= "\n";
            msg+= "\n";
            imprimeTicket(msg);
        }
        else{
            if (valider)Toast.makeText(getApplicationContext(),getString(R.string.billetage_incorect),Toast.LENGTH_LONG).show();
        }
    }


    private void imprimeTicket(final String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.impr_confirm));
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                try {
                    if (preferences.getBoolean("imprimenteexterne",false)) {
                        PrinterUtilsold printerUtils = new PrinterUtilsold(BuillitageActivity.this,mService) ;
                        Log.e("Builletage", msg) ;
                        printerUtils.print(msg);
                    }
                    else{
                        PrintPDA printPDA = new PrintPDA(BuillitageActivity.this) ;
                        printPDA.printTicket(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    if (!preferences.getBoolean("imprimenteexterne",false)){
                        PrintP800 printP800 = new PrintP800(BuillitageActivity.this) ;
                        printP800.printTicket(msg);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                } catch (UnsatisfiedLinkError e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), getString(R.string.imp), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


}
