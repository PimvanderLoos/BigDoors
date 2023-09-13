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

    public PathfinderAbstract() {}

    public void a(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        this.level = chunkcache;
        this.mob = entityinsentient;
        this.nodes.clear();
        this.entityWidth = MathHelper.d(entityinsentient.getWidth() + 1.0F);
        this.entityHeight = MathHelper.d(entityinsentient.getHeight() + 1.0F);
        this.entityDepth = MathHelper.d(entityinsentient.getWidth() + 1.0F);
    }

    public void a() {
        this.level = null;
        this.mob = null;
    }

    protected PathPoint b(BlockPosition blockposition) {
        return this.a(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    protected PathPoint a(int i, int j, int k) {
        return (PathPoint) this.nodes.computeIfAbsent(PathPoint.b(i, j, k), (l) -> {
            return new PathPoint(i, j, k);
        });
    }

    public abstract PathPoint b();

    public abstract PathDestination a(double d0, double d1, double d2);

    public abstract int a(PathPoint[] apathpoint, PathPoint pathpoint);

    public abstract PathType a(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1);

    public abstract PathType a(IBlockAccess iblockaccess, int i, int j, int k);

    public void a(boolean flag) {
        this.canPassDoors = flag;
    }

    public void b(boolean flag) {
        this.canOpenDoors = flag;
    }

    public void c(boolean flag) {
        this.canFloat = flag;
    }

    public boolean d() {
        return this.canPassDoors;
    }

    public boolean e() {
        return this.canOpenDoors;
    }

    public boolean f() {
        return this.canFloat;
    }
}
