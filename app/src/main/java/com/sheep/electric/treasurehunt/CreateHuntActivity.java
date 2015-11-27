package com.sheep.electric.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sheep.electric.treasurehunt.database.access.Player;
import com.sheep.electric.treasurehunt.database.access.Players;

public class CreateHuntActivity extends Activity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = "CreateHuntActivity";
    private String mHuntSelected;

    private Button mCreateHuntButton;

    public static final String PLAYER_ID = "CreateHuntActivity.PLAYER_ID";
    public static final String HUNT_NAME = "CreateHuntActivity.HUNT_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hunt);

        mCreateHuntButton = (Button) findViewById(R.id.start_hunt_button);
        mCreateHuntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ClueDisplayActivity.class);

                EditText userNameText = (EditText) findViewById(R.id.enter_user_name_edit);
                String userName = userNameText.getText().toString();

                EditText teamNameText = (EditText) findViewById(R.id.enter_team_name_edit);
                String teamName = teamNameText.getText().toString();

                // create new player
                Player player = new Player(userName, mHuntSelected, teamName);
                Players playersDb = new Players(v.getContext());
                playersDb.addPlayer(player);

                String playerId = player.getId().toString();

                intent.putExtra(PLAYER_ID, playerId);
                intent.putExtra(HUNT_NAME, mHuntSelected);

                Log.d(TAG, "Player ID: " + playerId);
                Log.d(TAG, "Hunt Name: " + mHuntSelected);
                startActivity(intent);
                finish();
            }
        });

        // set up the drop down list of the hunts to choose from
        Spinner spinner = (Spinner) findViewById(R.id.select_hunt_spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.hunt_list, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_hunt, menu);
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        mHuntSelected = (String) parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
