package database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;

import com.sheep.electric.treasurehunt.Answer;

import java.util.UUID;

import database.AnswersDbSchema.AnswersTable;

/**
 * Created by Shane on 09/11/2015.
 */
public class AnswerCursorWrapper extends CursorWrapper{
    private static final String TAG = "AnswerCursorWrapper";

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public AnswerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Answer getAnswer(){
        String uuidString = getString(getColumnIndex(AnswersTable.Cols.UUID));
        String clueId = getString(getColumnIndex(AnswersTable.Cols.CLUE_ID));
        String playerId = getString(getColumnIndex(AnswersTable.Cols.PLAYER_ID));
        String huntId = getString(getColumnIndex(AnswersTable.Cols.HUNT_ID));
        String pictureUri = getString(getColumnIndex(AnswersTable.Cols.PICTURE_URI));
        String text = getString(getColumnIndex(AnswersTable.Cols.TEXT));
        String location = getString(getColumnIndex(AnswersTable.Cols.LOCATION));

        Answer answer = new Answer(UUID.fromString(uuidString), UUID.fromString(clueId), UUID.fromString(playerId), UUID.fromString(huntId), Uri.parse(pictureUri), text, location);

        return answer;
    }
}
