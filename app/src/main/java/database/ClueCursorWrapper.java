package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sheep.electric.treasurehunt.Clue;

import java.util.UUID;

import database.CluesDbSchema.CluesTable;

/**
 * Created by Shane on 03/11/2015.
 */
public class ClueCursorWrapper extends CursorWrapper {

    public ClueCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Clue getClue(){
        String uuidString = getString(getColumnIndex(CluesTable.Cols.UUID));
        String huntId = getString(getColumnIndex(CluesTable.Cols.HUNT_ID));
        String clueText = getString(getColumnIndex(CluesTable.Cols.CLUE_TEXT));
        String clueType = getString(getColumnIndex(CluesTable.Cols.TYPE));
        String answer = getString(getColumnIndex(CluesTable.Cols.ANSWER));
        String location = getString(getColumnIndex(CluesTable.Cols.LOCATION));

        Clue clue = new Clue(UUID.fromString(uuidString));
        clue.setHuntId(huntId);
        clue.setClueText(clueText);
        clue.setClueType(clueType);
        clue.setClueAnswer(answer);
        clue.setClueLocation(location);

        return clue;

    }
}
