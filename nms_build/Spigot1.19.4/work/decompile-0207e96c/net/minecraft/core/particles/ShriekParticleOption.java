package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;

public class ShriekParticleOption implements ParticleParam {

    public static final Codec<ShriekParticleOption> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("delay").forGetter((shriekparticleoption) -> {
            return shriekparticleoption.delay;
        })).apply(instance, ShriekParticleOption::new);
    });
    public static final ParticleParam.a<ShriekParticleOption> DESERIALIZER = new ParticleParam.a<ShriekParticleOption>() {
        @Override
        public ShriekParticleOption fromCommand(Particle<ShriekParticleOption> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            int i = stringreader.readInt();

            return new ShriekParticleOption(i);
        }

        @Override
        public ShriekParticleOption fromNetwork(Particle<ShriekParticleOption> particle, PacketDataSerializer packetdataserializer) {
            return new ShriekParticleOption(packetdataserializer.readVarInt());
        }
    };
    private final int delay;

    public ShriekParticleOption(int i) {
        this.delay = i;
    }

    @Override
    public void writeToNetwork(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.delay);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.delay);
    }

    @Override
    public Particle<ShriekParticleOption> getType() {
        return Particles.SHRIEK;
    }

    public int getDelay() {
        return this.delay;
    }
}
