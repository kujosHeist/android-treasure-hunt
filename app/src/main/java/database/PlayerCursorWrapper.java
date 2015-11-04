package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sheep.electric.treasurehunt.Player;

import java.util.UUID;

import database.PlayersDbSchema.PlayersTable;

/**
 * Created by Shane on 03/11/2015.
 */
public class PlayerCursorWrapper extends CursorWrapper {

    public PlayerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Player getPlayer(){
        String uuidString = getString(getColumnIndex(PlayersTable.Cols.UUID));
        String name = getString(getColumnIndex(PlayersTable.Cols.NAME));
        String team = getString(getColumnIndex(PlayersTable.Cols.TEAM));
        String hunt = getString(getColumnIndex(PlayersTable.Cols.HUNT));

        Player player = new Player(UUID.fromString(uuidString));
        player.setName(name);
        player.setTeam(team);
        player.setHunt(hunt);

        return player;
    }




}
