package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.HuntsDbSchema.HuntsTable;

/**
 * Created by Shane on 03/11/2015.
 */
public class HuntsBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "huntsBase.db";

    public HuntsBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + HuntsTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            HuntsTable.Cols.UUID + "," +
            HuntsTable.Cols.NAME + "," +
            HuntsTable.Cols.LOCATION + "," +
            HuntsTable.Cols.CREATOR + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
