package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class CaveVinesPlantBlock extends BlockGrowingStem implements IBlockFragilePlantElement, CaveVines {

    public CaveVinesPlantBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.DOWN, CaveVinesPlantBlock.SHAPE, false);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(CaveVinesPlantBlock.BERRIES, false));
    }

    @Override
    protected BlockGrowingTop getHeadBlock() {
        return (BlockGrowingTop) Blocks.CAVE_VINES;
    }

    @Override
    protected IBlockData updateHeadAfterConvertedFromBody(IBlockData iblockdata, IBlockData iblockdata1) {
        return (IBlockData) iblockdata1.setValue(CaveVinesPlantBlock.BERRIES, (Boolean) iblockdata.getValue(CaveVinesPlantBlock.BERRIES));
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.GLOW_BERRIES);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return CaveVines.use(iblockdata, world, blockposition);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(CaveVinesPlantBlock.BERRIES);
    }

    @Override
    public boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return !(Boolean) iblockdata.getValue(CaveVinesPlantBlock.BERRIES);
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(CaveVinesPlantBlock.BERRIES, true), 2);
    }
}
