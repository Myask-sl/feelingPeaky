package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.WorldProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(WorldProvider.class)
public abstract class MixinWorldProvider implements IExpandedWorldOrProvider {
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
}
