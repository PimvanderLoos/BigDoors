package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.phys.Vec3D;

public class PathPoint {

    public final int x;
    public final int y;
    public final int z;
    private final int hash;
    public int heapIdx = -1;
    public float g;
    public float h;
    public float f;
    @Nullable
    public PathPoint cameFrom;
    public boolean closed;
    public float walkedDistance;
    public float costMalus;
    public PathType type;

    public PathPoint(int i, int j, int k) {
        this.type = PathType.BLOCKED;
        this.x = i;
        this.y = j;
        this.z = k;
        this.hash = createHash(i, j, k);
    }

    public PathPoint cloneAndMove(int i, int j, int k) {
        PathPoint pathpoint = new PathPoint(i, j, k);

        pathpoint.heapIdx = this.heapIdx;
        pathpoint.g = this.g;
        pathpoint.h = this.h;
        pathpoint.f = this.f;
        pathpoint.cameFrom = this.cameFrom;
        pathpoint.closed = this.closed;
        pathpoint.walkedDistance = this.walkedDistance;
        pathpoint.costMalus = this.costMalus;
        pathpoint.type = this.type;
        return pathpoint;
    }

    public static int createHash(int i, int j, int k) {
        return j & 255 | (i & 32767) << 8 | (k & 32767) << 24 | (i < 0 ? Integer.MIN_VALUE : 0) | (k < 0 ? '\u8000' : 0);
    }

    public float distanceTo(PathPoint pathpoint) {
        float f = (float) (pathpoint.x - this.x);
        float f1 = (float) (pathpoint.y - this.y);
        float f2 = (float) (pathpoint.z - this.z);

        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public float distanceToXZ(PathPoint pathpoint) {
        float f = (float) (pathpoint.x - this.x);
        float f1 = (float) (pathpoint.z - this.z);

        return MathHelper.sqrt(f * f + f1 * f1);
    }

    public float distanceTo(BlockPosition blockposition) {
        float f = (float) (blockposition.getX() - this.x);
        float f1 = (float) (blockposition.getY() - this.y);
        float f2 = (float) (blockposition.getZ() - this.z);

        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public float distanceToSqr(PathPoint pathpoint) {
        float f = (float) (pathpoint.x - this.x);
        float f1 = (float) (pathpoint.y - this.y);
        float f2 = (float) (pathpoint.z - this.z);

        return f * f + f1 * f1 + f2 * f2;
    }

    public float distanceToSqr(BlockPosition blockposition) {
        float f = (float) (blockposition.getX() - this.x);
        float f1 = (float) (blockposition.getY() - this.y);
        float f2 = (float) (blockposition.getZ() - this.z);

        return f * f + f1 * f1 + f2 * f2;
    }

    public float distanceManhattan(PathPoint pathpoint) {
        float f = (float) Math.abs(pathpoint.x - this.x);
        float f1 = (float) Math.abs(pathpoint.y - this.y);
        float f2 = (float) Math.abs(pathpoint.z - this.z);

        return f + f1 + f2;
    }

    public float distanceManhattan(BlockPosition blockposition) {
        float f = (float) Math.abs(blockposition.getX() - this.x);
        float f1 = (float) Math.abs(blockposition.getY() - this.y);
        float f2 = (float) Math.abs(blockposition.getZ() - this.z);

        return f + f1 + f2;
    }

    public BlockPosition asBlockPos() {
        return new BlockPosition(this.x, this.y, this.z);
    }

    public Vec3D asVec3() {
        return new Vec3D((double) this.x, (double) this.y, (double) this.z);
    }

    public boolean equals(Object object) {
        if (!(object instanceof PathPoint)) {
            return false;
        } else {
            PathPoint pathpoint = (PathPoint) object;

            return this.hash == pathpoint.hash && this.x == pathpoint.x && this.y == pathpoint.y && this.z == pathpoint.z;
        }
    }

    public int hashCode() {
        return this.hash;
    }

    public boolean inOpenSet() {
        return this.heapIdx >= 0;
    }

    public String toString() {
        return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    public void writeToStream(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.x);
        packetdataserializer.writeInt(this.y);
        packetdataserializer.writeInt(this.z);
        packetdataserializer.writeFloat(this.walkedDistance);
        packetdataserializer.writeFloat(this.costMalus);
        packetdataserializer.writeBoolean(this.closed);
        packetdataserializer.writeEnum(this.type);
        packetdataserializer.writeFloat(this.f);
    }

    public static PathPoint createFromStream(PacketDataSerializer packetdataserializer) {
        PathPoint pathpoint = new PathPoint(packetdataserializer.readInt(), packetdataserializer.readInt(), packetdataserializer.readInt());

        readContents(packetdataserializer, pathpoint);
        return pathpoint;
    }

    protected static void readContents(PacketDataSerializer packetdataserializer, PathPoint pathpoint) {
        pathpoint.walkedDistance = packetdataserializer.readFloat();
        pathpoint.costMalus = packetdataserializer.readFloat();
        pathpoint.closed = packetdataserializer.readBoolean();
        pathpoint.type = (PathType) packetdataserializer.readEnum(PathType.class);
        pathpoint.f = packetdataserializer.readFloat();
    }
}
