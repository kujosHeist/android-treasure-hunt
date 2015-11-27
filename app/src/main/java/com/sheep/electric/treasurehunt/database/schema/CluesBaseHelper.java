package com.sheep.electric.treasurehunt.database.schema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class CluesBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cluesBase.db";
    public static final int VERSION = 1;

    public CluesBaseHelper(Context context ) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CluesDbSchema.CluesTable.NAME + "(" +
        " _id integer primary key autoincrement, " +
                        CluesDbSchema.CluesTable.Cols.UUID + "," +
                        CluesDbSchema.CluesTable.Cols.HUNT_ID + "," +
                        CluesDbSchema.CluesTable.Cols.CLUE_TEXT + "," +
                        CluesDbSchema.CluesTable.Cols.TYPE + "," +
                        CluesDbSchema.CluesTable.Cols.ANSWER + "," +
                        CluesDbSchema.CluesTable.Cols.LOCATION + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
