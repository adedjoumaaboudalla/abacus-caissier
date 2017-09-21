package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.LicenceHelper;
import pv.projects.mediasoft.com.pventes.database.ModePayementHelper;
import pv.projects.mediasoft.com.pventes.model.ModePayement;


/**
 * Created by Mayi on 16/10/2015.
 */
public class ModePayementDAO extends DAOBase implements Crud<ModePayement> {

    public ModePayementDAO(Context pContext) {
        super(pContext);
        this.mHandler = ModePayementHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(ModePayement modePayement) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ModePayementHelper.TABLE_KEY, modePayement.getCode());
        contentValues.put(ModePayementHelper.LIBELLE, modePayement.getLibelle());
        contentValues.put(ModePayementHelper.CREATED_AT, formatter.format(modePayement.getCreated_at()));

        long l = mDb.insert(ModePayementHelper.TABLE_NAME, null, contentValues) ;

        Log.e("ADD", String.valueOf(l)) ;

        return l ;
    }


    public boolean addAll(ArrayList<ModePayement> modePayements) {
        if (modePayements.size() > 0) clean() ;
        ModePayement modePayement = null ;
        for (int i = 0; i < modePayements.size(); ++i){
            modePayement = modePayements.get(i) ;
            Log.e("Debug", modePayement.getCode() + " " + modePayement.getLibelle()) ;
            ContentValues contentValues = new ContentValues();
            contentValues.put(ModePayementHelper.TABLE_KEY, modePayement.getCode());
            contentValues.put(ModePayementHelper.LIBELLE, modePayement.getLibelle());
            contentValues.put(ModePayementHelper.CREATED_AT, formatter.format(modePayement.getCreated_at()));

            mDb.insert(ModePayementHelper.TABLE_NAME, null, contentValues) ;
        }

        return true;
    }

    @Override
    public int delete(long id) {
        return mDb.delete(ModePayementHelper.TABLE_NAME,  ModePayementHelper.TABLE_KEY + " = ? ", new String[]{Long.toString(id)});
    }


    public int clean() {
        return mDb.delete(ModePayementHelper.TABLE_NAME,null, null);
    }

    @Override
    public int update(ModePayement modePayement) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(ModePayementHelper.TABLE_KEY, modePayement.getCode());
        contentValues.put(ModePayementHelper.LIBELLE, modePayement.getLibelle());
        contentValues.put(ModePayementHelper.CREATED_AT, formatter.format(modePayement.getCreated_at()));

        int i = mDb.update(ModePayementHelper.TABLE_NAME, contentValues, ModePayementHelper.TABLE_KEY + " = ? ", new String[]{(modePayement.getCode())});
        Log.e("UPDATE", String.valueOf(i)) ;

        return i;
    }

    @Override
    public ModePayement getOne(long id) {
        ModePayement modePayement = null;

        Cursor res = mDb.rawQuery("select * from " + ModePayementHelper.TABLE_NAME + " where " + ModePayementHelper.TABLE_KEY + "=" + id + "", null);

        if (res.moveToFirst()){

            modePayement = new ModePayement();

            modePayement.setCode(res.getString(res.getColumnIndex(ModePayementHelper.TABLE_KEY)));
            modePayement.setLibelle(res.getString(res.getColumnIndex(ModePayementHelper.LIBELLE)));

            try {
                modePayement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ModePayementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        res.close();
        return modePayement;
    }


    @Override
    public ArrayList<ModePayement> getAll() {
        ArrayList<ModePayement> modePayements = new ArrayList<ModePayement>();
        ModePayement modePayement = null;

        Cursor res = mDb.rawQuery("select * from " + ModePayementHelper.TABLE_NAME + " order by " +  ModePayementHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            modePayement = new ModePayement();

            modePayement.setCode(res.getString(res.getColumnIndex(ModePayementHelper.TABLE_KEY)));
            modePayement.setLibelle(res.getString(res.getColumnIndex(ModePayementHelper.LIBELLE)));

            try {
                modePayement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ModePayementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("Debug", modePayement.getLibelle()) ;

            modePayements.add(modePayement);
            res.moveToNext();
        }
        res.close();
        return modePayements;
    }

    public boolean exist(String s) {
        Cursor res = mDb.rawQuery("select * from " + ModePayementHelper.TABLE_NAME + " where " + ModePayementHelper.TABLE_KEY + "='" + s + "'", null);

        if (res.moveToFirst()){
            return true ;
        }

        return false;
    }

}