package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.BilletHelper;
import pv.projects.mediasoft.com.pventes.database.PointVenteHelper;
import pv.projects.mediasoft.com.pventes.database.TypeOperationHelper;
import pv.projects.mediasoft.com.pventes.model.TypeOperation;


/**
 * Created by Mayi on 16/10/2015.
 */
public class TypeOperationDAO extends DAOBase implements Crud<TypeOperation> {

    public TypeOperationDAO(Context pContext) {
        super(pContext);
        this.mHandler = TypeOperationHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(TypeOperation typeOperation) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TypeOperationHelper.TABLE_KEY, typeOperation.getCode());
        contentValues.put(TypeOperationHelper.LIBELLE, typeOperation.getLibelle());
        contentValues.put(TypeOperationHelper.CREATED_AT, formatter.format(typeOperation.getCreated_at()));

        long l = mDb.insert(TypeOperationHelper.TABLE_NAME, null, contentValues) ;

        Log.e("ADD", String.valueOf(l)) ;

        return l ;
    }


    public boolean addAll(ArrayList<TypeOperation> typeOperations) {
        if (typeOperations.size() > 0) deleteAll() ;
        TypeOperation typeOperation = null ;
        for (int i = 0; i < typeOperations.size(); ++i){
            typeOperation = typeOperations.get(i) ;
            Log.e("Debug", typeOperation.getCode() + " " + typeOperation.getLibelle()) ;
            ContentValues contentValues = new ContentValues();
            contentValues.put(TypeOperationHelper.TABLE_KEY, typeOperation.getCode());
            contentValues.put(TypeOperationHelper.LIBELLE, typeOperation.getLibelle());
            contentValues.put(TypeOperationHelper.CREATED_AT, formatter.format(typeOperation.getCreated_at()));

            mDb.insert(TypeOperationHelper.TABLE_NAME, null, contentValues) ;
        }

        return true;
    }

    @Override
    public int delete(long id) {
        return mDb.delete(TypeOperationHelper.TABLE_NAME,  TypeOperationHelper.TABLE_KEY + " = ? ", new String[]{Long.toString(id)});
    }



    public int clean() {
        return  mDb.delete(TypeOperationHelper.TABLE_NAME, null,null);
    }

    public int deleteAll() {
        return mDb.delete(TypeOperationHelper.TABLE_NAME,null, null);
    }

    @Override
    public int update(TypeOperation typeOperation) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(TypeOperationHelper.TABLE_KEY, typeOperation.getCode());
        contentValues.put(TypeOperationHelper.LIBELLE, typeOperation.getLibelle());
        contentValues.put(TypeOperationHelper.CREATED_AT, formatter.format(typeOperation.getCreated_at()));

        int i = mDb.update(TypeOperationHelper.TABLE_NAME, contentValues, TypeOperationHelper.TABLE_KEY + " = ? ", new String[]{typeOperation.getCode()});
        Log.e("UPDATE", String.valueOf(i)) ;

        return i;
    }

    @Override
    public TypeOperation getOne(long id) {
        TypeOperation typeOperation = null;

        Cursor res = mDb.rawQuery("select * from " + TypeOperationHelper.TABLE_NAME + " where " + TypeOperationHelper.TABLE_KEY + "=" + id + "", null);

        if (res.moveToFirst()){

            typeOperation = new TypeOperation();

            typeOperation.setCode(res.getString(res.getColumnIndex(TypeOperationHelper.TABLE_KEY)));
            typeOperation.setLibelle(res.getString(res.getColumnIndex(TypeOperationHelper.LIBELLE)));
            try {
                typeOperation.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(TypeOperationHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        res.close();
        return typeOperation;
    }


    public TypeOperation getOne(String id) {
        TypeOperation typeOperation = null;

        Cursor res = mDb.rawQuery("select * from " + TypeOperationHelper.TABLE_NAME + " where " + TypeOperationHelper.TABLE_KEY + "='" + id + "'", null);

        if (res.moveToFirst()){

            typeOperation = new TypeOperation();

            typeOperation.setCode(res.getString(res.getColumnIndex(TypeOperationHelper.TABLE_KEY)));
            typeOperation.setLibelle(res.getString(res.getColumnIndex(TypeOperationHelper.LIBELLE)));
            try {
                typeOperation.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(TypeOperationHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        res.close();
        return typeOperation;
    }


    @Override
    public ArrayList<TypeOperation> getAll() {
        ArrayList<TypeOperation> typeOperations = new ArrayList<TypeOperation>();
        TypeOperation typeOperation = null;

        Cursor res = mDb.rawQuery("select * from " + TypeOperationHelper.TABLE_NAME + " order by " +  TypeOperationHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            typeOperation = new TypeOperation();

            typeOperation.setCode(res.getString(res.getColumnIndex(TypeOperationHelper.TABLE_KEY)));
            typeOperation.setLibelle(res.getString(res.getColumnIndex(TypeOperationHelper.LIBELLE)));
            try {
                typeOperation.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(TypeOperationHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("Debug", typeOperation.getLibelle()) ;

            typeOperations.add(typeOperation);
            res.moveToNext();
        }
        res.close();
        return typeOperations;
    }


    public ArrayList<TypeOperation> getAllDepense() {
        ArrayList<TypeOperation> typeOperations = new ArrayList<TypeOperation>();
        TypeOperation typeOperation = null;

        Cursor res = mDb.rawQuery("select * from " + TypeOperationHelper.TABLE_NAME + " where " + TypeOperationHelper.TABLE_KEY + " LIKE 'DP_%'  order by " +  TypeOperationHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            typeOperation = new TypeOperation();

            typeOperation.setCode(res.getString(res.getColumnIndex(TypeOperationHelper.TABLE_KEY)));
            typeOperation.setLibelle(res.getString(res.getColumnIndex(TypeOperationHelper.LIBELLE)));
            try {
                typeOperation.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(TypeOperationHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("Debug", typeOperation.getLibelle()) ;

            typeOperations.add(typeOperation);
            res.moveToNext();
        }
        res.close();
        return typeOperations;
    }



    public ArrayList<TypeOperation> getAllImmo() {
        ArrayList<TypeOperation> typeOperations = new ArrayList<TypeOperation>();
        TypeOperation typeOperation = null;

        Cursor res = mDb.rawQuery("select * from " + TypeOperationHelper.TABLE_NAME + " where " + TypeOperationHelper.TABLE_KEY + " LIKE 'IMMO_%'  order by " +  TypeOperationHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            typeOperation = new TypeOperation();

            typeOperation.setCode(res.getString(res.getColumnIndex(TypeOperationHelper.TABLE_KEY)));
            typeOperation.setLibelle(res.getString(res.getColumnIndex(TypeOperationHelper.LIBELLE)));
            try {
                typeOperation.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(TypeOperationHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("Debug", typeOperation.getLibelle()) ;

            typeOperations.add(typeOperation);
            res.moveToNext();
        }
        res.close();
        return typeOperations;
    }



    public ArrayList<TypeOperation> getAllPlacement() {
        ArrayList<TypeOperation> typeOperations = new ArrayList<TypeOperation>();
        TypeOperation typeOperation = null;

        Cursor res = mDb.rawQuery("select * from " + TypeOperationHelper.TABLE_NAME + " where " + TypeOperationHelper.TABLE_KEY + " LIKE 'PLAC_%'  order by " +  TypeOperationHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            typeOperation = new TypeOperation();

            typeOperation.setCode(res.getString(res.getColumnIndex(TypeOperationHelper.TABLE_KEY)));
            typeOperation.setLibelle(res.getString(res.getColumnIndex(TypeOperationHelper.LIBELLE)));
            try {
                typeOperation.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(TypeOperationHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("Debug", typeOperation.getLibelle()) ;

            typeOperations.add(typeOperation);
            res.moveToNext();
        }
        res.close();
        return typeOperations;
    }



    public ArrayList<TypeOperation> getAllDivers() {
        ArrayList<TypeOperation> typeOperations = new ArrayList<TypeOperation>();
        TypeOperation typeOperation = null;

        Cursor res = mDb.rawQuery("select * from " + TypeOperationHelper.TABLE_NAME + " where " + TypeOperationHelper.TABLE_KEY + " LIKE 'PD_%' OR  " + TypeOperationHelper.TABLE_KEY + " LIKE 'CH_%' order by " +  TypeOperationHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            typeOperation = new TypeOperation();

            typeOperation.setCode(res.getString(res.getColumnIndex(TypeOperationHelper.TABLE_KEY)));
            typeOperation.setLibelle(res.getString(res.getColumnIndex(TypeOperationHelper.LIBELLE)));
            try {
                typeOperation.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(TypeOperationHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("Debug", typeOperation.getLibelle()) ;

            typeOperations.add(typeOperation);
            res.moveToNext();
        }
        res.close();
        return typeOperations;
    }

    public boolean exist(String s) {
        Cursor res = mDb.rawQuery("select * from " + TypeOperationHelper.TABLE_NAME + " where " + TypeOperationHelper.TABLE_KEY + "='" + s + "'", null);

        if (res.moveToFirst()){
            return true ;
        }

        return false;
    }

}