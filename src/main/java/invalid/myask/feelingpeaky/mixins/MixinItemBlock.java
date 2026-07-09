package invalid.myask.feelingpeaky.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(ItemBlock.class)
public class MixinItemBlock {
    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y == 255")
    @ModifyExpressionValue(method = "onItemUse", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean topLayer(boolean original, ItemStack stackblock, EntityPlayer steve, World w,
                             int x, int y, int z, int side, float subX, float subY, float subZ) {
        return y == ((IExpandedWorldOrProvider) w).getWorldMaxY();
    }
}
