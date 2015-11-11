package com.sheep.electric.treasurehunt;

import android.net.Uri;

import java.util.UUID;

/**
 * Created by Shane on 02/11/2015.
 */
public class Answer {

    private UUID mClueId;
    private UUID mId;
    private UUID mPlayerId;
    private UUID mHuntId;
    private Uri mPictureUri;
    private String mText;
    private String mLocation;

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
}
