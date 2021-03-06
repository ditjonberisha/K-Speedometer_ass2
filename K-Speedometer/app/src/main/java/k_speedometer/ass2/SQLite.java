package k_speedometer.ass2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.SQLDataException;
import java.sql.SQLException;

/*
    Created by Arnold, Ditjon and Blend
 */
public class  SQLite extends Activity{

    private static String KEY_ROWID = "_id";
    private static String KEY_DATE = "_date";
    private static String KEY_MAXSPEED = "_maxSpeed";
    private static String KEY_TIME = "_time";

    private static final String DB_NAME = "History";
    private static final String TBL_NAME = "MaxSpeed";
    private static final int DB_VERSION = 1;

    private DbHelper myHelper;
    private Context myContext;
    private SQLiteDatabase myDatabase;
    public SQLite(Context c) {
        myContext = c;
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TBL_NAME + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_DATE + " TEXT NOT NULL, " + KEY_MAXSPEED + " TEXT NOT NULL, "+ KEY_TIME + " TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {

            db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
            onCreate(db);
        }
    }

    public SQLite Open() throws SQLDataException {
        //open connection with database
        myHelper = new DbHelper(myContext);
        myDatabase = myHelper.getWritableDatabase();
        return this;
    }

    //close connection with database
    public void Close() {
        myHelper.close();
    }

    public long InsertData(String date, String speed,String time) throws SQLException {

        //put data in contentValue than insert conventValue in table
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, date);
        cv.put(KEY_MAXSPEED, speed);
        cv.put(KEY_TIME, time);

        return myDatabase.insert(TBL_NAME, null, cv);
    }

    public String getAll() {

        String data = "";
        String[] columns = new String[]{KEY_ROWID, KEY_DATE, KEY_MAXSPEED,KEY_TIME};
        Cursor c = myDatabase.query(TBL_NAME, columns, null, null, null, null, null);
        int iDate = c.getColumnIndex(KEY_DATE);
        int iSpeed = c.getColumnIndex(KEY_MAXSPEED);
        int iTime = c.getColumnIndex(KEY_TIME);
        int i = 0;
        if (c != null) {
            //get data from cursor one by one  from first to last row
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                i++;
                data += "\n" + i+".  " + c.getString(iDate) + "                  " + c.getString(iSpeed)+
                        "  km/h                  "+ c.getString(iTime);
            }

        }
        return data;
    }


    public void Delete() {
        myDatabase.execSQL("DROP TABLE " + TBL_NAME);
        myHelper.onCreate(myDatabase);
    }
}
