package pv.projects.mediasoft.com.pventes.model;

import java.util.Date;

/**
 * Created by Mayi on 02/10/2015.
 */
public class Contact {

    long id ;
    long id_externe ;
    int etat ;
    String nom  = null ;
    String prenom  = null ;
    String tel  = null ;
    String email  = null ;
    String sexe  = null ;
    String contact  = null ;
    String adresse  = null ;
    long partenaire_id = 0 ;
    Date created_at = null ;

    public Contact(){
        created_at = new Date() ;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
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

    public long getId_externe() {
        return id_externe;
    }

    public void setId_externe(long id_externe) {
        this.id_externe = id_externe;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public long getPartenaire_id() {
        return partenaire_id;
    }

    public void setPartenaire_id(long partenaire_id) {
        this.partenaire_id = partenaire_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
