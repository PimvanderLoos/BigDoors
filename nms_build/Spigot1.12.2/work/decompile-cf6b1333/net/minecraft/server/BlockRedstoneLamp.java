package net.minecraft.server;

import java.util.Random;

public class BlockRedstoneLamp extends Block {

    private final boolean a;

    public BlockRedstoneLamp(boolean flag) {
        super(Material.BUILDABLE_GLASS);
        this.a = flag;
        if (flag) {
            this.a(1.0F);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            if (this.a && !world.isBlockIndirectlyPowered(blockposition)) {
                world.setTypeAndData(blockposition, Blocks.REDSTONE_LAMP.getBlockData(), 2);
            } else if (!this.a && world.isBlockIndirectlyPowered(blockposition)) {
                world.setTypeAndData(blockposition, Blocks.LIT_REDSTONE_LAMP.getBlockData(), 2);
            }

        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            if (this.a && !world.isBlockIndirectlyPowered(blockposition)) {
                world.a(blockposition, (Block) this, 4);
            } else if (!this.a && world.isBlockIndirectlyPowered(blockposition)) {
                world.setTypeAndData(blockposition, Blocks.LIT_REDSTONE_LAMP.getBlockData(), 2);
            }

        }
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (this.a && !world.isBlockIndirectlyPowered(blockposition)) {
                world.setTypeAndData(blockposition, Blocks.REDSTONE_LAMP.getBlockData(), 2);
            }

        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.REDSTONE_LAMP);
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.REDSTONE_LAMP);
    }

    protected ItemStack u(IBlockData iblockdata) {
        return new ItemStack(Blocks.REDSTONE_LAMP);
    }
}
