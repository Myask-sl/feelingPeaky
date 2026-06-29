package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(ChunkCache.class)
public abstract class MixinChunkCache implements IExpandedWorldOrProvider {
    @Shadow
    private World worldObj;

    @Override
    public int getWorldMinY() {
        return ((IExpandedWorldOrProvider) worldObj.provider).getWorldMinY();
    }

    @Override
    public int getWorldMaxY() {
        return ((IExpandedWorldOrProvider) worldObj.provider).getWorldMaxY();
    }

    @Override
    public int getSubChunkCount() {
        return ((IExpandedWorldOrProvider) worldObj.provider).getSubChunkCount();
    }

    @Override
    public int getNegativeChunkCount() { return ((IExpandedWorldOrProvider) worldObj.provider).getNegativeChunkCount(); }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y >= 0")
    @ModifyExpressionValue(method = {"getBlock",
        "getSpecialBlockBrightness"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isInYRangeMin (boolean old, int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        return p_147439_2_ >= getWorldMinY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y < 256")
    @ModifyExpressionValue(method = {"getBlock",
        "getSpecialBlockBrightness"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isInYRangeMax (boolean old, int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        return p_147439_2_ <= getWorldMaxY();
    }


    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y < 0")
    @ModifyExpressionValue(method = {"getBlockMetadata",
        "getSkyBlockTypeBrightness",
        "getSpecialBlockBrightness"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean belowBottom (boolean old, int x, int y, int z) {
        return y < getWorldMinY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y >= 256")
    @ModifyExpressionValue(method = {"getBlockMetadata",
        "getSkyBlockTypeBrightness",
        "getSpecialBlockBrightness"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean ott (boolean old, int x, int y, int z) {
        return y > getWorldMaxY();
    }


}
