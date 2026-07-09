package invalid.myask.feelingpeaky.mixins.client;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer_fogUnBlind {
    @Definition(id = "d0", local = @Local(type = double.class, name = "d0"))
    @Expression("(? + (? - ?) * (double)?) * ?")
    @ModifyExpressionValue(method = "updateFogColor", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private double red0 (double original, float fracTicks, @Local EntityLivingBase cameraman) {
        if (original < 0.05) {
            int minY = ((IExpandedWorldOrProvider)cameraman.worldObj).getWorldMinY();
            double lerpY = (cameraman.lastTickPosY + (cameraman.posY - cameraman.lastTickPosY) * fracTicks);
            if (lerpY > minY - 8) {
                if (lerpY > minY)
                    original = 0.05;
                else {
                    original = 0.05 * (lerpY - (minY - 8));
                    if (original < 0) original = 0.001;
                }
            }
        }
        return original;
    }
}
