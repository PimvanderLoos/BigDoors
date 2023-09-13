package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockFlowerPot extends Block {

    private static final Map<Block, Block> b = Maps.newHashMap();
    protected static final VoxelShape a = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
    private final Block c;

    public BlockFlowerPot(Block block, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.c = block;
        BlockFlowerPot.b.put(block, this);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockFlowerPot.a;
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
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
                } else if (!entityhuman.g(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }

                world.setTypeAndData(blockposition, Blocks.FLOWER_POT.getBlockData(), 3);
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public Block c() {
        return this.c;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
