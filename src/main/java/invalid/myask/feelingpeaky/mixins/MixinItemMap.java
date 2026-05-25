package invalid.myask.feelingpeaky.mixins;

import net.minecraft.item.ItemMap;
import net.minecraft.world.World;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(ItemMap.class)
public class MixinItemMap {
    @Definition(id = "l4", local = @Local(type = int.class, name = "l4"))
    @Expression("l4 > 1")
    @ModifyExpressionValue(method = "updateMapData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean plusOneAboveBottom (boolean old, World world, @Local(name = "l4") int l4) {
        return l4 > ((IExpandedWorldOrProvider)world).getWorldMinY() + 1;
    }

    @Definition(id = "l4", local = @Local(type = int.class, name = "l4"))
    @Expression("l4 > 0")
    @ModifyExpressionValue(method = "updateMapData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean aboveBottom (boolean old, World world, @Local(name = "l4") int l4) {
        return l4 > ((IExpandedWorldOrProvider)world).getWorldMinY();
    }
}
