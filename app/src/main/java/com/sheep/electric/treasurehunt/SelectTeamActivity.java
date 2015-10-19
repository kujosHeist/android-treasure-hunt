package com.sheep.electric.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by fergus on 18/10/2015.
 */
public class SelectTeamActivity extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team);
    }

    public void selectTeam(View view){
        Intent intent = new Intent(this, TeamOptionsActivity.class);
        startActivity(intent);
    }


}
