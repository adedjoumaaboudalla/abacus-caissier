package pv.projects.mediasoft.com.pventes.model;

/**
 * Created by Mayi on 08/01/2016.
 */
public class CompteBanque {
    long id = 0 ;
    long id_externe = 0 ;
    long utilisateur_id = -1 ;
    String code = null ;
    double solde = 0 ;
    int etat = 0 ;
    int cheque = 0 ;
    int cartebanque = 0 ;
    String libelle = null ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public long getId_externe() {
        return id_externe;
    }

    public void setId_externe(long id_externe) {
        this.id_externe = id_externe;
    }

    public long getUtilisateur_id() {
        return utilisateur_id;
    }

    public void setUtilisateur_id(long utilisateur_id) {
        this.utilisateur_id = utilisateur_id;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getCheque() {
        return cheque;
    }

    public void setCheque(int cheque) {
        this.cheque = cheque;
    }

    public int getCartebanque() {
        return cartebanque;
    }

    public void setCartebanque(int cartebanque) {
        this.cartebanque = cartebanque;
    }
}
