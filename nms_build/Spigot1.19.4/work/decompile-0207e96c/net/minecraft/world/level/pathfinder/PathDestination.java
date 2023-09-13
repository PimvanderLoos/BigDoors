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

    public void updateBest(float f, PathPoint pathpoint) {
        if (f < this.bestHeuristic) {
            this.bestHeuristic = f;
            this.bestNode = pathpoint;
        }

    }

    public PathPoint getBestNode() {
        return this.bestNode;
    }

    public void setReached() {
        this.reached = true;
    }

    public boolean isReached() {
        return this.reached;
    }

    public static PathDestination createFromStream(PacketDataSerializer packetdataserializer) {
        PathDestination pathdestination = new PathDestination(packetdataserializer.readInt(), packetdataserializer.readInt(), packetdataserializer.readInt());

        readContents(packetdataserializer, pathdestination);
        return pathdestination;
    }
}
