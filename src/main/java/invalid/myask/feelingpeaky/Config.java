package invalid.myask.feelingpeaky;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
    public static int SUBCHUNK_COUNT = 20;
    public static int LIGHT_QUEUE_COUNT = 4096;
    public static int NEGATIVE_SUBCHUNK_COUNT = 4;
    public static  boolean OVERRIDE_SERVER_BUILDHEIGHT = true;


    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        OVERRIDE_SERVER_BUILDHEIGHT = configuration.getBoolean("OVERRIDE_SERVER_BUILDHEIGHT", Configuration.CATEGORY_GENERAL,
            OVERRIDE_SERVER_BUILDHEIGHT, "Whether to override server build height.");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
