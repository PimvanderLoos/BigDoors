package net.minecraft.network.protocol.game;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutPosition implements Packet<PacketListenerPlayOut> {

    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;
    private final Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> relativeArguments;
    private final int id;
    private final boolean dismountVehicle;

    public PacketPlayOutPosition(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set, int i, boolean flag) {
        this.x = d0;
        this.y = d1;
        this.z = d2;
        this.yRot = f;
        this.xRot = f1;
        this.relativeArguments = set;
        this.id = i;
        this.dismountVehicle = flag;
    }

    public PacketPlayOutPosition(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readFloat();
        this.xRot = packetdataserializer.readFloat();
        this.relativeArguments = PacketPlayOutPosition.EnumPlayerTeleportFlags.a(packetdataserializer.readUnsignedByte());
        this.id = packetdataserializer.j();
        this.dismountVehicle = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeFloat(this.yRot);
        packetdataserializer.writeFloat(this.xRot);
        packetdataserializer.writeByte(PacketPlayOutPosition.EnumPlayerTeleportFlags.a(this.relativeArguments));
        packetdataserializer.d(this.id);
        packetdataserializer.writeBoolean(this.dismountVehicle);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public double b() {
        return this.x;
    }

    public double c() {
        return this.y;
    }

    public double d() {
        return this.z;
    }

    public float e() {
        return this.yRot;
    }

    public float f() {
        return this.xRot;
    }

    public int g() {
        return this.id;
    }

    public boolean h() {
        return this.dismountVehicle;
    }

    public Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> i() {
        return this.relativeArguments;
    }

    public static enum EnumPlayerTeleportFlags {

        X(0), Y(1), Z(2), Y_ROT(3), X_ROT(4);

        private final int bit;

        private EnumPlayerTeleportFlags(int i) {
            this.bit = i;
        }

        private int a() {
            return 1 << this.bit;
        }

        private boolean b(int i) {
            return (i & this.a()) == this.a();
        }

        public static Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> a(int i) {
            Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);
            PacketPlayOutPosition.EnumPlayerTeleportFlags[] apacketplayoutposition_enumplayerteleportflags = values();
            int j = apacketplayoutposition_enumplayerteleportflags.length;

            for (int k = 0; k < j; ++k) {
                PacketPlayOutPosition.EnumPlayerTeleportFlags packetplayoutposition_enumplayerteleportflags = apacketplayoutposition_enumplayerteleportflags[k];

                if (packetplayoutposition_enumplayerteleportflags.b(i)) {
                    set.add(packetplayoutposition_enumplayerteleportflags);
                }
            }

            return set;
        }

        public static int a(Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
            int i = 0;

            PacketPlayOutPosition.EnumPlayerTeleportFlags packetplayoutposition_enumplayerteleportflags;

            for (Iterator iterator = set.iterator(); iterator.hasNext(); i |= packetplayoutposition_enumplayerteleportflags.a()) {
                packetplayoutposition_enumplayerteleportflags = (PacketPlayOutPosition.EnumPlayerTeleportFlags) iterator.next();
            }

            return i;
        }
    }
}
