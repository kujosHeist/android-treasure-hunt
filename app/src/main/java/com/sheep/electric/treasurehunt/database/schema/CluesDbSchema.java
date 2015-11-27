package com.sheep.electric.treasurehunt.database.schema;


public class CluesDbSchema {
    public static final class CluesTable{
        public static final String NAME = "clues";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String HUNT_ID = "huntId";
            public static final String CLUE_TEXT = "clueText";
            public static final String TYPE = "type";
            public static final String ANSWER = "answer";
            public static final String LOCATION = "location";
        }
    }
}
