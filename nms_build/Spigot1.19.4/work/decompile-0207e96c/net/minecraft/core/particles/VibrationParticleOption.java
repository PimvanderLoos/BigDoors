package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3D;

public class VibrationParticleOption implements ParticleParam {

    public static final Codec<VibrationParticleOption> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(PositionSource.CODEC.fieldOf("destination").forGetter((vibrationparticleoption) -> {
            return vibrationparticleoption.destination;
        }), Codec.INT.fieldOf("arrival_in_ticks").forGetter((vibrationparticleoption) -> {
            return vibrationparticleoption.arrivalInTicks;
        })).apply(instance, VibrationParticleOption::new);
    });
    public static final ParticleParam.a<VibrationParticleOption> DESERIALIZER = new ParticleParam.a<VibrationParticleOption>() {
        @Override
        public VibrationParticleOption fromCommand(Particle<VibrationParticleOption> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            float f = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f1 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f2 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            int i = stringreader.readInt();
            BlockPosition blockposition = BlockPosition.containing((double) f, (double) f1, (double) f2);

            return new VibrationParticleOption(new BlockPositionSource(blockposition), i);
        }

        @Override
        public VibrationParticleOption fromNetwork(Particle<VibrationParticleOption> particle, PacketDataSerializer packetdataserializer) {
            PositionSource positionsource = PositionSourceType.fromNetwork(packetdataserializer);
            int i = packetdataserializer.readVarInt();

            return new VibrationParticleOption(positionsource, i);
        }
    };
    private final PositionSource destination;
    private final int arrivalInTicks;

    public VibrationParticleOption(PositionSource positionsource, int i) {
        this.destination = positionsource;
        this.arrivalInTicks = i;
    }

    @Override
    public void writeToNetwork(PacketDataSerializer packetdataserializer) {
        PositionSourceType.toNetwork(this.destination, packetdataserializer);
        packetdataserializer.writeVarInt(this.arrivalInTicks);
    }

    @Override
    public String writeToString() {
        Vec3D vec3d = (Vec3D) this.destination.getPosition((World) null).get();
        double d0 = vec3d.x();
        double d1 = vec3d.y();
        double d2 = vec3d.z();

        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), d0, d1, d2, this.arrivalInTicks);
    }

    @Override
    public Particle<VibrationParticleOption> getType() {
        return Particles.VIBRATION;
    }

    public PositionSource getDestination() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}
