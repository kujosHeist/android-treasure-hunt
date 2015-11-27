package com.sheep.electric.treasurehunt.database.access;

import java.util.UUID;

// used store players
public class Player {

    private String mName;
    private UUID mId;
    private String mHunt;

    private String mTeam;


    public Player(String name, String hunt, String team){
        mName = name;
        mId = UUID.randomUUID();
        mHunt = hunt;
        mTeam = team;
    }

    public Player(UUID uuid){
        mId = uuid;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public String getTeam() {
        return mTeam;
    }

    public String getHunt() {
        return mHunt;
    }





    public void setName(String name) {
        mName = name;
    }

    public void setTeam(String team) {
        mTeam = team;
    }

    public void setHunt(String hunt) {
        mHunt = hunt;
    }
}
