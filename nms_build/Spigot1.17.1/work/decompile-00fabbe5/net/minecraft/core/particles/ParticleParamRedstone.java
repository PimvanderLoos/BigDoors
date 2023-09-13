package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3fa;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.phys.Vec3D;

public class ParticleParamRedstone extends DustParticleOptionsBase {

    public static final Vector3fa REDSTONE_PARTICLE_COLOR = new Vector3fa(Vec3D.a(16711680));
    public static final ParticleParamRedstone REDSTONE = new ParticleParamRedstone(ParticleParamRedstone.REDSTONE_PARTICLE_COLOR, 1.0F);
    public static final Codec<ParticleParamRedstone> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Vector3fa.CODEC.fieldOf("color").forGetter((particleparamredstone) -> {
            return particleparamredstone.color;
        }), Codec.FLOAT.fieldOf("scale").forGetter((particleparamredstone) -> {
            return particleparamredstone.scale;
        })).apply(instance, ParticleParamRedstone::new);
    });
    public static final ParticleParam.a<ParticleParamRedstone> DESERIALIZER = new ParticleParam.a<ParticleParamRedstone>() {
        @Override
        public ParticleParamRedstone b(Particle<ParticleParamRedstone> particle, StringReader stringreader) throws CommandSyntaxException {
            Vector3fa vector3fa = DustParticleOptionsBase.a(stringreader);

            stringreader.expect(' ');
            float f = stringreader.readFloat();

            return new ParticleParamRedstone(vector3fa, f);
        }

        @Override
        public ParticleParamRedstone b(Particle<ParticleParamRedstone> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamRedstone(DustParticleOptionsBase.b(packetdataserializer), packetdataserializer.readFloat());
        }
    };

    public ParticleParamRedstone(Vector3fa vector3fa, float f) {
        super(vector3fa, f);
    }

    @Override
    public Particle<ParticleParamRedstone> getParticle() {
        return Particles.DUST;
    }
}
