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
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(CaveVinesPlantBlock.BERRIES, false));
    }

    @Override
    protected BlockGrowingTop d() {
        return (BlockGrowingTop) Blocks.CAVE_VINES;
    }

    @Override
    protected IBlockData a(IBlockData iblockdata, IBlockData iblockdata1) {
        return (IBlockData) iblockdata1.set(CaveVinesPlantBlock.BERRIES, (Boolean) iblockdata.get(CaveVinesPlantBlock.BERRIES));
    }

    @Override
    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.GLOW_BERRIES);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return CaveVines.harvest(iblockdata, world, blockposition);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(CaveVinesPlantBlock.BERRIES);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return !(Boolean) iblockdata.get(CaveVinesPlantBlock.BERRIES);
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(CaveVinesPlantBlock.BERRIES, true), 2);
    }
}
