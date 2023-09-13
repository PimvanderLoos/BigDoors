package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.world.phys.Vec3D;

public class PacketPlayOutCustomSoundEffect implements Packet<PacketListenerPlayOut> {

    public static final float LOCATION_ACCURACY = 8.0F;
    private final MinecraftKey name;
    private final SoundCategory source;
    private final int x;
    private final int y;
    private final int z;
    private final float volume;
    private final float pitch;

    public PacketPlayOutCustomSoundEffect(MinecraftKey minecraftkey, SoundCategory soundcategory, Vec3D vec3d, float f, float f1) {
        this.name = minecraftkey;
        this.source = soundcategory;
        this.x = (int) (vec3d.x * 8.0D);
        this.y = (int) (vec3d.y * 8.0D);
        this.z = (int) (vec3d.z * 8.0D);
        this.volume = f;
        this.pitch = f1;
    }

    public PacketPlayOutCustomSoundEffect(PacketDataSerializer packetdataserializer) {
        this.name = packetdataserializer.q();
        this.source = (SoundCategory) packetdataserializer.a(SoundCategory.class);
        this.x = packetdataserializer.readInt();
        this.y = packetdataserializer.readInt();
        this.z = packetdataserializer.readInt();
        this.volume = packetdataserializer.readFloat();
        this.pitch = packetdataserializer.readFloat();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.name);
        packetdataserializer.a((Enum) this.source);
        packetdataserializer.writeInt(this.x);
        packetdataserializer.writeInt(this.y);
        packetdataserializer.writeInt(this.z);
        packetdataserializer.writeFloat(this.volume);
        packetdataserializer.writeFloat(this.pitch);
    }

    public MinecraftKey b() {
        return this.name;
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
