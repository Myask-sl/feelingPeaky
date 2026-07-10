package invalid.myask.feelingpeaky;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.launchwrapper.Launch;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.


    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientConfig.synchronizeConfiguration(new File(Launch.minecraftHome, "config" + File.separator + "feelingpeaky_client.cfg"));
    }
}
