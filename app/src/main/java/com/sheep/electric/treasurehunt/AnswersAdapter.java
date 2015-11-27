package com.sheep.electric.treasurehunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Shane on 10/11/2015.
 */
public class AnswersAdapter extends ArrayAdapter<Answer>  {

    private static final String TAG = "AnswersAdapter";

    public AnswersAdapter(Context context, ArrayList<Answer> answers) {
        super(context, 0, answers);
    }

    View mConvertView;
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        mConvertView = convertView;

        Answer answer = getItem(position);

        UUID clueId = answer.getClueId();
        Clues cluesDb = new Clues(getContext());

        Clue clue = cluesDb.getClue(clueId);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.answer_item, parent, false);
        }


        TextView clueTextView = (TextView) convertView.findViewById(R.id.clue_text);
        clueTextView.setText("Clue Text: " + clue.getClueText());

        TextView answerTextView  = (TextView) convertView.findViewById(R.id.user_answer_text);
        ImageView pictureView = (ImageView) convertView.findViewById(R.id.answer_image);
        TextView locationView = (TextView) convertView.findViewById(R.id.answer_location);


        switch (clue.getClueType()){
            case Clue.TEXT:
                answerTextView.setVisibility(View.VISIBLE);
                answerTextView.setText("Answer Text: " + answer.getText());


                String userAnswer = answer.getText();

                String[] correctAnswers = clue.getClueAnswer().split("\\|");
                Log.d(TAG, "There are " + correctAnswers.length + " correct answers");

                convertView.setBackgroundColor(Color.parseColor("RED"));  // sets answer to red by default
                for(int i = 0; i < correctAnswers.length; i++){
                    Log.d(TAG, "Correct Answer: " + correctAnswers[i] + ",  User Answer: " + userAnswer);

                    if(correctAnswers[i].equalsIgnoreCase(userAnswer)){
                        convertView.setBackgroundColor(Color.parseColor("GREEN"));
                        break;
                    }
                }


                pictureView.setVisibility(View.GONE);
                locationView.setVisibility(View.GONE);
                break;

            case Clue.PICTURE:
                pictureView.setVisibility(View.VISIBLE);

                Bitmap image = BitmapFactory.decodeFile(answer.getPictureUri().getPath());
                pictureView.setImageBitmap(image);

                convertView.setBackgroundColor(Color.parseColor("#ADD8E6"));

                answerTextView.setVisibility(View.GONE);
                locationView.setVisibility(View.GONE);
                break;
            case Clue.LOCATION:
                locationView.setVisibility(View.VISIBLE);
                Log.d(TAG, "Clue text: " + clue.getClueText());

                String[] clueAnswer = clue.getClueAnswer().split(",");

                double latitude = Double.parseDouble(clueAnswer[0]);
                double longitude = Double.parseDouble(clueAnswer[1]);
                double thresholdRadius = Double.parseDouble(clueAnswer[2]);

                String[] userLocation = answer.getLocation().split(",");  // gets the location submitted by user in form: lat,long
                Log.d(TAG, "User submitted location: " +  userLocation[0] + "," + userLocation[1]);

                double distance = distFrom(latitude, longitude, Double.parseDouble(userLocation[0]), Double.parseDouble(userLocation[1]));
                Log.d(TAG, "Distance from checkin to target: " + distance+ ", threshold: "  +thresholdRadius);

                if(distance < thresholdRadius){
                    locationView.setText(R.string.location_was_correct);

                    convertView.setBackgroundColor(Color.parseColor("GREEN"));
                }else{
                    locationView.setText(R.string.location_incorrect);

                    convertView.setBackgroundColor(Color.parseColor("RED"));
                }


                answerTextView.setVisibility(View.GONE);
                pictureView.setVisibility(View.GONE);
                break;
            default:
                Log.e(TAG, "Invalid clue type");
        }

        return convertView;
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist;
    }


}
