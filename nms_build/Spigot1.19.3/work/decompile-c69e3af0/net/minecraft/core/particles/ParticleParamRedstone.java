package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public class ParticleParamRedstone extends DustParticleOptionsBase {

    public static final Vector3f REDSTONE_PARTICLE_COLOR = Vec3D.fromRGB24(16711680).toVector3f();
    public static final ParticleParamRedstone REDSTONE = new ParticleParamRedstone(ParticleParamRedstone.REDSTONE_PARTICLE_COLOR, 1.0F);
    public static final Codec<ParticleParamRedstone> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((particleparamredstone) -> {
            return particleparamredstone.color;
        }), Codec.FLOAT.fieldOf("scale").forGetter((particleparamredstone) -> {
            return particleparamredstone.scale;
        })).apply(instance, ParticleParamRedstone::new);
    });
    public static final ParticleParam.a<ParticleParamRedstone> DESERIALIZER = new ParticleParam.a<ParticleParamRedstone>() {
        @Override
        public ParticleParamRedstone fromCommand(Particle<ParticleParamRedstone> particle, StringReader stringreader) throws CommandSyntaxException {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(stringreader);

            stringreader.expect(' ');
            float f = stringreader.readFloat();

            return new ParticleParamRedstone(vector3f, f);
        }

        @Override
        public ParticleParamRedstone fromNetwork(Particle<ParticleParamRedstone> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamRedstone(DustParticleOptionsBase.readVector3f(packetdataserializer), packetdataserializer.readFloat());
        }
    };

    public ParticleParamRedstone(Vector3f vector3f, float f) {
        super(vector3f, f);
    }

    @Override
    public Particle<ParticleParamRedstone> getType() {
        return Particles.DUST;
    }
}
