package pv.projects.mediasoft.com.pventes.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Mayi on 05/10/2015.
 */
public abstract class DAOBase {
    public final static int VERSION = 1;

    public final static String DATABASE = "abacuscaissier.db";
    protected SQLiteDatabase mDb = null;
    protected SQLiteOpenHelper mHandler = null;
    Context mContext = null ;

    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
    public static final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd") ;
    public static final SimpleDateFormat formatter6 = new SimpleDateFormat("dd/MM/yy") ;
    public static final SimpleDateFormat formatter7 = new SimpleDateFormat("dd/MM") ;
    public static final SimpleDateFormat formatterj = new SimpleDateFormat("dd/MM/yyyy") ;
    public static final SimpleDateFormat formatter3 = new SimpleDateFormat("EEE dd MMM HH:mm", Locale.getDefault()) ;
    public static final SimpleDateFormat formatter5 = new SimpleDateFormat("EEE dd MMM", Locale.getDefault()) ;
    public static final SimpleDateFormat formatter4 = new SimpleDateFormat("HH:mm", Locale.getDefault()) ;
    //public static final SimpleDateFormat formatter3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy") ;

    public DAOBase(Context pContext) {
            mContext = pContext ;
    }

    public SQLiteDatabase open() {
        mDb = mHandler.getWritableDatabase();
        return mDb;
    }

    public void close() {
        if (mDb!=null) mDb.close();
    }

    public SQLiteDatabase getDb() {
        if (mDb == null) return open() ;
        return mDb;
    }



}
