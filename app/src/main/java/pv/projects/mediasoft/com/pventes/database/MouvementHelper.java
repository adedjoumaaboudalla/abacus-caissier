package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 05/10/2015.
 */
public class MouvementHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "mouvement";

    public static final String TABLE_KEY = "_id";
    public static final String PRIXV = "priV";
    public static final String PRIXA = "priA";
    public static final String QUANTITE = "quantite";
    public static final String RESTANT = "restant";
    public static final String CMUP = "cmup";
    public static final String PRODUIT_ID = "produit_id";
    public static final String OPERATION_ID = "operation_id";
    public static final String PRODUIT = "produit";
    public static final String DESCRIPTION = "description";
    public static final String PRODUIT_ETAT = "produit_etat";
    public static final String CREATED_AT = "created_at";
    public static final String ETAT = "etat";
    public static final String ANNULER = "annuler";
    public static final String ENTREE = "entree";

    public static final String PRODUIT_VENTE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                    PRIXV + " REAL, " +
                    PRIXA + " REAL, " +
                    QUANTITE + " REAL, " +
                    CMUP + " REAL, " +
                    RESTANT + " REAL, " +
                    PRODUIT_ID + " INTEGER, " +
                    OPERATION_ID + " INTEGER, " +
                    PRODUIT_ETAT + " INTEGER, " +
                    PRODUIT + " TEXT, " +
                    DESCRIPTION + " TEXT, " +
                    ETAT + " INTEGER, " +
                    ANNULER + " INTEGER, " +
                    ENTREE + " INTEGER, " +
                    CREATED_AT + " TEXT);";

    public static final String PRODUIT_VENTE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    public static final String UPDATE_TABLE_MOUV = "ALTER TABLE " + TABLE_NAME + "  ADD " + DESCRIPTION + " TEXT;";

    private static MouvementHelper instance ;

    private MouvementHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized MouvementHelper getHelper(Context context, String name, int version){

        SQLiteDatabase mDb = null;

        if (instance==null) instance = new MouvementHelper(context, name, null, version) ;

        try{
            mDb = instance.getWritableDatabase();
            String sql = "SELECT " + TABLE_KEY + " FROM " + TABLE_NAME;
            Cursor cursor = mDb.rawQuery(sql, null);
            cursor.close();
        }
        catch(SQLiteException s){
            mDb.execSQL(PRODUIT_VENTE_TABLE_CREATE);
        }

        return instance ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PRODUIT_VENTE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PRODUIT_VENTE_TABLE_DROP);
        onCreate(db);
    }
}
