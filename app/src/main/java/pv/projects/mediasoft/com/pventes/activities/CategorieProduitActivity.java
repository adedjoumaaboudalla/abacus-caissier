package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CategorieProduitDAO;
import pv.projects.mediasoft.com.pventes.model.CategorieProduit;

public class CategorieProduitActivity extends AppCompatActivity {
    private static final int MAX_SIZE = 2;
    private SearchView mSearchView;
    private static final int PROGRESS_DIALOG_ID = 1;
    ListView lv = null;
    private LayoutInflater mInflater;
    ListeCategorieProduitAdapter adapter = null ;
    private ProgressDialog mProgressBar;
    private ArrayList<CategorieProduit> categorieProduits;
    private String nocompte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie_produit);

        setupToolbar();


        lv = (ListView) findViewById(R.id.list);
        mInflater = LayoutInflater.from(this) ;
        categorieProduits = new ArrayList<CategorieProduit>() ;

        adapter = new ListeCategorieProduitAdapter(categorieProduits) ;
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent result = new Intent();
                CategorieProduit categorieProduit = adapter.getItem(i);
                Log.e("CategorieProduit", String.valueOf(categorieProduit.getId()));
                result.putExtra(ProduitFormActivity.CATEGORIE, categorieProduit.getId());
                setResult(RESULT_OK, result);
                finish();
            }
        });

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
        getMenuInflater().inflate(R.menu.menu_categorie, menu);

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
                filtrer(categorieProduits,newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }


    public class ListeCategorieProduitAdapter extends BaseAdapter {

        ArrayList<CategorieProduit> CategorieProduits = new ArrayList<CategorieProduit>() ;

        public ListeCategorieProduitAdapter(ArrayList<CategorieProduit> pv){
            CategorieProduits = pv ;
        }

        @Override
        public int getCount() {
            return CategorieProduits.size() ;
        }

        @Override
        public CategorieProduit getItem(int position) {
            return CategorieProduits.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return CategorieProduits.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.categorieproduit_item, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            CategorieProduit categorieProduit = (CategorieProduit) getItem(position);


            if(categorieProduit != null) {
                holder.libelleTV.setText(categorieProduit.getLibelle());
            };

            return convertView;
        }

    }



    static class ViewHolder{
        TextView libelleTV ;

        public ViewHolder(View v) {
            libelleTV = (TextView)v.findViewById(R.id.libelleTV);
        }
    }


    public void filtrer(ArrayList<CategorieProduit> CategorieProduits, String query){
        CategorieProduit categorieProduit = null ;
        ArrayList<CategorieProduit> data = new ArrayList<CategorieProduit>() ;

        if(categorieProduit != null)
            for(int i = 0 ; i < CategorieProduits.size() ; i++){
                categorieProduit = CategorieProduits.get(i) ;
                if(categorieProduit.getLibelle().toLowerCase().contains(query)) data.add(categorieProduit) ;
            }
        adapter = new ListeCategorieProduitAdapter(data) ;
        lv.setAdapter(adapter) ;
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ChargementCategorieProduit chargementCategorieProduit = new ChargementCategorieProduit() ;
        chargementCategorieProduit.execute() ;
    }



    public class ChargementCategorieProduit extends AsyncTask<String,Void,String> {

        CategorieProduitDAO categorieProduitDAO = null ;
        CategorieProduit c  =null;
        JSONArray categorieProduitArray = null ;
        private SharedPreferences preferences;

        public ChargementCategorieProduit() {
        }

        @Override
        protected String doInBackground(String... urls) {
            //TelephonyManager telephonyManager =  ( TelephonyManager ) getSystemService (Context.TELEPHONY_SERVICE);
            //String imei = telephonyManager.getDeviceId ();

            FormBody.Builder formBuilder = new FormBody.Builder() ;
            categorieProduits = categorieProduitDAO.getAll() ;
            Log.e("DEBUG", String.valueOf(categorieProduits.size())) ;
            return "" ;
        }


        @Override
        protected void onPostExecute(String res) {
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            super.onPostExecute(res);

            adapter = new ListeCategorieProduitAdapter(categorieProduits) ;
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (categorieProduits.size()==0)  Toast.makeText(CategorieProduitActivity.this, R.string.anyresult, Toast.LENGTH_SHORT).show();

            try {
                dismissDialog(PROGRESS_DIALOG_ID);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(PROGRESS_DIALOG_ID);
            if (categorieProduitDAO==null) categorieProduitDAO = new CategorieProduitDAO(CategorieProduitActivity.this) ;
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()) ;
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);

        if (mProgressBar == null) {
            mProgressBar = new ProgressDialog(this);
            mProgressBar.setCancelable(true);
            mProgressBar.setTitle(

                    getString(R.string.loding)

            );
            mProgressBar.setMessage(
                    getString(R.string.wait)
            );
            mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressBar.setMax(MAX_SIZE);
        }

        return mProgressBar;
    }

}
