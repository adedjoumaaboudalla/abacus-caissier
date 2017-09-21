package pv.projects.mediasoft.com.pventes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayi on 05/10/2015.
 */
public class DeviseOperationHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "devise_operation";

    public static final String TABLE_KEY = "_id";
    public static final String OPERATION_ID = "operation_id";
    public static final String DEVISE_ID = "devise_id";
    public static final String MONTANT = "montant";

    public static final String CAISSE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    OPERATION_ID + " INT, " +
                    DEVISE_ID + " INT, " +
                    MONTANT + " REAL);";

    public static final String CAISSE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private static DeviseOperationHelper instance ;

    private DeviseOperationHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized DeviseOperationHelper getHelper(Context context, String name, int version){
        SQLiteDatabase mDb = null;

        if (instance==null) instance = new DeviseOperationHelper(context, name, null, version) ;

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
