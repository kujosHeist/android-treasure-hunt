package com.sheep.electric.treasurehunt;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * This shows to include a map in lite mode in a ListView.
 * Note the use of the view holder pattern with the
 * {@link OnMapReadyCallback}.
 */

public class SummaryMapActivity extends FragmentActivity {

    private ListFragment mList;

    private MapAdapter mAdapter;


    private static final String TAG = "SummaryActivity";
    private TextView mHuntNameTextView;
    private TextView mPlayerNameTextView;
    private TextView mTeamNameTextView;

    private Answers mAnswersDb;

    private TextView mAnswersCorrectText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_summary_map);

        Intent intent = getIntent();
        String playerId = intent.getStringExtra(ClueDisplayActivity.PLAYER_ID);
        String huntId = intent.getStringExtra(ClueDisplayActivity.HUNT_ID);

        Hunts huntDb = new Hunts(this);
        Hunt hunt = huntDb.getHunt(UUID.fromString(huntId));

        mHuntNameTextView = (TextView) findViewById(R.id.hunt_name_text);
        Log.d(TAG, "View is null? " + (mHuntNameTextView == null));
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

        // Set a custom list adapter for a list of locations
        mAdapter = new MapAdapter(this, answers);
        ListView mList = (ListView) findViewById(R.id.list);
        mList.setAdapter(mAdapter);


        Log.d(TAG, "Setting score");
        mAnswersCorrectText = (TextView) findViewById(R.id.answers_correct_text);

        Clues cluesDb = new Clues(this);
        int answersCorrect = Answers.getNumberOfCorrectAnswers(answers, cluesDb);
        mAnswersCorrectText.setText(" " + answersCorrect + "/" + answers.size());




    }


    private class MapAdapter extends ArrayAdapter<Answer> {

        private static final String TAG = "SummaryMapActivity";
        private final HashSet<MapView> mMaps = new HashSet<MapView>();

        private int length;

        public MapAdapter(Context context, ArrayList<Answer> answers) {
            super(context, 0, answers);
            length = answers.size();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Answer answer = getItem(position);
            UUID clueId = answer.getClueId();
            Clues cluesDb = new Clues(getContext());
            Clue clue = cluesDb.getClue(clueId);


            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.summary_list_row, parent, false);
            }

            TextView clueTextView = (TextView) convertView.findViewById(R.id.clue_text);
            clueTextView.setText("Clue Text: " + clue.getClueText());

            TextView answerTextView  = (TextView) convertView.findViewById(R.id.answer_text);
            ImageView pictureView = (ImageView) convertView.findViewById(R.id.answer_picture);
            TextView locationView = (TextView) convertView.findViewById(R.id.answer_location);


            switch (clue.getClueType()){
                case Clue.TEXT:
                    answerTextView.setVisibility(View.VISIBLE);
                    answerTextView.setText("Answer Text: " + answer.getText());
                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.GONE);

                    answer.setResult(clue);
                    boolean answerCorrect = answer.getResult();
                    Log.d(TAG, "Clue text result: " + clue.getClueText() + " as " + answerCorrect );

                    if(answerCorrect){
                        convertView.setBackgroundColor(Color.parseColor("GREEN"));
                    }else{
                        convertView.setBackgroundColor(Color.parseColor("RED"));
                    }



                    pictureView.setVisibility(View.GONE);
                    locationView.setVisibility(View.GONE);
                    break;

                case Clue.PICTURE:
                    pictureView.setVisibility(View.VISIBLE);

                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.GONE);

                    Bitmap image = BitmapFactory.decodeFile(answer.getPictureUri().getPath());
                    pictureView.setImageBitmap(image);

                    convertView.setBackgroundColor(Color.parseColor("#ADD8E6"));

                    answerTextView.setVisibility(View.GONE);
                    locationView.setVisibility(View.GONE);
                    break;
                case Clue.LOCATION:
                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.VISIBLE);

                    locationView.setVisibility(View.VISIBLE);

                    answer.setResult(clue);
                    if(answer.getResult()){
                        locationView.setText("Location was correct!");
                        convertView.setBackgroundColor(Color.parseColor("GREEN"));
                    }else{
                        locationView.setText("Location was incorrect!");
                        convertView.setBackgroundColor(Color.parseColor("RED"));
                    }

                    answerTextView.setVisibility(View.GONE);
                    pictureView.setVisibility(View.GONE);

                    // create map area and put it in the activity
                    ViewHolder mapHolder;

                    mapHolder = new ViewHolder();
                    mapHolder.mapView = (MapView) convertView.findViewById(R.id.lite_listrow_map);

                    convertView.setTag(mapHolder);

                    LatLng userLatLng = getCoords(answer.getLocation());
                    LatLng clueLatLng = getCoords(clue.getClueLocation());
                    double thresholdRadius = getRadius(clue.getClueAnswer());

                    mapHolder.initializeMapView(clueLatLng, userLatLng, thresholdRadius);

                    // Keep track of MapView
                    mMaps.add(mapHolder.mapView);

                default:
                    Log.e(TAG, "Invalid clue type");
            }


            return convertView;

        }

/*
        public View getLocationView(View convertView, Clue clue, Answer answer){
            convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.VISIBLE);

            TextView clueTextView = (TextView) convertView.findViewById(R.id.clue_text);
            clueTextView.setText("Clue Text: " + clue.getClueText());

            TextView answerTextView  = (TextView) convertView.findViewById(R.id.answer_text);
            ImageView pictureView = (ImageView) convertView.findViewById(R.id.answer_picture);
            TextView locationView = (TextView) convertView.findViewById(R.id.answer_location);


            locationView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Clue text: " + clue.getClueText());

            LatLng userLatLng = getCoords(answer.getLocation());
            LatLng clueLatLng = getCoords(clue.getClueLocation());
            double thresholdRadius = getRadius(clue.getClueAnswer());


            Log.d(TAG, "User submitted location: " +  userLatLng.latitude + "," + userLatLng.longitude);

            double distance = distFrom(clueLatLng.latitude, clueLatLng.longitude, userLatLng.latitude, userLatLng.longitude);
            Log.d(TAG, "Distance from checkin to target: " + distance + ", threshold: " + thresholdRadius);

            if(distance < thresholdRadius){
                locationView.setText("Location was correct!");
                convertView.setBackgroundColor(Color.parseColor("GREEN"));
            }else{
                locationView.setText("Location was incorrect!");
                convertView.setBackgroundColor(Color.parseColor("RED"));
            }

            answerTextView.setVisibility(View.GONE);
            pictureView.setVisibility(View.GONE);


            ViewHolder mapHolder;

            mapHolder = new ViewHolder();
            mapHolder.mapView = (MapView) convertView.findViewById(R.id.lite_listrow_map);

            convertView.setTag(mapHolder);

            mapHolder.initializeMapView(clueLatLng, userLatLng, thresholdRadius);

            // Keep track of MapView
            mMaps.add(mapHolder.mapView);

            return convertView;
        }

*/
        public HashSet<MapView> getMaps() {
            return mMaps;
        }
    }


    public LatLng getCoords(String location){
        String[] tokens = location.split(",");
        return new LatLng(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
    }

    public double getRadius(String location){
        String[] tokens = location.split(",");
        return Double.parseDouble(tokens[0]);
    }

    class ViewHolder implements OnMapReadyCallback {
        MapView mapView;
        LatLng cluelatLng = null;
        LatLng userlatLng = null;

        double radius;

        public void initializeMapView(LatLng clueCoords, LatLng userCoords, double rad) {
            if (mapView != null) {
                cluelatLng = clueCoords;
                userlatLng = userCoords;

                radius = rad;
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap map) {
            // Add a marker for this item and set the camera

            Log.d(TAG, "User: " + userlatLng);
            Log.d(TAG, "Clue: " + cluelatLng);
            map.addMarker(new MarkerOptions().position(userlatLng));


            Circle circle = map.addCircle(new CircleOptions()
                    .center(cluelatLng)
                    .radius(radius)
                    .strokeColor(Color.RED)
                    .strokeWidth(4));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(cluelatLng, 16f));

            // Set the map type back to normal.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }



}
