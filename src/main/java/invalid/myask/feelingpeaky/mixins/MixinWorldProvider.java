package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import invalid.myask.feelingpeaky.Config;
import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(WorldProvider.class)
public abstract class MixinWorldProvider implements IExpandedWorldOrProvider {
    @Shadow
    public WorldType terrainType;

    @Shadow (remap = false)
    public abstract int getHeight();
    @Shadow (remap = false)
    public abstract int getActualHeight();

    @Override
    public int getWorldMinY() {
        return 0;
    }

    @Override
    public int getWorldMaxY() {
        return getHeight() - 1;
    }

    @Override
    public int getSubChunkCount() {
        return Config.SUBCHUNK_COUNT;
    }

    @Override
    public int getNegativeChunkCount() {
        return Config.NEGATIVE_SUBCHUNK_COUNT;
    }

    @Override
    public int getWorldSpawnMaxY() {
        return (getSubChunkCount() - getNegativeChunkCount()) * 16 - 1;
    }

    @Override
    public int getWorldSpawnMinY() {
        return getNegativeChunkCount() * -16;
    }
}
