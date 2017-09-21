package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 05/10/2015.
 */
public class CaisseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "caisse";

    public static final String TABLE_KEY = "_id";
    public static final String CODE = "code";
    public static final String IMEI = "imei";
    public static final String ACTIF = "actif";
    public static final String RAISON = "raison";
    public static final String SOLDE = "solde";
    public static final String POINTVENTE_ID = "pointVente_id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    public static final String CAISSE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY, " +
                    CODE + " TEXT, " +
                    IMEI + " TEXT, " +
                    RAISON + " TEXT, " +
                    ACTIF + " INT, " +
                    SOLDE + " REAL, " +
                    POINTVENTE_ID + " INTEGER, " +
                    CREATED_AT + " TEXT);";

    public static final String CAISSE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static CaisseHelper instance ;

    private CaisseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized CaisseHelper getHelper(Context context,String name, int version){
        SQLiteDatabase mDb = null;

        if (instance==null) instance = new CaisseHelper(context, name, null, version) ;

        try{
            mDb = instance.getWritableDatabase();
            String sql = "SELECT " + TABLE_KEY + " FROM " + TABLE_NAME;
            Cursor cursor = mDb.rawQuery(sql, null);
            cursor.close();
        }
        catch(SQLiteException s){
            mDb.execSQL(CAISSE_TABLE_CREATE);
        }

        return instance ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CAISSE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CAISSE_TABLE_DROP);
        onCreate(db);
    }

}
