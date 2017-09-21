package pv.projects.mediasoft.com.pventes.model;

import java.util.Date;

/**
 * Created by Mayi on 02/10/2015.
 */
public class Partenaire {

    long id = 0 ;
    long id_externe = 0 ;
    String nom  = "" ;
    String prenom  = "" ;
    String raisonsocial  = "" ;
    String sexe  = "" ;
    String email  = "" ;
    String typepersonne = null ;
    String typepartenaire = null ;
    int etat = 0 ;
    String contact  = "" ;
    String adresse  = "" ;
    long utilisateur_id ;
    long pointvente_id ;
    Date created_at = null ;

    public Partenaire(){
        created_at = new Date() ;
    }

    public String getTypepartenaire() {
        return typepartenaire;
    }

    public void setTypepartenaire(String typepartenaire) {
        this.typepartenaire = typepartenaire;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypepersonne() {
        return typepersonne;
    }

    public void setTypepersonne(String typepersonne) {
        this.typepersonne = typepersonne;
    }

    public long getPointvente_id() {
        return pointvente_id;
    }

    public void setPointvente_id(long pointvente_id) {
        this.pointvente_id = pointvente_id;
    }

    public long getId_externe() {
        return id_externe;
    }

    public void setId_externe(long id_externe) {
        this.id_externe = id_externe;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public long getUtilisateur_id() {
        return utilisateur_id;
    }

    public void setUtilisateur_id(long utilisateur_id) {
        this.utilisateur_id = utilisateur_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRaisonsocial() {
        return raisonsocial;
    }

    public void setRaisonsocial(String raisonsocial) {
        this.raisonsocial = raisonsocial;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }


    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
