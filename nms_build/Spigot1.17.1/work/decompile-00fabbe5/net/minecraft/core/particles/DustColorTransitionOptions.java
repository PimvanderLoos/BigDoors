package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3fa;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.phys.Vec3D;

public class DustColorTransitionOptions extends DustParticleOptionsBase {

    public static final Vector3fa SCULK_PARTICLE_COLOR = new Vector3fa(Vec3D.a(3790560));
    public static final DustColorTransitionOptions SCULK_TO_REDSTONE = new DustColorTransitionOptions(DustColorTransitionOptions.SCULK_PARTICLE_COLOR, ParticleParamRedstone.REDSTONE_PARTICLE_COLOR, 1.0F);
    public static final Codec<DustColorTransitionOptions> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Vector3fa.CODEC.fieldOf("fromColor").forGetter((dustcolortransitionoptions) -> {
            return dustcolortransitionoptions.color;
        }), Vector3fa.CODEC.fieldOf("toColor").forGetter((dustcolortransitionoptions) -> {
            return dustcolortransitionoptions.toColor;
        }), Codec.FLOAT.fieldOf("scale").forGetter((dustcolortransitionoptions) -> {
            return dustcolortransitionoptions.scale;
        })).apply(instance, DustColorTransitionOptions::new);
    });
    public static final ParticleParam.a<DustColorTransitionOptions> DESERIALIZER = new ParticleParam.a<DustColorTransitionOptions>() {
        @Override
        public DustColorTransitionOptions b(Particle<DustColorTransitionOptions> particle, StringReader stringreader) throws CommandSyntaxException {
            Vector3fa vector3fa = DustParticleOptionsBase.a(stringreader);

            stringreader.expect(' ');
            float f = stringreader.readFloat();
            Vector3fa vector3fa1 = DustParticleOptionsBase.a(stringreader);

            return new DustColorTransitionOptions(vector3fa, vector3fa1, f);
        }

        @Override
        public DustColorTransitionOptions b(Particle<DustColorTransitionOptions> particle, PacketDataSerializer packetdataserializer) {
            Vector3fa vector3fa = DustParticleOptionsBase.b(packetdataserializer);
            float f = packetdataserializer.readFloat();
            Vector3fa vector3fa1 = DustParticleOptionsBase.b(packetdataserializer);

            return new DustColorTransitionOptions(vector3fa, vector3fa1, f);
        }
    };
    private final Vector3fa toColor;

    public DustColorTransitionOptions(Vector3fa vector3fa, Vector3fa vector3fa1, float f) {
        super(vector3fa, f);
        this.toColor = vector3fa1;
    }

    public Vector3fa c() {
        return this.color;
    }

    public Vector3fa d() {
        return this.toColor;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        super.a(packetdataserializer);
        packetdataserializer.writeFloat(this.toColor.a());
        packetdataserializer.writeFloat(this.toColor.b());
        packetdataserializer.writeFloat(this.toColor.c());
    }

    @Override
    public String a() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", IRegistry.PARTICLE_TYPE.getKey(this.getParticle()), this.color.a(), this.color.b(), this.color.c(), this.scale, this.toColor.a(), this.toColor.b(), this.toColor.c());
    }

    @Override
    public Particle<DustColorTransitionOptions> getParticle() {
        return Particles.DUST_COLOR_TRANSITION;
    }
}
