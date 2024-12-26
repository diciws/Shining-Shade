package at.dici.shade.userapplications.phasmoGuessr;


import java.awt.*;

public class PgSettings {

    // ----- General -----
    public static final String BUTTON_ID_START = "pg_start";
    static final String IMG_ID_CHARSET = "0123456789BCDFGHJKLMNPQRSTVWXYZ";
    static final String SAVE_FILE_NAME = "games.pg";

    // ----- Points ------
    public static final int POINTS_MAX_MAP = 5;
    public static final int POINTS_MAX_ROOM = 8;
    public static final int POINTS_BONUS_FOR_BOTH_IN_ONE = 3;
    public static final int POINTS_MIN_MAP = 1;
    public static final int POINTS_MIN_ROOM = 2;
    public static final int MAX_MAP_FAILS_PLAYER = 3;
    public static final int MAX_ROOM_FAILS_PLAYER = 5;



    // ----- Timings ------
    static final long DELAY_BETWEEN_ROUNDS = 5; // in seconds
    static final long DELAY_REMOVE_INFO_FROM_IMAGE = 300; // in seconds
    static final long COOLDOWN_COUNT_WRONG_GUESSES = 5000; // in milliseconds


    // ----- Gameplay ------
    static final int MAX_ROUNDS = 5;
    static final int MAX_MAP_FAILS_GAME = 10;
    static final int MAX_ROOM_FAILS_GAME = 3;
    static final int MESSAGES_BEFORE_REFRESH = 8;
    static final int WINNER_LIST_MAX_SIZE = 5;
    static final int CHARS_TO_NOT_REVEAL = 2;
    static final int MAX_FAILS_AFTER_FULL_REVEAL = 0; // true value has CHARS_TO_NOT_REVEAL added.

    public static class EmbedColor{
        public static final Color PGINFO = Color.darkGray;
        static final Color CORRECT_GUESS = Color.GREEN;
        static final Color WRONG_GUESS = Color.RED;
        static final Color IMAGE = Color.darkGray;
        static final Color REVEALED_MAP = Color.darkGray;
        static final Color GAME_OVER = Color.darkGray;
    }
}
