package net.minecraft.network.protocol.game;

import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.Validate;

public class PacketPlayOutEntitySound implements Packet<PacketListenerPlayOut> {

    private final SoundEffect sound;
    private final SoundCategory source;
    private final int id;
    private final float volume;
    private final float pitch;

    public PacketPlayOutEntitySound(SoundEffect soundeffect, SoundCategory soundcategory, Entity entity, float f, float f1) {
        Validate.notNull(soundeffect, "sound", new Object[0]);
        this.sound = soundeffect;
        this.source = soundcategory;
        this.id = entity.getId();
        this.volume = f;
        this.pitch = f1;
    }

    public PacketPlayOutEntitySound(PacketDataSerializer packetdataserializer) {
        this.sound = (SoundEffect) IRegistry.SOUND_EVENT.fromId(packetdataserializer.j());
        this.source = (SoundCategory) packetdataserializer.a(SoundCategory.class);
        this.id = packetdataserializer.j();
        this.volume = packetdataserializer.readFloat();
        this.pitch = packetdataserializer.readFloat();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(IRegistry.SOUND_EVENT.getId(this.sound));
        packetdataserializer.a((Enum) this.source);
        packetdataserializer.d(this.id);
        packetdataserializer.writeFloat(this.volume);
        packetdataserializer.writeFloat(this.pitch);
    }

    public SoundEffect b() {
        return this.sound;
    }

    public SoundCategory c() {
        return this.source;
    }

    public int d() {
        return this.id;
    }

    public float e() {
        return this.volume;
    }

    public float f() {
        return this.pitch;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
