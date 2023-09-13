package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.EntityDolphin;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalWaterJump extends PathfinderGoalWaterJumpAbstract {

    private static final int[] a = new int[]{0, 1, 4, 5, 6, 7};
    private final EntityDolphin b;
    private final int c;
    private boolean d;

    public PathfinderGoalWaterJump(EntityDolphin entitydolphin, int i) {
        this.b = entitydolphin;
        this.c = i;
    }

    @Override
    public boolean a() {
        if (this.b.getRandom().nextInt(this.c) != 0) {
            return false;
        } else {
            EnumDirection enumdirection = this.b.getAdjustedDirection();
            int i = enumdirection.getAdjacentX();
            int j = enumdirection.getAdjacentZ();
            BlockPosition blockposition = this.b.getChunkCoordinates();
            int[] aint = PathfinderGoalWaterJump.a;
            int k = aint.length;

            for (int l = 0; l < k; ++l) {
                int i1 = aint[l];

                if (!this.a(blockposition, i, j, i1) || !this.b(blockposition, i, j, i1)) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean a(BlockPosition blockposition, int i, int j, int k) {
        BlockPosition blockposition1 = blockposition.b(i * k, 0, j * k);

        return this.b.world.getFluid(blockposition1).a((Tag) TagsFluid.WATER) && !this.b.world.getType(blockposition1).getMaterial().isSolid();
    }

    private boolean b(BlockPosition blockposition, int i, int j, int k) {
        return this.b.world.getType(blockposition.b(i * k, 1, j * k)).isAir() && this.b.world.getType(blockposition.b(i * k, 2, j * k)).isAir();
    }

    @Override
    public boolean b() {
        double d0 = this.b.getMot().y;

        return (d0 * d0 >= 0.029999999329447746D || this.b.pitch == 0.0F || Math.abs(this.b.pitch) >= 10.0F || !this.b.isInWater()) && !this.b.isOnGround();
    }

    @Override
    public boolean C_() {
        return false;
    }

    @Override
    public void c() {
        EnumDirection enumdirection = this.b.getAdjustedDirection();

        this.b.setMot(this.b.getMot().add((double) enumdirection.getAdjacentX() * 0.6D, 0.7D, (double) enumdirection.getAdjacentZ() * 0.6D));
        this.b.getNavigation().o();
    }

    @Override
    public void d() {
        this.b.pitch = 0.0F;
    }

    @Override
    public void e() {
        boolean flag = this.d;

        if (!flag) {
            Fluid fluid = this.b.world.getFluid(this.b.getChunkCoordinates());

            this.d = fluid.a((Tag) TagsFluid.WATER);
        }

        if (this.d && !flag) {
            this.b.playSound(SoundEffects.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
        }

        Vec3D vec3d = this.b.getMot();

        if (vec3d.y * vec3d.y < 0.029999999329447746D && this.b.pitch != 0.0F) {
            this.b.pitch = MathHelper.j(this.b.pitch, 0.0F, 0.2F);
        } else {
            double d0 = Math.sqrt(Entity.c(vec3d));
            double d1 = Math.signum(-vec3d.y) * Math.acos(d0 / vec3d.f()) * 57.2957763671875D;

            this.b.pitch = (float) d1;
        }

    }
}
