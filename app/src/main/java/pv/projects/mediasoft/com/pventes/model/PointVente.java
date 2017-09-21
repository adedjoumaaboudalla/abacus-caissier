package pv.projects.mediasoft.com.pventes.model;

import java.util.Date;

/**
 * Created by Mayi on 05/10/2015.
 */
public class PointVente {

    long id = 0 ;
    String libelle ;
    String pays ;
    String quartier ;
    String ville ;
    String tel ;
    long utilisateur_id = 0  ;
    Date created_at = null ;
    Date updated_at = null ;

    public PointVente() {
        this.created_at = new Date() ;
    }

    public long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getPays() {
        return pays;
    }

    public String getVille() {
        return ville;
    }

    public String getTel() {
        return tel;
    }

    public String getQuartier() {
        return quartier;
    }

    public long getUtilisateur() {
        return utilisateur_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setId(long id) {
        this.id = id;
    }


    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setUtilisateur_id(long utilisateur_id) {
        this.utilisateur_id = utilisateur_id;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public void setUtilisateur(long utilisateur) {
        this.utilisateur_id = utilisateur;
    }



    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

}
