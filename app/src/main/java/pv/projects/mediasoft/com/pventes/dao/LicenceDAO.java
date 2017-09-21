package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.JournalHelper;
import pv.projects.mediasoft.com.pventes.database.LicenceHelper;
import pv.projects.mediasoft.com.pventes.model.Licence;


/**
 * Created by Mayi on 05/10/2015.
 */
public class LicenceDAO extends DAOBase implements Crud<Licence> {

    public LicenceDAO(Context pContext) {
        super(pContext);
        this.mHandler = LicenceHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(Licence licence) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LicenceHelper.CODE, licence.getCode());
        contentValues.put(LicenceHelper.DATEEXP, formatter.format(licence.getDateExp()));
        contentValues.put(LicenceHelper.CREATED_AT, formatter.format(licence.getCreated_at()));

        long l = mDb.insert(LicenceHelper.TABLE_NAME, null, contentValues);

        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(LicenceHelper.TABLE_NAME, LicenceHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }

    @Override
    public int update(Licence licence) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LicenceHelper.CODE, licence.getCode());
        contentValues.put(LicenceHelper.DATEEXP, formatter.format(licence.getDateExp()));
        contentValues.put(LicenceHelper.CREATED_AT, formatter.format(licence.getCreated_at()));

        int i  = mDb.update(LicenceHelper.TABLE_NAME, contentValues,  LicenceHelper.TABLE_KEY + " = ? ", new String[] { Long.toString(licence.getId()) } );

        return i;
    }

    @Override
    public Licence getOne(long id) {
        Licence licence = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ LicenceHelper.TABLE_NAME +" where " + LicenceHelper.TABLE_KEY +  "="+id+"", null );

        if (res.moveToFirst()){
            licence = new Licence();

            licence.setId(res.getLong(res.getColumnIndex(LicenceHelper.TABLE_KEY)));
            licence.setCode(res.getString(res.getColumnIndex(LicenceHelper.CODE)));
            licence.setDateExp(java.sql.Date.valueOf(res.getString(res.getColumnIndex(LicenceHelper.DATEEXP))));

            try {
                licence.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(LicenceHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        res.close();
            return licence;
    }


    public Licence getLast() {
        Licence licence = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ LicenceHelper.TABLE_NAME +" order by  " + LicenceHelper.TABLE_KEY +  " desc", null );

        if (res.moveToFirst()){
            licence = new Licence();

            licence.setId(res.getLong(res.getColumnIndex(LicenceHelper.TABLE_KEY)));
            licence.setCode(res.getString(res.getColumnIndex(LicenceHelper.CODE)));
            licence.setDateExp(java.sql.Date.valueOf(res.getString(res.getColumnIndex(LicenceHelper.DATEEXP))));

            try {
                licence.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(LicenceHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        res.close();
            return licence;
    }

    @Override
    public ArrayList<Licence> getAll() {
        ArrayList<Licence> licences = new ArrayList<Licence>();
        Licence licence = null ;

        Cursor res =  mDb.rawQuery( "select * from "+LicenceHelper.TABLE_NAME, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            licence = new Licence();

            licence.setId(res.getLong(res.getColumnIndex(LicenceHelper.TABLE_KEY)));
            licence.setCode(res.getString(res.getColumnIndex(LicenceHelper.CODE)));
            licence.setDateExp(java.sql.Date.valueOf(res.getString(res.getColumnIndex(LicenceHelper.DATEEXP))));

            try {
                licence.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(LicenceHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            licences.add(licence);
            res.moveToNext();
        }
        res.close();
        return licences;
    }

}
