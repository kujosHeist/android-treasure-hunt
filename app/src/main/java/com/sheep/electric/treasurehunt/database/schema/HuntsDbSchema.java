package com.sheep.electric.treasurehunt.database.schema;

public class HuntsDbSchema {
    public static final class HuntsTable {
        public static final String NAME = "hunts";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String LOCATION = "location";
            public static final String CREATOR = "creator";
        }
    }
}
