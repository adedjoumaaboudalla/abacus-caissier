package pv.projects.mediasoft.com.pventes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import pv.projects.mediasoft.com.pventes.R;

public class StatistiqueActivity extends AppCompatActivity {

    private SharedPreferences preferences = null;
    private Toolbar mToolbar;

    private ListView liste = null;
    private ParametresAdapter adapter = null;

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

    private void initialisation() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        liste = (ListView) findViewById(R.id.liste);
        adapter = new ParametresAdapter() ;
        liste.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(this) ;

        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 : {
                        Intent intent = new Intent(StatistiqueActivity.this,MPChartActivity.class) ;
                        intent.putExtra("type",0) ;
                        startActivity(intent);
                    }
                    break;
                    case 1 : {
                        Intent intent = new Intent(StatistiqueActivity.this,MPChartActivity.class) ;
                        intent.putExtra("type",1) ;
                        startActivity(intent);
                    } break;
                }
            }
        });
    }


    public class ParametresAdapter extends BaseAdapter {

        int nbr = 2 ;

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
                    holder.libelle.setText(R.string.pourcop);
                    //holder.icon.setImageResource(R.mipmap.ic_cat);
                } break;
                case 1 : {
                    holder.libelle.setText(R.string.listeopparmoi);
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
