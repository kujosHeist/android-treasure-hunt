package com.sheep.electric.treasurehunt.database.schema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sheep.electric.treasurehunt.database.access.Hunt;

import java.util.UUID;

public class HuntCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public HuntCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Hunt getHunt(){
        String uuidString = getString(getColumnIndex(HuntsDbSchema.HuntsTable.Cols.UUID));
        String name = getString(getColumnIndex(HuntsDbSchema.HuntsTable.Cols.NAME));
        String location = getString(getColumnIndex(HuntsDbSchema.HuntsTable.Cols.LOCATION));
        String creator = getString(getColumnIndex(HuntsDbSchema.HuntsTable.Cols.CREATOR));

        Hunt hunt = new Hunt(UUID.fromString(uuidString));
        hunt.setName(name);
        hunt.setLocation(location);
        hunt.setCreator(creator);

        return hunt;
    }
}
