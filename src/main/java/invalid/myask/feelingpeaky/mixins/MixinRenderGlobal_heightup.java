package invalid.myask.feelingpeaky.mixins;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import invalid.myask.feelingpeaky.Config;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal_heightup {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GLAllocation;generateDisplayLists(I)I", ordinal = 0))
    private int howMany (int original) {
        return original / 16 * Config.SUBCHUNK_COUNT;
    }

    @WrapOperation(method = "loadRenderers", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderChunksTall:I", opcode = Opcodes.PUTFIELD))
    private void handle(RenderGlobal heccaeity, int theWrite, Operation<Void> original) {
        original.call(heccaeity, Config.SUBCHUNK_COUNT);
    }

/*    //for negaheight
    @Definition(id = "WorldRenderer", type = WorldRenderer.class)
    @Expression("new WorldRenderer(?, ?, ?, @(?), ?, ?)")
    @ModifyExpressionValue(method = "loadRenderers", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int lowerBottom (int original) {
        return original - 16 * Config.NEGATIVE_SUBCHUNK_COUNT;
    }*/
}
