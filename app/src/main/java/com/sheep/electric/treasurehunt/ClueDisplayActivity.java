package com.sheep.electric.treasurehunt;


import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
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


import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private Button mTakePhotoButton;
    private TextView mClueTextView;
    private EditText mAnswerText;
    private Button mSubmitLocationButton;

    private TextView mEnterAnswerTextView;


    private ImageView mImageView;

    private Button mDeletePicture;

    private Button mSubmitAnswerButton;
    private Uri mFileUri;

    private Answers mClueAnswers;
    private UUID mHuntId;
    private UUID mPlayerId;

    private Location mLocation;

    private LocationManager mLocationManager;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;


/*
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            Log.d(TAG, "Location is: " + mLocation.toString());
            Toast.makeText(getApplicationContext(), "Location is: " + mLocation.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    */

    // Camera code ******************************************************************************
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    GoogleApiClient mGoogleApiClient;

    Location mLastLocation;
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            mLatitudeTextView.setText("Latitude: " + mLastLocation.getLatitude());
            mLongitudeTextView.setText("Lonitude: " + mLastLocation.getLongitude());
        }else{
            Toast.makeText(this, "Can't get location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_display);

        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLatitudeTextView = (TextView) findViewById(R.id.longitude_text);

        buildGoogleApiClient();



        /*

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        */ /*
        try{
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, mLocationListener);
        }catch (Exception e){
            e.printStackTrace();
        }

        */

        mClueTextView = (TextView) findViewById(R.id.clue_text);
        mAnswerText = (EditText) findViewById(R.id.clue_answer_edit_text);
        mEnterAnswerTextView = (TextView) findViewById(R.id.enter_answer_text);


        mTakePhotoButton = (Button) findViewById(R.id.take_photo_button);
        mSubmitLocationButton = (Button) findViewById(R.id.submit_location_button);
        mArrowLeftButton = (ImageButton) findViewById(R.id.arrow_left);
        mArrowRightButton = (ImageButton) findViewById(R.id.arrow_right);
        mSubmitAnswerButton = (Button) findViewById(R.id.submit_answer_button);

        mDeletePicture = (Button) findViewById(R.id.delete_image);
        mImageView = (ImageView) findViewById(R.id.captured_picture);

        Intent intent = getIntent();
        String playerIdString = intent.getStringExtra(CreateHuntActivity.PLAYER_ID);
        String huntName = intent.getStringExtra(CreateHuntActivity.HUNT_NAME);

        mPlayerId = UUID.fromString(playerIdString);
        mHuntId = populateClueBank(huntName);   // creates hunt if it doesn't already exist, returns hunt id

        mClueAnswers = new Answers(this);

        updateClue();

        mSubmitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clue clue = mClueBank.get(mCurrentClueIndex);

                Answer answer = new Answer(clue.getId(), mPlayerId, mHuntId);  // create new answer ready to be stored in db

                switch (clue.getClueType()) {
                    case Clue.TEXT:
                        EditText clueAnswerEditText = (EditText) findViewById(R.id.clue_answer_edit_text);

                        String clueAnswer = clueAnswerEditText.getText().toString();
                        clueAnswerEditText.setText("");

                        answer.setText(clueAnswer);
                        answer.setPictureUri(null);
                        answer.setLocation(null);

                        mClueBank.remove(mCurrentClueIndex);
                        break;

                    case Clue.PICTURE:
                        if (mFileUri != null) {

                            answer.setPictureUri(mFileUri);
                            answer.setText(null);
                            answer.setLocation(null);

                            mClueBank.remove(mCurrentClueIndex);

                            mFileUri = null;
                            mImageView.setImageBitmap(null);
                            mImageView.setVisibility(View.GONE);
                            mDeletePicture.setVisibility(View.GONE);
                            break;

                        } else {
                            Log.d(TAG, "Must take photo first");
                            Toast.makeText(ClueDisplayActivity.this, "Must take photo first", Toast.LENGTH_SHORT).show();
                            return;
                        }


                    case Clue.LOCATION:
                        String clueLocationAnswer = "{Test Location}";

                        answer.setLocation(clueLocationAnswer);
                        answer.setText(null);
                        answer.setPictureUri(null);

                        mClueBank.remove(mCurrentClueIndex);
                        break;
                }

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
                deleteImage();
                updateClue();
            }
        });

        mArrowRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentClueIndex = (mCurrentClueIndex + 1) % mClueBank.size();
                deleteImage();
                updateClue();
            }
        });

        mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        mDeletePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
                Toast.makeText(v.getContext(), "Image Deleted", Toast.LENGTH_LONG).show();
            }
        });



        // if you want to load map from fragment which is already defined in the layout
        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);
    }

    public void deleteImage(){
        if(mFileUri != null){
            mDeletePicture.setVisibility(View.GONE);
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
                mImageView.setVisibility(View.VISIBLE);
                mDeletePicture.setVisibility(View.VISIBLE);
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
        Clues cluesDb = new Clues(this);

        cluesDb.getClues();

        if(cluesDb.getClues().size() == 0) {
            Log.d(TAG, "Clue db is empty, adding clues from hunts.xml");
            addCluesFromXml();
            cluesDb = new Clues(this);  // get new reference to the clue db
        }

        Intent intent = getIntent();
        intent.getStringExtra(CreateHuntActivity.PLAYER_ID);



        Hunts huntsDb = new Hunts(this);
        UUID huntId = huntsDb.getHunt(huntName).getId();

        mClueBank = (ArrayList<Clue>) cluesDb.getClues(huntId);

        Log.d(TAG, "Returning hunt id: " + huntId);
        return huntId;
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
        Clue clue = mClueBank.get(mCurrentClueIndex);

        mClueTextView.setText(clue.getClueText());

        switch(clue.getClueType()) {
            case Clue.TEXT:
                mEnterAnswerTextView.setVisibility(View.VISIBLE);
                mAnswerText.setVisibility(EditText.VISIBLE);

                mTakePhotoButton.setVisibility(Button.GONE);
                mSubmitLocationButton.setVisibility(Button.GONE);

                break;
            case Clue.PICTURE:

                mTakePhotoButton.setVisibility(Button.VISIBLE);



                mEnterAnswerTextView.setVisibility(View.GONE);
                mAnswerText.setVisibility(EditText.GONE);
                mSubmitLocationButton.setVisibility(Button.GONE);
                break;

            case Clue.LOCATION:
                mSubmitLocationButton.setVisibility(Button.VISIBLE);

                mEnterAnswerTextView.setVisibility(View.GONE);
                mAnswerText.setVisibility(EditText.GONE);
                mTakePhotoButton.setVisibility(Button.GONE);
        }
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

    // opens up separate activity and displays it
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

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ucd = new LatLng(53.307262,-6.219077);
        LatLng latLng = new LatLng(53.307262,-6.219077);
        Log.d(TAG,"LatLng: " + latLng.toString());
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ucd, 15));
        googleMap.addMarker(new MarkerOptions().title("Sydney").snippet(("Most people in Oz")).position(ucd));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
