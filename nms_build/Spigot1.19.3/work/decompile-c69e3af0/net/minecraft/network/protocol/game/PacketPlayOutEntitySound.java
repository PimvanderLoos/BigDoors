package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.Entity;

public class PacketPlayOutEntitySound implements Packet<PacketListenerPlayOut> {

    private final Holder<SoundEffect> sound;
    private final SoundCategory source;
    private final int id;
    private final float volume;
    private final float pitch;
    private final long seed;

    public PacketPlayOutEntitySound(Holder<SoundEffect> holder, SoundCategory soundcategory, Entity entity, float f, float f1, long i) {
        this.sound = holder;
        this.source = soundcategory;
        this.id = entity.getId();
        this.volume = f;
        this.pitch = f1;
        this.seed = i;
    }

    public PacketPlayOutEntitySound(PacketDataSerializer packetdataserializer) {
        this.sound = packetdataserializer.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEffect::readFromNetwork);
        this.source = (SoundCategory) packetdataserializer.readEnum(SoundCategory.class);
        this.id = packetdataserializer.readVarInt();
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
        packetdataserializer.writeVarInt(this.id);
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

    public int getId() {
        return this.id;
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
        packetlistenerplayout.handleSoundEntityEvent(this);
    }
}
