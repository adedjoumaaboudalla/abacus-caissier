package pv.projects.mediasoft.com.pventes.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.fragment.VenteplusFormFragment;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.utiles.Url;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

public class ProduitListeActivity extends AppCompatActivity {


    private SearchView mSearchView;
    ListView lv = null;
    private LayoutInflater mInflater;
    ArrayList<Produit> produits = null ;
    ListeProduitAdapter adapter = null ;
    private ProduitDAO produitDAO;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produit_liste);

        setupToolbar();


        lv = (ListView) findViewById(R.id.list);
        mInflater = LayoutInflater.from(this) ;
        produitDAO = new ProduitDAO(this) ;
        produits = new ArrayList<Produit>() ;
        ProduitDAO produitDAO = new ProduitDAO(getApplicationContext()) ;

        Intent intent = getIntent() ;
        if (intent!=null){
            type = intent.getStringExtra("Type") ;
        }
        if (type.equals(AccueilActivity.VENTE)) produits = produitDAO.getAll(ProduitDAO.VENTE) ;
        else if (type.equals(AchatActivity.ACHAT)) produits = produitDAO.getAll(ProduitDAO.ACHAT) ;
        else produits = produitDAO.getAll() ;
        adapter = new ListeProduitAdapter(produits) ;
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent result = new Intent();
                Produit produit = adapter.getItem(position) ;
                result.putExtra(VenteplusFormFragment.PRODUIT, produit);
                setResult(RESULT_OK , result);
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
        getMenuInflater().inflate(R.menu.menu_produit_liste, menu);

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
                filtrer(produits,newText);
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


    public class ListeProduitAdapter extends BaseAdapter {

        ArrayList<Produit> produits = new ArrayList<Produit>() ;

        public ListeProduitAdapter(ArrayList<Produit> pv){
            produits = pv ;
        }

        @Override
        public int getCount() {
            return produits.size() ;
        }

        @Override
        public Produit getItem(int position) {
            return produits.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return produits.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.produit_item, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            Produit produit = (Produit) getItem(position);


            if(produit != null) {
                holder.libelleTextView.setText(produit.getLibelle());
                holder.codeTextView.setText(getString(R.string.codepoint) + produit.getCode());
                if (type.equals(AccueilActivity.VENTE))holder.prixVTextView.setText(getString(R.string.prixpoin) + Utiles.formatMtn(produit.getPrixV()));
                else holder.prixVTextView.setText(getString(R.string.prixpoin) + Utiles.formatMtn(produit.getPrixA()));

                if (produit.getImage()!=null && produit.getImage().contains("/")) loadLocalImage(produit.getImage(),holder.imageView);
                else if (produit.getImage()!=null)loadWebImage(holder.imageView, produit);

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
        public ImageView imageView;

        public ViewHolder(View v) {
            codeTextView = (TextView)v.findViewById(R.id.codeTV);
            libelleTextView = (TextView)v.findViewById(R.id.libelleTV);
            prixVTextView = (TextView)v.findViewById(R.id.prixVTV);
            imageView = (ImageView)v.findViewById(R.id.image);
        }
    }


    public void filtrer(ArrayList<Produit> produits, String query){
        Produit produit = null ;
        ArrayList<Produit> data = new ArrayList<Produit>() ;

        if(produits != null)
            for(int i = 0 ; i < produits.size() ; i++){
                produit = produits.get(i) ;
                if(produit.getLibelle().toLowerCase().contains(query)|| produit.getCode().toLowerCase().contains(query) ) data.add(produit) ;
            }
        adapter = new ListeProduitAdapter(data) ;
        lv.setAdapter(adapter) ;
        adapter.notifyDataSetChanged();
    }


    public void loadWebImage(final ImageView imageView, final Produit produit) {

        final boolean[] b = {true};
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap1, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("RESULT", "Sauvegarde en cours...");
                        //bitmap = bitmap1 ;
                        long i = System.currentTimeMillis() ;
                        final Uri[] uri = {null};
                        ProduitListeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap1);

                                // progressBar.setVisibility(View.GONE);
                            }
                        });

                        try {
                            uri[0] = Utiles.saveImageExternalStorage(bitmap1, ProduitListeActivity.this, String.valueOf(produit.getId())+".jpg", Utiles.PV_PRODUIT_IMAGE_DIR);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e("Save", uri[0].getPath());
                        if (uri[0]!=null){
                            produit.setImage(uri[0].getPath());
                            produitDAO.update(produit) ;
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("RESULT", "Echec de la LOAD");
                //Toast.makeText(ShowBookActivity.this,"Echec de chargement d'image",Toast.LENGTH_SHORT).show();
                b[0] = false ;
                ProduitListeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //progressBar.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("RESULT", "LOAD en cours...");
                //Toast.makeText(getApplicationContext(),"Sauvegarde en cours...",Toast.LENGTH_LONG).show();
            }
        };

        String path = Url.getImageDirectory(ProduitListeActivity.this, produit.getImage()) ;
        Log.e("WEB PATH",path) ;
        Picasso.with(ProduitListeActivity.this).load(path).into(target);
    }



    public void loadLocalImage(final String name, final ImageView imageView) {
        String dossier = Utiles.PV_PRODUIT_IMAGE_DIR ;

        File file = new File(name) ;
        imageView.setImageURI(Uri.fromFile(file));

        Log.e("DEBUG", "LOD1") ;
    }
}
