package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalFleeSun extends PathfinderGoal {

    protected final EntityCreature mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private final World level;

    public PathfinderGoalFleeSun(EntityCreature entitycreature, double d0) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.level = entitycreature.level;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        return this.mob.getGoalTarget() != null ? false : (!this.level.isDay() ? false : (!this.mob.isBurning() ? false : (!this.level.g(this.mob.getChunkCoordinates()) ? false : (!this.mob.getEquipment(EnumItemSlot.HEAD).isEmpty() ? false : this.g()))));
    }

    protected boolean g() {
        Vec3D vec3d = this.h();

        if (vec3d == null) {
            return false;
        } else {
            this.wantedX = vec3d.x;
            this.wantedY = vec3d.y;
            this.wantedZ = vec3d.z;
            return true;
        }
    }

    @Override
    public boolean b() {
        return !this.mob.getNavigation().m();
    }

    @Override
    public void c() {
        this.mob.getNavigation().a(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Nullable
    protected Vec3D h() {
        Random random = this.mob.getRandom();
        BlockPosition blockposition = this.mob.getChunkCoordinates();

        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = blockposition.c(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (!this.level.g(blockposition1) && this.mob.f(blockposition1) < 0.0F) {
                return Vec3D.c((BaseBlockPosition) blockposition1);
            }
        }

        return null;
    }
}
