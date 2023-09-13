package net.minecraft.server;

import java.util.Random;

public class BlockGrass extends Block implements IBlockFragilePlantElement {

    public static final BlockStateBoolean SNOWY = BlockStateBoolean.of("snowy");

    protected BlockGrass() {
        super(Material.GRASS);
        this.w(this.blockStateList.getBlockData().set(BlockGrass.SNOWY, Boolean.valueOf(false)));
        this.a(true);
        this.a(CreativeModeTab.b);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = iblockaccess.getType(blockposition.up()).getBlock();

        return iblockdata.set(BlockGrass.SNOWY, Boolean.valueOf(block == Blocks.SNOW || block == Blocks.SNOW_LAYER));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (world.getLightLevel(blockposition.up()) < 4 && world.getType(blockposition.up()).c() > 2) {
                world.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData());
            } else {
                if (world.getLightLevel(blockposition.up()) >= 9) {
                    for (int i = 0; i < 4; ++i) {
                        BlockPosition blockposition1 = blockposition.a(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                        if (blockposition1.getY() >= 0 && blockposition1.getY() < 256 && !world.isLoaded(blockposition1)) {
                            return;
                        }

                        IBlockData iblockdata1 = world.getType(blockposition1.up());
                        IBlockData iblockdata2 = world.getType(blockposition1);

                        if (iblockdata2.getBlock() == Blocks.DIRT && iblockdata2.get(BlockDirt.VARIANT) == BlockDirt.EnumDirtVariant.DIRT && world.getLightLevel(blockposition1.up()) >= 4 && iblockdata1.c() <= 2) {
                            world.setTypeUpdate(blockposition1, Blocks.GRASS.getBlockData());
                        }
                    }
                }

            }
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Blocks.DIRT.getDropType(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT), random, i);
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.up();
        int i = 0;

        while (i < 128) {
            BlockPosition blockposition2 = blockposition1;
            int j = 0;

            while (true) {
                if (j < i / 16) {
                    blockposition2 = blockposition2.a(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                    if (world.getType(blockposition2.down()).getBlock() == Blocks.GRASS && !world.getType(blockposition2).l()) {
                        ++j;
                        continue;
                    }
                } else if (world.getType(blockposition2).getBlock().material == Material.AIR) {
                    if (random.nextInt(8) == 0) {
                        BlockFlowers.EnumFlowerVarient blockflowers_enumflowervarient = world.getBiome(blockposition2).a(random, blockposition2);
                        BlockFlowers blockflowers = blockflowers_enumflowervarient.a().a();
                        IBlockData iblockdata1 = blockflowers.getBlockData().set(blockflowers.g(), blockflowers_enumflowervarient);

                        if (blockflowers.f(world, blockposition2, iblockdata1)) {
                            world.setTypeAndData(blockposition2, iblockdata1, 3);
                        }
                    } else {
                        IBlockData iblockdata2 = Blocks.TALLGRASS.getBlockData().set(BlockLongGrass.TYPE, BlockLongGrass.EnumTallGrassType.GRASS);

                        if (Blocks.TALLGRASS.f(world, blockposition2, iblockdata2)) {
                            world.setTypeAndData(blockposition2, iblockdata2, 3);
                        }
                    }
                }

                ++i;
                break;
            }
        }

    }

    public int toLegacyData(IBlockData iblockdata) {
        return 0;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockGrass.SNOWY});
    }
}
