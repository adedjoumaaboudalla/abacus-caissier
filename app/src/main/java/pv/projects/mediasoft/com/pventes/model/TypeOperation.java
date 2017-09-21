package pv.projects.mediasoft.com.pventes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import pv.projects.mediasoft.com.pventes.dao.DAOBase;

/**
 * Created by Mayi on 16/10/2015.
 */
public class TypeOperation implements Parcelable {

    String code  = null ;
    String libelle  = null ;
    Date created_at = null ;

    public TypeOperation(){
        created_at = new Date() ;
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
        dest.writeString(code);
        dest.writeString(libelle);
        dest.writeString(DAOBase.formatter.format(created_at));
    }

    public TypeOperation(Parcel source) {
        code = source.readString();
        libelle = source.readString();
        created_at = java.sql.Date.valueOf(source.readString())  ;
    }


    public static final Creator<TypeOperation> CREATOR = new Creator<TypeOperation>() {
        @Override
        public TypeOperation createFromParcel(Parcel source) {
            return new TypeOperation(source);
        }

        @Override
        public TypeOperation[] newArray(int size) {
            return new TypeOperation[0];
        }
    };
}
