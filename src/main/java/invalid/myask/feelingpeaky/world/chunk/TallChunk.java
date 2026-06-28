package invalid.myask.feelingpeaky.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import invalid.myask.feelingpeaky.Config;
import invalid.myask.feelingpeaky.ducks.IExpandedChunk;
import invalid.myask.feelingpeaky.ducks.IExpandedWorldOrProvider;

public class TallChunk extends Chunk implements IExpandedChunk {
    public TallChunk(World world, int chunkX, int chunkZ) {
        super(world, chunkX, chunkZ);
    }

    /*public TallChunk(World world, Block[] blocks, int chunkX, int chunkZ) {
        super(world, blocks, chunkX, chunkZ);
    }*/

    public TallChunk(World world, Block[] blocks, byte[] metas, int chunkX, int chunkZ) {
        super(world, blocks, metas, chunkX, chunkZ);
    }

    @Override
    public int getChunkMinY() {
        return getNegativeChunkCount() * -16;
    }

    @Override
    public int getChunkMaxY() {
        return (getSubChunkCount() - getNegativeChunkCount()) * 16 - 1;
    }

    @Override
    public int getNegativeChunkCount() {
        return ((IExpandedWorldOrProvider)worldObj).getNegativeChunkCount();
    }

    @Override
    public int getSubChunkCount() {
        return ((IExpandedWorldOrProvider)worldObj).getSubChunkCount();
    }

    @Override
    public int getTopFilledSegment() {
        int subchunkY = super.getTopFilledSegment();
        if (subchunkY > 0 || getBlockStorageArray()[subchunkY] != null) return subchunkY;
        for (int i = 1; i <= getNegativeChunkCount(); i++) {
            if (getBlockStorageArray()[subchunkY] != null)
                return getBlockStorageArray()[subchunkY].getYLocation();
        }
        return getChunkMinY();
    }

    @Override
    public boolean getAreLevelsEmpty(int minY, int maxY) {
        if (maxY < minY) return true;
        ExtendedBlockStorage ebs;
        if (minY < 0 || maxY < 0) {
            int subMax = Math.floorMod(maxY >> 4, getSubChunkCount());
            for (int subY = Math.floorMod(minY >> 4, getSubChunkCount()); subY < getSubChunkCount() && subY < subMax; subY++) {
                ebs = getBlockStorageArray()[subY];
                if (ebs != null && !ebs.isEmpty()) return false;
            }
        } else if (minY > 256 || maxY > 256) {
            for (int subY = Math.max(minY >> 4, 16); subY < getSubChunkCount() - getNegativeChunkCount(); subY++) {
                ebs = getBlockStorageArray()[subY];
                if (ebs != null && !ebs.isEmpty()) return false;
            }
        }
        return super.getAreLevelsEmpty(minY, maxY);
    }
}
