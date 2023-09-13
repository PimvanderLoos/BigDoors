package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.MathHelper;

public class ParticleParamRedstone implements ParticleParam {

    public static final ParticleParamRedstone a = new ParticleParamRedstone(1.0F, 0.0F, 0.0F, 1.0F);
    public static final Codec<ParticleParamRedstone> b = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("r").forGetter((particleparamredstone) -> {
            return particleparamredstone.d;
        }), Codec.FLOAT.fieldOf("g").forGetter((particleparamredstone) -> {
            return particleparamredstone.e;
        }), Codec.FLOAT.fieldOf("b").forGetter((particleparamredstone) -> {
            return particleparamredstone.f;
        }), Codec.FLOAT.fieldOf("scale").forGetter((particleparamredstone) -> {
            return particleparamredstone.g;
        })).apply(instance, ParticleParamRedstone::new);
    });
    public static final ParticleParam.a<ParticleParamRedstone> c = new ParticleParam.a<ParticleParamRedstone>() {
        @Override
        public ParticleParamRedstone b(Particle<ParticleParamRedstone> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            float f = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f1 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f2 = (float) stringreader.readDouble();

            stringreader.expect(' ');
            float f3 = (float) stringreader.readDouble();

            return new ParticleParamRedstone(f, f1, f2, f3);
        }

        @Override
        public ParticleParamRedstone b(Particle<ParticleParamRedstone> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamRedstone(packetdataserializer.readFloat(), packetdataserializer.readFloat(), packetdataserializer.readFloat(), packetdataserializer.readFloat());
        }
    };
    private final float d;
    private final float e;
    private final float f;
    private final float g;

    public ParticleParamRedstone(float f, float f1, float f2, float f3) {
        this.d = f;
        this.e = f1;
        this.f = f2;
        this.g = MathHelper.a(f3, 0.01F, 4.0F);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeFloat(this.d);
        packetdataserializer.writeFloat(this.e);
        packetdataserializer.writeFloat(this.f);
        packetdataserializer.writeFloat(this.g);
    }

    @Override
    public String a() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", IRegistry.PARTICLE_TYPE.getKey(this.getParticle()), this.d, this.e, this.f, this.g);
    }

    @Override
    public Particle<ParticleParamRedstone> getParticle() {
        return Particles.DUST;
    }
}
