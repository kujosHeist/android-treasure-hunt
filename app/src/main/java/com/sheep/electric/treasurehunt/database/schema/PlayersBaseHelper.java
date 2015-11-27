package com.sheep.electric.treasurehunt.database.schema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PlayersBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "playerBase.db";

    public PlayersBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PlayersDbSchema.PlayersTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                PlayersDbSchema.PlayersTable.Cols.UUID + "," +
                PlayersDbSchema.PlayersTable.Cols.NAME + "," +
                PlayersDbSchema.PlayersTable.Cols.TEAM + "," +
                PlayersDbSchema.PlayersTable.Cols.HUNT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
