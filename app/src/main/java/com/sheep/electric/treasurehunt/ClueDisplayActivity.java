package com.sheep.electric.treasurehunt;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.sheep.electric.treasurehunt.database.access.Answer;
import com.sheep.electric.treasurehunt.database.access.Answers;
import com.sheep.electric.treasurehunt.database.access.Clue;
import com.sheep.electric.treasurehunt.database.access.Clues;
import com.sheep.electric.treasurehunt.database.access.Hunt;
import com.sheep.electric.treasurehunt.database.access.Hunts;
import com.sheep.electric.treasurehunt.util.XMLPullParserHandler;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class ClueDisplayActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "ClueDisplayActivity";
    public static final String PLAYER_ID = "ClueDisplayActivity.PLAYER_ID";
    public static final String HUNT_ID = "ClueDisplayActivity.HUNT_ID";
    public static final LatLng UCD_LAT_LONG = new LatLng(53.3069227,-6.2234008);
    public static final int MAP_ZOOM_LEVEL = 15;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private LinearLayout mLocationLayout;
    private LinearLayout mAnswerTextLayout;
    private ImageButton mArrowLeftButton;
    private ImageButton mArrowRightButton;
    private ImageButton mTakeOrDeletePhotoButton;
    private ImageButton mGetOrDeleteLocationButton;
    private Button mShowOnMap;
    private Button mSubmitAnswerButton;
    private ImageView mImageView;
    private TextView mClueTextView;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    private Answers mClueAnswers;
    private Location mLocation;
    private Clues mCluesDb;
    private UUID mHuntId;
    private UUID mPlayerId;
    private Uri mFileUri;
    private int mCurrentClueIndex = 0;

    private ArrayList<LatLng> mCheckedInLocations;
    private ArrayList<Clue> mClueBank;

    public GoogleApiClient mGoogleApiClient;
    public SupportMapFragment mapsActivity;
    public android.support.v4.app.FragmentManager fm;
    public android.support.v4.app.FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_display);

        // instantiate all the various views which need to be accessed
        mLocationLayout = (LinearLayout) findViewById(R.id.location_layout);
        mAnswerTextLayout = (LinearLayout) findViewById(R.id.answer_text_layout);
        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);

        mShowOnMap = (Button) findViewById(R.id.show_on_map_button);
        mClueTextView = (TextView) findViewById(R.id.clue_text);

        mTakeOrDeletePhotoButton = (ImageButton) findViewById(R.id.take_picture_button);
        mGetOrDeleteLocationButton = (ImageButton) findViewById(R.id.get_location_button);
        mArrowLeftButton = (ImageButton) findViewById(R.id.arrow_left);
        mArrowRightButton = (ImageButton) findViewById(R.id.arrow_right);
        mSubmitAnswerButton = (Button) findViewById(R.id.submit_answer_button);
        mImageView = (ImageView) findViewById(R.id.captured_picture);


        mCheckedInLocations = new ArrayList<LatLng>();
        mCluesDb = new Clues(this);
        buildGoogleApiClient();   // builds the location service for use later

        // retrieve the intent which started the activity
        Intent intent = getIntent();
        String playerIdString = intent.getStringExtra(CreateHuntActivity.PLAYER_ID);
        String huntName = intent.getStringExtra(CreateHuntActivity.HUNT_NAME);

        // gets access to the required database's
        mPlayerId = UUID.fromString(playerIdString);
        mHuntId = populateClueBank(huntName);   // creates hunt if it doesn't already exist, returns hunt id
        mClueAnswers = new Answers(this);

        // displays the first clue
        updateClue();


        // Set the various click listeners to respond to the users actions ******************//
        // gets the users location and displays it on screen
        mGetOrDeleteLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocation == null) {
                    mGoogleApiClient.connect();
                    mGetOrDeleteLocationButton.setImageResource(R.drawable.ic_delete_black_24dp);
                } else {
                    mLatitudeTextView.setText(R.string.latitude_text);
                    mLongitudeTextView.setText(R.string.longitude_text);
                    mGetOrDeleteLocationButton.setImageResource(R.drawable.ic_add_location_black_24dp);
                    mLocation = null;
                }
            }
        });

        // if user selects the take photo button
        mTakeOrDeletePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMap();  // if open, map gets closed
                if (mFileUri == null) {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri); // set the image file name

                    // start the image capture Intent
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                } else {
                    deleteImage();
                    Toast.makeText(v.getContext(), "Image Deleted", Toast.LENGTH_LONG).show();
                    mTakeOrDeletePhotoButton.setImageResource(R.drawable.ic_camera_enhance_black_24dp);
                }
            }
        });

        // opens up the map in the bottom half of the screen
        mShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        // user can scroll through the clues the arrow buttons
        mArrowLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentClueIndex = (mCurrentClueIndex - 1) % mClueBank.size();
                if (mCurrentClueIndex < 0) {
                    mCurrentClueIndex += mClueBank.size();
                }
                cleanUp();   // closes various views and resets edit box's etc
            }
        });

        mArrowRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentClueIndex = (mCurrentClueIndex + 1) % mClueBank.size();
                cleanUp();
            }
        });

        // handles the user submitting their answer
        mSubmitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // First gets the clue from the db
                Clue clue = mClueBank.get(mCurrentClueIndex);
                // create new answer ready to be stored in db
                Answer answer = new Answer(clue.getId(), mPlayerId, mHuntId);

                // has different behaviour depending on the clue type
                switch (clue.getClueType()) {
                    case Clue.TEXT:
                        EditText clueAnswerEditText = (EditText) findViewById(R.id.clue_answer_edit_text);
                        String clueAnswer = clueAnswerEditText.getText().toString();

                        if(clueAnswer.length() != 0){
                            clueAnswerEditText.setText("");
                            answer.setText(clueAnswer);
                            answer.setPictureUri(null);
                            answer.setLocation(null);
                            mClueBank.remove(mCurrentClueIndex);
                            break;
                        }else{
                            Toast.makeText(v.getContext(), "Must enter answer first", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Must enter answer first");
                            return;
                        }

                    case Clue.PICTURE:
                        // if the user has taken a photo
                        if (mFileUri != null) {
                            answer.setPictureUri(mFileUri);
                            answer.setText(null);
                            answer.setLocation(null);

                            mClueBank.remove(mCurrentClueIndex);
                            mTakeOrDeletePhotoButton.setImageResource(R.drawable.ic_camera_enhance_black_24dp);

                            mFileUri = null;
                            mImageView.setImageBitmap(null);
                            mImageView.setVisibility(View.GONE);
                            break;

                        } else {
                            Log.d(TAG, "Must take photo first");
                            Toast.makeText(ClueDisplayActivity.this, "Must take photo first", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    case Clue.LOCATION:
                        if(mLocation != null){
                            answer.setLocation(mLocation.getLatitude() + "," + mLocation.getLongitude());
                            answer.setText(null);
                            answer.setPictureUri(null);

                            mClueBank.remove(mCurrentClueIndex);
                            deleteLocation();
                            mCheckedInLocations = new ArrayList<LatLng>();
                            break;
                        }else{
                            Log.d(TAG, "Must get location first");
                            Toast.makeText(ClueDisplayActivity.this, "Must get location first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                }

                closeMap();
                mClueAnswers.addAnswer(answer);  // adds answer to db

                // updates the clue to show the next one on screen
                if (mClueBank.size() > 0) {
                    if (mCurrentClueIndex == mClueBank.size()) {
                        mCurrentClueIndex -= 1;
                    }
                    updateClue();
                } else {

                    // All clues have been answered so opens summary screen
                    Intent intent = new Intent(v.getContext(), SummaryActivity.class);
                    intent.putExtra(PLAYER_ID, mPlayerId.toString());
                    intent.putExtra(HUNT_ID, mHuntId.toString());

                    Log.d(TAG, "Putting extra playerId: " + mPlayerId);
                    Log.d(TAG, "Putting extra huntId: " + mHuntId);
                    startActivity(intent);

                    finish();  // close this activity so you cannot go back to it
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clue_display, menu);
        //Item item = (Item) findViewById(R.id.action_settings);
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

    public void cleanUp(){
        deleteLocation();
        closeMap();
        deleteImage();
        updateClue();
        mCheckedInLocations = new ArrayList<LatLng>();
    }

    //
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // call back method for google location API
    @Override
    public void onConnected(Bundle connectionHint){
        // gets the users location and stores it
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation != null){
            mCheckedInLocations.add(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            mLatitudeTextView.setText(getResources().getString(R.string.longitude_text) + " " + mLocation.getLatitude());
            mLongitudeTextView.setText(getResources().getString(R.string.longitude_text) + " " + mLocation.getLongitude());
        }else{
            Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Cannot connect to location services");
        }
        mGoogleApiClient.disconnect();
    }

    // call back method for google location API
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }


    public void deleteLocation(){
        mGetOrDeleteLocationButton.setImageResource(R.drawable.ic_add_location_black_24dp);
        mLatitudeTextView.setText(R.string.latitude_text);
        mLongitudeTextView.setText(R.string.longitude_text);
        mLocationLayout.setVisibility(LinearLayout.GONE);
        mLocation = null;
    }

    public void deleteImage(){
        if(mFileUri != null){
            mTakeOrDeletePhotoButton.setVisibility(View.VISIBLE);
            mTakeOrDeletePhotoButton.setImageResource(R.drawable.ic_camera_enhance_black_24dp);
            File file = new File(mFileUri.toString());
            file.delete();
            mFileUri = null;
            mImageView.setImageBitmap(null);
        }
    }


    // call back method for the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mTakeOrDeletePhotoButton.setImageResource(R.drawable.ic_delete_black_24dp);
                mImageView.setVisibility(View.VISIBLE);

                Bitmap image = BitmapFactory.decodeFile(mFileUri.getPath());
                mImageView.setImageBitmap(image);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "User cancelled the image capture");

            } else {
                Log.d(TAG, "Image capture failed");
            }
        }
    }

    // gets clues from the db, or if db is empty, populates them from the hunts.xml file
    public UUID populateClueBank(String huntName){

        if(mCluesDb.getClues().size() == 0) {
            Log.d(TAG, "Clue db is empty, adding clues from hunts.xml");
            addCluesFromXml();
            mCluesDb = new Clues(this);  // get new reference to the clue db
        }

        Intent intent = getIntent();
        intent.getStringExtra(CreateHuntActivity.PLAYER_ID);

        Hunts huntsDb = new Hunts(this);
        UUID huntId = huntsDb.getHunt(huntName).getId();

        mClueBank = (ArrayList<Clue>) mCluesDb.getClues(huntId);

        Log.d(TAG, "Returning hunt id: " + huntId);
        return huntId;
    }

    private void addCluesFromXml()  {
        try {
            XMLPullParserHandler pullParserHandler = new XMLPullParserHandler(this);
            InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.hunts);
            boolean update = pullParserHandler.parse(inputStream);

            if(update){
                List<Clue> clues = mCluesDb.getClues();
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

    public void hideSoftKeyboard(){
        Log.d(TAG, "Hiding soft keyboard");
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void updateClue(){
        Clue clue = mClueBank.get(mCurrentClueIndex);

        mClueTextView.setText(clue.getClueText());

        switch(clue.getClueType()) {
            case Clue.TEXT:
                mAnswerTextLayout.setVisibility(LinearLayout.VISIBLE);
                mTakeOrDeletePhotoButton.setVisibility(Button.GONE);
                mLocationLayout.setVisibility(LinearLayout.GONE);
                break;

            case Clue.PICTURE:
                hideSoftKeyboard();
                mTakeOrDeletePhotoButton.setVisibility(Button.VISIBLE);
                mAnswerTextLayout.setVisibility(LinearLayout.GONE);
                mLocationLayout.setVisibility(LinearLayout.GONE);
                break;

            case Clue.LOCATION:
                hideSoftKeyboard();
                mLocationLayout.setVisibility(LinearLayout.VISIBLE);
                mAnswerTextLayout.setVisibility(LinearLayout.GONE);
                mTakeOrDeletePhotoButton.setVisibility(Button.GONE);
                break;
        }
    }

    // creates a file to store a user captured image
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        System.out.println("SD Mounted: " + Environment.getExternalStorageState());

        // stores them in a public directory visible to all apps on device
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TreasureHunt");

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

    // creates a map fragment and displays it on screen
    public void openMap(){
        if(mapsActivity == null){
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();

            mapsActivity = new SupportMapFragment();
            mapsActivity.getMapAsync(this);

            ft.replace(R.id.mapOrCameraDisplay,  mapsActivity, "tag");
            ft.commit();
        }else{
            closeMap();
        }
    }

    public void closeMap(){
        if(mapsActivity != null){
            ft = fm.beginTransaction();
            ft.remove(mapsActivity);
            mapsActivity = null;
            ft.commit();
        }
    }

    // callback method when the map is created, gets the polygon coordinates from the clue and
    // displays it on the screen if there is one defined for that clue
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            Clue currentClue = mClueBank.get(mCurrentClueIndex);
            String clueLocations = currentClue.getClueLocation();  // returns string of comma separated lat/longs

            String[] rawLatLong = clueLocations.split(",");
            ArrayList<String> latLongListList = new ArrayList<>();

            latLongListList.addAll(Arrays.asList(rawLatLong));

            if(latLongListList.size() % 2 != 0 || latLongListList.size() == 0){
                Log.d(TAG, "Cannot Parse");
            }

            LatLng centralCluePoint = new LatLng(Double.parseDouble(latLongListList.remove(0)), Double.parseDouble(latLongListList.remove(0)));
            Log.d(TAG, centralCluePoint.toString());

            int numPoints = latLongListList.size()/2;
            ArrayList<LatLng> latLongs = new ArrayList<>();

            for(int i = 0, j = 0, k = 1; i < numPoints; i++, j+=2, k+=2){
                latLongs.add(new LatLng(Double.parseDouble(latLongListList.get(j)), Double.parseDouble(latLongListList.get(k))));
                Log.d(TAG, latLongs.get(i).toString());
            }

            // adds polygon to map
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centralCluePoint, MAP_ZOOM_LEVEL));
            if(latLongListList.size() > 0){
                googleMap.addPolygon(new PolygonOptions()
                        .addAll(latLongs)
                        .strokeColor(Color.RED));
            }

            // adds marker if the user has checked in (user must have checked in before opening the map)
            if(mCheckedInLocations.size() > 0){
                for(LatLng checkedIn: mCheckedInLocations){
                    googleMap.addMarker(new MarkerOptions().title("Checked in Here!").snippet(("")).position(checkedIn));
                }
            }

        }catch(Exception e){
            e.printStackTrace();

            // if there is any errors then it shows standard map of UCD
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UCD_LAT_LONG, MAP_ZOOM_LEVEL));
        }
    }

    // warns user they will lose progress
    @Override
    public void onBackPressed() {
        final Activity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.on_back_button);
        builder.setMessage(R.string.clue_activity_on_back_button_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                activity.finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }
}
