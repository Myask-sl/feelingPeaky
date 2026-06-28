package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;
import invalid.myask.feelingpeaky.world.chunk.TallChunk;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader_heightup {
    @WrapOperation(method = "readChunkFromNBT", at = @At(value = "NEW", target = "(Lnet/minecraft/world/World;II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk replaceChunkType(World w, int chunX, int chunZ, Operation<Chunk> original) {
        return new TallChunk(w, chunX, chunZ);
    }

    @ModifyVariable(method = "readChunkFromNBT", at = @At(value = "STORE"))
    private byte chunkReCount(byte value, World theWorld) {
        return (byte) ((IExpandedWorldOrProvider)theWorld).getSubChunkCount();
    }

}
