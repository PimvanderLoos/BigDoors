package net.minecraft.server;

import java.util.Random;

public class BlockSapling extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateEnum<BlockWood.EnumLogVariant> TYPE = BlockStateEnum.of("type", BlockWood.EnumLogVariant.class);
    public static final BlockStateInteger STAGE = BlockStateInteger.of("stage", 0, 1);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

    protected BlockSapling() {
        this.w(this.blockStateList.getBlockData().set(BlockSapling.TYPE, BlockWood.EnumLogVariant.OAK).set(BlockSapling.STAGE, Integer.valueOf(0)));
        this.a(CreativeModeTab.c);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSapling.d;
    }

    public String getName() {
        return LocaleI18n.get(this.a() + "." + BlockWood.EnumLogVariant.OAK.d() + ".name");
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            super.b(world, blockposition, iblockdata, random);
            if (world.getLightLevel(blockposition.up()) >= 9 && random.nextInt(7) == 0) {
                this.grow(world, blockposition, iblockdata, random);
            }

        }
    }

    public void grow(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (((Integer) iblockdata.get(BlockSapling.STAGE)).intValue() == 0) {
            world.setTypeAndData(blockposition, iblockdata.a((IBlockState) BlockSapling.STAGE), 4);
        } else {
            this.d(world, blockposition, iblockdata, random);
        }

    }

    public void d(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        Object object = random.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
        int i = 0;
        int j = 0;
        boolean flag = false;
        IBlockData iblockdata1;

        switch ((BlockWood.EnumLogVariant) iblockdata.get(BlockSapling.TYPE)) {
        case SPRUCE:
            label66:
            for (i = 0; i >= -1; --i) {
                for (j = 0; j >= -1; --j) {
                    if (this.a(world, blockposition, i, j, BlockWood.EnumLogVariant.SPRUCE)) {
                        object = new WorldGenMegaTree(false, random.nextBoolean());
                        flag = true;
                        break label66;
                    }
                }
            }

            if (!flag) {
                i = 0;
                j = 0;
                object = new WorldGenTaiga2(true);
            }
            break;

        case BIRCH:
            object = new WorldGenForest(true, false);
            break;

        case JUNGLE:
            iblockdata1 = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.JUNGLE);
            IBlockData iblockdata2 = Blocks.LEAVES.getBlockData().set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.JUNGLE).set(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

            label78:
            for (i = 0; i >= -1; --i) {
                for (j = 0; j >= -1; --j) {
                    if (this.a(world, blockposition, i, j, BlockWood.EnumLogVariant.JUNGLE)) {
                        object = new WorldGenJungleTree(true, 10, 20, iblockdata1, iblockdata2);
                        flag = true;
                        break label78;
                    }
                }
            }

            if (!flag) {
                i = 0;
                j = 0;
                object = new WorldGenTrees(true, 4 + random.nextInt(7), iblockdata1, iblockdata2, false);
            }
            break;

        case ACACIA:
            object = new WorldGenAcaciaTree(true);
            break;

        case DARK_OAK:
            label90:
            for (i = 0; i >= -1; --i) {
                for (j = 0; j >= -1; --j) {
                    if (this.a(world, blockposition, i, j, BlockWood.EnumLogVariant.DARK_OAK)) {
                        object = new WorldGenForestTree(true);
                        flag = true;
                        break label90;
                    }
                }
            }

            if (!flag) {
                return;
            }

        case OAK:
        }

        iblockdata1 = Blocks.AIR.getBlockData();
        if (flag) {
            world.setTypeAndData(blockposition.a(i, 0, j), iblockdata1, 4);
            world.setTypeAndData(blockposition.a(i + 1, 0, j), iblockdata1, 4);
            world.setTypeAndData(blockposition.a(i, 0, j + 1), iblockdata1, 4);
            world.setTypeAndData(blockposition.a(i + 1, 0, j + 1), iblockdata1, 4);
        } else {
            world.setTypeAndData(blockposition, iblockdata1, 4);
        }

        if (!((WorldGenerator) object).generate(world, random, blockposition.a(i, 0, j))) {
            if (flag) {
                world.setTypeAndData(blockposition.a(i, 0, j), iblockdata, 4);
                world.setTypeAndData(blockposition.a(i + 1, 0, j), iblockdata, 4);
                world.setTypeAndData(blockposition.a(i, 0, j + 1), iblockdata, 4);
                world.setTypeAndData(blockposition.a(i + 1, 0, j + 1), iblockdata, 4);
            } else {
                world.setTypeAndData(blockposition, iblockdata, 4);
            }
        }

    }

    private boolean a(World world, BlockPosition blockposition, int i, int j, BlockWood.EnumLogVariant blockwood_enumlogvariant) {
        return this.a(world, blockposition.a(i, 0, j), blockwood_enumlogvariant) && this.a(world, blockposition.a(i + 1, 0, j), blockwood_enumlogvariant) && this.a(world, blockposition.a(i, 0, j + 1), blockwood_enumlogvariant) && this.a(world, blockposition.a(i + 1, 0, j + 1), blockwood_enumlogvariant);
    }

    public boolean a(World world, BlockPosition blockposition, BlockWood.EnumLogVariant blockwood_enumlogvariant) {
        IBlockData iblockdata = world.getType(blockposition);

        return iblockdata.getBlock() == this && iblockdata.get(BlockSapling.TYPE) == blockwood_enumlogvariant;
    }

    public int getDropData(IBlockData iblockdata) {
        return ((BlockWood.EnumLogVariant) iblockdata.get(BlockSapling.TYPE)).a();
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        BlockWood.EnumLogVariant[] ablockwood_enumlogvariant = BlockWood.EnumLogVariant.values();
        int i = ablockwood_enumlogvariant.length;

        for (int j = 0; j < i; ++j) {
            BlockWood.EnumLogVariant blockwood_enumlogvariant = ablockwood_enumlogvariant[j];

            nonnulllist.add(new ItemStack(this, 1, blockwood_enumlogvariant.a()));
        }

    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) world.random.nextFloat() < 0.45D;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.grow(world, blockposition, iblockdata, random);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockSapling.TYPE, BlockWood.EnumLogVariant.a(i & 7)).set(BlockSapling.STAGE, Integer.valueOf((i & 8) >> 3));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((BlockWood.EnumLogVariant) iblockdata.get(BlockSapling.TYPE)).a();

        i |= ((Integer) iblockdata.get(BlockSapling.STAGE)).intValue() << 3;
        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockSapling.TYPE, BlockSapling.STAGE});
    }
}
