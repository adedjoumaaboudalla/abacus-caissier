package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.CompteBanqueHelper;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;


/**
 * Created by Mayi on 18/12/2015.
 */
public class CompteBanqueDAO extends DAOBase implements Crud<CompteBanque> {

    Context context = null ;

    public CompteBanqueDAO(Context pContext) {
        super(pContext);
        context = pContext ;
        this.mHandler = CompteBanqueHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(CompteBanque compteBanque) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(CompteBanqueHelper.ID_EXTERNE, compteBanque.getId_externe());
        contentValues.put(CompteBanqueHelper.LIBELLE, compteBanque.getLibelle());
        contentValues.put(CompteBanqueHelper.CODE, compteBanque.getCode());
        contentValues.put(CompteBanqueHelper.UTILISATEUR_ID, compteBanque.getUtilisateur_id());
        contentValues.put(CompteBanqueHelper.ETAT, compteBanque.getEtat());
        contentValues.put(CompteBanqueHelper.CHEQUE, compteBanque.getCheque());
        contentValues.put(CompteBanqueHelper.CARTEBANK, compteBanque.getCartebanque());
        contentValues.put(CompteBanqueHelper.SOLDE, compteBanque.getSolde());

        Long l = mDb.insert(CompteBanqueHelper.TABLE_NAME, null, contentValues);

        if (compteBanque.getId_externe()==0) {
            compteBanque.setId_externe(l);
            update(compteBanque) ;
        }
        Log.e("DEBUG", String.valueOf(l)) ;
        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(CompteBanqueHelper.TABLE_NAME, CompteBanqueHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }

    public int clean() {
        return  mDb.delete(CompteBanqueHelper.TABLE_NAME, null,null);
    }

    @Override
    public int update(CompteBanque compteBanque) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(CompteBanqueHelper.ID_EXTERNE, compteBanque.getId_externe());
        contentValues.put(CompteBanqueHelper.LIBELLE, compteBanque.getLibelle());
        contentValues.put(CompteBanqueHelper.CODE, compteBanque.getCode());
        contentValues.put(CompteBanqueHelper.ETAT, compteBanque.getEtat());
        contentValues.put(CompteBanqueHelper.CHEQUE, compteBanque.getCheque());
        contentValues.put(CompteBanqueHelper.CARTEBANK, compteBanque.getCartebanque());
        contentValues.put(CompteBanqueHelper.UTILISATEUR_ID, compteBanque.getUtilisateur_id());
        contentValues.put(CompteBanqueHelper.SOLDE, compteBanque.getSolde());

        int l = mDb.update(CompteBanqueHelper.TABLE_NAME, contentValues, CompteBanqueHelper.TABLE_KEY + " = ? ", new String[]{Long.toString(compteBanque.getId())});

        Log.e("DEBUG", String.valueOf(l)) ;
        return l;
    }

    @Override
    public CompteBanque getOne(long id) {
        CompteBanque compteBanque = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ CompteBanqueHelper.TABLE_NAME +" where " + CompteBanqueHelper.TABLE_KEY + "="+id+"", null );

        if (res.moveToFirst()) {
            compteBanque = new CompteBanque();

            compteBanque.setId(res.getLong(res.getColumnIndex(CompteBanqueHelper.TABLE_KEY)));
            compteBanque.setId_externe(res.getLong(res.getColumnIndex(CompteBanqueHelper.ID_EXTERNE)));
            compteBanque.setUtilisateur_id(res.getLong(res.getColumnIndex(CompteBanqueHelper.UTILISATEUR_ID)));
            compteBanque.setLibelle(res.getString(res.getColumnIndex(CompteBanqueHelper.LIBELLE)));
            compteBanque.setCode(res.getString(res.getColumnIndex(CompteBanqueHelper.CODE)));
            compteBanque.setEtat(res.getInt(res.getColumnIndex(CompteBanqueHelper.ETAT)));
            compteBanque.setCartebanque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CARTEBANK)));
            compteBanque.setCheque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CHEQUE)));
            compteBanque.setSolde(res.getDouble(res.getColumnIndex(CompteBanqueHelper.SOLDE)));
        }
        res.close();
        return compteBanque;
    }



    public CompteBanque getOneByIdExterne(long id) {
        CompteBanque compteBanque = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ CompteBanqueHelper.TABLE_NAME +" where " + CompteBanqueHelper.ID_EXTERNE + "="+id+"", null );

        if (res.moveToFirst()) {
            compteBanque = new CompteBanque();

            compteBanque.setId(res.getLong(res.getColumnIndex(CompteBanqueHelper.TABLE_KEY)));
            compteBanque.setId_externe(res.getLong(res.getColumnIndex(CompteBanqueHelper.ID_EXTERNE)));
            compteBanque.setUtilisateur_id(res.getLong(res.getColumnIndex(CompteBanqueHelper.UTILISATEUR_ID)));
            compteBanque.setLibelle(res.getString(res.getColumnIndex(CompteBanqueHelper.LIBELLE)));
            compteBanque.setCode(res.getString(res.getColumnIndex(CompteBanqueHelper.CODE)));
            compteBanque.setEtat(res.getInt(res.getColumnIndex(CompteBanqueHelper.ETAT)));
            compteBanque.setCartebanque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CARTEBANK)));
            compteBanque.setCheque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CHEQUE)));
            compteBanque.setSolde(res.getDouble(res.getColumnIndex(CompteBanqueHelper.SOLDE)));
        }
        res.close();
        return compteBanque;
    }


    public CompteBanque getOne(String code) {
        CompteBanque compteBanque = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ CompteBanqueHelper.TABLE_NAME +" where " + CompteBanqueHelper.CODE + "='"+code+"'", null );

        if (res.moveToFirst()) {
            compteBanque = new CompteBanque();

            compteBanque.setId(res.getLong(res.getColumnIndex(CompteBanqueHelper.TABLE_KEY)));
            compteBanque.setId_externe(res.getLong(res.getColumnIndex(CompteBanqueHelper.ID_EXTERNE)));
            compteBanque.setUtilisateur_id(res.getLong(res.getColumnIndex(CompteBanqueHelper.UTILISATEUR_ID)));
            compteBanque.setLibelle(res.getString(res.getColumnIndex(CompteBanqueHelper.LIBELLE)));
            compteBanque.setCode(res.getString(res.getColumnIndex(CompteBanqueHelper.CODE)));
            compteBanque.setEtat(res.getInt(res.getColumnIndex(CompteBanqueHelper.ETAT)));
            compteBanque.setCartebanque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CARTEBANK)));
            compteBanque.setCheque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CHEQUE)));
            compteBanque.setSolde(res.getDouble(res.getColumnIndex(CompteBanqueHelper.SOLDE)));
        }
        res.close();
        return compteBanque;
    }

    @Override
    public ArrayList<CompteBanque> getAll() {
        ArrayList<CompteBanque> compteBanques = new ArrayList<CompteBanque>();

        CompteBanque compteBanque = null ;
        Cursor res =  mDb.rawQuery( "select * from "+ CompteBanqueHelper.TABLE_NAME+ " order by "+ CompteBanqueHelper.TABLE_KEY + " asc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            compteBanque = new CompteBanque();

            compteBanque.setId(res.getLong(res.getColumnIndex(CompteBanqueHelper.TABLE_KEY)));
            compteBanque.setId_externe(res.getLong(res.getColumnIndex(CompteBanqueHelper.ID_EXTERNE)));
            compteBanque.setUtilisateur_id(res.getLong(res.getColumnIndex(CompteBanqueHelper.UTILISATEUR_ID)));
            compteBanque.setLibelle(res.getString(res.getColumnIndex(CompteBanqueHelper.LIBELLE)));
            compteBanque.setCode(res.getString(res.getColumnIndex(CompteBanqueHelper.CODE)));
            compteBanque.setEtat(res.getInt(res.getColumnIndex(CompteBanqueHelper.ETAT)));
            compteBanque.setCartebanque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CARTEBANK)));
            compteBanque.setCheque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CHEQUE)));
            compteBanque.setSolde(res.getDouble(res.getColumnIndex(CompteBanqueHelper.SOLDE)));

            compteBanques.add(compteBanque) ;
            res.moveToNext();
        }
        res.close();
        return compteBanques;
    }


    public ArrayList<CompteBanque> getNonSyncInterval() {
        ArrayList<CompteBanque> compteBanques = new ArrayList<CompteBanque>();

        CompteBanque compteBanque = null ;
        Cursor res =  mDb.rawQuery( "select * from "+ CompteBanqueHelper.TABLE_NAME+ " where "+ CompteBanqueHelper.ETAT + " < 2 ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            compteBanque = new CompteBanque();

            compteBanque.setId(res.getLong(res.getColumnIndex(CompteBanqueHelper.TABLE_KEY)));
            compteBanque.setId_externe(res.getLong(res.getColumnIndex(CompteBanqueHelper.ID_EXTERNE)));
            compteBanque.setUtilisateur_id(res.getLong(res.getColumnIndex(CompteBanqueHelper.UTILISATEUR_ID)));
            compteBanque.setLibelle(res.getString(res.getColumnIndex(CompteBanqueHelper.LIBELLE)));
            compteBanque.setCode(res.getString(res.getColumnIndex(CompteBanqueHelper.CODE)));
            compteBanque.setEtat(res.getInt(res.getColumnIndex(CompteBanqueHelper.ETAT)));
            compteBanque.setCartebanque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CARTEBANK)));
            compteBanque.setCheque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CHEQUE)));
            compteBanque.setSolde(res.getDouble(res.getColumnIndex(CompteBanqueHelper.SOLDE)));

            compteBanques.add(compteBanque) ;
            res.moveToNext();
        }
        res.close();
        return compteBanques;
    }


    public ArrayList<CompteBanque> getCheque() {
        ArrayList<CompteBanque> compteBanques = new ArrayList<CompteBanque>();

        CompteBanque compteBanque = null ;
        Cursor res =  mDb.rawQuery( "select * from "+ CompteBanqueHelper.TABLE_NAME+ " order by "+ CompteBanqueHelper.LIBELLE + " asc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            compteBanque = new CompteBanque();

            compteBanque.setId(res.getLong(res.getColumnIndex(CompteBanqueHelper.TABLE_KEY)));
            compteBanque.setId_externe(res.getLong(res.getColumnIndex(CompteBanqueHelper.ID_EXTERNE)));
            compteBanque.setUtilisateur_id(res.getLong(res.getColumnIndex(CompteBanqueHelper.UTILISATEUR_ID)));
            compteBanque.setLibelle(res.getString(res.getColumnIndex(CompteBanqueHelper.LIBELLE)));
            compteBanque.setCode(res.getString(res.getColumnIndex(CompteBanqueHelper.CODE)));
            compteBanque.setEtat(res.getInt(res.getColumnIndex(CompteBanqueHelper.ETAT)));
            compteBanque.setCartebanque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CARTEBANK)));
            compteBanque.setCheque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CHEQUE)));
            compteBanque.setSolde(res.getDouble(res.getColumnIndex(CompteBanqueHelper.SOLDE)));


            if (compteBanque.getCheque()==1) compteBanques.add(compteBanque) ;
            res.moveToNext();
        }
        res.close();
        return compteBanques;
    }

    public ArrayList<CompteBanque> getCarteBanque() {
        ArrayList<CompteBanque> compteBanques = new ArrayList<CompteBanque>();

        CompteBanque compteBanque = null ;
        Cursor res =  mDb.rawQuery( "select * from "+ CompteBanqueHelper.TABLE_NAME + " order by "+ CompteBanqueHelper.LIBELLE + " asc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            compteBanque = new CompteBanque();

            compteBanque.setId(res.getLong(res.getColumnIndex(CompteBanqueHelper.TABLE_KEY)));
            compteBanque.setId_externe(res.getLong(res.getColumnIndex(CompteBanqueHelper.ID_EXTERNE)));
            compteBanque.setUtilisateur_id(res.getLong(res.getColumnIndex(CompteBanqueHelper.UTILISATEUR_ID)));
            compteBanque.setLibelle(res.getString(res.getColumnIndex(CompteBanqueHelper.LIBELLE)));
            compteBanque.setCode(res.getString(res.getColumnIndex(CompteBanqueHelper.CODE)));
            compteBanque.setEtat(res.getInt(res.getColumnIndex(CompteBanqueHelper.ETAT)));
            compteBanque.setCartebanque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CARTEBANK)));
            compteBanque.setCheque(res.getInt(res.getColumnIndex(CompteBanqueHelper.CHEQUE)));
            compteBanque.setSolde(res.getDouble(res.getColumnIndex(CompteBanqueHelper.SOLDE)));

            Log.e("CB : " + compteBanque.getLibelle() , String.valueOf(compteBanque.getCartebanque())) ;
            if (compteBanque.getCartebanque()==1) compteBanques.add(compteBanque) ;
            res.moveToNext();
        }
        res.close();
        return compteBanques;
    }

}
