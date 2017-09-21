package pv.projects.mediasoft.com.pventes.utiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bw.spdev.PosDevCallBackController;
import com.bw.spdev.RspCode;
import com.example.libtest.PosUtils;
import com.odm.misc.MiscDevice;
import com.odm.misc.MiscUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
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
public class PrintP800 {
    protected static final String TAG = "Print Utils";
    private Handler mHandler;
    MouvementDAO mouvementDAO = null ;
    PointVenteDAO pointVenteDAO = null ;
    private String msgFin = null;
    private String societeNom = null;
    private String societeAdresse = null;
    String msg;
    Context context;
    public static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy H:m:s") ;
    String macAddress = null ;
    WifiManager wifiManager ;
    private OperationDAO operationDAO;

    String LOGID = "PRINER";
    byte printData[];
    int  printlen;
    //private int  request_code=0;
    PrintThread rth = null;


    DeviseDAO deviseDAO = null ;
    DeviseOperationDAO deviseOperationDAO = null ;

    public PrintP800(Context context){
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = wInfo.getMacAddress();
        pointVenteDAO = new PointVenteDAO(context) ;
        operationDAO = new OperationDAO(context) ;
        mouvementDAO = new MouvementDAO(context) ;
        pointVenteDAO = new PointVenteDAO(context) ;
        deviseDAO = new DeviseDAO(context) ;
        deviseOperationDAO = new DeviseOperationDAO(context) ;
        this.context = context ;
        societeNom = pointVenteDAO.getLast().getLibelle() ;
        societeNom = preferences.getString("societeNom", pointVenteDAO.getLast().getLibelle()) ;
        societeAdresse = context.getString(R.string.tel) + pointVenteDAO.getLast().getTel() + context.getString(R.string.adresse) + pointVenteDAO.getLast().getVille()+ ", "+ pointVenteDAO.getLast().getPays();
        msgFin = preferences.getString("messagefinal", context.getString(R.string.gooddays)) ;
        initPrintP800();
    }


    
    public void printTicket(long id, int dupilcata){
        Operation operation = operationDAO.getOneExterne(id) ;
        if (operation==null) return;
        Partenaire partenaire = new PartenaireDAO(context).getOne(operation.getPartenaire_id()) ;
        CompteBanque compteBanque = new CompteBanqueDAO(context).getOne(operation.getComptebanque_id()) ;

        ArrayList<Mouvement> mouvements = mouvementDAO.getMany(operation.getId()) ;
        Devise reference = deviseDAO.getReference() ;

        Calendar cal = Calendar.getInstance() ;
        msg = "########################";
        msg+= "\n";
        msg +=  aligntCenter(societeNom) ;
        msg+= "\n";
        msg+= context.getString(R.string.telephone) + pointVenteDAO.getLast().getTel();
        msg+= "\n";
        if (operation.getAttente()==0) msg += "########################" ;
        else msg +=                           "######### DEVIS ########" ;
        msg+= "\n";
        msg+= "Date      : "+ formatter.format(new Date());
        msg+= "\n";
        msg+= "Ticket No : "+ operation.getId()+ "/" + cal.get(Calendar.YEAR);
        msg+= "\n";
        if (partenaire==null)msg+= "Client    : "+ operation.getClient();
        else msg+= "Client    : "+ operation.getClient() + "("  + partenaire.getContact() + ")";
        msg+= "\n";
        msg += "-----------------------------------------------";
        msg += "\n";
        msg+= context.getString(R.string.enetep800);
        msg += "\n";
        msg += "-----------------------------------------------";
        msg+= "\n";
        int n = mouvements.size() ;
        for (int i = 0; i < n; i++){
            Mouvement mv = mouvements.get(i) ;
            String ligne = String.valueOf(mv.getQuantite()) + "         " + Utiles.formatMtn(mv.getPrixV())   + "        " + Utiles.formatMtn(mv.getPrixV()*mv.getQuantite())  ;
            msg+= mv.getProduit() + " \n" ;
            msg += alignRight(ligne) ;
            msg+= "\n";
        }
        msg += "-----------------------------------------------";
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
            msg+= totaux1(Utiles.formatMtn(operation.getMontant()) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getMontant())) ;
        msg+= "\n";
        msg+= context.getString(R.string.print_net_a_payer);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getMontant()-operation.getRemise()) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getMontant()-operation.getRemise())) ;
        msg+= "\n";
        msg += "-----------------------------------------------";
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
        msg += "-----------------------------------------------";
        msg+= "\n";
        msg+= context.getString(R.string.print_rendu);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getRecu()-(operation.getMontant()-operation.getRemise())) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getRecu()-(operation.getMontant()-operation.getRemise()))) ;
        msg+= "\n";

        if (operation.getDescription().length()>0){
            msg += "-----------------------------------------------";
            msg += "\n";
            msg += operation.getDescription() ;
            msg += "\n";
        }
        msg += "-----------------------------------------------";
        msg += "\n";
        msg += msgFin;
        msg += "\n";
        msg += "########################";
        msg += "\n";
        msg += context.getString(R.string.copyright);
        msg += "\n";
        msg += "\n";
        msg += "\n";
        msg += "\n";
        msg += "\n";
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




    private String aligntCenter(String ligne) {
        String result = "" ;
        if (ligne.length()>=74) return ligne ;
        for (int i = 0; i < 74 - ligne.length(); i++) {
            if (i==(74-ligne.length())/2)result += ligne ;
            else result += " " ;
        }
        return result;
    }


    private String alignRight(String ligne) {
        String result = "" ;
        for (int i = 0; i < 91 - ligne.length(); i++) {
            result += " " ;
        }
        result = result + ligne ;
        return result;
    }

    private String alignRight1(String ligne) {
        String result = "" ;
        for (int i = 0; i < 97 - ligne.length(); i++) {
            result += " " ;
        }
        result = result + ligne ;
        return result;
    }

    public String name(String name){
        int n = 11 ;
        int siz = 0 ;
        Log.e("NAME1",name) ;
        name = (name.length()>9)?(name.substring(0,8)+".."):(name) ;
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

    public String totaux(String text){
        int n = 61;
        int siz = 0 ;
        text = (text.length()>n)?(text.substring(0,n-1)):(text) ;
        siz = n-text.length() ;
        String space = "";
        for (int i =0 ; i < siz; ++i){
            space = " " + space ;
        }
        return space + text ;
    }

    public String totaux1(String text){
        int n = 72;
        int siz = 0 ;
        text = (text.length()>n)?(text.substring(0,n-1)):(text) ;
        siz = n-text.length() ;
        String space = "";
        for (int i =0 ; i < siz; ++i){
            space = " " + space ;
        }
        return space + text ;
    }



    public void printTicket(String msg) {

        try {
            PosUtils.sp.SpDevSetAppContext(context);
            rth = new PrintThread();
            rth.startRunning();
            rth.start();

            PosUtils.sp.SpDevRelease();
            PowerOffDevice();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


    private Context getApplicationContext(){
        return this.context;
    }

    private void initPrintP800(){
        PowerOnDevice();
        PosUtils.InitDev();
        PosUtils.sys.SetCmdSendMaxWT(1000);
        PosUtils.sys.SetRspRecvMaxWT(1000);
    }

    static byte[] string2Unicode(String s) {
        try {
            byte[] bytes = s.getBytes("unicode");
            byte[] bt = new byte[bytes.length - 2];
            for (int i = 2, j = 0; i < bytes.length - 1; i += 2, j += 2) {
                bt[j] = (byte) (bytes[i + 1] & 0xff);
                bt[j + 1] = (byte) (bytes[i] & 0xff);
            }
            return bt;
        } catch (Exception e) {
            try {
                byte[] bt = s.getBytes("GBK");
                return bt;
            } catch (UnsupportedEncodingException e1) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }
    }



    private void ShowMsg(String msg){
        Toast.makeText(this.context, msg,
                Toast.LENGTH_SHORT).show();
    }


    public byte[] Bitmap2Bytes(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    private class PrintThread extends Thread
    {
        private boolean mStopRunning = false;
        //private AlertDialog show;
        public void stopRunning() {
            mStopRunning = true;
        }
        public void startRunning(){
            mStopRunning = false;
        }
        public void run()
        {
            Looper.prepare();
            super.run();

            toPrint();
            //or
            //toPrintBmp();

            Looper.loop();
            rth = null;
        }
    };

    private String getDateTimeStr(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sDateFormat.format(new java.util.Date());
        return datetime;
    }

    void toPrint() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSec = c.get(Calendar.SECOND);

        PosUtils.sp.SpDevSetAppContext(getApplicationContext());
        // init,set the callbak
        PosUtils.printer.Init(new DeviceResponseHandlerImpl(),
                getApplicationContext());
        PosUtils.printer.ClearPrintData();// ��մ�ӡ����
        PosUtils.printer.SetStep(1000);// ���ò���
        PosUtils.printer.SetPrinterPara((short) 1250);// ���ô�ӡ�Ҷ�

        StringBuilder time = (new StringBuilder()
                .append((mHour < 10) ? "0" + mHour : mHour).append(":")
                .append((mMinute < 10) ? "0" + mMinute : mMinute).append(":")
                .append((mSec < 10) ? "0" + mSec : mSec));
        String sTime = time.toString();

        StringBuilder data = (new StringBuilder()
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
                .append("/").append((mDay < 10) ? "0" + mDay : mDay)
                .append("/").append(mYear));
        String sData = data.toString();
        PosUtils.printer.SetProperty(0);//是否粗体
        PosUtils.printer.SetFontSize(24);
        PosUtils.printer.AddString("\n");
        PosUtils.printer.AddString(msg);
        PosUtils.printer.AddString("\r\n ");
        PosUtils.printer.AddString("\r\n ");
        PosUtils.printer.AddString("\r\n ");
        PosUtils.printer.Print();
    }

    class DeviceResponseHandlerImpl extends PosDevCallBackController
    {
        @Override
        public int onPrinterPrint(int status)
        {

            switch(status)
            {
                case RspCode.RSPOK:
                    Log.e(null,"print finish");
                    break;
                case RspCode.RET_PRINTER_ERR_COMERR:
                    Log.e(null,"RET_PRINTER_ERR_COMERR");
                    break;
                case RspCode.RET_PRINTER_ERR_BMPLOST:
                    Log.e(null,"RET_PRINTER_ERR_BMPLOST");
                    break;
                case RspCode.RET_PRINTER_ERR_NOPAPER:
                    Log.e(null,"RET_PRINTER_ERR_NOPAPER");
                    break;
                case RspCode.RET_PRINTER_ERR_HT:
                    Log.e(null,"RET_PRINTER_ERR_HT");
                    break;
                default:
                case RspCode.RET_PRINTER_ERR_OTHER:
                    Log.e(null,"RET_PRINTER_ERR_OTHER");
                    break;
            }
            return 0;
        }
    }


    void toPrintBmp()
    {
        PosUtils.sp.SpDevSetAppContext(getApplicationContext());
        //set the callback
        PosUtils.printer.Init(new DeviceResponseHandlerImpl(),getApplicationContext());
        PosUtils.printer.ClearPrintData();//clean the data
        PosUtils.printer.SetStep(1000);//set the step
        PosUtils.printer.SetPrinterPara((short) 1250);//set the gray

        //hunter1

//         FileService fp = new FileService();
//         fp.open(Environment.getExternalStorageDirectory().getPath()+"/test.bmp", "rw");
//         int size = (int) fp.size();
//
//         Log.d("ToPrintBmp", Environment.getExternalStorageDirectory().getPath()+"/test.bmp");
//         Log.e(null,"file size:"+size);
//
//         byte ptr[] = fp.read(size);
//         fp.close();

        byte [] ptr1 = readFromRaw();
        PosUtils.printer.AddBmpData(ptr1, ptr1.length, 40, 0);
        PosUtils.printer.Print();
        //PosUtils.printer.PrintBmp(Environment.getExternalStorageDirectory().getPath()+"/ic_launcher.bmp", 40,0 );

    }

    public byte[] readFromRaw()
    {
        String res = "";
        try{

            //�õ���Դ�е�Raw������
            InputStream in = context.getResources().openRawResource(R.raw.test);

            //�õ����ݵĴ�С
            int length = in.available();

            byte [] buffer = new byte[length];

            //��ȡ����
            in.read(buffer);


            //�ر�
            in.close();
            return buffer;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private  Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    void PowerOffDevice()
    {
        Class<?> c;

        String postty = null;
        try {

            //Log.e(null,"1");
            c  = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class);
            postty = (String) get.invoke(c, "ro.config.postty");

            if(postty==null||postty.isEmpty()==true)
            {

                PosUtils.misPos.setPinLow(MiscUtil.POS_PIN_PWR);
                PosUtils.misPos.setPinLow(MiscUtil.POS_PIN_PWD);

                return ;
            }
            else if(postty.length()==0)
            {
                PosUtils.misPos.setPinLow(MiscUtil.POS_PIN_PWR);
                PosUtils.misPos.setPinLow(MiscUtil.POS_PIN_PWD);
                return ;

            }
            else
            {
                return;
            }
        } catch (Exception e)
        {


        }
    }

    void PowerOnDevice()
    {
        Class<?> c;

        String postty = null;
        try {

            //Log.e(null,"1");
            c  = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class);
            postty = (String) get.invoke(c, "ro.config.postty");

            if(postty==null||postty.isEmpty()==true)
            {
                PosUtils.misPos = new MiscDevice(MiscUtil.POS_MISC_DEV, MiscUtil.POS_MISC_IO);
                PosUtils.misPos.setPinHigh(MiscUtil.POS_PIN_PWR);
                PosUtils.misPos.setPinHigh(MiscUtil.POS_PIN_PWD);
                return ;
            }
            else if(postty.length()==0)
            {
                PosUtils.misPos = new MiscDevice(MiscUtil.POS_MISC_DEV, MiscUtil.POS_MISC_IO);
                PosUtils.misPos.setPinHigh(MiscUtil.POS_PIN_PWR);
                PosUtils.misPos.setPinHigh(MiscUtil.POS_PIN_PWD);
                return ;

            }
            else
            {
                return;
            }
        } catch (Exception e)
        {


        }
    }

}
