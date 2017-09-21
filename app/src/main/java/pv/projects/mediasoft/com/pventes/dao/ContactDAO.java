package pv.projects.mediasoft.com.pventes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import pv.projects.mediasoft.com.pventes.database.ContactHelper;
import pv.projects.mediasoft.com.pventes.model.Contact;

/**
 * Created by Mayi on 30/10/2015.
 */
public class ContactDAO extends DAOBase implements Crud<Contact>{


    public ContactDAO(Context pContext) {
        super(pContext);
        this.mHandler = ContactHelper.getHelper(pContext, DATABASE, VERSION);
        open();
    }

    @Override
    public long add(Contact contact) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactHelper.ID_EXTERNE, contact.getId_externe());
        contentValues.put(ContactHelper.NOM, contact.getNom());
        contentValues.put(ContactHelper.PRENOM, contact.getPrenom());
        contentValues.put(ContactHelper.EMAIL, contact.getEmail());
        contentValues.put(ContactHelper.SEXE, contact.getSexe());
        contentValues.put(ContactHelper.ETAT, contact.getEtat());
        contentValues.put(ContactHelper.CONTACT, contact.getContact());
        contentValues.put(ContactHelper.ADRESSE, contact.getAdresse());
        contentValues.put(ContactHelper.PARTENAIRE_iD, contact.getPartenaire_id());
        contentValues.put(ContactHelper.CREATED_AT, formatter.format(contact.getCreated_at()));

        long  l = mDb.insert(ContactHelper.TABLE_NAME, null, contentValues);

        return l;
    }

    @Override
    public int delete(long id) {
        return  mDb.delete(ContactHelper.TABLE_NAME,  ContactHelper.TABLE_KEY + " = ? ",new String[] { Long.toString(id)});
    }

    @Override
    public int update(Contact contact) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContactHelper.ID_EXTERNE, contact.getId_externe());
        contentValues.put(ContactHelper.NOM, contact.getNom());
        contentValues.put(ContactHelper.PRENOM, contact.getPrenom());
        contentValues.put(ContactHelper.EMAIL, contact.getEmail());
        contentValues.put(ContactHelper.SEXE, contact.getSexe());
        contentValues.put(ContactHelper.ETAT, contact.getEtat());
        contentValues.put(ContactHelper.CONTACT, contact.getContact());
        contentValues.put(ContactHelper.ADRESSE, contact.getAdresse());
        contentValues.put(ContactHelper.PARTENAIRE_iD, contact.getPartenaire_id());
        contentValues.put(ContactHelper.CREATED_AT, formatter.format(contact.getCreated_at()));

        int i =mDb.update(ContactHelper.TABLE_NAME, contentValues,  ContactHelper.TABLE_KEY + " = ? ", new String[] { Long.toString(contact.getId()) } );

        return i;
    }

    @Override
    public Contact getOne(long id) {
        Contact contact = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ ContactHelper.TABLE_NAME +" where " +ContactHelper.TABLE_KEY + "  = "+id+"", null );

        if(res.moveToFirst()){
            contact = new Contact();

            contact.setId(res.getLong(res.getColumnIndex(ContactHelper.TABLE_KEY)));
            contact.setId_externe(res.getLong(res.getColumnIndex(ContactHelper.ID_EXTERNE)));
            contact.setNom(res.getString(res.getColumnIndex(ContactHelper.NOM)));
            contact.setPrenom(res.getString(res.getColumnIndex(ContactHelper.PRENOM)));
            contact.setAdresse(res.getString(res.getColumnIndex(ContactHelper.ADRESSE)));
            contact.setContact(res.getString(res.getColumnIndex(ContactHelper.CONTACT)));
            contact.setSexe(res.getString(res.getColumnIndex(ContactHelper.SEXE)));
            contact.setEmail(res.getString(res.getColumnIndex(ContactHelper.EMAIL)));
            contact.setEtat(res.getInt(res.getColumnIndex(ContactHelper.ETAT)));
            contact.setPartenaire_id(res.getInt(res.getColumnIndex(ContactHelper.PARTENAIRE_iD)));

            try {
                contact.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ContactHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

         };

        res.close();
        return contact ;
    }

    @Override
    public ArrayList<Contact> getAll() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        Contact contact = null ;

        Cursor res =  mDb.rawQuery( "select * from "+ ContactHelper.TABLE_NAME + " order by " +ContactHelper.TABLE_KEY + " desc ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            contact = new Contact();

            contact.setId(res.getLong(res.getColumnIndex(ContactHelper.TABLE_KEY)));
            contact.setId_externe(res.getLong(res.getColumnIndex(ContactHelper.ID_EXTERNE)));
            contact.setNom(res.getString(res.getColumnIndex(ContactHelper.NOM)));
            contact.setPrenom(res.getString(res.getColumnIndex(ContactHelper.PRENOM)));
            contact.setAdresse(res.getString(res.getColumnIndex(ContactHelper.ADRESSE)));
            contact.setContact(res.getString(res.getColumnIndex(ContactHelper.CONTACT)));
            contact.setSexe(res.getString(res.getColumnIndex(ContactHelper.SEXE)));
            contact.setEmail(res.getString(res.getColumnIndex(ContactHelper.EMAIL)));
            contact.setEtat(res.getInt(res.getColumnIndex(ContactHelper.ETAT)));
            contact.setPartenaire_id(res.getInt(res.getColumnIndex(ContactHelper.PARTENAIRE_iD)));

            try {
                contact.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ContactHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            contacts.add(contact);
            res.moveToNext();
        }
        res.close();
        return contacts;
    }



    public ArrayList<Contact> getInterval(Date dateDebut, Date dateFin) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        Contact contact = null ;



        if (dateDebut==null) dateDebut=new Date("01/01/2015");
        if (dateFin==null) dateFin=new Date() ;

        Log.i("Debug", "SELECT * FROM " + ContactHelper.TABLE_NAME + " WHERE " + ContactHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateFin) + "' AND '" + formatter.format(dateDebut) + "' ORDER BY " +ContactHelper.TABLE_KEY + " DESC ") ;

        Cursor res =  mDb.rawQuery( "SELECT * FROM "+ContactHelper.TABLE_NAME + " WHERE "+ContactHelper.CREATED_AT + "  BETWEEN '" + formatter.format(dateDebut) + "' AND '" + formatter.format(dateFin) + "' ORDER BY " +ContactHelper.TABLE_KEY + " DESC ", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            contact = new Contact();
            contact.setId(res.getLong(res.getColumnIndex(ContactHelper.TABLE_KEY)));
            contact.setId_externe(res.getLong(res.getColumnIndex(ContactHelper.ID_EXTERNE)));
            contact.setNom(res.getString(res.getColumnIndex(ContactHelper.NOM)));
            contact.setPrenom(res.getString(res.getColumnIndex(ContactHelper.PRENOM)));
            contact.setAdresse(res.getString(res.getColumnIndex(ContactHelper.ADRESSE)));
            contact.setContact(res.getString(res.getColumnIndex(ContactHelper.CONTACT)));
            contact.setSexe(res.getString(res.getColumnIndex(ContactHelper.SEXE)));
            contact.setEmail(res.getString(res.getColumnIndex(ContactHelper.EMAIL)));
            contact.setEtat(res.getInt(res.getColumnIndex(ContactHelper.ETAT)));
            contact.setPartenaire_id(res.getInt(res.getColumnIndex(ContactHelper.PARTENAIRE_iD)));

            try {
                contact.setCreated_at(DAOBase.formatter.parse(res.getString(res.getColumnIndex(ContactHelper.CREATED_AT))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            contacts.add(contact);
            res.moveToNext();
        }
        Log.i("DEBUG",String.valueOf(contacts.size()));
        res.close();
        return contacts;
    }
}
