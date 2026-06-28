package invalid.myask.feelingpeaky.mixins;


import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.management.PlayerManager$PlayerInstance")
public class MixinPlayerManager$PlayerInstance_heightup {
    @Unique
    byte feelingPeaky$extraBits[] = new byte[64];
    @Shadow
    private int numberOfTilesToUpdate;

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

    @ModifyVariable(method = "flagChunkForUpdate", at = @At("STORE"))
    private short trimbit (short original, int subX, int y, int subZ) {
        return (short) (subX << 12 | subZ << 8 | (y & 255));
    }

    @Expression("? & 255")
    @ModifyExpressionValue(method = "sendChunkUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int reBit (int originalY) {
        if ((feelingPeaky$extraBits[0] | 1) == 1) originalY += 256;
        if ((feelingPeaky$extraBits[0] | 2) == 2) originalY -= 512;
        return originalY;
    }
}

