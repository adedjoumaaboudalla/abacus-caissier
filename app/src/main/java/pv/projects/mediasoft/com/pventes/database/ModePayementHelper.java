package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 16/10/2015.
 */
public class ModePayementHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "modePayement";

    public static final String TABLE_KEY = "code";
    public static final String LIBELLE = "libelle";
    public static final String CREATED_AT = "created_at";

    public static final String TYPEDEPENSE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " TEXT PRIMARY KEY , " +
                    LIBELLE + " TEXT, " +
                    CREATED_AT + " TEXT);";

    public static final String TYPEDEPENSE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static ModePayementHelper instance;

    private ModePayementHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized ModePayementHelper getHelper(Context context, String name, int version) {
        SQLiteDatabase mDb = null;

        if (instance == null) instance = new ModePayementHelper(context, name, null, version);

        try {
            mDb = instance.getWritableDatabase();
            String sql = "SELECT " + TABLE_KEY + " FROM " + TABLE_NAME;
            Cursor cursor = mDb.rawQuery(sql, null);
            cursor.close();
        } catch (SQLiteException s) {
            mDb.execSQL(TYPEDEPENSE_TABLE_CREATE);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TYPEDEPENSE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TYPEDEPENSE_TABLE_DROP);
        onCreate(db);
    }

}
