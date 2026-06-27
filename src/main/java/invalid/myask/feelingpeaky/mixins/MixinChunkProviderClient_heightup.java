package invalid.myask.feelingpeaky.mixins;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.world.chunk.TallChunk;

@Mixin(ChunkProviderClient.class)
public class MixinChunkProviderClient_heightup {
    @WrapOperation(method = "loadChunk", at = @At(value = "NEW", target = "(Lnet/minecraft/world/World;II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk replaceChunk(World world, int chunX, int chunZ, Operation<Chunk> original) {
        return new TallChunk(world, chunX, chunZ);
    }
}
