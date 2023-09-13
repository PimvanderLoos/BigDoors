package net.minecraft.server;

public class BlockSlime extends BlockHalfTransparent {

    public BlockSlime() {
        super(Material.CLAY, false, MaterialMapColor.d);
        this.a(CreativeModeTab.c);
        this.frictionFactor = 0.8F;
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        if (entity.isSneaking()) {
            super.fallOn(world, blockposition, entity, f);
        } else {
            entity.e(f, 0.0F);
        }

    }

    public void a(World world, Entity entity) {
        if (entity.isSneaking()) {
            super.a(world, entity);
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
}
