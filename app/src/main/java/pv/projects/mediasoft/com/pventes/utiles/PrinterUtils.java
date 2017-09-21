package pv.projects.mediasoft.com.pventes.utiles;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.activities.ParametreActivity;
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
 * Created by mediasoft on 22/08/2017.
 */

public class PrinterUtils {

    private final String bluetoothConfig = "bluetoothConfig";
    private final String bPrinter = "DP-HT201";
    private String path;
    private String matriculeCollecteur = null;
    private String msgFin = null;
    private String societeNom = null;
    private String societeAdresse = null;
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    public static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy H:m:s") ;

    String msg;
    String nomImprimante;
    MouvementDAO mouvementDAO = null ;
    DeviseDAO deviseDAO = null ;
    DeviseOperationDAO deviseOperationDAO = null ;
    OperationDAO operationDAO = null ;
    PointVenteDAO pointVenteDAO = null ;
    Context context = null ;
    BluetoothService mService = null ;
    BluetoothDevice con_dev = null;


    public PrinterUtils(Context context){
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        nomImprimante = preferences.getString(bluetoothConfig, bPrinter) ;
        pointVenteDAO = new PointVenteDAO(context) ;
        mouvementDAO = new MouvementDAO(context) ;
        operationDAO = new OperationDAO(context) ;
        deviseDAO = new DeviseDAO(context) ;
        deviseOperationDAO = new DeviseOperationDAO(context) ;
        this.context = context ;
        societeNom = preferences.getString("societeNom", pointVenteDAO.getLast().getLibelle()) ;
        societeAdresse = context.getString(R.string.tel) + pointVenteDAO.getLast().getTel() + context.getString(R.string.adresse) + pointVenteDAO.getLast().getVille()+ ", "+ pointVenteDAO.getLast().getPays();
        msgFin = preferences.getString("messagefinal", context.getString(R.string.gooddays)) ;

        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Abacus/logo.bmp";
        path = preferences.getString(ParametreActivity.LOGO,path) ;

        mService = new BluetoothService(context, mHandler);
        //À¶ÑÀ²»¿ÉÓÃÍË³ö³ÌÐò
        if( mService.isAvailable() == false ){
            Toast.makeText(context,R.string.bluetootheteint , Toast.LENGTH_LONG).show();
        }

        else{
            if( mService.isBTopen() == false)
            {
                Toast.makeText(context, R.string.bluetootheteint, Toast.LENGTH_SHORT).show();
            }
        }
        Log.e("PRINT","Start") ;
    }



    public void printTicket(long id,int duplicata){
        Operation operation = operationDAO.getOneExterne(id) ;
        if (operation==null) return;
        Partenaire partenaire = new PartenaireDAO(context).getOne(operation.getPartenaire_id()) ;
        CompteBanque compteBanque = new CompteBanqueDAO(context).getOne(operation.getComptebanque_id()) ;

        ArrayList<Mouvement> mouvements = mouvementDAO.getMany(operation.getId()) ;
        Devise reference = deviseDAO.getReference() ;
        Calendar cal = Calendar.getInstance() ;

        msg = "" ;
        msg+= "\n";
        msg += "##############################" ;
        msg+= "\n";
        msg +=  aligntCenter(societeNom) ;
        msg+= "\n";
        msg+= context.getString(R.string.telephone) + pointVenteDAO.getLast().getTel();
        msg+= "\n";
        if (operation.getAttente()==0) msg += "################################" ;
        else msg +=                           "############# DEVIS ############" ;
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
        msg+= context.getString(R.string.enteteputil);
        msg += "\n";
        msg += "--------------------------------";
        msg+= "\n";
        int n = mouvements.size() ;
        for (int i = 0; i < n; i++){
            Mouvement mv = mouvements.get(i) ;
            String ligne = String.valueOf(mv.getQuantite()) + " " + Utiles.formatMtn(mv.getPrixV())   + " " + Utiles.formatMtn(mv.getPrixV()*mv.getQuantite())  ;
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
        msg+= context.getString(R.string.nbrearticle) ;
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
        if (compteBanque!=null){
            msg+= context.getString(R.string.print_bk) + compteBanque.getLibelle();
            msg+= "\n";
        }
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
        msg += "\n";
        msg += "\n";
        msg += "\n";

        // Connection au périphérique
        connection() ;
        Log.e("PRINT","end") ;
    }


    private void connection() {
        // Cancel discovery because it's costly and we're about to connect
        mService.cancelDiscovery();

        Log.e("BlueTooth","Connect to " + nomImprimante) ;
        con_dev = mService.getDevByName(nomImprimante);
        mService.connect(con_dev);
    }

    private String alignRight(String ligne) {
        String result = "" ;
        for (int i = 0; i < 33 - ligne.length(); i++) {
            result += " " ;
        }
        result = result + ligne ;
        return result;
    }

    private String alignRight1(String ligne) {
        String result = "" ;
        for (int i = 0; i < 32 - ligne.length(); i++) {
            result += " " ;
        }
        result = result + ligne ;
        return result ;
    }

    private String aligntCenter(String ligne) {
        String result = "" ;
        if (ligne.length()>=30) return ligne ;
        for (int i = 0; i < 30 - ligne.length(); i++) {
            if (i==(30-ligne.length())/2)result += ligne ;
            else result += " " ;
        }
        return result;
    }


    public String name(String name){
        int n = 11 ;
        int siz = 0 ;
        Log.e("NAME1",name) ;
        name = (name.length()>6)?(name.substring(0,6)+".."):(name) ;
        siz = n-name.length() ;
        for (int i =0 ; i < siz; ++i){
            //name += " " ;
        }
        Log.e("NAME2",name) ;
        return name ;
    }

    public String depenselibelle(String name){
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

    public String totaux(String totaux){
        int n = 22 ;
        int siz = 0 ;
        totaux = (totaux.length()>21)?(totaux.substring(0,20)):(totaux) ;
        siz = n-totaux.length() ;
        for (int i =0 ; i < siz; ++i){
            totaux = " " + totaux ;
        }
        return totaux ;
    }

    public String totaux1(String totaux){
        int n = 20 ;
        int siz = 0 ;
        totaux = (totaux.length()>18)?(totaux.substring(0,17)):(totaux) ;
        siz = n-totaux.length() ;
        for (int i =0 ; i < siz; ++i){
            totaux = " " + totaux ;
        }
        return totaux ;
    }


    public void print(String msg) {
        this.msg = msg ;
        try {
            if( this.msg.length() > 0 ){
                mService.sendMessage(this.msg+"\n", "GBK");
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


    private void printImage() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(384);
        pg.initPaint();
        File file = new File(path) ;


        Log.e("PATH",path) ;
        if (file.exists()){
            try{
                pg.drawImage(50, 0, path);
                sendData = pg.printDraw();
                mService.write(sendData);//´òÓ¡byteÁ÷Êý¾Ý
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Log.e("PRINT",path + " " + file.exists()) ;
    }




    /**
     * ´´½¨Ò»¸öHandlerÊµÀý£¬ÓÃÓÚ½ÓÊÕBluetoothServiceÀà·µ»Ø»ØÀ´µÄÏûÏ¢
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (message.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //ÒÑÁ¬½Ó
                            Toast.makeText(context, R.string.connexionreussi,Toast.LENGTH_SHORT).show();
                            // Impression d'image
                            printImage() ;

                            // Impression du text
                            print(msg);

                            int i =0 ;
                            while (i<100000) i++ ;

                            // Fermeture des ports ouverts
                            mService.stop();
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
                    Toast.makeText(context, R.string.conneionperdubt,Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸
                    Toast.makeText(context, R.string.impossconnexbt,Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };




}
