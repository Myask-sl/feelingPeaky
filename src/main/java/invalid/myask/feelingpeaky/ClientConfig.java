package invalid.myask.feelingpeaky;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ClientConfig {
    public static float MIN_FOG = 1F/1024;
    public static float TAPER_VOIDFOG_THRESHOLD = 1F/64;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        MIN_FOG = configuration.getFloat("MIN_FOG", "client",
            MIN_FOG, 0, 1, "Minimum permitted [void] fog value");
        TAPER_VOIDFOG_THRESHOLD = configuration.getFloat("TAPER_VOIDFOG_THRESHOLD", "client",
            TAPER_VOIDFOG_THRESHOLD, 0, 1, "What voidfog value to taper off from, to 0 at (8 below bottom of map)).");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
