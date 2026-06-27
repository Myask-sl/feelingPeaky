package invalid.myask.feelingpeaky.mixins;

import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

@Mixin(World.class)
public abstract class MixinWorld implements IExpandedWorldOrProvider {
    @Shadow
    public final WorldProvider provider = null;

    @Shadow
    public abstract Chunk getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_);

    @Shadow
    public abstract Chunk getChunkFromBlockCoords(int p_72938_1_, int p_72938_2_);

    @Override
    public int getWorldMinY() {
        return ((IExpandedWorldOrProvider)provider).getWorldMinY();
    }

    @Override
    public int getWorldMaxY() {
        return ((IExpandedWorldOrProvider)provider).getWorldMaxY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y >= 0")
    @ModifyExpressionValue(method = {"getBlock",
        "blockExists",
        "checkChunksExist",
        "getTileEntity"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isInYRangeMin (boolean old, int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        return p_147439_2_ >= getWorldMinY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y < 256")
    @ModifyExpressionValue(method = {"getBlock",
        "blockExists",
        "checkChunksExist",
        "getTileEntity"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isInYRangeMax (boolean old, int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        return p_147439_2_ <= getWorldMaxY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y >= 0")
    @ModifyExpressionValue(method = {
            "canBlockFreezeBody",
                "canSnowAtBody"},
    at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    private boolean isInYRangeMinUnmapped (boolean old, int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        return p_147439_2_ >= getWorldMinY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y < 256")
    @ModifyExpressionValue(method = {
        "canBlockFreezeBody",
        "canSnowAtBody"},
        at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    private boolean isInYRangeMaxUnmapped (boolean old, int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        return p_147439_2_ <= getWorldMaxY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y < 0")
    @ModifyExpressionValue(method = {"setBlock(IIILnet/minecraft/block/Block;II)Z",
        "getBlockMetadata",
        "setBlockMetadataWithNotify",
        "getFullBlockLightValue",
        "getBlockLightValue_do",
        "getSkyBlockTypeBrightness",
        "getSavedLightValue"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean belowBottom (boolean old, int x, int y, int z) {
        return y < getWorldMinY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y >= 256")
    @ModifyExpressionValue(method = {"setBlock(IIILnet/minecraft/block/Block;II)Z",
        "getBlockMetadata",
        "setBlockMetadataWithNotify",
        "getFullBlockLightValue",
        "getBlockLightValue_do",
        "getSkyBlockTypeBrightness",
        "getSavedLightValue"}, //TODO: Check assignments these lead to.
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean ott (boolean old, int x, int y, int z) {
        return y > getWorldMaxY();
    }

    @ModifyReturnValue(method = "getHeightValue",
        slice =
        @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;chunkExists(II)Z")),
        at = @At(value = "RETURN", ordinal = 0))
    private int getANewHeight(int old) {
        return getWorldMinY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y >= 0")
    @ModifyExpressionValue(method = {"setLightValue"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isAboveBottom (boolean old, EnumSkyBlock whichLight, int x, int y, int z, int pN) {
        return y >= getWorldMinY();
    }
    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y < 256")
    @ModifyExpressionValue(method = {"setLightValue"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isBelowTop (boolean old, EnumSkyBlock whichLight, int x, int y, int z, int pN) {
        return  y <= getWorldMaxY();
    }

    @Definition(id = "k", local = @Local(type = int.class, name = "k"))
    @Expression("k > 0")
    @ModifyExpressionValue(method = "getTopSolidOrLiquidBlock", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean anotherOverBottom (boolean old, @Local(name = "k") int k) {
        return k > getWorldMinY();
    }

    @ModifyReturnValue(method = "getTopSolidOrLiquidBlock", at = @At(value = "RETURN", ordinal = 1))
    private int belowMapHeight(int original) {
        return original == -1 ? original : getWorldMinY() - 1;
    }

    @ModifyReturnValue(method = "getBlockLightOpacity", remap = false,
        at = @At(value = "RETURN", ordinal = 1))
    private int newPacity (int old, int x, int y, int z) {
        return getChunkFromChunkCoords(x >> 4, z >> 4).func_150808_b(x & 15, y, z & 15);
    }

    @ModifyReturnValue(method = "getChunkHeightMapMinimum", at = @At("RETURN"))
    private int fakedBottom(int old) {
        if (old == 0) {
            return getWorldMinY();
        }
        return old;
    }
}
