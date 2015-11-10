package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.AnswersDbSchema.AnswersTable;

/**
 * Created by Shane on 09/11/2015.
 */
public class AnswersBaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "answerBase.db";
    public static final int VERSION = 1;

    public AnswersBaseHelper(Context context ) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AnswersTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        AnswersTable.Cols.UUID + "," +
                        AnswersTable.Cols.CLUE_ID + "," +
                        AnswersTable.Cols.PLAYER_ID + "," +
                        AnswersTable.Cols.HUNT_ID + "," +
                        AnswersTable.Cols.PICTURE_URI + "," +
                        AnswersTable.Cols.TEXT + "," +
                        AnswersTable.Cols.LOCATION + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
