package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;

public class VibrationParticleOption implements ParticleParam {

    public static final Codec<VibrationParticleOption> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(VibrationPath.CODEC.fieldOf("vibration").forGetter((vibrationparticleoption) -> {
            return vibrationparticleoption.vibrationPath;
        })).apply(instance, VibrationParticleOption::new);
    });
    public static final ParticleParam.a<VibrationParticleOption> DESERIALIZER = new ParticleParam.a<VibrationParticleOption>() {
        @Override
        public VibrationParticleOption b(Particle<VibrationParticleOption> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            float f = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f1 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f2 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f3 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f4 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f5 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            int i = stringreader.readInt();
            BlockPosition blockposition = new BlockPosition((double) f, (double) f1, (double) f2);
            BlockPosition blockposition1 = new BlockPosition((double) f3, (double) f4, (double) f5);

            return new VibrationParticleOption(new VibrationPath(blockposition, new BlockPositionSource(blockposition1), i));
        }

        @Override
        public VibrationParticleOption b(Particle<VibrationParticleOption> particle, PacketDataSerializer packetdataserializer) {
            VibrationPath vibrationpath = VibrationPath.a(packetdataserializer);

            return new VibrationParticleOption(vibrationpath);
        }
    };
    private final VibrationPath vibrationPath;

    public VibrationParticleOption(VibrationPath vibrationpath) {
        this.vibrationPath = vibrationpath;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        VibrationPath.a(packetdataserializer, this.vibrationPath);
    }

    @Override
    public String a() {
        BlockPosition blockposition = this.vibrationPath.b();
        double d0 = (double) blockposition.getX();
        double d1 = (double) blockposition.getY();
        double d2 = (double) blockposition.getZ();

        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %d", IRegistry.PARTICLE_TYPE.getKey(this.getParticle()), d0, d1, d2, d0, d1, d2, this.vibrationPath.a());
    }

    @Override
    public Particle<VibrationParticleOption> getParticle() {
        return Particles.VIBRATION;
    }

    public VibrationPath c() {
        return this.vibrationPath;
    }
}
