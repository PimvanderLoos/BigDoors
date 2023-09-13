package net.minecraft.server;

import java.io.IOException;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class PacketPlayInTabComplete implements Packet<PacketListenerPlayIn> {

    private String a;
    private boolean b;
    @Nullable
    private BlockPosition c;

    public PacketPlayInTabComplete() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e(32767);
        this.b = packetdataserializer.readBoolean();
        boolean flag = packetdataserializer.readBoolean();

        if (flag) {
            this.c = packetdataserializer.e();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(StringUtils.substring(this.a, 0, 32767));
        packetdataserializer.writeBoolean(this.b);
        boolean flag = this.c != null;

        packetdataserializer.writeBoolean(flag);
        if (flag) {
            packetdataserializer.a(this.c);
        }

    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public String a() {
        return this.a;
    }

    @Nullable
    public BlockPosition b() {
        return this.c;
    }

    public boolean c() {
        return this.b;
    }
}
