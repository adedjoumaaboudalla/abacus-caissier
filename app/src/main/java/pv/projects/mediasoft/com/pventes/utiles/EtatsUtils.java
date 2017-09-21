package pv.projects.mediasoft.com.pventes.utiles;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.DAOBase;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.dao.ProduitDAO;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.model.PointVente;
import pv.projects.mediasoft.com.pventes.model.Produit;

/**
 * Created by mediasoft on 11/11/2016.
 */
public class EtatsUtils {


    private static final String PV = "ABACUS ETATS";
    private static String name = "ABACUS";
    public static final String PV_PRODUIT_IMAGE_DIR = "PV_PRODUIT";
    //public static String serverIp = "http://1 92.168.0.104/" ;
    public static String serverIp = "http://192.168.43.78/";
    public static final SimpleDateFormat formatter = new SimpleDateFormat("EEE dd MM yyyy");

    public static final SimpleDateFormat mysql_format = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat french_format = new SimpleDateFormat("dd-MM-yyyy");



    public static void createandDisplayPdfEtat(int i, String name, Context context, String datedebut, String datefin) {
        switch (i){
            case 0: resultatExploitationPDF(context,name,datedebut,datefin) ;break;
            case 1: chiffreAffairePDF(context,name,datedebut,datefin) ;break;
            case 2: listeAchatPDF(context,name,datedebut,datefin) ;break;
            case 3: ficheDeStockPDF(context,name,datedebut,datefin); ;break;
            case 4: listePartenairePDF(context,name,datedebut,datefin);break;
        }
    }

    public static void createandDisplayExcelEtat(int i,String name, Context context, String datedebut, String datefin) {
        switch (i){
            case 0: resultatExploitationEXCEL(context,name,datedebut,datefin); ;break;
            case 1: chiffreAffaireExcel(context,name,datedebut,datefin); ;break;
            case 2: listeAchatExcel(context,name,datedebut,datefin); ;break;
            case 3: ficheDeStockExcel(context,name,datedebut,datefin); ;break;
            case 4: listePartenaireExcel(context,name,datedebut,datefin); break;
        }
    }




    public static void resultatExploitationPDF(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())    dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".pdf";
            name = name.replace('-', '_') ;
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
            doc.addTitle(context.getString(R.string.resultat));
            doc.setPageSize(PageSize.LETTER);
            //specify column widths

            //open the document
            doc.open();


            PointVenteDAO pointVenteDAO = new PointVenteDAO(context) ;
            PointVente pointVente = pointVenteDAO.getLast() ;

            Paragraph paragraph = new Paragraph(pointVente.getLibelle()) ;
            paragraph.setAlignment(Element.ALIGN_LEFT);
            doc.add(paragraph);
            doc.add(new Paragraph(" "));

            // Largeur des colonnes
            float[] columnWidths = {7f, 3f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);


            //create a paragraph
            paragraph = null;
            paragraph = new Paragraph("RESULTAT D'EXPLOITATION " + datedebut + " - " + datefin);

            /*
            if (ets == null)
            else
            //    paragraph = new Paragraph("Commande DE " + ets + " DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));

            */
            paragraph.setAlignment(Element.ALIGN_CENTER);

            //insert column headings
            insertCell(table, " ", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Valeur", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);


            //insert an empty row
            insertCell(table, "", Element.ALIGN_LEFT,5, bfBold12);
            //create section heading by cell merging
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ProduitDAO produitDAO = new ProduitDAO(context) ;

            ArrayList<Operation> operations = null ;
            try {
                operations = operationDAO.getAll(1, DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            double valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            double a = valeur ;
            // Chiffre d'affaire
            insertCell(table, context.getString(R.string.chiffreaffairee), Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bfBold12);

            ArrayList<Produit> produits = produitDAO.getAll();

            ArrayList<Mouvement> mouvements = null;

            // Vente par produit
            for (int i = 0; i < produits.size(); i++) {
                try {
                    mouvements = mouvementDAO.getManyByProductInterval(produits.get(i).getId_externe(),DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin)) ;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (mouvements != null){
                    valeur = 0 ;
                    for (int j = 0; j < mouvements.size(); j++) {
                        if (operationDAO.getOneExterne(mouvements.get(j).getOperation_id())==null) continue;
                        if (mouvements.get(j).getEntree()==1  && operationDAO.getOneExterne(mouvements.get(j).getOperation_id()).getAttente()==0) valeur += mouvements.get(j).getPrixV()*mouvements.get(j).getQuantite() ;
                    }

                    if (valeur>0){
                        insertCell(table, context.getString(R.string.ventede)+ " " + produits.get(i).getLibelle(), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);
                    }
                }
            }

            // Achat et  autres depense
            try {
                operations = operationDAO.getAll(2,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }


            try {
                operations = operationDAO.getAll(5,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }


            double b = valeur ;
            insertCell(table, context.getString(R.string.achatdepense), Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bfBold12);


            // Achat

            try {
                operations = operationDAO.getAll(5,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            insertCell(table, context.getString(R.string.achatmse), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);



            try {
                operations = operationDAO.getAll(2,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.TRANSPORT))valeur += operations.get(i).getMontant() ;
            }

            // transport
            insertCell(table, context.getString(R.string.tranport), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.TEL))valeur += operations.get(i).getMontant() ;
            }

            // telephone
            insertCell(table, context.getString(R.string.tele), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.LOYER))valeur += operations.get(i).getMontant() ;
            }

            // loyer
            insertCell(table, context.getString(R.string.loyer), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.EAU))valeur += operations.get(i).getMontant() ;
            }

            // eau
            insertCell(table, context.getString(R.string.eau), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.ELECTRICITE))valeur += operations.get(i).getMontant() ;
            }

            // electricité
            insertCell(table, context.getString(R.string.electricite), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.FRAIS_PERSO))valeur += operations.get(i).getMontant() ;
            }

            // frais
            insertCell(table, context.getString(R.string.frais), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.AUTRES_DEP))valeur += operations.get(i).getMontant() ;
            }

            // Autres dépense
            insertCell(table, context.getString(R.string.autre), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);


            double c = a-b ;

            // Marche beneficiaire
            insertCell(table, context.getString(R.string.margebenef), Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, Utiles.formatMtn(c), Element.ALIGN_RIGHT, 1, bfBold12);



            // Produit financiere
            try {
                operations = operationDAO.getAll(7,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            double d = valeur ;

            insertCell(table, context.getString(R.string.prdtfin), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            // Charge financiere
            try {
                operations = operationDAO.getAll(8,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            d -= valeur ;

            insertCell(table, context.getString(R.string.chargefin), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            insertCell(table, context.getString(R.string.margefin), Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, Utiles.formatMtn(d), Element.ALIGN_RIGHT, 1, bfBold12);

            // Produit Except
            try {
                operations = operationDAO.getAll(9,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }


            double f = valeur ;
            insertCell(table, context.getString(R.string.prdtexcept), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            // Charge Except
            try {
                operations = operationDAO.getAll(10,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            f -= valeur ;
            insertCell(table, context.getString(R.string.chargeexp), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            insertCell(table, context.getString(R.string.margeexp), Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, Utiles.formatMtn(f), Element.ALIGN_RIGHT, 1, bfBold12);




            // Impot et taxe
            try {
                operations = operationDAO.getAll(2,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.IMPOT))valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            insertCell(table, context.getString(R.string.impot), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);

            double g = c+d+f-valeur ;

            insertCell(table, context.getString(R.string.benefnet), Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, Utiles.formatMtn(g), Element.ALIGN_RIGHT, 1, bfBold12);

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









    public static void chiffreAffairePDF(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".pdf";
            name = name.replace('-', '_') ;
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
            doc.addTitle(context.getString(R.string.chiffreaffaire));
            doc.setPageSize(PageSize.LETTER);
            //specify column widths

            //open the document
            doc.open();


            PointVenteDAO pointVenteDAO = new PointVenteDAO(context) ;
            PointVente pointVente = pointVenteDAO.getLast() ;

            Paragraph paragraph = new Paragraph(pointVente.getLibelle()) ;
            paragraph.setAlignment(Element.ALIGN_LEFT);
            doc.add(paragraph);
            doc.add(new Paragraph(" "));

            // Largeur des colonnes
            float[] columnWidths = {3f, 5f, 2f, 5f, 7f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);


            //create a paragraph
            paragraph = null;
            paragraph = new Paragraph("CHIFFRE D'AFFAIRE SUR " +datedebut + " - " + datefin);

            /*
            if (ets == null)
            else
            //    paragraph = new Paragraph("Commande DE " + ets + " DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));

            */
            paragraph.setAlignment(Element.ALIGN_CENTER);

            //insert column headingsString.valueOf(i+1)
            insertCell(table, "No", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Libelle", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Qte", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Prix vente", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Montant", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);


            //insert an empty row
            insertCell(table, "", Element.ALIGN_LEFT,5, bfBold12);
            //create section heading by cell merging
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ProduitDAO produitDAO = new ProduitDAO(context) ;

            ArrayList<Produit> produits = produitDAO.getAll();

            ArrayList<Mouvement> mouvements = null;

            double valeur = 0 ;
            double total = 0 ;
            double quantite = 0 ;
            // Vente par produit
            for (int i = 0; i < produits.size(); i++) {
                try {
                    mouvements = mouvementDAO.getManyByProductInterval(produits.get(i).getId_externe(),DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin)) ;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (mouvements != null){
                    valeur = 0 ;
                    quantite = 0 ;
                    for (int j = 0; j < mouvements.size(); j++) {
                        Mouvement mouvement = mouvements.get(j) ;
                        Operation operation = operationDAO.getOneExterne(mouvement.getOperation_id());;
                        if (operation==null || operation.getAnnuler()==1) continue;
                        if (mouvements.get(j).getEntree()==1) {
                            quantite += mouvement.getQuantite() ;
                            valeur += mouvement.getPrixV() * mouvement.getQuantite() ;
                        }
                    }

                    if (valeur>0){
                        insertCell(table, String.valueOf(i+1), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, produits.get(i).getLibelle(), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, String.valueOf(quantite), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, String.valueOf(produits.get(i).getPrixV()), Element.ALIGN_RIGHT, 1, bf12);
                        insertCell(table, String.valueOf(valeur), Element.ALIGN_RIGHT, 1, bf12);
                        total+=valeur ;
                    }
                }
            }

            insertCell(table, context.getString(R.string.total), Element.ALIGN_CENTER, 2, bfBold12);
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bfBold12);
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bfBold12);
            insertCell(table, Utiles.formatMtn(total), Element.ALIGN_RIGHT, 1, bfBold12);

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







    public static void listePartenaireExcel(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".xlsx";
            name = name.replace('-', '_') ;

            //file path
            File file = new File(path, name);

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;



            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("rapport", 0);
            int ligne = 0 ;
            sheet.addCell(new Label(0, ligne, "Désignation"));
            sheet.addCell(new Label(1, ligne, "Genre"));
            sheet.addCell(new Label(2, ligne, "Contact"));
            sheet.addCell(new Label(3, ligne, "E-mail"));
            sheet.addCell(new Label(4, ligne, "Adresse"));
            ligne++ ;
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            PartenaireDAO partenaireDAO = new PartenaireDAO(context) ;

            ArrayList<Partenaire> partenaires = partenaireDAO.getAll();

            ArrayList<Mouvement> mouvements = null;

            double valeur = 0 ;
            double total = 0 ;
            double quantite = 0 ;
            // Vente par produit
            Partenaire partenaire = null ;
            for (int i = 0; i < partenaires.size(); i++) {
                partenaire = partenaires.get(i) ;
                sheet.addCell(new Label(0, ligne, partenaire.getNom() + " " + partenaire.getPrenom() + " " + partenaire.getRaisonsocial()));
                sheet.addCell(new Label(1, ligne, partenaire.getSexe()));
                sheet.addCell(new Label(2, ligne, partenaire.getContact()));
                sheet.addCell(new Label(3, ligne, partenaire.getEmail()));
                sheet.addCell(new Label(4, ligne, partenaire.getAdresse()));
                ligne++ ;
            }


            workbook.write();

            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }

            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }

        viewPdf(name, PV, context);
    }






    public static void listeAchatPDF(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".pdf";
            name = name.replace('-', '_') ;
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
            doc.addTitle(context.getString(R.string.resultat));
            doc.setPageSize(PageSize.LETTER);
            //specify column widths

            //open the document
            doc.open();

            PointVenteDAO pointVenteDAO = new PointVenteDAO(context) ;
            PointVente pointVente = pointVenteDAO.getLast() ;

            Paragraph paragraph = new Paragraph(pointVente.getLibelle()) ;
            paragraph.setAlignment(Element.ALIGN_LEFT);
            doc.add(paragraph);
            doc.add(new Paragraph(" "));

            // Largeur des colonnes
            float[] columnWidths = {3f, 5f, 2f, 5f, 7f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);


            //create a paragraph
            paragraph = null;
            paragraph = new Paragraph("LISTE DES ACHATS SUR " + datedebut + " - " + datefin);

            /*
            if (ets == null)
            else
            //    paragraph = new Paragraph("Commande DE " + ets + " DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));

            */
            paragraph.setAlignment(Element.ALIGN_CENTER);

            //insert column headings
            insertCell(table, "No", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Libelle", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Qte", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Prix vente", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Montant", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);


            //insert an empty row
            insertCell(table, "", Element.ALIGN_LEFT,5, bfBold12);
            //create section heading by cell merging
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ProduitDAO produitDAO = new ProduitDAO(context) ;

            ArrayList<Produit> produits = produitDAO.getAll();

            ArrayList<Mouvement> mouvements = null;

            double valeur = 0 ;
            double total = 0 ;
            double quantite = 0 ;
            // Vente par produit
            for (int i = 0; i < produits.size(); i++) {
                try {
                    mouvements = mouvementDAO.getManyByProductInterval(produits.get(i).getId_externe(),DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin)) ;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (mouvements != null){
                    valeur = 0 ;
                    quantite = 0 ;
                    for (int j = 0; j < mouvements.size(); j++) {
                        Log.e("MOUV ENTREE", String.valueOf(mouvements.get(j).getEntree())) ;
                        Mouvement mouvement = mouvements.get(j) ;
                        Operation operation = operationDAO.getOneExterne(mouvement.getOperation_id());;
                        if (operation==null || operation.getAnnuler()==1 || operation.getTypeOperation_id().startsWith(OperationDAO.CMD)) continue;
                        if (mouvements.get(j).getEntree()==0) {
                            quantite += mouvement.getQuantite() ;
                            valeur += mouvement.getPrixV() * mouvement.getQuantite() ;
                        }
                    }

                    if (valeur>0){
                        insertCell(table, String.valueOf(i+1), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, produits.get(i).getLibelle(), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, String.valueOf(quantite), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, Utiles.formatMtn(produits.get(i).getPrixV()), Element.ALIGN_RIGHT, 1, bf12);
                        insertCell(table, Utiles.formatMtn(valeur), Element.ALIGN_RIGHT, 1, bf12);
                        total+=valeur ;
                    }
                }
            }

            insertCell(table, context.getString(R.string.total), Element.ALIGN_CENTER, 2, bfBold12);
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bfBold12);
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bfBold12);
            insertCell(table, Utiles.formatMtn(total), Element.ALIGN_RIGHT, 1, bfBold12);

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








    public static void ficheDeStockPDF(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".pdf";
            name = name.replace('-', '_') ;
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
            doc.addTitle(context.getString(R.string.resultat));
            doc.setPageSize(PageSize.LETTER);
            //specify column widths

            //open the document
            doc.open();

            PointVenteDAO pointVenteDAO = new PointVenteDAO(context) ;
            PointVente pointVente = pointVenteDAO.getLast() ;

            Paragraph paragraph = new Paragraph(pointVente.getLibelle()) ;
            paragraph.setAlignment(Element.ALIGN_LEFT);
            doc.add(paragraph);
            doc.add(new Paragraph(" "));

            // Largeur des colonnes
            float[] columnWidths = {3f, 2f, 5f, 2f, 2f, 2f, 2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);


            //create a paragraph
            paragraph = null;
            paragraph = new Paragraph("FICHE DE STOCK SUR " + datedebut + " - " + datefin);

            /*
            if (ets == null)
            else
            //    paragraph = new Paragraph("Commande DE " + ets + " DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));

            */
            paragraph.setAlignment(Element.ALIGN_CENTER);

            //insert column headings
            insertCell(table, "Date", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "No", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Libelle", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, "SI", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Entrée", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Sortie", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "SF", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);


            //insert an empty row
            //create section heading by cell merging
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ProduitDAO produitDAO = new ProduitDAO(context) ;


            ArrayList<Mouvement> mouvements = null;

            double valeur = 0 ;
            double total = 0 ;
            double quantite = 0 ;

                try {
                    mouvements = mouvementDAO.getManyByInterval(DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin)) ;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (mouvements != null){
                    valeur = 0 ;
                    quantite = 0 ;
                    for (int j = 0; j < mouvements.size(); j++) {
                        Log.e("MOUV ENTREE", String.valueOf(mouvements.get(j).getEntree())) ;
                        Mouvement mouvement = mouvements.get(j) ;
                        Operation operation = operationDAO.getOneExterne(mouvement.getOperation_id());;
                        if (operation==null || operation.getAnnuler()==1 || operation.getTypeOperation_id().startsWith(OperationDAO.CMD)) continue;


                        insertCell(table, french_format.format(mouvement.getCreated_at()), Element.ALIGN_CENTER, 1, bf12);
                        insertCell(table, String.valueOf(j+1), Element.ALIGN_CENTER, 1, bf12);
                        insertCell(table, mouvement.getProduit(), Element.ALIGN_LEFT, 1, bf12);

                        if (mouvement.getEntree()==1) insertCell(table, String.valueOf(mouvement.getRestant()+mouvement.getQuantite()), Element.ALIGN_CENTER, 1, bf12);
                        else insertCell(table, String.valueOf(mouvement.getRestant()-mouvement.getQuantite()), Element.ALIGN_CENTER, 1, bf12);


                        if (mouvement.getEntree()==1) {
                            insertCell(table, String.valueOf(0), Element.ALIGN_CENTER, 1, bf12);
                            insertCell(table, String.valueOf(mouvement.getQuantite()), Element.ALIGN_CENTER, 1, bf12);
                        }
                        else {
                            insertCell(table, String.valueOf(mouvement.getQuantite()), Element.ALIGN_CENTER, 1, bf12);
                            insertCell(table, String.valueOf(0), Element.ALIGN_CENTER, 1, bf12);
                        }


                        insertCell(table, String.valueOf(mouvement.getRestant()), Element.ALIGN_CENTER, 1, bf12);
                    }

                }

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





    public static void listePartenairePDF(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".pdf";
            name = name.replace('-', '_') ;
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
            doc.addTitle(context.getString(R.string.resultat));
            doc.setPageSize(PageSize.LETTER);
            //specify column widths

            //open the document
            doc.open();

            PointVenteDAO pointVenteDAO = new PointVenteDAO(context) ;
            PointVente pointVente = pointVenteDAO.getLast() ;

            Paragraph paragraph = new Paragraph(pointVente.getLibelle()) ;
            paragraph.setAlignment(Element.ALIGN_LEFT);
            doc.add(paragraph);
            doc.add(new Paragraph(" "));

            // Largeur des colonnes
            float[] columnWidths = {5f, 3f, 5f, 5f, 4f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);


            //create a paragraph
            paragraph = null;
            paragraph = new Paragraph("LISTE DES PARTENAIRES SUR " + datedebut + " - " + datefin);

            /*
            if (ets == null)
            else
            //    paragraph = new Paragraph("Commande DE " + ets + " DU " + formatter.format(operations.get(0).getDateoperation()) + " AU " + formatter.format(operations.get(operations.size() - 1).getDateoperation()));

            */
            paragraph.setAlignment(Element.ALIGN_CENTER);

            //insert column headings
            insertCell(table, "Désignation", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Genre", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Contact", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "E-mail", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Adresse", Element.ALIGN_CENTER, 1, bfBold12);

            table.setHeaderRows(1);


            //insert an empty row
            insertCell(table, "", Element.ALIGN_LEFT,5, bfBold12);
            //create section heading by cell merging
            //insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4, bfBold12);
            //double orderTotal, total = 0;

            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            PartenaireDAO partenaireDAO = new PartenaireDAO(context) ;

            ArrayList<Partenaire> partenaires = partenaireDAO.getAll();

            ArrayList<Mouvement> mouvements = null;

            double valeur = 0 ;
            double total = 0 ;
            double quantite = 0 ;
            // Vente par produit
            Partenaire partenaire = null ;
            for (int i = 0; i < partenaires.size(); i++) {
                partenaire = partenaires.get(i) ;
                insertCell(table, partenaire.getNom() + " " + partenaire.getPrenom() + " " + partenaire.getRaisonsocial(), Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, partenaire.getSexe(), Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, partenaire.getContact(), Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, partenaire.getEmail(), Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, partenaire.getAdresse(), Element.ALIGN_CENTER, 1, bfBold12);
                total+=valeur ;
            }

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








    public static void chiffreAffaireExcel(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".xlsx";
            name = name.replace('-', '_') ;

            //file path
            File file = new File(path, name);

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;



            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("rapport", 0);
            int ligne = 0 ;
            sheet.addCell(new Label(0, ligne, "No"));
            sheet.addCell(new Label(1, ligne, "Libelle"));
            sheet.addCell(new Label(2, ligne, "Qte"));
            sheet.addCell(new Label(3, ligne, "Prix vente"));
            sheet.addCell(new Label(4, ligne, "Montant"));
            ligne++ ;


            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ProduitDAO produitDAO = new ProduitDAO(context) ;

            ArrayList<Produit> produits = produitDAO.getAll();

            ArrayList<Mouvement> mouvements = null;

            double valeur = 0 ;
            double total = 0 ;
            double quantite = 0 ;
            // Vente par produit
            for (int i = 0; i < produits.size(); i++) {
                try {
                    mouvements = mouvementDAO.getManyByProductInterval(produits.get(i).getId_externe(),DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin)) ;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (mouvements != null){
                    valeur = 0 ;
                    quantite = 0 ;
                    for (int j = 0; j < mouvements.size(); j++) {
                        Mouvement mouvement = mouvements.get(j) ;
                        Operation operation = operationDAO.getOneExterne(mouvement.getOperation_id());;
                        if (operation==null || operation.getAnnuler()==1) continue;
                        if (mouvements.get(j).getEntree()==1) {
                            quantite += mouvement.getQuantite() ;
                            valeur += mouvement.getPrixV() * mouvement.getQuantite() ;
                        }
                    }

                    if (valeur>0){
                        sheet.addCell(new Label(0, ligne, String.valueOf(i+1)));
                        sheet.addCell(new Label(1, ligne, produits.get(i).getLibelle()));
                        sheet.addCell(new Label(2, ligne, String.valueOf(quantite)));
                        sheet.addCell(new Label(3, ligne, Utiles.formatMtn(produits.get(i).getPrixV())));
                        sheet.addCell(new Label(4, ligne, Utiles.formatMtn(valeur)));
                        ligne++ ;
                        total+=valeur ;
                    }
                }
            }

            sheet.addCell(new Label(0, ligne, context.getString(R.string.total)));
            sheet.addCell(new Label(1, ligne, ""));
            sheet.addCell(new Label(2, ligne, ""));
            sheet.addCell(new Label(3, ligne, ""));
            sheet.addCell(new Label(4, ligne, Utiles.formatMtn(total)));
            ligne++ ;

            workbook.write();

            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }

        viewEXCEL(name, PV, context);
    }





    public static void listeAchatExcel(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".xlsx";
            name = name.replace('-', '_') ;

            //file path
            File file = new File(path, name);

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;



            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("rapport", 0);
            int ligne = 0 ;
            sheet.addCell(new Label(0, ligne, "No"));
            sheet.addCell(new Label(1, ligne, "Libelle"));
            sheet.addCell(new Label(2, ligne, "Qte"));
            sheet.addCell(new Label(3, ligne, "Prix vente"));
            sheet.addCell(new Label(4, ligne, "Montant"));
            ligne++ ;


            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ProduitDAO produitDAO = new ProduitDAO(context) ;

            ArrayList<Produit> produits = produitDAO.getAll();

            ArrayList<Mouvement> mouvements = null;

            double valeur = 0 ;
            double total = 0 ;
            double quantite = 0 ;
            // Vente par produit
            for (int i = 0; i < produits.size(); i++) {
                try {
                    mouvements = mouvementDAO.getManyByProductInterval(produits.get(i).getId_externe(),DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin)) ;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (mouvements != null){
                    valeur = 0 ;
                    quantite = 0 ;
                    for (int j = 0; j < mouvements.size(); j++) {
                        Mouvement mouvement = mouvements.get(j) ;
                        Operation operation = operationDAO.getOneExterne(mouvement.getOperation_id());;
                        if (operation==null || operation.getAnnuler()==1  || operation.getTypeOperation_id().startsWith(OperationDAO.CMD)) continue;
                        if (mouvements.get(j).getEntree()==0) {
                            quantite += mouvement.getQuantite() ;
                            valeur += mouvement.getPrixV() * mouvement.getQuantite() ;
                        }
                    }

                    if (valeur>0){
                        sheet.addCell(new Label(0, ligne, String.valueOf(i+1)));
                        sheet.addCell(new Label(1, ligne, produits.get(i).getLibelle()));
                        sheet.addCell(new Label(2, ligne, String.valueOf(quantite)));
                        sheet.addCell(new Label(3, ligne, Utiles.formatMtn(produits.get(i).getPrixV())));
                        sheet.addCell(new Label(4, ligne, Utiles.formatMtn(valeur)));
                        ligne++ ;
                        total+=valeur ;
                    }
                }
            }

            sheet.addCell(new Label(0, ligne, context.getString(R.string.total)));
            sheet.addCell(new Label(1, ligne, ""));
            sheet.addCell(new Label(2, ligne, ""));
            sheet.addCell(new Label(3, ligne, ""));
            sheet.addCell(new Label(4, ligne, Utiles.formatMtn(total)));
            ligne++ ;

            workbook.write();

            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }

        viewEXCEL(name, PV, context);
    }






    public static void ficheDeStockExcel(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".xlsx";
            name = name.replace('-', '_') ;

            //file path
            File file = new File(path, name);

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;



            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("rapport", 0);
            int ligne = 0 ;
            sheet.addCell(new Label(0, ligne, "Date"));
            sheet.addCell(new Label(1, ligne, "No"));
            sheet.addCell(new Label(2, ligne, "Libelle"));
            sheet.addCell(new Label(3, ligne, "SI"));
            sheet.addCell(new Label(4, ligne, "Entrée"));
            sheet.addCell(new Label(5, ligne, "Sortie"));
            sheet.addCell(new Label(6, ligne, "SF"));
            ligne++ ;


            //just some random data to fill
            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ProduitDAO produitDAO = new ProduitDAO(context) ;

            ArrayList<Mouvement> mouvements = null;

            double valeur = 0 ;
            double total = 0 ;
            double quantite = 0 ;

            try {
                mouvements = mouvementDAO.getManyByInterval(DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin)) ;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mouvements != null){
                valeur = 0 ;
                quantite = 0 ;
                for (int j = 0; j < mouvements.size(); j++) {
                    Log.e("MOUV ENTREE", String.valueOf(mouvements.get(j).getEntree())) ;
                    Mouvement mouvement = mouvements.get(j) ;
                    Operation operation = operationDAO.getOneExterne(mouvement.getOperation_id());;
                    if (operation==null || operation.getAnnuler()==1 || operation.getTypeOperation_id().startsWith(OperationDAO.CMD)) continue;


                    sheet.addCell(new Label(0, ligne, french_format.format(mouvement.getCreated_at())));
                    sheet.addCell(new Label(1, ligne, String.valueOf(j+1)));
                    sheet.addCell(new Label(2, ligne, mouvement.getProduit()));


                    if (mouvement.getEntree()==1)
                        sheet.addCell(new Label(3, ligne, String.valueOf(mouvement.getRestant()+mouvement.getQuantite())));
                    else
                        sheet.addCell(new Label(3, ligne, String.valueOf(mouvement.getRestant()-mouvement.getQuantite()) ));


                    if (mouvement.getEntree()==1) {
                        sheet.addCell(new Label(4, ligne,String.valueOf(0) ));
                        sheet.addCell(new Label(5, ligne, String.valueOf(mouvement.getQuantite())));
                    }
                    else {
                        sheet.addCell(new Label(5, ligne,String.valueOf(0) ));
                        sheet.addCell(new Label(4, ligne, String.valueOf(mouvement.getQuantite())));
                    }


                    sheet.addCell(new Label(6, ligne,  String.valueOf(mouvement.getRestant())));
                    ligne++ ;
                }

            }

            workbook.write();

            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }

        viewEXCEL(name, PV, context);
    }



    public static void resultatExploitationEXCEL(Context context, String name, String datedebut, String datefin) {

        Document doc = new Document();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ets = preferences.getString("nomSociete", null);

        try {

            String path = null ;
            if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PV;
            }
            else if (!preferences.getBoolean("stockage",true)){
                path = context.getFilesDir().getAbsolutePath() + "/" + PV;
            }
            else{
                Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
                return;
            }


            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            name = name + "_" + french_format.format(new Date()).replace(' ', '_') + ".xlsx";
            name = name.replace('-', '_') ;

            //file path
            File file = new File(path, name);

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;



            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("rapport", 0);
            int ligne = 0 ;
            sheet.addCell(new Label(0, ligne, " "));
            sheet.addCell(new Label(1, ligne, "Valeur"));
            ligne++ ;


            float entree = 0 ;
            float sortie = 0 ;
            OperationDAO operationDAO = new OperationDAO(context) ;
            MouvementDAO mouvementDAO = new MouvementDAO(context) ;
            ProduitDAO produitDAO = new ProduitDAO(context) ;

            ArrayList<Operation> operations = null ;
            try {
                operations = operationDAO.getAll(1,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            double valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            double a = valeur ;
            // Chiffre d'affaire
            sheet.addCell(new Label(0, ligne, context.getString(R.string.chiffreaffairee)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            ArrayList<Produit> produits = produitDAO.getAll();

            ArrayList<Mouvement> mouvements = null;

            // Vente par produit
            for (int i = 0; i < produits.size(); i++) {
                mouvements = mouvementDAO.getManyByProductInterval(produits.get(i).getId_externe(),DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin)) ;
                if (mouvements != null){
                    valeur = 0 ;
                    for (int j = 0; j < mouvements.size(); j++) {
                        if (operationDAO.getOneExterne(mouvements.get(j).getOperation_id())==null) continue;
                        if (mouvements.get(j).getEntree()==1  && operationDAO.getOneExterne(mouvements.get(j).getOperation_id()).getAttente()==0) valeur += mouvements.get(j).getPrixV()*mouvements.get(j).getQuantite() ;
                    }

                    if (valeur>0){
                        sheet.addCell(new Label(0, ligne, context.getString(R.string.ventede) + " " + produits.get(i).getLibelle()));
                        sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
                        ligne++ ;
                    }
                }
            }

            // Achat et  autres depense
            try {
                operations = operationDAO.getAll(2,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }


            try {
                operations = operationDAO.getAll(5,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }


            double b = valeur ;

            sheet.addCell(new Label(0, ligne, context.getString(R.string.achatdepense)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;


            // Achat

            try {
                operations = operationDAO.getAll(5,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            sheet.addCell(new Label(0, ligne, context.getString(R.string.achatmse)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;



            try {
                operations = operationDAO.getAll(2,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.TRANSPORT))valeur += operations.get(i).getMontant() ;
            }

            // transport
            sheet.addCell(new Label(0, ligne, context.getString(R.string.tranport)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.TEL))valeur += operations.get(i).getMontant() ;
            }

            // telephone
            sheet.addCell(new Label(0, ligne, context.getString(R.string.tele)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.LOYER))valeur += operations.get(i).getMontant() ;
            }

            // loyer
            sheet.addCell(new Label(0, ligne, context.getString(R.string.loyer)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.EAU))valeur += operations.get(i).getMontant() ;
            }

            // eau
            sheet.addCell(new Label(0, ligne, context.getString(R.string.eau)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.ELECTRICITE))valeur += operations.get(i).getMontant() ;
            }

            // electricité
            sheet.addCell(new Label(0, ligne, context.getString(R.string.electricite)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.FRAIS_PERSO))valeur += operations.get(i).getMontant() ;
            }

            // frais
            sheet.addCell(new Label(0, ligne, context.getString(R.string.frais)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.AUTRES_DEP))valeur += operations.get(i).getMontant() ;
            }

            // Autres dépense
            sheet.addCell(new Label(0, ligne, context.getString(R.string.autre)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;


            double c = a-b ;

            // Marche beneficiaire
            sheet.addCell(new Label(0, ligne, context.getString(R.string.margebenef)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(c)));
            ligne++ ;



            // Produit financiere
            try {
                operations = operationDAO.getAll(7,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            double d = valeur ;

            sheet.addCell(new Label(0, ligne, context.getString(R.string.prdtfin)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            // Charge financiere
            try {
                operations = operationDAO.getAll(8,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant()-operations.get(i).getRemise() ;
            }

            d -= valeur ;

            sheet.addCell(new Label(0, ligne, context.getString(R.string.chargefin)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            sheet.addCell(new Label(0, ligne, context.getString(R.string.margefin)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(d)));
            ligne++ ;

            // Produit Except
            try {
                operations = operationDAO.getAll(9,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant() -operations.get(i).getRemise();
            }


            double f = valeur ;
            sheet.addCell(new Label(0, ligne, context.getString(R.string.prdtexcept)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            // Charge Except
            try {
                operations = operationDAO.getAll(10,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                valeur += operations.get(i).getMontant() -operations.get(i).getRemise();
            }

            f -= valeur ;
            sheet.addCell(new Label(0, ligne, context.getString(R.string.chargeexp)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            sheet.addCell(new Label(0, ligne, context.getString(R.string.margeexp)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(f)));
            ligne++ ;




            // Impot et taxe
            try {
                operations = operationDAO.getAll(2,DAOBase.formatter2.parse(datedebut),DAOBase.formatter2.parse(datefin));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            valeur = 0 ;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i).getAnnuler()==1) continue;
                if (operations.get(i).getTypeOperation_id().equals(OperationDAO.IMPOT))valeur += operations.get(i).getMontant() ;
            }

            sheet.addCell(new Label(0, ligne, context.getString(R.string.impot)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(valeur)));
            ligne++ ;

            double g = c+d+f-valeur ;

            sheet.addCell(new Label(0, ligne, context.getString(R.string.benefnet)));
            sheet.addCell(new Label(1, ligne, Utiles.formatMtn(g)));
            ligne++ ;

            workbook.write();

            Toast.makeText(context, "Sauvegarder en " + path, Toast.LENGTH_LONG).show();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            doc.close();
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
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(french_format.format(new Date())), 110, 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(name), 310, 30, 0);
        }

    }

    // Method for opening a pdf file
    private static void viewPdf(String file, String directory, Context context) {

        File pdfFile = null ;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context) ;

        Uri path = null ;
        if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
            pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        }
        else if (!preferences.getBoolean("stockage",true)){
            pdfFile = new File(context.getFilesDir() + "/" + directory + "/" + file);
        }
        else{
            Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
            return;
        }

        path = Uri.fromFile(pdfFile);

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


        File pdfFile = null ;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context) ;

        Uri path = null ;
        if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
            pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        }
        else if (!preferences.getBoolean("stockage",true)){
            pdfFile = new File(context.getFilesDir() + "/" + directory + "/" + file);
        }
        else{
            Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
            return;
        }

        path = Uri.fromFile(pdfFile);

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


        File excelFile = null ;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context) ;

        Uri path = null ;
        if (Utiles.isSdReadable() && preferences.getBoolean("stockage",true)){
            excelFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        }
        else if (!preferences.getBoolean("stockage",true)){
            excelFile = new File(context.getFilesDir() + "/" + directory + "/" + file);
        }
        else{
            Toast.makeText(context,R.string.sdabsent,Toast.LENGTH_LONG).show();
            return;
        }

        path = Uri.fromFile(excelFile);

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


}
