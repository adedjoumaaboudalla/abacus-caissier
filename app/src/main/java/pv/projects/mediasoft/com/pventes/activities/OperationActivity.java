package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zj.btsdk.BluetoothService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.fragment.OperationFragment;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.utiles.PrintPDA;
import pv.projects.mediasoft.com.pventes.utiles.PrinterUtilsold;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class OperationActivity extends AppCompatActivity implements OperationFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener {

    OperationFragment operationFragment1 = null;
    OperationFragment operationFragment2 = null;
    OperationFragment operationFragment3 = null;
    OperationFragment operationFragment4 = null;
    OperationFragment operationFragment5 = null;
    OperationFragment operationFragment6 = null;
    OperationFragment operationFragment7 = null;
    OperationFragment operationFragment8 = null;
    OperationFragment operationFragment9 = null;
    OperationFragment operationFragment10;
    OperationFragment operationFragment = null;
    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH) ;



    private String bluetoothConfig = "imprimenteexterne";
    AlertDialog.Builder dateBox = null ;
    AlertDialog.Builder recapBox = null ;
    Button button = null ;
    private LayoutInflater mInflater= null ;
    private TabLayout tabLayout= null ;
    private ViewPager viewPager= null ;


    TextView depot = null ;
    TextView retrait = null ;
    TextView total = null ;
    TextView nbre = null ;
    TextView si1 = null ;
    TextView si2 = null ;
    final static int PROGRESS_DIALOG_ID = 0 ;

    private ProgressDialog mProgressBar;
    private int MAX_SIZE;
    private CollectionAdapter collectionAdapter;
    private int mPosition = 0 ;
    private AlertDialog alert;
    OperationDAO operationDAO = null ;
    private String dateFin = null;
    private String dateDebut = null ;
    private SharedPreferences preferences;
    private double valeur = 0 ;
    private ArrayList<Operation> payements;
    private BluetoothService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);


        init();
        setupToolbar();
        setupFragments();
        setupTablayout();

        refresh(mPosition) ;
        mService = new BluetoothService(this, mHandler);
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
    protected void onResume() {
        super.onResume();

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



    private void refresh(int position) {
        DatePicker debut = new DatePicker(this) ;
        DatePicker fin = new DatePicker(this) ;
        if (dateDebut==null) dateDebut= DAOBase.formatter2.format(new Date("2015/01/01"));
        if (dateFin==null) dateFin= DAOBase.formatter2.format(new Date());
        ArrayList<Operation> operations = new ArrayList<Operation>() ;
        double mtn = 0 ;
        int nbr = 0 ;

        retrait.setText("0 F");
        depot.setText("0 F");


        if (preferences.getBoolean("stateonoff",false)){
            try {
                operations = operationDAO.getAll(12,DAOBase.formatter2.parse(dateDebut),DAOBase.formatter2.parse(dateFin));
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1 || operations.get(i).getAttente()==1) continue;
                    mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                depot.setText(Utiles.formatMtn(mtn) + " F");

                Log.e("OPERATION SIZE", String.valueOf(operations.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            nbr = operations.size() ;
            nbre.setText(String.valueOf(nbr));
            return;
        }

        if (position==0 || position==1){
            try {
                operations = operationDAO.getAll(12,DAOBase.formatter2.parse(dateDebut),DAOBase.formatter2.parse(dateFin));
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1 || operations.get(i).getAttente()==1) continue;
                    mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                depot.setText(Utiles.formatMtn(mtn) + " F");

                Log.e("OPERATION SIZE", String.valueOf(operations.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (position==0||position==2){
            try {
                operations = operationDAO.getAll(1,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                depot.setText(Utiles.formatMtn(mtn) + " F");


                Log.e("OPERATION SIZE", String.valueOf(operations.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        if (position==0||position==3){
            try {
                operations = operationDAO.getAll(2,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                retrait.setText(Utiles.formatMtn(mtn) + " F");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (position==0||position==4){
            try {
                operations = operationDAO.getAll(3,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                depot.setText(Utiles.formatMtn(mtn) + " F");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        if (position==0||position==5){
            try {
                operations = operationDAO.getAll(4,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                retrait.setText(Utiles.formatMtn(mtn) + " F");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (position==0||position==6){
            try {
                operations = operationDAO.getAll(5,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                double mtn1 = 0;;
                double mtn2 = 0;;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    if (operations.get(i).getEntree()==1)mtn1 += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                    else mtn2 += operations.get(i).getMontant()  - operations.get(i).getRemise();
                }
                depot.setText(Utiles.formatMtn(mtn1) + " F");
                retrait.setText(Utiles.formatMtn(mtn2) + " F");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (position==0||position==7){
            try {
                operations = operationDAO.getAll(11,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                double mtn1 = 0;;
                double mtn2 = 0;;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    if (operations.get(i).getEntree()==1) mtn1 += operations.get(i).getMontant()  - operations.get(i).getRemise();
                    else mtn2 += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                depot.setText(Utiles.formatMtn(mtn1) + " F");
                retrait.setText(Utiles.formatMtn(mtn2) + " F");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (position==0||position==8){
            try {
                operations = operationDAO.getAll(13,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                double mtn1 = 0;;
                double mtn2 = 0;;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    if (operations.get(i).getEntree()==1) mtn1 += operations.get(i).getMontant()  - operations.get(i).getRemise();
                    else mtn2 += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                depot.setText(Utiles.formatMtn(mtn1) + " F");
                retrait.setText(Utiles.formatMtn(mtn2) + " F");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (position==0||position==9){
            try {
                operations = operationDAO.getAll(14,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                double mtn1 = 0;;
                double mtn2 = 0;;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    if (operations.get(i).getEntree()==1) mtn1 += operations.get(i).getMontant()  - operations.get(i).getRemise();
                    else mtn2 += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                depot.setText(Utiles.formatMtn(mtn1) + " F");
                retrait.setText(Utiles.formatMtn(mtn2) + " F");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (position==0||position==10){
            try {
                operations = operationDAO.getAll(15,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
                double mtn1 = 0;;
                double mtn2 = 0;;
                for (int i = 0; i<operations.size();++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    if (operations.get(i).getEntree()==1) mtn1 += operations.get(i).getMontant()  - operations.get(i).getRemise();
                    else mtn2 += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                depot.setText(Utiles.formatMtn(mtn1) + " F");
                retrait.setText(Utiles.formatMtn(mtn2) + " F");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (position==0){
            mtn = 0 ;
            try {
              operations = operationDAO.getAll(0, DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
            } catch (Exception e) {
                e.printStackTrace();
            }

            double mtn1 = 0;
            double mtn2 = 0;
            for (int i = 0; i<operations.size();++i){
                Log.e("MTN",Utiles.formatMtn(mtn1));
                if (operations.get(i).getAnnuler()==1 || operations.get(i).getPayer()==0){
                    Log.e("MTNA",Utiles.formatMtn(mtn1));
                    continue;
                }
                if (operations.get(i).getEntree()==1){
                    Log.e("MTNE",Utiles.formatMtn(mtn1));
                    mtn1 += operations.get(i).getMontant() - operations.get(i).getRemise() ;
                }
                else mtn2 += operations.get(i).getMontant() - operations.get(i).getRemise() ;
            }

            Log.e("MTN",Utiles.formatMtn(mtn1));
            Log.e("MTN", ""+ mtn1);
            Log.e("MTN", ""+ mtn2);
            depot.setText(Utiles.formatMtn(mtn1) + " F");
            retrait.setText(Utiles.formatMtn(mtn2) + " F");
        }

        nbr = operations.size() ;
        nbre.setText(String.valueOf(nbr));
    }


    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(OperationActivity.this) ;
        mInflater = LayoutInflater.from(this);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        depot = (TextView) findViewById(R.id.depot);
        retrait = (TextView) findViewById(R.id.retrait);
        total = (TextView) findViewById(R.id.total);
        nbre = (TextView) findViewById(R.id.nbre);
        operationDAO = new OperationDAO(this) ;

        ArrayList<Operation> operations = operationDAO.getAll() ;
        float solde = 0 ;
        for (int i = 0; i<operations.size();++i){
            if (operations.get(i).getTypeOperation_id().equals(OperationDAO.VENTE)) solde += operations.get(i).getMontant() - operations.get(i).getRemise() ;
            else  solde -= operations.get(i).getMontant() - operations.get(i).getRemise() ;
        }

       // si2.setText(Utiles.formatMtn(solde) + " F");
    }


    private void setupTablayout() {
        collectionAdapter = new CollectionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(collectionAdapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupFragments() {
        final FragmentManager fm = getSupportFragmentManager();
        //this.operationFragment = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment == null) {
            this.operationFragment = OperationFragment.newInstance(null,"-2");
        }
        //this.operationFragment1 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment1 == null) {
            this.operationFragment1 = OperationFragment.newInstance(null,"1");
        }
        //this.operationFragment2 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment2 == null) {
            this.operationFragment2 = OperationFragment.newInstance(null,"2");
        }
        //this.operationFragment2 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment3 == null) {
            this.operationFragment3 = OperationFragment.newInstance(null,"3");
        }
        //this.operationFragment2 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment4 == null) {
            this.operationFragment4 = OperationFragment.newInstance(null,"4");
        }
        //this.operationFragment2 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment5 == null) {
            this.operationFragment5 = OperationFragment.newInstance(null,"5");
        }
        //this.operationFragment2 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment6 == null) {
            this.operationFragment6 = OperationFragment.newInstance(null,"11");
        }
        //this.operationFragment2 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment7 == null) {
            this.operationFragment7 = OperationFragment.newInstance(null,"12");
        }
        if (this.operationFragment8 == null) {
            this.operationFragment8 = OperationFragment.newInstance(null,"13");
        }
        if (this.operationFragment9 == null) {
            this.operationFragment9 = OperationFragment.newInstance(null,"14");
        }
        if (this.operationFragment10 == null) {
            this.operationFragment10 = OperationFragment.newInstance(null,"15");
        }
    }


    private void setupToolbar() {
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
        getMenuInflater().inflate(R.menu.menu_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_interval) {
            dateBox = new AlertDialog.Builder(this);
            ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.dialogbox,null);
            if (((ViewGroup)scrollView.getParent())!=null)((ViewGroup)scrollView.getParent()).removeAllViews();
            dateBox.setView(scrollView);
            dateBox.setTitle(getString(R.string.datechoice));

            final DatePicker debut = (DatePicker) scrollView.findViewById(R.id.dateDebut);
            final DatePicker fin = (DatePicker) scrollView.findViewById(R.id.dateFin);
            button = (Button) scrollView.findViewById(R.id.valider);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dateBox != null) {
                        dateDebut = String.valueOf(debut.getYear()) + "-" + String.valueOf(debut.getMonth() + 1) + "-" + String.valueOf(debut.getDayOfMonth());
                        dateFin = String.valueOf(fin.getYear()) + "-" + String.valueOf(fin.getMonth() + 1) + "-" + String.valueOf(fin.getDayOfMonth());
                        if (mPosition==0)operationFragment.interval(mPosition,dateDebut, dateFin);
                        else if (mPosition==1)operationFragment7.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==2) operationFragment1.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==3)operationFragment2.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==4)operationFragment4.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==5)operationFragment5.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==6)operationFragment3.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==7)operationFragment6.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==8)operationFragment8.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==9)operationFragment9.interval(mPosition,dateDebut,dateFin);
                        else if (mPosition==10)operationFragment10.interval(mPosition,dateDebut,dateFin);
                        else operationFragment.interval(mPosition,dateDebut,dateFin);
                        dateBox = null;
                        alert.dismiss();
                        refresh(mPosition);
                    }
                }
            });
            alert = dateBox.show();
        } else if (id == R.id.action_recap) {
            recapitulatif() ;
        }

        else if (id == R.id.action_imp){
            final CharSequence[] items = { getString(R.string.pdf), getString(R.string.xls), getString(R.string.fermer) };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle(getString(R.string.action));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals(getString(R.string.pdf))) {
                        if (preferences.getBoolean("stateonoff",false)){
                            operationFragment7.imprimePDFDoc(getString(R.string.listeattente));
                            return;
                        }
                        if (mPosition == 0) operationFragment.imprimePDFDoc(getString(R.string.listeop));
                        else if (mPosition == 1)
                            operationFragment7.imprimePDFDoc(getString(R.string.listeattente));
                        else if (mPosition == 2)
                            operationFragment1.imprimePDFDoc(getString(R.string.listedesvt));
                        else if (mPosition == 3)
                            operationFragment2.imprimePDFDoc(getString(R.string.listedesdp));
                        else if (mPosition == 4)
                            operationFragment3.imprimePDFDoc(getString(R.string.listecmdfrn));
                        else if (mPosition == 5)
                            operationFragment4.imprimePDFDoc(getString(R.string.listecmdclt));
                        else if (mPosition == 6)
                            operationFragment5.imprimePDFDoc(getString(R.string.listedesachat));
                        else if (mPosition == 7)
                            operationFragment6.imprimePDFDoc(getString(R.string.listechargeproduit));
                        else if (mPosition == 8)
                            operationFragment8.imprimePDFDoc(getString(R.string.listeplacement));
                        else if (mPosition == 9)
                            operationFragment9.imprimePDFDoc(getString(R.string.listeimmo));
                        else if (mPosition == 10)
                            operationFragment10.imprimePDFDoc(getString(R.string.listeannuler));

                    } else if (items[item].equals(getString(R.string.xls))) {
                        if (preferences.getBoolean("stateonoff",false)) {
                            operationFragment7.imprimeExcelDoc(getString(R.string.listeattente));
                            return;
                        }
                        if (mPosition == 0) operationFragment.imprimeExcelDoc(getString(R.string.listeop));
                        else if (mPosition == 1)
                            operationFragment7.imprimeExcelDoc(getString(R.string.listeattente));
                        else if (mPosition == 2)
                            operationFragment1.imprimeExcelDoc(getString(R.string.listedesvt));
                        else if (mPosition == 3)
                            operationFragment2.imprimeExcelDoc(getString(R.string.listedesdp));
                        else if (mPosition == 4)
                            operationFragment3.imprimeExcelDoc(getString(R.string.listecmdfrn));
                        else if (mPosition == 5)
                            operationFragment4.imprimeExcelDoc(getString(R.string.listecmdclt));
                        else if (mPosition == 6)
                            operationFragment5.imprimeExcelDoc(getString(R.string.listedesachat));
                        else if (mPosition == 7)
                            operationFragment6.imprimeExcelDoc(getString(R.string.listechargeproduit));
                        else if (mPosition == 8)
                            operationFragment8.imprimeExcelDoc(getString(R.string.listeplacement));
                        else if (mPosition == 9)
                            operationFragment9.imprimeExcelDoc(getString(R.string.listeimmo));
                        else if (mPosition == 10)
                            operationFragment10.imprimeExcelDoc(getString(R.string.listeannuler));

                    } else {
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog alertDialog = builder.show();
        }
        else if (id==R.id.action_aide){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
            builder.setTitle(getString(R.string.app_name)) ;
            builder.setView(R.layout.legende) ;

            builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }) ;
            final AlertDialog alertdialog = builder.create();
            alertdialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                    alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
                }
            }) ;
            alertdialog.show();
        }

        return super.onOptionsItemSelected(item);
    }


    private void recapitulatif() {
        recapBox = new AlertDialog.Builder(this);
        ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.recapbox,null);
        if (((ViewGroup)scrollView.getParent())!=null)((ViewGroup)scrollView.getParent()).removeAllViews();
        recapBox.setView(scrollView);

        final TextView nbr1 = (TextView) scrollView.findViewById(R.id.nbr1);
        final TextView nbr2 = (TextView) scrollView.findViewById(R.id.nbr2);
        final TextView nbr3 = (TextView) scrollView.findViewById(R.id.nbr3);
        final TextView nbr4 = (TextView) scrollView.findViewById(R.id.nbr4);
        final TextView nbr5 = (TextView) scrollView.findViewById(R.id.nbr5);
        final TextView nbr6 = (TextView) scrollView.findViewById(R.id.nbr6);
        final TextView nbr7 = (TextView) scrollView.findViewById(R.id.nbr7);

        final TextView mtn1 = (TextView) scrollView.findViewById(R.id.mtn1);
        final TextView mtn2 = (TextView) scrollView.findViewById(R.id.mtn2);
        final TextView mtn3 = (TextView) scrollView.findViewById(R.id.mtn3);
        final TextView mtn4 = (TextView) scrollView.findViewById(R.id.mtn4);
        final TextView mtn5 = (TextView) scrollView.findViewById(R.id.mtn5);
        final TextView mtn6 = (TextView) scrollView.findViewById(R.id.mtn6);
        final TextView mtn7 = (TextView) scrollView.findViewById(R.id.mtn7);

        final TextView interval = (TextView) scrollView.findViewById(R.id.interval);

        DatePicker debut = new DatePicker(this) ;
        DatePicker fin = new DatePicker(this) ;

        if (dateDebut==null)dateDebut = String.valueOf(debut.getYear()) + "-" + String.valueOf(debut.getMonth() + 1) + "-" + String.valueOf(debut.getDayOfMonth());
        if (dateFin==null)dateFin = String.valueOf(fin.getYear()) + "-" + String.valueOf(fin.getMonth() + 1) + "-" + String.valueOf(fin.getDayOfMonth());
        recapBox.setTitle(getString(R.string.recap) + " du " + dateDebut + " au " + dateFin);

        double mtn = 0 ;
        ArrayList<Operation> operations = null;
        try {
            operations = operationDAO.getAll(1, DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));

            for (int i = 0; i<operations.size();++i){
                mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
            }

            nbr1.setText(String.valueOf(operations.size()));
            mtn1.setText(Utiles.formatMtn(mtn) + " F");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            operations = operationDAO.getAll(2,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
            mtn = 0 ;
            for (int i = 0; i<operations.size();++i){
                mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
            }

            nbr2.setText(String.valueOf(operations.size()));
            mtn2.setText(Utiles.formatMtn(mtn) + " F");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            operations = operationDAO.getAll(5,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
            mtn = 0 ;
            for (int i = 0; i<operations.size();++i){
                mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
            }

            nbr3.setText(String.valueOf(operations.size()));
            mtn3.setText(Utiles.formatMtn(mtn) + " F");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            operations = operationDAO.getAll(6,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
            mtn = 0 ;
            for (int i = 0; i<operations.size();++i){
                mtn += operations.get(i).getMontant() - operations.get(i).getRemise() ;
            }

            nbr4.setText(String.valueOf(operations.size()));
            mtn4.setText(Utiles.formatMtn(mtn) + " F");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            operations = operationDAO.getAll(16,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
            mtn = 0 ;
            valeur = 0 ;
            Operation operation = null ;
            double n = 0 ;
            ArrayList<Operation> payements = null ;

            for (int i = 0; i < operations.size(); i++) {
                operation = operations.get(i) ;
                if (operation.getAnnuler()==1) continue;
                if (operation.getPayer() == 0){
                    valeur += operation.getMontant()-operation.getRemise() ;
                    payements = operationDAO.getMany(operation.getId_externe()) ;
                    for (int j = 0; j < payements.size(); j++) {
                        valeur -= payements.get(j).getMontant() ;
                    }
                }
                n++ ;
            }
            mtn = valeur ;
            operations = operationDAO.getAll(17,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));

            valeur = 0 ;

            for (int i = 0; i < operations.size(); i++) {
                operation = operations.get(i) ;
                if (operation.getAnnuler()==1) continue;
                if (operation.getPayer() == 0){
                    valeur += operation.getMontant()-operation.getRemise() ;
                    payements = operationDAO.getMany(operation.getId_externe()) ;
                    for (int j = 0; j < payements.size(); j++) {
                        valeur -= payements.get(j).getMontant() ;
                    }
                }
                n++ ;
            }
            mtn += valeur ;
            nbr5.setText(String.valueOf(n));
            mtn5.setText(Utiles.formatMtn(mtn) + " F");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            operations = operationDAO.getAll(13,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
            mtn = 0 ;
            for (int i = 0; i<operations.size();++i){
                mtn += operations.get(i).getMontant()  - operations.get(i).getRemise();
            }

            nbr6.setText(String.valueOf(operations.size()));
            mtn6.setText(Utiles.formatMtn(mtn) + " F");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            operations = operationDAO.getAll(17,DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
            mtn = 0 ;
            Operation operation = null ;
            valeur = 0 ;

            for (int i = 0; i < operations.size(); i++) {
                operation = operations.get(i) ;
                if (operation.getAnnuler()==1) continue;
                if (operation.getPayer() == 0){
                    valeur += operation.getMontant()-operation.getRemise() ;
                    payements = operationDAO.getMany(operation.getId_externe()) ;
                    for (int j = 0; j < payements.size(); j++) {
                        valeur -= payements.get(j).getMontant() ;
                    }
                }
            }

            nbr7.setText(String.valueOf(operations.size()));
            mtn7.setText(Utiles.formatMtn(mtn) + " F");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        button = (Button) scrollView.findViewById(R.id.close);
        Button impBtn = (Button) scrollView.findViewById(R.id.imp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        impBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean imp = preferences.getBoolean(bluetoothConfig, false) ;
                if (imp){
                    try {
                        PrinterUtilsold printerUtils = new PrinterUtilsold(OperationActivity.this,mService) ;
                        //printerUtils.printTicket(dateDebut,dateFin);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        PrintPDA printPDA = new PrintPDA(OperationActivity.this) ;
                        //printPDA.printTicket(dateDebut,dateFin);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        alert = recapBox.show();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        if (mProgressBar == null) {
            mProgressBar = new ProgressDialog(this);
            mProgressBar.setCancelable(false);
            mProgressBar.setTitle(getString(R.string.send_loding));
            mProgressBar.setMessage(getString(R.string.wait));
            mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressBar.setMax(MAX_SIZE);
        }

        return mProgressBar;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     *                             Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {
        mPosition = position ;
        refresh(mPosition);
        if (mPosition==0)operationFragment.interval(mPosition,dateDebut, dateFin);
        else if (mPosition==1) operationFragment1.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==2)operationFragment2.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==3)operationFragment3.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==4)operationFragment4.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==5)operationFragment5.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==6)operationFragment6.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==7)operationFragment7.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==8)operationFragment8.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==9)operationFragment9.interval(mPosition,dateDebut,dateFin);
        else if (mPosition==10)operationFragment10.interval(mPosition,dateDebut,dateFin);
        else operationFragment4.interval(mPosition,dateDebut,dateFin);
    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see ViewPager#SCROLL_STATE_IDLE
     * @see ViewPager#SCROLL_STATE_DRAGGING
     * @see ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class  CollectionAdapter extends FragmentPagerAdapter {
        int pages = 11 ;

        public CollectionAdapter(FragmentManager fm) {
            super(fm);
            if (preferences.getBoolean("stateonoff",false)) {
                pages = 1 ;
            }
        }

        @Override
        public Fragment getItem(int position) {
            if (preferences.getBoolean("stateonoff",false)) return operationFragment7 ;
            switch (position){
                case 0 : {
                    return operationFragment ;
                }
                case 1 : {
                    return operationFragment7 ;
                }
                case 2 : {
                    return operationFragment1 ;
                }
                case 3 : {
                    return operationFragment2 ;
                }
                case 4 : {
                    return operationFragment3 ;
                }
                case 5 : {
                    return operationFragment4 ;
                }
                case 6 : {
                    return operationFragment5 ;
                }
                case 7 : {
                    return operationFragment6 ;
                }
                case 8 : {
                    return operationFragment8 ;
                }
                case 9 : {
                    return operationFragment9 ;
                }
                case 10 : {
                    return operationFragment10 ;
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
            if (preferences.getBoolean("stateonoff",false))  return getString(R.string.attente) ;
            switch (position){
                case 0 : {
                    return getString(R.string.tout) ;
                }
                case 1 : {
                    return getString(R.string.attente) ;
                }
                case 2 : {
                    return getString(R.string.vente) ;
                }
                case 3 : {
                    return getString(R.string.depense) ;
                }
                case 4 : {
                    return getString(R.string.cmdfr) ;
                }
                case 5 : {
                    return getString(R.string.cmdclt) ;
                }
                case 6 : {
                    return getString(R.string.achat) ;
                }
                case 7 : {
                    return getString(R.string.chargeetproduit) ;
                }
                case 8 : {
                    return getString(R.string.menu_placement) ;
                }
                case 9 : {
                    return getString(R.string.menu_immo) ;
                }
                case 10 : {
                    return getString(R.string.annuler) ;
                }
            }
            return super.getPageTitle(position);
        }
    }


}


