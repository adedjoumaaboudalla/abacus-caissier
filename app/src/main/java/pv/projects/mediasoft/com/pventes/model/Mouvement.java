package pv.projects.mediasoft.com.pventes.model;

import java.util.Date;

/**
 * Created by Mayi on 05/10/2015.
 */
public class Mouvement {

    long id  = 0;
    double prixV  = 0;
    double prixA  = 0;
    int entree = 0  ;
    double quantite = 0  ;
    double restant = 0  ;
    double cmup = 0  ;
    long operation_id  = 0 ;
    long produit_id  = 0;
    int produit_etat  = 0;
    String produit ;
    String description = "";
    Date created_at = null ;
    int etat = 0 ;
    int annuler = 0 ;

    public Mouvement(){
        created_at = new Date() ;
    }

    public int getAnnuler() {
        return annuler;
    }

    public void setAnnuler(int annuler) {
        this.annuler = annuler;
    }

    public double getCmup() {
        return cmup;
    }

    public void setCmup(double cmup) {
        this.cmup = cmup;
    }

    public long getId() {
        return id;
    }

    public double getPrixV() {
        return prixV;
    }

    public void setPrixV(double prixV) {
        this.prixV = prixV;
    }

    public int getEntree() {
        return entree;
    }

    public void setEntree(int entree) {
        this.entree = entree;
    }

    public double getPrixA() {
        return prixA;
    }

    public void setPrixA(double prixA) {
        this.prixA = prixA;
    }

    public long getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(long operation_id) {
        this.operation_id = operation_id;
    }

    public long getProduit_id() {
        return produit_id;
    }


    public int getProduit_etat() {
        return produit_etat;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public String getProduit() {
        return produit;
    }

    public int getEtat() {
        return etat;
    }

    public void setId(long id) {
        this.id = id;
    }


    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public void setProduit_id(long produit_id) {
        this.produit_id = produit_id;
    }

    public void setProduit_etat(int produit_etat) {
        this.produit_etat = produit_etat;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public double getRestant() {
        return restant;
    }

    public void setRestant(double restant) {
        this.restant = restant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
