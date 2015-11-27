package com.sheep.electric.treasurehunt.database.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sheep.electric.treasurehunt.database.schema.ClueCursorWrapper;
import com.sheep.electric.treasurehunt.database.schema.CluesBaseHelper;
import com.sheep.electric.treasurehunt.database.schema.CluesDbSchema.CluesTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


// used to access db
public class Clues {
    private SQLiteDatabase mDatabase;

    public Clues(Context context){
        mDatabase = new CluesBaseHelper(context).getWritableDatabase();
    }


    public void addClue(Clue clue){
        ContentValues values = getContentValues(clue);
        mDatabase.insert(CluesTable.NAME, null, values);

    }

    public Clue getClue(UUID uuid){
        ClueCursorWrapper cursor = queryClues(
                CluesTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getClue();
        }finally {
            cursor.close();
        }
    }

    public List<Clue> getClues(){
        List<Clue> clues = new ArrayList<Clue>();

        ClueCursorWrapper cursor = queryClues(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                clues.add(cursor.getClue());
                cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }
        return clues;
    }

    public List<Clue> getClues(UUID huntId){
        List<Clue> clues = new ArrayList<Clue>();

        ClueCursorWrapper cursor = queryClues(
                CluesTable.Cols.HUNT_ID + " =  ?",
                new String[]{huntId.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                clues.add(cursor.getClue());
                cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }
        return clues;
    }

    private ContentValues getContentValues(Clue clue) {
        ContentValues values = new ContentValues();

        values.put(CluesTable.Cols.UUID, clue.getId().toString());
        values.put(CluesTable.Cols.HUNT_ID, clue.getHuntId().toString());
        values.put(CluesTable.Cols.CLUE_TEXT, clue.getClueText());
        values.put(CluesTable.Cols.TYPE, clue.getClueType());
        values.put(CluesTable.Cols.ANSWER, clue.getClueAnswer());
        values.put(CluesTable.Cols.LOCATION, clue.getClueLocation());

        return values;

    }

    private ClueCursorWrapper queryClues(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CluesTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ClueCursorWrapper(cursor);
    }


}
