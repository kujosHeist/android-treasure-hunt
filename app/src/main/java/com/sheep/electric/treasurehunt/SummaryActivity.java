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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sheep.electric.treasurehunt.database.access.Answer;
import com.sheep.electric.treasurehunt.database.access.Answers;
import com.sheep.electric.treasurehunt.database.access.Clue;
import com.sheep.electric.treasurehunt.database.access.Clues;
import com.sheep.electric.treasurehunt.database.access.Hunt;
import com.sheep.electric.treasurehunt.database.access.Hunts;
import com.sheep.electric.treasurehunt.database.access.Player;
import com.sheep.electric.treasurehunt.database.access.Players;
import com.sheep.electric.treasurehunt.util.SelfCheckDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class SummaryActivity extends FragmentActivity {

    private static final String TAG = "SummaryActivity";
    public static float ZOOM_LEVEL = 15f;
    public static LatLng UCD_CENTRAL = new LatLng(53.3076, -6.22208);
    public static String CORRECT_COLOR = "#33cc33";
    public static String INCORRECT_COLOR = "#e62d00";
    public static String PICTURE_COLOR = "#4285F4";

    private AnswerAdapter mAdapter;

    private TextView mHuntNameTextView;
    private TextView mPlayerNameTextView;
    private TextView mTeamNameTextView;
    private Button mMainMenuButton;
    private Button mPlayAgainButton;
    private TextView mAnswersCorrectText;

    private Answers mAnswersDb;
    public Clues mCluesDb;
    public Hunts mHuntsDb;
    public Players mPlayerDb;

    public int mAnswersCorrect;
    public int mAnswersSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_map);

        // Tells the user the hunt is complete
        SelfCheckDialog checkDialog = new SelfCheckDialog();
        checkDialog.show(getSupportFragmentManager(), TAG);

        mHuntsDb = new Hunts(this);
        mPlayerDb = new Players(this);
        mAnswersDb = new Answers(this);
        mCluesDb = new Clues(this);

        // gets the sent by the last activity
        Intent intent = getIntent();
        String playerId = intent.getStringExtra(ClueDisplayActivity.PLAYER_ID);
        String huntId = intent.getStringExtra(ClueDisplayActivity.HUNT_ID);

        // retrieves the hunt info from the db and displays it to the screen
        Hunt hunt = mHuntsDb.getHunt(UUID.fromString(huntId));
        mHuntNameTextView = (TextView) findViewById(R.id.hunt_name_text);
        mHuntNameTextView.setText(hunt.getName());

        Player player = mPlayerDb.getPlayer(UUID.fromString(playerId));
        mPlayerNameTextView = (TextView) findViewById(R.id.player_name_text);
        mPlayerNameTextView.setText(player.getName());

        mTeamNameTextView = (TextView) findViewById(R.id.team_name_text);
        mTeamNameTextView.setText(player.getTeam());

        // gets the users answers from the db
        ArrayList<Answer> answers = (ArrayList<Answer>) mAnswersDb.getAnswers(UUID.fromString(playerId), UUID.fromString(huntId));


        // Set a custom list adapter for displaying the users clue answers
        mAdapter = new AnswerAdapter(this, answers);
        ListView mList = (ListView) findViewById(R.id.list);
        mList.setAdapter(mAdapter);

        mAnswersCorrectText = (TextView) findViewById(R.id.user_score);
        mAnswersCorrect = Answers.getNumberOfCorrectAnswers(answers, mCluesDb);
        mAnswersSize = answers.size();

        // displays the users score
        mAnswersCorrectText.setText("Score: " + mAnswersCorrect + "/" + mAnswersSize);

        mPlayAgainButton = (Button) findViewById(R.id.play_again_button);
        mMainMenuButton = (Button) findViewById(R.id.main_menu_button);

        // sets listener to the two buttons
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


    // this class handles displaying the clue answers to the screen
    private class AnswerAdapter extends ArrayAdapter<Answer> {

        private static final String TAG = "AnswerAdapter";

        // instantiates various views
        public LinearLayout mAnswerTextLayout;
        public LinearLayout mPictureLayout;
        public LinearLayout mRadioLayout;
        public ImageView mUserImageView;
        public ImageView mAnswerImageView;
        public TextView mClueTextView;
        public TextView mUserAnswerTextView;
        public TextView mAnswerTextView;
        public TextView mLocationView;
        public RadioGroup mRadioGroup;
        public RadioButton mYesButton;
        public RadioButton mNoButton;

        public Bitmap mUserImage;
        public Bitmap mAnswerImage;
        public HashMap<Integer, Bitmap> mUserImageMap;
        public HashMap<Integer, Bitmap> mAnswerImageMap;
        public HashMap<Integer, String> selfCheckMap;

        public AnswerAdapter(Context context, ArrayList<Answer> answers) {
            super(context, 0, answers);
            mUserImageMap = new HashMap<>();
            mAnswerImageMap = new HashMap<>();
            selfCheckMap = new HashMap<>();
        }

        // this method is to display the answer item each time it is in view
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // gets the answers details
            Answer answer = getItem(position);
            UUID clueId = answer.getClueId();
            Clue clue = mCluesDb.getClue(clueId);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.summary_list_row, parent, false);
            }

            // instantiates the various views, needs to be done each time AFAIK
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


            // different things get displayed depending on the clue type
            switch (clue.getClueType()){
                case Clue.TEXT:
                    mAnswerTextLayout.setVisibility(View.VISIBLE);
                    mUserAnswerTextView.setText("You Answered: " + answer.getText());
                    mAnswerTextView.setText("Correct Answer: " + clue.getClueAnswer().split("\\|")[0]);

                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.GONE);

                    // this method checks the users answer against the answer in the db
                    answer.setResult(clue);
                    boolean answerCorrect = answer.getResult();

                    // displays a different for correct and incorrect
                    if(answerCorrect){
                        convertView.setBackgroundColor(Color.parseColor(CORRECT_COLOR));
                    }else{
                        convertView.setBackgroundColor(Color.parseColor(INCORRECT_COLOR));
                    }

                    // hides the other clue type related views
                    mPictureLayout.setVisibility(View.GONE);
                    mLocationView.setVisibility(View.GONE);
                    break;

                // if clue is type picture, we have to get the user to check the result against an example photo
                case Clue.PICTURE:
                    mPictureLayout.setVisibility(View.VISIBLE);

                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.GONE);

                    mRadioGroup = (RadioGroup) convertView.findViewById(R.id.radio_group);

                    // this makes sure to only load an image once, and then stores it in a map, otherwise
                    // it would freshly load it from memory every time the scrolls by, slowing down performance
                    if(mUserImage == null || mUserImageMap.get(position) == null){
                        mUserImage = BitmapFactory.decodeFile(answer.getPictureUri().getPath());
                        mUserImageMap.put(position, mUserImage);
                    }

                    if(mAnswerImage == null || mAnswerImageMap.get(position) == null){
                        String answerPicString = clue.getClueAnswer();
                        Log.d(TAG, mCluesDb.getClue((answer.getClueId())).getClueText());

                        // displays the example picture from db depending on whats stored in the clue
                        if(answerPicString.equalsIgnoreCase("man_walk_through_wall")){
                            mAnswerImage = BitmapFactory.decodeResource(getResources(), R.drawable.man_walk_through_wall);
                        }else{
                            mAnswerImage = BitmapFactory.decodeResource(getResources(), R.drawable.ucd_egg);
                        }

                        // stores the image in a map
                        mAnswerImageMap.put(position, mAnswerImage);
                    }

                    // displays the suers image and the example image
                    mUserImageView.setImageBitmap(mUserImageMap.get(position));
                    mAnswerImageView.setImageBitmap(mAnswerImageMap.get(position));

                    // responds to the user clicking on a radio button to say if they had taken
                    // the correct photo or not
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

                            // removes the radio view so user only can choose once
                            View view = (View) v.getParent().getParent();
                            view.setVisibility(View.GONE);
                        }
                    });

                    mNoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mRadioLayout.setVisibility(View.GONE);
                            selfCheckMap.put(position, INCORRECT_COLOR);
                            finalConvertView.setBackgroundColor(Color.parseColor(INCORRECT_COLOR));

                            View view = (View) v.getParent().getParent();
                            view.setVisibility(View.GONE);
                        }
                    });

                    // updates the users score depending on their response
                    mAnswersCorrectText.setText("Score: " + mAnswersCorrect + "/" + mAnswersSize);

                    // changes the background color from default blue to red or green if the user
                    // has self checked
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

                    break;

                case Clue.LOCATION:
                    convertView.findViewById(R.id.lite_listrow_map).setVisibility(View.VISIBLE);

                    mLocationView.setVisibility(View.VISIBLE);

                    // checks the users result, in this case it will check if the user checked in
                    // from within a target distance of the clue answer
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

                    // create map area and put it in the activity, this will show the user where
                    // they checked in for that clue in relation to a circle area where the were meant to
                    ViewHolder mapHolder;

                    mapHolder = new ViewHolder();
                    mapHolder.mapView = (MapView) convertView.findViewById(R.id.lite_listrow_map);

                    convertView.setTag(mapHolder);

                    LatLng userLatLng = getCoords(answer.getLocation());
                    LatLng clueLatLng = getCoords(clue.getClueLocation());
                    double thresholdRadius = getRadius(clue.getClueAnswer());

                    // displays the map to the screen, which is a google 'lite', so you cannot
                    // move it around like in the clue display activity, you click through to google maps
                    // from it however
                    mapHolder.initializeMapView(clueLatLng, userLatLng, thresholdRadius);
                default:
                    Log.e(TAG, "Invalid clue type");
            }

            return convertView;
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

    // this inner class holds the map details and contains the callback method
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
            map.addMarker(new MarkerOptions().position(userlatLng));

            map.addCircle(new CircleOptions()
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
