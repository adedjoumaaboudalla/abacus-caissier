package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.DeviseHelper;
import pv.projects.mediasoft.com.pventes.database.DeviseOperationHelper;
import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.model.DeviseOperation;
import pv.projects.mediasoft.com.pventes.model.Operation;

/**
 * Created by Mayi on 05/10/2015.
 */
public class DeviseOperationDAO extends DAOBase implements Crud<DeviseOperation> {


    public DeviseOperationDAO(Context pContext) {
        super(pContext);
        this.mHandler = DeviseOperationHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(DeviseOperation deviseOperation) {
        
        ContentValues contentValues = new ContentValues();

        contentValues.put(DeviseOperationHelper.OPERATION_ID, deviseOperation.getOperation_id());
        contentValues.put(DeviseOperationHelper.DEVISE_ID, deviseOperation.getDevise_id());
        contentValues.put(DeviseOperationHelper.MONTANT, deviseOperation.getMontant());

        long  l = mDb.insert(DeviseOperationHelper.TABLE_NAME, null, contentValues);

        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(DeviseOperationHelper.TABLE_NAME,  DeviseOperationHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }


    public int clean() {
        return  mDb.delete(DeviseOperationHelper.TABLE_NAME,  null,null);
    }

    @Override
    public int update(DeviseOperation deviseOperation) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DeviseOperationHelper.OPERATION_ID, deviseOperation.getOperation_id());
        contentValues.put(DeviseOperationHelper.DEVISE_ID, deviseOperation.getDevise_id());
        contentValues.put(DeviseOperationHelper.MONTANT, deviseOperation.getMontant());

        int i = mDb.update(DeviseOperationHelper.TABLE_NAME, contentValues, DeviseOperationHelper.TABLE_KEY + " = ? ", new String[] { Long.toString(deviseOperation.getId()) } );

        return i;
    }

    @Override
    public DeviseOperation getOne(long id) {
        DeviseOperation deviseOperation = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ DeviseOperationHelper.TABLE_NAME +" where " + DeviseOperationHelper.TABLE_KEY + "="+id+"", null );

        if (res.moveToFirst()){
            deviseOperation = new DeviseOperation();

            deviseOperation.setId(res.getLong(res.getColumnIndex(DeviseOperationHelper.TABLE_KEY)));
            deviseOperation.setOperation_id(res.getLong(res.getColumnIndex(DeviseOperationHelper.OPERATION_ID)));
            deviseOperation.setDevise_id(res.getLong(res.getColumnIndex(DeviseOperationHelper.DEVISE_ID)));
            deviseOperation.setMontant(res.getDouble(res.getColumnIndex(DeviseOperationHelper.MONTANT)));
         };

        res.close();
        return deviseOperation ;
    }

    public DeviseOperation getFirst() {
        DeviseOperation deviseOperation = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ DeviseOperationHelper.TABLE_NAME +" order by " + DeviseOperationHelper.TABLE_KEY  + " desc", null );

        if (res.moveToFirst()){

            deviseOperation = new DeviseOperation();

            deviseOperation.setId(res.getLong(res.getColumnIndex(DeviseOperationHelper.TABLE_KEY)));
            deviseOperation.setOperation_id(res.getLong(res.getColumnIndex(DeviseOperationHelper.OPERATION_ID)));
            deviseOperation.setDevise_id(res.getLong(res.getColumnIndex(DeviseOperationHelper.DEVISE_ID)));
            deviseOperation.setMontant(res.getDouble(res.getColumnIndex(DeviseOperationHelper.MONTANT)));
         };

        res.close();
        return deviseOperation ;
    }

    @Override
    public ArrayList<DeviseOperation> getAll() {
        ArrayList<DeviseOperation> deviseOperations = new ArrayList<DeviseOperation>();
        DeviseOperation deviseOperation = null ;

        Cursor res =  mDb.rawQuery( "select * from "+DeviseOperationHelper.TABLE_NAME+ " order by " + DeviseOperationHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            deviseOperation = new DeviseOperation();


            deviseOperation.setId(res.getLong(res.getColumnIndex(DeviseOperationHelper.TABLE_KEY)));
            deviseOperation.setOperation_id(res.getLong(res.getColumnIndex(DeviseOperationHelper.OPERATION_ID)));
            deviseOperation.setDevise_id(res.getLong(res.getColumnIndex(DeviseOperationHelper.DEVISE_ID)));
            deviseOperation.setMontant(res.getDouble(res.getColumnIndex(DeviseOperationHelper.MONTANT)));

            deviseOperations.add(deviseOperation);
            res.moveToNext();
        }
        res.close();
        return deviseOperations;
    }


    public ArrayList<DeviseOperation> getMany(long operation_id) {
        ArrayList<DeviseOperation> deviseOperations = new ArrayList<DeviseOperation>();
        DeviseOperation deviseOperation = null ;

        Cursor res =  mDb.rawQuery( "select * from "+DeviseOperationHelper.TABLE_NAME+ " where " + DeviseOperationHelper.OPERATION_ID + " = "  + operation_id +  " order by " + DeviseOperationHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            deviseOperation = new DeviseOperation();


            deviseOperation.setId(res.getLong(res.getColumnIndex(DeviseOperationHelper.TABLE_KEY)));
            deviseOperation.setOperation_id(res.getLong(res.getColumnIndex(DeviseOperationHelper.OPERATION_ID)));
            deviseOperation.setDevise_id(res.getLong(res.getColumnIndex(DeviseOperationHelper.DEVISE_ID)));
            deviseOperation.setMontant(res.getDouble(res.getColumnIndex(DeviseOperationHelper.MONTANT)));

            deviseOperations.add(deviseOperation);
            res.moveToNext();
        }
        res.close();
        return deviseOperations;
    }


    public int updateMany(Operation operation) {
        ContentValues contentValues = new ContentValues();

        if (operation.getEtat()<2)contentValues.put(DeviseOperationHelper.OPERATION_ID, operation.getId());
        else contentValues.put(DeviseOperationHelper.OPERATION_ID, operation.getId_externe());


        int i = mDb.update(DeviseOperationHelper.TABLE_NAME, contentValues, DeviseOperationHelper.OPERATION_ID + " = ?", new String[] {Long.toString(operation.getId())});
        Log.e("UPDATE",String.valueOf(i)) ;
        return i ;
    }
}
