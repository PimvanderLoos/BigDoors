package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;

public class PacketPlayOutNamedSoundEffect implements Packet<PacketListenerPlayOut> {

    public static final float LOCATION_ACCURACY = 8.0F;
    private final Holder<SoundEffect> sound;
    private final SoundCategory source;
    private final int x;
    private final int y;
    private final int z;
    private final float volume;
    private final float pitch;
    private final long seed;

    public PacketPlayOutNamedSoundEffect(Holder<SoundEffect> holder, SoundCategory soundcategory, double d0, double d1, double d2, float f, float f1, long i) {
        this.sound = holder;
        this.source = soundcategory;
        this.x = (int) (d0 * 8.0D);
        this.y = (int) (d1 * 8.0D);
        this.z = (int) (d2 * 8.0D);
        this.volume = f;
        this.pitch = f1;
        this.seed = i;
    }

    public PacketPlayOutNamedSoundEffect(PacketDataSerializer packetdataserializer) {
        this.sound = packetdataserializer.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEffect::readFromNetwork);
        this.source = (SoundCategory) packetdataserializer.readEnum(SoundCategory.class);
        this.x = packetdataserializer.readInt();
        this.y = packetdataserializer.readInt();
        this.z = packetdataserializer.readInt();
        this.volume = packetdataserializer.readFloat();
        this.pitch = packetdataserializer.readFloat();
        this.seed = packetdataserializer.readLong();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), this.sound, (packetdataserializer1, soundeffect) -> {
            soundeffect.writeToNetwork(packetdataserializer1);
        });
        packetdataserializer.writeEnum(this.source);
        packetdataserializer.writeInt(this.x);
        packetdataserializer.writeInt(this.y);
        packetdataserializer.writeInt(this.z);
        packetdataserializer.writeFloat(this.volume);
        packetdataserializer.writeFloat(this.pitch);
        packetdataserializer.writeLong(this.seed);
    }

    public Holder<SoundEffect> getSound() {
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

    public long getSeed() {
        return this.seed;
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSoundEvent(this);
    }
}
