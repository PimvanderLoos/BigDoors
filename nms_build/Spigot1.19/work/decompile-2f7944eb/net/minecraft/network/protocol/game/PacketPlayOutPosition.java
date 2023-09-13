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
        this.relativeArguments = PacketPlayOutPosition.EnumPlayerTeleportFlags.unpack(packetdataserializer.readUnsignedByte());
        this.id = packetdataserializer.readVarInt();
        this.dismountVehicle = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeFloat(this.yRot);
        packetdataserializer.writeFloat(this.xRot);
        packetdataserializer.writeByte(PacketPlayOutPosition.EnumPlayerTeleportFlags.pack(this.relativeArguments));
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeBoolean(this.dismountVehicle);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleMovePlayer(this);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYRot() {
        return this.yRot;
    }

    public float getXRot() {
        return this.xRot;
    }

    public int getId() {
        return this.id;
    }

    public boolean requestDismountVehicle() {
        return this.dismountVehicle;
    }

    public Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> getRelativeArguments() {
        return this.relativeArguments;
    }

    public static enum EnumPlayerTeleportFlags {

        X(0), Y(1), Z(2), Y_ROT(3), X_ROT(4);

        private final int bit;

        private EnumPlayerTeleportFlags(int i) {
            this.bit = i;
        }

        private int getMask() {
            return 1 << this.bit;
        }

        private boolean isSet(int i) {
            return (i & this.getMask()) == this.getMask();
        }

        public static Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> unpack(int i) {
            Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);
            PacketPlayOutPosition.EnumPlayerTeleportFlags[] apacketplayoutposition_enumplayerteleportflags = values();
            int j = apacketplayoutposition_enumplayerteleportflags.length;

            for (int k = 0; k < j; ++k) {
                PacketPlayOutPosition.EnumPlayerTeleportFlags packetplayoutposition_enumplayerteleportflags = apacketplayoutposition_enumplayerteleportflags[k];

                if (packetplayoutposition_enumplayerteleportflags.isSet(i)) {
                    set.add(packetplayoutposition_enumplayerteleportflags);
                }
            }

            return set;
        }

        public static int pack(Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
            int i = 0;

            PacketPlayOutPosition.EnumPlayerTeleportFlags packetplayoutposition_enumplayerteleportflags;

            for (Iterator iterator = set.iterator(); iterator.hasNext(); i |= packetplayoutposition_enumplayerteleportflags.getMask()) {
                packetplayoutposition_enumplayerteleportflags = (PacketPlayOutPosition.EnumPlayerTeleportFlags) iterator.next();
            }

            return i;
        }
    }
}
