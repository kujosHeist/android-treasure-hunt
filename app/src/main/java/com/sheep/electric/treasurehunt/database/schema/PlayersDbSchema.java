package com.sheep.electric.treasurehunt.database.schema;

public class PlayersDbSchema {
    public static final class PlayersTable {
        public static final String NAME = "players";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String TEAM = "team";
            public static final String HUNT = "hunt";
        }
    }

}
