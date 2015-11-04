package com.sheep.electric.treasurehunt;


import android.content.Intent;

import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.CluesBaseHelper;

public class ClueDisplayActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = "ClueDisplayActivity";

    private Clue[] mClueBank;
    private int mCurrentClueIndex = 0;

    private ImageButton mArrowLeftButton;
    private ImageButton mArrowRightButton;
    private Button mTakePhotoButton;
    private TextView mClueTextView;
    private EditText mClueAnswer;
    private Button mSubmitLocationButton;

    private Button mSavePicture;
    private Button mSubmitAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_display);

        generateClues();

        mClueTextView = (TextView) findViewById(R.id.clue_text);
        mClueAnswer = (EditText) findViewById(R.id.clue_answer);
        mTakePhotoButton = (Button) findViewById(R.id.take_photo_button);
        mSubmitLocationButton = (Button) findViewById(R.id.submit_location_button);

        updateClue();

        mArrowLeftButton = (ImageButton) findViewById(R.id.arrow_left);
        mArrowRightButton = (ImageButton) findViewById(R.id.arrow_right);

        mArrowLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentClueIndex = (mCurrentClueIndex - 1) % mClueBank.length;
                if(mCurrentClueIndex < 0){
                    mCurrentClueIndex += mClueBank.length;
                }
                updateClue();
            }
        });

        mArrowRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentClueIndex = (mCurrentClueIndex + 1) % mClueBank.length;
                updateClue();
            }
        });
        // if you want to load map from fragment which is already defined in the layout
        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);
    }

    // temporary measure, will be declared in a db eventually
    public void generateClues(){
        Clues cluesDb = new Clues(this);

        if(cluesDb.getClues().size() == 0){
            Log.d(TAG, "Clue db is empty, adding clues from hunts.xml");
            addCluesFromXml();
        }

        Clues cluesDbase = new Clues(this);

        ArrayList<Clue> clueList = (ArrayList<Clue>) cluesDbase.getClues();

        mClueBank = clueList.toArray(new Clue[clueList.size()]);
    }

    private void addCluesFromXml()  {
        try {
            XMLPullParserHandler pullParserHandler = new XMLPullParserHandler(this);

            InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.hunts);

            boolean update = pullParserHandler.parse(inputStream);

            if(update){

                Clues db = new Clues(this);

                List<Clue> clues = db.getClues();

                for(Clue c: clues){
                    Log.i(TAG, "db: " + c.toString());
                }

                Hunts hdb = new Hunts(this);
                List<Hunt> hunts = hdb.getHunts();

                for(Hunt h: hunts){
                    Log.i(TAG, "db: " + h.toString());
                }
            }else{
                Log.e(TAG, "Update Failed");
            }

        }catch (Exception e){
            Log.e(TAG, "Cannot open file!!!!!!!");
            e.printStackTrace();
        }
    }

    public void updateClue(){
        Clue clue = mClueBank[mCurrentClueIndex];
        mClueTextView.setText(clue.getClueText());

        switch(clue.getClueType()) {
            case Clue.TEXT:
                mClueAnswer.setVisibility(EditText.VISIBLE);

                mTakePhotoButton.setVisibility(Button.GONE);
                mSubmitLocationButton.setVisibility(Button.GONE);

                break;
            case Clue.PICTURE:
                mTakePhotoButton.setVisibility(Button.VISIBLE);

                mClueAnswer.setVisibility(EditText.GONE);
                mSubmitLocationButton.setVisibility(Button.GONE);
                break;

            case Clue.LOCATION:
                mSubmitLocationButton.setVisibility(Button.VISIBLE);

                mClueAnswer.setVisibility(EditText.GONE);
                mTakePhotoButton.setVisibility(Button.GONE);

        }
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

    // Camera code ******************************************************************************
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri;

    public void takePhoto(View view){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                  fileUri.toString(), Toast.LENGTH_LONG).show();

                Bitmap image = BitmapFactory.decodeFile(fileUri.getPath());
                final ImageView imageView = (ImageView) findViewById(R.id.captured_picture);
                imageView.setImageBitmap(image);

                galleryAddPic();   // not working

                ViewGroup layout = (ViewGroup) findViewById(R.id.button_layout);
                Button bt = new Button(this);
                bt.setText("Save");

                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Image Saved", Toast.LENGTH_LONG).show();
                    }
                });

                Button bt2 = new Button(this);
                bt2.setText("Delete");

                bt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.setImageBitmap(null);
                        File file = new File(fileUri.toString());
                        file.delete();
                        Toast.makeText(v.getContext(), "Image Deleted", Toast.LENGTH_LONG).show();
                    }
                });

                layout.addView(bt);
                layout.addView(bt2);

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    // currently not working, not saving to the phones gallery
    private void galleryAddPic() {

        File file = new File(fileUri.getPath());
        Uri content = Uri.fromFile(file);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, content);

        this.sendBroadcast(mediaScanIntent);
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        System.out.println("SD Mounted: " + Environment.getExternalStorageState());

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TreasureHunt");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("ClueActivityActivity", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    // **************************************************

    SupportMapFragment mapsActivity;
    android.support.v4.app.FragmentManager fm;
    android.support.v4.app.FragmentTransaction ft;

    // opens up seperate activity and displays it
    public void openMap(View view){

        if(mapsActivity == null){
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();

            mapsActivity = new SupportMapFragment();
            mapsActivity.getMapAsync(this);

            ft.replace(R.id.mapOrCameraDisplay,  mapsActivity, "tag");
            ft.commit();
        }else{

            ft = fm.beginTransaction();
            ft.remove(mapsActivity);
            mapsActivity = null;
            ft.commit();
            /*
            //mapsActivity.getMapAsync(this);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            int fragId = fm.findFragmentByTag("tag").getId();

            ft.replace(fragId,  blankFrameLayout);
            System.out.println("Replacing with tag: " + fragId);
            */

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(53.307262,-6.219077);
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        googleMap.addMarker(new MarkerOptions().title("Sydney").snippet(("Most people in Oz")).position(sydney));
    }
}
