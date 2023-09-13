package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.IBlockAccess;

public abstract class PathfinderAbstract {

    protected ChunkCache level;
    protected EntityInsentient mob;
    protected final Int2ObjectMap<PathPoint> nodes = new Int2ObjectOpenHashMap();
    protected int entityWidth;
    protected int entityHeight;
    protected int entityDepth;
    protected boolean canPassDoors;
    protected boolean canOpenDoors;
    protected boolean canFloat;
    protected boolean canWalkOverFences;

    public PathfinderAbstract() {}

    public void prepare(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        this.level = chunkcache;
        this.mob = entityinsentient;
        this.nodes.clear();
        this.entityWidth = MathHelper.floor(entityinsentient.getBbWidth() + 1.0F);
        this.entityHeight = MathHelper.floor(entityinsentient.getBbHeight() + 1.0F);
        this.entityDepth = MathHelper.floor(entityinsentient.getBbWidth() + 1.0F);
    }

    public void done() {
        this.level = null;
        this.mob = null;
    }

    protected PathPoint getNode(BlockPosition blockposition) {
        return this.getNode(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    protected PathPoint getNode(int i, int j, int k) {
        return (PathPoint) this.nodes.computeIfAbsent(PathPoint.createHash(i, j, k), (l) -> {
            return new PathPoint(i, j, k);
        });
    }

    public abstract PathPoint getStart();

    public abstract PathDestination getGoal(double d0, double d1, double d2);

    protected PathDestination getTargetFromNode(PathPoint pathpoint) {
        return new PathDestination(pathpoint);
    }

    public abstract int getNeighbors(PathPoint[] apathpoint, PathPoint pathpoint);

    public abstract PathType getBlockPathType(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient);

    public abstract PathType getBlockPathType(IBlockAccess iblockaccess, int i, int j, int k);

    public void setCanPassDoors(boolean flag) {
        this.canPassDoors = flag;
    }

    public void setCanOpenDoors(boolean flag) {
        this.canOpenDoors = flag;
    }

    public void setCanFloat(boolean flag) {
        this.canFloat = flag;
    }

    public void setCanWalkOverFences(boolean flag) {
        this.canWalkOverFences = flag;
    }

    public boolean canPassDoors() {
        return this.canPassDoors;
    }

    public boolean canOpenDoors() {
        return this.canOpenDoors;
    }

    public boolean canFloat() {
        return this.canFloat;
    }

    public boolean canWalkOverFences() {
        return this.canWalkOverFences;
    }
}
