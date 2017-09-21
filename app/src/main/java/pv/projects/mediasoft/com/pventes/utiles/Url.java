package pv.projects.mediasoft.com.pventes.utiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Mayi on 07/10/2015.
 */

public class Url {
    public static String ipConfig = "ipserver" ;
    //public static String serverIp = "http://1 92.168.0.104/" ;
    public static String serverIp = "50.63.13.84/~abacusweb/pvente";
    //public static String serverIp = "quickprojet.mediasoftmoney.com";
    //local
    //public static String serverIp = "192.168.10.6/pvente_serviceweb" ;
    //public static String licenceConnexion = serverIp + "/service_web/checkLicence.php" ;


    public static String getWebUrl() {
        String result = "http://50.63.13.84/~abacusweb/abacus/cpanel";

        return result ;
    }

    public static String getCheckCaisseValidation(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/checkactivation.php";

        return result ;
    }


    public static String getCheckAndInitCaisse(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/loading.php";

        return result ;
    }

    public static String getPostMouvementUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/addMouvement.php";

        return result ;
    }


    public static String getLoadAttente(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/loadattente.php";

        return result ;
    }

    public static String getPostOperationUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/addOperation.php";

        return result ;
    }

    public static String getAnnuleOpUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/annuleOperation.php";

        return result ;
    }



    public static String getPostProduitUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/addProduit.php";

        return result ;
    }


    public static String getPostPayementUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/addPayement.php";

        return result ;
    }




    public static String getPostPartenaireUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/addPartenaire.php";

        return result ;
    }




    public static String getPostCommercialUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/addCommercial.php";

        return result ;
    }



    public static String getDeleteProduit(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/deleteproduit.php";

        return result ;
    }


    public static String getPostCompteBanqueUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/addCompteBanque.php";

        return result ;
    }


    public static String getImageDirectory(Context context, String image) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/uploads/produits/"+ image;

        return result ;
    }


    public static String getSendImageUrl(Context context) {
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String result = "http://" + preferences.getString(ipConfig, serverIp) + "/service_web/addimageproduit.php";
        return result ;
    }


}
