package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.BilletHelper;
import pv.projects.mediasoft.com.pventes.model.Billet;


/**
 * Created by Mayi on 18/12/2015.
 */
public class BilletDAO extends DAOBase implements Crud<Billet> {

    Context context = null ;

    public BilletDAO(Context pContext) {
        super(pContext);
        context = pContext ;
        this.mHandler = BilletHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(Billet billet) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(BilletHelper.TABLE_KEY, billet.getId());
        contentValues.put(BilletHelper.LIBELLE, billet.getLibelle());
        contentValues.put(BilletHelper.MONTANT, billet.getMontant());

        Long l = mDb.insert(BilletHelper.TABLE_NAME, null, contentValues);

        Log.e("DEBUG", String.valueOf(l)) ;
        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(BilletHelper.TABLE_NAME, BilletHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }

    public int clean() {
        return  mDb.delete(BilletHelper.TABLE_NAME, null,null);
    }

    @Override
    public int update(Billet billet) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(BilletHelper.TABLE_KEY, billet.getId());
        contentValues.put(BilletHelper.LIBELLE, billet.getLibelle());
        contentValues.put(BilletHelper.MONTANT, billet.getMontant());

        int l = mDb.update(BilletHelper.TABLE_NAME, contentValues, BilletHelper.TABLE_KEY + " = ? ", new String[]{Long.toString(billet.getId())});

        Log.e("DEBUG", String.valueOf(l)) ;
        return l;
    }

    @Override
    public Billet getOne(long id) {
        Billet billet = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ BilletHelper.TABLE_NAME +" where " + BilletHelper.TABLE_KEY + "="+id+"", null );


        if (res.moveToFirst()) {
            billet = new Billet();

            billet.setId(res.getLong(res.getColumnIndex(BilletHelper.TABLE_KEY)));
            billet.setLibelle(res.getString(res.getColumnIndex(BilletHelper.LIBELLE)));
            billet.setMontant(res.getDouble(res.getColumnIndex(BilletHelper.MONTANT)));
        }
        res.close();
        return billet;
    }

    @Override
    public ArrayList<Billet> getAll() {
        ArrayList<Billet> billets = new ArrayList<Billet>();

        Billet billet = null ;
        Cursor res =  mDb.rawQuery( "select * from "+ BilletHelper.TABLE_NAME+ " order by "+ BilletHelper.TABLE_KEY + " asc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            billet = new Billet();

            billet.setId(res.getLong(res.getColumnIndex(BilletHelper.TABLE_KEY)));
            billet.setLibelle(res.getString(res.getColumnIndex(BilletHelper.LIBELLE)));
            billet.setMontant(res.getDouble(res.getColumnIndex(BilletHelper.MONTANT)));

            billets.add(billet) ;
            res.moveToNext();
        }
        res.close();
        return billets;
    }

}
