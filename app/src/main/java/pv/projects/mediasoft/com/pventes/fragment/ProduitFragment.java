package pv.projects.mediasoft.com.pventes.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.activities.AccueilActivity;
import pv.projects.mediasoft.com.pventes.activities.DepenseFormActivity;
import pv.projects.mediasoft.com.pventes.activities.ProduitActivity;
import pv.projects.mediasoft.com.pventes.activities.ProduitFormActivity;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.database.ProduitHelper;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.utiles.Url;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProduitFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProduitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProduitFragment extends ListFragment {
    public static final String TAG = "ProduitFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LayoutInflater mInflater;
    AllProduitAdapter adapter = null ;
    ArrayList<Produit> produits = null;
    private ProduitDAO produitDAO;
    GetAllProduitTask getAllProduitTask = null ;
    ArrayList<Integer> selectedItems = null ;
    private ProduitActivity mParent = null;

    final static int PROGRESS_DIALOG_ID = 0 ;
    Produit produit = null ;

    LayoutInflater inflater = null ;
    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH) ;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VenteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProduitFragment newInstance(String param1, String param2) {
        ProduitFragment fragment = new ProduitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProduitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_produit, container, false);

        mInflater = inflater ;
        selectedItems = new ArrayList<Integer>();
        init();

        return  v ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (adapter.getSelectedCount()==0){
                    adapter.toggleSelection(position);
                    mListener.onFragmentInteraction(adapter.getSelectedCount());
                }
                return true;
            }
        });*/
    }

    private void init() {
        produitDAO = new ProduitDAO(getActivity()) ;

        getAllProduitTask = new GetAllProduitTask() ;
        getAllProduitTask.execute() ;
    }



    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);
        /*if (adapter.getSelectedCount()>0){
            adapter = (AllProduitAdapter) getListAdapter();
            adapter.toggleSelection(position);

            mListener.onFragmentInteraction(adapter.getSelectedCount());
        }
        else{
            adapter = (AllProduitAdapter) getListAdapter();
            produit = adapter.getItem(position);
            mListener.onFragmentInteraction(-1);
        }*/

        final MouvementDAO mouvementDAO = new MouvementDAO(getActivity()) ;

        final CharSequence[] items = {getString(R.string.modif), getString(R.string.delete), getString(R.string.annuler)};

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
                    if (mouvementDAO.getManyByProduct(adapter.getItem(position).getId_externe()).size()==0)  deleteItem(position);
                    else Toast.makeText(getActivity(), R.string.produitdelete, Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mParent = (ProduitActivity) activity ;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mParent = null ;
        mListener = null;
    }

    public void interval(String dateDebut, String dateFin) {

        getAllProduitTask = new GetAllProduitTask(dateDebut,dateFin) ;
        getAllProduitTask.execute() ;
        mParent.setTitle(dateDebut + " - " + dateFin);
    }

    public Produit getProduit() {
        return produit;
    }

    public ArrayList<Produit> getProduits() {
        return  produits ;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(int uri);
    }

    public class ViewHolder{
        // each data item is just a string in this case
        public TextView code;
        public TextView libelle;
        public TextView prixV;
        public TextView prixA;
        public ImageView imageView;
        public View stateTV;

        public ViewHolder(View v) {
            code = (TextView)v.findViewById(R.id.codeTV);
            libelle = (TextView)v.findViewById(R.id.libelleTV);
            prixV = (TextView)v.findViewById(R.id.prixVTV);
            imageView = (ImageView)v.findViewById(R.id.image);
            prixA = (TextView)v.findViewById(R.id.prixATV);
        }
    }


    public void destroyActionMode(){
        adapter.removeSelection() ;
    }


    public void deleteItem(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
        builder.setTitle(getString(R.string.app_name)) ;
        builder.setMessage(getString(R.string.delconfirm)) ;
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                Produit produit = adapter.getItem(position);
                adapter.toggleSelection(position);
                adapter.deleteItems() ;
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

        if (Utiles.isConnected(getActivity())) alertdialog.show();
        else Toast.makeText(getActivity(), R.string.noconnexion, Toast.LENGTH_SHORT).show();

    }

    public void refresh(){
        init();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }



    public void filtrer(String query){
        Produit produit = null ;
        ArrayList<Produit> data = new ArrayList<Produit>() ;

        if(produits != null)
            for(int i = 0 ; i < produits.size() ; i++){
                produit = produits.get(i) ;
                if(produit.getLibelle().toLowerCase().contains(query)|| produit.getCode().toLowerCase().contains(query) ) data.add(produit) ;
            }
        adapter = new AllProduitAdapter(data) ;
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    public class AllProduitAdapter extends BaseAdapter {


        ArrayList<Produit> produits = null ;
        SparseBooleanArray mSelectedItemsIds;


        public AllProduitAdapter(ArrayList<Produit> produits1){
            mSelectedItemsIds = new SparseBooleanArray();
            produits = produits1 ;
        }


        public void toggleSelection(int position)
        {
            selectView(position, !mSelectedItemsIds.get(position));
        }


        public void deleteItems(){
            ArrayList<Produit> produits = new ArrayList<Produit>() ;

            for (int i = 0; i< mSelectedItemsIds.size(); i++) {
                produits.add(getItem(mSelectedItemsIds.keyAt(i))) ;
            }

            DelProduitTask task = new DelProduitTask(produits) ;
            task.execute(Url.getDeleteProduit(getActivity())) ;
            removeSelection();
        }


        public void selectView(int position, boolean value)
        {
            if(value)
                mSelectedItemsIds.put(position, value);
            else
                mSelectedItemsIds.delete(position);

            notifyDataSetChanged();
        }

        public int getSelectedCount() {
            return mSelectedItemsIds.size();// mSelectedCount;
        }

        public SparseBooleanArray getSelectedIds() {
            return mSelectedItemsIds;
        }

        public void removeSelection() {
            mSelectedItemsIds = new SparseBooleanArray();
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            if (produits != null)  return produits.size() ;
            else return  0 ;
        }

        @Override
        public Produit getItem(int position) {
            if (produits.get(position) != null)return produits.get(position) ;
            else  return null ;
        }

        @Override
        public long getItemId(int position) {
            if (produits.get(position) != null)return produits.get(position).getId();
            return -1 ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.produititem, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            Produit produit = (Produit) getItem(position);


            if(produit != null) {
                Log.e("Code", produit.getCode()) ;
                holder.code.setText("Code: " + produit.getCode());
                holder.libelle.setText(produit.getLibelle());
                holder.prixV.setText("Prix V: " + String.valueOf(produit.getPrixV()));
                holder.prixA.setText("Prix A: " + String.valueOf(produit.getPrixA()));

                //String path = Url.getImageDirectory(getActivity(), produit.getImage()) ;
                //Log.e("WEB PATH", path) ;

                if (produit.getImage()!=null && !produit.getImage().equals("")) {
                    Log.e("IMAGE",produit.getImage()) ;
                    if (produit.getImage().contains("/")) loadLocalImage(produit.getImage(),holder.imageView);
                    else loadWebImage(holder.imageView, produit);
                }
                else holder.imageView.setImageResource(R.mipmap.ic_produit);
            }
            CardView cardView = (CardView) convertView ;
            cardView.setCardBackgroundColor(mSelectedItemsIds.get(position) ? getResources().getColor(R.color.my_primary_light) : getResources().getColor(android.R.color.background_light));
            return convertView;
        }
    }





    public class GetAllProduitTask extends AsyncTask<Void, Integer, Boolean> {
        Date dateDebut = null ;
        Date dateFin = null ;
        boolean data = false ;
        Calendar calendar = Calendar.getInstance() ;

        public GetAllProduitTask(){

        }

        public GetAllProduitTask(boolean d){
            data = d ;
        }

        public  GetAllProduitTask(String dateDebut, String dateFin){
            data = true ;
            setDateDebut(dateDebut);
            setDateFin(dateFin);
        }

        public void setDateDebut(String dd) {
            dateDebut = java.sql.Date.valueOf(dd);
            //Log.e("TEST", formatter.format(this.dateDebut)) ;
        }

        public void setDateFin(String df) {
            dateFin = java.sql.Date.valueOf(df) ;
            //Log.e("TEST", formatter.format(this.dateFin)) ;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (data){
                dateDebut = calendar.getTime();
                dateFin = calendar.getTime();
            }

            if (mParam1.equals("1"))produits = produitDAO.getInterval(dateDebut, dateFin) ;
            else if (mParam1.equals("2"))produits = produitDAO.getRecurrentInterval(null,null) ;
            else if (mParam1.equals("3"))produits = produitDAO.getNonSyncInterval(dateDebut, dateFin) ;
            if (produits.size()==0) return false ;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean){
                produitDAO = null ;
                //Toast.makeText(getActivity(), getString(R.string.produit_empty), Toast.LENGTH_LONG).show();
            }

            adapter = new AllProduitAdapter(produits) ;
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();

            dateDebut = null ;
            dateFin = null ;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            produitDAO = new ProduitDAO(mParent) ;
        }
    }


    public class DelProduitTask extends AsyncTask<String , Integer, Integer>{
        ArrayList<Produit> mProduits = null ;

        public DelProduitTask(ArrayList<Produit> produits1){
            this.mProduits = produits1;
        }

        @Override
        protected Integer doInBackground(String... url) {

            FormBody.Builder formBuilder = null ;

            int res = 0 ;
            for (int i = 0; i< mProduits.size(); i++) {
                formBuilder = new FormBody.Builder() ;
                formBuilder.add(ProduitHelper.ID_EXTERNE, String.valueOf(mProduits.get(i).getId_externe()));

                Log.e("URL",url[0]) ;

                String result = "" ;


                try {
                    result = Utiles.POST(url[0],formBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("REPONSEEEEEEEEEEEEEEEE", result);

                if (result.contains("OK:")){
                    produitDAO.delete(mProduits.get(i).getId()) ;
                    mProduits.get(i) ;
                    res++ ;
                }
            }

            return res;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            try{
                getActivity().dismissDialog(ProduitActivity.PROGRESS_DIALOG_ID);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            if (result.intValue() > 0) {
                Toast.makeText(getActivity(), String.valueOf(result.intValue()) + " " + getResources().getString(R.string.produit_suprimmer), Toast.LENGTH_LONG).show();
                refresh();
            }
            else Toast.makeText(getActivity(), getString(R.string.echec_del), Toast.LENGTH_LONG).show();
            mParent.dissmisAlert();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                getActivity().showDialog(ProduitActivity.PROGRESS_DIALOG_ID);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
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
                        if (getActivity()!=null)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap1);
                                // progressBar.setVisibility(View.GONE);
                            }
                        });
                        try {
                            uri[0] = Utiles.saveImageExternalStorage(bitmap1, getActivity(), produit.getImage(), Utiles.PV_PRODUIT_IMAGE_DIR);
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
                if (getActivity()!=null)
                getActivity().runOnUiThread(new Runnable() {
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


        if (getActivity()!=null){
            String path = Url.getImageDirectory(getActivity(), produit.getImage()) ;
            Log.e("WEB PATH",path) ;
            Picasso.with(getActivity()).load(path).into(target);
        }
    }



    public void loadLocalImage(final String name, final ImageView imageView) {
        String dossier = Utiles.PV_PRODUIT_IMAGE_DIR ;

        File file = new File(name) ;
        imageView.setImageURI(Uri.fromFile(file));

        Log.e("DEBUG", "LOD1") ;

    }

}
