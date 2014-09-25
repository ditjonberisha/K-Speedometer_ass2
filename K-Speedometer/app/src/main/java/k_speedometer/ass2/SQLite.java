package k_speedometer.ass2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLDataException;
import java.sql.SQLException;

/**
 * Created by blendi on 9/25/2014.
 */
public class SQLite {

    public static String KEY_ROWID = "_id";
    public static String KEY_DATE = "_date";
    public static String KEY_MAXSPEED = "_maxSpeed";

    private static final String DB_NAME = "History";
    private static final String TBL_NAME = "MaxSpeed";
    private static final int DB_VERSION = 1;

    private DbHelper myHelper;
    private final Context myContext;
    private SQLiteDatabase myDatabase;

    public SQLite(Context c){myContext = c;}


    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TBL_NAME + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_DATE + " TEXT NOT NULL, " + KEY_MAXSPEED + " TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {

            db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
            onCreate(db);
        }
    }
    public SQLite Open() throws SQLDataException {

        myHelper = new DbHelper(myContext);
        myDatabase = myHelper.getWritableDatabase();
        return this;
    }

    public void Close() {
        myHelper.close();
    }

    public  long InsertData(String date,String speed) throws SQLException{

        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE,date);
        cv.put(KEY_MAXSPEED,speed);

        return myDatabase.insert(TBL_NAME,null,cv);
    }
    public String getAll(){

        String data = "";
        String[] coulums = new String[]{KEY_ROWID,KEY_DATE,KEY_MAXSPEED};
        Cursor c = myDatabase.query(TBL_NAME,coulums,null,null,null,null,null);
        int iDate = c.getColumnIndex(KEY_DATE);
        int iSpeed = c.getColumnIndex(KEY_MAXSPEED);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            data += "\n" + c.getString(iDate) + "   " + c.getString(iSpeed);
        }
        return  data;
    }

    public void Delete() {
        myDatabase.execSQL("DROP TABLE " + TBL_NAME);
        myHelper.onCreate(myDatabase);
    }
}