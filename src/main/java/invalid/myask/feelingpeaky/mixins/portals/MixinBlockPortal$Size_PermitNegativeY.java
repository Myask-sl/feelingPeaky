package invalid.myask.feelingpeaky.mixins.portals;

import net.minecraft.block.BlockPortal;
import net.minecraft.world.World;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(BlockPortal.Size.class)
public class MixinBlockPortal$Size_PermitNegativeY {
    @Definition(id = "y", local = @Local(type = int.class, ordinal = 1, argsOnly = true))
    @Expression("y > 0")
    @ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean aboveWorldBottom (boolean original, World world, int x, int y) {
        return y > ((IExpandedWorldOrProvider)world).getWorldMinY();
    }
}
