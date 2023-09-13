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
        this.sound = (SoundEffect) IRegistry.SOUND_EVENT.byId(packetdataserializer.readVarInt());
        this.source = (SoundCategory) packetdataserializer.readEnum(SoundCategory.class);
        this.x = packetdataserializer.readInt();
        this.y = packetdataserializer.readInt();
        this.z = packetdataserializer.readInt();
        this.volume = packetdataserializer.readFloat();
        this.pitch = packetdataserializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(IRegistry.SOUND_EVENT.getId(this.sound));
        packetdataserializer.writeEnum(this.source);
        packetdataserializer.writeInt(this.x);
        packetdataserializer.writeInt(this.y);
        packetdataserializer.writeInt(this.z);
        packetdataserializer.writeFloat(this.volume);
        packetdataserializer.writeFloat(this.pitch);
    }

    public SoundEffect getSound() {
        return this.sound;
    }

    public SoundCategory getSource() {
        return this.source;
    }

    public double getX() {
        return (double) ((float) this.x / 8.0F);
    }

    public double getY() {
        return (double) ((float) this.y / 8.0F);
    }

    public double getZ() {
        return (double) ((float) this.z / 8.0F);
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSoundEvent(this);
    }
}
