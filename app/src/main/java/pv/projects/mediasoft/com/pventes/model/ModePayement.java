package pv.projects.mediasoft.com.pventes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import pv.projects.mediasoft.com.pventes.dao.DAOBase;

/**
 * Created by Mayi on 16/10/2015.
 */
public class ModePayement implements Parcelable {


    long id  = 0;
    String code  = null ;
    String libelle  = null ;
    Date created_at = null ;

    public ModePayement(){
        created_at = new Date() ;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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
        dest.writeString(DAOBase.formatter.format(created_at));
    }

    public ModePayement(Parcel source) {
        id = source.readLong() ;
        code = source.readString();
        libelle = source.readString();
        created_at = java.sql.Date.valueOf(source.readString())  ;
    }


    public static final Creator<ModePayement> CREATOR = new Creator<ModePayement>() {
        @Override
        public ModePayement createFromParcel(Parcel source) {
            return new ModePayement(source);
        }

        @Override
        public ModePayement[] newArray(int size) {
            return new ModePayement[0];
        }
    };
}
