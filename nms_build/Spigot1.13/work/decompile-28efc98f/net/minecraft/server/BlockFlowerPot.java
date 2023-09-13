package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;

public class BlockFlowerPot extends Block {

    private static final Map<Block, Block> b = Maps.newHashMap();
    protected static final VoxelShape a = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
    private final Block c;

    public BlockFlowerPot(Block block, Block.Info block_info) {
        super(block_info);
        this.c = block;
        BlockFlowerPot.b.put(block, this);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockFlowerPot.a;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();
        Block block = item instanceof ItemBlock ? (Block) BlockFlowerPot.b.getOrDefault(((ItemBlock) item).getBlock(), Blocks.AIR) : Blocks.AIR;
        boolean flag = block == Blocks.AIR;
        boolean flag1 = this.c == Blocks.AIR;

        if (flag != flag1) {
            if (flag1) {
                world.setTypeAndData(blockposition, block.getBlockData(), 3);
                entityhuman.a(StatisticList.POT_FLOWER);
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemstack.subtract(1);
                }
            } else {
                ItemStack itemstack1 = new ItemStack(this.c);

                if (itemstack.isEmpty()) {
                    entityhuman.a(enumhand, itemstack1);
                } else if (!entityhuman.d(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }

                world.setTypeAndData(blockposition, Blocks.FLOWER_POT.getBlockData(), 3);
            }
        }

        return true;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return this.c == Blocks.AIR ? super.a(iblockaccess, blockposition, iblockdata) : new ItemStack(this.c);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Blocks.FLOWER_POT;
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        super.dropNaturally(iblockdata, world, blockposition, f, i);
        if (this.c != Blocks.AIR) {
            a(world, blockposition, new ItemStack(this.c));
        }

    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
