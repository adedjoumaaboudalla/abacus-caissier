package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.DeviseHelper;
import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.model.Devise;
import pv.projects.mediasoft.com.pventes.model.Operation;

/**
 * Created by Mayi on 05/10/2015.
 */
public class DeviseDAO extends DAOBase implements Crud<Devise> {


    public DeviseDAO(Context pContext) {
        super(pContext);
        this.mHandler = DeviseHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(Devise caisse) {
        
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeviseHelper.TABLE_KEY, caisse.getId());
        contentValues.put(DeviseHelper.CODEDEVISE, caisse.getCodedevise());
        contentValues.put(DeviseHelper.LIBELLE_DEVISE, caisse.getLibelledevise());
        contentValues.put(DeviseHelper.COURS_MOYEN, caisse.getCoursmoyen());
        contentValues.put(DeviseHelper.UNITE, caisse.getUnite());
        contentValues.put(DeviseHelper.SYMBOLE, caisse.getSymbole());
        contentValues.put(DeviseHelper.DEFAUT, caisse.getDefaut());

        long  l = mDb.insert(DeviseHelper.TABLE_NAME, null, contentValues);

        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(DeviseHelper.TABLE_NAME,  DeviseHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }


    public int clean() {
        return  mDb.delete(DeviseHelper.TABLE_NAME,  null,null);
    }

    @Override
    public int update(Devise caisse) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeviseHelper.CODEDEVISE, caisse.getCodedevise());
        contentValues.put(DeviseHelper.LIBELLE_DEVISE, caisse.getLibelledevise());
        contentValues.put(DeviseHelper.COURS_MOYEN, caisse.getCoursmoyen());
        contentValues.put(DeviseHelper.UNITE, caisse.getUnite());
        contentValues.put(DeviseHelper.SYMBOLE, caisse.getSymbole());
        contentValues.put(DeviseHelper.DEFAUT, caisse.getDefaut());

        int i = mDb.update(DeviseHelper.TABLE_NAME, contentValues, DeviseHelper.TABLE_KEY + " = ? ", new String[] { Long.toString(caisse.getId()) } );

        return i;
    }

    @Override
    public Devise getOne(long id) {
        Devise caisse = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ DeviseHelper.TABLE_NAME +" where " + DeviseHelper.TABLE_KEY + "="+id+"", null );

        if (res.moveToFirst()){

            caisse = new Devise();

            caisse.setId(res.getLong(res.getColumnIndex(DeviseHelper.TABLE_KEY)));
            caisse.setCodedevise(res.getString(res.getColumnIndex(DeviseHelper.CODEDEVISE)));
            caisse.setLibelledevise(res.getString(res.getColumnIndex(DeviseHelper.LIBELLE_DEVISE)));
            caisse.setCoursmoyen(res.getDouble(res.getColumnIndex(DeviseHelper.COURS_MOYEN)));
            caisse.setUnite(res.getString(res.getColumnIndex(DeviseHelper.UNITE)));
            caisse.setSymbole(res.getString(res.getColumnIndex(DeviseHelper.SYMBOLE)));
            caisse.setDefaut(res.getInt(res.getColumnIndex(DeviseHelper.DEFAUT)));


         };

        res.close();
        return caisse ;
    }


    public Devise getReference() {
        Devise caisse = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ DeviseHelper.TABLE_NAME +" where " + DeviseHelper.DEFAUT + "= 1 ", null );

        if (res.moveToFirst()){

            caisse = new Devise();

            caisse.setId(res.getLong(res.getColumnIndex(DeviseHelper.TABLE_KEY)));
            caisse.setCodedevise(res.getString(res.getColumnIndex(DeviseHelper.CODEDEVISE)));
            caisse.setLibelledevise(res.getString(res.getColumnIndex(DeviseHelper.LIBELLE_DEVISE)));
            caisse.setCoursmoyen(res.getDouble(res.getColumnIndex(DeviseHelper.COURS_MOYEN)));
            caisse.setUnite(res.getString(res.getColumnIndex(DeviseHelper.UNITE)));
            caisse.setSymbole(res.getString(res.getColumnIndex(DeviseHelper.SYMBOLE)));
            caisse.setDefaut(res.getInt(res.getColumnIndex(DeviseHelper.DEFAUT)));


         };

        res.close();
        return caisse ;
    }

    public Devise getFirst() {
        Devise caisse = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ DeviseHelper.TABLE_NAME +" order by " + DeviseHelper.TABLE_KEY  + " desc", null );

        if (res.moveToFirst()){

            caisse = new Devise();

            caisse.setId(res.getLong(res.getColumnIndex(DeviseHelper.TABLE_KEY)));
            caisse.setCodedevise(res.getString(res.getColumnIndex(DeviseHelper.CODEDEVISE)));
            caisse.setLibelledevise(res.getString(res.getColumnIndex(DeviseHelper.LIBELLE_DEVISE)));
            caisse.setCoursmoyen(res.getDouble(res.getColumnIndex(DeviseHelper.COURS_MOYEN)));
            caisse.setUnite(res.getString(res.getColumnIndex(DeviseHelper.UNITE)));
            caisse.setSymbole(res.getString(res.getColumnIndex(DeviseHelper.SYMBOLE)));
            caisse.setDefaut(res.getInt(res.getColumnIndex(DeviseHelper.DEFAUT)));



        };

        res.close();
        return caisse ;
    }

    @Override
    public ArrayList<Devise> getAll() {
        ArrayList<Devise> caisses = new ArrayList<Devise>();
        Devise caisse = null ;

        Cursor res =  mDb.rawQuery( "select * from "+DeviseHelper.TABLE_NAME+ " order by " + DeviseHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            caisse = new Devise();

            caisse.setId(res.getLong(res.getColumnIndex(DeviseHelper.TABLE_KEY)));
            caisse.setCodedevise(res.getString(res.getColumnIndex(DeviseHelper.CODEDEVISE)));
            caisse.setLibelledevise(res.getString(res.getColumnIndex(DeviseHelper.LIBELLE_DEVISE)));
            caisse.setCoursmoyen(res.getDouble(res.getColumnIndex(DeviseHelper.COURS_MOYEN)));
            caisse.setUnite(res.getString(res.getColumnIndex(DeviseHelper.UNITE)));
            caisse.setSymbole(res.getString(res.getColumnIndex(DeviseHelper.SYMBOLE)));
            caisse.setDefaut(res.getInt(res.getColumnIndex(DeviseHelper.DEFAUT)));


            caisses.add(caisse);
            res.moveToNext();
        }
        res.close();
        return caisses;
    }


}
