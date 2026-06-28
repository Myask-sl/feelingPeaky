package invalid.myask.feelingpeaky.mixins;

import java.util.List;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import invalid.myask.feelingpeaky.ducks.IExpandedChunk;
import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;
import invalid.myask.feelingpeaky.world.chunk.TallChunk;

import static invalid.myask.feelingpeaky.Config.LIGHT_QUEUE_COUNT;
import static invalid.myask.feelingpeaky.Config.NEGATIVE_SUBCHUNK_COUNT;
import static invalid.myask.feelingpeaky.Config.SUBCHUNK_COUNT;

@Mixin(Chunk.class)
public class MixinChunk_heightup implements IExpandedChunk {
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
        return 0;
    }

    @Override
    public int getChunkMaxY() {
        return 255;
    }

    @Override
    public int getSubChunkCount() {
        return ((IExpandedWorldOrProvider)worldObj).getSubChunkCount();
    }

    @Override
    public int getNegativeChunkCount() {
        return ((IExpandedWorldOrProvider)worldObj).getNegativeChunkCount();
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;II)V",
        constant = @Constant(intValue = 16))
    private int feelingpeaky$subChunkCount(int old) {
        return ((Object)this) instanceof TallChunk ? SUBCHUNK_COUNT : old;
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;II)V",
        constant = @Constant(intValue = 4096))
    private int feelingpeaky$lightCount(int old) {
        return ((Object)this) instanceof TallChunk ? LIGHT_QUEUE_COUNT : old;
    }

    @ModifyConstant(method = "*", constant = @Constant(intValue = -999))
    private int feelingpeaky$presetBottom(int old) {
        return Integer.MIN_VALUE;
    }

    @ModifyVariable(method = "getTopFilledSegment()I",
        at = @At(value = "STORE"), name = "i")
    private int startAtTheTop(int old) {
        return ((Object)this) instanceof TallChunk ? old - NEGATIVE_SUBCHUNK_COUNT : old;
    }

    @Definition(id = "y", local = @Local(type = int.class, name = "l"))
    @Expression("y > 0")
    @ModifyExpressionValue(method = {"generateHeightMap", "generateSkylightMap"},
    at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean bottomOutWhere(boolean old, @Local (name = "l") int y) {
        return y > getChunkMinY();
    }

    @Definition(id = "y", local = @Local(type = int.class, name = "i1"))
    @Expression("y >> 4")
    @ModifyExpressionValue(method = "generateSkylightMap",
    at = @At("MIXINEXTRAS:EXPRESSION"))
    private int accountForNegativeIndices(int original) {
        return Math.floorMod(original, getSubChunkCount()); //Make it positive.
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
    private int reClampY255(int old, int p_76615_1_, int p_76615_2_, int p_76615_3_, @Local (name = "l") int y) {
        return MathHelper.clamp_int(heightMap[p_76615_3_ << 4 | p_76615_1_], getChunkMinY(), getChunkMaxY());
    }

    @Expression("? >> 4 < ?")
    @ModifyExpressionValue(method = "getBlock",
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isInRange(boolean original, final int p_150810_1_, final int p_150810_2_, final int p_150810_3_) {
        return p_150810_2_ >= getChunkMinY() && p_150810_2_ <= getChunkMaxY();
    }

    @Definition(id = "y", local = @Local(argsOnly = true, type = int.class, ordinal = 1))
    @Expression("y >> 4")
    @ModifyExpressionValue(method = "getBlock",
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private int accountForNegativeIndices2(int original) {
        return Math.floorMod(original, getSubChunkCount()); //Make it positive.
    }

    @Expression("? >> 4 >= ?")
    @ModifyExpressionValue(method = "getBlockMetadata",
        at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean isNotInRange(boolean original, int p_76628_1_, int p_76628_2_, int p_76628_3_) {
        return p_76628_2_ < getChunkMinY() || p_76628_2_ > getChunkMaxY();
    }

    @Definition(id = "storageArrays", field = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;")
    @Expression("storageArrays[?]")
    @WrapOperation(method = "*", at = @At("MIXINEXTRAS:EXPRESSION"))
    private ExtendedBlockStorage handler(ExtendedBlockStorage[] array, int index, Operation<ExtendedBlockStorage> original) {
        return array[Math.floorMod(index, array.length)];
    }

    @WrapOperation(method = "addEntity",
       at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;floor_double(D)I", ordinal = 2))
    private int reindexEntityAdd(double p_76128_0_, Operation<Integer> original) {
        return Math.floorMod(original.call(p_76128_0_), getSubChunkCount());
    }

    @WrapOperation(method = {"getEntitiesWithinAABBForEntity", "getEntitiesOfTypeWithinAAAB"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_int(III)I", ordinal = 0))
    private int wrapInsteadOfClamp(int val, int min, int max, Operation<Integer> original) {
        return Math.floorMod(val, getSubChunkCount());
    }

    @WrapOperation(method = {"getEntitiesWithinAABBForEntity", "getEntitiesOfTypeWithinAAAB"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_int(III)I", ordinal = 1))
    private int wrapInsteadOfClamp2(int val, int min, int max, Operation<Integer> original, @Local(ordinal = 0) int i) {
        int result = Math.floorMod(val, getSubChunkCount());
//        if (result <  i) result += entityLists.length;
        return result;
    }

    @Definition(id = "entityLists", field = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Ljava/util/List;")
    @Expression("entityLists[?]")
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
