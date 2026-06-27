package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderFlat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.world.chunk.TallChunk;

@Mixin(ChunkProviderFlat.class)
public class MixinChunkProviderFlat_heightup {
    @WrapOperation(method = "provideChunk", at = @At(value = "NEW", target = "(Lnet/minecraft/world/World;II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk replaceChunkType(World w, int chunX, int chunZ, Operation<Chunk> original) {
        return new TallChunk(w, chunX, chunZ);
    }
}
