package invalid.myask.feelingpeaky.mixins.networkmanagement;


import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.ChunkCoordIntPair;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import invalid.myask.feelingpeaky.FeelingPeaky;
import invalid.myask.feelingpeaky.network.MultiBlockChangeMessage_S22b;

@Mixin(targets = "net.minecraft.server.management.PlayerManager$PlayerInstance")
public class MixinPlayerManager$PlayerInstance_heightup {
    @Unique
    byte feelingPeaky$extraBits[] = new byte[64];
    @Shadow
    private int numberOfTilesToUpdate;
    @Shadow
    private int flagsYAreasToUpdate;
    @Shadow
    @Final
    private List<EntityPlayerMP> playersWatchingChunk;
    @Shadow
    @Final
    private ChunkCoordIntPair chunkLocation;
    @Shadow
    private short[] locationOfBlockChange;

    @Shadow (remap = false)
    @Final
    private PlayerManager this$0;

    @Inject(method = "flagChunkForUpdate", at = @At("HEAD"))
    private void outThereUpdate(int x, int y, int z, CallbackInfo ci) {
        boolean outThere = false, negative = false;
        if (y < 0 || y > 255) {
            outThere = true;
            negative = (y < 0);
        }
        if (numberOfTilesToUpdate == feelingPeaky$extraBits.length) {
            feelingPeaky$extraBits = java.util.Arrays.copyOf(feelingPeaky$extraBits, feelingPeaky$extraBits.length << 1);
        }
        byte result = (byte) ((outThere ? 1 : 0) | (negative ? 2 : 0));
        feelingPeaky$extraBits[numberOfTilesToUpdate] = result;
    }
/*
    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y >> 4")
    @ModifyExpressionValue(method = "flagChunkForUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int properBitNegative (int original, int x, int y, int z) {
        if (y >= 0) return original;
        return original + (Config.SUBCHUNK_COUNT - 32);
    }*/

    @ModifyVariable(method = "flagChunkForUpdate", at = @At("STORE"))
    private short trimbit (short original, int subX, int y, int subZ) {
        return (short) (subX << 12 | subZ << 8 | (y & 255));
    }

    @Expression("?[0] & 255")
    @ModifyExpressionValue(method = "sendChunkUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int reBit (int originalY) {
        if ((feelingPeaky$extraBits[0] | 1) == 1) originalY += 256;
        if ((feelingPeaky$extraBits[0] | 2) == 2) originalY -= 512;
        return originalY;
    }

    @Definition(id = "i", local = @Local(type = int.class, name = "i"))
    @Expression("?[i] & 255")
    @ModifyExpressionValue(method = "sendChunkUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int reBit2 (int originalY, @Local(type = int.class, name = "i") int i) {
        if ((feelingPeaky$extraBits[i] | 1) == 1) originalY += 256;
        if ((feelingPeaky$extraBits[i] | 2) == 2) originalY -= 512;
        return originalY;
    }

    /**
     * @author Myask
     * @reason Original packet packs everything so no more bits may be added.
     */
    @WrapOperation(method = "sendChunkUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerManager$PlayerInstance;sendToAllPlayersWatchingChunk(Lnet/minecraft/network/Packet;)V", ordinal = 2))
    private void replaceMultiBlockPacket(@Coerce Object callee, Packet packet, Operation<Void> op) {
        if (callee != this) throw new Error("Something other than FeelingPeaky is mucking with the multiblock packet send!");
        EntityPlayerMP viewer;
        IMessage message = new MultiBlockChangeMessage_S22b((short) numberOfTilesToUpdate, locationOfBlockChange,
            feelingPeaky$extraBits, this$0.getWorldServer().getChunkFromChunkCoords(chunkLocation.chunkXPos, chunkLocation.chunkZPos));
        for (int i = 0; i < playersWatchingChunk.size(); i++) {
            viewer = playersWatchingChunk.get(i);
            if (!viewer.loadedChunks.contains(this.chunkLocation))
                FeelingPeaky.NETWORK_WRAPPER.sendTo(message, viewer);
        }
    }
}
