package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class CaveVinesBlock extends BlockGrowingTop implements IBlockFragilePlantElement, CaveVines {

    private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11F;

    public CaveVinesBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.DOWN, CaveVinesBlock.SHAPE, false, 0.1D);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(CaveVinesBlock.AGE, 0)).setValue(CaveVinesBlock.BERRIES, false));
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource randomsource) {
        return 1;
    }

    @Override
    protected boolean canGrowInto(IBlockData iblockdata) {
        return iblockdata.isAir();
    }

    @Override
    protected Block getBodyBlock() {
        return Blocks.CAVE_VINES_PLANT;
    }

    @Override
    protected IBlockData updateBodyAfterConvertedFromHead(IBlockData iblockdata, IBlockData iblockdata1) {
        return (IBlockData) iblockdata1.setValue(CaveVinesBlock.BERRIES, (Boolean) iblockdata.getValue(CaveVinesBlock.BERRIES));
    }

    @Override
    protected IBlockData getGrowIntoState(IBlockData iblockdata, RandomSource randomsource) {
        return (IBlockData) super.getGrowIntoState(iblockdata, randomsource).setValue(CaveVinesBlock.BERRIES, randomsource.nextFloat() < 0.11F);
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.GLOW_BERRIES);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return CaveVines.use(entityhuman, iblockdata, world, blockposition);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        super.createBlockStateDefinition(blockstatelist_a);
        blockstatelist_a.add(CaveVinesBlock.BERRIES);
    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return !(Boolean) iblockdata.getValue(CaveVinesBlock.BERRIES);
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(CaveVinesBlock.BERRIES, true), 2);
    }
}
