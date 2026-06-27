package invalid.myask.feelingpeaky.mixins;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient_heightup extends World {
    public MixinWorldClient_heightup(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_, WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }

    @Override
    public void markBlockRangeForRenderUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (minY == 0 && maxY == 256) {
            minY = ((IExpandedWorldOrProvider) this).getWorldMinY();
            maxY = ((IExpandedWorldOrProvider) this).getWorldMaxY() + 1;
        }
        super.markBlockRangeForRenderUpdate(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
