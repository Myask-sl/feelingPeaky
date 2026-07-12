package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader_heightup {
    @ModifyVariable(method = "readChunkFromNBT", at = @At(value = "STORE"))
    private byte chunkReCount(byte value, World theWorld) {
        return (byte) ((IExpandedWorldOrProvider)theWorld).getSubChunkCount();
    }

    @Expression("?[?] = ?")
    @WrapOperation(method = "readChunkFromNBT", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void handleArrayAssign(ExtendedBlockStorage[] array, int index, ExtendedBlockStorage value, Operation<ExtendedBlockStorage> original) {
        array[Math.floorMod(index, array.length)] = value;
    }
}
