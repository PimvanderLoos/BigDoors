package net.minecraft.server;

import java.util.Random;

public class BlockMagma extends Block {

    public BlockMagma() {
        super(Material.STONE);
        this.a(CreativeModeTab.b);
        this.a(0.2F);
        this.a(true);
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return MaterialMapColor.L;
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isFireProof() && entity instanceof EntityLiving && !EnchantmentManager.i((EntityLiving) entity)) {
            entity.damageEntity(DamageSource.HOT_FLOOR, 1.0F);
        }

        super.stepOn(world, blockposition, entity);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.getBlock() == Blocks.WATER || iblockdata1.getBlock() == Blocks.FLOWING_WATER) {
            world.setAir(blockposition1);
            world.a((EntityHuman) null, blockposition, SoundEffects.bN, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
            if (world instanceof WorldServer) {
                ((WorldServer) world).a(EnumParticle.SMOKE_LARGE, (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.25D, (double) blockposition1.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D, new int[0]);
            }
        }

    }

    public boolean a(IBlockData iblockdata, Entity entity) {
        return entity.isFireProof();
    }
}
