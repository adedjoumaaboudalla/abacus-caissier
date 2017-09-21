package pv.projects.mediasoft.com.pventes.utiles;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;


/**
 * Created by Abdallah on 06/08/2015.
 */
public class Utiles {

    private static final String PV = "ABACUS ETATS";
    public static final String PV_PRODUIT_IMAGE_DIR = "PV_PRODUIT";
    public static final String PV_PRODUIT_IMAGE_TMP_DIR = "PV_TMP_PRODUIT";
    //public static String serverIp = "http://1 92.168.0.104/" ;
    public static String serverIp = "http:mediasofthome.com";
    //local
    //public static String serverIp = "http://192.168.43.78/";
    public static final SimpleDateFormat formatter = new SimpleDateFormat("EEE dd MM yyyy");

    public static final SimpleDateFormat mysql_format = new SimpleDateFormat("yyyy-mm-dd");
    public static final SimpleDateFormat french_format = new SimpleDateFormat("dd/mm/yyyy");
    private ArrayList<Operation> operations;

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }



    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }



    public static String POST(String url, RequestBody body) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String result = "";
        Log.e("URL", url);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        result = response.body().string();
        Log.e("Resulttt",response.body().toString()) ;

        try{
            result = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result ;
    }




    public static String GET(String url) throws IOException {
        String result = "";
        Log.e("URL", url);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        try{
            result = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.e("Resulttt",result) ;
        return result ;
    }


    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream,"UTF-8"));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }



    public static Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }


            BitmapFactory.Options o1 = new BitmapFactory.Options();
            o1.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(f), null, o1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static Uri getFile(Context context, String name, String dossier) {
        File mypath = new File(name);
        if (mypath.exists()) Log.e("DEBUG", mypath.getAbsolutePath());
        else Log.e("FILE NOT FOUND", mypath.getAbsolutePath());

        return Uri.fromFile(mypath);
    }


    public static Uri saveImageInternalStorage(String filepath, Context context, String name, String dossier) throws IOException {

        String fullPath = Environment.getDataDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/";

        Bitmap bitmapImage = BitmapFactory.decodeFile(filepath);

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(dossier, Context.MODE_PRIVATE);
        // Create imageDir
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File mypath = new File(directory, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, 100, 100, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return Uri.fromFile(mypath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        //Toast.makeText(context,directory.getAbsolutePath(),Toast.LENGTH_LONG).show();
        return null;
    }


    public static Uri saveImageInternalStorage(Bitmap bitmap, Context context, String name, String dossier) throws IOException {

        String fullPath = Environment.getDataDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/";

        Bitmap bitmapImage = bitmap;

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(dossier, Context.MODE_PRIVATE);
        // Create imageDir
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File mypath = new File(directory, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, 100, 100, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return Uri.fromFile(mypath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        //Toast.makeText(context,directory.getAbsolutePath(),Toast.LENGTH_LONG).show();
        return null;
    }


    public static Bitmap loadImageFromStorage(Context context, String name, String dossier) {

        //String fullPath = Environment.getDataDirectory().getAbsolutePath() +  "/" + context.getPackageName() + "/plaques/";

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(dossier, Context.MODE_PRIVATE);
        String fullPath = directory.getAbsolutePath();
        String[] p = name.split("/");
        Log.e("FILE", fullPath + "/" + p[p.length - 1]);

        try {
            File f = new File(fullPath, p[p.length - 1]);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //img.setImageBitmap(b);
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }


    public static int uploadFile(String uploadFilePath, final String uploadFileName, final Context context) {


        String fileName = uploadFilePath;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" + uploadFilePath);
            return 0;

        } else {
            int serverResponseCode = 0;
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(Url.getSendImageUrl(context));

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", uploadFileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename='" + uploadFileName + "'" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    String msg = "File Upload Completed.\n\n See uploaded file here : \n\n" + " http://www.androidexample.com/media/uploads/" + uploadFileName;
                    Log.e("DEBUGGGGGGGG", msg);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Upload file to server", "Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }


    public static void vibrate(Context context, int i) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(i);
    }

    public static String formatMtn(double mtn) {
        String retour = "";
        String l = String.format("%.2f", mtn);
        Log.e("PARSE", l);
        int n = l.length();
        int i = 0;
        int j = 0;
        int k = 0;
        j = n - 3;

        for (i = n - 3; i > 0; --i) {
            if (k % 3 == 0) {
                retour = l.substring(i, j) + " " + retour;
                Log.e("PARSE", retour);
                j = i;
            }
            k++;
        }
        Log.e("PARSE", retour);
        if (l.substring(n - 2, n).equals("00")) retour = l.substring(0, j) + " " + retour;
        else retour = l.substring(0, j) + " " + retour + "," + l.substring(n - 2, n);
        Log.e("PARSE", retour);
        return retour;
    }

    public static String formatMtn2(double mtn) {
        String retour = "";
        String l = String.format("%.2f", mtn);
        Log.e("PARSE", l);
        retour = l.replaceAll(",",".") ;
        Log.e("PARSE", retour);

        return retour;
    }

    public static Uri saveTemp(Bitmap bitmap, Context context) throws IOException {

        if (!isSdReadable()) {
            //Toast.makeText(context, context.getString(R.string.sdabsent), Toast.LENGTH_LONG).show();
            return null;
        }

        Bitmap bitmapImage = saveScaledPhotoToFile(bitmap);

        File mypath = null;

        String fullPath = Environment.getExternalStorageDirectory() + "";

        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileOutputStream fos = null;
        try {
            mypath = new File(dir, "temp.jpg");
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return Uri.fromFile(mypath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) fos.close();
        }

        return null;
    }


    public static Bitmap saveScaledPhotoToFile(Bitmap bPV) {
        //Convert your photo to a bitmap
        Bitmap photoBm = (Bitmap) bPV;
        //get its orginal dimensions
        int bmOriginalWidth = photoBm.getWidth();
        int bmOriginalHeight = photoBm.getHeight();
        double originalWidthToHeightRatio = 1.0 * bmOriginalWidth / bmOriginalHeight;
        double originalHeightToWidthRatio = 1.0 * bmOriginalHeight / bmOriginalWidth;
        //choose a maximum height
        //int maxHeight = 1024;
        int maxHeight = 125;
        //choose a max width
        //int maxWidth = 1024;
        int maxWidth = 125;
        //call the method to get the scaled bitmap
        photoBm = getScaledBitmap(photoBm, bmOriginalWidth, bmOriginalHeight,
                originalWidthToHeightRatio, originalHeightToWidthRatio,
                maxHeight, maxWidth);


        return photoBm;
    }


    public static boolean isSdReadable() {

        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
// We can read and write the media
            mExternalStorageAvailable = true;
            Log.i("isSdReadable", "External storage card is readable.");
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
// We can only read the media
            Log.i("isSdReadable", "External storage card is readable.");
            mExternalStorageAvailable = true;
        } else {
// Something else is wrong. It may be one of many other
// states, but all we need to know is we can neither read nor write
            mExternalStorageAvailable = false;
        }

        return mExternalStorageAvailable;
    }


    public static long getSize(Bitmap bitmap) throws IOException {

        if (!isSdReadable()) {
            //Toast.makeText(context, context.getString(R.string.sdabsent), Toast.LENGTH_LONG).show();
            return 0;
        }

        Bitmap bitmapImage = saveScaledPhotoToFile(bitmap);

        File mypath = null;

        String fullPath = Environment.getExternalStorageDirectory() + "";

        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileOutputStream fos = null;
        try {
            mypath = new File(dir, "temp.jpg");
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return mypath.length() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) fos.close();
        }

        return 0;
    }


    private static Bitmap getScaledBitmap(Bitmap bm, int bmOriginalWidth, int bmOriginalHeight, double originalWidthToHeightRatio, double originalHeightToWidthRatio, int maxHeight, int maxWidth) {
        if (bmOriginalWidth > maxWidth || bmOriginalHeight > maxHeight) {
            Log.e("TAG", "RESIZED bitmap TO %sx%s " + bm.getWidth() + " " + bm.getHeight());

            if (bmOriginalWidth > bmOriginalHeight) {
                bm = scaleDeminsFromWidth(bm, maxWidth, bmOriginalHeight, originalHeightToWidthRatio);
            } else if (bmOriginalHeight > bmOriginalWidth) {
                bm = scaleDeminsFromHeight(bm, maxHeight, bmOriginalHeight, originalWidthToHeightRatio);
            }

            Log.e("TAG", "RESIZED bitmap TO %sx%s " + bm.getWidth() + " " + bm.getHeight());
        }
        return bm;
    }

    private static Bitmap scaleDeminsFromHeight(Bitmap bm, int maxHeight, int bmOriginalHeight, double originalWidthToHeightRatio) {
        int newHeight = (int) Math.max(maxHeight, bmOriginalHeight * .55);
        int newWidth = (int) (newHeight * originalWidthToHeightRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    private static Bitmap scaleDeminsFromWidth(Bitmap bm, int maxWidth, int bmOriginalWidth, double originalHeightToWidthRatio) {
        //scale the width
        int newWidth = (int) Math.max(maxWidth, bmOriginalWidth * .75);
        int newHeight = (int) (newWidth * originalHeightToWidthRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }


    public static String getMD5EncryptedString(String encTarget) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }




    public static Uri saveImageExternalStorage(Bitmap bitmap, Context context,String name,String dossier) throws IOException {

        if (!isSdReadable()){
            //Toast.makeText(context, context.getString(R.string.sdabsent), Toast.LENGTH_LONG).show();
            return null ;
        }

        Bitmap bitmapImage = bitmap;

        File mypath = null ;

        String fullPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName() +  "/"+ dossier+"/"  ;

        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileOutputStream fos = null;
        try {
            Log.e("Save","Begin") ;
            mypath = new File(dir,name) ;
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.e("Save", mypath.getPath()) ;
            return Uri.fromFile(mypath) ;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos!=null)fos.close();
        }

        return null;
    }


    public static void createandDisplayOperationPdf(Operation operation, String name, Context context) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + formatter.format(new Date()).replace(' ', '_') + ".pdf";
            File file = new File(dir, name);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            writer.setPageEvent(new HeaderFooterPageEvent(context));

            //special font sizes
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            //document header attributes
            doc.addAuthor("Point de vente");
            doc.addCreationDate();
            doc.addProducer();
            //doc.addCreator("MySampleCode.com");
            doc.addTitle("Operations etat");
            doc.setPageSize(PageSize.LETTER);
            //specify column widths

            //open the document
            doc.open();

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            float[] columnWidths = {2f, 2f, 5f, 2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);


            //create a paragraph
            Paragraph paragraph = null;
            //paragraph = new Paragraph("LISTE DES OPERATIONS DE " + ets + " DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));
            paragraph.setAlignment(Element.ALIGN_CENTER);

            //insert column headings
            insertCell(table, "No", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Date", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Libelle", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Débit", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Crédit", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);


            //insert an empty row
            insertCell(table, "", Element.ALIGN_LEFT, 5, bfBold12);
            //create section heading by cell merging
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            /*
            for (int x = 0; x < operations.size(); x++) {

                insertCell(table, DAOBase.formatter.format(operations.get(x).getDateoperation()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(operations.get(x).getId()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, operations.get(x).getLibelle(), Element.ALIGN_LEFT, 1, bf12);

                if (operations.get(x).getTypeoperation()==1 || operations.get(x).getTypeoperation()==4){
                    insertCell(table, "0", Element.ALIGN_CENTER, 1, bf12);
                    insertCell(table, String.valueOf(operations.get(x).getMontant()), Element.ALIGN_CENTER, 1, bf12);
                }
                else {
                    insertCell(table, String.valueOf(operations.get(x).getMontant()), Element.ALIGN_CENTER, 1, bf12);
                    insertCell(table, "0", Element.ALIGN_CENTER, 1, bf12);
                }
                //insertCell(table, df.format(orderTotal), Element.ALIGN_RIGHT, 1, bf12);

            }
            */

            //add the PDF table to the paragraph
            paragraph.add(table);
            // add the paragraph to the document
            doc.add(paragraph);

            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }

        viewPdf(name, PV, context);
    }




    public static void createandDisplayOperationPdf(ArrayList<Operation> operations, String name, Context context, boolean bon) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;

            File dir = new File(path);
            if (!dir.exists())    dir.mkdirs();

            name = name + "_" + formatter.format(new Date()).replace(' ', '_') + ".pdf";
            File file = new File(dir, name);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            writer.setPageEvent(new HeaderFooterPageEvent(context));

            //special font sizes
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            //document header attributes
            doc.addAuthor(context.getResources().getString(R.string.app_name));
            doc.addCreationDate();
            doc.addProducer();
            //doc.addCreator("MySampleCode.com");
            doc.addTitle("Operations etat");
            doc.setPageSize(PageSize.LETTER);
            //specify column widths

            //open the document
            doc.open();

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            // Largeur des colonnes
            float[] columnWidths = {2f, 3f, 5f, 3f, 3f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);


            //create a paragraph
            Paragraph paragraph = null;
            if (ets == null)
                paragraph = new Paragraph("LISTE DES OPERATIONS DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));
            else
                paragraph = new Paragraph("LISTE DES OPERATIONS DE " + ets + " DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));
            paragraph.setAlignment(Element.ALIGN_CENTER);

            //insert column headings
            insertCell(table, "No", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Date", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Libelle", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Sortie", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Entrée", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);


            //insert an empty row
            insertCell(table, "", Element.ALIGN_LEFT, 5, bfBold12);
            //create section heading by cell merging
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            for (int x = 0; x < operations.size(); x++) {
                if (operations.get(x).getAttente()==1) continue;
                if (bon)if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CMDFRNSS) || operations.get(x).getTypeOperation_id().equals(OperationDAO.CMDCLNT))  continue;

                insertCell(table, String.valueOf(operations.get(x).getId()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, DAOBase.formatterj.format(operations.get(x).getDateoperation()), Element.ALIGN_CENTER, 1, bf12);

                if (operations.get(x).getTypeOperation_id().equals(OperationDAO.VENTE) && operations.get(x).getPayer()==0)insertCell(table, context.getString(R.string.ventecredi), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.VENTE))insertCell(table, context.getString(R.string.vente), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.AUTRES_DEP))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.EAU))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.ELECTRICITE))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.TEL))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.FRAIS_PERSO))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.IMPOT))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.LOYER))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.IMMOBILISATION))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.PLACEMENT))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.ACHAT))insertCell(table, context.getString(R.string.appro), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CMDCLNT))insertCell(table, context.getString(R.string.cmdclt), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CMDFRNSS))insertCell(table, context.getString(R.string.cmdfr), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CH_FN))insertCell(table, operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CH_EXC))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.PRODUIT_FIN))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.PRODUIT_EXC))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.TRANSPORT))insertCell(table,  operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);
                else insertCell(table,operations.get(x).getDescription(), Element.ALIGN_LEFT, 1, bf12);

                if (operations.get(x).getEntree()==1){
                    if (operations.get(x).getPayer()!=0)sortie += operations.get(x).getMontant()-operations.get(x).getRemise() ;
                    insertCell(table, "0", Element.ALIGN_RIGHT, 1, bf12);
                    insertCell(table, Utiles.formatMtn(operations.get(x).getMontant()), Element.ALIGN_RIGHT, 1, bf12);
                }
                else {
                    if (operations.get(x).getPayer()!=0)entree += operations.get(x).getMontant()-operations.get(x).getRemise() ;
                    insertCell(table, Utiles.formatMtn(operations.get(x).getMontant()), Element.ALIGN_RIGHT, 1, bf12);
                    insertCell(table, "0", Element.ALIGN_RIGHT, 1, bf12);
                }
                //insertCell(table, df.format(orderTotal), Element.ALIGN_RIGHT, 1, bf12);
            }


            insertCell(table, "Total", Element.ALIGN_LEFT, 3, bfBold12);
            insertCell(table, String.valueOf(entree), Element.ALIGN_RIGHT, 1, bfBold12);
            insertCell(table, String.valueOf(sortie), Element.ALIGN_RIGHT, 1, bfBold12);


            //add the PDF table to the paragraph
            paragraph.add(table);
            // add the paragraph to the document
            doc.add(paragraph);


            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }

        viewPdf(name, PV, context);
    }





    public static void createandDisplayOperationPdf(Operation operation, String name, Context context, boolean bon) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + formatter.format(new Date()).replace(' ', '_') + ".pdf";
            File file = new File(dir, name);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            writer.setPageEvent(new HeaderFooterPageEvent(context));

            //special font sizes
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            //document header attributes
            doc.addAuthor(context.getResources().getString(R.string.app_name));
            doc.addCreationDate();
            doc.addProducer();
            //doc.addCreator("MySampleCode.com");
            doc.addTitle("Commande");
            doc.setPageSize(PageSize.LETTER);
            //specify column widths

            //open the document
            doc.open();

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            // Largeur des colonnes
            float[] columnWidths = {2f, 5f, 3f, 3f, 3f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);


            //create a paragraph
            Paragraph paragraph = null;
            paragraph = new Paragraph("Commande DU " + formatter.format(operation.getDateoperation()));

            /*
            if (ets == null)
            else
            //    paragraph = new Paragraph("Commande DE " + ets + " DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));

            */
            paragraph.setAlignment(Element.ALIGN_CENTER);

            //insert column headings
            insertCell(table, "No", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Libelle", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Quantité", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Prix unitaire", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Prix total", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);


            //insert an empty row
            insertCell(table, "", Element.ALIGN_LEFT,5, bfBold12);
            //create section heading by cell merging
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ArrayList<Mouvement> mouvements = mouvementDAO.getMany(operation.getId_externe());;
            for (int x = 0; x < mouvements.size(); x++) {
                insertCell(table, String.valueOf(x+1), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, mouvements.get(x).getProduit(), Element.ALIGN_LEFT, 1, bf12);
                insertCell(table, String.valueOf(mouvements.get(x).getQuantite()), Element.ALIGN_LEFT, 1, bf12);
                insertCell(table, String.valueOf(mouvements.get(x).getPrixV()), Element.ALIGN_RIGHT, 1, bf12);
                insertCell(table, String.valueOf(mouvements.get(x).getPrixV()*mouvements.get(x).getQuantite()), Element.ALIGN_RIGHT, 1, bf12);

                //insertCell(table, df.format(orderTotal), Element.ALIGN_RIGHT, 1, bf12);
            }

            insertCell(table, "", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, "Total", Element.ALIGN_LEFT, 3, bfBold12);
            insertCell(table, Utiles.formatMtn(operation.getMontant()), Element.ALIGN_RIGHT, 1, bfBold12);


            insertCell(table, "", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, "Remise", Element.ALIGN_LEFT, 3, bfBold12);
            insertCell(table, Utiles.formatMtn(operation.getRemise()), Element.ALIGN_RIGHT, 1, bfBold12);


            insertCell(table, "", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, "Net à payer", Element.ALIGN_LEFT, 3, bfBold12);
            insertCell(table, Utiles.formatMtn(operation.getMontant()-operation.getRemise()), Element.ALIGN_RIGHT, 1, bfBold12);



            //add the PDF table to the paragraph
            paragraph.add(table);
            // add the paragraph to the document
            doc.add(paragraph);


            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }

        viewPdf(name, PV, context);
    }


    public static void createandDisplayOperationExcel(ArrayList<Operation> operations, String name, Context context,boolean bon) {

        name = name + "_" + formatter.format(new Date()).replace(' ', '_') + ".xlsx";

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;

        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        //file path
        File file = new File(path, name);

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("rapport", 0);

            try {
                sheet.addCell(new Label(0, 0, "No"));
                sheet.addCell(new Label(1, 0, "Date"));
                sheet.addCell(new Label(2, 0, "Libelle"));
                sheet.addCell(new Label(3, 0, "Sortie"));
                sheet.addCell(new Label(4, 0, "Entrée"));


                int entree = 0;
                int sortie = 0;
                int i = 0;
                for (int x = 0; x < operations.size(); x++) {
                    if (operations.get(x).getAttente()==1) continue;
                    if (bon)if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CMDFRNSS) || operations.get(x).getTypeOperation_id().equals(OperationDAO.CMDCLNT))  continue;

                    i = x + 1;
                    sheet.addCell(new Label(0, i, String.valueOf(i)));
                    sheet.addCell(new Label(1, i, DAOBase.formatterj.format(operations.get(x).getDateoperation())));

                    if (operations.get(x).getTypeOperation_id().equals(OperationDAO.VENTE) && operations.get(x).getPayer()==0) sheet.addCell(new Label(2, i, context.getString(R.string.ventecredi)));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.VENTE))sheet.addCell(new Label(2, i, context.getString(R.string.vente)));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.AUTRES_DEP))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.EAU))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.ELECTRICITE))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.TEL))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.FRAIS_PERSO))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.IMPOT))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.LOYER))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.PLACEMENT))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().startsWith(OperationDAO.IMMOBILISATION))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.ACHAT))sheet.addCell(new Label(2, i, context.getString(R.string.appro)));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CMDCLNT))sheet.addCell(new Label(2, i, context.getString(R.string.cmdclt)));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CMDFRNSS))sheet.addCell(new Label(2, i, context.getString(R.string.cmdfr)));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.TRANSPORT))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CH_EXC))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.CH_FN))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.PRODUIT_EXC))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else if (operations.get(x).getTypeOperation_id().equals(OperationDAO.PRODUIT_FIN))sheet.addCell(new Label(2, i, operations.get(x).getDescription()));
                    else sheet.addCell(new Label(2, i, operations.get(x).getDescription()));

                    if (operations.get(x).getEntree()==1){
                        sheet.addCell(new Label(3, i, "0"));
                        if (operations.get(x).getPayer()!=0)entree+=operations.get(x).getMontant()-operations.get(x).getRemise() ;
                        sheet.addCell(new Label(4, i, Utiles.formatMtn(operations.get(x).getMontant())));
                    } else {
                        if (operations.get(x).getPayer()!=0)sortie+=operations.get(x).getMontant()-operations.get(x).getRemise() ;
                        sheet.addCell(new Label(3, i, Utiles.formatMtn(operations.get(x).getMontant())));
                        sheet.addCell(new Label(4, i, "0"));
                    }

                }


                sheet.addCell(new Label(0, i+1, "Total"));
                sheet.addCell(new Label(3, i+1, String.valueOf(sortie)));
                sheet.addCell(new Label(4, i+1, String.valueOf(entree)));


            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();

            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewEXCEL(name, PV, context);
    }


    private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }

    public static class HeaderFooterPageEvent extends PdfPageEventHelper {

        Context c = null;
        SharedPreferences preferences = null;
        String ets = null;
        String adress = null;

        public HeaderFooterPageEvent(Context context) {
            c = context;
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            ets = preferences.getString("nomSociete", "");
            adress = preferences.getString("adresse", "");
        }

        public void onStartPage(PdfWriter writer, Document document) {
            Font ffont = new Font(Font.FontFamily.UNDEFINED, 10, Font.ITALIC);
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase(ets, ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, header, document.left() + document.leftMargin(), document.top() + 10, 0);
            header = new Phrase(adress, ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header, document.right() - document.rightMargin(), document.top() + 10, 0);

            //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("LEFT" +ets), 30, 800, 0);
            //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(adress), 550, 800, 0);
        }

        /*
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()), 550, 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Copyright Media Soft"), 110, 30, 0);
        }
        */

        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()), 550, 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Copyright Media Soft"), 110, 30, 0);
        }

    }


    // Method for opening a pdf file
    private static void viewPdf(String file, String directory, Context context) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, getMimeType(file));
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.noapp, Toast.LENGTH_LONG).show();
        }
    }


    // Method for opening a pdf file
    private static void viewWord(String file, String directory, Context context) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent wordIntent = new Intent(Intent.ACTION_VIEW);
        wordIntent.setDataAndType(path, getMimeType(file));
        wordIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(wordIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Vous ne posseder pas d'App pour lire les fichiers Word", Toast.LENGTH_LONG).show();
        }
    }


    // Method for opening a pdf file
    private static void viewEXCEL(String file, String directory, Context context) {

        File excelFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(excelFile);

        // Setting the intent for pdf reader
        Intent excelIntent = new Intent(Intent.ACTION_VIEW);
        excelIntent.setDataAndType(path, getMimeType(file));
        excelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(excelIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.noappexel, Toast.LENGTH_LONG).show();
        }
    }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }




    public static void deleteAllMsg(Context context) {
        try {

            //  DELETE ALL MESSAGE FROM SERVER

            /*
            Uri uri = Uri.parse("content://sms/inbox");
            String where = "address="+ Url.getSmsNumber(context);
            Cursor cursor = context.getContentResolver().query(uri, new String[] { "_id", "thread_id"}, where, null,
                    null);
            long thread_id = cursor.getLong(1);
            where = "thread_id="+thread_id;
            Uri thread = Uri.parse("content://sms/inbox");
            context.getContentResolver().delete(thread, where, null);


            */
            String fromAddress = null ;
            //Url.getSmsNumber(context);
            Uri uriSMS = Uri.parse("content://sms/inbox");
            Cursor cursor = context.getContentResolver().query(uriSMS, null,
                    null, null, null);
            cursor.moveToFirst();
            if(cursor.getCount() > 0){
                int ThreadId = cursor.getInt(1);
                Log.d("Thread Id", ThreadId+" id - "+cursor.getInt(0));
                Log.d("contact number", cursor.getString(2));
                Log.d("column name", cursor.getColumnName(2));

                context.getContentResolver().delete(Uri.
                                parse("content://sms/conversations/"+ThreadId), "address=?",
                        new String[]{fromAddress});
                Log.d("Message Thread Deleted", fromAddress);
            }
            cursor.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    public static File getExtFile(Context context,String name, String dossier){
        if (!isSdReadable()){
            Toast.makeText(context, context.getString(R.string.sdabsent),Toast.LENGTH_LONG).show();
            return null ;
        }

        File mypath = null ;

        String fullPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/ " + context.getPackageName() +  "/"+ dossier+"/"  ;

        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mypath = new File(dir,name) ;

        return  mypath ;
    }


    public static double getSolde(Context context) {

        double solde = 0 ;
        solde = Utiles.getCaisseDebit(context) - Utiles.getCaisseCredit(context) ;
        return solde ;
    }


    public static double getCaisseDebit(Context context) {
        OperationDAO operationDAO = new OperationDAO(context) ;

        ArrayList<Operation> operations = operationDAO.getAll();
        double valeur = 0;
        Date date = new Date() ;
        for (int i = 0; i < operations.size(); i++) {
            Operation operation = operations.get(i) ;
            if (operation.getAnnuler()==1  && operation.getDateannulation().before(date)) continue;
            if (operation.getComptebanque_id()>0  && !operation.getTypeOperation_id().equals(OperationDAO.BQ)) continue;
            if (operation.getEntree()==1){
                if (operation.getPayer()==0) {
                    if (operation.getTypeOperation_id().equals(OperationDAO.EMPRUNT)) {
                        valeur += operation.getMontant() - operation.getRemise();
                        {
                            ArrayList<Operation> payements = operationDAO.getMany(operation.getId_externe());
                            for (int j = 0; j < payements.size(); j++) {
                                valeur -= payements.get(j).getMontant();
                            }
                        }
                    }
                    else {
                        valeur += operations.get(i).getMontant() - operations.get(i).getRemise();
                    }
                }
                else {
                    valeur += operations.get(i).getMontant() - operations.get(i).getRemise();
                }
            }
        }

        return valeur ;
    }


    public static double getCaisseCredit(Context context) {
        OperationDAO operationDAO = new OperationDAO(context) ;

        ArrayList<Operation> operations = operationDAO.getAll();
        double valeur = 0;
        Date date = new Date() ;

        for (int i = 0; i < operations.size(); i++) {
            Operation operation = operations.get(i) ;
            if (operation.getAnnuler()==1  && operation.getDateannulation().before(date)) continue;
            if (operation.getComptebanque_id()>0  && !operation.getTypeOperation_id().equals(OperationDAO.BQ)) continue;
            if (operation.getEntree()==0){
                if (operation.getPayer()==0) {
                    ArrayList<Operation> payements = operationDAO.getMany(operation.getId_externe());
                    for (int j = 0; j < payements.size(); j++) {
                        valeur += payements.get(j).getMontant();
                    }
                }
                else {
                    valeur += operations.get(i).getMontant() - operations.get(i).getRemise();
                }
            }
        }

        return valeur ;
    }

}
