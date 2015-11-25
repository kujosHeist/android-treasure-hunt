package com.sheep.electric.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

public class SummaryActivity extends Activity {

    private static final String TAG = "SummaryActivity";
    private TextView mHuntNameTextView;
    private TextView mPlayerNameTextView;
    private TextView mTeamNameTextView;

    private Answers mAnswersDb;

    public static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Log.d(TAG, "count Summary activity: " + ++count);

        Intent intent = getIntent();
        String playerId = intent.getStringExtra(ClueDisplayActivity.PLAYER_ID);
        String huntId = intent.getStringExtra(ClueDisplayActivity.HUNT_ID);

        Hunts huntDb = new Hunts(this);
        Hunt hunt = huntDb.getHunt(UUID.fromString(huntId));

        mHuntNameTextView = (TextView) findViewById(R.id.hunt_name_text);
        mHuntNameTextView.setText(hunt.getName());

        Players playerDb = new Players(this);
        Player player = playerDb.getPlayer(UUID.fromString(playerId));

        mPlayerNameTextView = (TextView) findViewById(R.id.player_name_text);
        mPlayerNameTextView.setText(player.getName());

        mTeamNameTextView = (TextView) findViewById(R.id.team_name_text);
        mTeamNameTextView.setText(player.getTeam());

        mAnswersDb = new Answers(this);


        ArrayList<Answer> answers = (ArrayList<Answer>) mAnswersDb.getAnswers(UUID.fromString(playerId), UUID.fromString(huntId));

        Log.d(TAG, "Answers size: " + answers.size());

        if(answers != null){
            AnswersAdapter adapter = new AnswersAdapter(this, answers);
            ListView listView = (ListView) findViewById(R.id.answer_list_view);
            listView.setAdapter(adapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
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
