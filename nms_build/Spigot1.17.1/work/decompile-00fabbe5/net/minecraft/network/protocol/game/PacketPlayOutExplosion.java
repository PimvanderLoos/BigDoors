package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.MathHelper;
import net.minecraft.world.phys.Vec3D;

public class PacketPlayOutExplosion implements Packet<PacketListenerPlayOut> {

    private final double x;
    private final double y;
    private final double z;
    private final float power;
    private final List<BlockPosition> toBlow;
    private final float knockbackX;
    private final float knockbackY;
    private final float knockbackZ;

    public PacketPlayOutExplosion(double d0, double d1, double d2, float f, List<BlockPosition> list, @Nullable Vec3D vec3d) {
        this.x = d0;
        this.y = d1;
        this.z = d2;
        this.power = f;
        this.toBlow = Lists.newArrayList(list);
        if (vec3d != null) {
            this.knockbackX = (float) vec3d.x;
            this.knockbackY = (float) vec3d.y;
            this.knockbackZ = (float) vec3d.z;
        } else {
            this.knockbackX = 0.0F;
            this.knockbackY = 0.0F;
            this.knockbackZ = 0.0F;
        }

    }

    public PacketPlayOutExplosion(PacketDataSerializer packetdataserializer) {
        this.x = (double) packetdataserializer.readFloat();
        this.y = (double) packetdataserializer.readFloat();
        this.z = (double) packetdataserializer.readFloat();
        this.power = packetdataserializer.readFloat();
        int i = MathHelper.floor(this.x);
        int j = MathHelper.floor(this.y);
        int k = MathHelper.floor(this.z);

        this.toBlow = packetdataserializer.a((packetdataserializer1) -> {
            int l = packetdataserializer1.readByte() + i;
            int i1 = packetdataserializer1.readByte() + j;
            int j1 = packetdataserializer1.readByte() + k;

            return new BlockPosition(l, i1, j1);
        });
        this.knockbackX = packetdataserializer.readFloat();
        this.knockbackY = packetdataserializer.readFloat();
        this.knockbackZ = packetdataserializer.readFloat();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeFloat((float) this.x);
        packetdataserializer.writeFloat((float) this.y);
        packetdataserializer.writeFloat((float) this.z);
        packetdataserializer.writeFloat(this.power);
        int i = MathHelper.floor(this.x);
        int j = MathHelper.floor(this.y);
        int k = MathHelper.floor(this.z);

        packetdataserializer.a((Collection) this.toBlow, (packetdataserializer1, blockposition) -> {
            int l = blockposition.getX() - i;
            int i1 = blockposition.getY() - j;
            int j1 = blockposition.getZ() - k;

            packetdataserializer1.writeByte(l);
            packetdataserializer1.writeByte(i1);
            packetdataserializer1.writeByte(j1);
        });
        packetdataserializer.writeFloat(this.knockbackX);
        packetdataserializer.writeFloat(this.knockbackY);
        packetdataserializer.writeFloat(this.knockbackZ);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public float b() {
        return this.knockbackX;
    }

    public float c() {
        return this.knockbackY;
    }

    public float d() {
        return this.knockbackZ;
    }

    public double e() {
        return this.x;
    }

    public double f() {
        return this.y;
    }

    public double g() {
        return this.z;
    }

    public float h() {
        return this.power;
    }

    public List<BlockPosition> i() {
        return this.toBlow;
    }
}
