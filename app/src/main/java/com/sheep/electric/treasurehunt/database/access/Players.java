package com.sheep.electric.treasurehunt.database.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sheep.electric.treasurehunt.database.schema.PlayerCursorWrapper;
import com.sheep.electric.treasurehunt.database.schema.PlayersBaseHelper;
import com.sheep.electric.treasurehunt.database.schema.PlayersDbSchema.PlayersTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// used to access players db, not really used
public class Players {
    private SQLiteDatabase mDatabase;
    private String mName;

    public Players(Context context){
        mDatabase = new PlayersBaseHelper(context.getApplicationContext()).getWritableDatabase();
    }

    public void addPlayer(Player player){
        ContentValues values = getContentValues(player);
        mDatabase.insert(PlayersTable.NAME, null, values);
    }

    public Player getPlayer(UUID uuid){
        PlayerCursorWrapper cursor = queryPlayers(
                PlayersTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );
        try{
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getPlayer();
        }finally {
            cursor.close();
        }
    }

    public List<Player> getPlayers(){
        List<Player> players = new ArrayList<Player>();

        PlayerCursorWrapper cursor = queryPlayers(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                players.add(cursor.getPlayer());
                cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }
        return players;
    }

    public PlayerCursorWrapper queryPlayers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PlayersTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new PlayerCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Player player) {
        ContentValues values = new ContentValues();
        values.put(PlayersTable.Cols.UUID, player.getId().toString());
        values.put(PlayersTable.Cols.NAME, player.getName());
        values.put(PlayersTable.Cols.TEAM, player.getTeam().toString());
        values.put(PlayersTable.Cols.HUNT, player.getHunt().toString());

        return values;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
