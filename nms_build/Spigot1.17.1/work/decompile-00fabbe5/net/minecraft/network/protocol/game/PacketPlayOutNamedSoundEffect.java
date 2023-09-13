package net.minecraft.network.protocol.game;

import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import org.apache.commons.lang3.Validate;

public class PacketPlayOutNamedSoundEffect implements Packet<PacketListenerPlayOut> {

    public static final float LOCATION_ACCURACY = 8.0F;
    private final SoundEffect sound;
    private final SoundCategory source;
    private final int x;
    private final int y;
    private final int z;
    private final float volume;
    private final float pitch;

    public PacketPlayOutNamedSoundEffect(SoundEffect soundeffect, SoundCategory soundcategory, double d0, double d1, double d2, float f, float f1) {
        Validate.notNull(soundeffect, "sound", new Object[0]);
        this.sound = soundeffect;
        this.source = soundcategory;
        this.x = (int) (d0 * 8.0D);
        this.y = (int) (d1 * 8.0D);
        this.z = (int) (d2 * 8.0D);
        this.volume = f;
        this.pitch = f1;
    }

    public PacketPlayOutNamedSoundEffect(PacketDataSerializer packetdataserializer) {
        this.sound = (SoundEffect) IRegistry.SOUND_EVENT.fromId(packetdataserializer.j());
        this.source = (SoundCategory) packetdataserializer.a(SoundCategory.class);
        this.x = packetdataserializer.readInt();
        this.y = packetdataserializer.readInt();
        this.z = packetdataserializer.readInt();
        this.volume = packetdataserializer.readFloat();
        this.pitch = packetdataserializer.readFloat();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(IRegistry.SOUND_EVENT.getId(this.sound));
        packetdataserializer.a((Enum) this.source);
        packetdataserializer.writeInt(this.x);
        packetdataserializer.writeInt(this.y);
        packetdataserializer.writeInt(this.z);
        packetdataserializer.writeFloat(this.volume);
        packetdataserializer.writeFloat(this.pitch);
    }

    public SoundEffect b() {
        return this.sound;
    }

    public SoundCategory c() {
        return this.source;
    }

    public double d() {
        return (double) ((float) this.x / 8.0F);
    }

    public double e() {
        return (double) ((float) this.y / 8.0F);
    }

    public double f() {
        return (double) ((float) this.z / 8.0F);
    }

    public float g() {
        return this.volume;
    }

    public float h() {
        return this.pitch;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
