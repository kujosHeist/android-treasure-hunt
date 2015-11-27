package com.sheep.electric.treasurehunt.database.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sheep.electric.treasurehunt.database.schema.HuntCursorWrapper;
import com.sheep.electric.treasurehunt.database.schema.HuntsBaseHelper;
import com.sheep.electric.treasurehunt.database.schema.HuntsDbSchema.HuntsTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// used to access db
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
                HuntsTable.Cols.UUID + " = ?",
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

    public Hunt getHunt(String huntName){
        Log.d("Hunts", "Looking for hunt: " + huntName);
        HuntCursorWrapper cursor = queryHunts(
                HuntsTable.Cols.NAME + " = ?",
                new String[]{huntName}
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
