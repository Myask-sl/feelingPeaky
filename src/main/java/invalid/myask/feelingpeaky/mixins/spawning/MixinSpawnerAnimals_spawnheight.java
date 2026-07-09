package invalid.myask.feelingpeaky.mixins.spawning;

import java.util.Random;

import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(SpawnerAnimals.class)
public class MixinSpawnerAnimals_spawnheight {
    @WrapOperation(method = "func_151350_a", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 2))
    private static int spawnRange(Random instance, int ogBound, Operation<Integer> original, World world) {
        int spawnMin = ((IExpandedWorldOrProvider)world).getWorldSpawnMinY(), newBound = ((IExpandedWorldOrProvider)world).getWorldSpawnMaxY() - spawnMin;
        if (newBound < 1) newBound = 1;
        return original.call(instance, newBound) + spawnMin;
    }
}
