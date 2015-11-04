package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sheep.electric.treasurehunt.Hunt;

import java.util.UUID;

import database.HuntsDbSchema.HuntsTable;

/**
 * Created by Shane on 03/11/2015.
 */
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
        String uuidString = getString(getColumnIndex(HuntsTable.Cols.UUID));
        String name = getString(getColumnIndex(HuntsTable.Cols.NAME));
        String location = getString(getColumnIndex(HuntsTable.Cols.LOCATION));
        String creator = getString(getColumnIndex(HuntsTable.Cols.CREATOR));

        Hunt hunt = new Hunt(UUID.fromString(uuidString));
        hunt.setName(name);
        hunt.setLocation(location);
        hunt.setCreator(creator);

        return hunt;
    }
}
