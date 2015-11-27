package com.sheep.electric.treasurehunt;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sheep.electric.treasurehunt.util.SelfCheckDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.sheep.electric.treasurehunt.database.access.Answer;
import com.sheep.electric.treasurehunt.database.access.Answers;
import com.sheep.electric.treasurehunt.database.access.Clue;
import com.sheep.electric.treasurehunt.database.access.Clues;
import com.sheep.electric.treasurehunt.database.access.Hunt;
import com.sheep.electric.treasurehunt.database.access.Hunts;
import com.sheep.electric.treasurehunt.database.access.Player;
import com.sheep.electric.treasurehunt.database.access.Players;

/**
 * This shows to include a map in lite mode in a ListView.
 * Note the use of the view holder pattern with the
 * {@link OnMapReadyCallback}.
 */

public class SummaryActivity extends FragmentActivity {

    public static float ZOOM_LEVEL = 15f;
    public static LatLng UCD_CENTRAL = new LatLng(53.3076, -6.22208);

    public static String CORRECT_COLOR = "#33cc33";
    public static String INCORRECT_COLOR = "#e62d00";

    public static String PICTURE_COLOR = "#4285F4";

    private MapAdapter mAdapter;


    private static final String TAG = "SummaryActivity";
    private TextView mHuntNameTextView;
    private TextView mPlayerNameTextView;
    private TextView mTeamNameTextView;

    private Button mMainMenuButton;
    private Button mPlayAgainButton;

    private Answers mAnswersDb;

    private TextView mAnswersCorrectText;

    public Clues mCluesDb;
    public Hunts mHuntsDb;
    public Players mPlayerDb;

    public int mAnswersCorrect;
    public int mAnswersSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_map);

        SelfCheckDialog checkDialog = new SelfCheckDialog();
        checkDialog.show(getSupportFragmentManager(), TAG);

        mHuntsDb = new Hunts(this);
        mPlayerDb = new Players(this);
        mAnswersDb = new Answers(this);
        mCluesDb = new Clues(this);

        Intent intent = getIntent();
        String playerId = intent.getStringExtra(ClueDisplayActivity.PLAYER_ID);
        String huntId = intent.getStringExtra(ClueDisplayActivity.HUNT_ID);


        Hunt hunt = mHuntsDb.getHunt(UUID.fromString(huntId));
        mHuntNameTextView = (TextView) findViewById(R.id.hunt_name_text);
        Log.d(TAG, "View is null? " + (mHuntNameTextView == null));
        mHuntNameTextView.setText(hunt.getName());


        Player player = mPlayerDb.getPlayer(UUID.fromString(playerId));
        mPlayerNameTextView = (TextView) findViewById(R.id.player_name_text);
        mPlayerNameTextView.setText(player.getName());

        mTeamNameTextView = (TextView) findViewById(R.id.team_name_text);
        mTeamNameTextView.setText(player.getTeam());

        ArrayList<Answer> answers = (ArrayList<Answer>) mAnswersDb.getAnswers(UUID.fromString(playerId), UUID.fromString(huntId));

        Log.d(TAG, "Answers size: " + answers.size());

        // Set a custom list adapter for a list of locations
        mAdapter = new MapAdapter(this, answers);
        ListView mList = (ListView) findViewById(R.id.list);
        mList.setAdapter(mAdapter);


        Log.d(TAG, "Setting score");
        mAnswersCorrectText = (TextView) findViewById(R.id.user_score);


        mAnswersCorrect = Answers.getNumberOfCorrectAnswers(answers, mCluesDb);
        mAnswersSize = answers.size();
        mAnswersCorrectText.setText("Score: " + mAnswersCorrect + "/" + mAnswersSize);

        mPlayAgainButton = (Button) findViewById(R.id.play_again_button);
        mMainMenuButton = (Button) findViewById(R.id.main_menu_button);

        mMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mPlayAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateHuntActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    @Override
    public void onBackPressed() {

        final Activity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.on_back_button);
        builder.setMessage(R.string.summary_activity_on_back_button_message);
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

    private class MapAdapter extends ArrayAdapter<Answer> {

        private static final String TAG = "SummaryActivity";
        private final HashSet<MapView> mMaps = new HashSet<MapView>();

        public Bitmap mUserImage;
        public Bitmap mAnswerImage;

        public TextView mClueTextView;

        public TextView mUserAnswerTextView;
        public TextView mAnswerTextView;
        public LinearLayout mAnswerTextLayout;

        public LinearLayout mPictureLayout;
        public TextView mLocationView;

        public ImageView mUserImageView;
        public ImageView mAnswerImageView;

        public HashMap<Integer, Bitmap> mUserImageMap;
        public HashMap<Integer, Bitmap> mAnswerImageMap;

        public RadioButton mYesButton;
        public RadioButton mNoButton;
        public LinearLayout mRadioLayout;

        public HashMap<Integer, View> viewMap;

        public HashMap<Integer, String> selfCheckMap;

        public RadioGroup mRadioGroup;



        public MapAdapter(Context context, ArrayList<Answer> answers) {
            super(context, 0, answers);
            mUserImageMap = new HashMap<>();
            mAnswerImageMap = new HashMap<>();

            selfCheckMap = new HashMap<>();
            viewMap = new HashMap<>();
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Answer answer = getItem(position);
            UUID clueId = answer.getClueId();

            Clue clue = mCluesDb.getClue(clueId);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.summary_list_row, parent, false);
            }

            mClueTextView = (TextView) convertView.findViewById(R.id.clue_text);
            mClueTextView.setText(clue.getClueText());

            mAnswerTextLayout = (LinearLayout) convertView.findViewById(R.id.answer_text_layout);
            mUserAnswerTextView = (TextView) convertView.findViewById(R.id.user_answer_text);
            mAnswerTextView = (TextView) convertView.findViewById(R.id.answer_text);

            mYesButton = (RadioButton) convertView.findViewById(R.id.self_check_yes);
            mNoButton = (RadioButton) convertView.findViewById(R.id.self_check_no);


            mPictureLayout = (LinearLayout) convertView.findViewById(R.id.image_layout);
            mLocationView = (TextView) convertView.findViewById(R.id.answer_location);

            mUserImageView = (ImageView) convertView.findViewById(R.id.user_image);
            mAnswerImageView = (ImageView) convertView.findViewById(R.id.answer_image);

            mRadioLayout = (LinearLayout) convertView.findViewById(R.id.self_check_radio);



            switch (clue.getClueType()){
                case Clue.TEXT:
                    mAnswerTextLayout.setVisibility(View.VISIBLE);
                    mUserAnswerTextView.setText("You Answered: " + answer.getText());
                    mAnswerTextView.setText("Correct Answer: " + clue.getClueAnswer().split("\\|")[0]);


                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.GONE);

                    answer.setResult(clue);
                    boolean answerCorrect = answer.getResult();
                    Log.d(TAG, "Clue text result: " + clue.getClueText() + " as " + answerCorrect );

                    if(answerCorrect){
                        convertView.setBackgroundColor(Color.parseColor(CORRECT_COLOR));

                    }else{
                        convertView.setBackgroundColor(Color.parseColor(INCORRECT_COLOR));
                    }

                    mPictureLayout.setVisibility(View.GONE);
                    mLocationView.setVisibility(View.GONE);
                    break;

                case Clue.PICTURE:
                    mPictureLayout.setVisibility(View.VISIBLE);

                    mRadioGroup = (RadioGroup) convertView.findViewById(R.id.radio_group);

                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.GONE);

                    if(mUserImage == null || mUserImageMap.get(position) == null){
                        mUserImage = BitmapFactory.decodeFile(answer.getPictureUri().getPath());
                        mUserImageMap.put(position, mUserImage);
                    }

                    if(mAnswerImage == null || mAnswerImageMap.get(position) == null){
                        //mAnswerImage = BitmapFactory.decodeFile(answer.getPictureUri().getPath());

                        String answerPicString = clue.getClueAnswer();


                        Log.d(TAG, mCluesDb.getClue((answer.getClueId())).getClueText());

                        if(answerPicString.equalsIgnoreCase("man_walk_through_wall")){
                            mAnswerImage = BitmapFactory.decodeResource(getResources(), R.drawable.man_walk_through_wall);
                        }else{
                            mAnswerImage = BitmapFactory.decodeResource(getResources(), R.drawable.ucd_egg);
                        }




                        mAnswerImageMap.put(position, mAnswerImage);
                    }

                    mUserImageView.setImageBitmap(mUserImageMap.get(position));
                    mAnswerImageView.setImageBitmap(mAnswerImageMap.get(position));

                    final View finalConvertView = convertView;

                    final TextView finalAnswersCorrect = mAnswersCorrectText;
                    mYesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Correct: " + mAnswersCorrect);
                            Log.d(TAG, "Text: " + finalAnswersCorrect.getText());
                            mAnswersCorrect++;


                            Log.d(TAG, "Correct 2: " + mAnswersCorrect);
                            selfCheckMap.put(position, CORRECT_COLOR);
                            finalConvertView.setBackgroundColor(Color.parseColor(CORRECT_COLOR));
                            //mRadioLayout.setVisibility(View.GONE);

                            View view =  (View) v.getParent().getParent();
                            view.setVisibility(View.GONE);
                        }
                    });
                    mAnswersCorrectText.setText("Score: " + mAnswersCorrect + "/" + mAnswersSize);

                    mNoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mRadioLayout.setVisibility(View.GONE);
                            selfCheckMap.put(position, INCORRECT_COLOR);
                            finalConvertView.setBackgroundColor(Color.parseColor(INCORRECT_COLOR));
                            //mRadioLayout.setVisibility(View.GONE);

                            View view = (View) v.getParent().getParent();
                            view.setVisibility(View.GONE);

                        }
                    });


                    if(selfCheckMap.get(position) != null){
                        convertView.setBackgroundColor(Color.parseColor(selfCheckMap.get(position)));

                    }else{
                        mRadioLayout.setVisibility(View.VISIBLE);
                        convertView.setBackgroundColor(Color.parseColor(PICTURE_COLOR));
                        mYesButton.setChecked(false);
                        mNoButton.setChecked(false);
                    }



                    mAnswerTextLayout.setVisibility(View.GONE);
                    mLocationView.setVisibility(View.GONE);


                    viewMap.put(position, convertView);
                    break;
                case Clue.LOCATION:
                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.VISIBLE);

                    mLocationView.setVisibility(View.VISIBLE);

                    answer.setResult(clue);
                    if(answer.getResult()){
                        mLocationView.setText(R.string.location_was_correct);
                        convertView.setBackgroundColor(Color.parseColor(CORRECT_COLOR));
                    }else{
                        mLocationView.setText(R.string.location_incorrect);
                        convertView.setBackgroundColor(Color.parseColor(INCORRECT_COLOR));
                    }

                    mAnswerTextLayout.setVisibility(View.GONE);
                    mPictureLayout.setVisibility(View.GONE);

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

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(UCD_CENTRAL, ZOOM_LEVEL));

            // Set the map type back to normal.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }
}
