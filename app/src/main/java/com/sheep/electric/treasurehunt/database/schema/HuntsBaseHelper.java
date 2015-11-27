package com.sheep.electric.treasurehunt.database.schema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HuntsBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "huntsBase.db";

    public HuntsBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + HuntsDbSchema.HuntsTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            HuntsDbSchema.HuntsTable.Cols.UUID + "," +
            HuntsDbSchema.HuntsTable.Cols.NAME + "," +
            HuntsDbSchema.HuntsTable.Cols.LOCATION + "," +
            HuntsDbSchema.HuntsTable.Cols.CREATOR + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
