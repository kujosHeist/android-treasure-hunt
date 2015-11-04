package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.CluesDbSchema.CluesTable;

/**
 * Created by Shane on 03/11/2015.
 */
public class CluesBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cluesBase.db";
    public static final int VERSION = 1;

    public CluesBaseHelper(Context context ) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CluesTable.NAME + "(" +
        " _id integer primary key autoincrement, " +
                        CluesTable.Cols.UUID + "," +
                        CluesTable.Cols.HUNT_ID + "," +
                        CluesTable.Cols.CLUE_TEXT + "," +
                        CluesTable.Cols.TYPE + "," +
                        CluesTable.Cols.ANSWER + "," +
                        CluesTable.Cols.LOCATION + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
