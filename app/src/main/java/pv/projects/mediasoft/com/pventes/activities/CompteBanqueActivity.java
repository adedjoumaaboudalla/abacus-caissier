package pv.projects.mediasoft.com.pventes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.Mouvement;

public class CompteBanqueActivity extends AppCompatActivity {


    private SearchView mSearchView;
    ListView lv = null;
    private LayoutInflater mInflater;
    ArrayList<CompteBanque> compteBanques = null ;
    ListeCompteBanqueAdapter adapter = null ;
    int choice = 0 ;
    FloatingActionButton fab = null ;
    private CompteBanqueDAO compteBanqueDAO;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte_banque);

        setupToolbar();

        if (getIntent().getBooleanExtra("CHOICE",false)) choice = 1 ;

        lv = (ListView) findViewById(R.id.list);
        mInflater = LayoutInflater.from(this) ;
        compteBanques = new ArrayList<CompteBanque>() ;
        compteBanqueDAO = new CompteBanqueDAO(getApplicationContext()) ;
        compteBanques = compteBanqueDAO.getAll() ;
        adapter = new ListeCompteBanqueAdapter(compteBanques) ;
        lv.setAdapter(adapter);
        fab = (FloatingActionButton) findViewById(R.id.fab);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateCompteBanque(position) ;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null ;
                intent = new Intent(CompteBanqueActivity.this,CompteBanqueFormActivity.class) ;
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refresh();
    }

    private void updateCompteBanque(final int position) {
        final CharSequence[] items = {getString(R.string.modif), getString(R.string.delete), getString(R.string.annuler)};

        final OperationDAO operationDAO = new OperationDAO(this) ;
        AlertDialog.Builder builder = new AlertDialog.Builder(CompteBanqueActivity.this);
        builder.setTitle(getString(R.string.action));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.modif))) {
                    CompteBanque p = adapter.getItem(position) ;
                    Intent intent = new Intent(CompteBanqueActivity.this, CompteBanqueFormActivity.class) ;
                    intent.putExtra("ID",p.getId());
                    startActivity(intent);
                    refresh();

                } else if (items[item].equals(getString(R.string.delete))) {
                    if (operationDAO.getManyByCompteBanque(adapter.getItem(position).getId_externe()).size()==0)  deleteItem(position);
                    else Toast.makeText(CompteBanqueActivity.this, R.string.compteBanquedelete, Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    public void deleteItem(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(CompteBanqueActivity.this) ;
        builder.setTitle(getString(R.string.app_name)) ;
        builder.setMessage(getString(R.string.delconfirm)) ;
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                CompteBanque compteBanque = adapter.getItem(position);

                //DelCompteBanqueTask task = new DelCompteBanqueTask(compteBanques) ;
                //task.execute() ;
                compteBanqueDAO.delete(compteBanque.getId()) ;
                refresh();
                Toast.makeText(CompteBanqueActivity.this, R.string.compteBanquedelete, Toast.LENGTH_SHORT).show();
            }
        }) ;
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
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
        getMenuInflater().inflate(R.menu.menu_client_liste, menu);

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
                filtrer(compteBanques,newText);
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


    public class ListeCompteBanqueAdapter extends BaseAdapter {

        ArrayList<CompteBanque> compteBanques = new ArrayList<CompteBanque>() ;

        public ListeCompteBanqueAdapter(ArrayList<CompteBanque> pv){
            compteBanques = pv ;
        }

        @Override
        public int getCount() {
            return compteBanques.size() ;
        }

        @Override
        public CompteBanque getItem(int position) {
            return compteBanques.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return compteBanques.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.comptebanque_item, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            CompteBanque compteBanque = (CompteBanque) getItem(position);


            if(compteBanque != null) {
                holder.nomprenomTV.setText(getString(R.string.cmpt) + " " + compteBanque.getCode());
                holder.contactTV.setText(getString(R.string.bq) + " " +  compteBanque.getLibelle());
                holder.adresseTV.setText(getString(R.string.solde) + " " +  compteBanque.getSolde());
                if (compteBanque.getEtat()==2) holder.sync.setVisibility(View.VISIBLE);
               /*
                if (compteBanque.getImage()!=null) loadLocalImage(compteBanque.getImage(), holder.imageView);
                else holder.imageView.setImageBitmap(null);
                 */
            };

            return convertView;
        }

        public void addData(Mouvement pv) {
            //if (mouvements==null) mouvements = new ArrayList<Mouvement>() ;
            //mouvements.add(pv) ;
            notifyDataSetChanged();
        }



        public void removeSelection() {
            notifyDataSetChanged();
        }
    }



    static class ViewHolder{
        TextView nomprenomTV ;
        TextView contactTV ;
        TextView adresseTV ;
        TextView sync ;
        public ImageView imageView;

        public ViewHolder(View v) {
            nomprenomTV = (TextView)v.findViewById(R.id.nomprenom);
            contactTV = (TextView)v.findViewById(R.id.contact);
            sync = (TextView)v.findViewById(R.id.sync);
            adresseTV = (TextView)v.findViewById(R.id.adresse);
            imageView = (ImageView)v.findViewById(R.id.image);
        }
    }


    public void filtrer(ArrayList<CompteBanque> compteBanques, String query){
        CompteBanque compteBanque = null ;
        ArrayList<CompteBanque> data = new ArrayList<CompteBanque>() ;

        if(compteBanques != null)
            for(int i = 0 ; i < compteBanques.size() ; i++){
                compteBanque = compteBanques.get(i) ;
                if(compteBanque.getCode().toLowerCase().contains(query)|| compteBanque.getLibelle().toLowerCase().contains(query)) data.add(compteBanque) ;
            }
        adapter = new ListeCompteBanqueAdapter(data) ;
        lv.setAdapter(adapter) ;
        adapter.notifyDataSetChanged();
    }




    public class DelCompteBanqueTask extends AsyncTask<Void, Integer, Boolean> {
        ArrayList<CompteBanque> mCompteBanques = null ;

        public DelCompteBanqueTask(ArrayList<CompteBanque> compteBanques1){
            this.mCompteBanques = compteBanques1;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            for (int i = 0; i< mCompteBanques.size(); i++) {
                compteBanqueDAO.delete(mCompteBanques.get(i).getId()) ;
                mCompteBanques.get(i) ;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(CompteBanqueActivity.this, String.valueOf(mCompteBanques.size()) + " " + getResources().getString(R.string.compteBanque_suprimmer), Toast.LENGTH_LONG).show();
                refresh();
            }
            else
                Toast.makeText(CompteBanqueActivity.this, getString(R.string.echec_del), Toast.LENGTH_LONG).show();
            dismissDialog(AccueilActivity.PROGRESS_DIALOG_ID);
        }
    }

    private void refresh() {
        compteBanques = compteBanqueDAO.getAll() ;
        adapter = new ListeCompteBanqueAdapter(compteBanques) ;
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



}
