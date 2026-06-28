package invalid.myask.feelingpeaky;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
    public static boolean a_config;

    public static int SUBCHUNK_COUNT = 20;
    public static int LIGHT_QUEUE_COUNT = 4096;
    public static int NEGATIVE_SUBCHUNK_COUNT = 0;


    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        a_config = configuration.getBoolean("a_config", Configuration.CATEGORY_GENERAL,
            a_config, "A config variable.");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
