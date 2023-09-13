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

    protected final EntityCreature a;
    private double b;
    private double c;
    private double d;
    private final double e;
    private final World f;

    public PathfinderGoalFleeSun(EntityCreature entitycreature, double d0) {
        this.a = entitycreature;
        this.e = d0;
        this.f = entitycreature.world;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        return this.a.getGoalTarget() != null ? false : (!this.f.isDay() ? false : (!this.a.isBurning() ? false : (!this.f.e(this.a.getChunkCoordinates()) ? false : (!this.a.getEquipment(EnumItemSlot.HEAD).isEmpty() ? false : this.g()))));
    }

    protected boolean g() {
        Vec3D vec3d = this.h();

        if (vec3d == null) {
            return false;
        } else {
            this.b = vec3d.x;
            this.c = vec3d.y;
            this.d = vec3d.z;
            return true;
        }
    }

    @Override
    public boolean b() {
        return !this.a.getNavigation().m();
    }

    @Override
    public void c() {
        this.a.getNavigation().a(this.b, this.c, this.d, this.e);
    }

    @Nullable
    protected Vec3D h() {
        Random random = this.a.getRandom();
        BlockPosition blockposition = this.a.getChunkCoordinates();

        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = blockposition.b(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (!this.f.e(blockposition1) && this.a.f(blockposition1) < 0.0F) {
                return Vec3D.c((BaseBlockPosition) blockposition1);
            }
        }

        return null;
    }
}
