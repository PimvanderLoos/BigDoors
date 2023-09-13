package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalPanic extends PathfinderGoal {

    protected final EntityCreature mob;
    protected final double speedModifier;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected boolean isRunning;

    public PathfinderGoalPanic(EntityCreature entitycreature, double d0) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (this.mob.getLastDamager() == null && !this.mob.isBurning()) {
            return false;
        } else {
            if (this.mob.isBurning()) {
                BlockPosition blockposition = this.a(this.mob.level, this.mob, 5, 4);

                if (blockposition != null) {
                    this.posX = (double) blockposition.getX();
                    this.posY = (double) blockposition.getY();
                    this.posZ = (double) blockposition.getZ();
                    return true;
                }
            }

            return this.g();
        }
    }

    protected boolean g() {
        Vec3D vec3d = DefaultRandomPos.a(this.mob, 5, 4);

        if (vec3d == null) {
            return false;
        } else {
            this.posX = vec3d.x;
            this.posY = vec3d.y;
            this.posZ = vec3d.z;
            return true;
        }
    }

    public boolean h() {
        return this.isRunning;
    }

    @Override
    public void c() {
        this.mob.getNavigation().a(this.posX, this.posY, this.posZ, this.speedModifier);
        this.isRunning = true;
    }

    @Override
    public void d() {
        this.isRunning = false;
    }

    @Override
    public boolean b() {
        return !this.mob.getNavigation().m();
    }

    @Nullable
    protected BlockPosition a(IBlockAccess iblockaccess, Entity entity, int i, int j) {
        BlockPosition blockposition = entity.getChunkCoordinates();
        int k = blockposition.getX();
        int l = blockposition.getY();
        int i1 = blockposition.getZ();
        float f = (float) (i * i * j * 2);
        BlockPosition blockposition1 = null;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j1 = k - i; j1 <= k + i; ++j1) {
            for (int k1 = l - j; k1 <= l + j; ++k1) {
                for (int l1 = i1 - i; l1 <= i1 + i; ++l1) {
                    blockposition_mutableblockposition.d(j1, k1, l1);
                    if (iblockaccess.getFluid(blockposition_mutableblockposition).a((Tag) TagsFluid.WATER)) {
                        float f1 = (float) ((j1 - k) * (j1 - k) + (k1 - l) * (k1 - l) + (l1 - i1) * (l1 - i1));

                        if (f1 < f) {
                            f = f1;
                            blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                        }
                    }
                }
            }
        }

        return blockposition1;
    }
}
