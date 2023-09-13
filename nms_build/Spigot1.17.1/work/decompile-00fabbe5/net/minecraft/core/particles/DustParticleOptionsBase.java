package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3fa;
import java.util.Locale;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.MathHelper;

public abstract class DustParticleOptionsBase implements ParticleParam {

    public static final float MIN_SCALE = 0.01F;
    public static final float MAX_SCALE = 4.0F;
    protected final Vector3fa color;
    protected final float scale;

    public DustParticleOptionsBase(Vector3fa vector3fa, float f) {
        this.color = vector3fa;
        this.scale = MathHelper.a(f, 0.01F, 4.0F);
    }

    public static Vector3fa a(StringReader stringreader) throws CommandSyntaxException {
        stringreader.expect(' ');
        float f = stringreader.readFloat();

        stringreader.expect(' ');
        float f1 = stringreader.readFloat();

        stringreader.expect(' ');
        float f2 = stringreader.readFloat();

        return new Vector3fa(f, f1, f2);
    }

    public static Vector3fa b(PacketDataSerializer packetdataserializer) {
        return new Vector3fa(packetdataserializer.readFloat(), packetdataserializer.readFloat(), packetdataserializer.readFloat());
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeFloat(this.color.a());
        packetdataserializer.writeFloat(this.color.b());
        packetdataserializer.writeFloat(this.color.c());
        packetdataserializer.writeFloat(this.scale);
    }

    @Override
    public String a() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", IRegistry.PARTICLE_TYPE.getKey(this.getParticle()), this.color.a(), this.color.b(), this.color.c(), this.scale);
    }

    public Vector3fa e() {
        return this.color;
    }

    public float f() {
        return this.scale;
    }
}
