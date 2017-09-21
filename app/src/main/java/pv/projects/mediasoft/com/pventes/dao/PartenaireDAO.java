package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.database.BilletHelper;
import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.database.PartenaireHelper;
import pv.projects.mediasoft.com.pventes.model.Partenaire;

/**
 * Created by Mayi on 30/10/2015.
 */
public class PartenaireDAO extends DAOBase implements Crud<Partenaire>{



    public PartenaireDAO(Context pContext) {
        super(pContext);
        this.mHandler = PartenaireHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(Partenaire partenaire) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PartenaireHelper.ID_EXTERNE, partenaire.getId_externe());
        contentValues.put(PartenaireHelper.NOM, partenaire.getNom());
        contentValues.put(PartenaireHelper.PRENOM, partenaire.getPrenom());
        contentValues.put(PartenaireHelper.RAISONSOCIAL, partenaire.getRaisonsocial());
        contentValues.put(PartenaireHelper.SEXE, partenaire.getSexe());
        contentValues.put(PartenaireHelper.EMAIL, partenaire.getEmail());
        contentValues.put(PartenaireHelper.TYPEPERSONNE, partenaire.getTypepersonne());
        contentValues.put(PartenaireHelper.TYPEPARTENAIRE, partenaire.getTypepartenaire());
        contentValues.put(PartenaireHelper.ETAT, partenaire.getEtat());
        contentValues.put(PartenaireHelper.CONTACT, partenaire.getContact());
        contentValues.put(PartenaireHelper.ADRESSE, partenaire.getAdresse());
        contentValues.put(PartenaireHelper.UTILISATEUR_iD, partenaire.getUtilisateur_id());
        contentValues.put(PartenaireHelper.POINTVENTE_iD, partenaire.getPointvente_id());
        contentValues.put(PartenaireHelper.CREATED_AT, formatter.format(partenaire.getCreated_at()));

        long  l = mDb.insert(PartenaireHelper.TABLE_NAME, null, contentValues);

        /*
        if (partenaire.getId_externe()==0){
            partenaire.setId_externe(l);
            update(partenaire) ;
        }
        */
        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(PartenaireHelper.TABLE_NAME,  PartenaireHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }


    public int clean() {
        return  mDb.delete(PartenaireHelper.TABLE_NAME, null,null);
    }

    @Override
    public int update(Partenaire partenaire) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PartenaireHelper.ID_EXTERNE, partenaire.getId_externe());
        contentValues.put(PartenaireHelper.NOM, partenaire.getNom());
        contentValues.put(PartenaireHelper.PRENOM, partenaire.getPrenom());
        contentValues.put(PartenaireHelper.RAISONSOCIAL, partenaire.getRaisonsocial());
        contentValues.put(PartenaireHelper.SEXE, partenaire.getSexe());
        contentValues.put(PartenaireHelper.EMAIL, partenaire.getEmail());
        contentValues.put(PartenaireHelper.TYPEPERSONNE, partenaire.getTypepersonne());
        contentValues.put(PartenaireHelper.TYPEPARTENAIRE, partenaire.getTypepartenaire());
        contentValues.put(PartenaireHelper.ETAT, partenaire.getEtat());
        contentValues.put(PartenaireHelper.CONTACT, partenaire.getContact());
        contentValues.put(PartenaireHelper.ADRESSE, partenaire.getAdresse());
        contentValues.put(PartenaireHelper.UTILISATEUR_iD, partenaire.getUtilisateur_id());
        contentValues.put(PartenaireHelper.POINTVENTE_iD, partenaire.getPointvente_id());
        contentValues.put(PartenaireHelper.CREATED_AT, formatter.format(partenaire.getCreated_at()));

        int i =mDb.update(PartenaireHelper.TABLE_NAME, contentValues,  PartenaireHelper.TABLE_KEY + " = ? ", new String[] { Long.toString(partenaire.getId()) } );

        return i;
    }

    @Override
    public Partenaire getOne(long id) {
        Partenaire partenaire = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ PartenaireHelper.TABLE_NAME +" where " +PartenaireHelper.TABLE_KEY + "  = "+id+"", null );

        if(res.moveToFirst()){
            partenaire = new Partenaire();

            partenaire.setId(res.getLong(res.getColumnIndex(PartenaireHelper.TABLE_KEY)));
            partenaire.setId_externe(res.getLong(res.getColumnIndex(PartenaireHelper.ID_EXTERNE)));
            partenaire.setNom(res.getString(res.getColumnIndex(PartenaireHelper.NOM)));
            partenaire.setPrenom(res.getString(res.getColumnIndex(PartenaireHelper.PRENOM)));
            partenaire.setAdresse(res.getString(res.getColumnIndex(PartenaireHelper.ADRESSE)));
            partenaire.setContact(res.getString(res.getColumnIndex(PartenaireHelper.CONTACT)));
            partenaire.setSexe(res.getString(res.getColumnIndex(PartenaireHelper.SEXE)));
            partenaire.setEmail(res.getString(res.getColumnIndex(PartenaireHelper.EMAIL)));
            partenaire.setTypepersonne(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPERSONNE)));
            partenaire.setTypepartenaire(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPARTENAIRE)));
            partenaire.setRaisonsocial(res.getString(res.getColumnIndex(PartenaireHelper.RAISONSOCIAL)));
            partenaire.setEtat(res.getInt(res.getColumnIndex(PartenaireHelper.ETAT)));
            partenaire.setUtilisateur_id(res.getLong(res.getColumnIndex(PartenaireHelper.UTILISATEUR_iD)));
            partenaire.setPointvente_id(res.getLong(res.getColumnIndex(PartenaireHelper.POINTVENTE_iD)));

            try {
                partenaire.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

         };

        res.close();
        return partenaire ;
    }

    public Partenaire getLast() {
        Partenaire partenaire = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ PartenaireHelper.TABLE_NAME +" order by " +PartenaireHelper.TABLE_KEY + "  desc", null );

        if(res.moveToFirst()){
            partenaire = new Partenaire();

            partenaire.setId(res.getLong(res.getColumnIndex(PartenaireHelper.TABLE_KEY)));
            partenaire.setId_externe(res.getLong(res.getColumnIndex(PartenaireHelper.ID_EXTERNE)));
            partenaire.setNom(res.getString(res.getColumnIndex(PartenaireHelper.NOM)));
            partenaire.setPrenom(res.getString(res.getColumnIndex(PartenaireHelper.PRENOM)));
            partenaire.setAdresse(res.getString(res.getColumnIndex(PartenaireHelper.ADRESSE)));
            partenaire.setContact(res.getString(res.getColumnIndex(PartenaireHelper.CONTACT)));
            partenaire.setSexe(res.getString(res.getColumnIndex(PartenaireHelper.SEXE)));
            partenaire.setEmail(res.getString(res.getColumnIndex(PartenaireHelper.EMAIL)));
            partenaire.setTypepersonne(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPERSONNE)));
            partenaire.setTypepartenaire(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPARTENAIRE)));
            partenaire.setRaisonsocial(res.getString(res.getColumnIndex(PartenaireHelper.RAISONSOCIAL)));
            partenaire.setEtat(res.getInt(res.getColumnIndex(PartenaireHelper.ETAT)));
            partenaire.setUtilisateur_id(res.getLong(res.getColumnIndex(PartenaireHelper.UTILISATEUR_iD)));
            partenaire.setPointvente_id(res.getLong(res.getColumnIndex(PartenaireHelper.POINTVENTE_iD)));

            try {
                partenaire.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

         };

        res.close();
        return partenaire ;
    }

    @Override
    public ArrayList<Partenaire> getAll() {
        ArrayList<Partenaire> partenaires = new ArrayList<Partenaire>();
        Partenaire partenaire = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ PartenaireHelper.TABLE_NAME + " order by " +PartenaireHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            partenaire = new Partenaire();


            partenaire.setId(res.getLong(res.getColumnIndex(PartenaireHelper.TABLE_KEY)));
            partenaire.setId_externe(res.getLong(res.getColumnIndex(PartenaireHelper.ID_EXTERNE)));
            partenaire.setNom(res.getString(res.getColumnIndex(PartenaireHelper.NOM)));
            partenaire.setPrenom(res.getString(res.getColumnIndex(PartenaireHelper.PRENOM)));
            partenaire.setAdresse(res.getString(res.getColumnIndex(PartenaireHelper.ADRESSE)));
            partenaire.setContact(res.getString(res.getColumnIndex(PartenaireHelper.CONTACT)));
            partenaire.setSexe(res.getString(res.getColumnIndex(PartenaireHelper.SEXE)));
            partenaire.setRaisonsocial(res.getString(res.getColumnIndex(PartenaireHelper.RAISONSOCIAL)));
            partenaire.setEtat(res.getInt(res.getColumnIndex(PartenaireHelper.ETAT)));
            partenaire.setUtilisateur_id(res.getLong(res.getColumnIndex(PartenaireHelper.UTILISATEUR_iD)));
            partenaire.setPointvente_id(res.getLong(res.getColumnIndex(PartenaireHelper.POINTVENTE_iD)));
            partenaire.setEmail(res.getString(res.getColumnIndex(PartenaireHelper.EMAIL)));
            partenaire.setTypepersonne(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPERSONNE)));
            partenaire.setTypepartenaire(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPARTENAIRE)));

            try {
                partenaire.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            partenaires.add(partenaire);
            res.moveToNext();
        }
        res.close();
        return partenaires;
    }




    public ArrayList<Partenaire> getAllByTypePartnaire(String tp) {
        ArrayList<Partenaire> partenaires = new ArrayList<Partenaire>();
        Partenaire partenaire = null ;

        String sql = "select * from "+ PartenaireHelper.TABLE_NAME + " where " + PartenaireHelper.TYPEPARTENAIRE + " = '" + tp + "' order by " +PartenaireHelper.TABLE_KEY + " desc " ;
        Log.e("SQL",sql) ;
        Cursor res =  mDb.rawQuery( sql, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            partenaire = new Partenaire();


            partenaire.setId(res.getLong(res.getColumnIndex(PartenaireHelper.TABLE_KEY)));
            partenaire.setId_externe(res.getLong(res.getColumnIndex(PartenaireHelper.ID_EXTERNE)));
            partenaire.setNom(res.getString(res.getColumnIndex(PartenaireHelper.NOM)));
            partenaire.setPrenom(res.getString(res.getColumnIndex(PartenaireHelper.PRENOM)));
            partenaire.setAdresse(res.getString(res.getColumnIndex(PartenaireHelper.ADRESSE)));
            partenaire.setContact(res.getString(res.getColumnIndex(PartenaireHelper.CONTACT)));
            partenaire.setSexe(res.getString(res.getColumnIndex(PartenaireHelper.SEXE)));
            partenaire.setRaisonsocial(res.getString(res.getColumnIndex(PartenaireHelper.RAISONSOCIAL)));
            partenaire.setEtat(res.getInt(res.getColumnIndex(PartenaireHelper.ETAT)));
            partenaire.setUtilisateur_id(res.getLong(res.getColumnIndex(PartenaireHelper.UTILISATEUR_iD)));
            partenaire.setPointvente_id(res.getLong(res.getColumnIndex(PartenaireHelper.POINTVENTE_iD)));
            partenaire.setEmail(res.getString(res.getColumnIndex(PartenaireHelper.EMAIL)));
            partenaire.setTypepersonne(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPERSONNE)));
            partenaire.setTypepartenaire(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPARTENAIRE)));

            try {
                partenaire.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            partenaires.add(partenaire);
            res.moveToNext();
        }
        res.close();
        return partenaires;
    }



    public ArrayList<Partenaire> getNonSync() {
        ArrayList<Partenaire> partenaires = new ArrayList<Partenaire>();
        Partenaire partenaire = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ PartenaireHelper.TABLE_NAME  + " Where  " + PartenaireHelper.ETAT + " < 2  order by " +PartenaireHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            partenaire = new Partenaire();


            partenaire.setId(res.getLong(res.getColumnIndex(PartenaireHelper.TABLE_KEY)));
            partenaire.setId_externe(res.getLong(res.getColumnIndex(PartenaireHelper.ID_EXTERNE)));
            partenaire.setNom(res.getString(res.getColumnIndex(PartenaireHelper.NOM)));
            partenaire.setPrenom(res.getString(res.getColumnIndex(PartenaireHelper.PRENOM)));
            partenaire.setAdresse(res.getString(res.getColumnIndex(PartenaireHelper.ADRESSE)));
            partenaire.setContact(res.getString(res.getColumnIndex(PartenaireHelper.CONTACT)));
            partenaire.setSexe(res.getString(res.getColumnIndex(PartenaireHelper.SEXE)));
            partenaire.setRaisonsocial(res.getString(res.getColumnIndex(PartenaireHelper.RAISONSOCIAL)));
            partenaire.setEtat(res.getInt(res.getColumnIndex(PartenaireHelper.ETAT)));
            partenaire.setUtilisateur_id(res.getLong(res.getColumnIndex(PartenaireHelper.UTILISATEUR_iD)));
            partenaire.setPointvente_id(res.getLong(res.getColumnIndex(PartenaireHelper.POINTVENTE_iD)));
            partenaire.setEmail(res.getString(res.getColumnIndex(PartenaireHelper.EMAIL)));
            partenaire.setTypepersonne(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPERSONNE)));
            partenaire.setTypepartenaire(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPARTENAIRE)));

            try {
                partenaire.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            partenaires.add(partenaire);
            res.moveToNext();
        }
        res.close();
        Log.e("SIZE NON", String.valueOf(partenaires.size()));
        return partenaires;
    }



    public ArrayList<Partenaire> getInterval(Date dateDebut, Date dateFin) {
        ArrayList<Partenaire> partenaires = new ArrayList<Partenaire>();
        Partenaire partenaire = null ;



        if (dateDebut==null) dateDebut=new Date("01/01/2015");
        if (dateFin==null) dateFin=new Date() ;

        Log.i("Debug", "SELECT * FROM " + PartenaireHelper.TABLE_NAME + " WHERE " + PartenaireHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateFin) + "' AND '" + formatter.format(dateDebut) + "' ORDER BY " +PartenaireHelper.TABLE_KEY + " DESC ") ;

        Cursor res =  mDb.rawQuery( "SELECT * FROM "+PartenaireHelper.TABLE_NAME + " WHERE "+PartenaireHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " +PartenaireHelper.TABLE_KEY + " DESC ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            partenaire = new Partenaire();

            partenaire.setId(res.getLong(res.getColumnIndex(PartenaireHelper.TABLE_KEY)));
            partenaire.setId_externe(res.getLong(res.getColumnIndex(PartenaireHelper.ID_EXTERNE)));
            partenaire.setNom(res.getString(res.getColumnIndex(PartenaireHelper.NOM)));
            partenaire.setPrenom(res.getString(res.getColumnIndex(PartenaireHelper.PRENOM)));
            partenaire.setAdresse(res.getString(res.getColumnIndex(PartenaireHelper.ADRESSE)));
            partenaire.setContact(res.getString(res.getColumnIndex(PartenaireHelper.CONTACT)));
            partenaire.setSexe(res.getString(res.getColumnIndex(PartenaireHelper.SEXE)));
            partenaire.setRaisonsocial(res.getString(res.getColumnIndex(PartenaireHelper.RAISONSOCIAL)));
            partenaire.setEtat(res.getInt(res.getColumnIndex(PartenaireHelper.ETAT)));
            partenaire.setUtilisateur_id(res.getLong(res.getColumnIndex(PartenaireHelper.UTILISATEUR_iD)));
            partenaire.setPointvente_id(res.getLong(res.getColumnIndex(PartenaireHelper.POINTVENTE_iD)));
            partenaire.setEmail(res.getString(res.getColumnIndex(PartenaireHelper.EMAIL)));
            partenaire.setTypepersonne(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPERSONNE)));
            partenaire.setTypepartenaire(res.getString(res.getColumnIndex(PartenaireHelper.TYPEPARTENAIRE)));

            try {
                partenaire.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(PartenaireHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            partenaires.add(partenaire);
            res.moveToNext();
        }
        res.close();
        Log.i("DEBUG",String.valueOf(partenaires.size()));
        return partenaires;
    }
}
