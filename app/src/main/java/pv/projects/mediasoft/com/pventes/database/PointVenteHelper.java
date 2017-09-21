package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 05/10/2015.
 */
public class PointVenteHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "pointVente";

    public static final String TABLE_KEY = "_id";
    public static final String LIBELLE = "libelle";
    public static final String PAYS = "pays";
    public static final String VILLE = "ville";
    public static final String QUARTIER = "quartier";
    public static final String TEL = "tel";
    public static final String UTILISATEUR_ID = "utilisateur_id";
    public static final String CREATED_AT = "created_at";

    public static final String POINTVENTE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY, " +
                    LIBELLE + " TEXT, " +
                    PAYS + " TEXT, " +
                    VILLE + " TEXT, " +
                    QUARTIER + " TEXT, " +
                    TEL + " TEXT, " +
                    UTILISATEUR_ID + " INTEGER, " +
                    CREATED_AT + " TEXT);";

    public static final String POINTVENTE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static PointVenteHelper instance ;

    private PointVenteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized PointVenteHelper getHelper(Context context,String name, int version){
        SQLiteDatabase mDb = null;

        if (instance==null) instance = new PointVenteHelper(context, name, null, version) ;

        try{
            mDb = instance.getWritableDatabase();
            String sql = "SELECT " + TABLE_KEY + " FROM " + TABLE_NAME;
            Cursor cursor = mDb.rawQuery(sql, null);
            cursor.close();
        }
        catch(SQLiteException s){
            mDb.execSQL(POINTVENTE_TABLE_CREATE);
        }

        return instance ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(POINTVENTE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(POINTVENTE_TABLE_DROP);
        onCreate(db);
    }
}
