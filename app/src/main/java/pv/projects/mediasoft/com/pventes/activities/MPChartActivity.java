package pv.projects.mediasoft.com.pventes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pv.projects.mediasoft.com.pventes.MainActivity;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Produit;

public class MPChartActivity extends AppCompatActivity {


    private PieChart pieChart;
    private int i;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpchart);

        setupToolbar();

        Intent intent = getIntent() ;

        if (intent!=null){
            i = intent.getIntExtra("type",0) ;
            switch (i){
                case 0 : pieChart();break;
                case 1 : barCHart();break;
            }
        }

    }


    private void pieChart() {



        OperationDAO operationDAO = new OperationDAO(this) ;
        float size = operationDAO.getAll().size() ;
        pieChart = (PieChart) findViewById(R.id.piechart);

        ArrayList<Entry> entries = new ArrayList<>();
        double value = operationDAO.getTotal(1,null,new Date()) ;
        entries.add(new Entry((float) value, 0));
        value = operationDAO.getTotal(2,null,new Date()) ;
        entries.add(new Entry((float) value, 1));
        value = operationDAO.getTotal(3,null,new Date()) ;
        entries.add(new Entry((float) value, 2));
        value = operationDAO.getTotal(4,null,new Date()) ;
        entries.add(new Entry((float) value, 3));
        value = operationDAO.getTotal(5,null,new Date()) ;
        entries.add(new Entry((float) value, 4));

        ArrayList<String> labels = new ArrayList<String>();
        String[] xData = {getString(R.string.vente), getString(R.string.depense), getString(R.string.cmdfr),getString(R.string.cmdclt),getString(R.string.achat)};
        labels.add(getString(R.string.vente));
        labels.add(getString(R.string.depense));
        labels.add(getString(R.string.cmdfr));
        labels.add(getString(R.string.cmdclt));
        labels.add(getString(R.string.achat));


        PieDataSet dataset = new PieDataSet(entries, "");
        pieChart.setDescription("Description");
        PieData data = new PieData(labels, dataset); // initialize Piedata<br />
        pieChart.setData(data);

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);


        // customize legends
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        pieChart.setVisibility(View.VISIBLE);
    }



    private void barCHart() {

        OperationDAO operationDAO = new OperationDAO(this) ;
        float size = operationDAO.getAll().size() ;
        barChart = (BarChart) findViewById(R.id.barchart);

        ArrayList<Entry> entries = new ArrayList<>();
        double value = operationDAO.getTotal(1,null,new Date()) ;

        BarData data = new BarData(getXAxisValues(), getDataSet());
        barChart.setData(data);
        barChart.setDescription("My Chart");
        barChart.invalidate();



        // customize legends
        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        barChart.setVisibility(View.VISIBLE);
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
        getMenuInflater().inflate(R.menu.menu_mpchart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {

            switch (i){
                case 0 : pieChart.saveToGallery(getString(R.string.oper)+DAOBase.formatter.format(new Date()),100);break;
                case 1 : barChart.saveToGallery(getString(R.string.oper)+DAOBase.formatter.format(new Date()),100);break;
            }

            Toast.makeText(MPChartActivity.this, R.string.save_gall, Toast.LENGTH_SHORT).show();


            /*
            final CharSequence[] items = {getString(R.string.gallery), getString(R.string.delete), getString(R.string.annuler)};

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.action));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals(getString(R.string.modif))) {
                        Produit p = adapter.getItem(position) ;
                        Intent intent = new Intent(getActivity(), ProduitFormActivity.class) ;
                        intent.putExtra("ID",p.getId());
                        startActivity(intent);

                    } else if (items[item].equals(getString(R.string.delete))) {
                        deleteItem(position);
                    }
                    else {
                        dialog.dismiss();
                    }
                }
            });
final AlertDialog alertdialog = builder.create();
            alertdialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                    alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
                }
            }) ;
            alertdialog.show();
            */

            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        OperationDAO operationDAO = new OperationDAO(this) ;
        float size = operationDAO.getAll().size() ;
        double value = 0 ;
        for (int i = 1; i<=12; ++i){
            String mois = "" ;
            if (i<=9) mois = "0"+i ;
            else mois = String.valueOf(i) ;

            value = operationDAO.getAll(1,mois,"2016") ;
            BarEntry v1e1 = new BarEntry((float) value, 0);
            valueSet1.add(v1e1);

            value = operationDAO.getAll(2,mois,"2016") ;
            BarEntry v2e1 = new BarEntry((float) value, 0);
            valueSet2.add(v2e1);
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, getString(R.string.vente));
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, getString(R.string.depense));

        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        xAxis.add("JUI");
        xAxis.add("AOUT");
        xAxis.add("SEP");
        xAxis.add("OCT");
        xAxis.add("NOV");
        xAxis.add("DEC");
        return xAxis;
    }


}
