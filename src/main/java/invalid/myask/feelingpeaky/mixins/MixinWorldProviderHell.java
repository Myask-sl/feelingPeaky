package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.WorldProviderHell;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import invalid.myask.feelingpeaky.Config;

@Mixin(WorldProviderHell.class)
public class MixinWorldProviderHell extends MixinWorldProvider {
    @Unique
    private int CONST_HEIGHT;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void putHeight(CallbackInfo ci) {
        CONST_HEIGHT = 16 * (getSubChunkCount() - getNegativeChunkCount());
    }

    @Override
    public int getHeight() {
        return CONST_HEIGHT;
    }

    @Override
    public int getActualHeight() {
        return Config.NETHER_PORTAL_SPAWN_MAX;
    }

    @Override
    public int getWorldMinY() {
        return -16 * getNegativeChunkCount();
    }

    @Override
    public int getWorldSpawnMaxY() {
        return Config.NETHER_SPAWN_MAX;
    }
}
