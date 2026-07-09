package invalid.myask.feelingpeaky.mixins.networkmanagement;

import net.minecraft.network.play.server.S26PacketMapChunkBulk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import invalid.myask.feelingpeaky.Config;

@Mixin(S26PacketMapChunkBulk.class)
public class MixinS26PacketMapChunkBulk {
    @ModifyArg(method = "<init>(Ljava/util/List;)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/network/play/server/S21PacketChunkData;func_149269_a(Lnet/minecraft/world/chunk/Chunk;ZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;"))
    private int replaceFullChunkBitMask (int original) {
        return original == 65535 ? (1 << Config.SUBCHUNK_COUNT) - 1 : original;
    }
}
