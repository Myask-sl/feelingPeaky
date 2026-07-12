package invalid.myask.feelingpeaky.mixins;

import java.util.List;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import invalid.myask.feelingpeaky.ducks.IExpandedChunk;
import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

import static invalid.myask.feelingpeaky.Config.LIGHT_QUEUE_COUNT;

@Mixin(Chunk.class)
public abstract class MixinChunk_heightup implements IExpandedChunk {
    @Shadow
    public int[] heightMap;

    @Shadow
    private ExtendedBlockStorage[] storageArrays;

    @Shadow
    public List[] entityLists;

    @Shadow
    public World worldObj;

    @Override
    public int getChunkMinY() {
        return getNegativeChunkCount() * -16;
    }

    @Override
    public int getChunkMaxY() {
        return (getSubChunkCount() - getNegativeChunkCount()) * 16 - 1;
    }

    @Override
    public int getSubChunkCount() {
        return ((IExpandedWorldOrProvider)worldObj).getSubChunkCount();
    }

    @Override
    public int getNegativeChunkCount() {
        return ((IExpandedWorldOrProvider)worldObj).getNegativeChunkCount();
    }

    @ModifyReturnValue(method = "getTopFilledSegment", at = @At("RETURN"))
    private int getTopFilledNegativeSegment(int subchunkY) {
        if (subchunkY > 0 || storageArrays[subchunkY] != null) return subchunkY;
        for (int i = 1; i >= getNegativeChunkCount(); i--) {
            if (storageArrays[getSubChunkCount() - i] != null)
                return storageArrays[getSubChunkCount() - i].getYLocation();
        }
        return getChunkMinY();
    }

    @Inject(method = "getAreLevelsEmpty", at = @At("HEAD"), cancellable = true)
    private void checkOutsideChunks(int minY, int maxY, CallbackInfoReturnable<Boolean> cir) {
        if (maxY < minY) {
            cir.setReturnValue(true);
            return;
        }
        ExtendedBlockStorage ebs;
        if (minY < 0 || maxY < 0) {
            int subMax = Math.floorMod(maxY >> 4, getSubChunkCount());
            for (int subY = Math.floorMod(minY >> 4, getSubChunkCount()); subY < getSubChunkCount() && subY < subMax; subY++) {
                ebs = storageArrays[subY];
                if (ebs != null && !ebs.isEmpty()) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        } else if (minY > 256 || maxY > 256) {
            for (int subY = Math.max(minY >> 4, 16); subY < getSubChunkCount() - getNegativeChunkCount(); subY++) {
                ebs = storageArrays[subY];
                if (ebs != null && !ebs.isEmpty()) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;II)V",
        constant = @Constant(intValue = 16))
    private int feelingpeaky$subChunkCount(int old, World world) {
        return ((IExpandedWorldOrProvider)world).getSubChunkCount();
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;II)V",
        constant = @Constant(intValue = 4096))
    private int feelingpeaky$lightCount(int old) {
        return LIGHT_QUEUE_COUNT;
    }

    @ModifyConstant(method = "*", constant = @Constant(intValue = -999))
    private int feelingpeaky$presetBottom(int old) {
        return Integer.MIN_VALUE;
    }

    @ModifyVariable(method = "getTopFilledSegment()I",
        at = @At(value = "STORE"), name = "i")
    private int startAtTheTop(int old) {
        return getNegativeChunkCount();
    }

    @Definition(id = "y", local = @Local(type = int.class, name = "l"))
    @Expression("y > 0")
    @ModifyExpressionValue(method = {"generateHeightMap", "generateSkylightMap"},
    at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean bottomOutWhere(boolean old, @Local (name = "l") int y) {
        return y > getChunkMinY();
    }

    @Definition(id = "y", local = @Local(type = int.class, name = "i1"))
    @Expression("y > 0")
    @ModifyExpressionValue(method = {"generateSkylightMap", "relightBlock", "getPrecipitationHeight"},
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean bottomOutWhereAgain(boolean old, @Local (name = "i1") int y) {
        return y > getChunkMinY();
    }

    @Expression("? & 255")
    @ModifyExpressionValue(method = "relightBlock",
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private int reClampY255(int old, int p_76615_1_, int p_76615_2_, int p_76615_3_) { //, @Local (name = "l") int y) {
        return MathHelper.clamp_int(heightMap[p_76615_3_ << 4 | p_76615_1_], getChunkMinY(), getChunkMaxY());
    }

    @Expression("? >> 4 < ?")
    @ModifyExpressionValue(method = "getBlock",
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isInRange(boolean original, final int p_150810_1_, final int p_150810_2_, final int p_150810_3_) {
        return p_150810_2_ >= getChunkMinY() && p_150810_2_ <= getChunkMaxY();
    }

    @Expression("? >> 4 >= ?")
    @ModifyExpressionValue(method = "getBlockMetadata",
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isNotInRange(boolean original, int p_76628_1_, int p_76628_2_, int p_76628_3_) {
        return p_76628_2_ < getChunkMinY() || p_76628_2_ > getChunkMaxY();
    }

    @Definition(id = "storageArrays", field = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;")
    @Expression("this.storageArrays[?]")
    @WrapOperation(method = "*", at = @At("MIXINEXTRAS:EXPRESSION"))
    private ExtendedBlockStorage handler(ExtendedBlockStorage[] array, int index, Operation<ExtendedBlockStorage> original) {
        return array[Math.floorMod(index, array.length)];
    }

    @Definition(id = "storageArrays", field = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;")
    @Expression("this.storageArrays[?] = ?")
    @WrapOperation(method = "*", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void handleMultiAssign(ExtendedBlockStorage[] array, int index, ExtendedBlockStorage value, Operation<ExtendedBlockStorage> original) {
        array[Math.floorMod(index, array.length)] = value;
    }

    /**
     * The function itself clamps the index, so we have to modulo it early.
     */
    @WrapOperation(method = "addEntity",
       at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;floor_double(D)I", ordinal = 2))
    private int reindexEntityAdd(double p_76128_0_, Operation<Integer> original) {
        return Math.floorMod(original.call(p_76128_0_), getSubChunkCount());
    }

    /**
     * Note that next mixin will wrap the negatives.
     */
    @WrapOperation(method = {"getEntitiesWithinAABBForEntity", "getEntitiesOfTypeWithinAAAB"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_int(III)I"))
    private int clampLower(int val, int min, int max, Operation<Integer> original) {
        return original.call(val, min - getNegativeChunkCount(), max - getNegativeChunkCount());
    }

    @Definition(id = "entityLists", field = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Ljava/util/List;")
    @Expression("this.entityLists[?]")
    @WrapOperation(method = "*", at = @At("MIXINEXTRAS:EXPRESSION"))
    private List handler(List[] array, int index, Operation<ExtendedBlockStorage> original) {
        return array[Math.floorMod(index, array.length)];
    }

    @ModifyConstant(method = "getPrecipitationHeight", constant = @Constant(intValue = -1))
    private int reSentinelPrecipitationHeight(int original) {
        return -30000;
    }

    @Definition(id = "y", local = @Local(type = int.class, name = "l"))
    @Expression("y > 0")
    @ModifyExpressionValue(method = "func_150811_f", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean noReallyGoBelow(boolean original, @Local(ordinal = 3) int y) {
        return y > getChunkMinY();
    }
}
