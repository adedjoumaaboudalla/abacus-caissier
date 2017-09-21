package pv.projects.mediasoft.com.pventes.model;

import java.util.Date;

/**
 * Created by Mayi on 02/10/2015.
 */
public class Licence {

    long id ;
    String code  = null ;
    Date dateExp = null ;
    Date created_at = null ;

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Date getDateExp() {
        return dateExp;
    }


    public Date getCreated_at() {
        return created_at;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDateExp(Date dateExp) {
        this.dateExp = dateExp;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
