package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;

public class VibrationPath {

    public static final Codec<VibrationPath> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockPosition.CODEC.fieldOf("origin").forGetter((vibrationpath) -> {
            return vibrationpath.origin;
        }), PositionSource.CODEC.fieldOf("destination").forGetter((vibrationpath) -> {
            return vibrationpath.destination;
        }), Codec.INT.fieldOf("arrival_in_ticks").forGetter((vibrationpath) -> {
            return vibrationpath.arrivalInTicks;
        })).apply(instance, VibrationPath::new);
    });
    private final BlockPosition origin;
    private final PositionSource destination;
    private final int arrivalInTicks;

    public VibrationPath(BlockPosition blockposition, PositionSource positionsource, int i) {
        this.origin = blockposition;
        this.destination = positionsource;
        this.arrivalInTicks = i;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }

    public BlockPosition getOrigin() {
        return this.origin;
    }

    public PositionSource getDestination() {
        return this.destination;
    }

    public static VibrationPath read(PacketDataSerializer packetdataserializer) {
        BlockPosition blockposition = packetdataserializer.readBlockPos();
        PositionSource positionsource = PositionSourceType.fromNetwork(packetdataserializer);
        int i = packetdataserializer.readVarInt();

        return new VibrationPath(blockposition, positionsource, i);
    }

    public static void write(PacketDataSerializer packetdataserializer, VibrationPath vibrationpath) {
        packetdataserializer.writeBlockPos(vibrationpath.origin);
        PositionSourceType.toNetwork(vibrationpath.destination, packetdataserializer);
        packetdataserializer.writeVarInt(vibrationpath.arrivalInTicks);
    }
}
