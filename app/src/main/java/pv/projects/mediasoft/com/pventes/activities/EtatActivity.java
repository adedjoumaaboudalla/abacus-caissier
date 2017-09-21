package pv.projects.mediasoft.com.pventes.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.utiles.EtatsUtils;

public class EtatActivity extends AppCompatActivity {

    private SharedPreferences preferences = null;
    private Toolbar mToolbar;

    private ListView liste = null;
    private ParametresAdapter adapter = null;

    AlertDialog.Builder dateBox = null ;
    AlertDialog.Builder recapBox = null ;

    private String dateFin = null;
    private String dateDebut = null ;

    Button button = null ;
    private Dialog alert = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistique);
        initialisation();
        setupToolBar();
    }

    private void setupToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_back);
        ab.setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_etat, menu);
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
                        dateBox = null;
                        alert.dismiss();
                    }
                }
            });
            alert = dateBox.show();
        }


        return super.onOptionsItemSelected(item);
    }



    private void initialisation() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        liste = (ListView) findViewById(R.id.liste);
        adapter = new ParametresAdapter() ;
        liste.setAdapter(adapter);


        DatePicker debut = new DatePicker(this) ;
        DatePicker fin = new DatePicker(this) ;
        if (dateDebut==null) dateDebut= DAOBase.formatter2.format(new Date("2015/01/01"));
        if (dateFin==null) dateFin= DAOBase.formatter2.format(new Date());

        preferences = PreferenceManager.getDefaultSharedPreferences(this) ;

        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imprimerEtat(position) ;
            }
        });
    }

    private void imprimerEtat(final int i) {
        final CharSequence[] items = { getString(R.string.pdf), getString(R.string.xls), getString(R.string.fermer) };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(getString(R.string.action));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.pdf))) {
                    imprimePDFDoc(i);
                } else if (items[item].equals(getString(R.string.xls))) {
                    imprimeExcelDoc(i);
                } else {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public void imprimeExcelDoc(final int i) {
        final EditText edittext = new EditText(EtatActivity.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(EtatActivity.this);

        alert.setMessage(getString(R.string.docexcel));
        alert.setTitle(getString(R.string.ficname));

        alert.setView(edittext);
        edittext.setText(R.string.listeopp);

        alert.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String name = edittext.getText().toString();
                EtatsUtils.createandDisplayExcelEtat(i,name,EtatActivity.this, dateDebut,dateFin);
            }
        });

        alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        final AlertDialog alertdialog = alert.create();
        alertdialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
            }
        }) ;
        alertdialog.show();
    }




    public void imprimePDFDoc(final int i) {
        final EditText edittext = new EditText(EtatActivity.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(EtatActivity.this);

        alert.setMessage(getString(R.string.docpdf));
        alert.setTitle(getString(R.string.ficname));

        alert.setView(edittext);
        edittext.setText(R.string.listeopp);

        alert.setPositiveButton(R.string.valider, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String name = edittext.getText().toString();
                EtatsUtils.createandDisplayPdfEtat(i,name,EtatActivity.this,dateDebut,dateFin);
            }
        });

        alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        final AlertDialog alertdialog = alert.create();
        alertdialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
            }
        }) ;
        alertdialog.show();
    }



    public class ParametresAdapter extends BaseAdapter {

        int nbr = 4 ;

        public ParametresAdapter(){

        }

        @Override
        public int getCount() {
            return nbr ;
        }

        @Override
        public String getItem(int position) {
            return null ;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                convertView  = getLayoutInflater().inflate(R.layout.parametresitem, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }

            switch (position){
                case 0 : {
                    holder.libelle.setText(R.string.resultat);
                    //holder.icon.setImageResource(R.mipmap.ic_cat);
                } break;
                case 1 : {
                    holder.libelle.setText(R.string.chiffreaffaire);
                    //holder.icon.setImageResource(R.mipmap.ic_pays);
                } break;
                case 2 : {
                    holder.libelle.setText(R.string.listeachat);
                    //holder.icon.setImageResource(R.mipmap.ic_pays);
                } break;
                case 3 : {
                    holder.libelle.setText(R.string.fichedestock);
                    //holder.icon.setImageResource(R.mipmap.ic_pays);
                } break;
                case 4 : {
                    holder.libelle.setText(R.string.listepartenaire);
                    //holder.icon.setImageResource(R.mipmap.ic_pays);
                } break;
            }

            return convertView;
        }

    }

    static class ViewHolder{
        ImageView icon ;
        TextView libelle ;
        public ViewHolder(View v) {
            icon = (ImageView)v.findViewById(R.id.icon);
            libelle = (TextView)v.findViewById(R.id.libelleTV);
        }
    }
}
