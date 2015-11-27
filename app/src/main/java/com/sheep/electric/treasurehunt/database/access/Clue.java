package com.sheep.electric.treasurehunt.database.access;

import java.util.UUID;


// stores clues
public class Clue {

    public static final int PICTURE = 0;
    public static final int TEXT = 1;
    public static final int LOCATION = 2;

    private UUID mId;
    private UUID mHuntId;
    private String mClueText;
    private String mClueLocation;
    private String mClueAnswer;
    private int mClueType;


    public Clue(String clueText, int clueType, String clueAnswer, String clueLocation ){
        mId = UUID.randomUUID();
        mClueText = clueText;
        mClueType = clueType;
        mClueAnswer = clueAnswer;
        mClueLocation = clueLocation;
    }

    public Clue(UUID uuid) {
        mId = uuid;
    }

    public Clue(){mId = UUID.randomUUID();};

    public int getClueType() {
        return mClueType;
    }

    public void setClueType(int clueType) {
        mClueType = clueType;
    }

    public String getClueText() {
        return mClueText;
    }

    public void setClueText(String clueText) {
        mClueText = clueText;
    }

    public String getClueLocation() {
        return mClueLocation;
    }

    public void setClueLocation(String clueLocation) {
        mClueLocation = clueLocation;
    }

    public String getClueAnswer() {
        return mClueAnswer;
    }

    public void setClueAnswer(String clueAnswer) {
        mClueAnswer = clueAnswer;
    }

    public boolean checkAnswer(String clueAnswer){
        if(clueAnswer.equals(mClueAnswer)){
            return true;
        }else{
            return false;
        }
    }

    public void setHuntId(String huntId) {
        mHuntId = UUID.fromString(huntId);
    }

    public void setClueType(String clueType) {
        mClueType = Integer.parseInt(clueType);
    }

    public UUID getId() {
        return mId;
    }

    public UUID getHuntId() {
        return mHuntId;
    }

    public String toString(){
        return "Text: " +  mClueText + ", Type: " + mClueType;
    }
}
