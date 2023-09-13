package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockFlowerPot extends Block {

    private static final Map<Block, Block> POTTED_BY_CONTENT = Maps.newHashMap();
    public static final float AABB_SIZE = 3.0F;
    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
    private final Block content;

    public BlockFlowerPot(Block block, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.content = block;
        BlockFlowerPot.POTTED_BY_CONTENT.put(block, this);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockFlowerPot.SHAPE;
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        Item item = itemstack.getItem();
        IBlockData iblockdata1 = (item instanceof ItemBlock ? (Block) BlockFlowerPot.POTTED_BY_CONTENT.getOrDefault(((ItemBlock) item).getBlock(), Blocks.AIR) : Blocks.AIR).defaultBlockState();
        boolean flag = iblockdata1.is(Blocks.AIR);
        boolean flag1 = this.isEmpty();

        if (flag != flag1) {
            if (flag1) {
                world.setBlock(blockposition, iblockdata1, 3);
                entityhuman.awardStat(StatisticList.POT_FLOWER);
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            } else {
                ItemStack itemstack1 = new ItemStack(this.content);

                if (itemstack.isEmpty()) {
                    entityhuman.setItemInHand(enumhand, itemstack1);
                } else if (!entityhuman.addItem(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }

                world.setBlock(blockposition, Blocks.FLOWER_POT.defaultBlockState(), 3);
            }

            world.gameEvent((Entity) entityhuman, GameEvent.BLOCK_CHANGE, blockposition);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return this.isEmpty() ? super.getCloneItemStack(iblockaccess, blockposition, iblockdata) : new ItemStack(this.content);
    }

    private boolean isEmpty() {
        return this.content == Blocks.AIR;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public Block getContent() {
        return this.content;
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
