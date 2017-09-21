package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.database.CommercialHelper;
import pv.projects.mediasoft.com.pventes.database.MouvementHelper;
import pv.projects.mediasoft.com.pventes.model.Commercial;

/**
 * Created by Mayi on 30/10/2015.
 */
public class CommercialDAO extends DAOBase implements Crud<Commercial>{



    public CommercialDAO(Context pContext) {
        super(pContext);
        this.mHandler = CommercialHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(Commercial commercial) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CommercialHelper.ID_EXTERNE, commercial.getId_externe());
        contentValues.put(CommercialHelper.NOM, commercial.getNom());
        contentValues.put(CommercialHelper.PRENOM, commercial.getPrenom());
        contentValues.put(CommercialHelper.SEXE, commercial.getSexe());
        contentValues.put(CommercialHelper.EMAIL, commercial.getEmail());
        contentValues.put(CommercialHelper.ETAT, commercial.getEtat());
        contentValues.put(CommercialHelper.CONTACT, commercial.getContact());
        contentValues.put(CommercialHelper.ADRESSE, commercial.getAdresse());
        contentValues.put(CommercialHelper.UTILISATEUR_iD, commercial.getUtilisateur_id());
        contentValues.put(CommercialHelper.POINTVENTE_iD, commercial.getPointvente_id());
        contentValues.put(CommercialHelper.CREATED_AT, formatter.format(commercial.getCreated_at()));

        long  l = mDb.insert(CommercialHelper.TABLE_NAME, null, contentValues);
        /*
        if (commercial.getId_externe()==0){

            commercial.setId_externe(l);
            update(commercial) ;
        }
        */
        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(CommercialHelper.TABLE_NAME,  CommercialHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }


    public int clean() {
        return  mDb.delete(CommercialHelper.TABLE_NAME, null,null);
    }

    @Override
    public int update(Commercial commercial) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(CommercialHelper.ID_EXTERNE, commercial.getId_externe());
        contentValues.put(CommercialHelper.NOM, commercial.getNom());
        contentValues.put(CommercialHelper.PRENOM, commercial.getPrenom());
        contentValues.put(CommercialHelper.SEXE, commercial.getSexe());
        contentValues.put(CommercialHelper.EMAIL, commercial.getEmail());
        contentValues.put(CommercialHelper.ETAT, commercial.getEtat());
        contentValues.put(CommercialHelper.CONTACT, commercial.getContact());
        contentValues.put(CommercialHelper.ADRESSE, commercial.getAdresse());
        contentValues.put(CommercialHelper.UTILISATEUR_iD, commercial.getUtilisateur_id());
        contentValues.put(CommercialHelper.POINTVENTE_iD, commercial.getPointvente_id());
        contentValues.put(CommercialHelper.CREATED_AT, formatter.format(commercial.getCreated_at()));

        int i =mDb.update(CommercialHelper.TABLE_NAME, contentValues,  CommercialHelper.TABLE_KEY + " = ? ", new String[] { Long.toString(commercial.getId()) } );

        return i;
    }

    @Override
    public Commercial getOne(long id) {
        Commercial commercial = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ CommercialHelper.TABLE_NAME +" where " +CommercialHelper.TABLE_KEY + "  = "+id+"", null );

        if(res.moveToFirst()){
            commercial = new Commercial();

            commercial.setId(res.getLong(res.getColumnIndex(CommercialHelper.TABLE_KEY)));
            commercial.setId_externe(res.getLong(res.getColumnIndex(CommercialHelper.ID_EXTERNE)));
            commercial.setNom(res.getString(res.getColumnIndex(CommercialHelper.NOM)));
            commercial.setPrenom(res.getString(res.getColumnIndex(CommercialHelper.PRENOM)));
            commercial.setAdresse(res.getString(res.getColumnIndex(CommercialHelper.ADRESSE)));
            commercial.setContact(res.getString(res.getColumnIndex(CommercialHelper.CONTACT)));
            commercial.setSexe(res.getString(res.getColumnIndex(CommercialHelper.SEXE)));
            commercial.setEmail(res.getString(res.getColumnIndex(CommercialHelper.EMAIL)));
            commercial.setEtat(res.getInt(res.getColumnIndex(CommercialHelper.ETAT)));
            commercial.setUtilisateur_id(res.getLong(res.getColumnIndex(CommercialHelper.UTILISATEUR_iD)));
            commercial.setPointvente_id(res.getLong(res.getColumnIndex(CommercialHelper.POINTVENTE_iD)));

            try {
                commercial.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(CommercialHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

         };

        res.close();
        return commercial ;
    }

    public Commercial getLast() {
        Commercial commercial = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ CommercialHelper.TABLE_NAME +" order by " + CommercialHelper.TABLE_KEY + " desc", null );

        if(res.moveToFirst()){
            commercial = new Commercial();

            commercial.setId(res.getLong(res.getColumnIndex(CommercialHelper.TABLE_KEY)));
            commercial.setId_externe(res.getLong(res.getColumnIndex(CommercialHelper.ID_EXTERNE)));
            commercial.setNom(res.getString(res.getColumnIndex(CommercialHelper.NOM)));
            commercial.setPrenom(res.getString(res.getColumnIndex(CommercialHelper.PRENOM)));
            commercial.setAdresse(res.getString(res.getColumnIndex(CommercialHelper.ADRESSE)));
            commercial.setContact(res.getString(res.getColumnIndex(CommercialHelper.CONTACT)));
            commercial.setSexe(res.getString(res.getColumnIndex(CommercialHelper.SEXE)));
            commercial.setEmail(res.getString(res.getColumnIndex(CommercialHelper.EMAIL)));
            commercial.setEtat(res.getInt(res.getColumnIndex(CommercialHelper.ETAT)));
            commercial.setUtilisateur_id(res.getLong(res.getColumnIndex(CommercialHelper.UTILISATEUR_iD)));
            commercial.setPointvente_id(res.getLong(res.getColumnIndex(CommercialHelper.POINTVENTE_iD)));

            try {
                commercial.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(CommercialHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

         };

        res.close();
        return commercial ;
    }

    @Override
    public ArrayList<Commercial> getAll() {
        ArrayList<Commercial> commercials = new ArrayList<Commercial>();
        Commercial commercial = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ CommercialHelper.TABLE_NAME + " order by " +CommercialHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            commercial = new Commercial();


            commercial.setId(res.getLong(res.getColumnIndex(CommercialHelper.TABLE_KEY)));
            commercial.setId_externe(res.getLong(res.getColumnIndex(CommercialHelper.ID_EXTERNE)));
            commercial.setNom(res.getString(res.getColumnIndex(CommercialHelper.NOM)));
            commercial.setPrenom(res.getString(res.getColumnIndex(CommercialHelper.PRENOM)));
            commercial.setAdresse(res.getString(res.getColumnIndex(CommercialHelper.ADRESSE)));
            commercial.setContact(res.getString(res.getColumnIndex(CommercialHelper.CONTACT)));
            commercial.setSexe(res.getString(res.getColumnIndex(CommercialHelper.SEXE)));
            commercial.setEtat(res.getInt(res.getColumnIndex(CommercialHelper.ETAT)));
            commercial.setUtilisateur_id(res.getLong(res.getColumnIndex(CommercialHelper.UTILISATEUR_iD)));
            commercial.setPointvente_id(res.getLong(res.getColumnIndex(CommercialHelper.POINTVENTE_iD)));
            commercial.setEmail(res.getString(res.getColumnIndex(CommercialHelper.EMAIL)));

            try {
                commercial.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(CommercialHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            commercials.add(commercial);
            res.moveToNext();
        }
        res.close();
        return commercials;
    }



    public ArrayList<Commercial> getNonSync() {
        ArrayList<Commercial> commercials = new ArrayList<Commercial>();
        Commercial commercial = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ CommercialHelper.TABLE_NAME   + " Where  " + CommercialHelper.ETAT + " < 2  order by " +CommercialHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            commercial = new Commercial();

            commercial.setId(res.getLong(res.getColumnIndex(CommercialHelper.TABLE_KEY)));
            commercial.setId_externe(res.getLong(res.getColumnIndex(CommercialHelper.ID_EXTERNE)));
            commercial.setNom(res.getString(res.getColumnIndex(CommercialHelper.NOM)));
            commercial.setPrenom(res.getString(res.getColumnIndex(CommercialHelper.PRENOM)));
            commercial.setAdresse(res.getString(res.getColumnIndex(CommercialHelper.ADRESSE)));
            commercial.setContact(res.getString(res.getColumnIndex(CommercialHelper.CONTACT)));
            commercial.setSexe(res.getString(res.getColumnIndex(CommercialHelper.SEXE)));
            commercial.setEtat(res.getInt(res.getColumnIndex(CommercialHelper.ETAT)));
            commercial.setUtilisateur_id(res.getLong(res.getColumnIndex(CommercialHelper.UTILISATEUR_iD)));
            commercial.setPointvente_id(res.getLong(res.getColumnIndex(CommercialHelper.POINTVENTE_iD)));
            commercial.setEmail(res.getString(res.getColumnIndex(CommercialHelper.EMAIL)));

            try {
                commercial.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(CommercialHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            commercials.add(commercial);
            res.moveToNext();
        }
        Log.e("SIZE NON", String.valueOf(commercials.size()));

        res.close();
        return commercials;
    }



    public ArrayList<Commercial> getInterval(Date dateDebut, Date dateFin) {
        ArrayList<Commercial> commercials = new ArrayList<Commercial>();
        Commercial commercial = null ;



        if (dateDebut==null) dateDebut=new Date("01/01/2015");
        if (dateFin==null) dateFin=new Date() ;

        Log.i("Debug", "SELECT * FROM " + CommercialHelper.TABLE_NAME + " WHERE " + CommercialHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateFin) + "' AND '" + formatter.format(dateDebut) + "' ORDER BY " +CommercialHelper.TABLE_KEY + " DESC ") ;

        Cursor res =  mDb.rawQuery( "SELECT * FROM "+CommercialHelper.TABLE_NAME + " WHERE "+CommercialHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " +CommercialHelper.TABLE_KEY + " DESC ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            commercial = new Commercial();

            commercial.setId(res.getLong(res.getColumnIndex(CommercialHelper.TABLE_KEY)));
            commercial.setId_externe(res.getLong(res.getColumnIndex(CommercialHelper.ID_EXTERNE)));
            commercial.setNom(res.getString(res.getColumnIndex(CommercialHelper.NOM)));
            commercial.setPrenom(res.getString(res.getColumnIndex(CommercialHelper.PRENOM)));
            commercial.setAdresse(res.getString(res.getColumnIndex(CommercialHelper.ADRESSE)));
            commercial.setContact(res.getString(res.getColumnIndex(CommercialHelper.CONTACT)));
            commercial.setSexe(res.getString(res.getColumnIndex(CommercialHelper.SEXE)));
            commercial.setEtat(res.getInt(res.getColumnIndex(CommercialHelper.ETAT)));
            commercial.setUtilisateur_id(res.getLong(res.getColumnIndex(CommercialHelper.UTILISATEUR_iD)));
            commercial.setPointvente_id(res.getLong(res.getColumnIndex(CommercialHelper.POINTVENTE_iD)));
            commercial.setEmail(res.getString(res.getColumnIndex(CommercialHelper.EMAIL)));

            try {
                commercial.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(CommercialHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            commercials.add(commercial);
            res.moveToNext();
        }
        Log.i("DEBUG",String.valueOf(commercials.size()));
        res.close();
        return commercials;
    }
}
