package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockCake extends Block {

    public static final int MAX_BITES = 6;
    public static final BlockStateInteger BITES = BlockProperties.BITES;
    public static final int FULL_CAKE_SIGNAL = getOutputSignal(0);
    protected static final float AABB_OFFSET = 1.0F;
    protected static final float AABB_SIZE_PER_BITE = 2.0F;
    protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[]{Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};

    protected BlockCake(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockCake.BITES, 0));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockCake.SHAPE_BY_BITE[(Integer) iblockdata.getValue(BlockCake.BITES)];
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        Item item = itemstack.getItem();

        if (itemstack.is((Tag) TagsItem.CANDLES) && (Integer) iblockdata.getValue(BlockCake.BITES) == 0) {
            Block block = Block.byItem(item);

            if (block instanceof CandleBlock) {
                if (!entityhuman.isCreative()) {
                    itemstack.shrink(1);
                }

                world.playSound((EntityHuman) null, blockposition, SoundEffects.CAKE_ADD_CANDLE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlockAndUpdate(blockposition, CandleCakeBlock.byCandle(block));
                world.gameEvent(entityhuman, GameEvent.BLOCK_CHANGE, blockposition);
                entityhuman.awardStat(StatisticList.ITEM_USED.get(item));
                return EnumInteractionResult.SUCCESS;
            }
        }

        if (world.isClientSide) {
            if (eat(world, blockposition, iblockdata, entityhuman).consumesAction()) {
                return EnumInteractionResult.SUCCESS;
            }

            if (itemstack.isEmpty()) {
                return EnumInteractionResult.CONSUME;
            }
        }

        return eat(world, blockposition, iblockdata, entityhuman);
    }

    protected static EnumInteractionResult eat(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!entityhuman.canEat(false)) {
            return EnumInteractionResult.PASS;
        } else {
            entityhuman.awardStat(StatisticList.EAT_CAKE_SLICE);
            entityhuman.getFoodData().eat(2, 0.1F);
            int i = (Integer) iblockdata.getValue(BlockCake.BITES);

            generatoraccess.gameEvent(entityhuman, GameEvent.EAT, blockposition);
            if (i < 6) {
                generatoraccess.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockCake.BITES, i + 1), 3);
            } else {
                generatoraccess.removeBlock(blockposition, false);
                generatoraccess.gameEvent(entityhuman, GameEvent.BLOCK_DESTROY, blockposition);
            }

            return EnumInteractionResult.SUCCESS;
        }
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getBlockState(blockposition.below()).getMaterial().isSolid();
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockCake.BITES);
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return getOutputSignal((Integer) iblockdata.getValue(BlockCake.BITES));
    }

    public static int getOutputSignal(int i) {
        return (7 - i) * 2;
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
