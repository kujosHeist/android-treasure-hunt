package com.sheep.electric.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TeamSelectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        TextView textView = (TextView) findViewById(R.id.user_name_display);

        String huntName = intent.getStringExtra("huntName");
        TextView textView1 = (TextView) findViewById(R.id.hunt_selected);


        textView.setText(userName);
        textView1.setText(huntName);
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
