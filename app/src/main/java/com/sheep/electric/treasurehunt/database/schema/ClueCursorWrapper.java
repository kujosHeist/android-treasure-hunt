package com.sheep.electric.treasurehunt.database.schema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sheep.electric.treasurehunt.database.access.Clue;

import java.util.UUID;

public class ClueCursorWrapper extends CursorWrapper {

    public ClueCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Clue getClue(){
        String uuidString = getString(getColumnIndex(CluesDbSchema.CluesTable.Cols.UUID));
        String huntId = getString(getColumnIndex(CluesDbSchema.CluesTable.Cols.HUNT_ID));
        String clueText = getString(getColumnIndex(CluesDbSchema.CluesTable.Cols.CLUE_TEXT));
        String clueType = getString(getColumnIndex(CluesDbSchema.CluesTable.Cols.TYPE));
        String answer = getString(getColumnIndex(CluesDbSchema.CluesTable.Cols.ANSWER));
        String location = getString(getColumnIndex(CluesDbSchema.CluesTable.Cols.LOCATION));

        Clue clue = new Clue(UUID.fromString(uuidString));
        clue.setHuntId(huntId);
        clue.setClueText(clueText);
        clue.setClueType(clueType);
        clue.setClueAnswer(answer);
        clue.setClueLocation(location);

        return clue;

    }
}
