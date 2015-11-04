package com.sheep.electric.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class TeamSelectActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);

        Intent intent = getIntent();

        String playerID = intent.getStringExtra("playerId");
        TextView userNameTextView = (TextView) findViewById(R.id.user_name_textview);

        TextView huntNameTextView = (TextView) findViewById(R.id.hunt_name_textview);

        Players playersDb = new Players(this);
        Player player = playersDb.getPlayer(UUID.fromString(playerID));

        userNameTextView.setText(player.getName());
        huntNameTextView.setText(player.getHunt());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
