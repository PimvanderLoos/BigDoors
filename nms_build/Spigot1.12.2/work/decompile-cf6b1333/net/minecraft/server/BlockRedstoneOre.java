package net.minecraft.server;

import java.util.Random;

public class BlockRedstoneOre extends Block {

    private final boolean a;

    public BlockRedstoneOre(boolean flag) {
        super(Material.STONE);
        if (flag) {
            this.a(true);
        }

        this.a = flag;
    }

    public int a(World world) {
        return 30;
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.interact(world, blockposition);
        super.attack(world, blockposition, entityhuman);
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        this.interact(world, blockposition);
        super.stepOn(world, blockposition, entity);
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        this.interact(world, blockposition);
        return super.interact(world, blockposition, iblockdata, entityhuman, enumhand, enumdirection, f, f1, f2);
    }

    private void interact(World world, BlockPosition blockposition) {
        this.playEffect(world, blockposition);
        if (this == Blocks.REDSTONE_ORE) {
            world.setTypeUpdate(blockposition, Blocks.LIT_REDSTONE_ORE.getBlockData());
        }

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (this == Blocks.LIT_REDSTONE_ORE) {
            world.setTypeUpdate(blockposition, Blocks.REDSTONE_ORE.getBlockData());
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REDSTONE;
    }

    public int getDropCount(int i, Random random) {
        return this.a(random) + random.nextInt(i + 1);
    }

    public int a(Random random) {
        return 4 + random.nextInt(2);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        super.dropNaturally(world, blockposition, iblockdata, f, i);
        if (this.getDropType(iblockdata, world.random, i) != Item.getItemOf(this)) {
            int j = 1 + world.random.nextInt(5);

            this.dropExperience(world, blockposition, j);
        }

    }

    private void playEffect(World world, BlockPosition blockposition) {
        Random random = world.random;
        double d0 = 0.0625D;

        for (int i = 0; i < 6; ++i) {
            double d1 = (double) ((float) blockposition.getX() + random.nextFloat());
            double d2 = (double) ((float) blockposition.getY() + random.nextFloat());
            double d3 = (double) ((float) blockposition.getZ() + random.nextFloat());

            if (i == 0 && !world.getType(blockposition.up()).p()) {
                d2 = (double) blockposition.getY() + 0.0625D + 1.0D;
            }

            if (i == 1 && !world.getType(blockposition.down()).p()) {
                d2 = (double) blockposition.getY() - 0.0625D;
            }

            if (i == 2 && !world.getType(blockposition.south()).p()) {
                d3 = (double) blockposition.getZ() + 0.0625D + 1.0D;
            }

            if (i == 3 && !world.getType(blockposition.north()).p()) {
                d3 = (double) blockposition.getZ() - 0.0625D;
            }

            if (i == 4 && !world.getType(blockposition.east()).p()) {
                d1 = (double) blockposition.getX() + 0.0625D + 1.0D;
            }

            if (i == 5 && !world.getType(blockposition.west()).p()) {
                d1 = (double) blockposition.getX() - 0.0625D;
            }

            if (d1 < (double) blockposition.getX() || d1 > (double) (blockposition.getX() + 1) || d2 < 0.0D || d2 > (double) (blockposition.getY() + 1) || d3 < (double) blockposition.getZ() || d3 > (double) (blockposition.getZ() + 1)) {
                world.addParticle(EnumParticle.REDSTONE, d1, d2, d3, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }

    }

    protected ItemStack u(IBlockData iblockdata) {
        return new ItemStack(Blocks.REDSTONE_ORE);
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Item.getItemOf(Blocks.REDSTONE_ORE), 1, this.getDropData(iblockdata));
    }
}
