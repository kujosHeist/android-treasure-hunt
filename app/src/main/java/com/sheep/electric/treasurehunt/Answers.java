package com.sheep.electric.treasurehunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Shane on 08/11/2015.
 */
public class Answers {
    public HashMap<UUID, String> mClueAnswers;

    public Answers(){
        mClueAnswers = new HashMap<UUID, String>();
    }

    public void submitClueAnswer(UUID clueId, String answer){
        mClueAnswers.put(clueId, answer);
    }

    public HashMap<UUID, String> getClueAnswers(){
        return mClueAnswers;
    }

}
