package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.phys.Vec3D;

public class BlockSlime extends BlockHalfTransparent {

    public BlockSlime(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        if (entity.bw()) {
            super.fallOn(world, blockposition, entity, f);
        } else {
            entity.b(f, 0.0F);
        }

    }

    @Override
    public void a(IBlockAccess iblockaccess, Entity entity) {
        if (entity.bw()) {
            super.a(iblockaccess, entity);
        } else {
            this.a(entity);
        }

    }

    private void a(Entity entity) {
        Vec3D vec3d = entity.getMot();

        if (vec3d.y < 0.0D) {
            double d0 = entity instanceof EntityLiving ? 1.0D : 0.8D;

            entity.setMot(vec3d.x, -vec3d.y * d0, vec3d.z);
        }

    }

    @Override
    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        double d0 = Math.abs(entity.getMot().y);

        if (d0 < 0.1D && !entity.bv()) {
            double d1 = 0.4D + d0 * 0.2D;

            entity.setMot(entity.getMot().d(d1, 1.0D, d1));
        }

        super.stepOn(world, blockposition, entity);
    }
}
