package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.database.PartenaireHelper;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Produit;


/**
 * Created by Mayi on 21/04/2011.
 */
/*
public class PayementDAO extends DAOBase implements Crud<Payement> {


    private OperationDAO operationDAO;

    public PayementDAO(Context pContext) {
        super(pContext);
        this.mHandler = PayementHelper.getHelper(pContext, DATABASE, VERSION);
        operationDAO = new OperationDAO(pContext) ;
        open();
    }

    @Override
    public long add(Payement payement) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PayementHelper.OPERATION_ID, payement.getOperation_id());
        contentValues.put(PayementHelper.IDEXTERNE, payement.getId_externe());
        contentValues.put(PayementHelper.ETAT, payement.getEtat());
        contentValues.put(PayementHelper.MONTANT, payement.getMontant());
        contentValues.put(PayementHelper.MODEPAYEMENT, payement.getModepayement());
        contentValues.put(PayementHelper.NUMCHEQUE, payement.getNumcheque());
        contentValues.put(PayementHelper.BANQUE_ID, payement.getBanque_id());
        contentValues.put(PayementHelper.CREATED_AT, formatter.format(payement.getCreated_at()));


        long  l = mDb.insert(PayementHelper.TABLE_NAME, null, contentValues);

        Log.e("DEBUG PAYEMENT",String.valueOf(l)) ;
        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(PayementHelper.TABLE_NAME, PayementHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }


    public int clean() {
        return  mDb.delete(PayementHelper.TABLE_NAME, null,null);
    }


    public int deleteOP(long id) {
        return  mDb.delete(PayementHelper.TABLE_NAME, PayementHelper.OPERATION_ID + " = ? ",new String[] { Long.toString(id)});
    }

    @Override
    public int update(Payement payement) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PayementHelper.OPERATION_ID, payement.getOperation_id());
        contentValues.put(PayementHelper.IDEXTERNE, payement.getId_externe());
        contentValues.put(PayementHelper.ETAT, payement.getEtat());
        contentValues.put(PayementHelper.MONTANT, payement.getMontant());
        contentValues.put(PayementHelper.MODEPAYEMENT, payement.getModepayement());
        contentValues.put(PayementHelper.NUMCHEQUE, payement.getNumcheque());
        contentValues.put(PayementHelper.BANQUE_ID, payement.getBanque_id());
        contentValues.put(PayementHelper.CREATED_AT, formatter.format(payement.getCreated_at()));

        int i = mDb.update(PayementHelper.TABLE_NAME, contentValues, PartenaireHelper.TABLE_KEY  + " = ? ", new String[]{Long.toString(payement.getId())});

        return i;
    }


    @Override
    public Payement getOne(long id) {
        Payement payement = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ PayementHelper.TABLE_NAME +" where " + PayementHelper.TABLE_KEY +  "="+id+"", null );

        if (res.moveToFirst()){
            payement = new Payement();

            payement.setId(res.getLong(res.getColumnIndex(PayementHelper.TABLE_KEY)));
            payement.setId_externe(res.getLong(res.getColumnIndex(PayementHelper.IDEXTERNE)));
            payement.setEtat(res.getInt(res.getColumnIndex(PayementHelper.ETAT)));
            payement.setMontant(res.getDouble(res.getColumnIndex(PayementHelper.MONTANT)));
            payement.setOperation_id(res.getInt(res.getColumnIndex(PayementHelper.OPERATION_ID)));
            payement.setModepayement(res.getString(res.getColumnIndex(PayementHelper.MODEPAYEMENT)));
            payement.setNumcheque(res.getString(res.getColumnIndex(PayementHelper.NUMCHEQUE)));
            payement.setBanque_id(res.getLong(res.getColumnIndex(PayementHelper.BANQUE_ID)));

            try {
                payement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };


        res.close();
        return payement ;
    }

    @Override
    public ArrayList<Payement> getAll() {
        ArrayList<Payement> payements = new ArrayList<Payement>();
        Payement payement = null ;

        Cursor res =  mDb.rawQuery( "select * from "+PayementHelper.TABLE_NAME, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            payement = new Payement();

            payement.setId(res.getLong(res.getColumnIndex(PayementHelper.TABLE_KEY)));
            payement.setId_externe(res.getLong(res.getColumnIndex(PayementHelper.IDEXTERNE)));
            payement.setEtat(res.getInt(res.getColumnIndex(PayementHelper.ETAT)));
            payement.setMontant(res.getDouble(res.getColumnIndex(PayementHelper.MONTANT)));
            payement.setOperation_id(res.getInt(res.getColumnIndex(PayementHelper.OPERATION_ID)));
            payement.setModepayement(res.getString(res.getColumnIndex(PayementHelper.MODEPAYEMENT)));
            payement.setNumcheque(res.getString(res.getColumnIndex(PayementHelper.NUMCHEQUE)));
            payement.setBanque_id(res.getLong(res.getColumnIndex(PayementHelper.BANQUE_ID)));

            try {
                payement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            payements.add(payement);
            res.moveToNext();
        }
        res.close();
        return payements;
    }

    public ArrayList<Payement> getMany(long id) {
        ArrayList<Payement> payements = new ArrayList<Payement>();
        Payement payement = null ;
        Cursor res = null ;
        Log.e("DEBUG","select * from "+PayementHelper.TABLE_NAME + " Where " + PayementHelper.OPERATION_ID + " = " + id) ;
        res =  mDb.rawQuery( "select * from "+PayementHelper.TABLE_NAME + " Where " + PayementHelper.OPERATION_ID + " = " + id + " order by _id desc", null);


        res.moveToFirst();

        while(res.isAfterLast() == false){
            payement = new Payement();

            payement.setId(res.getLong(res.getColumnIndex(PayementHelper.TABLE_KEY)));
            payement.setId_externe(res.getLong(res.getColumnIndex(PayementHelper.IDEXTERNE)));
            payement.setEtat(res.getInt(res.getColumnIndex(PayementHelper.ETAT)));
            payement.setMontant(res.getDouble(res.getColumnIndex(PayementHelper.MONTANT)));
            payement.setOperation_id(res.getInt(res.getColumnIndex(PayementHelper.OPERATION_ID)));
            payement.setModepayement(res.getString(res.getColumnIndex(PayementHelper.MODEPAYEMENT)));
            payement.setNumcheque(res.getString(res.getColumnIndex(PayementHelper.NUMCHEQUE)));
            payement.setBanque_id(res.getLong(res.getColumnIndex(PayementHelper.BANQUE_ID)));

            try {
                payement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            payements.add(payement);
            res.moveToNext();
        }
        res.close();
        return payements;
    }


    public ArrayList<Payement> getNonSync(long id) {
        ArrayList<Payement> payements = new ArrayList<Payement>();
        Payement payement = null ;

        Log.e("DEBUG","select * from "+PayementHelper.TABLE_NAME + " Where  " + PayementHelper.ETAT + " = 0 and " + PayementHelper.OPERATION_ID + " = " + id) ;
        Cursor res =  mDb.rawQuery( "select * from "+PayementHelper.TABLE_NAME + " Where  " + PayementHelper.ETAT + " = 0 and " + PayementHelper.OPERATION_ID + " = " + id, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            payement = new Payement();
            payement.setId(res.getLong(res.getColumnIndex(PayementHelper.TABLE_KEY)));
            payement.setId_externe(res.getLong(res.getColumnIndex(PayementHelper.IDEXTERNE)));
            payement.setEtat(res.getInt(res.getColumnIndex(PayementHelper.ETAT)));
            payement.setMontant(res.getDouble(res.getColumnIndex(PayementHelper.MONTANT)));
            payement.setOperation_id(res.getInt(res.getColumnIndex(PayementHelper.OPERATION_ID)));
            payement.setModepayement(res.getString(res.getColumnIndex(PayementHelper.MODEPAYEMENT)));
            payement.setNumcheque(res.getString(res.getColumnIndex(PayementHelper.NUMCHEQUE)));
            payement.setBanque_id(res.getLong(res.getColumnIndex(PayementHelper.BANQUE_ID)));

            try {
                payement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            payements.add(payement);
            res.moveToNext();
        }
        res.close();
        return payements;
    }

    public ArrayList<Payement> getNonSync() {
        ArrayList<Payement> payements = new ArrayList<Payement>();
        Payement payement = null ;

        Cursor res =  mDb.rawQuery( "select * from "+PayementHelper.TABLE_NAME + " Where  " + PayementHelper.ETAT + " <> 2  ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            payement = new Payement();

            payement.setId(res.getLong(res.getColumnIndex(PayementHelper.TABLE_KEY)));
            payement.setId_externe(res.getLong(res.getColumnIndex(PayementHelper.IDEXTERNE)));
            payement.setEtat(res.getInt(res.getColumnIndex(PayementHelper.ETAT)));
            payement.setMontant(res.getDouble(res.getColumnIndex(PayementHelper.MONTANT)));
            payement.setOperation_id(res.getInt(res.getColumnIndex(PayementHelper.OPERATION_ID)));
            payement.setModepayement(res.getString(res.getColumnIndex(PayementHelper.MODEPAYEMENT)));
            payement.setNumcheque(res.getString(res.getColumnIndex(PayementHelper.NUMCHEQUE)));
            payement.setBanque_id(res.getLong(res.getColumnIndex(PayementHelper.BANQUE_ID)));

            try {
                payement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //if (!operationDAO.getOneExterne(payement.getOperation_id()).getTypeOperation_id().startsWith(OperationDAO.CMD)) payements.add(payement);

            payements.add(payement);
            res.moveToNext();
        }

        Log.e("SIZE PAYMT NON", String.valueOf(payements.size()));
        res.close();
        return payements;
    }

    public int updateMany(Operation vente) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PayementHelper.OPERATION_ID, vente.getId_externe());
        contentValues.put(PayementHelper.ETAT, 0);

        int i = mDb.update(PayementHelper.TABLE_NAME, contentValues, PayementHelper.OPERATION_ID + " = ? ", new String[] { Long.toString(vente.getId()) } );
        Log.e("UPDATE",String.valueOf(i)) ;
        return i ;
    }

}
 */