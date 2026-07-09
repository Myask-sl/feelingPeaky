package invalid.myask.feelingpeaky;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
    public static int SUBCHUNK_COUNT = 24; //TODO: make per-world
    public static int LIGHT_QUEUE_COUNT = 4096;
    public static int NEGATIVE_SUBCHUNK_COUNT = 4; //TODO: make per-world
    public static  boolean OVERRIDE_SERVER_BUILDHEIGHT = true;


    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        SUBCHUNK_COUNT = configuration.getInt("SUBCHUNK_COUNT", Configuration.CATEGORY_GENERAL, SUBCHUNK_COUNT, 1, 32, "How many subchunks (16 y tall parts) per chunk total. 16 vanilla, 24 Caves and Cliffs+.");
        NEGATIVE_SUBCHUNK_COUNT = configuration.getInt("NEGATIVE_SUBCHUNK_COUNT", Configuration.CATEGORY_GENERAL, NEGATIVE_SUBCHUNK_COUNT, 0, 32, "How many negative subchunks (16 y tall parts) per chunk. 0 vanilla, 4 Caves and Cliffs+.");

        OVERRIDE_SERVER_BUILDHEIGHT = configuration.getBoolean("OVERRIDE_SERVER_BUILDHEIGHT", Configuration.CATEGORY_GENERAL,
            OVERRIDE_SERVER_BUILDHEIGHT, "Whether to override server build height.");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
