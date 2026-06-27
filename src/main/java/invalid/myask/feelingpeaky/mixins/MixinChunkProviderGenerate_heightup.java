package invalid.myask.feelingpeaky.mixins;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderGenerate;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.world.chunk.TallChunk;

@Mixin(ChunkProviderGenerate.class)
public class MixinChunkProviderGenerate_heightup {
    @WrapOperation(method = "provideChunk", at = @At(value = "NEW", target = "(Lnet/minecraft/world/World;[Lnet/minecraft/block/Block;[BII)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk replaceChunkType(World w, Block[] blocks, byte[] biomeIDs, int chunX, int chunZ, Operation<Chunk> original) {
        return new TallChunk(w, blocks, biomeIDs, chunX, chunZ);
    }
}
