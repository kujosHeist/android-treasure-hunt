package com.sheep.electric.treasurehunt.util;

import android.content.Context;

import com.sheep.electric.treasurehunt.database.access.Clue;
import com.sheep.electric.treasurehunt.database.access.Clues;
import com.sheep.electric.treasurehunt.database.access.Hunt;
import com.sheep.electric.treasurehunt.database.access.Hunts;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// this class is used to read in the hunt details form the hunts.xml file, only needs to do this
// once after the app gets installed, then the hunt details are stored in the db
public class XMLPullParserHandler {
    private String mText;
    private Hunt mHunt;
    private Clue mClue;
    private UUID mHuntId;

    private Context mContext;

    public XMLPullParserHandler(Context context) {
        mContext = context;
    }

    public boolean parse(InputStream is) {

        Clues cluesDb = new Clues(mContext);
        Hunts huntsDb = new Hunts(mContext);

        List<Clue> clues = new ArrayList<Clue>();
        List<Hunt> hunts = new ArrayList<Hunt>();

        XmlPullParserFactory factory;
        XmlPullParser xpp;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();

            xpp.setInput(is, null);

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("hunt")) {
                            mHunt = new Hunt();
                            mHuntId = mHunt.getId();

                        } else if (tagName.equalsIgnoreCase("clue")) {
                            mClue = new Clue();
                            mClue.setHuntId(mHuntId.toString());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        mText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("hunt")) { // mHunt over
                            hunts.add(mHunt);

                        } else if (tagName.equalsIgnoreCase("clue")) {
                            clues.add(mClue);
                            //cluesDb.addClue(mClue);


                        } else if (tagName.equalsIgnoreCase("name")) {
                            mHunt.setName(mText);
                        } else if (tagName.equalsIgnoreCase("hunt_location")) {
                            mHunt.setLocation(mText);
                        } else if (tagName.equalsIgnoreCase("creator")) {
                            mHunt.setCreator(mText);


                        } else if (tagName.equalsIgnoreCase("text")) {
                            mClue.setClueText(mText);
                        } else if (tagName.equalsIgnoreCase("type")) {
                            mClue.setClueType(mText);
                        } else if (tagName.equalsIgnoreCase("answer")) {
                            mClue.setClueAnswer(mText);
                        } else if (tagName.equalsIgnoreCase("location")) {
                            mClue.setClueLocation(mText);
                        }
                        break;
                }
                eventType = xpp.next();
            }

            }catch(Exception e){
                e.printStackTrace();
                return false;
            }

        for(Clue c: clues){
            cluesDb.addClue(c);
        }
        for(Hunt h: hunts){
            huntsDb.addHunt(h);
        }
        return true;
    }

}

