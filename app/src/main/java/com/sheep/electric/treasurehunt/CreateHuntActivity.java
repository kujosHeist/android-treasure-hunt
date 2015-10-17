package com.sheep.electric.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class CreateHuntActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hunt);
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

    public void createHunt(View view){
        Intent intent = new Intent(this, TeamSelectActivity.class);

        EditText editText = (EditText) findViewById(R.id.user_name);
        String userName = editText.getText().toString();
        intent.putExtra("userName", userName);

        editText = (EditText) findViewById(R.id.hunt_name);
        String huntName = editText.getText().toString();
        intent.putExtra("huntName", huntName);

        editText = (EditText) findViewById(R.id.hunt_location);
        String huntLocation = editText.getText().toString();
        intent.putExtra("huntLocation", huntLocation);  // should use static final strings for key



        startActivity(intent);



    }
}
