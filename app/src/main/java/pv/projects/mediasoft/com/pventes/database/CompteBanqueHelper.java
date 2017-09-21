package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 05/10/2015.
 */
public class CompteBanqueHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "comptebanque";

    public static final String TABLE_KEY = "_id";
    public static final String ID_EXTERNE = "id_externe";
    public static final String CODE = "code";
    public static final String ETAT = "etat";
    public static final String CHEQUE = "cheque";
    public static final String CARTEBANK = "cartebanque";
    public static final String SOLDE = "solde";
    public static final String UTILISATEUR_ID = "utilisateur_id";
    public static final String LIBELLE = "libelle";

    public static final String _TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ID_EXTERNE + " INTEGER, " +
                    ETAT + " INTEGER, " +
                    CHEQUE + " INTEGER, " +
                    CARTEBANK + " INTEGER, " +
                    UTILISATEUR_ID + " INTEGER, " +
                    CODE + " TEXT, " +
                    SOLDE + " REAL, " +
                    LIBELLE + " TEXT );";

    public static final String _TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static CompteBanqueHelper instance ;

    private CompteBanqueHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized CompteBanqueHelper getHelper(Context context, String name, int version){
        SQLiteDatabase mDb = null;

        if (instance==null) instance = new CompteBanqueHelper(context, name, null, version) ;

        try{
            mDb = instance.getWritableDatabase();
            String sql = "SELECT " + TABLE_KEY + " FROM " + TABLE_NAME;
            Cursor cursor = mDb.rawQuery(sql, null);
            cursor.close();
        }
        catch(SQLiteException s){
            mDb.execSQL(_TABLE_CREATE);
        }

        return instance ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(_TABLE_DROP);
        onCreate(db);
    }

}
