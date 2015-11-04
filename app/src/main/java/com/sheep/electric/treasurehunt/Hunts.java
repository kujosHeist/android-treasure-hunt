package com.sheep.electric.treasurehunt;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.HuntCursorWrapper;
import database.HuntsBaseHelper;
import database.HuntsDbSchema;
import database.HuntsDbSchema.HuntsTable;
import database.PlayersDbSchema;
import database.PlayersDbSchema.PlayersTable;

/**
 * Created by Shane on 03/11/2015.
 */
public class Hunts {
    public SQLiteDatabase mDatabase;
    public String mName;

    public Hunts(Context context){
        mDatabase = new HuntsBaseHelper(context.getApplicationContext()).getWritableDatabase();
    }

    public void addHunt(Hunt hunt){
        ContentValues values = getContentValues(hunt);
        mDatabase.insert(HuntsTable.NAME, null, values);
    }

    public Hunt getHunt(UUID uuid){
        HuntCursorWrapper cursor = queryHunts(
                HuntsTable.Cols.NAME + " = ?",
                new String[]{uuid.toString()}
        );
        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getHunt();
        }finally {
            cursor.close();
        }
    }

    public List<Hunt> getHunts(){
        List<Hunt> hunts = new ArrayList<Hunt>();

        HuntCursorWrapper cursor = queryHunts(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                hunts.add(cursor.getHunt());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }

        return hunts;
    }

    private static ContentValues getContentValues(Hunt hunt) {
        ContentValues values = new ContentValues();
        values.put(HuntsTable.Cols.UUID, hunt.getId().toString());
        values.put(HuntsTable.Cols.NAME, hunt.getName());
        values.put(HuntsTable.Cols.LOCATION, hunt.getLocation());
        values.put(HuntsTable.Cols.CREATOR, hunt.getCreator());

        return values;
    }

    public HuntCursorWrapper queryHunts(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                HuntsTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new HuntCursorWrapper(cursor);
    }
}
