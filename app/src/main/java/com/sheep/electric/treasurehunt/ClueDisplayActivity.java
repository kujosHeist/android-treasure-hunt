package com.sheep.electric.treasurehunt;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    private ArrayList<Clue> mClueBank;
    private int mCurrentClueIndex = 0;

    private ImageButton mArrowLeftButton;
    private ImageButton mArrowRightButton;
    private Button mTakeOrDeletePhotoButton;
    private TextView mClueTextView;
    private EditText mAnswerText;
    private Button mGetOrDeleteLocationButton;

    private Button mShowOnMap;

    private TextView mEnterAnswerTextView;


    private ImageView mImageView;



    private Button mSubmitAnswerButton;
    private Uri mFileUri;

    private Answers mClueAnswers;
    private UUID mHuntId;
    private UUID mPlayerId;

    private Location mLocation;

    private LocationManager mLocationManager;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    private LinearLayout mLocationLayout;
    private LinearLayout mAnswerTextLayout;

    public static final LatLng UCD_LAT_LONG = new LatLng(53.3069227,-6.2234008);
    public static final int MAP_ZOOM_LEVEL = 15;

    private Clues mCluesDb;

    private ArrayList<LatLng> mCheckedInLocations;



    // Camera code ******************************************************************************
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;



    GoogleApiClient mGoogleApiClient;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_display);

        mCheckedInLocations = new ArrayList<LatLng>();

        mCluesDb = new Clues(this);

        mLocationLayout = (LinearLayout) findViewById(R.id.location_layout);
        mAnswerTextLayout = (LinearLayout) findViewById(R.id.answer_text_layout);

        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);

        buildGoogleApiClient();

        mShowOnMap = (Button) findViewById(R.id.show_on_map_button);

        mClueTextView = (TextView) findViewById(R.id.clue_text);
        mAnswerText = (EditText) findViewById(R.id.clue_answer_edit_text);
        mEnterAnswerTextView = (TextView) findViewById(R.id.enter_answer_text);


        mTakeOrDeletePhotoButton = (Button) findViewById(R.id.take_picture_button);
        mGetOrDeleteLocationButton = (Button) findViewById(R.id.get_location_button);
        mArrowLeftButton = (ImageButton) findViewById(R.id.arrow_left);
        mArrowRightButton = (ImageButton) findViewById(R.id.arrow_right);
        mSubmitAnswerButton = (Button) findViewById(R.id.submit_answer_button);


        mImageView = (ImageView) findViewById(R.id.captured_picture);

        Intent intent = getIntent();
        String playerIdString = intent.getStringExtra(CreateHuntActivity.PLAYER_ID);
        String huntName = intent.getStringExtra(CreateHuntActivity.HUNT_NAME);

        mPlayerId = UUID.fromString(playerIdString);
        mHuntId = populateClueBank(huntName);   // creates hunt if it doesn't already exist, returns hunt id

        mClueAnswers = new Answers(this);

        updateClue();

        mGetOrDeleteLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocation == null) {
                    mGoogleApiClient.connect();
                    mGetOrDeleteLocationButton.setText(R.string.delete_location_button);
                } else {
                    mLatitudeTextView.setText(R.string.latitude_text);
                    mLongitudeTextView.setText(R.string.longitude_text);
                    mGetOrDeleteLocationButton.setText(R.string.get_location_button);
                    mLocation = null;
                }

            }
        });

        mShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        mSubmitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clue clue = mClueBank.get(mCurrentClueIndex);

                Answer answer = new Answer(clue.getId(), mPlayerId, mHuntId);  // create new answer ready to be stored in db

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
                        if (mFileUri != null) {

                            answer.setPictureUri(mFileUri);
                            answer.setText(null);
                            answer.setLocation(null);

                            mClueBank.remove(mCurrentClueIndex);
                            mTakeOrDeletePhotoButton.setText(R.string.take_picture_button);

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

                if (mClueBank.size() > 0) {
                    if (mCurrentClueIndex == mClueBank.size()) {
                        mCurrentClueIndex -= 1;
                    }
                    updateClue();
                } else {
                    Intent intent = new Intent(v.getContext(), SummaryActivity.class);
                    intent.putExtra(PLAYER_ID, mPlayerId.toString());
                    intent.putExtra(HUNT_ID, mHuntId.toString());

                    Log.d(TAG, "Putting extra playerId: " + mPlayerId);
                    Log.d(TAG, "Putting extra huntId: " + mHuntId);
                    startActivity(intent);
                }
            }
        });

        mArrowLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentClueIndex = (mCurrentClueIndex - 1) % mClueBank.size();
                if (mCurrentClueIndex < 0) {
                    mCurrentClueIndex += mClueBank.size();
                }
                closeMap();
                deleteLocation();
                deleteImage();
                updateClue();
                mCheckedInLocations = new ArrayList<LatLng>();

            }
        });

        mArrowRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentClueIndex = (mCurrentClueIndex + 1) % mClueBank.size();
                deleteLocation();
                closeMap();
                deleteImage();
                updateClue();
                mCheckedInLocations = new ArrayList<LatLng>();

            }
        });


        mTakeOrDeletePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMap();
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
                    mTakeOrDeletePhotoButton.setText(R.string.take_picture_button);
                }


            }
        });

        // if you want to load map from fragment which is already defined in the layout
        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint){
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

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    public void deleteLocation(){
        mGetOrDeleteLocationButton.setText(R.string.get_location_button);
        mLatitudeTextView.setText(R.string.latitude_text);
        mLongitudeTextView.setText(R.string.longitude_text);
        mLocationLayout.setVisibility(LinearLayout.GONE);
        mLocation = null;
    }

    public void deleteImage(){
        if(mFileUri != null){
            mTakeOrDeletePhotoButton.setVisibility(View.VISIBLE);
            mTakeOrDeletePhotoButton.setText(R.string.take_picture_button);
            File file = new File(mFileUri.toString());
            file.delete();
            mFileUri = null;
            mImageView.setImageBitmap(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mTakeOrDeletePhotoButton.setText(R.string.delete_picture);
                mImageView.setVisibility(View.VISIBLE);

                // Image captured and saved to mFileUri specified in the Intent
                // Toast.makeText(this, "Image saved to:\n" + mFileUri.toString(), Toast.LENGTH_LONG).show();
                Bitmap image = BitmapFactory.decodeFile(mFileUri.getPath());
                mImageView.setImageBitmap(image);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "User cancelled the image capture");

            } else {
                Log.d(TAG, "Image capture failed");
            }
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
                mTakeOrDeletePhotoButton.setVisibility(Button.VISIBLE);
                mAnswerTextLayout.setVisibility(LinearLayout.GONE);
                mLocationLayout.setVisibility(LinearLayout.GONE);
                break;

            case Clue.LOCATION:
                mLocationLayout.setVisibility(LinearLayout.VISIBLE);
                mAnswerTextLayout.setVisibility(LinearLayout.GONE);
                mTakeOrDeletePhotoButton.setVisibility(Button.GONE);
        }
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        System.out.println("SD Mounted: " + Environment.getExternalStorageState());

        // stores them in a public directory visible to all apps on device
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

    // opens up separate activity and displays it
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            Clue currentClue = mClueBank.get(mCurrentClueIndex);

            String clueLocations = currentClue.getClueLocation();  // returns string of comma seperated lat/longs

            //String clueLocations = "53.307730,-6.222316,53.308409,-6.222694,53.307525,-6.223118,53.307018,-6.221982,53.307967,-6.221377";
            String[] rawLatLong = clueLocations.split(",");
            ArrayList<String> latLongListList = new ArrayList<>();

            latLongListList.addAll(Arrays.asList(rawLatLong));



            if(latLongListList.size() % 2 != 0 || latLongListList.size() == 0){
                Log.d(TAG, "Cannot Parse");
                //throw new Exception("Error parsing clue location");
            }

            LatLng centralCluePoint = new LatLng(Double.parseDouble(latLongListList.remove(0)), Double.parseDouble(latLongListList.remove(0)));
            Log.d(TAG, centralCluePoint.toString());

            int numPoints = latLongListList.size()/2;

            ArrayList<LatLng> latLongs = new ArrayList<>();

            for(int i = 0, j = 0, k = 1; i < numPoints; i++, j+=2, k+=2){
                latLongs.add(new LatLng(Double.parseDouble(latLongListList.get(j)), Double.parseDouble(latLongListList.get(k))));
                Log.d(TAG, latLongs.get(i).toString());
            }

            googleMap.setMyLocationEnabled(true);



            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centralCluePoint, MAP_ZOOM_LEVEL));
            if(latLongListList.size() > 0){
                googleMap.addPolygon(new PolygonOptions()
                        .addAll(latLongs)
                        .strokeColor(Color.RED));
                //googleMap.addMarker(new MarkerOptions().title("Hint").snippet(("Here be swans")).position(centralCluePoint));
            }

            if(mCheckedInLocations.size() > 0){
                for(LatLng checkedIn: mCheckedInLocations){
                    googleMap.addMarker(new MarkerOptions().title("Checked in Here!").snippet(("")).position(checkedIn));
                }

            }


        }catch(Exception e){
            e.printStackTrace();

            // just show standard map of ucd
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UCD_LAT_LONG, MAP_ZOOM_LEVEL));


        }

    }





    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }
}
