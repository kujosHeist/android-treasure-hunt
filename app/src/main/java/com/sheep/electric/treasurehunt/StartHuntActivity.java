package com.sheep.electric.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class StartHuntActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private String mHuntSelected;


    private static final String DEFAULT_TEAM_NAME = "Alpha";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_hunt);

        Button createHuntButton = (Button) findViewById(R.id.create_hunt_button);
        createHuntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ClueDisplayActivity.class);
                startActivity(intent);
            }
        });

        // set up the drop down list of the hunts to choose from
        Spinner spinner = (Spinner) findViewById(R.id.hunt_spinner);
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

    public void startHunt(View view){
        Intent intent = new Intent(this, TeamSelectActivity.class);

        EditText editText = (EditText) findViewById(R.id.user_name);
        String userName = editText.getText().toString();

        Player player = new Player(userName, mHuntSelected, DEFAULT_TEAM_NAME);

        Players playersDb = new Players(this);
        playersDb.addPlayer(player);

        String uuid = player.getId().toString();

        intent.putExtra("playerId", uuid);
        startActivity(intent);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        mHuntSelected = (String) parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
