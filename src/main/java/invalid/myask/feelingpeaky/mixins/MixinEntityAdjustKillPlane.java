package invalid.myask.feelingpeaky.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(Entity.class)
public class MixinEntityAdjustKillPlane {
    @Shadow
    public double posY;

    @Shadow
    public World worldObj;

    @Expression("?.? < -64.0D")
    @ModifyExpressionValue(method = "onEntityUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean adjustKillPlane(boolean original) {
        return posY < ((IExpandedWorldOrProvider)worldObj).getKillPLaneY();
    }
}
