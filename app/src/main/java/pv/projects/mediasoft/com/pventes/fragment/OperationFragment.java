package pv.projects.mediasoft.com.pventes.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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


import com.zj.btsdk.BluetoothService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.activities.AccueilActivity;
import pv.projects.mediasoft.com.pventes.activities.AchatActivity;
import pv.projects.mediasoft.com.pventes.activities.DeviseListActivity;
import pv.projects.mediasoft.com.pventes.activities.OperationActivity;
import pv.projects.mediasoft.com.pventes.dao.CaisseDAO;
import pv.projects.mediasoft.com.pventes.dao.CommercialDAO;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseOperationDAO;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.TypeOperationDAO;
import pv.projects.mediasoft.com.pventes.database.OperationHelper;
import pv.projects.mediasoft.com.pventes.model.Commercial;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.DeviseOperation;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
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
 * {@link OperationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OperationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class OperationFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener{
    public static final String TAG = "OperationFragment";
    public static final String OPERATION_ID = "operation_id";
    public static final String HIDE = "hide";
    public static final String SHOW = "show";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int CHOOSE_DEVISE_BAR_REQUEST = 5;
    ListView lv = null;
    ArrayList<Operation> operations = null;
    ListeOperationAdapter adapter = null;
    OperationDAO operationDAO = null;
    LinearLayout empty;
    LinearLayout full;
    LinearLayout progress;
    Button actualiser;
    private int type = 0;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private LayoutInflater mInflater;
    private SharedPreferences preferences;
    private String bluetoothConfig = "imprimenteexterne";
    private AlertDialog.Builder detailBox;
    private AlertDialog dialogue;
    private AlertDialog dialogu;
    private Operation operation;
    private Spinner spinner_banque;
    private EditText numcheque;


    private ScrollView sc = null ;
    private android.support.v7.app.AlertDialog.Builder builder;
    private Button total =null ;
    private Button init =null ;
    private Button close = null;
    private RelativeLayout rv = null ;


    private static String MODE = "ESPECE";
    private static final String ESPECE = "ESPECE";
    private static final String CHEQUE = "CHEQUE";
    private static final String VIREMENT = "VIREMENT";
    private ArrayList<CompteBanque> cb;
    private ArrayAdapter mAdapter;
    private long cpt_bk;
    private String numerocheque = "";
    private EditText et_mtrecue;


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
    private android.support.v7.app.AlertDialog alert;
    private boolean achat = false;
    private ArrayList<DeviseOperation> deviseOperations ;
    private DeviseOperationDAO deviseOperationDAO;
    private BluetoothService mService = null ;
    private DeviseDAO deviseDAO;


    public OperationFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OperationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OperationFragment newInstance(String param1, String param2) {
        OperationFragment fragment = new OperationFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        refresh();
        //operations = new ArrayList<Operation>() ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_operation, container, false);
        operationDAO = new OperationDAO(getActivity());

        mInflater = inflater;

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        empty = (LinearLayout) v.findViewById(R.id.vide);
        lv = (ListView) v.findViewById(R.id.liste);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Operation operation = adapter.getItem(i);
                mListener.onFragmentInteraction(Uri.parse(String.valueOf(operation.getId_externe())));
                return true;
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Operation operation = adapter.getItem(i);
                showOpDetails(operation);
            }
        });

        deviseOperations = new ArrayList<>() ;
        deviseOperationDAO = new DeviseOperationDAO(getActivity()) ;

        mService = new BluetoothService(getActivity(), mHandler);

        deviseDAO = new DeviseDAO(getActivity()) ;

        return v;
    }

    private void showOpDetails(final Operation operation) {
        detailBox = new AlertDialog.Builder(getActivity()) ;
        MouvementDAO mouvementDAO = new MouvementDAO(getActivity()) ;
        Operation parent = operationDAO.getOneExterne(operation.getOperation_id()) ;
        ArrayList<Mouvement> mouvements = null ;

        if ((parent!=null && !parent.getTypeOperation_id().equals(OperationDAO.EMPRUNT) && operation.getTypeOperation_id().equals(OperationDAO.REGLEMENT)) || operation.getTypeOperation_id().equals(OperationDAO.SORTIE_STOCK)  || operation.getTypeOperation_id().equals(OperationDAO.VENTE) || operation.getTypeOperation_id().equals(OperationDAO.ACHAT) ){

            ScrollView sc = (ScrollView) getActivity().getLayoutInflater().inflate(R.layout.operationdetails,null);
            detailBox.setView(sc);

            TextView prixTotal = (TextView) sc.findViewById(R.id.prixTotalTV);
            TextView remise = (TextView) sc.findViewById(R.id.remiseTV);
            TextView recu = (TextView) sc.findViewById(R.id.recuTV);
            TextView mode = (TextView) sc.findViewById(R.id.mode);
            TextView client = (TextView) sc.findViewById(R.id.client);
            TextView banque = (TextView) sc.findViewById(R.id.banque);
            TextView rendu = (TextView) sc.findViewById(R.id.renduTV);
            TextView dateoperation = (TextView) sc.findViewById(R.id.dateVente);
            ImageButton livre = (ImageButton) sc.findViewById(R.id.livre);
            ImageButton annuler = (ImageButton) sc.findViewById(R.id.annuler);
            ImageButton imp = (ImageButton) sc.findViewById(R.id.imp);
            Button fermer = (Button) sc.findViewById(R.id.fermer);
            ListView v = (ListView) sc.findViewById(R.id.listeView);
            LinearLayout table = (LinearLayout) sc.findViewById(R.id.table);

            detailBox.setTitle(getString(R.string.notransact) + operation.getId()) ;
            if (operation.getTypeOperation_id().equals(OperationDAO.REGLEMENT))prixTotal.setText(getString(R.string.montant) + Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
            else prixTotal.setText(getString(R.string.netapayer) + Utiles.formatMtn(operation.getMontant()-operation.getRemise()));

            remise.setText(getString(R.string.remise) + Utiles.formatMtn(operation.getRemise()));
            recu.setText(getString(R.string.recu) + Utiles.formatMtn(operation.getRecu()));
            if (operation.getRecu()-operation.getMontant()+operation.getRemise() >0 && !operation.getTypeOperation_id().equals(OperationDAO.REGLEMENT)) rendu.setText(getString(R.string.rendu) + Utiles.formatMtn(operation.getRecu()-operation.getMontant()+operation.getRemise()));
            else rendu.setText(getString(R.string.rendu) + "0");

            if (operation.getPayer()==0){
                ArrayList<Operation> payements = operationDAO.getMany(operation.getId_externe()) ;
                double mtn = 0 ;
                Log.e("SIZE OPF", String.valueOf(payements.size())) ;
                for (int i = 0; i < payements.size(); i++) {
                    mtn += payements.get(i).getMontant() ;
                }
                recu.setText(getString(R.string.recu) + Utiles.formatMtn(mtn));
            }

            mode.setText(getString(R.string.mode) + operation.getModepayement());
            CompteBanque compteBanque = new CompteBanqueDAO(getActivity()).getOne(operation.getComptebanque_id()) ;
            if (compteBanque!=null)banque.setText(getString(R.string.bk) + compteBanque.getLibelle());

            if (operation.getTypeOperation_id().equals(OperationDAO.REGLEMENT)){
                parent = operationDAO.getOneExterne(operation.getOperation_id()) ;
                if (parent!=null) mouvements = mouvementDAO.getMany(parent.getId_externe());
                else mouvements = mouvementDAO.getMany(operation.getId_externe());
                if (mouvements.size()==0)  mouvements = mouvementDAO.getMany(operation.getId());
            }
            else {
                mouvements = mouvementDAO.getMany(operation.getId_externe());
                if (mouvements.size()==0)  mouvements = mouvementDAO.getMany(operation.getId());
            }

            if (mouvements.size()==0)  table.setVisibility(View.GONE);

            Log.e("MV SIZE", String.valueOf(mouvements.size())) ;
            if (!operation.getTypeOperation_id().startsWith(OperationDAO.PRODUIT) && !operation.getTypeOperation_id().startsWith(OperationDAO.CHARGE) && !operation.getTypeOperation_id().startsWith(OperationDAO.DEPENSE)) {
                VenteProduitAdapter vpa = new VenteProduitAdapter(mouvements,operation.getEntree()) ;
                v.setAdapter(vpa);
            }
            else {
                livre.setVisibility(View.GONE);
                v.setVisibility(View.GONE);
            }

            if (operation.getTypeOperation_id().equals(OperationDAO.REGLEMENT)) livre.setVisibility(View.GONE);
            if (operation.getTypeOperation_id().startsWith(OperationDAO.CMD)) livre.setVisibility(View.VISIBLE);

            if (operation.getTypeOperation_id().equals(OperationDAO.VENTE) && operation.getPayer()==1)   livre.setVisibility(View.GONE);
            if (operation.getTypeOperation_id().equals(OperationDAO.VENTE) && operation.getAttente()==1)   livre.setVisibility(View.VISIBLE);

            dialogue = detailBox.show();


            dateoperation.setText(getString(R.string.dateoperation) + DAOBase.formatter.format(operation.getDateoperation()));
            client.setText("Client : " + operation.getClient());

            fermer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogue.dismiss();
                }
            });

            if (operation.getAnnuler()==1)   {
                annuler.setVisibility(View.GONE);
                livre.setVisibility(View.GONE);
            }

            livre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT) || operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS)){
                        operation_livre(operation) ;
                    }

                    if (operation.getAttente()==1) {
                        Intent intent = new Intent(getActivity(), AccueilActivity.class) ;
                        intent.putExtra("OPERATION",operation.getId_externe()) ;
                        intent.putExtra("LIVRE",true) ;
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //Toast.makeText(getActivity(),operation.getId() + "", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }

                    else if ((operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT) ||operation.getTypeOperation_id().equals(OperationDAO.AUTRE_DETTE) || operation.getTypeOperation_id().equals(OperationDAO.ACHAT) || operation.getTypeOperation_id().equals(OperationDAO.VENTE))  && operation.getPayer()==0) {
                        rembourser_credit(operation) ;
                    }

                }
            });

            imp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operation.getTypeOperation_id().equals(OperationDAO.VENTE) && operation.getAttente()==0)imprimeTicket(operation);
                    else if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT))imprimeFacture(operation);
                    else if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS))imprimeFacture(operation);
                    else imprimeDoc(operation);
                }
            });

            annuler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operation_annuler(operation) ;
                }
            });

        }
        else {

            ScrollView sc = (ScrollView) getActivity().getLayoutInflater().inflate(R.layout.operationdetails1,null);
            detailBox.setView(sc);

            TextView description = (TextView) sc.findViewById(R.id.description);
            TextView montant = (TextView) sc.findViewById(R.id.montant);
            TextView mode = (TextView) sc.findViewById(R.id.mode);
            TextView client = (TextView) sc.findViewById(R.id.client);
            TextView banque = (TextView) sc.findViewById(R.id.banque);


            TextView dateoperation = (TextView) sc.findViewById(R.id.dateVente);
            ImageButton livre = (ImageButton) sc.findViewById(R.id.livre);
            ImageButton annuler = (ImageButton) sc.findViewById(R.id.annuler);
            ImageButton imp = (ImageButton) sc.findViewById(R.id.imp);
            Button fermer = (Button) sc.findViewById(R.id.fermer);

            description.setText(operation.getDescription());
            montant.setText(String.valueOf(operation.getMontant()));
            detailBox.setTitle(getString(R.string.notransact) + operation.getId()) ;

            mode.setText(getString(R.string.mode) + operation.getModepayement());
            CompteBanque compteBanque = new CompteBanqueDAO(getActivity()).getOne(operation.getComptebanque_id()) ;
            if (compteBanque!=null)banque.setText(getString(R.string.bk) + compteBanque.getLibelle());


            dateoperation.setText(getString(R.string.dateoperation) + DAOBase.formatter.format(operation.getDateoperation()));
            client.setText("Client : " + operation.getClient());

            fermer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogue.dismiss();
                }
            });

            if (operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT)) livre.setVisibility(View.VISIBLE);
            else  livre.setVisibility(View.GONE);
            if (operation.getTypeOperation_id().startsWith(OperationDAO.CMD)) livre.setVisibility(View.VISIBLE);

            livre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT) || operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS)){
                        operation_livre(operation) ;
                    }

                    if (operation.getAttente()==1) {
                        Intent intent = new Intent(getActivity(), AccueilActivity.class) ;
                        intent.putExtra("OPERATION",operation.getId_externe()) ;
                        intent.putExtra("LIVRE",true) ;
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //Toast.makeText(getActivity(),operation.getId() + "", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }

                    else if ((operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT) ||operation.getTypeOperation_id().equals(OperationDAO.AUTRE_DETTE) || operation.getTypeOperation_id().equals(OperationDAO.ACHAT) || operation.getTypeOperation_id().equals(OperationDAO.VENTE))  && operation.getPayer()==0) {
                        rembourser_credit(operation) ;
                    }

                }
            });

            imp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operation.getTypeOperation_id().equals(OperationDAO.VENTE) && operation.getAttente()==0)imprimeTicket(operation);
                    else if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT))imprimeFacture(operation);
                    else if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS))imprimeFacture(operation);
                    else imprimeDoc(operation);
                }
            });

            annuler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operation_annuler(operation) ;
                }
            });

            dialogue = detailBox.show() ;

        }

    }

    private void imprimeFacture(final Operation operation) {

            final EditText edittext = new EditText(getActivity());
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setMessage(getString(R.string.docpdf));
            alert.setTitle(getString(R.string.ficname));

            alert.setView(edittext);
            edittext.setText(R.string.commande);

            alert.setPositiveButton(R.string.valider, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //What ever you want to do with the value
                    String name = edittext.getText().toString();
                    Utiles.createandDisplayOperationPdf(operation, name, getActivity(),false);
                }
            });

            alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });

            if (operations.size()!=0)alert.show();
            else Toast.makeText(getActivity(), R.string.anyop, Toast.LENGTH_LONG).show();

    }

    private void rembourser_credit(final Operation operation) {
        ScrollView sc = (ScrollView) getActivity().getLayoutInflater().inflate(R.layout.rembouser_creditpanel, null);
        detailBox = new AlertDialog.Builder(getActivity());
        detailBox.setView(sc);


        final TextView et_netaPayer = (TextView) sc.findViewById(R.id.net);
        final TextView et_remise = (TextView) sc.findViewById(R.id.remise);
        et_mtrecue = (EditText) sc.findViewById(R.id.recu);
        final TextView et_arendre = (TextView) sc.findViewById(R.id.rendre);
        TextView client = (TextView) sc.findViewById(R.id.client);
         Button valider = ( Button) sc.findViewById(R.id.valider);
         Button annuler = ( Button) sc.findViewById(R.id.annuler);
        Button buttonDevise = (Button) sc.findViewById(R.id.devise);
        Button fermer = (Button) sc.findViewById(R.id.fermer);
        final TextView et_ttc = new TextView(getActivity()) ;

        CommercialDAO commercialDAO = new CommercialDAO(getActivity()) ;
        Commercial commercial = commercialDAO.getOne(operation.getCommercialid());
        client.setText(operation.getClient());

        Button money1 = (Button) sc.findViewById(R.id.money1);

        money1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_mtrecue.requestFocus() ;
                showMoney(v);
            }
        });


        if (deviseDAO.getReference()!=null){
            money1.setText(deviseDAO.getReference().getCodedevise());
        }


        spinner_banque = (Spinner) sc.findViewById(R.id.banque);
        RadioGroup modegroupe = (RadioGroup) sc.findViewById(R.id.modegroup);
        final LinearLayout lignecheque = (LinearLayout) sc.findViewById(R.id.lignecheque);
        numcheque = (EditText) sc.findViewById(R.id.numcheque);

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
                        Log.e(MODE,VIREMENT) ;
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


        buttonDevise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviseListActivity.class) ;
                intent.putParcelableArrayListExtra(DEVISE,deviseOperations) ;
                startActivityForResult(intent,CHOOSE_DEVISE_BAR_REQUEST);
            }
        });


        ArrayList<Operation> payements = operationDAO.getMany(operation.getId_externe());

        double total =  operation.getMontant() -operation.getRemise();
        for (int i = 0; i < payements.size(); i++) {
            total -= payements.get(i).getMontant() ;
        }
        final double prixTotal = total;
        et_ttc.setText(Utiles.formatMtn(total));
        et_netaPayer.setText(Utiles.formatMtn2(total));

        detailBox.setTitle(getString(R.string.notransact) + operation.getId());
        et_ttc.setText(Utiles.formatMtn(total));

        if (operation.getRecu() - operation.getMontant() + operation.getRemise() > 0)
            et_arendre.setText(Utiles.formatMtn(operation.getRecu() - operation.getMontant() + operation.getRemise()));
        else et_arendre.setText("0");


        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double remise = 0 ;
                if (!et_remise.getText().toString().equals("")) remise = Double.parseDouble(et_remise.getText().toString()) ;

                if (adapter.getCount()>0){
                    Log.i("DEBUG",et_remise.getText().toString()) ;
                    double recu = 0 ;

                    if (spinner_banque.getSelectedItemPosition()==0 && spinner_banque.getVisibility()==View.VISIBLE && cb.size()>0) {
                        Toast.makeText(getActivity(), getString(R.string.baquerror), Toast.LENGTH_LONG).show();
                    }
                    else if (cb.size()==0) cpt_bk = 0 ;
                    else if (spinner_banque.getVisibility()!=View.VISIBLE) cpt_bk = 0 ;
                    else{
                        cpt_bk = cb.get(spinner_banque.getSelectedItemPosition()).getId_externe() ;
                        numerocheque = numcheque.getText().toString() ;
                    }

                    if (MODE.equals(CHEQUE) && numerocheque.length()==0){
                        Toast.makeText(getActivity(), getString(R.string.saisirnumcheque), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (et_mtrecue.getText().length()>0) recu = Double.parseDouble(et_mtrecue.getText().toString()) ;

                    if(recu==0){
                        Toast.makeText(getActivity(), getString(R.string.data_errors7), Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(remise>prixTotal){
                        Toast.makeText(getActivity(), getString(R.string.data_errors4), Toast.LENGTH_LONG).show();
                        return;
                    }
                    else{
                        ArrayList<Operation> payements = operationDAO.getMany(operation.getId_externe());

                        double total =  0 ;
                        for (int i = 0; i < payements.size(); i++) {
                            total += payements.get(i).getMontant() ;
                        }
                        double rest =  operation.getMontant() - total ;

                        CaisseDAO caisseDAO = new CaisseDAO(getActivity()) ;
                        Operation payement = new Operation() ;
                        if (recu>rest)payement.setMontant(rest);
                        else payement.setMontant(recu);
                        payement.setOperation_id(operation.getId_externe());
                        payement.setCaisse_id(operation.getCaisse_id());
                        payement.setClient(operation.getClient());
                        payement.setModepayement(MODE);
                        payement.setEntree(operation.getEntree());
                        if (operation.equals(OperationDAO.EMPRUNT)) payement.setEntree(0);
                        payement.setToken(caisseDAO.getFirst().getCode()+"/"+payement.getMontant()+"/"+System.currentTimeMillis());
                        payement.setTypeOperation_id(OperationDAO.REGLEMENT);
                        if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS) || operation.getTypeOperation_id().equals(OperationDAO.ACHAT)) payement.setDescription(getString(R.string.rglmtachat));
                        else if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT) || operation.getTypeOperation_id().equals(OperationDAO.VENTE)) payement.setDescription(getString(R.string.rglmtvt));
                        else if (operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT)) payement.setDescription(getString(R.string.rglmtemp));
                        payement.setNumcheque(numerocheque);
                        if (!MODE.equals(ESPECE))payement.setComptebanque_id(cb.get(spinner_banque.getSelectedItemPosition()).getId_externe());
                        long id = operationDAO.add(payement) ;
                        if (id>0){
                            if (deviseOperations!=null){
                                for (int i = 0; i < deviseOperations.size(); i++) {
                                    DeviseOperation deviseOperation = deviseOperations.get(i) ;
                                    deviseOperation.setOperation_id(id);
                                    deviseOperationDAO.add(deviseOperation) ;
                                }
                            }
                            Toast.makeText(getActivity(), R.string.operation_success, Toast.LENGTH_SHORT).show();
                        }
                        if (recu-remise >= prixTotal){
                            operation.setPayer(1);
                            operation.setEtat(0);
                            operation.setRemise(remise);
                            if (operationDAO.update(operation) > 0) {
                                //Toast.makeText(getActivity(), R.string.operation_success, Toast.LENGTH_SHORT).show();
                            }
                        }
                        refresh();
                        dialogu.dismiss();
                        dialogue.dismiss();
                    }
                }
                else
                    Toast.makeText(getActivity(), getString(R.string.data_errors1), Toast.LENGTH_LONG).show();
            }
        });

        et_remise.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    double remise = Double.parseDouble(s.toString());
                    double netapayer = 0;
                    double recu = 0;

                    if (prixTotal - remise >= 0)
                        et_netaPayer.setText(Utiles.formatMtn2(prixTotal - remise));
                    else {
                        try {
                            Toast.makeText(getActivity(), R.string.remiseincorrectte, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        et_netaPayer.setText(et_ttc.getText());
                    }

                    if (et_netaPayer.getText().length() > 0)
                        netapayer = Double.parseDouble(et_netaPayer.getText().toString());
                    if (et_mtrecue.getText().length() > 0)
                        recu = Double.parseDouble(et_mtrecue.getText().toString());

                    if (recu - netapayer >= 0) et_arendre.setText(Utiles.formatMtn(recu - netapayer));
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
                if (s.length() > 0) {
                    double netapayer = 0;
                    double recu = 0;

                    if (et_netaPayer.getText().length() > 0)
                        netapayer = Double.parseDouble(et_netaPayer.getText().toString());
                    if (et_mtrecue.getText().length() > 0)
                        recu = Double.parseDouble(et_mtrecue.getText().toString());

                    if (recu - netapayer >= 0) et_arendre.setText(Utiles.formatMtn(recu - netapayer));
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
                if (s.length() > 0) {
                    double netapayer = 0;
                    double recu = 0;

                    if (et_netaPayer.getText().length() > 0)
                        netapayer = Double.parseDouble(s.toString());
                    if (et_mtrecue.getText().length() > 0)
                        recu = Double.parseDouble(et_mtrecue.getText().toString());

                    if (recu - netapayer >= 0) et_arendre.setText(Utiles.formatMtn(recu - netapayer));
                    else et_arendre.setText("0");
                }
            }
        });

        dialogu = detailBox.show() ;


        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogu.dismiss();
            }
        });
    }




    public void showMoney(View view){
        money_init() ;
        achat = false ;
        builder = new android.support.v7.app.AlertDialog.Builder(getActivity()) ;
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
                if (et_mtrecue!=null && et_mtrecue.hasFocus())et_mtrecue.setText("0");
                alert.dismiss();
                showMoney(null);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel() ;
            }
        });

        if (et_mtrecue!=null && et_mtrecue.hasFocus()){
            String p = et_mtrecue.getText().toString() ;
            if (p.length()==0) et_mtrecue.setText("0");
            total.setText(et_mtrecue.getText().toString());
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


        // Si le total afficher est superieur à zero
        if (!total.getText().equals("0")){

            prix = Double.parseDouble(total.getText().toString()) ;
            if (prix>v) prix -= v ;

            if (et_mtrecue!=null && et_mtrecue.hasFocus())et_mtrecue.setText(String.valueOf(prix));

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_DEVISE_BAR_REQUEST) {
            if (resultCode!=getActivity().RESULT_OK) return;
            deviseOperations = data.getParcelableArrayListExtra(DEVISE);
            double ttc = data.getDoubleExtra("MTN",0);
            Log.e("TOTAL", String.valueOf(ttc)) ;
            if (et_mtrecue!=null && et_mtrecue.hasFocus()) et_mtrecue.setText(String.valueOf(ttc));
            //if (et_mtverse!=null && et_mtverse.hasFocus()) et_mtverse.setText(String.valueOf(ttc));
        }

    }

    private void imprimeDoc(final Operation operation) {
        final CharSequence[] items = { getString(R.string.pdf), getString(R.string.xls), getString(R.string.fermer) };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        //builder.setTitle(getString(R.string.action));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                String libelle = " " ;
                if (operation.getTypeOperation_id().equals(OperationDAO.VENTE)) libelle = "" ;
                if (items[item].equals(getString(R.string.pdf))) {
                    imprimePDFDoc(libelle);
                } else if (items[item].equals(getString(R.string.xls))) {
                    imprimeExcelDoc(libelle);
                } else {
                    dialog.dismiss();
                }
            }
        });
        android.support.v7.app.AlertDialog alertDialog = builder.show();
    }

    private void operation_livre(final Operation operation) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()) ;
        alertDialog.setTitle(R.string.app_name) ;
        if (operation.getPayer() == 0)alertDialog.setMessage(R.string.cmdpayer) ;
        else alertDialog.setMessage(R.string.livrecmd) ;
        alertDialog.setPositiveButton(R.string.livree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogue.dismiss();
                /*
                if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT)) operation.setTypeOperation_id(OperationDAO.VENTE);
                else if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS)) operation.setTypeOperation_id(OperationDAO.ACHAT);
                operation.setRecu(operation.getMontant());
                if (operationDAO.update(operation)>0) Toast.makeText(getActivity(), R.string.opupdate, Toast.LENGTH_SHORT).show();

                refresh();
                */
                Intent intent = null ;
                if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS)|| operation.getTypeOperation_id().equals(OperationDAO.ACHAT)) intent = new Intent(getActivity(), AchatActivity.class) ;
                else  intent = new Intent(getActivity(), AccueilActivity.class) ;
                intent.putExtra("OPERATION",operation.getId()) ;
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }) ;

        alertDialog.setNegativeButton(R.string.fermer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }) ;
        final AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
            }
        }) ;
        dialog.show();
    }

    private void operation_annuler(final Operation operation) {
        final MouvementDAO mouvementDAO = new MouvementDAO(getActivity()) ;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()) ;
        alertDialog.setTitle(R.string.app_name) ;
        alertDialog.setMessage(R.string.annulerop) ;
        alertDialog.setPositiveButton(R.string.annuler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogue!=null) dialogue.dismiss();
                if (operation.getEtat()<2){
                    if (operation.getAttente()==0){
                        operation.setAnnuler(1);
                        operation.setDateannulation(new Date());
                        if (operationDAO.update(operation)>0) {
                            mouvementDAO.updateMany(operation) ;
                            Toast.makeText(getActivity(), R.string.opupdate, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else operationDAO.delete(operation.getId()) ;
                    refresh();
                }
                else {
                    if (Utiles.isConnected(getActivity())){
                        AnnuleOperationTask annuleOperationTask = new AnnuleOperationTask(operation) ;
                        annuleOperationTask.execute() ;
                    }
                    else
                        Toast.makeText(getActivity(), R.string.noconnexion, Toast.LENGTH_SHORT).show();
                }
            }
        }) ;
        alertDialog.setNegativeButton(R.string.fermer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }) ;
        final AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.my_accent));
                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.my_accent));
            }
        }) ;
        dialog.show();
    }


    private void imprimeTicket(final Operation operation) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()) ;
        alertDialog.setTitle(R.string.app_name) ;
        alertDialog.setMessage(R.string.imprimermsg) ;
        alertDialog.setPositiveButton(R.string.imp, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (preferences.getBoolean("imprimenteexterne",true)) {
                    try {
                        if(preferences.getString("papier","").equals(R.string.petit)){
                            PrinterUtils printUtils = new PrinterUtils(getActivity()) ;
                            printUtils.printTicket(operation.getId_externe(),1);
                        }
                        else{
                            PrinterUtilsGrand printerUtilsGrand = new PrinterUtilsGrand(getActivity()) ;
                            printerUtilsGrand.printTicket(operation.getId_externe(),1);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.e("ICI","1") ;
                }
                else{
                    try {
                        PrintPDA printPDA = new PrintPDA(getActivity()) ;
                        printPDA.printTicket(operation.getId_externe(),1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        PrintPDAMobiPrint3 printPDAMobiPrint3 = new PrintPDAMobiPrint3(getActivity());
                        printPDAMobiPrint3.printTicket(operation.getId_externe(),1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("ICI","2") ;
                }


                if (!preferences.getBoolean("imprimenteexterne",true)){

                    try {
                        PrintP800 printP800 = new PrintP800(getActivity());
                        printP800.printTicket(operation.getId_externe(),1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (UnsatisfiedLinkError e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(getActivity(), R.string.imprimlancee, Toast.LENGTH_SHORT).show();
            }
        }) ;
        alertDialog.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }) ;
        alertDialog.show() ;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }




    @Override
    public void onStart() {
        super.onStart();

        //À¶ÑÀÎ´´ò¿ª£¬´ò¿ªÀ¶ÑÀ
        /*
        if( mService.isBTopen() == false  && preferences.getBoolean("imprimenteexterne",true))
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 112);
        }
        */
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



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //mParent = (OperationActivity) getActivity();
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public void interval(int i, String dateDebut, String dateFin) {
        if (getActivity() == null) return;
        if (operationDAO == null) operationDAO = new OperationDAO(getActivity());
        try {
            if (dateDebut != null && dateFin != null) operations = operationDAO.getAll(Integer.parseInt(mParam2), DAOBase.formatter2.parse(dateDebut), DAOBase.formatter2.parse(dateFin));
            else operations = operationDAO.getAll(Integer.parseInt(mParam2), null, new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("SIZE", String.valueOf(operations.size()));
        if (operations != null) {
            adapter = new ListeOperationAdapter(operations);
            lv.setAdapter(adapter);
            //Toast.makeText(getActivity(), operations.size() + "", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }

    private void refresh() {
        if (operationDAO == null) operationDAO = new OperationDAO(getActivity());
        operations = operationDAO.getAll(Integer.parseInt(mParam2), null, new Date());
        if (operations != null) {
            adapter = new ListeOperationAdapter(operations);
            lv.setAdapter(adapter);
            //Toast.makeText(getActivity(), operations.size() + "", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }

    public void imprimePDFDoc(final String titre) {
        final EditText edittext = new EditText(getActivity());
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setMessage(getString(R.string.docpdf));
        alert.setTitle(getString(R.string.ficname));

        alert.setView(edittext);
        edittext.setText(titre);

        alert.setPositiveButton(R.string.valider, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String name = edittext.getText().toString();
                if (mParam2.equals("3") || mParam2.equals("4")) Utiles.createandDisplayOperationPdf(operations, name, getActivity(),false);
                else Utiles.createandDisplayOperationPdf(operations, name, getActivity(),true);
            }
        });

        alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        if (operations.size()!=0)alert.show();
        else Toast.makeText(getActivity(), R.string.anyop, Toast.LENGTH_LONG).show();
    }

    public void imprimeExcelDoc(final String titre) {
        final EditText edittext = new EditText(getActivity());
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setMessage(getString(R.string.docexcel));
        alert.setTitle(getString(R.string.ficname));

        alert.setView(edittext);
        edittext.setText(titre);

        alert.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String name = edittext.getText().toString();
                if (mParam2.equals("3") || mParam2.equals("4"))Utiles.createandDisplayOperationExcel(operations, name, getActivity(),false);
                else Utiles.createandDisplayOperationExcel(operations, name, getActivity(),true);
            }
        });

        alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        if (operations.size()!=0)alert.show();
        else Toast.makeText(getActivity(), R.string.anyop, Toast.LENGTH_LONG).show();
    }

    public void filtrer(String newText) {
        ArrayList<Operation> data = new ArrayList<Operation>() ;

        if(operations != null)
            for(int i = 0 ; i < operations.size() ; i++){
                operation = operations.get(i) ;
                if(operation.getClient().toLowerCase().contains(newText) || operation.getDescription().toLowerCase().contains(newText)) data.add(operation) ;
            }
        adapter = new ListeOperationAdapter(data) ;
        lv.setAdapter(adapter) ;
        adapter.notifyDataSetChanged();
    }

    public void setId(String idexterne) {
        operations = operationDAO.getMany(Long.valueOf(idexterne));
        if (operations != null) {
            adapter = new ListeOperationAdapter(operations);
            lv.setAdapter(adapter);
            //Toast.makeText(getActivity(), operations.size() + "", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
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

    static class ViewHolder {
        TextView numcpt;
        TextView numpiece;
        TextView mte1;
        TextView mte2;
        TextView t1;
        TextView t2;
        TextView sync;
        TextView date;
        TextView type;
        ImageView imageView = null;

        public ViewHolder(View v) {
            imageView = (ImageView) v.findViewById(R.id.imageview);
            date = (TextView) v.findViewById(R.id.date);
            t1 = (TextView) v.findViewById(R.id.t1);
            mte1 = (TextView) v.findViewById(R.id.mte1);
            t2 = (TextView) v.findViewById(R.id.t2);
            mte2 = (TextView) v.findViewById(R.id.mte2);
            sync = (TextView) v.findViewById(R.id.sync);
            numpiece = (TextView) v.findViewById(R.id.numpiece);
            numcpt = (TextView) v.findViewById(R.id.numcpt);
            type = (TextView) v.findViewById(R.id.type);
        }
    }

    public class ListeOperationAdapter extends BaseAdapter {

        ArrayList<Operation> operations = new ArrayList<Operation>();

        public ListeOperationAdapter(ArrayList<Operation> usg) {
            operations = usg;
            if (operations.size() <= 0) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
            }
        }

        @Override
        public int getCount() {
            if (operations == null) return 0;
            return operations.size();
        }

        public Operation getItem(int position) {
            return operations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return operations.get(position).getId();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.operationitem, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Operation operation = (Operation) getItem(position);
            TypeOperationDAO typeOperationDAO = new TypeOperationDAO(getActivity()) ;


            if (operation != null) {
                Log.e("TO",operation.getTypeOperation_id()) ;
                if (operation.getTypeOperation_id().startsWith(OperationDAO.DEPENSE)) {
                    holder.imageView.setImageResource(R.mipmap.ic_retrait);
                    holder.type.setText(operation.getDescription());
                    holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte1.setText("0");
                }
                else if (operation.getTypeOperation_id().startsWith(OperationDAO.PLACEMENT)) {
                    holder.imageView.setImageResource(R.mipmap.ic_retrait);
                    if (operation.getComptebanque_id()>0){
                        CompteBanque compteBanque = new CompteBanqueDAO(getActivity()).getOneByIdExterne(operation.getComptebanque_id()) ;
                        if (compteBanque!=null)holder.type.setText(getString(R.string.placemnt) + compteBanque.getLibelle() + "(" + compteBanque.getCode() + ")");
                    }
                    else holder.type.setText(R.string.placemtcaisse);
                    holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte1.setText("0");
                } else if (operation.getTypeOperation_id().startsWith(OperationDAO.IMMOBILISATION)) {
                    holder.imageView.setImageResource(R.mipmap.ic_retrait);
                    holder.type.setText(operation.getDescription());
                    holder.mte2.setText(Utiles.formatMtn(operation.getMontant()));
                    holder.mte1.setText("0");
                } else if (operation.getTypeOperation_id().equals(OperationDAO.VENTE)) {
                    holder.imageView.setImageResource(R.mipmap.ic_depot);
                    holder.type.setText(R.string.vente);
                    holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte2.setText("0");
                } else if (operation.getTypeOperation_id().equals(OperationDAO.REGLEMENT)) {
                    holder.imageView.setImageResource(R.mipmap.ic_depot);
                    holder.type.setText(operation.getDescription());
                    holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte2.setText("0");
                }
                else if (operation.getTypeOperation_id().equals(OperationDAO.ACHAT)) {
                    holder.imageView.setImageResource(R.mipmap.ic_retrait);
                    holder.type.setText(R.string.achat);
                    holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte1.setText("0");
                }
                else if (operation.getTypeOperation_id().equals(OperationDAO.SORTIE_STOCK)) {
                    holder.imageView.setImageResource(R.mipmap.ic_retrait);
                    holder.type.setText(R.string.sortiestock);
                    holder.mte2.setText("0");
                    holder.mte1.setText("0");
                }else if (operation.getTypeOperation_id().equals(OperationDAO.CH_EXC)) {
                    holder.imageView.setImageResource(R.mipmap.ic_retrait);
                    holder.type.setText(operation.getDescription());
                    holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte1.setText("0");
                }else if (operation.getTypeOperation_id().equals(OperationDAO.CH_FN)) {
                    holder.imageView.setImageResource(R.mipmap.ic_retrait);
                    holder.type.setText(operation.getDescription());
                    holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte1.setText("0");
                }else if (operation.getTypeOperation_id().equals(OperationDAO.PRODUIT_EXC)) {
                    holder.imageView.setImageResource(R.mipmap.ic_depot);
                    holder.type.setText(operation.getDescription());
                    holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte2.setText("0");
                }else if (operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT)) {
                    holder.imageView.setImageResource(R.mipmap.ic_depot);
                    holder.type.setText(operation.getDescription());
                    holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte2.setText("0");
                }else if (operation.getTypeOperation_id().equals(OperationDAO.BQ)) {
                    if (operation.getEntree()==1){
                        holder.imageView.setImageResource(R.mipmap.ic_depot);
                        holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                        holder.mte2.setText("0");
                    }
                    else{
                        holder.imageView.setImageResource(R.mipmap.ic_retrait);
                        holder.mte1.setText("0");
                        holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    }
                    holder.type.setText(operation.getDescription());
                }else if (operation.getTypeOperation_id().equals(OperationDAO.PRODUIT_FIN)) {
                    holder.imageView.setImageResource(R.mipmap.ic_depot);
                    holder.type.setText(operation.getDescription());
                    holder.mte1.setText(Utiles.formatMtn(operation.getMontant()));
                    holder.mte2.setText("0");
                } else if (operation.getTypeOperation_id().equals(OperationDAO.CMDFRNSS)) {
                    holder.imageView.setImageResource(R.mipmap.ic_journal);
                    holder.type.setText(R.string.cmdfr);
                    holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte1.setText("0");
                }


                if (operation.getAttente()==1)  {
                    holder.type.setText(operation.getClient());
                    holder.imageView.setImageResource(R.mipmap.ic_attente);
                }

                if (operation.getPayer()==0 && operation.getTypeOperation_id().equals(OperationDAO.VENTE))  {
                    holder.type.setText(getActivity().getString(R.string.vente));
                }
                if (operation.getPayer()==0 && operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT))  {
                    holder.type.setText(getActivity().getString(R.string.dettede) + operation.getClient());
                }

                if (operation.getEtat()==2) holder.sync.setVisibility(View.VISIBLE);
                else holder.sync.setVisibility(View.GONE);

                //Toast.makeText(getActivity(), operation.getAnnuler() + "", Toast.LENGTH_SHORT).show();
                if (operation.getAnnuler()==1)  holder.imageView.setImageResource(R.mipmap.ic_annuler);
                else if (operation.getPayer()==0) {
                    holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte2.setText("0");
                    ArrayList<Operation> payements = operationDAO.getMany(operation.getId_externe());

                    double total =  0 ;
                    for (int i = 0; i < payements.size(); i++) {
                        total += payements.get(i).getMontant() ;
                    }

                    if (getActivity() !=null && getActivity() instanceof OperationActivity){
                        if (payements.size()>0)holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise() - payements.get(0).getMontant()));
                        else holder.mte2.setText(Utiles.formatMtn(operation.getMontant()));
                        holder.t1.setText(R.string.mte1) ;
                        holder.t2.setText(R.string.restant) ;
                    }
                    else {
                        holder.mte2.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise() - total));
                        holder.t1.setText(R.string.mte1) ;
                        holder.t2.setText(R.string.restant) ;
                    }

                    if (operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT)){
                        holder.imageView.setImageResource(R.mipmap.ic_dette);
                    }
                    else {
                        holder.imageView.setImageResource(R.mipmap.ic_acredit);
                    }


                    if (operation.getEntree()==0) {
                        holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    }
                    else {
                        holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    }
                    if (total>=operation.getMontant()-operation.getRemise()){
                        operation.setPayer(1);
                        operation.setEtat(0);
                        operationDAO.update(operation) ;
                    }
                }

                if (operation.getTypeOperation_id().startsWith(OperationDAO.CMD)) {
                    double avance = 0;
                    ArrayList<Operation> payements = operationDAO.getMany(operation.getId_externe());
                    for (int i = 0; i < payements.size(); i++) {
                        avance += payements.get(i).getMontant() ;
                    }

                    if (operation.getTypeOperation_id().equals(OperationDAO.CMDCLNT))holder.type.setText(R.string.cmdclt);
                    else holder.type.setText(R.string.cmdfr);

                    holder.imageView.setImageResource(R.mipmap.ic_journal);
                    holder.mte1.setText(Utiles.formatMtn(operation.getMontant()-operation.getRemise()));
                    holder.mte2.setText(Utiles.formatMtn(avance));
                    holder.t1.setText(R.string.mte1) ;
                    holder.t2.setText(R.string.restant) ;
                }

                holder.numpiece.setText(String.valueOf(operation.getId()));
                holder.numpiece.setVisibility(View.GONE);
                holder.date.setText(DAOBase.formatterj.format(operation.getDateoperation()));

            }

            return convertView;
        }

        public void addData(ArrayList<Operation> usg) {
            if (usg == null) operations = new ArrayList<Operation>();
            operations = usg;

            if (this.operations.size() <= 0) empty.setVisibility(View.VISIBLE);
            else empty.setVisibility(View.GONE);
            notifyDataSetChanged();
        }


        public void update(ArrayList<Operation> operations) {
            if (operations.size() == 0) return;
            this.operations = operations;

            if (this.operations.size() <= 0) empty.setVisibility(View.VISIBLE);
            else empty.setVisibility(View.GONE);
            notifyDataSetChanged();
        }
    }





    public class VenteProduitAdapter extends BaseAdapter {

        ArrayList<Mouvement> mouvements = null ;
        long entre = 0 ;

        public VenteProduitAdapter(ArrayList<Mouvement> pv, long entre){
            mouvements = pv ;
            this.entre = entre ;
        }


        @Override
        public int getCount() {
            if (mouvements!=null)   return mouvements.size() ;
            else return 0;
        }

        @Override
        public Mouvement getItem(int position) {
            return mouvements.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return mouvements.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProduitVenteViewHolder holder = null;

            if(convertView == null) {
                convertView  = mInflater.inflate(R.layout.mouvementitem, null);
                holder = new ProduitVenteViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ProduitVenteViewHolder)convertView.getTag();
            }
            Mouvement mouvement = (Mouvement) getItem(position);


            if(mouvement != null) {
                holder.produitTextView.setText(mouvement.getProduit());
                holder.quantiteTextView.setText(String.valueOf(mouvement.getQuantite()));
                if (mouvement.getEntree()==1){
                    holder.prixTextView.setText(Utiles.formatMtn(mouvement.getPrixV()));
                    holder.prixTotalTV.setText(Utiles.formatMtn(mouvement.getQuantite() * mouvement.getPrixV()));
                }
                else {
                    holder.prixTextView.setText(Utiles.formatMtn(mouvement.getPrixA()));
                    holder.prixTotalTV.setText(Utiles.formatMtn(mouvement.getQuantite() * mouvement.getPrixA()));
                }

                if (mouvement.getEtat()>0) holder.etatTV.setBackgroundColor(getResources().getColor(R.color.state2));
                else holder.etatTV.setBackgroundColor(getResources().getColor(R.color.state0));
            }

            return convertView;
        }

        public void addData(ArrayList<Mouvement> pvs) {
            mouvements = pvs ;
            notifyDataSetChanged();
        }

    }



    static class ProduitVenteViewHolder{
        TextView produitTextView ;
        TextView prixTextView ;
        TextView quantiteTextView ;
        TextView prixTotalTV ;
        TextView etatTV ;

        public ProduitVenteViewHolder(View v) {
            produitTextView = (TextView)v.findViewById(R.id.produitTV);
            prixTextView = (TextView)v.findViewById(R.id.prixTv);
            quantiteTextView = (TextView)v.findViewById(R.id.quantiteTV);
            prixTotalTV = (TextView)v.findViewById(R.id.prixTotalTV);
            etatTV = (TextView)v.findViewById(R.id.etat);
        }
    }






    public class AnnuleOperationTask extends AsyncTask<Void, Integer, Boolean> {
        Operation operation = null ;

        public AnnuleOperationTask(Operation operation) {
            this.operation = operation ;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            FormBody.Builder formBuilder = new FormBody.Builder() ;
            formBuilder.add(OperationHelper.TABLE_KEY, String.valueOf(operation.getId_externe()));
            formBuilder.add("payer", String.valueOf(operation.getPayer()));

            String result = "" ;


            try {
                result = Utiles.POST(Url.getAnnuleOpUrl(getActivity()),formBuilder.build());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("URL", Url.getAnnuleOpUrl(getActivity()));

            Log.e("Reponse",result) ;

            if (result.split(":").length == 2 && result.contains("OK")) {
                operation.setAnnuler(1);
                int i = operationDAO.update(operation) ;
                if (i<=0) return false ;
            }
            else return false ;

            return true ;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(getActivity(),getResources().getString(R.string.op_ration_annul_e), Toast.LENGTH_LONG).show();
                refresh();
            }
            else
                Toast.makeText(getActivity(), getString(R.string.echec_del), Toast.LENGTH_LONG).show();
            getActivity().dismissDialog(AccueilActivity.PROGRESS_DIALOG_ID);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            operationDAO = new OperationDAO(getActivity());
            getActivity().showDialog(AccueilActivity.PROGRESS_DIALOG_ID);
        }
    }
}
