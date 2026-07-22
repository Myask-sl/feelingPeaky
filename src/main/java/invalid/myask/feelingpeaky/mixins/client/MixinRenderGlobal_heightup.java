package invalid.myask.feelingpeaky.mixins.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal_heightup {
    @Shadow
    private int renderChunksTall;

    @Shadow
    private WorldClient theWorld;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GLAllocation;generateDisplayLists(I)I", ordinal = 0))
    private int howMany (int original) {
        return original * 2; //This is before there's a World available, so let's assume maximum.
    }

    @WrapOperation(method = "loadRenderers", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderChunksTall:I", opcode = Opcodes.PUTFIELD))
    private void handle(RenderGlobal heccaeity, int theWrite, Operation<Void> original) {
        original.call(heccaeity, ((IExpandedWorldOrProvider)theWorld).getSubChunkCount());
    }

    @WrapOperation(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;blockExists(III)Z"))
    private boolean whyZero(WorldClient instance, int x, int y, int z, Operation<Boolean> original, @Local(ordinal = 0) Entity entity) {
        return original.call(instance, x, MathHelper.floor_double(entity.posY), z);
    }

    @Definition(id = "l2", local = @Local(name = "l2", type = int.class))
    @Expression("l2 * 16")
    @ModifyExpressionValue(method = "markRenderersForNewPosition", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int offsetDown(int original) {
        return original - 16 * ((IExpandedWorldOrProvider)theWorld).getNegativeChunkCount();
    }

    @Expression(" (? * ?.? + @(?)) * ?.? + ?")
    @ModifyExpressionValue(method = "markBlocksForUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int offsetDownRenderer(int original) {
        return (original + ((IExpandedWorldOrProvider)theWorld).getNegativeChunkCount()) % renderChunksTall;
    }
}
