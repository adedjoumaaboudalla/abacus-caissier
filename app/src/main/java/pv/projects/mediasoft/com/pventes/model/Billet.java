package pv.projects.mediasoft.com.pventes.model;

/**
 * Created by Mayi on 08/01/2016.
 */
public class Billet {
    long id = -1 ;
    String libelle = null ;
    double montant = 0 ;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getLibelle() {
        return libelle;
    }


    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
}
