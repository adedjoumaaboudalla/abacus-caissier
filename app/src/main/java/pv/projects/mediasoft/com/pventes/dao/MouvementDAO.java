package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.database.ModePayementHelper;
import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Produit;


/**
 * Created by Mayi on 21/04/2011.
 */
public class MouvementDAO extends DAOBase implements Crud<Mouvement> {
    
    
    public MouvementDAO(Context pContext) {
        super(pContext);
        this.mHandler = MouvementHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }


    @Override
    public long add(Mouvement mouvement) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(MouvementHelper.PRIXV, mouvement.getPrixV());
        contentValues.put(MouvementHelper.QUANTITE, mouvement.getQuantite());
        contentValues.put(MouvementHelper.RESTANT, mouvement.getRestant());
        contentValues.put(MouvementHelper.CMUP, mouvement.getCmup());
        contentValues.put(MouvementHelper.OPERATION_ID, mouvement.getOperation_id());
        contentValues.put(MouvementHelper.PRIXA, mouvement.getPrixA());
        contentValues.put(MouvementHelper.PRODUIT, mouvement.getProduit());
        contentValues.put(MouvementHelper.DESCRIPTION, mouvement.getDescription());
        contentValues.put(MouvementHelper.PRODUIT_ETAT, mouvement.getProduit_etat());
        contentValues.put(MouvementHelper.PRODUIT_ID, mouvement.getProduit_id());
        contentValues.put(MouvementHelper.ETAT, mouvement.getEtat());
        contentValues.put(MouvementHelper.ENTREE, mouvement.getEntree());
        contentValues.put(MouvementHelper.CREATED_AT, formatter.format(mouvement.getCreated_at()));



        long  l =  0 ;
        Mouvement mv = getOne(mouvement.getProduit_id(), mouvement.getOperation_id());
        if (mv==null) l = mDb.insert(MouvementHelper.TABLE_NAME, null, contentValues);
        else l = mv.getId() ;

        Log.e("MOUV ", mouvement.getProduit() + " | " +mouvement.getQuantite() + " | " + mouvement.getRestant()  ) ;
        Log.e("MOUV operation",formatter.format(mouvement.getOperation_id())) ;
        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(MouvementHelper.TABLE_NAME, MouvementHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }

    public int clean() {
        return  mDb.delete(MouvementHelper.TABLE_NAME, null,null);
    }


    public int deletePV(long id) {
        return  mDb.delete(MouvementHelper.TABLE_NAME, MouvementHelper.OPERATION_ID + " = ? ",new String[] { Long.toString(id)});
    }

    @Override
    public int update(Mouvement mouvement) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(MouvementHelper.TABLE_KEY, mouvement.getId());
        contentValues.put(MouvementHelper.PRIXV, mouvement.getPrixV());
        contentValues.put(MouvementHelper.QUANTITE, mouvement.getQuantite());
        contentValues.put(MouvementHelper.RESTANT, mouvement.getRestant());
        contentValues.put(MouvementHelper.CMUP, mouvement.getCmup());
        contentValues.put(MouvementHelper.OPERATION_ID, mouvement.getOperation_id());
        contentValues.put(MouvementHelper.PRIXA, mouvement.getPrixA());
        contentValues.put(MouvementHelper.PRODUIT, mouvement.getProduit());
        contentValues.put(MouvementHelper.DESCRIPTION, mouvement.getDescription());
        contentValues.put(MouvementHelper.PRODUIT_ETAT, mouvement.getProduit_etat());
        contentValues.put(MouvementHelper.PRODUIT_ID, mouvement.getProduit_id());
        contentValues.put(MouvementHelper.ETAT, mouvement.getEtat());
        contentValues.put(MouvementHelper.ENTREE, mouvement.getEntree());
        contentValues.put(MouvementHelper.CREATED_AT, formatter.format(mouvement.getCreated_at()));

        int i = mDb.update(MouvementHelper.TABLE_NAME, contentValues, MouvementHelper.TABLE_KEY + " = ? ", new String[]{Long.toString(mouvement.getId())});

        Log.e("MOUV UPDATE", String.valueOf(i)) ;
        return i;
    }

    public boolean update(Produit produit) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MouvementHelper.PRODUIT_ID, produit.getId_externe());
        contentValues.put(MouvementHelper.PRODUIT_ETAT, produit.getEtat());

        int l = mDb.update(MouvementHelper.TABLE_NAME, contentValues, MouvementHelper.PRODUIT_ID + " = ? ", new String[]{Long.toString(produit.getId())});

        Log.e("Mouvement mis a jour", String.valueOf(l)) ;
        return true;
    }

    @Override
    public Mouvement getOne(long id) {
        Mouvement mouvement = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ MouvementHelper.TABLE_NAME +" where " + MouvementHelper.TABLE_KEY +  "="+id+"", null );

        if (res.moveToFirst()){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getDouble(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        };

        res.close();

        return mouvement ;
    }


    public Mouvement getOne(long produit, long operation) {
        Mouvement mouvement = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ MouvementHelper.TABLE_NAME +" where " + MouvementHelper.PRODUIT_ID +  "="+produit+" and " + MouvementHelper.OPERATION_ID + " = " + operation, null );

        if (res.moveToFirst()){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getDouble(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        };

        res.close();

        return mouvement ;
    }



    public Mouvement getLastbyProduct(long id) {
        Mouvement mouvement = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ MouvementHelper.TABLE_NAME +" where " + MouvementHelper.PRODUIT_ID +  "="+id+" order by " + MouvementHelper.TABLE_KEY + " desc", null );

        if (res.moveToFirst()){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getInt(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        };

        res.close();
        return mouvement ;
    }

    @Override
    public ArrayList<Mouvement> getAll() {
        ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
        Mouvement mouvement = null ;

        Cursor res =  mDb.rawQuery( "select * from "+MouvementHelper.TABLE_NAME, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getInt(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mouvements.add(mouvement);
            res.moveToNext();
        }
        res.close();
        return mouvements;
    }

    public ArrayList<Mouvement> getMany(long id) {
        ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
        Mouvement mouvement = null ;

        Log.e("DEBUG","select * from "+MouvementHelper.TABLE_NAME + " Where " + MouvementHelper.OPERATION_ID + " = " + id) ;
        Cursor res =  mDb.rawQuery( "select * from "+MouvementHelper.TABLE_NAME + " Where " + MouvementHelper.OPERATION_ID + " = " + id, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getInt(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mouvements.add(mouvement);
            res.moveToNext();
        }
        res.close();
        return mouvements;
    }



    public ArrayList<Mouvement> getManyByProduct(long id) {
        ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
        Mouvement mouvement = null ;

        Log.e("DEBUG","select * from "+MouvementHelper.TABLE_NAME + " Where " + MouvementHelper.PRODUIT_ID + " = " + id) ;
        Cursor res =  mDb.rawQuery( "select * from "+MouvementHelper.TABLE_NAME + " Where " + MouvementHelper.PRODUIT_ID + " = " + id, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getInt(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mouvements.add(mouvement);
            res.moveToNext();
        }
        res.close();
        return mouvements;
    }



    public ArrayList<Mouvement> getManyByProductInterval(long id,Date datedebut,Date datefin) {


        if (datedebut==null) datedebut=new Date("01/01/2015");
        if (datefin==null) datefin=new Date() ;


        ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
        Mouvement mouvement = null ;

        Log.e("DEBUG","select * from "+MouvementHelper.TABLE_NAME + " Where " + MouvementHelper.PRODUIT_ID + " = " + id ) ;
        Cursor res =  mDb.rawQuery( "select * from "+MouvementHelper.TABLE_NAME + " Where " + MouvementHelper.PRODUIT_ID + " = " + id + " and "  + MouvementHelper.CREATED_AT +  " between '" + formatter2.format(datedebut) + "' and '" + formatter2.format(datefin) +  " 23:59:59' ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getInt(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mouvements.add(mouvement);
            res.moveToNext();
        }
        res.close();
        return mouvements;
    }




    public ArrayList<Mouvement> getManyByInterval(Date datedebut,Date datefin) {


        if (datedebut==null) datedebut=new Date("01/01/2015");
        if (datefin==null) datefin=new Date() ;


        ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
        Mouvement mouvement = null ;

        Log.e("DEBUG","select * from "+MouvementHelper.TABLE_NAME + " Where "  + MouvementHelper.CREATED_AT +  " between '" + formatter2.format(datedebut) + "' and '" + formatter2.format(datefin) +  " 23:59:59' " ) ;
        Cursor res =  mDb.rawQuery( "select * from "+MouvementHelper.TABLE_NAME + " Where "  + MouvementHelper.CREATED_AT +  " between '" + formatter2.format(datedebut) + "' and '" + formatter2.format(datefin) +  " 23:59:59' ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getInt(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mouvements.add(mouvement);
            res.moveToNext();
        }
        res.close();
        return mouvements;
    }


    public ArrayList<Mouvement> getNonSync(long id) {
        ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
        Mouvement mouvement = null ;

        Log.e("DEBUG","select * from "+MouvementHelper.TABLE_NAME + " Where  " + MouvementHelper.ETAT + " = 0 and " + MouvementHelper.OPERATION_ID + " = " + id) ;
        Cursor res =  mDb.rawQuery( "select * from "+MouvementHelper.TABLE_NAME + " Where  " + MouvementHelper.ETAT + " = 0 and " + MouvementHelper.OPERATION_ID + " = " + id, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getInt(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mouvements.add(mouvement);
            res.moveToNext();
        }
        res.close();
        return mouvements;
    }

    public ArrayList<Mouvement> getNonSync() {
        ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
        Mouvement mouvement = null ;

        Log.e("DEBUG","select * from "+MouvementHelper.TABLE_NAME + " Where  " + MouvementHelper.ETAT + " < 2 ") ;
        Cursor res =  mDb.rawQuery("select * from "+MouvementHelper.TABLE_NAME + " Where  " + MouvementHelper.ETAT + " < 2 ORDER BY " + MouvementHelper.TABLE_KEY + " ASC", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mouvement = new Mouvement();

            mouvement.setId(res.getLong(res.getColumnIndex(MouvementHelper.TABLE_KEY)));
            mouvement.setPrixV(res.getInt(res.getColumnIndex(MouvementHelper.PRIXV)));
            mouvement.setPrixA(res.getInt(res.getColumnIndex(MouvementHelper.PRIXA)));
            mouvement.setEntree(res.getInt(res.getColumnIndex(MouvementHelper.ENTREE)));
            mouvement.setQuantite(res.getDouble(res.getColumnIndex(MouvementHelper.QUANTITE)));
            mouvement.setRestant(res.getDouble(res.getColumnIndex(MouvementHelper.RESTANT)));
            mouvement.setCmup(res.getDouble(res.getColumnIndex(MouvementHelper.CMUP)));
            mouvement.setProduit_id(res.getLong(res.getColumnIndex(MouvementHelper.PRODUIT_ID)));
            mouvement.setOperation_id(res.getLong(res.getColumnIndex(MouvementHelper.OPERATION_ID)));
            mouvement.setProduit_etat(res.getInt(res.getColumnIndex(MouvementHelper.PRODUIT_ETAT)));
            mouvement.setProduit(res.getString(res.getColumnIndex(MouvementHelper.PRODUIT)));
            mouvement.setDescription(res.getString(res.getColumnIndex(MouvementHelper.DESCRIPTION)));
            mouvement.setEtat(res.getInt(res.getColumnIndex(MouvementHelper.ETAT)));

            try {
                mouvement.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(MouvementHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e("ETAT",String.valueOf(mouvement.getEtat())) ;
            mouvements.add(mouvement);
            res.moveToNext();
        }
        res.close();
        Log.e("SIZE MOOV NON", String.valueOf(mouvements.size()));
        return mouvements;
    }

    public int updateMany(Operation operation) {
        ContentValues contentValues = new ContentValues();

        if (operation.getEtat()<2)contentValues.put(MouvementHelper.OPERATION_ID, operation.getId());
        else contentValues.put(MouvementHelper.OPERATION_ID, operation.getId_externe());

        contentValues.put(MouvementHelper.ANNULER, operation.getAnnuler());
        contentValues.put(MouvementHelper.ETAT, 0);

       int i = mDb.update(MouvementHelper.TABLE_NAME, contentValues, MouvementHelper.OPERATION_ID + " = ?", new String[] {Long.toString(operation.getId())});
        Log.e("UPDATE",String.valueOf(i)) ;
        return i ;
    }

    public int updateMany(Produit produit) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MouvementHelper.PRODUIT_ID, produit.getId_externe());

       int i = mDb.update(MouvementHelper.TABLE_NAME, contentValues, MouvementHelper.PRODUIT_ID + " = ? ", new String[] {Long.toString(produit.getId())});
        Log.e("UPDATE",String.valueOf(i)) ;
        return i ;
    }

}
