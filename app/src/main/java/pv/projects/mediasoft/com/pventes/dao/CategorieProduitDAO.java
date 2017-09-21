package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.BilletHelper;
import pv.projects.mediasoft.com.pventes.database.CaisseHelper;
import pv.projects.mediasoft.com.pventes.database.CategorieProduitHelper;
import pv.projects.mediasoft.com.pventes.model.CategorieProduit;


/**
 * Created by Mayi on 16/10/2015.
 */
public class CategorieProduitDAO extends DAOBase implements Crud<CategorieProduit> {

    public CategorieProduitDAO(Context pContext) {
        super(pContext);
        this.mHandler = CategorieProduitHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(CategorieProduit categorieProduit) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CategorieProduitHelper.TABLE_KEY, categorieProduit.getId());
        contentValues.put(CategorieProduitHelper.LIBELLE, categorieProduit.getLibelle());
        contentValues.put(CategorieProduitHelper.CREATED_AT, formatter.format(categorieProduit.getCreated_at()));

        long l = mDb.insert(CategorieProduitHelper.TABLE_NAME, null, contentValues) ;

        Log.e("ADD", String.valueOf(l)) ;

        return l ;
    }


    public boolean addAll(ArrayList<CategorieProduit> categorieProduits) {
        if (categorieProduits.size() > 0) deleteAll() ;
        CategorieProduit categorieProduit = null ;
        for (int i = 0; i < categorieProduits.size(); ++i){
            categorieProduit = categorieProduits.get(i) ;
            Log.e("Debug", categorieProduit.getCode() + " " + categorieProduit.getLibelle()) ;
            ContentValues contentValues = new ContentValues();
            contentValues.put(CategorieProduitHelper.TABLE_KEY, categorieProduit.getId());
            contentValues.put(CategorieProduitHelper.LIBELLE, categorieProduit.getLibelle());
            contentValues.put(CategorieProduitHelper.CREATED_AT, formatter.format(categorieProduit.getCreated_at()));

            mDb.insert(CategorieProduitHelper.TABLE_NAME, null, contentValues) ;
        }

        return true;
    }

    @Override
    public int delete(long id) {
        return mDb.delete(CategorieProduitHelper.TABLE_NAME,  CategorieProduitHelper.TABLE_KEY + " = ? ", new String[]{Long.toString(id)});
    }


    public int clean() {
        return  mDb.delete(CategorieProduitHelper.TABLE_NAME, null,null);
    }

    public int deleteAll() {
        return mDb.delete(CategorieProduitHelper.TABLE_NAME,null, null);
    }

    @Override
    public int update(CategorieProduit categorieProduit) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(CategorieProduitHelper.TABLE_KEY, categorieProduit.getId());
        contentValues.put(CategorieProduitHelper.LIBELLE, categorieProduit.getLibelle());
        contentValues.put(CategorieProduitHelper.CREATED_AT, formatter.format(categorieProduit.getCreated_at()));

        int i = mDb.update(CategorieProduitHelper.TABLE_NAME, contentValues, CategorieProduitHelper.TABLE_KEY + " = ? ", new String[]{Long.toString(categorieProduit.getId())});
        Log.e("UPDATE", String.valueOf(i)) ;

        return i;
    }

    @Override
    public CategorieProduit getOne(long id) {
        CategorieProduit categorieProduit = null;

        Cursor res = mDb.rawQuery("select * from " + CategorieProduitHelper.TABLE_NAME + " where " + CategorieProduitHelper.TABLE_KEY + "=" + id + "", null);

        if (res.moveToFirst()){

            categorieProduit = new CategorieProduit();

            categorieProduit.setId(res.getInt(res.getColumnIndex(CategorieProduitHelper.TABLE_KEY)));
            categorieProduit.setLibelle(res.getString(res.getColumnIndex(CategorieProduitHelper.LIBELLE)));



            try {
                categorieProduit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(CategorieProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        res.close();
        return categorieProduit;
    }


    @Override
    public ArrayList<CategorieProduit> getAll() {
        ArrayList<CategorieProduit> categorieProduits = new ArrayList<CategorieProduit>();
        CategorieProduit categorieProduit = null;

        Cursor res = mDb.rawQuery("select * from " + CategorieProduitHelper.TABLE_NAME + " order by " +  CategorieProduitHelper.LIBELLE + " asc ", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            categorieProduit = new CategorieProduit();

            categorieProduit.setId(res.getInt(res.getColumnIndex(CategorieProduitHelper.TABLE_KEY)));
            categorieProduit.setLibelle(res.getString(res.getColumnIndex(CategorieProduitHelper.LIBELLE)));

            try {
                categorieProduit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(CategorieProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.i("Debug", categorieProduit.getLibelle()) ;

            categorieProduits.add(categorieProduit);
            res.moveToNext();
        }
        res.close();
        return categorieProduits;
    }

    public boolean exist(String s) {
        Cursor res = mDb.rawQuery("select * from " + CategorieProduitHelper.TABLE_NAME + " where " + CategorieProduitHelper.TABLE_KEY + "='" + s + "'", null);

        if (res.moveToFirst()){
            return true ;
        }

        res.close();
        return false;
    }

}