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
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.CommercialDAO;
import pv.projects.mediasoft.com.pventes.fragment.CommandeFragment;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Commercial;

public class CommercialActivity extends AppCompatActivity {


    private SearchView mSearchView;
    ListView lv = null;
    private LayoutInflater mInflater;
    ArrayList<Commercial> commercials = null ;
    ListeCommercialAdapter adapter = null ;
    int choice = 0 ;
    FloatingActionButton fab = null ;
    private CommercialDAO commercialDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        setupToolbar();
        
        setTitle(getString(R.string.commercials));

        if (getIntent().getBooleanExtra("CHOICE",false)) choice = 1 ;

        lv = (ListView) findViewById(R.id.list);
        mInflater = LayoutInflater.from(this) ;
        commercials = new ArrayList<Commercial>() ;
        commercialDAO = new CommercialDAO(getApplicationContext()) ;
        commercials = commercialDAO.getAll() ;
        adapter = new ListeCommercialAdapter(commercials) ;
        lv.setAdapter(adapter);
        fab = (FloatingActionButton) findViewById(R.id.fab);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateCommercial(position) ;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null ;
                intent = new Intent(CommercialActivity.this,CommercialFormActivity.class) ;
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refresh();
    }

    private void updateCommercial(final int position) {
        final CharSequence[] items = {getString(R.string.modif), getString(R.string.delete), getString(R.string.annuler)};

        final OperationDAO operationDAO = new OperationDAO(this) ;
        AlertDialog.Builder builder = new AlertDialog.Builder(CommercialActivity.this);
        builder.setTitle(getString(R.string.action));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.modif))) {
                    Commercial p = adapter.getItem(position) ;
                    Intent intent = new Intent(CommercialActivity.this, CommercialFormActivity.class) ;
                    intent.putExtra("ID",p.getId());
                    startActivity(intent);
                    refresh();

                } else if (items[item].equals(getString(R.string.delete))) {
                    if (operationDAO.getManyByCommercial(adapter.getItem(position).getId_externe()).size()==0)  deleteItem(position);
                    else Toast.makeText(CommercialActivity.this, R.string.commercialdelete, Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    public void deleteItem(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(CommercialActivity.this) ;
        builder.setTitle(getString(R.string.app_name)) ;
        builder.setMessage(getString(R.string.delconfirm)) ;
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                Commercial commercial = adapter.getItem(position);

                //DelCommercialTask task = new DelCommercialTask(commercials) ;
                //task.execute() ;
                commercialDAO.delete(commercial.getId()) ;
                refresh();
                Toast.makeText(CommercialActivity.this, R.string.partdelet, Toast.LENGTH_SHORT).show();
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
                filtrer(commercials,newText);
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


    public class ListeCommercialAdapter extends BaseAdapter {

        ArrayList<Commercial> commercials = new ArrayList<Commercial>() ;

        public ListeCommercialAdapter(ArrayList<Commercial> pv){
            commercials = pv ;
        }

        @Override
        public int getCount() {
            return commercials.size() ;
        }

        @Override
        public Commercial getItem(int position) {
            return commercials.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return commercials.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.partenaire_item, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            Commercial commercial = (Commercial) getItem(position);


            if(commercial != null) {
                holder.nomprenomTV.setText(commercial.getNom() + " " + commercial.getPrenom());
                holder.contactTV.setText(getString(R.string.cnt) + commercial.getContact());
                holder.adresseTV.setText(getString(R.string.adr) + commercial.getAdresse());

               /*
                if (commercial.getImage()!=null) loadLocalImage(commercial.getImage(), holder.imageView);
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
        public ImageView imageView;

        public ViewHolder(View v) {
            nomprenomTV = (TextView)v.findViewById(R.id.nomprenom);
            contactTV = (TextView)v.findViewById(R.id.contact);
            adresseTV = (TextView)v.findViewById(R.id.adresse);
            imageView = (ImageView)v.findViewById(R.id.image);
        }
    }


    public void filtrer(ArrayList<Commercial> commercials, String query){
        Commercial commercial = null ;
        ArrayList<Commercial> data = new ArrayList<Commercial>() ;

        if(commercials != null)
            for(int i = 0 ; i < commercials.size() ; i++){
                commercial = commercials.get(i) ;
                if(commercial.getNom().toLowerCase().contains(query)|| commercial.getPrenom().toLowerCase().contains(query)) data.add(commercial) ;
            }
        adapter = new ListeCommercialAdapter(data) ;
        lv.setAdapter(adapter) ;
        adapter.notifyDataSetChanged();
    }




    public class DelCommercialTask extends AsyncTask<Void, Integer, Boolean> {
        ArrayList<Commercial> mCommercials = null ;

        public DelCommercialTask(ArrayList<Commercial> commercials1){
            this.mCommercials = commercials1;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            for (int i = 0; i< mCommercials.size(); i++) {
                commercialDAO.delete(mCommercials.get(i).getId()) ;
                mCommercials.get(i) ;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(CommercialActivity.this, String.valueOf(mCommercials.size()) + " " + getResources().getString(R.string.commercial_suprimmer), Toast.LENGTH_LONG).show();
                refresh();
            }
            else
                Toast.makeText(CommercialActivity.this, getString(R.string.echec_del), Toast.LENGTH_LONG).show();
            dismissDialog(AccueilActivity.PROGRESS_DIALOG_ID);
        }
    }

    private void refresh() {
        commercials = commercialDAO.getAll() ;
        adapter = new ListeCommercialAdapter(commercials) ;
        lv.setAdapter(adapter);
    }


}
