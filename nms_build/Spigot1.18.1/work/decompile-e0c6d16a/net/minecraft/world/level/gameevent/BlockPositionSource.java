package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.level.World;

public class BlockPositionSource implements PositionSource {

    public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockPosition.CODEC.fieldOf("pos").xmap(Optional::of, Optional::get).forGetter((blockpositionsource) -> {
            return blockpositionsource.pos;
        })).apply(instance, BlockPositionSource::new);
    });
    final Optional<BlockPosition> pos;

    public BlockPositionSource(BlockPosition blockposition) {
        this(Optional.of(blockposition));
    }

    public BlockPositionSource(Optional<BlockPosition> optional) {
        this.pos = optional;
    }

    @Override
    public Optional<BlockPosition> getPosition(World world) {
        return this.pos;
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.BLOCK;
    }

    public static class a implements PositionSourceType<BlockPositionSource> {

        public a() {}

        @Override
        public BlockPositionSource read(PacketDataSerializer packetdataserializer) {
            return new BlockPositionSource(Optional.of(packetdataserializer.readBlockPos()));
        }

        public void write(PacketDataSerializer packetdataserializer, BlockPositionSource blockpositionsource) {
            Optional optional = blockpositionsource.pos;

            Objects.requireNonNull(packetdataserializer);
            optional.ifPresent(packetdataserializer::writeBlockPos);
        }

        @Override
        public Codec<BlockPositionSource> codec() {
            return BlockPositionSource.CODEC;
        }
    }
}
