package com.sheep.electric.treasurehunt.database.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.sheep.electric.treasurehunt.database.schema.AnswerCursorWrapper;
import com.sheep.electric.treasurehunt.database.schema.AnswersBaseHelper;
import com.sheep.electric.treasurehunt.database.schema.AnswersDbSchema.AnswersTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// used to access db
public class Answers {

    public static final String TAG = "ANSWERS";
    SQLiteDatabase mDatabase;

    public Answers(Context context){
        mDatabase = new AnswersBaseHelper(context).getWritableDatabase();
    }

    public void addAnswer(Answer answer){
        ContentValues contentValues = getContentValues(answer);
        mDatabase.insert(AnswersTable.NAME, null, contentValues);
    }

    public Answer getAnswer(UUID uuid){
        AnswerCursorWrapper cursor = queryAnswers(
                AnswersTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getAnswer();
        }finally {
            cursor.close();
        }
    }

    public List<Answer> getAnswerPlayer(UUID uuid){

        ArrayList<Answer> answers = new ArrayList<Answer>();
        AnswerCursorWrapper cursor = queryAnswers(
                AnswersTable.Cols.PLAYER_ID + " = ?",
                new String[]{uuid.toString()}
        );

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                answers.add(cursor.getAnswer());
                cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }
        return answers;
    }

    public List<Answer> getAnswers(){
        List<Answer> answers = new ArrayList<Answer>();

        AnswerCursorWrapper cursor = queryAnswers(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                answers.add(cursor.getAnswer());
                cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }
        return answers;
    }


    public List<Answer> getAnswers(UUID huntId){
        List<Answer> answers = new ArrayList<Answer>();

        AnswerCursorWrapper cursor = queryAnswers(
                AnswersTable.Cols.HUNT_ID + " = ?",
                new String[]{ huntId.toString() }
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                answers.add(cursor.getAnswer());
                cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }
        return answers;
    }



    public List<Answer> getAnswers(UUID playerId, UUID huntId){
        List<Answer> answers = new ArrayList<Answer>();

        AnswerCursorWrapper cursor = queryAnswers(
                AnswersTable.Cols.PLAYER_ID + " = ? AND " + AnswersTable.Cols.HUNT_ID + " = ?",
                new String[]{playerId.toString(), huntId.toString() }
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                answers.add(cursor.getAnswer());
                cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }
        return answers;
    }

    private ContentValues getContentValues(Answer answer) {
        ContentValues values = new ContentValues();

        values.put(AnswersTable.Cols.UUID, answer.getId().toString());
        values.put(AnswersTable.Cols.CLUE_ID, answer.getClueId().toString());
        values.put(AnswersTable.Cols.PLAYER_ID, answer.getPlayerId().toString());
        values.put(AnswersTable.Cols.HUNT_ID, answer.getHuntId().toString());

        Uri picUri = answer.getPictureUri();
        if(picUri != null){
            values.put(AnswersTable.Cols.PICTURE_URI, picUri.toString());
        }else{
            values.put(AnswersTable.Cols.PICTURE_URI, "");
        }


        values.put(AnswersTable.Cols.TEXT, answer.getText());
        values.put(AnswersTable.Cols.LOCATION, answer.getLocation());

        return values;
    }

    private AnswerCursorWrapper queryAnswers(String whereClause, String[] whereArgs){
        Log.d(TAG, "Where Clause: " + whereClause);
        Log.d(TAG, "Where Args: " + Arrays.toString(whereArgs));
        Cursor cursor = mDatabase.query(
                AnswersTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new AnswerCursorWrapper(cursor);
    }

    public static int getNumberOfCorrectAnswers(ArrayList<Answer> answers, Clues cluesDb){

        int answersCorrect = 0;

        for(Answer answer: answers){
            answer.setResult(cluesDb.getClue(answer.getClueId()));
            if(answer.getResult()){
                answersCorrect++;
            }
        }

        return answersCorrect;
    }

}
