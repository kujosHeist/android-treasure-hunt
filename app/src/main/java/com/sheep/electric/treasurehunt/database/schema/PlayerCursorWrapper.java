package com.sheep.electric.treasurehunt.database.schema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sheep.electric.treasurehunt.database.access.Player;

import java.util.UUID;

public class PlayerCursorWrapper extends CursorWrapper {

    public PlayerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Player getPlayer(){
        String uuidString = getString(getColumnIndex(PlayersDbSchema.PlayersTable.Cols.UUID));
        String name = getString(getColumnIndex(PlayersDbSchema.PlayersTable.Cols.NAME));
        String team = getString(getColumnIndex(PlayersDbSchema.PlayersTable.Cols.TEAM));
        String hunt = getString(getColumnIndex(PlayersDbSchema.PlayersTable.Cols.HUNT));

        Player player = new Player(UUID.fromString(uuidString));
        player.setName(name);
        player.setTeam(team);
        player.setHunt(hunt);

        return player;
    }




}
