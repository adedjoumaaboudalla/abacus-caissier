package pv.projects.mediasoft.com.pventes.utiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.nbbse.mobiprint3.Printer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseOperationDAO;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.Devise;
import pv.projects.mediasoft.com.pventes.model.DeviseOperation;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Partenaire;


/**
 * Created by Mayi on 12/01/2016.
 */
public class PrintPDAMobiPrint3 {

    private final Printer print;
    private final String societeNom;
    private final PointVenteDAO pointVenteDAO;
    private SharedPreferences preferences;
    protected static final String TAG = "Print Utils";
    private Handler mHandler;
    OperationDAO operationDAO = null ;
    private String msgFin = null;
    private String agence = null;
    private String societeAdresse = null;
    String msg = "";
    Context context;
    public static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy H:m:s") ;
    String macAddress = null ;
    WifiManager wifiManager ;
    private MouvementDAO mouvementDAO;

    DeviseDAO deviseDAO = null ;
    DeviseOperationDAO deviseOperationDAO = null ;

    public PrintPDAMobiPrint3(Context context){
        preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = wInfo.getMacAddress();
        operationDAO = new OperationDAO(context) ;
        deviseDAO = new DeviseDAO(context) ;
        deviseOperationDAO = new DeviseOperationDAO(context) ;
        this.context = context ;
        mouvementDAO = new MouvementDAO(context) ;
        print = Printer.getInstance();
        pointVenteDAO = new PointVenteDAO(context) ;
        societeAdresse = preferences.getString("societe", "21 38 22 24 / 61 23 92 92") ;
        societeNom = preferences.getString("societeNom", pointVenteDAO.getLast().getLibelle()) ;
        msgFin = preferences.getString("messagefinal", context.getString(R.string.gooddays)) ;    }

    public String name(String name){
        int n = 11 ;
        int siz = 0 ;
        Log.e("NAME1",name) ;
        name = (name.length()>11)?(name.substring(0,10)):(name) ;
        siz = n-name.length() ;
        for (int i =0 ; i < siz; ++i){
            name += " " ;
        }
        Log.e("NAME2",name) ;
        return name ;
    }


    public String operationlibelle(String name){
        int n = 17 ;
        int siz = 0 ;
        Log.e("NAME1",name) ;
        name = (name.length()>17)?(name.substring(0,16)):(name) ;
        siz = n-name.length() ;
        for (int i =0 ; i < siz; ++i){
            name += " " ;
        }
        Log.e("NAME2",name) ;
        return name ;
    }

    public String prix(String prix){
        int n = 6 ;
        int siz = 0 ;
        prix = (prix.length()>6)?(prix.substring(0,5)):(prix) ;
        siz = n-prix.length() ;
        for (int i =0 ; i < siz; ++i){
            prix = " " + prix ;
        }
        return prix ;
    }

    public String montant(String prix){
        int n = 6 ;
        int siz = 0 ;
        prix = (prix.length()>6)?(prix.substring(0,5)):(prix) ;
        siz = n-prix.length() ;
        for (int i =0 ; i < siz; ++i){
            prix = " " + prix ;
        }
        return prix ;
    }

    public String quantite(String prix){
        int n = 5 ;
        int siz = 0 ;
        prix = (prix.length()>5)?(prix.substring(0,4)):(prix) ;
        siz = n-prix.length() ;
        for (int i =0 ; i < siz; ++i){
            prix = " " + prix ;
        }
        return prix ;
    }


    public void printTicketBillet(String msg) {

        //print.printBitmap(context.getResources().openRawResource(R.raw.logoalide));
        String path = "" ;
        if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mediasoft/logo.bmp";
        }
        else if (!preferences.getBoolean("stockage",true)){
            path = context.getFilesDir().getAbsolutePath() + "/Mediasoft/logo.bmp";
        }
        else{

        }

        File file = new File(path) ;
        if (file.exists())  print.printBitmap(path);
        //else print.printBitmap(context.getResources().openRawResource(R.raw.logoalide));

        printTicket(msg);
        //printTicket(msg);

    }



    public void printTicket(long id, int dupilcata){
        Operation operation = operationDAO.getOneExterne(id) ;
        if (operation==null) return;
        Partenaire partenaire = new PartenaireDAO(context).getOne(operation.getPartenaire_id()) ;
        CompteBanque compteBanque = new CompteBanqueDAO(context).getOne(operation.getComptebanque_id()) ;

        ArrayList<Mouvement> mouvements = mouvementDAO.getMany(operation.getId()) ;
        Devise reference = deviseDAO.getReference() ;

        Calendar cal = Calendar.getInstance() ;
        if (operation.getAttente()==1) msg += "             DEVIS             " ;
        msg += "################################" ;
        msg+= "\n";
        msg +=  aligntCenter(societeNom) ;
        msg+= "\n";
        msg+= context.getString(R.string.telephone) + pointVenteDAO.getLast().getTel();
        msg+= "\n";
        msg += "################################" ;
        msg+= "\n";
        msg+= "Date      : "+ formatter.format(new Date());
        msg+= "\n";
        msg+= "Ticket No : "+ operation.getId()+ "/" + cal.get(Calendar.YEAR);
        msg+= "\n";
        if (partenaire==null)msg+= "Client    : "+ operation.getClient();
        else msg+= "Client    : "+ operation.getClient() + "("  + partenaire.getContact() + ")";
        msg+= "\n";
        msg += "--------------------------------";
        msg += "\n";
        msg+= context.getString(R.string.entetepm3);
        msg += "\n";
        msg += "--------------------------------";
        msg+= "\n";
        int n = mouvements.size() ;
        for (int i = 0; i < n; i++){
            Mouvement mv = mouvements.get(i) ;
            String ligne = String.valueOf(mv.getQuantite()) + "  " + Utiles.formatMtn(mv.getPrixV())   + "  " + Utiles.formatMtn(mv.getPrixV()*mv.getQuantite())  ;
            msg+= mv.getProduit() + " \n" ;
            msg += alignRight(ligne) ;
            msg+= "\n";
        }
        msg += "--------------------------------";
        msg+= "\n";
        msg+= context.getString(R.string.total);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getMontant()) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getMontant())) ;
        msg+= "\n";
        msg+= context.getString(R.string.nbrearticle)  ;
        msg+= totaux(Utiles.formatMtn(mouvements.size()));
        msg+= "\n";
        msg+= context.getString(R.string.print_remise);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getRemise()) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getRemise())) ;
        msg+= "\n";
        msg+= context.getString(R.string.print_net_a_payer);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getMontant()-operation.getRemise()) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getMontant()-operation.getRemise())) ;
        msg+= "\n";
        msg += "--------------------------------";
        msg+= "\n";
        msg+= context.getString(R.string.print_recu);
        msg+= "\n";
        ArrayList<DeviseOperation> deviseOps = deviseOperationDAO.getMany(operation.getId_externe());
        n = deviseOps.size() ;
        for (int i = 0; i < n; i++){
            Devise devise = deviseDAO.getOne(deviseOps.get(i).getDevise_id()) ;
            String ligne = Utiles.formatMtn(deviseOps.get(i).getMontant()) + " " +  devise.getCodedevise()  ;
            msg += alignRight1(ligne) ;
            msg+= "\n";
        }
        if (n==0) msg += alignRight1(String.valueOf(operation.getRecu())) ;
        msg+= "\n";
        msg+= context.getString(R.string.print_mode);
        msg+= operation.getModepayement();
        msg+= "\n";
        if (compteBanque!=null)msg+= context.getString(R.string.print_bk) + compteBanque.getLibelle();
        msg+= "\n";
        msg += "--------------------------------";
        msg+= "\n";
        msg+= context.getString(R.string.print_rendu);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getRecu()-(operation.getMontant()-operation.getRemise())) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getRecu()-(operation.getMontant()-operation.getRemise()))) ;
        msg+= "\n";

        if (operation.getDescription().length()>0){
            msg += "--------------------------------";
            msg += "\n";
            msg += operation.getDescription() ;
            msg += "\n";
        }

        msg += "--------------------------------";
        msg += "\n";
        msg += msgFin;
        msg += "\n";
        msg += "################################";
        msg += "\n";
        msg += context.getString(R.string.copyright);
        msg += "\n";

        new Thread(
                new Runnable(){

                    @Override
                    public void run() {
                        try {
                            printTicket(msg);
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }).start();
    }




    public String totaux(String totaux){
        int n = 20 ;
        int siz = 0 ;
        totaux = (totaux.length()>20)?(totaux.substring(0,19)):(totaux) ;
        siz = n-totaux.length() ;
        for (int i =0 ; i < siz; ++i){
            totaux = " " + totaux ;
        }
        return totaux ;
    }



    public String totaux1(String totaux){
        int n = 18 ;
        int siz = 0 ;
        totaux = (totaux.length()>18)?(totaux.substring(0,17)):(totaux) ;
        siz = n-totaux.length() ;
        for (int i =0 ; i < siz; ++i){
            totaux = " " + totaux ;
        }
        return totaux ;
    }

    private String aligntCenter(String ligne) {
        String result = "" ;
        if (ligne.length()>=32) return ligne ;
        for (int i = 0; i < 32 - ligne.length(); i++) {
            if (i==(32-ligne.length())/2)result += ligne ;
            else result += " " ;
        }
        return result;
    }

    private String alignRight(String ligne) {
        String result = "" ;
        for (int i = 0; i < 32 - ligne.length(); i++) {
            result += " " ;
        }
        result = result + ligne ;
        Log.e(ligne,String.valueOf(ligne.length())) ;
        Log.e(result,String.valueOf(result.length())) ;
        return result;
    }
    private String alignRight1(String ligne) {
        String result = "" ;
        for (int i = 0; i < 30 - ligne.length(); i++) {
            result += " " ;
        }
        result = result + ligne ;
        Log.e(ligne,String.valueOf(ligne.length())) ;
        Log.e(result,String.valueOf(result.length())) ;
        return result;
    }


    public void printTicket(String msg) {

        try {
            print.printText(msg);
            print.printEndLine();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


    private Context getApplicationContext(){
        return this.context;
    }



    private void ShowMsg(String msg){
        Toast.makeText(this.context, msg,
                Toast.LENGTH_SHORT).show();
    }

    public void printImage(){
        String path = "" ;
        if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mediasoft/logo.bmp";
        }
        else if (!preferences.getBoolean("stockage",true)){
            path = context.getFilesDir().getAbsolutePath() + "/Mediasoft/logo.bmp";
        }
        else{

        }

        File file = new File(path) ;
        if (file.exists())  print.printBitmap(path);
        //else print.printBitmap(context.getResources().openRawResource(R.raw.logoalide));
    }

}
