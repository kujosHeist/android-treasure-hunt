package com.sheep.electric.treasurehunt.database.access;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

// this class is used to store the users anwers, and is also created directly from the answers db
public class Answer {

    public static String TAG = "Answer";
    private UUID mClueId;
    private UUID mId;
    private UUID mPlayerId;
    private UUID mHuntId;
    private Uri mPictureUri;
    private String mText;
    private String mLocation;

    private boolean result;

    public Answer(UUID clueId, UUID playerId, UUID huntId){
        mId = UUID.randomUUID();
        mClueId = clueId;
        mPlayerId = playerId;
        mHuntId = huntId;
    }

    public Answer(UUID id, UUID clueId, UUID playerId, UUID huntId, Uri pictureUri, String text, String location){
        mId = id;
        mClueId = clueId;
        mPlayerId = playerId;
        mHuntId = huntId;
        mPictureUri = pictureUri;
        mText = text;
        mLocation = location;
    }


    public UUID getId() {
        return mId;
    }

    public UUID getPlayerId() {
        return mPlayerId;
    }

    public UUID getHuntId() {
        return mHuntId;
    }

    public Uri getPictureUri() {
        return mPictureUri;
    }

    public String getText() {
        return mText;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setPictureUri(Uri pictureUri) {
        mPictureUri = pictureUri;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public UUID getClueId() {
        return mClueId;
    }

    // sets the result of the answer to true or false
    public void setResult(Clue clue) {
        result = false;
        switch (clue.getClueType()){
            case Clue.TEXT:
                // text clues can have multiple correct answers separated by a bar
                String[] correctAnswers = clue.getClueAnswer().split("\\|");

                for(int i = 0; i < correctAnswers.length; i++){
                    Log.d(TAG, "Checking is: " + correctAnswers[i] + " == " + mText);
                    if(correctAnswers[i].equalsIgnoreCase(mText)){
                        result = true;
                        break;
                    }
                }
                break;

            // location clue answers check are they within a certain distance
            case Clue.LOCATION:

                LatLng userLatLng = getCoords(mLocation);
                LatLng clueLatLng = getCoords(clue.getClueLocation());
                double thresholdRadius = getRadius(clue.getClueAnswer());

                Log.d(TAG, "User submitted location: " +  userLatLng.latitude + "," + userLatLng.longitude);

                double distance = distFrom(clueLatLng.latitude, clueLatLng.longitude, userLatLng.latitude, userLatLng.longitude);
                Log.d(TAG, "Distance from checkin to target: " + distance + ", threshold: " + thresholdRadius);

                if(distance < thresholdRadius){
                    result = true;
                }else{
                    result = false;
                }

                break;
            case Clue.PICTURE:
                break;
        }
        Log.d(TAG, "Setting question " + clue.getClueText() + " as " + result);
    }

    public boolean getResult() {
        return result;
    }

    public LatLng getCoords(String location){
        String[] tokens = location.split(",");
        return new LatLng(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
    }

    public double getRadius(String location){
        String[] tokens = location.split(",");
        return Double.parseDouble(tokens[0]);
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
