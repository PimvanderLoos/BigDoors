package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockIce extends BlockHalfTransparent {

    public BlockIce() {
        super(Material.ICE, false);
        this.frictionFactor = 0.98F;
        this.a(true);
        this.a(CreativeModeTab.b);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        entityhuman.b(StatisticList.a((Block) this));
        entityhuman.applyExhaustion(0.005F);
        if (this.n() && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) > 0) {
            a(world, blockposition, this.u(iblockdata));
        } else {
            if (world.worldProvider.l()) {
                world.setAir(blockposition);
                return;
            }

            int i = EnchantmentManager.getEnchantmentLevel(Enchantments.LOOT_BONUS_BLOCKS, itemstack);

            this.b(world, blockposition, iblockdata, i);
            Material material = world.getType(blockposition.down()).getMaterial();

            if (material.isSolid() || material.isLiquid()) {
                world.setTypeUpdate(blockposition, Blocks.FLOWING_WATER.getBlockData());
            }
        }

    }

    public int a(Random random) {
        return 0;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (world.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 11 - this.getBlockData().c()) {
            this.b(world, blockposition);
        }

    }

    protected void b(World world, BlockPosition blockposition) {
        if (world.worldProvider.l()) {
            world.setAir(blockposition);
        } else {
            this.b(world, blockposition, world.getType(blockposition), 0);
            world.setTypeUpdate(blockposition, Blocks.WATER.getBlockData());
            world.a(blockposition, (Block) Blocks.WATER, blockposition);
        }
    }

    public EnumPistonReaction h(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }
}
