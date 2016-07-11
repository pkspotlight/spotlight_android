package me.spotlight.spotlight.db;

/**
 * Created by Anatol on 7/10/2016.
 */
public class SpotlightsRepo {

    static private SpotlightsRepo instance;

    static public SpotlightsRepo getInstance() {
        if (null == instance) {
            instance = new SpotlightsRepo();
        }
        return instance;
    }

    private DatabaseHelper databaseHelper;

    // TODO:
}
