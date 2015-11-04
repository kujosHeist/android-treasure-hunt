package com.sheep.electric.treasurehunt;

import java.util.UUID;

/**
 * Created by Shane on 03/11/2015.
 */
public class Hunt {

    private String mName;
    private UUID mId;
    private String mLocation;
    private String mCreator;

    public Hunt(String name, String location, String creator){
        mName = name;
        mId = UUID.randomUUID();
    }

    public Hunt(UUID uuid) {
        mId = uuid;
    }

    public Hunt() {
        mId = UUID.randomUUID();
    }

    public String toString(){
        return mName;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getCreator() {
        return mCreator;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public void setCreator(String creator) {
        mCreator = creator;
    }
}
