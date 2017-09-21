package pv.projects.mediasoft.com.pventes.model;

import java.util.Date;

/**
 * Created by Mayi on 02/10/2015.
 */
public class Caisse {

    long id ;
    String code  = null ;
    String imei  = null ;
    double solde = 0 ;
    int actif = 1 ;
    String raison = "" ;
    long pointVente_id ;
    Date created_at = null ;

    public Caisse(){

    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public long getPointVente_id() {
        return pointVente_id;
    }

    public void setPointVente_id(long pointVente_id) {
        this.pointVente_id = pointVente_id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setPointVente(long pointVente) {
        this.pointVente_id = pointVente;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }


    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getImei() {
        return imei;
    }

    public long getPointVente() {
        return pointVente_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public int getActif() {
        return actif;
    }

    public void setActif(int actif) {
        this.actif = actif;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }
}
