package pv.projects.mediasoft.com.pventes.activities;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.fragment.VenteplusFormFragment;
import pv.projects.mediasoft.com.pventes.model.Devise;
import pv.projects.mediasoft.com.pventes.model.DeviseOperation;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class DeviseActivity extends AppCompatActivity {



    private SearchView mSearchView;
    ListView lv = null;
    private LayoutInflater mInflater;
    ArrayList<Devise> devises = null ;
    ListeDeviseAdapter adapter = null ;
    private DeviseDAO deviseDAO;
    private String type = "";

    ArrayList<DeviseOperation> deviseOperations = null ;
    private TextView total;
    private Button valider;
    private Button annuler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devise);

        setupToolbar();


        lv = (ListView) findViewById(R.id.list);
        mInflater = LayoutInflater.from(this) ;
        deviseDAO = new DeviseDAO(this) ;
        devises = new ArrayList<Devise>() ;
        deviseOperations = new ArrayList<DeviseOperation>(devises.size()) ;
        DeviseDAO deviseDAO = new DeviseDAO(getApplicationContext()) ;
        devises = deviseDAO.getAll() ;
        adapter = new ListeDeviseAdapter(devises) ;
        total = (TextView) findViewById(R.id.total);

        Intent intent = getIntent() ;
        if (intent!=null){
            type = intent.getStringExtra("Type") ;
        }
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent result = new Intent();
                Devise devise = adapter.getItem(position) ;
                showDevisePanel(devise,position) ;
            }
        });
    }



    private void showDevisePanel(final Devise devise, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviseActivity.this) ;
        ScrollView sc = (ScrollView) getLayoutInflater().inflate(R.layout.devisepanel,null);
        builder.setView(sc) ;

        final EditText value = (EditText) sc.findViewById(R.id.value);
        TextView cours = (TextView) sc.findViewById(R.id.cours);
        final TextView total = (TextView) sc.findViewById(R.id.total);

        Button valider = (Button) sc.findViewById(R.id.valider);
        Button annuler = (Button) sc.findViewById(R.id.annuler);
        cours.setText(String.valueOf(devise.getCoursmoyen()));

        final AlertDialog alert = builder.create();


        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value.getText().length()>0) {
                    devise.setCoursmoyen(Double.valueOf(value.getText().toString()));
                    deviseDAO.update(devise) ;
                }
                else Toast.makeText(DeviseActivity.this, R.string.data_errors1, Toast.LENGTH_SHORT).show();
                alert.dismiss();

            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });


        if (devise!=null){
            value.setText(String.valueOf(devise.getCoursmoyen()));
        }

        alert.show();

    }

    private DeviseOperation getDeviseOperation(Devise devise) {
        double ttc = 0;;
        for (int i = 0; i < deviseOperations.size(); i++) {
            if (devise==null){
                ttc += deviseOperations.get(i).getMontant() * deviseDAO.getOne(deviseOperations.get(i).getDevise_id()).getCoursmoyen() ;
                continue;
            }

            if (deviseOperations.get(i).getDevise_id() == devise.getId()) return deviseOperations.get(i) ;
        }
        if (devise==null) total.setText(Utiles.formatMtn2(ttc));
        return null;
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
        getMenuInflater().inflate(R.menu.menu_devise_liste, menu);

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
                filtrer(devises,newText);
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
        if (id == R.id.action_settings) {
            return true;
        }

        if (id==android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }


    public class ListeDeviseAdapter extends BaseAdapter {

        ArrayList<Devise> devises = new ArrayList<Devise>() ;

        public ListeDeviseAdapter(ArrayList<Devise> pv){
            devises = pv ;
        }

        @Override
        public int getCount() {
            return devises.size() ;
        }

        @Override
        public Devise getItem(int position) {
            return devises.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return devises.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DeviseListActivity.ViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.devise_item, null);
                holder = new DeviseListActivity.ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (DeviseListActivity.ViewHolder)convertView.getTag();
            }
            Devise devise = (Devise) getItem(position);


            if(devise != null) {
                holder.libelleTextView.setText(devise.getLibelledevise());
                holder.codeTextView.setText(getString(R.string.codepoint) + devise.getCodedevise());

                holder.imageView.setText(devise.getSymbole());
                double valeur = 0 ;
                holder.prixVTextView.setText(Utiles.formatMtn(devise.getCoursmoyen()));

            };

            return convertView;
        }

        public void addData(Mouvement pv) {
            //if (mouvements==null) mouvements = new ArrayList<Mouvement>() ;
            //mouvements.add(pv) ;
            notifyDataSetChanged();
        }

    }



    static class ViewHolder{
        TextView codeTextView ;
        TextView libelleTextView ;
        TextView prixVTextView ;
        public TextView imageView;

        public ViewHolder(View v) {
            codeTextView = (TextView)v.findViewById(R.id.codeTV);
            libelleTextView = (TextView)v.findViewById(R.id.libelleTV);
            prixVTextView = (TextView)v.findViewById(R.id.prixVTV);
            imageView = (TextView)v.findViewById(R.id.image);
        }
    }


    public void filtrer(ArrayList<Devise> devises, String query){
        Devise devise = null ;
        ArrayList<Devise> data = new ArrayList<Devise>() ;

        if(devises != null)
            for(int i = 0 ; i < devises.size() ; i++){
                devise = devises.get(i) ;
                if(devise.getLibelledevise().toLowerCase().contains(query)|| devise.getCodedevise().toLowerCase().contains(query) ) data.add(devise) ;
            }
        adapter = new ListeDeviseAdapter(data) ;
        lv.setAdapter(adapter) ;
        adapter.notifyDataSetChanged();
    }


}
