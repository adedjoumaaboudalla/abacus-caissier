package pv.projects.mediasoft.com.pventes.model;

import java.util.Date;

/**
 * Created by Mayi on 21/04/2011.
 */
public class Operation {

    long id  = 0;
    long id_externe = -1 ;
    double remise  = 0;
    double montant = 0  ;
    long caisse_id  = 0 ;
    long partenaire_id  = 0 ;
    long commercialid  = 0 ;
    long comptebanque_id  = 0 ;
    long operation_id  = 0 ;
    String typeOperation_id = null  ;
    int etat = 0 ;
    double recu = 0 ;
    int payer = 1 ;
    int entree = 0  ;
    int attente = 0  ;
    double nbreproduit = 0 ;
    int annuler = 0 ;
    String client = "COMPTANT";
    String description = "";
    String modepayement = "";
    String numcheque = "";
    Date dateoperation = null ;
    Date dateannulation = null ;
    Date dateecheance = null ;
    Date created_at = null ;
    Date updated_at = null ;

    String token = "" ;

    public Operation(){
        dateoperation = new Date() ;
        dateannulation = new Date() ;
        created_at = new Date() ;
        updated_at = new Date() ;
    }

    public long getComptebanque_id() {
        return comptebanque_id;
    }

    public void setComptebanque_id(long comptebanque_id) {
        this.comptebanque_id = comptebanque_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getCommercialid() {
        return commercialid;
    }

    public void setCommercialid(long commercialid) {
        this.commercialid = commercialid;
    }

    public int getEntree() {
        return entree;
    }

    public void setEntree(int entree) {
        this.entree = entree;
    }

    public Date getDateecheance() {
        return dateecheance;
    }

    public void setDateecheance(Date dateecheance) {
        this.dateecheance = dateecheance;
    }

    public int getAnnuler() {
        return annuler;
    }

    public void setAnnuler(int annuler) {
        this.annuler = annuler;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPartenaire_id() {
        return partenaire_id;
    }

    public void setPartenaire_id(long partenaire_id) {
        this.partenaire_id = partenaire_id;
    }

    public void setRecu(int recu) {
        this.recu = recu;
    }

    public void setId_externe(long id_externe) {
        this.id_externe = id_externe;
    }

    public String getClient() {
        return client;
    }

    public void setCaisse(long caisse) {
        this.caisse_id = caisse;
    }

    public void setRemise(int remise) {
        this.remise = remise;
    }

    public int getAttente() {
        return attente;
    }

    public void setAttente(int attente) {
        this.attente = attente;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public void setCaisse_id(long caisse_id) {
        this.caisse_id = caisse_id;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public long getId() {
        return id;
    }
    public long getId_externe() {
        return id_externe;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public double getRecu() {
        return recu;
    }

    public void setRecu(double recu) {
        this.recu = recu;
    }

    public int getPayer() {
        return payer;
    }

    public void setPayer(int payer) {
        this.payer = payer;
    }

    public double getNbreproduit() {
        return nbreproduit;
    }

    public void setNbreproduit(double nbreproduit) {
        this.nbreproduit = nbreproduit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCaisse() {
        return caisse_id;
    }

    public int getEtat() {
        return etat;
    }

    public long getCaisse_id() {
        return caisse_id;
    }

    public Date getDateoperation() {
        return dateoperation;
    }

    public void setDateoperation(Date dateoperation) {
        this.dateoperation = dateoperation;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public String getTypeOperation_id() {
        return typeOperation_id;
    }

    public void setTypeOperation_id(String typeOperation_id) {
        this.typeOperation_id = typeOperation_id;
    }

    public String getModepayement() {
        return modepayement;
    }

    public void setModepayement(String modepayement) {
        this.modepayement = modepayement;
    }

    public String getNumcheque() {
        return numcheque;
    }

    public void setNumcheque(String numcheque) {
        this.numcheque = numcheque;
    }

    public Date getDateannulation() {
        return dateannulation;
    }

    public void setDateannulation(Date dateannulation) {
        this.dateannulation = dateannulation;
    }

    public long getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(long operation_id) {
        this.operation_id = operation_id;
    }
}
