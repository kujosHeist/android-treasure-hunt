package com.sheep.electric.treasurehunt;

/**
 * Created by Shane on 29/10/2015.
 */
public class Clue {

    private String mClueText;

    private String mClueLocation;

    private String mClueAnswer;

    private int mClueType;

    public static final int PICTURE = 0;
    public static final int TEXT = 1;
    public static final int LOCATION = 2;


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

    public Clue(String clueText, int clueType, String clueAnswer, String clueLocation ){
        mClueText = clueText;
        mClueType = clueType;
        mClueAnswer = clueAnswer;
        mClueLocation = clueLocation;
    }

    public boolean checkAnswer(String clueAnswer){
        if(clueAnswer.equals(mClueAnswer)){
            return true;
        }else{
            return false;
        }
    }

}
