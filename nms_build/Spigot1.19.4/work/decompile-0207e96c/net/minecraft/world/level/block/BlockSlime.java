package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class BlockSlime extends BlockHalfTransparent {

    public BlockSlime(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(world, iblockdata, blockposition, entity, f);
        } else {
            entity.causeFallDamage(f, 0.0F, world.damageSources().fall());
        }

    }

    @Override
    public void updateEntityAfterFallOn(IBlockAccess iblockaccess, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(iblockaccess, entity);
        } else {
            this.bounceUp(entity);
        }

    }

    private void bounceUp(Entity entity) {
        Vec3D vec3d = entity.getDeltaMovement();

        if (vec3d.y < 0.0D) {
            double d0 = entity instanceof EntityLiving ? 1.0D : 0.8D;

            entity.setDeltaMovement(vec3d.x, -vec3d.y * d0, vec3d.z);
        }

    }

    @Override
    public void stepOn(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        double d0 = Math.abs(entity.getDeltaMovement().y);

        if (d0 < 0.1D && !entity.isSteppingCarefully()) {
            double d1 = 0.4D + d0 * 0.2D;

            entity.setDeltaMovement(entity.getDeltaMovement().multiply(d1, 1.0D, d1));
        }

        super.stepOn(world, blockposition, iblockdata, entity);
    }
}
