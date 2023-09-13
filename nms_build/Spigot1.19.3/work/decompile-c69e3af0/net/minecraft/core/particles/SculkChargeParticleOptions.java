package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;

public record SculkChargeParticleOptions(float roll) implements ParticleParam {

    public static final Codec<SculkChargeParticleOptions> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("roll").forGetter((sculkchargeparticleoptions) -> {
            return sculkchargeparticleoptions.roll;
        })).apply(instance, SculkChargeParticleOptions::new);
    });
    public static final ParticleParam.a<SculkChargeParticleOptions> DESERIALIZER = new ParticleParam.a<SculkChargeParticleOptions>() {
        @Override
        public SculkChargeParticleOptions fromCommand(Particle<SculkChargeParticleOptions> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            float f = stringreader.readFloat();

            return new SculkChargeParticleOptions(f);
        }

        @Override
        public SculkChargeParticleOptions fromNetwork(Particle<SculkChargeParticleOptions> particle, PacketDataSerializer packetdataserializer) {
            return new SculkChargeParticleOptions(packetdataserializer.readFloat());
        }
    };

    @Override
    public Particle<SculkChargeParticleOptions> getType() {
        return Particles.SCULK_CHARGE;
    }

    @Override
    public void writeToNetwork(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeFloat(this.roll);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.roll);
    }
}
