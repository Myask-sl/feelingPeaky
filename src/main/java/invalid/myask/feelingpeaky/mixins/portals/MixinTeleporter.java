package invalid.myask.feelingpeaky.mixins.portals;

import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(Teleporter.class)
public class MixinTeleporter {
    @Shadow
    @Final
    private WorldServer worldServerInstance;

    @Definition(id = "i2", local = @Local(type = int.class, name = "i2"))
    @Expression("i2 >= 0")
    @ModifyExpressionValue(method = "placeInExistingPortal", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean portalProbeAboveBottom(boolean original, @Local(type = int.class, name = "i2") int y) {
        return y > ((IExpandedWorldOrProvider)worldServerInstance).getWorldMinY();
    }
}
