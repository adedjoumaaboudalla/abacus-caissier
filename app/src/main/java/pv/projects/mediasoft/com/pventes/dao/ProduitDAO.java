package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.database.ProduitHelper;
import pv.projects.mediasoft.com.pventes.model.Produit;
import pv.projects.mediasoft.com.pventes.utiles.Utiles;


/**
 * Created by Mayi on 05/10/2015.
 */
public class ProduitDAO extends DAOBase implements Crud<Produit> {

    public static String ACHAT = "ACHAT" ;
    public static String VENTE = "VENTE" ;
    public static String ACHAT_VENTE = "ACHAT_VENTE" ;

    public ProduitDAO(Context pContext) {
        super(pContext);
        MouvementHelper.getHelper(pContext, DATABASE, VERSION) ;
        this.mHandler = ProduitHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(Produit produit) {

        ContentValues contentValues = new ContentValues();

        produit = genererId(produit) ;
        contentValues.put(ProduitHelper.TABLE_KEY, produit.getId());
        contentValues.put(ProduitHelper.ID_EXTERNE, produit.getId_externe());
        contentValues.put(ProduitHelper.ETAT, produit.getEtat());
        contentValues.put(ProduitHelper.MODIFIABLE, produit.getModifiable());
        contentValues.put(ProduitHelper.LIBELLE, produit.getLibelle());
        contentValues.put(ProduitHelper.CODE, produit.getCode());
        contentValues.put(ProduitHelper.AFFICHABLE, produit.getAffichable());
        contentValues.put(ProduitHelper.IMAGE, produit.getImage());
        contentValues.put(ProduitHelper.UNITE, produit.getUnite());
        contentValues.put(ProduitHelper.QUANTITE, produit.getQuantite());
        contentValues.put(ProduitHelper.PRIXACHAT, produit.getPrixA());
        contentValues.put(ProduitHelper.PRIXVENTE, produit.getPrixV());
        contentValues.put(ProduitHelper.CATEGORIE_PRODUIT, produit.getCategorie_id());
        contentValues.put(ProduitHelper.UTILISATEUR_ID, produit.getUtilisateur_id());
        contentValues.put(ProduitHelper.CREATED_AT, formatter.format(produit.getCreated_at()));


        long l = mDb.insert(ProduitHelper.TABLE_NAME, null, contentValues);


        Log.e("ID PRODUIT", String.valueOf(l)) ;

        produit.setId(l);
        /*
        if (produit.getId_externe()==0){
            produit.setId_externe(l);
            update(produit) ;
        }
        */

        return l;
    }

    private Produit genererId(Produit produit) {
        if (getAll().size()==0) produit.setId(1);
        else{
            long id = getLast().getId() ;
            id++ ;
            while (getOneByIdExterne(id)!=null) id++ ;
            produit.setId(id);
        }
        return produit;
    }


    public boolean addAll(ArrayList<Produit>  produits) {

        Produit produit = null ;
        if (produits.size()>0) clean() ;
        for (int i = 0; i < produits.size(); ++i){
            produit = produits.get(i) ;
            ContentValues contentValues = new ContentValues();
            contentValues.put(ProduitHelper.ID_EXTERNE, produit.getId());
            contentValues.put(ProduitHelper.ETAT, 2);
            contentValues.put(ProduitHelper.MODIFIABLE, produit.getModifiable());
            contentValues.put(ProduitHelper.LIBELLE, produit.getLibelle());
            contentValues.put(ProduitHelper.CODE, produit.getCode());
            contentValues.put(ProduitHelper.AFFICHABLE, produit.getAffichable());
            contentValues.put(ProduitHelper.IMAGE, produit.getImage());
            contentValues.put(ProduitHelper.UNITE, produit.getUnite());
            contentValues.put(ProduitHelper.QUANTITE, produit.getQuantite());
            contentValues.put(ProduitHelper.PRIXACHAT, produit.getPrixA());
            contentValues.put(ProduitHelper.PRIXVENTE, produit.getPrixV());
            contentValues.put(ProduitHelper.CATEGORIE_PRODUIT, produit.getCategorie_id());
            contentValues.put(ProduitHelper.UTILISATEUR_ID, produit.getUtilisateur_id());
            contentValues.put(ProduitHelper.CREATED_AT, formatter.format(produit.getCreated_at()));

            mDb.insert(ProduitHelper.TABLE_NAME, null, contentValues);

        }


        return true;
    }

    @Override
    public int delete(long id) {
        Produit p = getOne(id) ;
        if (p.getImage()!=null){
            File f = Utiles.getExtFile(mContext,p.getImage(), Utiles.PV_PRODUIT_IMAGE_DIR) ;
            if (f.exists()) f.delete() ;
        }
        return  mDb.delete(ProduitHelper.TABLE_NAME, ProduitHelper.TABLE_KEY + " = ?",new String[] { Long.toString(id)});
    }

    public int clean() {
        return  mDb.delete(ProduitHelper.TABLE_NAME,null,null);
    }

    @Override
    public int update(Produit produit) {


        ContentValues contentValues = new ContentValues();
        contentValues.put(ProduitHelper.TABLE_KEY, produit.getId());
        contentValues.put(ProduitHelper.ID_EXTERNE, produit.getId_externe());
        contentValues.put(ProduitHelper.ETAT, produit.getEtat());
        contentValues.put(ProduitHelper.MODIFIABLE, produit.getModifiable());
        contentValues.put(ProduitHelper.LIBELLE, produit.getLibelle());
        contentValues.put(ProduitHelper.CODE, produit.getCode());
        contentValues.put(ProduitHelper.AFFICHABLE, produit.getAffichable());
        contentValues.put(ProduitHelper.IMAGE, produit.getImage());
        contentValues.put(ProduitHelper.UNITE, produit.getUnite());
        contentValues.put(ProduitHelper.QUANTITE, produit.getQuantite());
        contentValues.put(ProduitHelper.PRIXACHAT, produit.getPrixA());
        contentValues.put(ProduitHelper.PRIXVENTE, produit.getPrixV());
        contentValues.put(ProduitHelper.CATEGORIE_PRODUIT, produit.getCategorie_id());
        contentValues.put(ProduitHelper.UTILISATEUR_ID, produit.getUtilisateur_id());
        contentValues.put(ProduitHelper.CREATED_AT, formatter.format(produit.getCreated_at()));

        int i = mDb.update(ProduitHelper.TABLE_NAME, contentValues, ProduitHelper.TABLE_KEY + " = ?", new String[] { Long.toString(produit.getId()) } );

        return i;
    }

    @Override
    public Produit getOne(long id) {
        Produit produit = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ ProduitHelper.TABLE_NAME +" where " + ProduitHelper.TABLE_KEY + "="+id , null );

        if (res.moveToFirst()){
            produit = new Produit();
            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
             } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        res.close();

        return produit ;
    }


    public Produit getOneByCode(String code) {
        Produit produit = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ ProduitHelper.TABLE_NAME +" where " + ProduitHelper.CODE + "='"+code+"'" , null );

        if (res.moveToFirst()){
            produit = new Produit();
            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
             } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        res.close();

        return produit ;
    }

    public Produit getOneByLibelle(String libelle) {
        Produit produit = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ ProduitHelper.TABLE_NAME +" where " + ProduitHelper.LIBELLE + " = '"+libelle+"'" , null );

        if (res.moveToFirst()){
            produit = new Produit();
            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
             } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        res.close();
        return produit ;
    }


    public Produit getOneByIdExterne(long id) {
        Produit produit = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ ProduitHelper.TABLE_NAME +" where " + ProduitHelper.ID_EXTERNE + "="+id , null );

        if (res.moveToFirst()){
            produit = new Produit();
            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
             } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        res.close();
        return produit ;
    }


    public Produit getOne(long id,int etat) {
        Produit produit = null ;

        Log.e("QUERY", "select * from "+ ProduitHelper.TABLE_NAME +" where "+ ProduitHelper.ID_EXTERNE +" = " +id+" AND " + ProduitHelper.ETAT  +" = "+etat) ;

        Cursor res =  mDb.rawQuery( "select * from "+ ProduitHelper.TABLE_NAME +" where "+ ProduitHelper.ID_EXTERNE +" = " +id+" AND " + ProduitHelper.ETAT  +" = "+etat , null );

        if (res.moveToFirst()){
            produit = new Produit();
            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        res.close();
        return produit ;
    }


    public Produit getLast() {
        Produit produit = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ ProduitHelper.TABLE_NAME +" order by "  + ProduitHelper.TABLE_KEY  +  " desc" , null );

        if (res.moveToFirst()){
            produit = new Produit();
            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        res.close();
        return produit ;
    }

    @Override
    public ArrayList<Produit> getAll() {
        ArrayList<Produit> Produits = new ArrayList<Produit>();
        Produit produit = null ;

        Log.i("DEBUGGGGGGGG", "select * from "+ProduitHelper.TABLE_NAME +" WHERE " + ProduitHelper.ETAT + " = 2 ORDER BY " + ProduitHelper.TABLE_KEY + " DESC ") ;

        //Cursor res =  mDb.rawQuery( "select * from "+ProduitHelper.TABLE_NAME + " WHERE " + ProduitHelper.ETAT + " = 2", null);
        Cursor res =  mDb.rawQuery( "select * from "+ProduitHelper.TABLE_NAME + " ORDER BY " + ProduitHelper.LIBELLE , null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            produit = new Produit();

            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Produits.add(produit);
            res.moveToNext();
        }
        res.close();
        return Produits;
    }



    public ArrayList<Produit> getAll(String affichable) {
        ArrayList<Produit> produits = new ArrayList<Produit>();
        Produit produit = null ;
        String sql = "select * from "+ProduitHelper.TABLE_NAME + " ORDER BY " + ProduitHelper.LIBELLE ;

        if (affichable!=null) sql = "select * from "+ProduitHelper.TABLE_NAME + " WHERE " + ProduitHelper.AFFICHABLE + " = '" + affichable + "' OR " + ProduitHelper.AFFICHABLE + " = '" + ACHAT_VENTE + "'  ORDER BY " + ProduitHelper.LIBELLE ;
        Cursor res =  mDb.rawQuery(sql, null);
        res.moveToFirst();

        Log.e("SQL",sql) ;
        while(res.isAfterLast() == false){
            produit = new Produit();

            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.e("Affichable","'"+produit.getAffichable()+"'") ;

            produits.add(produit);
            res.moveToNext();
        }
        res.close();
        return produits;
    }


    public ArrayList<Produit> getRecurent() {
        ArrayList<Produit> produits = new ArrayList<Produit>();
        Produit produit = null ;

        //Cursor res =  mDb.rawQuery( "select * from "+ProduitHelper.TABLE_NAME + " WHERE " + ProduitHelper.ETAT + " = 2", null);
        Cursor res =  mDb.rawQuery( "select * from "+ProduitHelper.TABLE_NAME  + " ORDER BY " + ProduitHelper.LIBELLE , null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            produit = new Produit();

            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            produits.add(produit);
            res.moveToNext();
        }
        res.close();
        return produits;
    }

    public ArrayList<Produit> getInterval(Date dateDebut, Date dateFin) {
        ArrayList<Produit> produits = new ArrayList<Produit>();
        Produit produit = null ;

        if (dateDebut==null) dateDebut=new Date("01/01/2000") ;
        if (dateFin==null) dateFin=new Date() ;

        Log.e("DEBUG", "select * from "+ProduitHelper.TABLE_NAME + " WHERE "+ProduitHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " + ProduitHelper.LIBELLE + " DESC " ) ;

        Cursor res =  mDb.rawQuery( "select * from "+ProduitHelper.TABLE_NAME + " WHERE " + ProduitHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " + ProduitHelper.LIBELLE + " DESC ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            produit = new Produit();

            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            produits.add(produit);
            res.moveToNext();
        }

        res.close();
        Log.e("SIZE", String.valueOf(produits.size())) ;
        return produits;
    }



    public ArrayList<Produit> getNew(Date dateDebut, Date dateFin) {
        ArrayList<Produit> produits = new ArrayList<Produit>();
        Produit produit = null ;

        if (dateDebut==null) dateDebut=new Date("01/01/2015") ;
        if (dateFin==null) dateFin=new Date() ;

        Log.i("DEBUG", "select * from "+ProduitHelper.TABLE_NAME + " WHERE "+ProduitHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " + ProduitHelper.LIBELLE + " DESC " ) ;

        Cursor res =  mDb.rawQuery( "select * from "+ProduitHelper.TABLE_NAME + " WHERE " + ProduitHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " + ProduitHelper.LIBELLE + " DESC ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            produit = new Produit();

            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            res.moveToNext();
        }
        Log.e("SIZE", String.valueOf(produits.size())) ;
        res.close();
        return produits;
    }

    public ArrayList<Produit> getNonSyncInterval(Date dateDebut, Date dateFin) {
        ArrayList<Produit> produits = new ArrayList<Produit>();
        Produit produit = null ;

        if (dateDebut==null) dateDebut=new Date("01/01/2015");
        if (dateFin==null) dateFin=new Date() ;

        Log.e("DEBUGGGGGGGG", "select * from "+ProduitHelper.TABLE_NAME +" WHERE " + ProduitHelper.ETAT + " <> 2 AND " +ProduitHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " + ProduitHelper.LIBELLE + " DESC ") ;

        Cursor res =  mDb.rawQuery( "select * from "+ProduitHelper.TABLE_NAME +" WHERE " + ProduitHelper.ETAT + " < 2  AND " +ProduitHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " + ProduitHelper.LIBELLE + " DESC ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            produit = new Produit();

            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            };

            produits.add(produit);
            res.moveToNext();
        }
        res.close();
        Log.e("SIZE PROD NON", String.valueOf(produits.size()));
        return produits;
    }

    public ArrayList<Produit> getRecurrentInterval(Date dateDebut, Date dateFin) {
        ArrayList<Produit> produits = new ArrayList<Produit>();
        Produit produit = null ;

        if (dateDebut==null) dateDebut=new Date("01/01/2015");
        if (dateFin==null) dateFin=new Date() ;

        Cursor res =  mDb.rawQuery("SELECT * FROM " + ProduitHelper.TABLE_NAME + " WHERE " + ProduitHelper.ID_EXTERNE + " IN (SELECT " + MouvementHelper.PRODUIT_ID + " FROM " + MouvementHelper.TABLE_NAME + "  GROUP BY " + MouvementHelper.PRODUIT_ID + " ORDER BY  COUNT(" + MouvementHelper.PRODUIT_ID + ") DESC) LIMIT 0,30", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            produit = new Produit() ;

            produit.setId(res.getLong(res.getColumnIndex(ProduitHelper.TABLE_KEY)));
            produit.setId_externe(res.getLong(res.getColumnIndex(ProduitHelper.ID_EXTERNE)));
            produit.setEtat(res.getInt(res.getColumnIndex(ProduitHelper.ETAT)));
            produit.setModifiable(res.getInt(res.getColumnIndex(ProduitHelper.MODIFIABLE)));
            produit.setPrixA(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXACHAT)));
            produit.setPrixV(res.getDouble(res.getColumnIndex(ProduitHelper.PRIXVENTE)));
            produit.setUtilisateur(res.getLong(res.getColumnIndex(ProduitHelper.UTILISATEUR_ID)));
            produit.setCategorie_id(res.getLong(res.getColumnIndex(ProduitHelper.CATEGORIE_PRODUIT)));
            produit.setLibelle(res.getString(res.getColumnIndex(ProduitHelper.LIBELLE)));
            produit.setCode(res.getString(res.getColumnIndex(ProduitHelper.CODE)));
            produit.setAffichable(res.getString(res.getColumnIndex(ProduitHelper.AFFICHABLE)));
            produit.setImage(res.getString(res.getColumnIndex(ProduitHelper.IMAGE)));
            produit.setUnite(res.getString(res.getColumnIndex(ProduitHelper.UNITE)));
            produit.setQuantite(res.getDouble(res.getColumnIndex(ProduitHelper.QUANTITE)));
            try {
                produit.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ProduitHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace() ;
            }
            produits.add(produit);
            res.moveToNext();
        }

        res.close();
        return produits;
    }
}
