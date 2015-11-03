package com.sheep.electric.treasurehunt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.PlayerCursorWrapper;
import database.PlayersBaseHelper;
import database.PlayersDbSchema;
import database.PlayersDbSchema.PlayersTable;

/**
 * Created by Shane on 02/11/2015.
 */
public class Players {

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    private String mName;

    public Players(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new PlayersBaseHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Player player) {
        ContentValues values = new ContentValues();
        values.put(PlayersTable.Cols.UUID, player.getId().toString());
        values.put(PlayersTable.Cols.NAME, player.getName());
        values.put(PlayersTable.Cols.TEAM, player.getTeam().toString());
        values.put(PlayersTable.Cols.HUNT, player.getHunt().toString());

        return values;
    }

    public void addPlayer(Player player){
        ContentValues values = getContentValues(player);
        mDatabase.insert(PlayersTable.NAME, null, values);
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

    public Player getPlayer(UUID uuid){

        PlayerCursorWrapper cursor = queryPlayers(
                PlayersTable.Cols.UUID + " = ?",
                new String[] {uuid.toString()}
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

}
