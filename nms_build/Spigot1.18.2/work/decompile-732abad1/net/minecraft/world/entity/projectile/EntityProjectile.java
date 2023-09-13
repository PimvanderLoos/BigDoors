package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEndGateway;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityProjectile extends IProjectile {

    protected EntityProjectile(EntityTypes<? extends EntityProjectile> entitytypes, World world) {
        super(entitytypes, world);
    }

    protected EntityProjectile(EntityTypes<? extends EntityProjectile> entitytypes, double d0, double d1, double d2, World world) {
        this(entitytypes, world);
        this.setPos(d0, d1, d2);
    }

    protected EntityProjectile(EntityTypes<? extends EntityProjectile> entitytypes, EntityLiving entityliving, World world) {
        this(entitytypes, entityliving.getX(), entityliving.getEyeY() - 0.10000000149011612D, entityliving.getZ(), world);
        this.setOwner(entityliving);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        double d1 = this.getBoundingBox().getSize() * 4.0D;

        if (Double.isNaN(d1)) {
            d1 = 4.0D;
        }

        d1 *= 64.0D;
        return d0 < d1 * d1;
    }

    @Override
    public void tick() {
        super.tick();
        MovingObjectPosition movingobjectposition = ProjectileHelper.getHitResult(this, this::canHitEntity);
        boolean flag = false;

        if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            BlockPosition blockposition = ((MovingObjectPositionBlock) movingobjectposition).getBlockPos();
            IBlockData iblockdata = this.level.getBlockState(blockposition);

            if (iblockdata.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockposition);
                flag = true;
            } else if (iblockdata.is(Blocks.END_GATEWAY)) {
                TileEntity tileentity = this.level.getBlockEntity(blockposition);

                if (tileentity instanceof TileEntityEndGateway && TileEntityEndGateway.canEntityTeleport(this)) {
                    TileEntityEndGateway.teleportEntity(this.level, blockposition, iblockdata, this, (TileEntityEndGateway) tileentity);
                }

                flag = true;
            }
        }

        if (movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS && !flag) {
            this.onHit(movingobjectposition);
        }

        this.checkInsideBlocks();
        Vec3D vec3d = this.getDeltaMovement();
        double d0 = this.getX() + vec3d.x;
        double d1 = this.getY() + vec3d.y;
        double d2 = this.getZ() + vec3d.z;

        this.updateRotation();
        float f;

        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f1 = 0.25F;

                this.level.addParticle(Particles.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
            }

            f = 0.8F;
        } else {
            f = 0.99F;
        }

        this.setDeltaMovement(vec3d.scale((double) f));
        if (!this.isNoGravity()) {
            Vec3D vec3d1 = this.getDeltaMovement();

            this.setDeltaMovement(vec3d1.x, vec3d1.y - (double) this.getGravity(), vec3d1.z);
        }

        this.setPos(d0, d1, d2);
    }

    protected float getGravity() {
        return 0.03F;
    }
}
