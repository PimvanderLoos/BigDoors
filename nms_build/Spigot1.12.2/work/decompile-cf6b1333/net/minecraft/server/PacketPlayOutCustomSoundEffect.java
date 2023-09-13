package net.minecraft.server;

import java.io.IOException;
import org.apache.commons.lang3.Validate;

public class PacketPlayOutCustomSoundEffect implements Packet<PacketListenerPlayOut> {

    private String a;
    private SoundCategory b;
    private int c;
    private int d = Integer.MAX_VALUE;
    private int e;
    private float f;
    private float g;

    public PacketPlayOutCustomSoundEffect() {}

    public PacketPlayOutCustomSoundEffect(String s, SoundCategory soundcategory, double d0, double d1, double d2, float f, float f1) {
        Validate.notNull(s, "name", new Object[0]);
        this.a = s;
        this.b = soundcategory;
        this.c = (int) (d0 * 8.0D);
        this.d = (int) (d1 * 8.0D);
        this.e = (int) (d2 * 8.0D);
        this.f = f;
        this.g = f1;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e(256);
        this.b = (SoundCategory) packetdataserializer.a(SoundCategory.class);
        this.c = packetdataserializer.readInt();
        this.d = packetdataserializer.readInt();
        this.e = packetdataserializer.readInt();
        this.f = packetdataserializer.readFloat();
        this.g = packetdataserializer.readFloat();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.a((Enum) this.b);
        packetdataserializer.writeInt(this.c);
        packetdataserializer.writeInt(this.d);
        packetdataserializer.writeInt(this.e);
        packetdataserializer.writeFloat(this.f);
        packetdataserializer.writeFloat(this.g);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
