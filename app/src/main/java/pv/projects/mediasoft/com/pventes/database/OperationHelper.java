package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 05/10/2015.
 */
public class OperationHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "operation";

    public static final String TABLE_KEY = "_id";
    public static final String ID_EXTERNE = "id_externe";
    public static final String REMISE = "remise";
    public static final String MONTANT = "montant";
    public static final String CAISSE_ID = "caisse_id";
    public static final String TYPEOPERATION_ID = "typeOperation_id";
    public static final String ETAT = "etat";
    public static final String PARTENAIRE_ID = "partenaire_id";
    public static final String COMMERCIAL_ID = "commercial_id";
    public static final String NBREPRODUIT = "nbreproduit";
    public static final String RECU = "recu";
    public static final String DESCRIPTION = "description";
    public static final String MODEPAYEMENT = "modepayement";
    public static final String NUMCHEQUE = "numcheque";
    public static final String TOKEN = "token";
    public static final String COMPTEBANQUE_ID = "compte_banque_id";
    public static final String OPERATION_ID = "operation_id";
    public static final String PAYER = "payer";
    public static final String CLIENT = "client";
    public static final String ANNULER = "annuler";
    public static final String ENTREE = "entree";
    public static final String ATTENTE = "attente";
    public static final String DATE_OPERATION = "dateOperation";
    public static final String DATE_ANNULATION = "date_annulation";
    public static final String DATE_ECHEANCE= "date_echeance";
    public static final String CREATED_AT = "created_at";

    public static final String VENTE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ID_EXTERNE + " INTEGER, " +
                    REMISE + " REAL, " +
                    MONTANT + " REAL, " +
                    CAISSE_ID + " INTEGER, " +
                    TYPEOPERATION_ID + " TEXT, " +
                    DESCRIPTION + " TEXT, " +
                    NBREPRODUIT + " REAL, " +
                    PAYER + " INTEGER, " +
                    ETAT + " INTEGER, " +
                    PARTENAIRE_ID + " INTEGER, " +
                    COMMERCIAL_ID + " INTEGER, " +
                    RECU + " REAL, " +
                    ANNULER + " INTEGER, " +
                    ENTREE + " INTEGER, " +
                    COMPTEBANQUE_ID + " INTEGER, " +
                    OPERATION_ID + " INTEGER, " +
                    ATTENTE + " INTEGER, " +
                    CLIENT + " TEXT, " +
                    TOKEN + " TEXT, " +
                    NUMCHEQUE + " TEXT, " +
                    MODEPAYEMENT + " TEXT, " +
                    DATE_OPERATION + " TEXT, " +
                    DATE_ECHEANCE + " TEXT, " +
                    DATE_ANNULATION + " TEXT, " +
                    CREATED_AT + " TEXT);";

    public static final String VENTE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static OperationHelper instance ;

    private OperationHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized OperationHelper getHelper(Context context, String name, int version){
        SQLiteDatabase mDb = null;

        if (instance==null) instance = new OperationHelper(context, name, null, version) ;

        try{
            mDb = instance.getWritableDatabase();
            String sql = "SELECT " + TABLE_KEY + " FROM " + TABLE_NAME;
            Cursor cursor = mDb.rawQuery(sql, null);
            cursor.close();
        }
        catch(SQLiteException s){
            mDb.execSQL(VENTE_TABLE_CREATE);
        }

        return instance ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VENTE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(VENTE_TABLE_DROP);
        onCreate(db);
    }
}
