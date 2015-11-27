package com.sheep.electric.treasurehunt.database.schema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class AnswersBaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "answerBase.db";
    public static final int VERSION = 1;

    public AnswersBaseHelper(Context context ) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AnswersDbSchema.AnswersTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        AnswersDbSchema.AnswersTable.Cols.UUID + "," +
                        AnswersDbSchema.AnswersTable.Cols.CLUE_ID + "," +
                        AnswersDbSchema.AnswersTable.Cols.PLAYER_ID + "," +
                        AnswersDbSchema.AnswersTable.Cols.HUNT_ID + "," +
                        AnswersDbSchema.AnswersTable.Cols.PICTURE_URI + "," +
                        AnswersDbSchema.AnswersTable.Cols.TEXT + "," +
                        AnswersDbSchema.AnswersTable.Cols.LOCATION + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
