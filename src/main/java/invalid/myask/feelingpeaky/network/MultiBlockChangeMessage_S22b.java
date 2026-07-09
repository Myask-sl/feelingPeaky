package invalid.myask.feelingpeaky.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.chunk.Chunk;

import io.netty.buffer.ByteBuf;

public class MultiBlockChangeMessage_S22b implements IMessage {
    private int chunkX, chunkZ;
    private short blockUpdateCount;

    private byte[] xzs;
    private short[] ys;
    private short[] blockIDs; //NEID-level only, write another for EID
    private byte[] blockMetas;

    public MultiBlockChangeMessage_S22b() {}

    public MultiBlockChangeMessage_S22b(short count, short[] packedPositions, byte[] extraYbits, Chunk chunk) {
        int subX, subZ;
        short y;
        chunkX = chunk.xPosition; chunkZ = chunk.zPosition;
        blockUpdateCount = count;

        xzs = new byte[count];
        ys = new short[count];
        blockIDs = new short[count];
        blockMetas = new byte[count];

        for (int i = 0; i < count; i++) {
            xzs[i] = (byte) (packedPositions[i] >> 8);
            subX = xzs[i] >> 4;
            subZ = xzs[i] & 0xF;
            ys[i] = y = (short) ((packedPositions[i] & 0xFF) +
                        switch (extraYbits[i]) {
                            case 1 -> 256;
                            case 2 -> -512;
                            case 3 -> -256;
                            default -> 0;
                        });
            blockIDs[i] = (short) Block.getIdFromBlock(chunk.getBlock(subX, y, subZ));
            blockMetas[i] = (byte) chunk.getBlockMetadata(subX, y, subZ);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
        blockUpdateCount = buf.readShort();
        if (blockUpdateCount > 0) {
            xzs = new byte[blockUpdateCount];
            ys = new short[blockUpdateCount];
            blockIDs = new short[blockUpdateCount];
            blockMetas = new byte[blockUpdateCount];
            for (int i = 0; i < blockUpdateCount; i++) {
                xzs[i] = buf.readByte();
                ys[i] = buf.readShort();
                blockIDs[i] = buf.readShort();
                blockMetas[i] = buf.readByte();
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(chunkX);
        buf.writeInt(chunkZ);
        if (xzs != null && ys != null && blockIDs != null && blockMetas != null) {
            buf.writeShort(blockUpdateCount);
            for (int i = 0; i < blockUpdateCount; i++) {
                buf.writeByte(xzs[i]);
                buf.writeShort(ys[i]);
                buf.writeShort(blockIDs[i]);
                buf.writeByte(blockMetas[i]);
            }
        } else buf.writeInt(0);
    }

    public void deploy (WorldClient world) {
        int x, z, chunkXS = chunkX << 4, chunkZS = chunkZ << 4;
        for (int i = 0; i < blockUpdateCount; i++) {
            x = chunkXS | (xzs[i] >> 4);
            z = chunkZS | (xzs[i] & 0xF);
            world.func_147492_c(x, ys[i], z, Block.getBlockById(blockIDs[i]), blockMetas[i]);
        }
    }
}
