package com.sheep.electric.treasurehunt;

import android.app.Activity;
<<<<<<< HEAD:app/src/main/java/com/sheep/electric/treasurehunt/CreateHuntActivity.java
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
=======
>>>>>>> a342fb2a72fd619845d8e1bee31612836762a19b:app/src/main/java/com/sheep/electric/treasurehunt/ClueDisplayActivty.java
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ClueDisplayActivty extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_display);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clue_display, menu);
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

<<<<<<< HEAD:app/src/main/java/com/sheep/electric/treasurehunt/CreateHuntActivity.java

=======
    public void openMap(View view){}

    public void takePhoto(View view){}
>>>>>>> a342fb2a72fd619845d8e1bee31612836762a19b:app/src/main/java/com/sheep/electric/treasurehunt/ClueDisplayActivty.java
}
