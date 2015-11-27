package com.sheep.electric.treasurehunt.database.schema;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;

import com.sheep.electric.treasurehunt.database.access.Answer;

import java.util.UUID;

/**
 * Created by Shane on 09/11/2015.
 */
public class AnswerCursorWrapper extends CursorWrapper{
    private static final String TAG = "AnswerCursorWrapper";

    public AnswerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Answer getAnswer(){
        String uuidString = getString(getColumnIndex(AnswersDbSchema.AnswersTable.Cols.UUID));
        String clueId = getString(getColumnIndex(AnswersDbSchema.AnswersTable.Cols.CLUE_ID));
        String playerId = getString(getColumnIndex(AnswersDbSchema.AnswersTable.Cols.PLAYER_ID));
        String huntId = getString(getColumnIndex(AnswersDbSchema.AnswersTable.Cols.HUNT_ID));
        String pictureUri = getString(getColumnIndex(AnswersDbSchema.AnswersTable.Cols.PICTURE_URI));
        String text = getString(getColumnIndex(AnswersDbSchema.AnswersTable.Cols.TEXT));
        String location = getString(getColumnIndex(AnswersDbSchema.AnswersTable.Cols.LOCATION));

        Answer answer = new Answer(UUID.fromString(uuidString), UUID.fromString(clueId), UUID.fromString(playerId), UUID.fromString(huntId), Uri.parse(pictureUri), text, location);

        return answer;
    }
}
