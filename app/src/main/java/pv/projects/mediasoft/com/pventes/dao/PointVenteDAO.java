package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import pv.projects.mediasoft.com.pventes.database.CaisseHelper;
import pv.projects.mediasoft.com.pventes.database.PartenaireHelper;
import pv.projects.mediasoft.com.pventes.database.PointVenteHelper;
import pv.projects.mediasoft.com.pventes.model.PointVente;


/**
 * Created by Mayi on 05/10/2015.
 */
public class PointVenteDAO extends DAOBase implements Crud<PointVente>{


    public PointVenteDAO(Context pContext) {
        super(pContext);
        this.mHandler = PointVenteHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(PointVente pointVente) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PointVenteHelper.TABLE_KEY, pointVente.getId());
        contentValues.put(PointVenteHelper.LIBELLE, pointVente.getLibelle());
        contentValues.put(PointVenteHelper.PAYS, pointVente.getPays());
        contentValues.put(PointVenteHelper.VILLE, pointVente.getVille());
        contentValues.put(PointVenteHelper.QUARTIER, pointVente.getQuartier());
        contentValues.put(PointVenteHelper.TEL, pointVente.getTel());
        contentValues.put(PointVenteHelper.UTILISATEUR_ID, pointVente.getUtilisateur());
        if (pointVente.getCreated_at()!=null)contentValues.put(PointVenteHelper.CREATED_AT, formatter.format(pointVente.getCreated_at()));

        //mDb.update(PointVenteHelper.TABLE_NAME, contentValues, "id = ? ", new String[]{Long.toString(pointVente.getId())});

        long l = mDb.insert(PointVenteHelper.TABLE_NAME, null, contentValues);
        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(PointVenteHelper.TABLE_NAME,  PointVenteHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }

    public int clean() {
        return  mDb.delete(PointVenteHelper.TABLE_NAME,  null,null);
    }


    @Override
    public int update(PointVente pointVente) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PointVenteHelper.LIBELLE, pointVente.getLibelle());
        contentValues.put(PointVenteHelper.PAYS, pointVente.getPays());
        contentValues.put(PointVenteHelper.VILLE, pointVente.getVille());
        contentValues.put(PointVenteHelper.QUARTIER, pointVente.getQuartier());
        contentValues.put(PointVenteHelper.TEL, pointVente.getTel());
        contentValues.put(PointVenteHelper.UTILISATEUR_ID, pointVente.getUtilisateur());
        contentValues.put(PointVenteHelper.CREATED_AT, formatter.format(pointVente.getCreated_at()));

        int i = mDb.update(PointVenteHelper.TABLE_NAME, contentValues, PointVenteHelper.TABLE_KEY + " = ? " , new String[]{Long.toString(pointVente.getId())});

        return i;
    }

    @Override
    public PointVente getOne(long id) {
        PointVente pointVente = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ PointVenteHelper.TABLE_NAME +" where " + PointVenteHelper.TABLE_KEY + "="+id+"", null );
        if (res.moveToFirst()){

            pointVente = new PointVente();

            pointVente.setId(res.getLong(res.getColumnIndex(PointVenteHelper.TABLE_KEY)));
            pointVente.setLibelle(res.getString(res.getColumnIndex(PointVenteHelper.LIBELLE)));
            pointVente.setPays(res.getString(res.getColumnIndex(PointVenteHelper.PAYS)));
            pointVente.setVille(res.getString(res.getColumnIndex(PointVenteHelper.VILLE)));
            pointVente.setQuartier(res.getString(res.getColumnIndex(PointVenteHelper.QUARTIER)));
            pointVente.setTel(res.getString(res.getColumnIndex(PointVenteHelper.TEL)));
            pointVente.setUtilisateur_id(res.getInt(res.getColumnIndex(PointVenteHelper.UTILISATEUR_ID)));
            try {
                pointVente.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PointVenteHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
        };

        res.close();
        return pointVente ;
    }

    public PointVente getLast() {
        PointVente pointVente = null ;

        Log.i("DEBUG","select * from "+ PointVenteHelper.TABLE_NAME +" ORDER BY " + PointVenteHelper.TABLE_KEY + " desc") ;
        Cursor res =  mDb.rawQuery( "select * from "+ PointVenteHelper.TABLE_NAME +" ORDER BY " + PointVenteHelper.TABLE_KEY + " desc", null );

        if (res.moveToFirst()){
            pointVente = new PointVente();

            pointVente.setId(res.getLong(res.getColumnIndex(PointVenteHelper.TABLE_KEY)));
            pointVente.setLibelle(res.getString(res.getColumnIndex(PointVenteHelper.LIBELLE)));
            pointVente.setPays(res.getString(res.getColumnIndex(PointVenteHelper.PAYS)));
            pointVente.setVille(res.getString(res.getColumnIndex(PointVenteHelper.VILLE)));
            pointVente.setQuartier(res.getString(res.getColumnIndex(PointVenteHelper.QUARTIER)));
            pointVente.setTel(res.getString(res.getColumnIndex(PointVenteHelper.TEL)));
            pointVente.setUtilisateur_id(res.getInt(res.getColumnIndex(PointVenteHelper.UTILISATEUR_ID)));
            try {
                pointVente.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PointVenteHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
        };

        res.close();
        return pointVente ;
    }

    @Override
    public ArrayList<PointVente> getAll() {
        ArrayList<PointVente> pointVentes = new ArrayList<PointVente>();
        PointVente pointVente = null ;

        Cursor res =  mDb.rawQuery( "select * from "+PointVenteHelper.TABLE_NAME, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            pointVente = new PointVente();

            pointVente.setId(res.getLong(res.getColumnIndex(PointVenteHelper.TABLE_KEY)));
            pointVente.setLibelle(res.getString(res.getColumnIndex(PointVenteHelper.LIBELLE)));
            pointVente.setPays(res.getString(res.getColumnIndex(PointVenteHelper.PAYS)));
            pointVente.setVille(res.getString(res.getColumnIndex(PointVenteHelper.VILLE)));
            pointVente.setQuartier(res.getString(res.getColumnIndex(PointVenteHelper.QUARTIER)));
            pointVente.setTel(res.getString(res.getColumnIndex(PointVenteHelper.TEL)));
            pointVente.setUtilisateur_id(res.getInt(res.getColumnIndex(PointVenteHelper.UTILISATEUR_ID)));
            try {
                pointVente.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PointVenteHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
            pointVentes.add(pointVente);
            res.moveToNext();
        }
        res.close();
        return pointVentes;
    }
}
