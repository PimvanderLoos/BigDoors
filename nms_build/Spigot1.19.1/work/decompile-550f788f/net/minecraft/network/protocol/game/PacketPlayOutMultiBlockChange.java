package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkSection;

public class PacketPlayOutMultiBlockChange implements Packet<PacketListenerPlayOut> {

    private static final int POS_IN_SECTION_BITS = 12;
    private final SectionPosition sectionPos;
    private final short[] positions;
    private final IBlockData[] states;
    private final boolean suppressLightUpdates;

    public PacketPlayOutMultiBlockChange(SectionPosition sectionposition, ShortSet shortset, ChunkSection chunksection, boolean flag) {
        this.sectionPos = sectionposition;
        this.suppressLightUpdates = flag;
        int i = shortset.size();

        this.positions = new short[i];
        this.states = new IBlockData[i];
        int j = 0;

        for (ShortIterator shortiterator = shortset.iterator(); shortiterator.hasNext(); ++j) {
            short short0 = (Short) shortiterator.next();

            this.positions[j] = short0;
            this.states[j] = chunksection.getBlockState(SectionPosition.sectionRelativeX(short0), SectionPosition.sectionRelativeY(short0), SectionPosition.sectionRelativeZ(short0));
        }

    }

    public PacketPlayOutMultiBlockChange(PacketDataSerializer packetdataserializer) {
        this.sectionPos = SectionPosition.of(packetdataserializer.readLong());
        this.suppressLightUpdates = packetdataserializer.readBoolean();
        int i = packetdataserializer.readVarInt();

        this.positions = new short[i];
        this.states = new IBlockData[i];

        for (int j = 0; j < i; ++j) {
            long k = packetdataserializer.readVarLong();

            this.positions[j] = (short) ((int) (k & 4095L));
            this.states[j] = (IBlockData) Block.BLOCK_STATE_REGISTRY.byId((int) (k >>> 12));
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeLong(this.sectionPos.asLong());
        packetdataserializer.writeBoolean(this.suppressLightUpdates);
        packetdataserializer.writeVarInt(this.positions.length);

        for (int i = 0; i < this.positions.length; ++i) {
            packetdataserializer.writeVarLong((long) (Block.getId(this.states[i]) << 12 | this.positions[i]));
        }

    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleChunkBlocksUpdate(this);
    }

    public void runUpdates(BiConsumer<BlockPosition, IBlockData> biconsumer) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i < this.positions.length; ++i) {
            short short0 = this.positions[i];

            blockposition_mutableblockposition.set(this.sectionPos.relativeToBlockX(short0), this.sectionPos.relativeToBlockY(short0), this.sectionPos.relativeToBlockZ(short0));
            biconsumer.accept(blockposition_mutableblockposition, this.states[i]);
        }

    }

    public boolean shouldSuppressLightUpdates() {
        return this.suppressLightUpdates;
    }
}
