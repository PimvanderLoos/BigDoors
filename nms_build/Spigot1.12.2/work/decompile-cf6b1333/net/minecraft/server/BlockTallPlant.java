package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockTallPlant extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateEnum<BlockTallPlant.EnumTallFlowerVariants> VARIANT = BlockStateEnum.of("variant", BlockTallPlant.EnumTallFlowerVariants.class);
    public static final BlockStateEnum<BlockTallPlant.EnumTallPlantHalf> HALF = BlockStateEnum.of("half", BlockTallPlant.EnumTallPlantHalf.class);
    public static final BlockStateEnum<EnumDirection> d = BlockFacingHorizontal.FACING;

    public BlockTallPlant() {
        super(Material.REPLACEABLE_PLANT);
        this.w(this.blockStateList.getBlockData().set(BlockTallPlant.VARIANT, BlockTallPlant.EnumTallFlowerVariants.SUNFLOWER).set(BlockTallPlant.HALF, BlockTallPlant.EnumTallPlantHalf.LOWER).set(BlockTallPlant.d, EnumDirection.NORTH));
        this.c(0.0F);
        this.a(SoundEffectType.c);
        this.c("doublePlant");
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockTallPlant.j;
    }

    private BlockTallPlant.EnumTallFlowerVariants a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.getBlock() == this) {
            iblockdata = iblockdata.c(iblockaccess, blockposition);
            return (BlockTallPlant.EnumTallFlowerVariants) iblockdata.get(BlockTallPlant.VARIANT);
        } else {
            return BlockTallPlant.EnumTallFlowerVariants.FERN;
        }
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return super.canPlace(world, blockposition) && world.isEmpty(blockposition.up());
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        if (iblockdata.getBlock() != this) {
            return true;
        } else {
            BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = (BlockTallPlant.EnumTallFlowerVariants) iblockdata.c(iblockaccess, blockposition).get(BlockTallPlant.VARIANT);

            return blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.FERN || blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.GRASS;
        }
    }

    protected void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.f(world, blockposition, iblockdata)) {
            boolean flag = iblockdata.get(BlockTallPlant.HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER;
            BlockPosition blockposition1 = flag ? blockposition : blockposition.up();
            BlockPosition blockposition2 = flag ? blockposition.down() : blockposition;
            Object object = flag ? this : world.getType(blockposition1).getBlock();
            Object object1 = flag ? world.getType(blockposition2).getBlock() : this;

            if (object == this) {
                world.setTypeAndData(blockposition1, Blocks.AIR.getBlockData(), 2);
            }

            if (object1 == this) {
                world.setTypeAndData(blockposition2, Blocks.AIR.getBlockData(), 3);
                if (!flag) {
                    this.b(world, blockposition2, iblockdata, 0);
                }
            }

        }
    }

    public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.get(BlockTallPlant.HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER) {
            return world.getType(blockposition.down()).getBlock() == this;
        } else {
            IBlockData iblockdata1 = world.getType(blockposition.up());

            return iblockdata1.getBlock() == this && super.f(world, blockposition, iblockdata1);
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        if (iblockdata.get(BlockTallPlant.HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER) {
            return Items.a;
        } else {
            BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = (BlockTallPlant.EnumTallFlowerVariants) iblockdata.get(BlockTallPlant.VARIANT);

            return blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.FERN ? Items.a : (blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.GRASS ? (random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.a) : super.getDropType(iblockdata, random, i));
        }
    }

    public int getDropData(IBlockData iblockdata) {
        return iblockdata.get(BlockTallPlant.HALF) != BlockTallPlant.EnumTallPlantHalf.UPPER && iblockdata.get(BlockTallPlant.VARIANT) != BlockTallPlant.EnumTallFlowerVariants.GRASS ? ((BlockTallPlant.EnumTallFlowerVariants) iblockdata.get(BlockTallPlant.VARIANT)).a() : 0;
    }

    public void a(World world, BlockPosition blockposition, BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants, int i) {
        world.setTypeAndData(blockposition, this.getBlockData().set(BlockTallPlant.HALF, BlockTallPlant.EnumTallPlantHalf.LOWER).set(BlockTallPlant.VARIANT, blocktallplant_enumtallflowervariants), i);
        world.setTypeAndData(blockposition.up(), this.getBlockData().set(BlockTallPlant.HALF, BlockTallPlant.EnumTallPlantHalf.UPPER), i);
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        world.setTypeAndData(blockposition.up(), this.getBlockData().set(BlockTallPlant.HALF, BlockTallPlant.EnumTallPlantHalf.UPPER), 2);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (world.isClientSide || itemstack.getItem() != Items.SHEARS || iblockdata.get(BlockTallPlant.HALF) != BlockTallPlant.EnumTallPlantHalf.LOWER || !this.b(world, blockposition, iblockdata, entityhuman)) {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (iblockdata.get(BlockTallPlant.HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER) {
            if (world.getType(blockposition.down()).getBlock() == this) {
                if (entityhuman.abilities.canInstantlyBuild) {
                    world.setAir(blockposition.down());
                } else {
                    IBlockData iblockdata1 = world.getType(blockposition.down());
                    BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = (BlockTallPlant.EnumTallFlowerVariants) iblockdata1.get(BlockTallPlant.VARIANT);

                    if (blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.FERN && blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.GRASS) {
                        world.setAir(blockposition.down(), true);
                    } else if (world.isClientSide) {
                        world.setAir(blockposition.down());
                    } else if (!entityhuman.getItemInMainHand().isEmpty() && entityhuman.getItemInMainHand().getItem() == Items.SHEARS) {
                        this.b(world, blockposition, iblockdata1, entityhuman);
                        world.setAir(blockposition.down());
                    } else {
                        world.setAir(blockposition.down(), true);
                    }
                }
            }
        } else if (world.getType(blockposition.up()).getBlock() == this) {
            world.setTypeAndData(blockposition.up(), Blocks.AIR.getBlockData(), 2);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    private boolean b(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = (BlockTallPlant.EnumTallFlowerVariants) iblockdata.get(BlockTallPlant.VARIANT);

        if (blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.FERN && blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.GRASS) {
            return false;
        } else {
            entityhuman.b(StatisticList.a((Block) this));
            int i = (blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.GRASS ? BlockLongGrass.EnumTallGrassType.GRASS : BlockLongGrass.EnumTallGrassType.FERN).a();

            a(world, blockposition, new ItemStack(Blocks.TALLGRASS, 2, i));
            return true;
        }
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        BlockTallPlant.EnumTallFlowerVariants[] ablocktallplant_enumtallflowervariants = BlockTallPlant.EnumTallFlowerVariants.values();
        int i = ablocktallplant_enumtallflowervariants.length;

        for (int j = 0; j < i; ++j) {
            BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = ablocktallplant_enumtallflowervariants[j];

            nonnulllist.add(new ItemStack(this, 1, blocktallplant_enumtallflowervariants.a()));
        }

    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this, 1, this.a((IBlockAccess) world, blockposition, iblockdata).a());
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = this.a((IBlockAccess) world, blockposition, iblockdata);

        return blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.GRASS && blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.FERN;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        a(world, blockposition, new ItemStack(this, 1, this.a((IBlockAccess) world, blockposition, iblockdata).a()));
    }

    public IBlockData fromLegacyData(int i) {
        return (i & 8) > 0 ? this.getBlockData().set(BlockTallPlant.HALF, BlockTallPlant.EnumTallPlantHalf.UPPER) : this.getBlockData().set(BlockTallPlant.HALF, BlockTallPlant.EnumTallPlantHalf.LOWER).set(BlockTallPlant.VARIANT, BlockTallPlant.EnumTallFlowerVariants.a(i & 7));
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (iblockdata.get(BlockTallPlant.HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER) {
            IBlockData iblockdata1 = iblockaccess.getType(blockposition.down());

            if (iblockdata1.getBlock() == this) {
                iblockdata = iblockdata.set(BlockTallPlant.VARIANT, iblockdata1.get(BlockTallPlant.VARIANT));
            }
        }

        return iblockdata;
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockTallPlant.HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER ? 8 | ((EnumDirection) iblockdata.get(BlockTallPlant.d)).get2DRotationValue() : ((BlockTallPlant.EnumTallFlowerVariants) iblockdata.get(BlockTallPlant.VARIANT)).a();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockTallPlant.HALF, BlockTallPlant.VARIANT, BlockTallPlant.d});
    }

    public Block.EnumRandomOffset u() {
        return Block.EnumRandomOffset.XZ;
    }

    public static enum EnumTallPlantHalf implements INamable {

        UPPER, LOWER;

        private EnumTallPlantHalf() {}

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this == BlockTallPlant.EnumTallPlantHalf.UPPER ? "upper" : "lower";
        }
    }

    public static enum EnumTallFlowerVariants implements INamable {

        SUNFLOWER(0, "sunflower"), SYRINGA(1, "syringa"), GRASS(2, "double_grass", "grass"), FERN(3, "double_fern", "fern"), ROSE(4, "double_rose", "rose"), PAEONIA(5, "paeonia");

        private static final BlockTallPlant.EnumTallFlowerVariants[] g = new BlockTallPlant.EnumTallFlowerVariants[values().length];
        private final int h;
        private final String i;
        private final String j;

        private EnumTallFlowerVariants(int i, String s) {
            this(i, s, s);
        }

        private EnumTallFlowerVariants(int i, String s, String s1) {
            this.h = i;
            this.i = s;
            this.j = s1;
        }

        public int a() {
            return this.h;
        }

        public String toString() {
            return this.i;
        }

        public static BlockTallPlant.EnumTallFlowerVariants a(int i) {
            if (i < 0 || i >= BlockTallPlant.EnumTallFlowerVariants.g.length) {
                i = 0;
            }

            return BlockTallPlant.EnumTallFlowerVariants.g[i];
        }

        public String getName() {
            return this.i;
        }

        public String c() {
            return this.j;
        }

        static {
            BlockTallPlant.EnumTallFlowerVariants[] ablocktallplant_enumtallflowervariants = values();
            int i = ablocktallplant_enumtallflowervariants.length;

            for (int j = 0; j < i; ++j) {
                BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = ablocktallplant_enumtallflowervariants[j];

                BlockTallPlant.EnumTallFlowerVariants.g[blocktallplant_enumtallflowervariants.a()] = blocktallplant_enumtallflowervariants;
            }

        }
    }
}
