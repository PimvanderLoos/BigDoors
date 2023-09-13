package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockFlowerPot extends BlockTileEntity {

    public static final BlockStateInteger LEGACY_DATA = BlockStateInteger.of("legacy_data", 0, 15);
    public static final BlockStateEnum<BlockFlowerPot.EnumFlowerPotContents> CONTENTS = BlockStateEnum.of("contents", BlockFlowerPot.EnumFlowerPotContents.class);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);

    public BlockFlowerPot() {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockFlowerPot.CONTENTS, BlockFlowerPot.EnumFlowerPotContents.EMPTY).set(BlockFlowerPot.LEGACY_DATA, Integer.valueOf(0)));
    }

    public String getName() {
        return LocaleI18n.get("item.flowerPot.name");
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockFlowerPot.c;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);
        TileEntityFlowerPot tileentityflowerpot = this.c(world, blockposition);

        if (tileentityflowerpot == null) {
            return false;
        } else {
            ItemStack itemstack1 = tileentityflowerpot.getContents();

            if (itemstack1.isEmpty()) {
                if (!this.a(itemstack)) {
                    return false;
                }

                tileentityflowerpot.setContents(itemstack);
                entityhuman.b(StatisticList.T);
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemstack.subtract(1);
                }
            } else {
                if (itemstack.isEmpty()) {
                    entityhuman.a(enumhand, itemstack1);
                } else if (!entityhuman.c(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }

                tileentityflowerpot.setContents(ItemStack.a);
            }

            tileentityflowerpot.update();
            world.notify(blockposition, iblockdata, iblockdata, 3);
            return true;
        }
    }

    private boolean a(ItemStack itemstack) {
        Block block = Block.asBlock(itemstack.getItem());

        if (block != Blocks.YELLOW_FLOWER && block != Blocks.RED_FLOWER && block != Blocks.CACTUS && block != Blocks.BROWN_MUSHROOM && block != Blocks.RED_MUSHROOM && block != Blocks.SAPLING && block != Blocks.DEADBUSH) {
            int i = itemstack.getData();

            return block == Blocks.TALLGRASS && i == BlockLongGrass.EnumTallGrassType.FERN.a();
        } else {
            return true;
        }
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntityFlowerPot tileentityflowerpot = this.c(world, blockposition);

        if (tileentityflowerpot != null) {
            ItemStack itemstack = tileentityflowerpot.getContents();

            if (!itemstack.isEmpty()) {
                return itemstack;
            }
        }

        return new ItemStack(Items.FLOWER_POT);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return super.canPlace(world, blockposition) && world.getType(blockposition.down()).q();
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.getType(blockposition.down()).q()) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntityFlowerPot tileentityflowerpot = this.c(world, blockposition);

        if (tileentityflowerpot != null && tileentityflowerpot.getItem() != null) {
            a(world, blockposition, new ItemStack(tileentityflowerpot.getItem(), 1, tileentityflowerpot.getData()));
        }

        super.remove(world, blockposition, iblockdata);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        super.a(world, blockposition, iblockdata, entityhuman);
        if (entityhuman.abilities.canInstantlyBuild) {
            TileEntityFlowerPot tileentityflowerpot = this.c(world, blockposition);

            if (tileentityflowerpot != null) {
                tileentityflowerpot.setContents(ItemStack.a);
            }
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.FLOWER_POT;
    }

    @Nullable
    private TileEntityFlowerPot c(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot) tileentity : null;
    }

    public TileEntity a(World world, int i) {
        Object object = null;
        int j = 0;

        switch (i) {
        case 1:
            object = Blocks.RED_FLOWER;
            j = BlockFlowers.EnumFlowerVarient.POPPY.b();
            break;

        case 2:
            object = Blocks.YELLOW_FLOWER;
            break;

        case 3:
            object = Blocks.SAPLING;
            j = BlockWood.EnumLogVariant.OAK.a();
            break;

        case 4:
            object = Blocks.SAPLING;
            j = BlockWood.EnumLogVariant.SPRUCE.a();
            break;

        case 5:
            object = Blocks.SAPLING;
            j = BlockWood.EnumLogVariant.BIRCH.a();
            break;

        case 6:
            object = Blocks.SAPLING;
            j = BlockWood.EnumLogVariant.JUNGLE.a();
            break;

        case 7:
            object = Blocks.RED_MUSHROOM;
            break;

        case 8:
            object = Blocks.BROWN_MUSHROOM;
            break;

        case 9:
            object = Blocks.CACTUS;
            break;

        case 10:
            object = Blocks.DEADBUSH;
            break;

        case 11:
            object = Blocks.TALLGRASS;
            j = BlockLongGrass.EnumTallGrassType.FERN.a();
            break;

        case 12:
            object = Blocks.SAPLING;
            j = BlockWood.EnumLogVariant.ACACIA.a();
            break;

        case 13:
            object = Blocks.SAPLING;
            j = BlockWood.EnumLogVariant.DARK_OAK.a();
        }

        return new TileEntityFlowerPot(Item.getItemOf((Block) object), j);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockFlowerPot.CONTENTS, BlockFlowerPot.LEGACY_DATA});
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockFlowerPot.LEGACY_DATA)).intValue();
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockFlowerPot.EnumFlowerPotContents blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.EMPTY;
        TileEntity tileentity = iblockaccess instanceof ChunkCache ? ((ChunkCache) iblockaccess).a(blockposition, Chunk.EnumTileEntityState.CHECK) : iblockaccess.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityFlowerPot) {
            TileEntityFlowerPot tileentityflowerpot = (TileEntityFlowerPot) tileentity;
            Item item = tileentityflowerpot.getItem();

            if (item instanceof ItemBlock) {
                int i = tileentityflowerpot.getData();
                Block block = Block.asBlock(item);

                if (block == Blocks.SAPLING) {
                    switch (BlockWood.EnumLogVariant.a(i)) {
                    case OAK:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.OAK_SAPLING;
                        break;

                    case SPRUCE:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.SPRUCE_SAPLING;
                        break;

                    case BIRCH:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.BIRCH_SAPLING;
                        break;

                    case JUNGLE:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.JUNGLE_SAPLING;
                        break;

                    case ACACIA:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.ACACIA_SAPLING;
                        break;

                    case DARK_OAK:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.DARK_OAK_SAPLING;
                        break;

                    default:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.EMPTY;
                    }
                } else if (block == Blocks.TALLGRASS) {
                    switch (i) {
                    case 0:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.DEAD_BUSH;
                        break;

                    case 2:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.FERN;
                        break;

                    default:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.EMPTY;
                    }
                } else if (block == Blocks.YELLOW_FLOWER) {
                    blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.DANDELION;
                } else if (block == Blocks.RED_FLOWER) {
                    switch (BlockFlowers.EnumFlowerVarient.a(BlockFlowers.EnumFlowerType.RED, i)) {
                    case POPPY:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.POPPY;
                        break;

                    case BLUE_ORCHID:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.BLUE_ORCHID;
                        break;

                    case ALLIUM:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.ALLIUM;
                        break;

                    case HOUSTONIA:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.HOUSTONIA;
                        break;

                    case RED_TULIP:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.RED_TULIP;
                        break;

                    case ORANGE_TULIP:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.ORANGE_TULIP;
                        break;

                    case WHITE_TULIP:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.WHITE_TULIP;
                        break;

                    case PINK_TULIP:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.PINK_TULIP;
                        break;

                    case OXEYE_DAISY:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.OXEYE_DAISY;
                        break;

                    default:
                        blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.EMPTY;
                    }
                } else if (block == Blocks.RED_MUSHROOM) {
                    blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.MUSHROOM_RED;
                } else if (block == Blocks.BROWN_MUSHROOM) {
                    blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.MUSHROOM_BROWN;
                } else if (block == Blocks.DEADBUSH) {
                    blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.DEAD_BUSH;
                } else if (block == Blocks.CACTUS) {
                    blockflowerpot_enumflowerpotcontents = BlockFlowerPot.EnumFlowerPotContents.CACTUS;
                }
            }
        }

        return iblockdata.set(BlockFlowerPot.CONTENTS, blockflowerpot_enumflowerpotcontents);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public static enum EnumFlowerPotContents implements INamable {

        EMPTY("empty"), POPPY("rose"), BLUE_ORCHID("blue_orchid"), ALLIUM("allium"), HOUSTONIA("houstonia"), RED_TULIP("red_tulip"), ORANGE_TULIP("orange_tulip"), WHITE_TULIP("white_tulip"), PINK_TULIP("pink_tulip"), OXEYE_DAISY("oxeye_daisy"), DANDELION("dandelion"), OAK_SAPLING("oak_sapling"), SPRUCE_SAPLING("spruce_sapling"), BIRCH_SAPLING("birch_sapling"), JUNGLE_SAPLING("jungle_sapling"), ACACIA_SAPLING("acacia_sapling"), DARK_OAK_SAPLING("dark_oak_sapling"), MUSHROOM_RED("mushroom_red"), MUSHROOM_BROWN("mushroom_brown"), DEAD_BUSH("dead_bush"), FERN("fern"), CACTUS("cactus");

        private final String w;

        private EnumFlowerPotContents(String s) {
            this.w = s;
        }

        public String toString() {
            return this.w;
        }

        public String getName() {
            return this.w;
        }
    }
}
