package pv.projects.mediasoft.com.pventes.dao;

import java.util.ArrayList;

public interface Crud<Object> {

    public long add(Object object);
    public int delete(long id);
    public int update(Object object);
    public Object getOne(long id);
    public ArrayList<Object> getAll();
}
