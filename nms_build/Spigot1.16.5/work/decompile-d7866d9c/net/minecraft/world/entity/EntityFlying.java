package net.minecraft.world.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityFlying extends EntityInsentient {

    protected EntityFlying(EntityTypes<? extends EntityFlying> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public boolean b(float f, float f1) {
        return false;
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    public void g(Vec3D vec3d) {
        if (this.isInWater()) {
            this.a(0.02F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.800000011920929D));
        } else if (this.aQ()) {
            this.a(0.02F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.5D));
        } else {
            float f = 0.91F;

            if (this.onGround) {
                f = this.world.getType(new BlockPosition(this.locX(), this.locY() - 1.0D, this.locZ())).getBlock().getFrictionFactor() * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);

            f = 0.91F;
            if (this.onGround) {
                f = this.world.getType(new BlockPosition(this.locX(), this.locY() - 1.0D, this.locZ())).getBlock().getFrictionFactor() * 0.91F;
            }

            this.a(this.onGround ? 0.1F * f1 : 0.02F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a((double) f));
        }

        this.a((EntityLiving) this, false);
    }

    @Override
    public boolean isClimbing() {
        return false;
    }
}
