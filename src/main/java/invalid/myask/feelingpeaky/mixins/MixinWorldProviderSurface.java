package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.WorldProviderSurface;

import org.spongepowered.asm.mixin.Mixin;

import invalid.myask.feelingpeaky.Config;

@Mixin(WorldProviderSurface.class)
public class MixinWorldProviderSurface extends MixinWorldProvider {
    @Override
    public int getHeight() {
        return 16 * (Config.SUBCHUNK_COUNT - Config.NEGATIVE_SUBCHUNK_COUNT);
    }

    @Override
    public int getActualHeight() {
        return getHeight();
    }

    @Override
    public int getWorldMinY() {
        return 16 * Config.NEGATIVE_SUBCHUNK_COUNT;
    }
}
