package pv.projects.mediasoft.com.pventes.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zj.btsdk.BluetoothService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.activities.AccueilActivity;
import pv.projects.mediasoft.com.pventes.activities.AchatActivity;
import pv.projects.mediasoft.com.pventes.activities.CommandeActivity;
import pv.projects.mediasoft.com.pventes.activities.DeviseListActivity;
import pv.projects.mediasoft.com.pventes.activities.PartenaireActivity;
import pv.projects.mediasoft.com.pventes.activities.ProduitListeActivity;
import pv.projects.mediasoft.com.pventes.activities.ScannerActivity;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;
import pv.projects.mediasoft.com.pventes.dao.CommercialDAO;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseOperationDAO;
import pv.projects.mediasoft.com.pventes.dao.JournalDAO;
import pv.projects.mediasoft.com.pventes.dao.ModePayementDAO;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.dao.TypeOperationDAO;
import pv.projects.mediasoft.com.pventes.model.Commercial;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.Devise;
import pv.projects.mediasoft.com.pventes.model.DeviseOperation;
import pv.projects.mediasoft.com.pventes.model.Journal;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.model.TypeOperation;
import pv.projects.mediasoft.com.pventes.utiles.PrintP800;
import pv.projects.mediasoft.com.pventes.utiles.PrintPDA;
import pv.projects.mediasoft.com.pventes.utiles.PrintPDAMobiPrint3;
import pv.projects.mediasoft.com.pventes.utiles.PrinterUtils;
import pv.projects.mediasoft.com.pventes.utiles.PrinterUtilsGrand;
import pv.projects.mediasoft.com.pventes.utiles.Url;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;

import static pv.projects.mediasoft.com.pventes.fragment.VenteplusFormFragment.DEVISE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommandeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommandeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommandeFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int CHOOSE_PRODUIT_REQUEST = 1;
    public static final String PRODUIT = "produit";
    public static final int CHOOSE_CODE_BAR_REQUEST = 2;
    public static final int CHOOSE_PARTENAIRE_REQUEST = 3;
    public static final String PARTENAIRE = "partenaire";
    public static final String TAG = "CommandeFragment";
    private static final int CHOOSE_DEVISE_BAR_REQUEST = 5;
    private static String MODE = "ESPECE";
    private static final String ESPECE = "ESPECE";
    private static final String CHEQUE = "CHEQUE";
    private static final String VIREMENT = "VIREMENT";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LayoutInflater mInflater;
    private ListeProduitAdapter racourciAdapter;


    ArrayList<Produit> produits = null;
    EditText et_prix = null ;
    EditText et_quantite = null ;
    EditText et_produit = null ;
    EditText et_remise = null ;
    EditText et_mtrecue = null ;
    EditText et_mtverse = null ;
    EditText et_client = null ;
    TextView et_ttc = null ;
    TextView et_qt = null ;


    private ScrollView sc = null ;
    private AlertDialog.Builder builder;
    private Button total =null ;
    private Button init =null ;
    private Button close = null;
    private RelativeLayout rv = null ;



    RelativeLayout dixmille = null ;
    RelativeLayout cinqmille = null ;
    RelativeLayout deuxmille = null ;
    RelativeLayout mille = null ;
    RelativeLayout cinqcent = null ;
    RelativeLayout deuxcentcinquante = null ;
    RelativeLayout deuxcent = null ;
    RelativeLayout cent = null ;
    RelativeLayout vingcinq = null ;
    RelativeLayout cinquante = null ;



    int position = -1 ;
    private Activity mParent = null;
    private ImageButton btn_parcourir;
    private Produit mProduit;
    private TextView et_netaPayer;
    private TextView et_arendre;
    private double ancienPrixTotal = 0;
    private ImageButton btn_racourcis = null;
    private GridView gridView;

    private double prixTotal = 0 ;
    private OperationDAO operationDAO;
    private MouvementDAO mouvementDAO;
    private SharedPreferences preferences;
    private ListView lv;
    OperationProduitAdapter adapter = null ;
    private ArrayList<Mouvement> mouvements;
    private ProduitDAO produitDAO;
    private Button attente;
    private Button credit;
    private Button comptant;
    private Operation operation;
    private EditText dateecheancier;
    private Commercial commercial;
    private Date d;
    private ArrayList<Commercial> commercials;
    private AlertDialog alert;
    private ImageButton dateBtn;
    private AlertDialog.Builder dateBox;
    private int id;
    private Partenaire partenaire;
    private ArrayAdapter mAdapter;
    private Spinner spinner_banque;
    private long cpt_bk = 0 ;
    private ArrayList<CompteBanque> cb;
    private EditText numcheque;
    private String numerocheque = "";
    private ArrayList<DeviseOperation> deviseOperations;
    private DeviseDAO deviseDAO;
    private boolean achat = false;
    private EditText et_description = null;
    private BluetoothService mService;

    public CommandeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommandeFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static CommandeFragment newInstance(String param1, String param2) {
        CommandeFragment fragment = new CommandeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_commande, container, false);
        mInflater = inflater ;


        produitDAO = new ProduitDAO(getActivity()) ;
        deviseDAO = new DeviseDAO(getActivity()) ;
        operationDAO = new OperationDAO(getActivity()) ;
        mouvementDAO = new MouvementDAO(getActivity()) ;

        produits = new ArrayList<Produit>() ;
        deviseOperations = new ArrayList<DeviseOperation>() ;
        produits = produitDAO.getRecurrentInterval(null,null) ;

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()) ;
        lv = (ListView) v.findViewById(R.id.listeView);
        mouvements = new ArrayList<>() ;
        adapter = new OperationProduitAdapter(mouvements) ;
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                position = pos ;
                Mouvement mouvement = adapter.getItem(position) ;
                showMouvement(mouvement) ;
            }
        });

        d= new Date() ;

        et_qt = (TextView) v.findViewById(R.id.qt);
        et_ttc = (TextView) v.findViewById(R.id.ttc);
        attente = (Button) v.findViewById(R.id.attente);
        credit = (Button) v.findViewById(R.id.credit);
        comptant = (Button) v.findViewById(R.id.comptant);

        comptant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComptant() ;
            }
        });


        mService = new BluetoothService(getActivity(), mHandler);

        return v ;
    }



    @Override
    public void onStart() {
        super.onStart();

        //À¶ÑÀÎ´´ò¿ª£¬´ò¿ªÀ¶ÑÀ
        if( mService.isBTopen() == false  && preferences.getBoolean("imprimenteexterne",true))
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 112);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) mService.stop();
        mService = null;
    }


    /**
     * ´´½¨Ò»¸öHandlerÊµÀý£¬ÓÃÓÚ½ÓÊÕBluetoothServiceÀà·µ»Ø»ØÀ´µÄÏûÏ¢
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //ÒÑÁ¬½Ó
                            //Toast.makeText(context, "Connect successful",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_CONNECTING:  //ÕýÔÚÁ¬½Ó
                            Log.d("À¶ÑÀµ÷ÊÔ","ÕýÔÚÁ¬½Ó.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //¼àÌýÁ¬½ÓµÄµ½À´
                        case BluetoothService.STATE_NONE:
                            Log.d("À¶ÑÀµ÷ÊÔ","µÈ´ýÁ¬½Ó.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //À¶ÑÀÒÑ¶Ï¿ªÁ¬½Ó
                    //Toast.makeText(getApplicationContext(), "Device connection was lost",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸
                    //Toast.makeText(getApplicationContext(), "Unable to connect device",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };




    private void showMouvement(final Mouvement mouvement) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
        builder.setTitle(mouvement.getProduit()) ;
        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.mouvementform,null);
        builder.setView(linearLayout) ;
        et_prix = (EditText) linearLayout.findViewById(R.id.prix);
        et_quantite = (EditText) linearLayout.findViewById(R.id.quantite);
        et_description = (EditText) linearLayout.findViewById(R.id.description);
        Button valider = (Button) linearLayout.findViewById(R.id.valider);
        Button annuler = (Button) linearLayout.findViewById(R.id.annuler);

        Produit produit = produitDAO.getOneByIdExterne(mouvement.getProduit_id()) ;
        if (produit==null) produit = produitDAO.getOne(mouvement.getProduit_id()) ;
        if (produit.getModifiable()==0) et_prix.setEnabled(false);
        if (mParam1.equals(CommandeActivity.CLIENT)) et_prix.setText(String.valueOf(mouvement.getPrixV()));
        else  et_prix.setText(String.valueOf(mouvement.getPrixA()));
        et_quantite.setText(String.valueOf(mouvement.getQuantite()));
        et_description.setText(mouvement.getDescription());

        final AlertDialog alertDialog = builder.show() ;
        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduitOperation(alertDialog, mouvement.getProduit_id()) ;
            }
        });
    }

    private boolean isValid() {
        if (et_quantite.getText().length() == 0) return false ;
        if (et_prix.getText().length() == 0) return false ;
        double a = Double.valueOf(et_prix.getText().toString()) ;
        double b = Double.valueOf(et_quantite.getText().toString()) ;
        if (a==0) return false ;
        if (b==0) return false ;
        return true;
    }


    private void updateProduitOperation(AlertDialog alertDialog, long id) {
        if (isValid()){
            prixTotal-=ancienPrixTotal ;
            et_ttc.setText(Utiles.formatMtn(prixTotal));
            double remise = 0 ;
            if (et_remise!=null && !et_remise.getText().toString().equals("")) remise = Double.parseDouble(et_remise.getText().toString()) ;
            if (et_netaPayer!=null) et_netaPayer.setText(Utiles.formatMtn2(prixTotal-remise));

            ancienPrixTotal = 0 ;
            // Mettre a jour la quantité du produit en cours dans la liste des produit de ce formulaire
            if (id!=-1) updateProduitEcrasement(id);

            adapter.updateItem(position) ;
            alertDialog.dismiss();
            refresh(mouvements);
        }
        else
            Toast.makeText(getActivity(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();

    }


    // ecraser dans la liste des produit du formulaire la quantite du produit à mettre à jour
    private void updateProduitEcrasement(long id) {

        for (int i = 0; i < produits.size(); ++i){
            if (produits.get(i).getId_externe() == id){
                Produit produit = produits.get(i) ;
                produit.setNbre(Double.parseDouble(et_quantite.getText().toString()));
                produits.set(i,produit) ;
            }
        }
    }


    private void addComptant() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
        builder.setTitle(getActivity().getString(R.string.rglmntcmpt)) ;
        ScrollView scrollView = (ScrollView) getActivity().getLayoutInflater().inflate(R.layout.comptantform,null);
        builder.setView(scrollView) ;
        et_client = (EditText) scrollView.findViewById(R.id.client);
        et_remise = (EditText) scrollView.findViewById(R.id.remise);
        et_netaPayer = (TextView) scrollView.findViewById(R.id.net);
        Button buttonDevise = (Button) scrollView.findViewById(R.id.devise);
        et_mtrecue = (EditText) scrollView.findViewById(R.id.recu);
        et_mtverse = (EditText) scrollView.findViewById(R.id.verser);
        et_arendre = (TextView) scrollView.findViewById(R.id.rendre);
        et_description = (EditText) scrollView.findViewById(R.id.description);

        spinner_banque = (Spinner) scrollView.findViewById(R.id.banque);
        RadioGroup modegroupe = (RadioGroup) scrollView.findViewById(R.id.modegroup);
        final LinearLayout lignecheque = (LinearLayout) scrollView.findViewById(R.id.lignecheque);
        numcheque = (EditText) scrollView.findViewById(R.id.numcheque);
        Button money1 = (Button) scrollView.findViewById(R.id.money1);
        Button money2 = (Button) scrollView.findViewById(R.id.money2);

        if (deviseDAO.getReference()!=null){
            money1.setText(deviseDAO.getReference().getCodedevise());
            money2.setText(deviseDAO.getReference().getCodedevise());
        }

        money1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_mtrecue.requestFocus() ;
                showMoney(v);
            }
        });

        money2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_remise.requestFocus() ;
                showMoney(v);
            }
        });




        buttonDevise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDevise() ;
            }
        });



        modegroupe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                lignecheque.setVisibility(View.GONE);
                if (checkedId == R.id.espece) {
                    spinner_banque.setVisibility(View.GONE);
                    MODE = ESPECE ;
                }
                else  {
                    CompteBanqueDAO compteBanqueDAO = new CompteBanqueDAO(getActivity()) ;
                    ArrayList<String> banque = new ArrayList<String>();


                    if (checkedId == R.id.cheque){
                        MODE = CHEQUE ;
                        lignecheque.setVisibility(View.VISIBLE);
                        cb = compteBanqueDAO.getCheque();
                        for (int i = 0; i < cb.size(); i++) {
                            banque.add(cb.get(i).getLibelle() + " - " + cb.get(i).getCode() );
                        }
                    }

                    else if (checkedId == R.id.cartebanque){
                        MODE = VIREMENT ;
                        cb = compteBanqueDAO.getCarteBanque();
                        for (int i = 0; i < cb.size(); i++) {
                            banque.add(cb.get(i).getLibelle() + " - " + cb.get(i).getCode() );
                        }
                    }


                    banque.add(0,getString(R.string.choosebank));

                    mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, banque);
                    spinner_banque.setAdapter(mAdapter);
                    spinner_banque.setSelection(0);
                    spinner_banque.setVisibility(View.VISIBLE);
                }
            }
        });



        CompteBanqueDAO compteBanqueDAO = new CompteBanqueDAO(getActivity()) ;
        cb = compteBanqueDAO.getAll();
        ArrayList<String> banque = new ArrayList<String>();
        for (int i = 0; i < cb.size(); i++) {
            banque.add(cb.get(i).getLibelle() + " - " + cb.get(i).getCode() );
        }

        banque.add(0,getString(R.string.choosebank));

        mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, banque);
        spinner_banque.setAdapter(mAdapter);
        spinner_banque.setSelection(0);


        Button valider = (Button) scrollView.findViewById(R.id.valider);
        Button annuler = (Button) scrollView.findViewById(R.id.annuler);
        ImageButton clt = (ImageButton) scrollView.findViewById(R.id.clt);


        if (operation!=null) et_client.setText(operation.getClient());

        et_remise.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_netaPayer.setText(String.valueOf(prixTotal));
                if (s.length()>0){
                    double remise = Double.parseDouble(s.toString()) ;
                    double netapayer = 0;
                    double recu = 0;

                    if (prixTotal-remise>=0) et_netaPayer.setText(Utiles.formatMtn2(prixTotal-remise));
                    else {
                        try {
                            Toast.makeText(getActivity(), R.string.remiseincorrectte,Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        et_netaPayer.setText(Utiles.formatMtn2(prixTotal));
                    }

                    if (et_netaPayer.getText().length()>0) netapayer = Double.parseDouble(et_netaPayer.getText().toString()) ;
                    if (et_mtrecue.getText().length()>0) recu =  Double.parseDouble(et_mtrecue.getText().toString()) ;

                    if (recu-netapayer>=0) et_arendre.setText(Utiles.formatMtn2(recu-netapayer));
                    else et_arendre.setText("0");
                }
            }
        });

        et_mtrecue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0){
                    double netapayer = 0;
                    double recu = 0;

                    if (et_netaPayer.getText().length()>0) netapayer = Double.parseDouble(et_netaPayer.getText().toString()) ;
                    if (et_mtrecue.getText().length()>0) recu =  Double.parseDouble(et_mtrecue.getText().toString()) ;

                    if (recu-netapayer>=0) et_arendre.setText(Utiles.formatMtn2(recu-netapayer));
                    else {
                        et_arendre.setText("0");
                    }
                }
            }
        });

        et_netaPayer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0){
                    double netapayer = 0;
                    double recu = 0;

                    if (et_netaPayer.getText().length()>0) netapayer = Double.parseDouble(s.toString()) ;
                    if (et_mtrecue.getText().length()>0) recu =  Double.parseDouble(et_mtrecue.getText().toString()) ;

                    if (recu-netapayer>=0) et_arendre.setText(Utiles.formatMtn2(recu-netapayer));
                    else et_arendre.setText("0");
                }
            }
        });


        if (adapter.getCount() == 0) {
            Toast.makeText(getActivity(), R.string.selectproduct, Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(getActivity(), Utiles.formatMtn2(prixTotal), Toast.LENGTH_SHORT).show();
        et_netaPayer.setText(Utiles.formatMtn2(prixTotal));
        final AlertDialog alertDialog = builder.show() ;
        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOperation(false, alertDialog);
            }
        });

        clt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PartenaireActivity.class) ;
                intent.putExtra("CHOICE",true) ;
                if (mParam1.equals(AchatActivity.ACHAT))intent.putExtra("CLT",false) ;
                else intent.putExtra("CLT",true) ;
                startActivityForResult(intent, CommandeFragment.CHOOSE_PARTENAIRE_REQUEST);
            }
        });
    }

    private void goToDevise() {
        Intent intent = new Intent(getActivity(), DeviseListActivity.class) ;
        intent.putParcelableArrayListExtra(DEVISE,deviseOperations) ;
        intent.putExtra("MTN",prixTotal) ;
        Log.e("TTC", String.valueOf(prixTotal)) ;
        startActivityForResult(intent,CHOOSE_DEVISE_BAR_REQUEST);
    }


    public void addOperation(final boolean acredit,AlertDialog alertDialog ) {
        double remise = 0 ;
        if (et_remise != null && !et_remise.getText().toString().equals("")) remise = Double.parseDouble(et_remise.getText().toString()) ;

        if (adapter.getCount()>0){
            double recu = 0 ;
            double verser = 0 ;
            if (et_mtrecue.getText().length()>0) recu = Double.parseDouble(et_mtrecue.getText().toString()) ;
            if (et_mtverse.getText().length()>0) verser = Double.parseDouble(et_mtverse.getText().toString()) ;

            if (spinner_banque.getSelectedItemPosition()==0 && spinner_banque.getVisibility()==View.VISIBLE && cb.size()>0) {
                Toast.makeText(getActivity(), getString(R.string.baquerror), Toast.LENGTH_LONG).show();
                return ;
            }
            else if (cb.size()==0) cpt_bk = 0 ;
            else if (spinner_banque.getVisibility()!=View.VISIBLE) cpt_bk = 0 ;
            else {
                cpt_bk = cb.get(spinner_banque.getSelectedItemPosition()-1).getId_externe() ;
                numerocheque = numcheque.getText().toString() ;
            }

            if (MODE.equals(CHEQUE) && numerocheque.length()==0){
                Toast.makeText(getActivity(), getString(R.string.saisirnumcheque), Toast.LENGTH_LONG).show();
                return;
            }

            if(remise>prixTotal){
                Toast.makeText(getActivity(), getString(R.string.data_errors4), Toast.LENGTH_LONG).show();
                return;
            }
            else if(verser < 0  && acredit){
                Toast.makeText(getActivity(), getString(R.string.data_errors7), Toast.LENGTH_LONG).show();
                return;
            }
            else if (acredit && dateecheancier.getText().length()<=0){
                Toast.makeText(getActivity(), getString(R.string.dateecheance), Toast.LENGTH_LONG).show();
                return;
            }
            else{
                //Log.e("MTN VERSE AVT",et_mtverse.getText().toString()) ;
                if (!preferences.getBoolean("commercial",false)){
                    AddOperationTask task = new AddOperationTask(et_remise.getText().toString(),et_client.getText().toString(),et_mtrecue.getText().toString(),et_mtverse.getText().toString(),acredit,false,cpt_bk,numerocheque, et_description.getText().toString());
                    task.execute();
                }
                else if (mParam1.equals(AchatActivity.ACHAT)) {
                    AddOperationTask task = new AddOperationTask(et_remise.getText().toString(),et_client.getText().toString(),et_mtrecue.getText().toString(),et_mtverse.getText().toString(),acredit,false,cpt_bk,numerocheque, et_description.getText().toString());
                    task.execute();
                }
                else if (preferences.getBoolean("commercial",false) &&  operation == null && mParam1.equals(CommandeActivity.CLIENT)){
                    CommercialDAO commercialDAO = new CommercialDAO(getActivity()) ;

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
                    LinearLayout ll = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.comercialchoose,null);

                    ListView lv = (ListView) ll.findViewById(R.id.commercialliste);
                    commercials = commercialDAO.getAll() ;
                    ArrayList<String> liste = new ArrayList<>() ;
                    for (int i = 0; i < commercials.size(); i++) {
                        liste.add(commercials.get(i).getNom() + " "+ commercials.get(i).getPrenom()) ;
                    }
                    final ArrayAdapter commercialAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, liste);
                    lv.setAdapter(commercialAdapter);
                    builder.setTitle(getActivity().getString(R.string.choosecommercial)) ;
                    builder.setView(ll) ;

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            commercial = (Commercial) commercials.get(position);
                            AddOperationTask task = new AddOperationTask(et_remise.getText().toString(),et_client.getText().toString(),et_mtrecue.getText().toString(),et_mtverse.getText().toString(),acredit,false, cpt_bk,numerocheque, et_description.getText().toString());
                            task.execute();
                            alert.dismiss();
                        }
                    });

                    if (commercialDAO.getAll().size()>1) alert = builder.show() ;
                    else if (commercialDAO.getAll().size()==1) {
                        commercial = commercialDAO.getLast() ;
                        AddOperationTask task = new AddOperationTask(et_remise.getText().toString(),et_client.getText().toString(),et_mtrecue.getText().toString(),et_mtrecue.getText().toString(),acredit,false, cpt_bk,numerocheque, et_description.getText().toString());
                        task.execute();
                    }
                    else Toast.makeText(getActivity(), R.string.anycommercial, Toast.LENGTH_SHORT).show();
                }
                else if (preferences.getBoolean("commercial",false) && operation!=null && operation.getCommercialid()!=0) {
                    AddOperationTask task = new AddOperationTask(et_remise.getText().toString(),et_client.getText().toString(),et_mtrecue.getText().toString(),et_mtverse.getText().toString(),acredit,false, cpt_bk,numerocheque, et_description.getText().toString());
                    task.execute();
                }
            }
        }
        else
            Toast.makeText(getActivity(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();

        double qt = 0 ;
        for (int i = 0; i < produits.size(); ++i){
            qt += produits.get(i).getQuantite() ;
        }
        et_qt.setText(String.valueOf(qt));
        alertDialog.dismiss();
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParent = getActivity() ;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void sendIntent() {
        Intent intent = new Intent(getActivity(),ProduitListeActivity.class) ;
        intent.putExtra("Type",mParam1) ;
        startActivityForResult(intent, CHOOSE_PRODUIT_REQUEST);
    }

    public void readCodeBar() {
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        startActivityForResult(intent, CHOOSE_CODE_BAR_REQUEST);
    }

    public void showRacourci() {
        final AlertDialog.Builder box = new AlertDialog.Builder(getActivity()) ;
        LinearLayout ll = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.produitraccourcis,null);
        box.setView(ll);
        gridView = (GridView) ll.findViewById(R.id.gridView);

        racourciAdapter = new ListeProduitAdapter(produits) ;

        gridView.setAdapter(racourciAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProduit = racourciAdapter.getItem(position) ;
                racourciAdapter.increment(position) ;

                //et_qt.setText(mProduit.get);
                //et_prix.setText(Utiles.formatMtn2(mProduit.getPrixV()));
                //et_produit.setText(mProduit.getCode());
                addProduitOperationParRacourcis();

            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mProduit = racourciAdapter.getItem(position) ;
                racourciAdapter.decrement(position) ;
                adapter.diminuerItem(mProduit) ;

                updatequantite(1,false);
                return true;
            }
        });



        box.setTitle(R.string.produithabituel);
        box.show();
    }





    private void addProduitOperationParRacourcis() {
        if(mProduit != null){
            Mouvement mouvement = new Mouvement() ;
            if (mParam1.equals(CommandeActivity.CLIENT)) mouvement.setEntree(1);
            mouvement.setProduit(mProduit.getLibelle());
            mouvement.setPrixV(mProduit.getPrixV());
            mouvement.setPrixA(mProduit.getPrixA());
            mouvement.setQuantite(mProduit.getNbre());

            // Si le produit est deja synchroniser on utilise l'id externe sinon l'id interne
            if (mProduit.getId_externe()>0)mouvement.setProduit_id(mProduit.getId_externe());
            else mouvement.setProduit_id(mProduit.getId());

            if (!adapter.exist(mouvement.getProduit_id())) adapter.addData(mouvement) ;
            else adapter.addSpeedData(mouvement) ;

            et_ttc.setText(Utiles.formatMtn(prixTotal));
            double remise = 0 ;
            /*
            if (!et_remise.getText().toString().equals("")) remise = Double.parseDouble(et_remise.getText().toString()) ;
            et_netaPayer.setText(Utiles.formatMtn2(prixTotal-remise));
            */
            refresh(mouvements);
        }
        else
            Toast.makeText(getActivity(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();
    }


    private void effacer() {
        position = -1 ;
        id = -1 ;
        mProduit = null ;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public class OperationProduitAdapter extends BaseAdapter {

        ArrayList<Mouvement> mouvements = null ;
        SparseBooleanArray mSelectedItemsIds;

        public OperationProduitAdapter(ArrayList<Mouvement> pv){
            mSelectedItemsIds = new SparseBooleanArray();
            mouvements = pv ;
            //et_qt.setText(String.valueOf(getCount()));
        }

        public void toggleSelection(int position)
        {
            selectView(position, !mSelectedItemsIds.get(position));
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

        public void deleteItems(){
            ArrayList<Mouvement> pv = new ArrayList<Mouvement>() ;
            int n = mouvements.size() ;
            for (int i = 0 ; i < n ; i++){
                pv.add(mouvements.get(i)) ;
            }

            n = mSelectedItemsIds.size() ;
            for (int i = 0 ; i < n ; i++) {
                Log.d("debug",String.valueOf(i)) ;
                Log.d("debug",String.valueOf(mSelectedItemsIds.keyAt(i))) ;
                Log.d("debug","-----") ;


                if (racourciAdapter==null) racourciAdapter = new ListeProduitAdapter(produits) ;
                prixTotal -= pv.get(mSelectedItemsIds.keyAt(i)).getPrixV() * pv.get(mSelectedItemsIds.keyAt(i)).getQuantite() ;
                mouvements.remove(pv.get(mSelectedItemsIds.keyAt(i)));
                racourciAdapter.init(pv.get(mSelectedItemsIds.keyAt(i)).getProduit_id());
                mSelectedItemsIds.delete(mSelectedItemsIds.keyAt(i));
            }
            Toast.makeText(getActivity(), String.valueOf(n) + getResources().getString(R.string.item_del), Toast.LENGTH_LONG).show();
            et_qt.setText(String.valueOf(mouvements.size()));
        }


        @Override
        public int getCount() {
            if (mouvements!=null)   return mouvements.size() ;
            else return 0;
        }

        @Override
        public Mouvement getItem(int position) {
            if (mouvements.size()<=0) return null ;
            return mouvements.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return mouvements.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.mouvement_item, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final Mouvement mouvement = (Mouvement) getItem(position);
            final Produit produit = produitDAO.getOneByIdExterne(mouvement.getProduit_id()) ;

            if (produit!=null){
                if (produit.getImage()!=null && produit.getImage().contains("/")) loadLocalImage(produit.getImage(),holder.imageView);
                else if (produit.getImage()!=null)loadWebImage(holder.imageView, produit);
            }



            if(mouvement != null) {
                holder.produitTextView.setText(mouvement.getProduit());
                if (mParam1.equals(CommandeActivity.CLIENT)) holder.prixTextView.setText("PU: " + Utiles.formatMtn(mouvement.getPrixV()));
                else  holder.prixTextView.setText("PU: " + Utiles.formatMtn(mouvement.getPrixA()));
                holder.quantiteTextView.setText("Qte : " + String.valueOf(mouvement.getQuantite()));
                if (mParam1.equals(CommandeActivity.CLIENT)) holder.prixTotalTV.setText(Utiles.formatMtn(mouvement.getQuantite() * mouvement.getPrixV()));
                else holder.prixTotalTV.setText(Utiles.formatMtn(mouvement.getQuantite() * mouvement.getPrixA()));

                final ViewHolder finalHolder = holder;
                holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mouvement.setQuantite(0);
                        updateProduit(mouvement);
                        mouvements.remove(position) ;
                        refresh(mouvements) ;
                    }
                });
            };



            convertView.setBackgroundColor(mSelectedItemsIds.get(position) ? getResources().getColor(R.color.my_primary_light) : getResources().getColor(android.R.color.background_light));
            return convertView;
        }

        public void addData(Mouvement pvt) {
            if (mouvements==null) mouvements = new ArrayList<Mouvement>() ;

            if (exist(pvt.getProduit_id())){
                for (int i = 0; i < mouvements.size(); ++i)
                    if (mouvements.get(i).getProduit_id() == pvt.getProduit_id()) {
                        Mouvement pv = mouvements.get(i) ;

                        // rajouter la nouvelle quantite
                        //updatequantite(pv.getQuantite(),true);
                        if (pv!=null){
                            // On retire le total ancien
                            prixTotal -= pv.getPrixV()*pv.getQuantite() ;
                            pv.setQuantite(pvt.getQuantite()+pv.getQuantite());
                            // puis on ajoute le total nouveau
                            prixTotal += pv.getPrixV()*pv.getQuantite() ;
                            et_ttc.setText(Utiles.formatMtn(prixTotal));
                            double remise = 0 ;
                            //if (!et_remise.getText().toString().equals("")) remise = Double.parseDouble(et_remise.getText().toString()) ;
                            //et_netaPayer.setText(Utiles.formatMtn2(prixTotal-remise));
                            notifyDataSetChanged();
                        }
                    }
            }
            else{
                //updatequantite(pvt.getQuantite(),true);
                mouvements.add(0,pvt); ;
                prixTotal += pvt.getPrixV()*pvt.getQuantite() ;
                notifyDataSetChanged();
            }
            updateProduit(pvt);
            et_qt.setText(String.valueOf(getCount()));
        }

        private boolean exist(long produit_id) {
            if (mouvements != null)
                for (int i = 0; i < mouvements.size(); ++i)
                    if (mouvements.get(i).getProduit_id() == produit_id) return true ;
            return false;
        }


        public void updateItem(int id) {
            Mouvement mouvement = getItem(id) ;
            // retirer la nouvelle quantite
            if (mouvement!=null){

                updatequantite(mouvement.getQuantite(),false);

                if (mParam1.equals(CommandeActivity.CLIENT)) mouvement.setPrixV(Double.parseDouble(et_prix.getText().toString()));
                else  mouvement.setPrixA(Double.parseDouble(et_prix.getText().toString()));
                mouvement.setQuantite(Double.parseDouble(et_quantite.getText().toString()));
                mouvement.setDescription(et_description.getText().toString());

                // rajouter la nouvelle quantite
                updatequantite(mouvement.getQuantite(),true);

                if (mParam1.equals(CommandeActivity.CLIENT)) prixTotal += mouvement.getPrixV()*mouvement.getQuantite() ;
                else  prixTotal += mouvement.getPrixA() * mouvement.getQuantite() ;
                et_ttc.setText(Utiles.formatMtn(prixTotal));
                double remise = 0 ;
                if (et_remise!=null && !et_remise.getText().toString().equals("")) remise = Double.parseDouble(et_remise.getText().toString()) ;
                if (et_netaPayer!=null)et_netaPayer.setText(Utiles.formatMtn2(prixTotal-remise));
                notifyDataSetChanged();
                et_qt.setText(String.valueOf(getCount()));
            }
        }

        public void addSpeedData(Mouvement mouvement) {
            for (int i = 0; i < mouvements.size(); ++i)
                if (mouvements.get(i).getProduit_id() == mouvement.getProduit_id()) {
                    Mouvement pv = getItem(i) ;
                    if (pv!=null){
                        updatequantite(1,true);
                        if (mParam1.equals(CommandeActivity.CLIENT)) prixTotal -= pv.getPrixV()*pv.getQuantite() ;
                        else  prixTotal -= pv.getPrixA()*pv.getQuantite() ;
                        pv.setQuantite(pv.getQuantite()+1);
                        if (mParam1.equals(CommandeActivity.CLIENT)) prixTotal += pv.getPrixV()*pv.getQuantite() ;
                        else  prixTotal += pv.getPrixA()*pv.getQuantite() ;
                        et_ttc.setText(Utiles.formatMtn(prixTotal));
                        double remise = 0 ;
                        /*
                        if (!et_remise.getText().toString().equals("")) remise = Double.parseDouble(et_remise.getText().toString()) ;
                        et_netaPayer.setText(Utiles.formatMtn2(prixTotal-remise));
                        */
                        notifyDataSetChanged();
                        refresh(mouvements);
                    }
                }
            et_qt.setText(String.valueOf(getCount()));
        }


        public void diminuerItem(Produit mProduit) {
            boolean find = false ;
            for (int i = 0; i < mouvements.size(); ++i) if (mouvements.get(i).getProduit_id() == mProduit.getId_externe()) {
                Mouvement pv = mouvements.get(i);
                find = true ;
                if (pv.getQuantite()==1) mouvements.remove(i) ;
                else {
                    double n = pv.getQuantite() ;
                    n-- ;
                    pv.setQuantite(n);
                    mouvements.set(i,pv) ;
                }

                notifyDataSetChanged();
                refresh(mouvements);
                Toast.makeText(getActivity(),mProduit.getLibelle() + " diminué", Toast.LENGTH_LONG).show();
            }

            if (!find) Toast.makeText(getActivity(),mProduit.getLibelle() + " non trouvé" , Toast.LENGTH_LONG).show();

        }
    }

    /*
    private void deleteProduit(long produit_id) {
        for (int i = 0; i < produits.size(); i++) {
            if (produits.get(i).getId() == id) produits.get(i).setQuantite(0);
        }
    }
*/
    private void refresh(ArrayList<Mouvement> mouvements) {
        adapter = new OperationProduitAdapter(mouvements);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        prixTotal = 0 ;
        for (int i = 0; i < this.mouvements.size(); i++) {
            if (mParam1.equals(CommandeActivity.CLIENT))prixTotal += this.mouvements.get(i).getPrixV()* this.mouvements.get(i).getQuantite() ;
            else prixTotal += this.mouvements.get(i).getPrixA()* this.mouvements.get(i).getQuantite() ;
        }

        et_ttc.setText(Utiles.formatMtn(prixTotal));
        et_qt.setText(String.valueOf(this.mouvements.size()));


        racourciAdapter = new ListeProduitAdapter(produits) ;
        if (gridView!=null)gridView.setAdapter(racourciAdapter);
        racourciAdapter.notifyDataSetChanged();
    }


    private void updatequantite(double quantite,boolean add) {
        double qt = 0;
        //qt = Integer.parseInt(et_qt.getText().toString());
        if (add)qt += quantite;
        else qt -= quantite;


        //et_qt.setText(String.valueOf(qt));


        double remise = 0 ;
        double netapayer = 0;
        double recu = 0;

        /*if (et_netaPayer.getText().length()>0) netapayer = Double.parseDouble(et_netaPayer.getText().toString()) ;
        if (et_mtrecue.getText().length()>0) recu =  Double.parseDouble(et_mtrecue.getText().toString()) ;
        if (et_remise.getText().length()>0) remise =  Double.parseDouble(et_remise.getText().toString()) ;

        et_netaPayer.setText(Utiles.formatMtn2(prixTotal-remise));
        if (recu-netapayer>=0) et_arendre.setText(Utiles.formatMtn2(recu-netapayer));
        else et_arendre.setText("0");
        */

        //et_qt.setText(String.valueOf(adapter.getCount()));
    }


    public void addProduitOperation() {
        if(mProduit != null){
            Mouvement mouvement = new Mouvement() ;
            mouvement.setProduit(mProduit.getLibelle());
            if (mParam1.equals(CommandeActivity.CLIENT)) mouvement.setEntree(1);
            mouvement.setProduit(mProduit.getLibelle());
            mouvement.setPrixV(Double.parseDouble(et_prix.getText().toString()));
            mouvement.setPrixA(mProduit.getPrixA());
            mouvement.setQuantite(Double.parseDouble(et_quantite.getText().toString()));
            mouvement.setDescription(et_description.getText().toString());

            if (mProduit.getId_externe()>0)mouvement.setProduit_id(mProduit.getId_externe());
            else mouvement.setProduit_id(mProduit.getId());

            updateProduit(mouvement);
            adapter.addData(mouvement) ;

            et_ttc.setText(Utiles.formatMtn(prixTotal));
            double remise = 0 ;
            if (!et_remise.getText().toString().equals("")) remise = Double.parseDouble(et_remise.getText().toString()) ;
            et_netaPayer.setText(Utiles.formatMtn2(prixTotal-remise));
            //effacer();
        }
        else
            Toast.makeText(getActivity(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();
    }



    // ajouter dans la liste des produit du formulaire la quantite du produit à mettre à jour
    private void updateProduit(Mouvement mouvement) {
        for (int i = 0; i < produits.size(); ++i){
            if (produits.get(i).getId_externe() == mouvement.getProduit_id()){
                Produit produit = produits.get(i) ;
                produit.setNbre(mouvement.getQuantite());
                produits.set(i,produit) ;
            }
        }

        racourciAdapter = new ListeProduitAdapter(produits) ;
        racourciAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent() ;
        long id = intent.getLongExtra("OPERATION",0) ;
        //Toast.makeText(getActivity(), id + "", Toast.LENGTH_SHORT).show();
        if (id!=0) {
            operation = operationDAO.getOne(id);
            //prixTotal = operation.getMontant() ;
            if (operation!=null){

                ArrayList<Mouvement> mvs = mouvementDAO.getMany(id) ;
                Mouvement mv = null ;
                for (int i = 0 ; i < mvs.size();++i){
                    //updateProduit(mvs.get(i).getProduit_id());
                    mv = mvs.get(i) ;
                    mv.setEtat(0);
                    adapter.addData(mv) ;
                }
                //Toast.makeText(getActivity(), operation.getCommercialid()+"", Toast.LENGTH_SHORT).show();

                et_ttc.setText(Utiles.formatMtn2(prixTotal));
                double remise = 0 ;
                effacer();
            }
        }


        if (mParam1.equals(AchatActivity.ACHAT)) {
            attente.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CHOOSE_PRODUIT_REQUEST) {
            if (resultCode == getActivity().RESULT_OK ) {
                Produit produit = (Produit) data.getParcelableExtra(PRODUIT);
                if(produit != null){
                    addProduit(produit);
                }
                else
                    Toast.makeText(getActivity(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();
            }
        }

        else if (requestCode == CommandeFragment.CHOOSE_PARTENAIRE_REQUEST) {
            if (resultCode == getActivity().RESULT_OK ) {
                partenaire = (Partenaire) new PartenaireDAO(getActivity()).getOne(data.getLongExtra(CommandeFragment.PARTENAIRE,0));
                if (partenaire!=null) if (et_client!=null)et_client.setText(partenaire.getRaisonsocial()+ "" + partenaire.getNom() + " " + partenaire.getPrenom()) ;
            }
        }

        else if (requestCode == CHOOSE_CODE_BAR_REQUEST) {
            if (data==null) return;
            String cobare = data.getStringExtra(AccueilActivity.CODEBAR);
            if (cobare!=null) {
                Produit produit = produitDAO.getOneByCode(cobare);
                if (produit != null) {
                    addProduit(produit);
                }
                else Toast.makeText(getActivity(), R.string.produitintr, Toast.LENGTH_SHORT).show();
            }
            // Relancer la lecture
            readCodeBar();
        }
        else  if (requestCode == CHOOSE_DEVISE_BAR_REQUEST) {
            if (resultCode!=getActivity().RESULT_OK) return;
            deviseOperations = data.getParcelableArrayListExtra(DEVISE);
            double ttc = data.getDoubleExtra("MTN",0);
            Log.e("TOTAL", String.valueOf(ttc)) ;
            if (et_mtrecue!=null && et_mtrecue.hasFocus()) et_mtrecue.setText(String.valueOf(ttc));
            //if (et_mtverse!=null && et_mtverse.hasFocus()) et_mtverse.setText(String.valueOf(ttc));
        }

    }


    public void setProduit(Produit produit) {
        mProduit = produit ;
        et_produit.setText(mProduit.getLibelle());
        if (mParam1.equals(CommandeActivity.CLIENT))et_prix.setText(String.valueOf(mProduit.getPrixV()));
        else et_prix.setText(String.valueOf(mProduit.getPrixA()));
        if (mProduit.getModifiable() == 1) et_prix.setEnabled(true);
        else et_prix.setEnabled(false);
        et_quantite.setText("1");
    }


    private void addProduit(Produit produit) {
        mProduit = produit ;
        Mouvement mouvement = new Mouvement() ;
        if (mParam1.equals(CommandeActivity.CLIENT)) mouvement.setEntree(1);
        mouvement.setProduit(mProduit.getLibelle());
        mouvement.setPrixV(mProduit.getPrixV());
        mouvement.setPrixA(mProduit.getPrixA());
        mouvement.setQuantite(1);

        if (mProduit.getId_externe()>0)mouvement.setProduit_id(mProduit.getId_externe());
        else mouvement.setProduit_id(mProduit.getId());

        //updateProduit(mouvement.getProduit_id());
        adapter.addData(mouvement) ;
        refresh(mouvements);

        et_ttc.setText(Utiles.formatMtn(prixTotal));
        et_qt.setText(String.valueOf(adapter.getCount()));
        //effacer();
    }



    static class ViewHolder{
        TextView produitTextView ;
        TextView prixTextView ;
        TextView quantiteTextView ;
        TextView prixTotalTV ;
        ImageView imageView ;
        ImageView deleteImage ;

        public ViewHolder(View v) {
            produitTextView = (TextView)v.findViewById(R.id.produitTV);
            prixTextView = (TextView)v.findViewById(R.id.prixTv);
            quantiteTextView = (TextView)v.findViewById(R.id.quantiteTV);
            prixTotalTV = (TextView)v.findViewById(R.id.prixTotalTV);
            imageView = (ImageView) v.findViewById(R.id.image);
            deleteImage = (ImageView) v.findViewById(R.id.delete);
        }
    }




    public class ListeProduitAdapter extends BaseAdapter {

        ArrayList<Produit> produits = null ;

        public ListeProduitAdapter(ArrayList<Produit> pProduit){
            produits = pProduit ;
        }

        @Override
        public int getCount() {
            if (produits==null) return  0 ;
            return produits.size() ;
        }

        @Override
        public Produit getItem(int position) {
            if (produits==null) return null ;
            return produits.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return produits.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RacourcisViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.racourci_item, null);
                holder = new RacourcisViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (RacourcisViewHolder)convertView.getTag();
            }

            Produit produit = (Produit) getItem(position);



            if(produit != null) {
                holder.libelleTextView.setText(produit.getLibelle());
                holder.codeTextView.setText(produit.getCode());
                if (mParam1.equals(CommandeActivity.CLIENT))holder.prixVTextView.setText(Utiles.formatMtn(produit.getPrixV()));
                else holder.prixVTextView.setText(Utiles.formatMtn(produit.getPrixA()));
                if (produit.getNbre()==0) holder.nbrTextView.setVisibility(View.GONE);
                else {
                    holder.nbrTextView.setVisibility(View.VISIBLE);
                    holder.nbrTextView.setText(String.valueOf(produit.getNbre()));
                }


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

        public void increment(int position) {
            Produit produit = getItem(position) ;
            if (produit!= null) produit.setNbre(produit.getNbre()+1);
            notifyDataSetChanged();
        }

        public void decrement(int position) {
            Produit produit = getItem(position) ;
            if (produit!= null && produit.getNbre()>0) produit.setNbre(produit.getNbre()-1);
            notifyDataSetChanged();
        }

        public void init(long id) {
            for (int i =0; i<produits.size();++i){
                if (produits.get(i).getId_externe() == id) {
                    updatequantite(produits.get(i).getNbre(),false);
                    produits.get(i).setNbre(0); ;
                }
            }
            notifyDataSetChanged();
        }
    }





    public void showMoney(View view){
        money_init() ;
        achat = false ;
        builder = new AlertDialog.Builder(getActivity()) ;
        builder.setView(rv) ;
        alert = builder.show() ;
    }


    private void money_init(){
        rv = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.buillet_layout, null);

        sc = (ScrollView) rv.findViewById(R.id.scroll);
        total = (Button) rv.findViewById(R.id.total);
        init = (Button) rv.findViewById(R.id.init);
        close = (Button) rv.findViewById(R.id.close);

        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total.setText("0");
                if (et_remise!=null && et_remise.hasFocus())et_remise.setText("0");
                if (et_mtrecue!=null && et_mtrecue.hasFocus())et_mtrecue.setText("0");
                if (et_mtverse!=null && et_mtverse.hasFocus())et_mtverse.setText("0");
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel() ;
            }
        });

        if (et_remise!=null && et_remise.hasFocus()){
            String p = et_remise.getText().toString() ;
            if (p.length()==0) et_remise.setText("0");
            total.setText(et_remise.getText().toString());
        }
        else if (et_mtrecue!=null && et_mtrecue.hasFocus()){
            String p = et_mtrecue.getText().toString() ;
            if (p.length()==0) et_mtrecue.setText("0");
            total.setText(et_mtrecue.getText().toString());
        }
        else if (et_mtverse!=null && et_mtverse.hasFocus()){
            String p = et_mtverse.getText().toString() ;
            if (p.length()==0) et_mtverse.setText("0");
            total.setText(et_mtverse.getText().toString());
        }

        dixmille = (RelativeLayout) sc.findViewById(R.id.dimille);
        dixmille.setOnClickListener(this);
        dixmille.setOnLongClickListener(this);
        cinqmille = (RelativeLayout) sc.findViewById(R.id.cinqmille);
        cinqmille.setOnClickListener(this);
        cinqmille.setOnLongClickListener(this);
        deuxmille = (RelativeLayout) sc.findViewById(R.id.deuxmille);
        deuxmille.setOnClickListener(this);
        deuxmille.setOnLongClickListener(this);
        mille = (RelativeLayout) sc.findViewById(R.id.mille);
        mille.setOnClickListener(this);
        mille.setOnLongClickListener(this);
        cinqcent = (RelativeLayout) sc.findViewById(R.id.cinqcent);
        cinqcent.setOnClickListener(this);
        cinqcent.setOnLongClickListener(this);
        deuxcentcinquante = (RelativeLayout) sc.findViewById(R.id.deuxcentcinquante);
        deuxcentcinquante.setOnClickListener(this);
        deuxcentcinquante.setOnLongClickListener(this);
        deuxcent = (RelativeLayout) sc.findViewById(R.id.deuxcent);
        deuxcent.setOnClickListener(this);
        deuxcent.setOnLongClickListener(this);
        cent = (RelativeLayout) sc.findViewById(R.id.cent);
        cent.setOnClickListener(this);
        cent.setOnLongClickListener(this);
        cinquante = (RelativeLayout) sc.findViewById(R.id.cinquante);
        cinquante.setOnClickListener(this);
        cinquante.setOnLongClickListener(this);
        vingcinq = (RelativeLayout) sc.findViewById(R.id.vingcinq);
        vingcinq.setOnClickListener(this);
        vingcinq.setOnLongClickListener(this);
    }




    public void diminuer(int v, TextView tv){
        double val = 0 ;
        double prix = 0 ;
        if (tv.getVisibility()!=View.GONE) val = Double.parseDouble(tv.getText().toString());

        if (et_mtrecue!=null && et_mtrecue.hasFocus()){
            String p = et_mtrecue.getText().toString() ;
            if (p.length()==0) et_mtrecue.setText("0");
        }
        else if (et_remise!=null && et_remise.hasFocus()){
            String p = et_remise.getText().toString() ;
            if (p.length()==0) et_remise.setText("0");
        }
        else if (et_mtverse!=null && et_mtverse.hasFocus()){
            String p = et_mtverse.getText().toString() ;
            if (p.length()==0) et_mtverse.setText("0");
        }


        // Si le total afficher est superieur à zero
        if (!total.getText().equals("0")){

            prix = Double.parseDouble(total.getText().toString()) ;
            if (prix>v) prix -= v ;

            if (et_mtrecue!=null && et_mtrecue.hasFocus())et_mtrecue.setText(String.valueOf(prix));
            if (et_remise!=null && et_remise.hasFocus())et_remise.setText(String.valueOf(prix));
            if (et_mtverse!=null && et_mtverse.hasFocus())et_mtverse.setText(String.valueOf(prix));

            if (val>0){
                val-- ;
                tv.setText(String.valueOf(val));
                tv.setVisibility(View.VISIBLE);
            }

            total.setText(String.valueOf(prix));
        }

        if (val==0)tv.setVisibility(View.GONE);
    }



    public void augmenter(int v,TextView tv){
        double val = 0 ;
        double prix = 0 ;
        if (tv.getVisibility()!=View.GONE) val = Double.parseDouble(tv.getText().toString());

        if (et_mtrecue!=null && et_mtrecue.hasFocus()){
            String p = et_mtrecue.getText().toString() ;
            if (p.length()==0) et_mtrecue.setText("0");
            prix += v + Double.parseDouble(et_mtrecue.getText().toString()) ;
            et_mtrecue.setText(String.valueOf(prix));
        }
        else if (et_mtverse!=null && et_mtverse.hasFocus()){
            String p = et_mtverse.getText().toString() ;
            if (p.length()==0) et_mtverse.setText("0");
            prix += v + Double.parseDouble(et_mtverse.getText().toString()) ;
            et_mtverse.setText(String.valueOf(prix));
        }
        else if (et_remise!=null && et_remise.hasFocus()){
            String p = et_remise.getText().toString() ;
            if (p.length()==0) et_remise.setText("0");
            prix += v + Double.parseDouble(et_remise.getText().toString()) ;
            et_remise.setText(String.valueOf(prix));
        }



        val++ ;

        // On met à jour les etiquettes
        tv.setText(String.valueOf(val));
        tv.setVisibility(View.VISIBLE);

        total.setText(String.valueOf(prix));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dimille : {
                TextView tv = (TextView) dixmille.getChildAt(1);
                augmenter(10000,tv);
            } break;
            case R.id.cinqmille : {
                TextView tv = (TextView) cinqmille.getChildAt(1);
                augmenter(5000, tv);
            } break;
            case R.id.deuxmille : {
                TextView tv = (TextView) deuxmille.getChildAt(1);
                augmenter(2000, tv);
            } break;
            case R.id.mille : {
                TextView tv = (TextView) mille.getChildAt(1);
                augmenter(1000, tv);
            } break;
            case R.id.cinqcent : {
                TextView tv = (TextView) cinqcent.getChildAt(1);
                augmenter(500, tv);
            } break;
            case R.id.deuxcentcinquante : {
                TextView tv = (TextView) deuxcentcinquante.getChildAt(1);
                augmenter(250, tv);
            } break;
            case R.id.deuxcent : {
                TextView tv = (TextView) deuxcent.getChildAt(1);
                augmenter(200, tv);
            } break;
            case R.id.cent : {
                TextView tv = (TextView) cent.getChildAt(1);
                augmenter(100, tv);
            } break;
            case R.id.cinquante : {
                TextView tv = (TextView) cinquante.getChildAt(1);
                augmenter(50, tv);
            } break;
            case R.id.vingcinq : {
                TextView tv = (TextView) vingcinq.getChildAt(1);
                augmenter(25, tv);
            } break;
        }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.dimille : {
                TextView tv = (TextView) dixmille.getChildAt(1);
                diminuer(10000,tv);
            } break;
            case R.id.cinqmille : {
                TextView tv = (TextView) cinqmille.getChildAt(1);
                diminuer(5000,tv);
            } break;
            case R.id.deuxmille : {
                TextView tv = (TextView) deuxmille.getChildAt(1);
                diminuer(2000,tv);
            } break;
            case R.id.mille : {
                TextView tv = (TextView) mille.getChildAt(1);
                diminuer(1000,tv);
            } break;
            case R.id.cinqcent : {
                TextView tv = (TextView) cinqcent.getChildAt(1);
                diminuer(500,tv);
            } break;
            case R.id.deuxcentcinquante : {
                TextView tv = (TextView) deuxcentcinquante.getChildAt(1);
                diminuer(250,tv);
            } break;
            case R.id.deuxcent : {
                TextView tv = (TextView) deuxcent.getChildAt(1);
                diminuer(200,tv);
            } break;
            case R.id.cent : {
                TextView tv = (TextView) cent.getChildAt(1);
                diminuer(100,tv);
            } break;
            case R.id.cinquante : {
                TextView tv = (TextView) cinquante.getChildAt(1);
                diminuer(50,tv);
            } break;
            case R.id.vingcinq : {
                TextView tv = (TextView) vingcinq.getChildAt(1);
                diminuer(25,tv);
            } break;
        }
        return true;
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
                        if (getActivity()!=null)getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap1);

                                // progressBar.setVisibility(View.GONE);
                            }
                        });

                        try {
                            uri[0] = Utiles.saveImageExternalStorage(bitmap1, getActivity(), String.valueOf(produit.getId())+".jpg", Utiles.PV_PRODUIT_IMAGE_DIR);
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
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("RESULT", "LOAD en cours...");
                //Toast.makeText(getApplicationContext(),"Sauvegarde en cours...",Toast.LENGTH_LONG).show();
            }
        };

        String path = Url.getImageDirectory(getActivity(), produit.getImage()) ;
        Log.e("WEB PATH",path) ;
        Picasso.with(getActivity()).load(path).into(target);
    }



    public void loadLocalImage(final String name, final ImageView imageView) {
        String dossier = Utiles.PV_PRODUIT_IMAGE_DIR ;

        File file = new File(name) ;
        imageView.setImageURI(Uri.fromFile(file));

        Log.e("DEBUG", "LOD1") ;
    }

    static class RacourcisViewHolder{
        TextView codeTextView ;
        TextView libelleTextView ;
        TextView prixVTextView ;
        TextView nbrTextView ;
        ImageView imageView ;

        public RacourcisViewHolder(View v) {
            codeTextView = (TextView)v.findViewById(R.id.codeTV);
            libelleTextView = (TextView)v.findViewById(R.id.libelleTV);
            prixVTextView = (TextView)v.findViewById(R.id.prixVTV);
            nbrTextView = (TextView)v.findViewById(R.id.nbre);
            imageView = (ImageView) v.findViewById(R.id.imageview);
        }
    }




    public class AddOperationTask extends AsyncTask<Void, Integer, String> {

        OperationDAO operationDAO = null ;
        MouvementDAO pvOperationDAO = null ;
        Mouvement  pv = null ;
        String et_remise ;
        String et_client ;
        String et_mtrecue ;
        String et_verser ;
        private TypeOperation to;
        private CaisseDAO caisseDAO;
        boolean acredit = false ;
        boolean attente = false ;
        long cpt_bk = 0;
        String numcheque ;
        private DeviseOperationDAO deviseOperationDAO;
        String description = "" ;


        public AddOperationTask(String et_remise, String et_client, String et_mtrecue, String et_verser, boolean ch, boolean at, long cpt_bk, String numcheque, String desc) {
            this.et_remise = et_remise ;
            this.et_client = et_client ;
            this.et_mtrecue = et_mtrecue ;
            this.et_verser = et_verser ;
            this.acredit = ch ;
            this.attente = at ;
            this.cpt_bk = cpt_bk ;
            this.numcheque = numcheque ;
            this.description = desc ;
        }

        @Override
        protected String doInBackground(Void... params) {
            String client = getString(R.string.clientcomptoir) ;
            to = new TypeOperationDAO(getActivity()).getOne(OperationDAO.CMDCLNT);
            operation = new Operation() ;
            if (commercial!=null){
                // Si le commercial est deja synchroniser on utilise l'id externe sinon l'id interne
                if (commercial.getId_externe()>0)operation.setCommercialid(commercial.getId_externe());
                else operation.setCommercialid(commercial.getId());
            }
            if (mParam1.equals(CommandeActivity.CLIENT)){
                to = new TypeOperationDAO(getActivity()).getOne(OperationDAO.CMDCLNT);
                operation.setTypeOperation_id(OperationDAO.CMDCLNT);
            }
            else {
                to = new TypeOperationDAO(getActivity()).getOne(OperationDAO.CMDFRNSS);
                operation.setTypeOperation_id(OperationDAO.CMDFRNSS);
            }

            operation.setDescription(this.description);

            double remise = 0 ;
            double recu = 0 ;
            double verser = 0 ;
            if (!et_remise.equals("")) remise = Double.parseDouble(et_remise) ;
            operation.setRemise(remise);
            operation.setCaisse_id(caisseDAO.getFirst().getId());
            operation.setMontant(prixTotal);
            if (et_client.length()>0) client =  et_client ;
            operation.setClient(client);
            operation.setEtat(0);
            if (partenaire!=null) operation.setPartenaire_id(partenaire.getId_externe());
            if (mParam1.equals(CommandeActivity.CLIENT)) {
                operation.setEntree(1);
            }
            if (acredit) {
                operation.setPayer(0);
                operation.setDateecheance(d);
            }
            else operation.setPayer(1);

            operation.setModepayement(MODE);
            operation.setNumcheque(numcheque);
            operation.setComptebanque_id(cpt_bk);

            if (attente) {
                operation.setAttente(1);
            }
            else operation.setAttente(0);

            if(!et_mtrecue.equals(""))recu = Double.parseDouble(et_mtrecue) ;
            operation.setRecu(recu);
            Log.e("RECU", String.valueOf(operation.getRecu())) ;

            SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(getActivity());
            //ATOUCHER
            //operation.setCaisse_id(preferences.getLong(LicenceActivity.CAISSE_ID,-1));

            //if (preferences.getLong(LicenceActivity.CAISSE_ID,-1)==-1) return false ;
            long result =  0 ;
            operation.setToken(caisseDAO.getFirst().getCode()+"/"+operation.getMontant()+"/"+System.currentTimeMillis());
            if (operation.getId()==0) result = operationDAO.add(operation) ;
            else result = operationDAO.update(operation) ;

            if (result>0){
                operation = operationDAO.getLast() ;

                if (deviseOperations == null) {
                    deviseOperations = new ArrayList<>() ;
                }

                Devise devise = new DeviseDAO(getActivity()).getReference() ;
                if (devise!=null  && deviseOperations.size()==0) {
                    DeviseOperation deviseOperation = new DeviseOperation();
                    deviseOperation.setDevise_id(devise.getId()) ;
                    deviseOperation.setMontant(operation.getRecu());
                    deviseOperations.add(deviseOperation);
                }

                if (operation.getAttente()==0 && deviseOperations!=null){
                    for (int i = 0; i < deviseOperations.size(); i++) {
                        DeviseOperation deviseOperation = deviseOperations.get(i) ;
                        deviseOperation.setOperation_id(result);
                        deviseOperationDAO.add(deviseOperation) ;
                    }
                }

                Log.e("RECU", String.valueOf(operation.getRecu())) ;

                mouvementDAO.deletePV(operation.getId_externe()) ;
                int n = adapter.getCount() ;
                double restant = 0 ;
                Produit produit = null ;
                for (int i = n-1; i >= 0; i--){
                    pv = adapter.getItem(i) ;
                    Mouvement last  = mouvementDAO.getLastbyProduct(pv.getProduit_id()) ;
                    if (last==null) restant = 0 ;
                    else restant = last.getRestant() ;

                    if (pv.getEntree()==1) restant -= pv.getQuantite() ;
                    else  restant += pv.getQuantite() ;

                    pv.setRestant(restant);
                    pv.setEtat(2);
                    pv.setOperation_id(operation.getId_externe());
                    pv.setCreated_at(operation.getCreated_at());

                    long res = mouvementDAO.add(pv);
                    // Mettre à jour le prix de vente ou le prix d'achat si il a été modifié
                    /*
                    if (res>0){
                        produit = produitDAO.getOne(pv.getProduit_id()) ;
                        if (produit!=null){
                            if (mParam1.equals(CommandeActivity.CLIENT) && produit.getPrixV()!=pv.getPrixV()) {
                                produit.setPrixV(pv.getPrixV());
                                produit.setEtat(0);
                                produitDAO.update(produit) ;
                            }
                            else if (!mParam1.equals(CommandeActivity.CLIENT) && produit.getPrixA()!=pv.getPrixA()) {
                                produit.setPrixA(pv.getPrixA());
                                produit.setEtat(0);
                                produitDAO.update(produit) ;
                            }
                        }
                    }
                    */
                }
                operation.setNbreproduit(n);
                operationDAO.update(operation);

                if (et_verser.equals("")) et_verser = "0" ;
                if (Double.parseDouble(et_verser)==operation.getMontant()-operation.getRemise()) {
                    operation.setPayer(1);
                    operation.setEtat(0);
                    if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT)) operation.setTypeOperation_id(OperationDAO.VENTE);
                    else if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS)) operation.setTypeOperation_id(OperationDAO.ACHAT);
                    operationDAO.update(operation) ;
                    mouvementDAO.updateMany(operation) ;
                }
                Operation payement = new Operation() ;
                verser = Double.valueOf(et_verser) ;
                payement.setOperation_id(operation.getId_externe());
                payement.setCaisse_id(operation.getCaisse_id());
                payement.setClient(operation.getClient());
                payement.setMontant(recu);
                if (operation.getPayer()!=1)payement.setEtat(2);
                Log.e("MTN RECU", String.valueOf(recu)) ;
                payement.setEntree(operation.getEntree());

                if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS) || operation.getTypeOperation_id().equals(OperationDAO.ACHAT)) payement.setDescription(getString(R.string.rglmtcmdachat));
                else if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT) || operation.getTypeOperation_id().equals(OperationDAO.VENTE)) payement.setDescription(getString(R.string.rglmtcmdvt));
                else if (operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT)) payement.setDescription(getString(R.string.rglmtemp));

                if (operation.equals(OperationDAO.EMPRUNT)) payement.setEntree(0);
                payement.setToken(caisseDAO.getFirst().getCode()+"/"+payement.getMontant()+"/"+System.currentTimeMillis());
                payement.setModepayement(new ModePayementDAO(getActivity()).getAll().get(1).getCode());
                payement.setTypeOperation_id(OperationDAO.REGLEMENT);
                if (recu>0) operationDAO.add(payement) ;
            }

            return String.valueOf(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            operationDAO = new OperationDAO(getActivity()) ;
            caisseDAO = new CaisseDAO(getActivity()) ;
            deviseOperationDAO = new DeviseOperationDAO(getActivity()) ;
            pvOperationDAO = new MouvementDAO(getActivity()) ;
            mParent.showDialog(AccueilActivity.PROGRESS_DIALOG_ID);
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (Double.valueOf(result)>0) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }

                builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.imprimermsg));
                builder.setPositiveButton(R.string.imp, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lancerImprime();
                    }
                });
                if (operation.getAttente() == 1) builder.show();
                else lancerImprime();


                mParent.dismissDialog(AccueilActivity.PROGRESS_DIALOG_ID);


                double a_payer = 0;

                //a_payer = Double.parseDouble() ;
                JournalDAO journalDAO = new JournalDAO(getActivity());
                Journal journal = new Journal();
                journal.setType(to.getLibelle());
                journal.setMontant(operation.getMontant());
                if (mParam1.equals(AccueilActivity.VENTE))
                    journal.setDescription(getString(R.string.ventemt) + operation.getMontant());
                else if (mParam1.equals(AchatActivity.ACHAT))
                    journal.setDescription(getString(R.string.achatmt) + operation.getMontant());
                journalDAO.add(journal);

                Toast.makeText(getActivity(), getString(R.string.operation_success), Toast.LENGTH_LONG).show();
                clean();
            }
        }


        private void lancerImprime() {

            if (operation.getTypeOperation_id().equals(OperationDAO.VENTE)){
                if (preferences.getBoolean("imprimenteexterne",true)) {
                    try {
                        if(preferences.getString("papier","").equals(R.string.petit)){
                            PrinterUtils printUtils = new PrinterUtils(getActivity()) ;
                            printUtils.printTicket(operation.getId_externe(),0);
                        }
                        else{
                            PrinterUtilsGrand printerUtilsGrand = new PrinterUtilsGrand(getActivity()) ;
                            printerUtilsGrand.printTicket(operation.getId_externe(),0);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        PrintPDA printPDA = new PrintPDA(getActivity()) ;
                        printPDA.printTicket(operation.getId_externe(),0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        PrintPDAMobiPrint3 printPDAMobiPrint3 = new PrintPDAMobiPrint3(getActivity());
                        printPDAMobiPrint3.printTicket(operation.getId_externe(),0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!preferences.getBoolean("imprimenteexterne",true)){

                    try {
                        PrintP800 printP800 = new PrintP800(getActivity());
                        printP800.printTicket(operation.getId_externe(),0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (UnsatisfiedLinkError e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    private void clean() {
        mouvements.clear();
        adapter = new OperationProduitAdapter(mouvements) ;
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        prixTotal=0 ;
        et_ttc.setText("0");;
        et_qt.setText("0");;
        operation = null ;
        deviseOperations = new ArrayList<>() ;
    }

}
