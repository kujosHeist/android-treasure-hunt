package com.sheep.electric.treasurehunt.database.schema;


public class AnswersDbSchema {

    public static final class AnswersTable{
        public static final String NAME = "answers";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String CLUE_ID = "clueId";
            public static final String PLAYER_ID = "playerId";
            public static final String HUNT_ID = "huntId";
            public static final String PICTURE_URI = "pictureUri";
            public static final String TEXT = "text";
            public static final String LOCATION = "location";


        }
    }
}
