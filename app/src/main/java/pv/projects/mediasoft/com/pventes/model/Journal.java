package pv.projects.mediasoft.com.pventes.model;

import java.util.Date;

/**
 * Created by Mayi on 30/10/2015.
 */
public class Journal {

    long id ;
    long id_externe = 0 ;
    String type  = null ;
    String description  = null ;
    double montant ;
    int etat = 0 ;
    Date dateoperation = null ;

    public Journal(){
        dateoperation = new Date();
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public int getEtat() {
        return etat;
    }

    public long getId_externe() {
        return id_externe;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Date getDateoperation() {
        return dateoperation;
    }


    public void setId(long id) {
        this.id = id;
    }



    public void setType(String type) {
        this.type = type;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public void setId_externe(long id_externe) {
        this.id_externe = id_externe;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateoperation(Date dateoperation) {
        this.dateoperation = dateoperation;
    }


}
