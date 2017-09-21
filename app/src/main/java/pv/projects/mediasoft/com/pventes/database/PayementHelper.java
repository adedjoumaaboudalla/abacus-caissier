package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 05/10/2015.
 */
/*
public class PayementHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "payement";

    public static final String TABLE_KEY = "_id";
    public static final String IDEXTERNE = "idexterne";
    public static final String MONTANT = "montant";
    public static final String OPERATION_ID = "operation_id";
    public static final String CREATED_AT = "created_at";
    public static final String MODEPAYEMENT = "modepayement";
    public static final String NUMCHEQUE = "numcheque";
    public static final String BANQUE_ID = "banque_id";
    public static final String ETAT = "etat";

    public static final String PRODUIT_VENTE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                    IDEXTERNE + " INTEGER , " +
                    MONTANT + " REAL, " +
                    OPERATION_ID + " INTEGER, " +
                    ETAT + " INTEGER, " +
                    MODEPAYEMENT + " TEXT, " +
                    NUMCHEQUE + " TEXT, " +
                    BANQUE_ID + " INTEGER, " +
                    CREATED_AT + " TEXT);";

    public static final String PRODUIT_VENTE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static PayementHelper instance ;

    private PayementHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized PayementHelper getHelper(Context context, String name, int version){

        SQLiteDatabase mDb = null;

        if (instance==null) instance = new PayementHelper(context, name, null, version) ;

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
*/