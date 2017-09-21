package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.fragment.CommandeFragment;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.model.PointVente;
import pv.projects.mediasoft.com.pventes.utiles.ExcelReaderListener;
import pv.projects.mediasoft.com.pventes.utiles.FileChooser;

public class PartenaireActivity extends AppCompatActivity {


    private static final int MAX_SIZE = 0;
    private static final int PROGRESS_ID = 1;
    private SearchView mSearchView;
    ListView lv = null;
    private LayoutInflater mInflater;
    ArrayList<Partenaire> partenaires = null ;
    ListePartenaireAdapter adapter = null ;
    int choice = 0 ;
    FloatingActionButton fab = null ;
    private PartenaireDAO partenaireDAO;
    private String typepartenaire;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        setupToolbar();
        
        setTitle(getString(R.string.partenaires));



        if (getIntent().getBooleanExtra("CLT",false))typepartenaire = PartenaireFormActivity.CLT ;
        else if (getIntent().getBooleanExtra("FIN",false))typepartenaire = PartenaireFormActivity.FIN ;
        else typepartenaire = PartenaireFormActivity.FRS ;

        lv = (ListView) findViewById(R.id.list);
        mInflater = LayoutInflater.from(this) ;
        partenaires = new ArrayList<Partenaire>() ;
        partenaireDAO = new PartenaireDAO(getApplicationContext()) ;
        fab = (FloatingActionButton) findViewById(R.id.fab);


        if (getIntent().getBooleanExtra("CHOICE",false)){
            choice = 1 ;
            partenaires = partenaireDAO.getAllByTypePartnaire(typepartenaire) ;
        }
        else partenaires = partenaireDAO.getAll() ;

        adapter = new ListePartenaireAdapter(partenaires) ;
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (choice==1){
                    Intent result = new Intent();
                    Partenaire partenaire = adapter.getItem(position) ;
                    result.putExtra(CommandeFragment.PARTENAIRE, partenaire.getId());
                    setResult(RESULT_OK , result);
                    finish();
                }
                else{
                    updatePartenaire(position) ;
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null ;
                intent = new Intent(PartenaireActivity.this,PartenaireFormActivity.class) ;
                if (choice==1)intent.putExtra("CHOICE",true) ;
                startActivity(intent);
            }
        });
    }

    private void updatePartenaire(final int position) {
        final CharSequence[] items = {getString(R.string.modif), getString(R.string.delete), getString(R.string.annuler)};

        final OperationDAO operationDAO = new OperationDAO(this) ;
        AlertDialog.Builder builder = new AlertDialog.Builder(PartenaireActivity.this);
        builder.setTitle(getString(R.string.action));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.modif))) {
                    Partenaire p = adapter.getItem(position) ;
                    Intent intent = new Intent(PartenaireActivity.this, PartenaireFormActivity.class) ;
                    intent.putExtra("ID",p.getId());
                    startActivity(intent);
                    refresh();

                } else if (items[item].equals(getString(R.string.delete))) {
                    if (operationDAO.getManyByPartenaire(adapter.getItem(position).getId_externe()).size()==0)  deleteItem(position);
                    else Toast.makeText(PartenaireActivity.this, R.string.partenairedelete, Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    public void deleteItem(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PartenaireActivity.this) ;
        builder.setTitle(getString(R.string.app_name)) ;
        builder.setMessage(getString(R.string.delconfirm)) ;
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                Partenaire partenaire = adapter.getItem(position);

                //DelPartenaireTask task = new DelPartenaireTask(partenaires) ;
                //task.execute() ;
                partenaireDAO.delete(partenaire.getId()) ;
                refresh();
                Toast.makeText(PartenaireActivity.this, R.string.partdelet, Toast.LENGTH_SHORT).show();
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
                filtrer(partenaires,newText);
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

        if (id == R.id.menu_choose_file) {
            chooseExcelFile();
            return true;
        }

        if (id==android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }


    public class ListePartenaireAdapter extends BaseAdapter {

        ArrayList<Partenaire> partenaires = new ArrayList<Partenaire>() ;

        public ListePartenaireAdapter(ArrayList<Partenaire> pv){
            partenaires = pv ;
        }

        @Override
        public int getCount() {
            return partenaires.size() ;
        }

        @Override
        public Partenaire getItem(int position) {
            return partenaires.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return partenaires.get(position).getId();
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
            Partenaire partenaire = (Partenaire) getItem(position);


            if(partenaire != null) {
                holder.nomprenomTV.setText(partenaire.getRaisonsocial() + "" + partenaire.getNom() + " " + partenaire.getPrenom());
                holder.contactTV.setText(getString(R.string.cnt) + partenaire.getContact());
                holder.adresseTV.setText(getString(R.string.adr) + partenaire.getAdresse());

               /*
                if (partenaire.getImage()!=null) loadLocalImage(partenaire.getImage(), holder.imageView);
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


    public void filtrer(ArrayList<Partenaire> partenaires, String query){
        Partenaire partenaire = null ;
        ArrayList<Partenaire> data = new ArrayList<Partenaire>() ;

        if(partenaires != null)
            for(int i = 0 ; i < partenaires.size() ; i++){
                partenaire = partenaires.get(i) ;
                if(partenaire.getNom().toLowerCase().contains(query)|| partenaire.getPrenom().toLowerCase().contains(query) || partenaire.getRaisonsocial().toLowerCase().contains(query) ) data.add(partenaire) ;
            }
        adapter = new ListePartenaireAdapter(data) ;
        lv.setAdapter(adapter) ;
        adapter.notifyDataSetChanged();
    }




    public class DelPartenaireTask extends AsyncTask<Void, Integer, Boolean> {
        ArrayList<Partenaire> mPartenaires = null ;

        public DelPartenaireTask(ArrayList<Partenaire> partenaires1){
            this.mPartenaires = partenaires1;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            for (int i = 0; i< mPartenaires.size(); i++) {
                partenaireDAO.delete(mPartenaires.get(i).getId()) ;
                mPartenaires.get(i) ;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(PartenaireActivity.this, String.valueOf(mPartenaires.size()) + " " + getResources().getString(R.string.partenaire_suprimmer), Toast.LENGTH_LONG).show();
                refresh();
            }
            else
                Toast.makeText(PartenaireActivity.this, getString(R.string.echec_del), Toast.LENGTH_LONG).show();
            dismissDialog(AccueilActivity.PROGRESS_DIALOG_ID);
        }
    }

    private void refresh() {
        partenaires = partenaireDAO.getAll() ;
        adapter = new ListePartenaireAdapter(partenaires) ;
        lv.setAdapter(adapter);
    }


    public void chooseExcelFile() {
      AlertDialog.Builder builder =  new AlertDialog.Builder(PartenaireActivity.this) ;
        builder.setTitle(getString(R.string.choosefileexcel)) ;
        builder.setMessage("raison social\n" +
                "nom\n" +
                "prenom\n" +
                "sexe\n" +
                "contact\n" +
                "email\n" +
                "addresse\n" +
                "type personne (PM, PP)\n" +
                "type partenaire (FIN, FRS, CLT)") ;
        builder.setPositiveButton(R.string.choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                new FileChooser(PartenaireActivity.this, new String[]{"xls", "xlsx"})
                        .setFileListener(new FileChooser.FileSelectedListener() {
                            @Override
                            public void fileSelected(File file) {
                                String filePath = file.getAbsolutePath();
                                Log.e("PATH",filePath) ;
                               ChooseFileTask chooseFileTask = new ChooseFileTask(filePath) ;
                                chooseFileTask.execute() ;
                            }
                        })
                        .showDialog();
            }
        }) ;
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }) ;
        builder.show();
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        if (mProgressBar ==null){
            mProgressBar = new ProgressDialog(this) ;
            mProgressBar.setCancelable(true);
            mProgressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
            mProgressBar.setTitle(getString(R.string.send_loding));
            mProgressBar.setMessage(getString(R.string.wait));
            mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressBar.setMax(MAX_SIZE);
        }

        return mProgressBar ;
    }




    public class ChooseFileTask extends AsyncTask<Void,String,String>{
        int size = 0 ;
        String filePath = null ;
        public ChooseFileTask(String path) {
            this.filePath = path ;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = new WebView(PartenaireActivity.this);
                        PointVente pv = new PointVenteDAO(PartenaireActivity.this).getLast() ;
                        try {
                            Workbook workbook = Workbook.getWorkbook(new File(filePath)) ;
                            Sheet sheet = workbook.getSheet(0) ;
                            size = sheet.getRows() ;
                            Partenaire partenaire = null ;
                            for (int i = 1; i < size; i++) {
                                Cell[] row = sheet.getRow(i);
                                partenaire = new Partenaire() ;
                                partenaire.setRaisonsocial(row[0].getContents());
                                partenaire.setNom(row[1].getContents());
                                partenaire.setPrenom(row[2].getContents());
                                partenaire.setSexe(row[3].getContents());
                                partenaire.setContact(row[4].getContents());
                                partenaire.setEmail(row[5].getContents());
                                partenaire.setAdresse(row[6].getContents());
                                partenaire.setTypepersonne(row[7].getContents());
                                partenaire.setTypepartenaire(row[8].getContents());
                                partenaire.setUtilisateur_id(pv.getUtilisateur());

                                partenaireDAO.add(partenaire) ;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (BiffException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return String.valueOf(size) ;
            } catch (Exception ex) {
                Log.e("Import excel error", ex.getMessage());
            }
            return "0";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(PROGRESS_ID);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissDialog(PROGRESS_ID);
            Toast.makeText(PartenaireActivity.this, result + " " + getString(R.string.partenaireajoute), Toast.LENGTH_SHORT).show();
        }
    }

}
