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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

public class EmpruntPayementActivity extends AppCompatActivity implements  OperationFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener {

    OperationFragment operationFragment1 = null;
    OperationFragment payementFragment = null;

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
    public final static int PROGRESS_DIALOG_ID = 0 ;

    private ProgressDialog mProgressBar;
    private int MAX_SIZE;
    private CollectionAdapter collectionAdapter;
    private int mPosition = 0 ;
    private AlertDialog alert;
    OperationDAO operationDAO = null ;
    private String dateFin = null;
    private String dateDebut = null ;
    private SharedPreferences preferences;
    private SearchView mSearchView;
    private BluetoothService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dette_payement);


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
        if (dateDebut==null) dateDebut= DAOBase.formatter2.format(new Date("01/01/2015"));
        if (dateFin==null) dateFin= DAOBase.formatter2.format(new Date());
        ArrayList<Operation> operations = new ArrayList<Operation>() ;
        double mtn = 0 ;
        double reglement = 0 ;
        int nbr = 0 ;

        retrait.setText("0 F");
        depot.setText("0 F");

        if (position==0){
            mtn = 0 ;
            try {
                operations = operationDAO.getAll(17,null, new Date());
                nbr = operations.size() ;
                for (int i = 0; i<nbr;++i){
                    if (operations.get(i).getAnnuler()==1) continue;
                    mtn += operations.get(i).getMontant()-operations.get(i).getRemise() ;
                    ArrayList<Operation> payements = operationDAO.getMany(operations.get(i).getId_externe());
                    for (int j = 0; j < payements.size(); j++) {
                        reglement += payements.get(j).getMontant() ;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            retrait.setText(Utiles.formatMtn(mtn) + " F");
            depot.setText(Utiles.formatMtn(mtn-reglement) + " F");

        }
        nbr = operations.size() ;
        //else total.setText("0 F");
        nbre.setText(String.valueOf(nbr));
    }


    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(EmpruntPayementActivity.this) ;
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
            if (operations.get(i).getTypeOperation_id().equals(OperationDAO.VENTE)) solde += operations.get(i).getMontant() ;
            else  solde -= operations.get(i).getMontant() ;
        }

        // si2.setText(Utiles.formatMtn(solde) + " F");
    }


    private void setupTablayout() {
        collectionAdapter = new EmpruntPayementActivity.CollectionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(collectionAdapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupFragments() {
        final FragmentManager fm = getSupportFragmentManager();
        //this.operationFragment1 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.operationFragment1 == null) {
            this.operationFragment1 = OperationFragment.newInstance(null,"17");
        }

        //this.operationFragment2 = (OperationFragment) fm.findFragmentByTag(OperationFragment.TAG);
        if (this.payementFragment == null) {
            this.payementFragment = OperationFragment.newInstance(null,"-1");
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
        getMenuInflater().inflate(R.menu.menu_creditpayement, menu);
        // SearchView
        MenuItem itemSearch = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                operationFragment1.filtrer(newText);
                return false;
            }
        });
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
                        if (mPosition==0) operationFragment1.interval(17,dateDebut,dateFin);
                        else if (mPosition==2)payementFragment.interval(0,dateDebut,dateFin);
                        dateBox = null;
                        alert.dismiss();
                        refresh(0);
                    }
                }
            });
            alert = dateBox.show();
        } else if (id == R.id.action_recap) {

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

            final TextView mtn1 = (TextView) scrollView.findViewById(R.id.mtn1);
            final TextView mtn2 = (TextView) scrollView.findViewById(R.id.mtn2);
            final TextView mtn3 = (TextView) scrollView.findViewById(R.id.mtn3);
            final TextView mtn4 = (TextView) scrollView.findViewById(R.id.mtn4);
            final TextView mtn5 = (TextView) scrollView.findViewById(R.id.mtn5);
            final TextView mtn6 = (TextView) scrollView.findViewById(R.id.mtn6);

            final TextView interval = (TextView) scrollView.findViewById(R.id.interval);

            DatePicker debut = new DatePicker(this) ;
            DatePicker fin = new DatePicker(this) ;
            recapBox.setTitle(getString(R.string.recap) + " du ");

            if (dateDebut==null)dateDebut = String.valueOf(debut.getYear()) + "-" + String.valueOf(debut.getMonth() + 1) + "-" + String.valueOf(debut.getDayOfMonth());
            if (dateFin==null)dateFin = String.valueOf(fin.getYear()) + "-" + String.valueOf(fin.getMonth() + 1) + "-" + String.valueOf(fin.getDayOfMonth());

            interval.setText(dateDebut + " au " + dateFin);

            float mtn = 0 ;
            ArrayList<Operation> operations = null;
            try {
                operations = operationDAO.getAll(1, DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));

                for (int i = 0; i<operations.size();++i){
                    mtn += operations.get(i).getMontant() ;
                }

                nbr1.setText(String.valueOf(operations.size()));
                mtn1.setText(Utiles.formatMtn(mtn) + " F");
            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                operations = operationDAO.getAll(2,DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    mtn += operations.get(i).getMontant() ;
                }

                nbr2.setText(String.valueOf(operations.size()));
                mtn2.setText(Utiles.formatMtn(mtn) + " F");

            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                operations = operationDAO.getAll(5,DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    mtn += operations.get(i).getMontant() ;
                }

                nbr3.setText(String.valueOf(operations.size()));
                mtn3.setText(Utiles.formatMtn(mtn) + " F");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                operations = operationDAO.getAll(3,DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    mtn += operations.get(i).getMontant() ;
                }

                nbr3.setText(String.valueOf(operations.size()));
                mtn3.setText(Utiles.formatMtn(mtn) + " F");
            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                operations = operationDAO.getAll(4,DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    mtn += operations.get(i).getMontant() ;
                }

                nbr4.setText(String.valueOf(operations.size()));
                mtn4.setText(Utiles.formatMtn(mtn) + " F");
            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                operations = operationDAO.getAll(3,DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    mtn += operations.get(i).getMontant() ;
                }

                nbr5.setText(String.valueOf(operations.size()));
                mtn5.setText(Utiles.formatMtn(mtn) + " F");
            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                operations = operationDAO.getAll(0,DAOBase.formatterj.parse(dateDebut), DAOBase.formatterj.parse(dateFin));
                mtn = 0 ;
                for (int i = 0; i<operations.size();++i){
                    mtn += operations.get(i).getMontant() ;
                }

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
                            PrinterUtilsold printerUtils = new PrinterUtilsold(EmpruntPayementActivity.this,mService) ;
                            //printerUtils.printTicket(dateDebut,dateFin);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            PrintPDA printPDA = new PrintPDA(EmpruntPayementActivity.this) ;
                            //printPDA.printTicket(dateDebut,dateFin);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            alert = recapBox.show();
        }

        else if (id == R.id.action_imp){
            final CharSequence[] items = { getString(R.string.pdf), getString(R.string.xls), getString(R.string.fermer) };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle(getString(R.string.action));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals(getString(R.string.pdf))) {

                        if (mPosition == 0) operationFragment1.imprimePDFDoc(getString(R.string.listeop));
                        //else if (mPosition == 1)   payementFragment.imprimePDFDoc(getString(R.string.listedesvt));
                    } else if (items[item].equals(getString(R.string.xls))) {

                        if (mPosition == 0) operationFragment1.imprimeExcelDoc(getString(R.string.listeop));
                        //else if (mPosition == 1)    operationFragment2.imprimeExcelDoc(getString(R.string.listedesvt));
                    } else {
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog alertDialog = builder.show();
        }

        return super.onOptionsItemSelected(item);
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
        String idexterne = uri.toString() ;
        payementFragment.setId(idexterne);
        viewPager.setCurrentItem(1);
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
        //refresh(mPosition);
        if (mPosition==0) operationFragment1.interval(17,dateDebut,dateFin);
        //else if (mPosition==1)payementFragment.interval(dateDebut,dateFin);
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
        int pages = 2 ;

        public CollectionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 : {
                    return operationFragment1 ;
                }
                case 1 : {
                    return payementFragment ;
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
            switch (position){
                case 0 : {
                    return getString(R.string.emprunt) ;
                }
                case 1 : {
                    return getString(R.string.payement) ;
                }
            }
            return super.getPageTitle(position);
        }
    }


}
