package net.minecraft.server;

public class BlockSlime extends BlockHalfTransparent {

    public BlockSlime(Block.Info block_info) {
        super(block_info);
    }

    public TextureType c() {
        return TextureType.TRANSLUCENT;
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        if (entity.isSneaking()) {
            super.fallOn(world, blockposition, entity, f);
        } else {
            entity.c(f, 0.0F);
        }

    }

    public void a(IBlockAccess iblockaccess, Entity entity) {
        if (entity.isSneaking()) {
            super.a(iblockaccess, entity);
        } else if (entity.motY < 0.0D) {
            entity.motY = -entity.motY;
            if (!(entity instanceof EntityLiving)) {
                entity.motY *= 0.8D;
            }
        }

    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        if (Math.abs(entity.motY) < 0.1D && !entity.isSneaking()) {
            double d0 = 0.4D + Math.abs(entity.motY) * 0.2D;

            entity.motX *= d0;
            entity.motZ *= d0;
        }

        super.stepOn(world, blockposition, entity);
    }

    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 0;
    }
}
