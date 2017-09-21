package pv.projects.mediasoft.com.pventes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Mayi on 05/10/2015.
 */
public class Produit implements Parcelable {

    long id = 0;
    long id_externe = 0 ;
    String libelle ;
    String code ;
    String affichable = "ACHAT_VENTE" ;
    String image = null ;
    double prixV = 0 ;
    double prixA  = 0;
    int etat = 0 ;
    int modifiable = 0 ;
    String unite ;
    double quantite = 0 ;
    double nbre = 0 ;
    long utilisateur_id ;
    long categorie_id = 1;
    Date created_at = null ;
    Date updated_at = null ;



    public  Produit(){
        created_at = new Date() ;
        updated_at = new Date() ;
    }

    public double getNbre() {
        return nbre;
    }

    public void setNbre(double nbre) {
        this.nbre = nbre;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getUtilisateur_id() {
        return utilisateur_id;
    }

    public void setUtilisateur_id(long utilisateur_id) {
        this.utilisateur_id = utilisateur_id;
    }

    public  Produit(int id,int id_externe, String libelle,String code, int prixV, int prixA,int etat,int modifiable,String unite){
        this.id = id ;
        this.id_externe = id_externe ;
        this.libelle = libelle ;
        this.code = code ;
        this.prixV = prixV ;
        this.prixA = prixA ;
        this.etat = etat ;
        this.modifiable = modifiable ;
        this.unite = unite ;
    }


    public Produit(Parcel source) {
        id = source.readLong() ;
        id_externe = source.readLong() ;
        libelle = source.readString();
        code = source.readString();
        affichable = source.readString();
        image = source.readString();
        prixV = source.readDouble() ;
        prixA = source.readDouble() ;
        etat = source.readInt() ;
        modifiable = source.readInt() ;
        unite = source.readString() ;
        quantite = source.readDouble() ;
        utilisateur_id = source.readLong() ;
        categorie_id = source.readLong() ;
    }

    public long getId() {
        return id;
    }

    public long getId_externe() {
        return id_externe;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getCode() {
        return code;
    }

    public int getEtat() {
        return etat;
    }

    public long getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(long categorie_id) {
        this.categorie_id = categorie_id;
    }

    public int getModifiable() {
        return modifiable;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public Date getCreated_at() {
        return created_at;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setId_externe(long id_externe) {
        this.id_externe = id_externe;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public double getPrixV() {
        return prixV;
    }

    public void setPrixV(double prixV) {
        this.prixV = prixV;
    }

    public double getPrixA() {
        return prixA;
    }

    public void setPrixA(double prixA) {
        this.prixA = prixA;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public void setModifiable(int modifiable) {
        this.modifiable = modifiable;
    }

    public void setUtilisateur(long utilisateur) {
        this.utilisateur_id = utilisateur;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(id_externe);
        dest.writeString(libelle);
        dest.writeString(code);
        dest.writeString(affichable);
        dest.writeString(image);
        dest.writeDouble(prixV);
        dest.writeDouble(prixA);
        dest.writeInt(etat);
        dest.writeInt(modifiable);
        dest.writeString(unite);
        dest.writeDouble(quantite);
        dest.writeLong(utilisateur_id);
        dest.writeLong(categorie_id);
    }


    public static final Creator<Produit> CREATOR = new Creator<Produit>() {
        @Override
        public Produit createFromParcel(Parcel source) {
            return new Produit(source);
        }

        @Override
        public Produit[] newArray(int size) {
            return new Produit[0];
        }
    };

    public String toString(){
        return getCode() ;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Produit && this.getId_externe() == ((Produit)o).getId_externe()) return true ;
        return false ;
    }

    public String getAffichable() {
        return affichable;
    }

    public void setAffichable(String affichable) {
        this.affichable = affichable;
    }


}
