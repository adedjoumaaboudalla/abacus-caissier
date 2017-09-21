package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.database.CaisseHelper;
import pv.projects.mediasoft.com.pventes.database.CategorieProduitHelper;
import pv.projects.mediasoft.com.pventes.database.JournalHelper;
import pv.projects.mediasoft.com.pventes.model.Journal;

/**
 * Created by Mayi on 30/10/2015.
 */
public class JournalDAO extends DAOBase implements Crud<Journal>{


    public static final String ENTRER_EN_CAISSE = "Entrée en caisse";
    public static final String VENTE = "Vente";
    public static final String DEPENSE = "Depense";


    public JournalDAO(Context pContext) {
        super(pContext);
        this.mHandler = JournalHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(Journal journal) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(JournalHelper.DESCRIPTION, journal.getDescription());
        contentValues.put(JournalHelper.ID_EXTERNE, journal.getId_externe());
        contentValues.put(JournalHelper.TYPE, journal.getType());
        contentValues.put(JournalHelper.ETAT, journal.getEtat());
        contentValues.put(JournalHelper.MONTANT, journal.getMontant());
        contentValues.put(JournalHelper.DATE_OPERATION, formatter.format(journal.getDateoperation()));

        Log.i("Debug", journal.getDescription() + String.valueOf(journal.getMontant())) ;


        long  l = mDb.insert(JournalHelper.TABLE_NAME, null, contentValues);

        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(JournalHelper.TABLE_NAME,  JournalHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }

    @Override
    public int update(Journal journal) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(JournalHelper.DESCRIPTION, journal.getDescription());
        contentValues.put(JournalHelper.ID_EXTERNE, journal.getId_externe());
        contentValues.put(JournalHelper.TYPE, journal.getType());
        contentValues.put(JournalHelper.ETAT, journal.getEtat());
        contentValues.put(JournalHelper.MONTANT, journal.getMontant());
        contentValues.put(JournalHelper.DATE_OPERATION, formatter.format(journal.getDateoperation()));

        int i =mDb.update(JournalHelper.TABLE_NAME, contentValues,  JournalHelper.TABLE_KEY + " = ? ", new String[] { Long.toString(journal.getId()) } );

        return i;
    }

    @Override
    public Journal getOne(long id) {
        Journal journal = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ JournalHelper.TABLE_NAME +" where " +JournalHelper.TABLE_KEY + "  = "+id+"", null );

        if(res.moveToFirst()){
            journal = new Journal();

            journal.setId(res.getLong(res.getColumnIndex(JournalHelper.TABLE_KEY)));
            journal.setId_externe(res.getLong(res.getColumnIndex(JournalHelper.ID_EXTERNE)));
            journal.setDescription(res.getString(res.getColumnIndex(JournalHelper.DESCRIPTION)));
            journal.setMontant(res.getDouble(res.getColumnIndex(JournalHelper.MONTANT)));
            journal.setEtat(res.getInt(res.getColumnIndex(JournalHelper.ETAT)));
            journal.setType(res.getString(res.getColumnIndex(JournalHelper.TYPE)));

            try {
                journal.setDateoperation(DAOBase.formatter.parse(res.getString(res.getColumnIndex(JournalHelper.DATE_OPERATION))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        res.close();
        return journal ;
    }

    @Override
    public ArrayList<Journal> getAll() {
        ArrayList<Journal> journals = new ArrayList<Journal>();
        Journal journal = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ JournalHelper.TABLE_NAME + " order by " +JournalHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            journal = new Journal();

            journal.setId(res.getLong(res.getColumnIndex(JournalHelper.TABLE_KEY)));
            journal.setId_externe(res.getLong(res.getColumnIndex(JournalHelper.ID_EXTERNE)));
            journal.setDescription(res.getString(res.getColumnIndex(JournalHelper.DESCRIPTION)));
            journal.setMontant(res.getDouble(res.getColumnIndex(JournalHelper.MONTANT)));
            journal.setEtat(res.getInt(res.getColumnIndex(JournalHelper.ETAT)));
            journal.setType(res.getString(res.getColumnIndex(JournalHelper.TYPE)));

            try {
                journal.setDateoperation(DAOBase.formatter.parse(res.getString(res.getColumnIndex(JournalHelper.DATE_OPERATION))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            journals.add(journal);
            res.moveToNext();
        }
        res.close();
        return journals;
    }



    public ArrayList<Journal> getInterval(Date dateDebut, Date dateFin) {
        ArrayList<Journal> journals = new ArrayList<Journal>();
        Journal journal = null ;



        if (dateDebut==null) dateDebut=new Date("01/01/2015");
        if (dateFin==null) dateFin=new Date() ;

        Log.i("Debug", "SELECT * FROM " + JournalHelper.TABLE_NAME + " WHERE " + JournalHelper.DATE_OPERATION + "  BETWEEN '" + formatter.format(dateFin) + "' AND '" + formatter.format(dateDebut) + "' ORDER BY " +JournalHelper.TABLE_KEY + " DESC ") ;

        Cursor res =  mDb.rawQuery( "SELECT * FROM "+JournalHelper.TABLE_NAME + " WHERE "+JournalHelper.DATE_OPERATION + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " +JournalHelper.TABLE_KEY + " DESC ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            journal = new Journal();

            journal.setId(res.getLong(res.getColumnIndex(JournalHelper.TABLE_KEY)));
            journal.setId_externe(res.getLong(res.getColumnIndex(JournalHelper.ID_EXTERNE)));
            journal.setDescription(res.getString(res.getColumnIndex(JournalHelper.DESCRIPTION)));
            journal.setMontant(res.getDouble(res.getColumnIndex(JournalHelper.MONTANT)));
            journal.setEtat(res.getInt(res.getColumnIndex(JournalHelper.ETAT)));
            journal.setType(res.getString(res.getColumnIndex(JournalHelper.TYPE)));

            try {
                journal.setDateoperation(DAOBase.formatter.parse(res.getString(res.getColumnIndex(JournalHelper.DATE_OPERATION))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            journals.add(journal);
            res.moveToNext();
        }
        Log.i("DEBUG",String.valueOf(journals.size()));
        res.close();
        return journals;
    }
}
