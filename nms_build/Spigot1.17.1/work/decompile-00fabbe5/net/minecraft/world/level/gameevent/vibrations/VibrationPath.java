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

    public int a() {
        return this.arrivalInTicks;
    }

    public BlockPosition b() {
        return this.origin;
    }

    public PositionSource c() {
        return this.destination;
    }

    public static VibrationPath a(PacketDataSerializer packetdataserializer) {
        BlockPosition blockposition = packetdataserializer.f();
        PositionSource positionsource = PositionSourceType.c(packetdataserializer);
        int i = packetdataserializer.j();

        return new VibrationPath(blockposition, positionsource, i);
    }

    public static void a(PacketDataSerializer packetdataserializer, VibrationPath vibrationpath) {
        packetdataserializer.a(vibrationpath.origin);
        PositionSourceType.a(vibrationpath.destination, packetdataserializer);
        packetdataserializer.d(vibrationpath.arrivalInTicks);
    }
}
