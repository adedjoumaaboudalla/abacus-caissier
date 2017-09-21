package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 05/10/2015.
 */
public class DeviseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "devise";

    public static final String TABLE_KEY = "_id";
    public static final String CODEDEVISE = "codedevise";
    public static final String LIBELLE_DEVISE = "libelle";
    public static final String COURS_MOYEN = "cours_moyen";
    public static final String UNITE = "unite";
    public static final String SYMBOLE = "symbole";
    public static final String DEFAUT = "defaut";

    public static final String DEVISE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY, " +
                    CODEDEVISE + " TEXT, " +
                    LIBELLE_DEVISE + " TEXT, " +
                    COURS_MOYEN + " REAL, " +
                    UNITE + " TEXT, " +
                    SYMBOLE + " TEXT, " +
                    DEFAUT + " INTEGER);";

    public static final String DEVISE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static DeviseHelper instance ;

    private DeviseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized DeviseHelper getHelper(Context context, String name, int version){
        SQLiteDatabase mDb = null;

        if (instance==null) instance = new DeviseHelper(context, name, null, version) ;

        try{
            mDb = instance.getWritableDatabase();
            String sql = "SELECT " + TABLE_KEY + " FROM " + TABLE_NAME;
            Cursor cursor = mDb.rawQuery(sql, null);
            cursor.close();
        }
        catch(SQLiteException s){
            mDb.execSQL(DEVISE_TABLE_CREATE);
        }

        return instance ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DEVISE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DEVISE_TABLE_DROP);
        onCreate(db);
    }

}
