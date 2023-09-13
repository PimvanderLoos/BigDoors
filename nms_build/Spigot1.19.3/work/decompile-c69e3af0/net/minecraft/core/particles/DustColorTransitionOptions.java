package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public class DustColorTransitionOptions extends DustParticleOptionsBase {

    public static final Vector3f SCULK_PARTICLE_COLOR = Vec3D.fromRGB24(3790560).toVector3f();
    public static final DustColorTransitionOptions SCULK_TO_REDSTONE = new DustColorTransitionOptions(DustColorTransitionOptions.SCULK_PARTICLE_COLOR, ParticleParamRedstone.REDSTONE_PARTICLE_COLOR, 1.0F);
    public static final Codec<DustColorTransitionOptions> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.VECTOR3F.fieldOf("fromColor").forGetter((dustcolortransitionoptions) -> {
            return dustcolortransitionoptions.color;
        }), ExtraCodecs.VECTOR3F.fieldOf("toColor").forGetter((dustcolortransitionoptions) -> {
            return dustcolortransitionoptions.toColor;
        }), Codec.FLOAT.fieldOf("scale").forGetter((dustcolortransitionoptions) -> {
            return dustcolortransitionoptions.scale;
        })).apply(instance, DustColorTransitionOptions::new);
    });
    public static final ParticleParam.a<DustColorTransitionOptions> DESERIALIZER = new ParticleParam.a<DustColorTransitionOptions>() {
        @Override
        public DustColorTransitionOptions fromCommand(Particle<DustColorTransitionOptions> particle, StringReader stringreader) throws CommandSyntaxException {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(stringreader);

            stringreader.expect(' ');
            float f = stringreader.readFloat();
            Vector3f vector3f1 = DustParticleOptionsBase.readVector3f(stringreader);

            return new DustColorTransitionOptions(vector3f, vector3f1, f);
        }

        @Override
        public DustColorTransitionOptions fromNetwork(Particle<DustColorTransitionOptions> particle, PacketDataSerializer packetdataserializer) {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(packetdataserializer);
            float f = packetdataserializer.readFloat();
            Vector3f vector3f1 = DustParticleOptionsBase.readVector3f(packetdataserializer);

            return new DustColorTransitionOptions(vector3f, vector3f1, f);
        }
    };
    private final Vector3f toColor;

    public DustColorTransitionOptions(Vector3f vector3f, Vector3f vector3f1, float f) {
        super(vector3f, f);
        this.toColor = vector3f1;
    }

    public Vector3f getFromColor() {
        return this.color;
    }

    public Vector3f getToColor() {
        return this.toColor;
    }

    @Override
    public void writeToNetwork(PacketDataSerializer packetdataserializer) {
        super.writeToNetwork(packetdataserializer);
        packetdataserializer.writeFloat(this.toColor.x());
        packetdataserializer.writeFloat(this.toColor.y());
        packetdataserializer.writeFloat(this.toColor.z());
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale, this.toColor.x(), this.toColor.y(), this.toColor.z());
    }

    @Override
    public Particle<DustColorTransitionOptions> getType() {
        return Particles.DUST_COLOR_TRANSITION;
    }
}
