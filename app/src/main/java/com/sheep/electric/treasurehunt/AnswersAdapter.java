package com.sheep.electric.treasurehunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class AnswersAdapter extends ArrayAdapter<Answer> {

    private static final String TAG = "AnswersAdapter";

    public AnswersAdapter(Context context, ArrayList<Answer> answers) {
        super(context, 0, answers);
    }

    @Override
    public View getView(int poisiton, View convertView, ViewGroup parent){
        Answer answer = getItem(poisiton);

        UUID clueId = answer.getClueId();
        Clues cluesDb = new Clues(getContext());

        Clue clue = cluesDb.getClue(clueId);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.answer_item, parent, false);
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

                pictureView.setVisibility(View.GONE);
                locationView.setVisibility(View.GONE);
                break;

            case Clue.PICTURE:
                pictureView.setVisibility(View.VISIBLE);

                Bitmap image = BitmapFactory.decodeFile(answer.getPictureUri().getPath());
                pictureView.setImageBitmap(image);

                answerTextView.setVisibility(View.GONE);
                locationView.setVisibility(View.GONE);

                break;
            case Clue.LOCATION:
                locationView.setVisibility(View.VISIBLE);
                locationView.setText("Clue Location " + answer.getLocation());

                answerTextView.setVisibility(View.GONE);
                pictureView.setVisibility(View.GONE);
                break;
            default:
                Log.e(TAG, "Invalid clue type");
        }


        return convertView;
    }
}
