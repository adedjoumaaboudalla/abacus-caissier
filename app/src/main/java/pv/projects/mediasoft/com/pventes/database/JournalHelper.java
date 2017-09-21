package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 30/10/2015.
 */
public class JournalHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "journal";

    public static final String TABLE_KEY = "_id";
    public static final String ID_EXTERNE = "id_externe";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String ETAT = "etat";
    public static final String MONTANT = "montant";
    public static final String DATE_OPERATION = "dateOperation";

    public static final String JOURNAL_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ID_EXTERNE + " INTEGER, " +
                    TYPE + " TEXT, " +
                    DESCRIPTION + " TEXT, " +
                    MONTANT + " REAL, " +
                    ETAT + " INTEGER, " +
                    DATE_OPERATION + " TEXT);";

    public static final String JOURNAL_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static JournalHelper instance ;

    private JournalHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized JournalHelper getHelper(Context context,String name, int version){
        SQLiteDatabase mDb = null;

        if (instance==null) instance = new JournalHelper(context, name, null, version) ;

        try{
            mDb = instance.getWritableDatabase();
            String sql = "SELECT " + TABLE_KEY + " FROM " + TABLE_NAME;
            Cursor cursor = mDb.rawQuery(sql, null);
            cursor.close();
        }
        catch(SQLiteException s){
            mDb.execSQL(JOURNAL_TABLE_CREATE);
        }

        return instance ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(JOURNAL_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(JOURNAL_TABLE_DROP);
        onCreate(db);
    }

}