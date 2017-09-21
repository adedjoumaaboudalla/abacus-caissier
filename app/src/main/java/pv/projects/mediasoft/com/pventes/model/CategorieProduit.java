package pv.projects.mediasoft.com.pventes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import pv.projects.mediasoft.com.pventes.dao.DAOBase;

/**
 * Created by Mayi on 16/10/2015.
 */
public class CategorieProduit implements Parcelable {


    long id  = 0;
    String code  = null ;
    String libelle  = null ;
    int etat = 0 ;
    Date created_at = null ;

    public CategorieProduit(){
        created_at = new Date() ;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }


    public String getCode() {
        return code;
    }

    public long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public int getEtat() {
        return etat;
    }

    public Date getCreated_at() {
        return created_at;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(code);
        dest.writeString(libelle);
        dest.writeInt(etat);
        dest.writeString(DAOBase.formatter.format(created_at));
    }

    public CategorieProduit(Parcel source) {
        id = source.readLong() ;
        code = source.readString();
        libelle = source.readString();
        etat = source.readInt();
        created_at = java.sql.Date.valueOf(source.readString())  ;
    }


    public static final Creator<CategorieProduit> CREATOR = new Creator<CategorieProduit>() {
        @Override
        public CategorieProduit createFromParcel(Parcel source) {
            return new CategorieProduit(source);
        }

        @Override
        public CategorieProduit[] newArray(int size) {
            return new CategorieProduit[0];
        }
    };
}
