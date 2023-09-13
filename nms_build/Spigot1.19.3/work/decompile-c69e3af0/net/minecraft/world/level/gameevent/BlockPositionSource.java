package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class BlockPositionSource implements PositionSource {

    public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockPosition.CODEC.fieldOf("pos").forGetter((blockpositionsource) -> {
            return blockpositionsource.pos;
        })).apply(instance, BlockPositionSource::new);
    });
    final BlockPosition pos;

    public BlockPositionSource(BlockPosition blockposition) {
        this.pos = blockposition;
    }

    @Override
    public Optional<Vec3D> getPosition(World world) {
        return Optional.of(Vec3D.atCenterOf(this.pos));
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.BLOCK;
    }

    public static class a implements PositionSourceType<BlockPositionSource> {

        public a() {}

        @Override
        public BlockPositionSource read(PacketDataSerializer packetdataserializer) {
            return new BlockPositionSource(packetdataserializer.readBlockPos());
        }

        public void write(PacketDataSerializer packetdataserializer, BlockPositionSource blockpositionsource) {
            packetdataserializer.writeBlockPos(blockpositionsource.pos);
        }

        @Override
        public Codec<BlockPositionSource> codec() {
            return BlockPositionSource.CODEC;
        }
    }
}
