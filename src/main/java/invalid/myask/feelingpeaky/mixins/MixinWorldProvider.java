package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.WorldProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(WorldProvider.class)
public class MixinWorldProvider implements IExpandedWorldOrProvider {
    @Shadow
    public int getHeight() {return 42;} //dummy
    @Shadow
    public int getActualHeight() {return 68;}

    @Override
    public int getWorldMinY() {
        return 0;
    }

    @Override
    public int getWorldMaxY() {
        return getHeight() - 1;
    }
}
