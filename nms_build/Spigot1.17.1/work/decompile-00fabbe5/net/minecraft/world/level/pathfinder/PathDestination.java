package net.minecraft.world.level.pathfinder;

import net.minecraft.network.PacketDataSerializer;

public class PathDestination extends PathPoint {

    private float bestHeuristic = Float.MAX_VALUE;
    private PathPoint bestNode;
    private boolean reached;

    public PathDestination(PathPoint pathpoint) {
        super(pathpoint.x, pathpoint.y, pathpoint.z);
    }

    public PathDestination(int i, int j, int k) {
        super(i, j, k);
    }

    public void a(float f, PathPoint pathpoint) {
        if (f < this.bestHeuristic) {
            this.bestHeuristic = f;
            this.bestNode = pathpoint;
        }

    }

    public PathPoint d() {
        return this.bestNode;
    }

    public void e() {
        this.reached = true;
    }

    public boolean f() {
        return this.reached;
    }

    public static PathDestination c(PacketDataSerializer packetdataserializer) {
        PathDestination pathdestination = new PathDestination(packetdataserializer.readInt(), packetdataserializer.readInt(), packetdataserializer.readInt());

        pathdestination.walkedDistance = packetdataserializer.readFloat();
        pathdestination.costMalus = packetdataserializer.readFloat();
        pathdestination.closed = packetdataserializer.readBoolean();
        pathdestination.type = PathType.values()[packetdataserializer.readInt()];
        pathdestination.f = packetdataserializer.readFloat();
        return pathdestination;
    }
}
