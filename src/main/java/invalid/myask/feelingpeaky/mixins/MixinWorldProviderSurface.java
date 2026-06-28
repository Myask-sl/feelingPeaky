package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.WorldProviderSurface;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldProviderSurface.class)
public class MixinWorldProviderSurface extends MixinWorldProvider {
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
        return getHeight();
    }

    @Override
    public int getWorldMinY() {
        return 0;//16 * Config.NEGATIVE_SUBCHUNK_COUNT;
    }
}
