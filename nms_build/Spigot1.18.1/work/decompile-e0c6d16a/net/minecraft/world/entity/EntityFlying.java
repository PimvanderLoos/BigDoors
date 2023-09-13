package net.minecraft.world.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityFlying extends EntityInsentient {

    protected EntityFlying(EntityTypes<? extends EntityFlying> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isInWater()) {
            this.moveRelative(0.02F, vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929D));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
        } else {
            float f = 0.91F;

            if (this.onGround) {
                f = this.level.getBlockState(new BlockPosition(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getFriction() * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);

            f = 0.91F;
            if (this.onGround) {
                f = this.level.getBlockState(new BlockPosition(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getFriction() * 0.91F;
            }

            this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale((double) f));
        }

        this.calculateEntityAnimation(this, false);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }
}
