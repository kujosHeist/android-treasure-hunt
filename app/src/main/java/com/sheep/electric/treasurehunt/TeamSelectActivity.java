package com.sheep.electric.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;

public class TeamSelectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);

        Intent intent = getIntent();
        //String userName = intent.getStringExtra("userName");

        String playerID = intent.getStringExtra("playerId");
        TextView textView = (TextView) findViewById(R.id.user_name_display);

        TextView textView1 = (TextView) findViewById(R.id.hunt_selected);

        Players playersDb = new Players(this);
        Player player = playersDb.getPlayer(UUID.fromString(playerID));


        textView.setText(player.getName());
        textView1.setText(player.getHunt());
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

    public void startGame(View view){
        Intent intent = new Intent(this, ClueDisplayActivity.class);
        startActivity(intent);
    }
}
