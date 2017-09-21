package pv.projects.mediasoft.com.pventes.model;

/**
 * Created by mediasoft on 01/08/2017.
 */

public class Devise {
    long id = 0 ;
    String codedevise = null ;
    String libelledevise = null ;
    double coursmoyen = 0 ;
    String unite = null ;
    String symbole = null ;
    int defaut = 0 ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCodedevise() {
        return codedevise;
    }

    public void setCodedevise(String codedevise) {
        this.codedevise = codedevise;
    }

    public String getLibelledevise() {
        return libelledevise;
    }

    public void setLibelledevise(String libelledevise) {
        this.libelledevise = libelledevise;
    }

    public double getCoursmoyen() {
        return coursmoyen;
    }

    public void setCoursmoyen(double coursmoyen) {
        this.coursmoyen = coursmoyen;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getSymbole() {
        return symbole;
    }

    public void setSymbole(String symbole) {
        this.symbole = symbole;
    }

    public int getDefaut() {
        return defaut;
    }

    public void setDefaut(int defaut) {
        this.defaut = defaut;
    }
}
