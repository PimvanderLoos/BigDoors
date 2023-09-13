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

public class CaveVinesBlock extends BlockGrowingTop implements IBlockFragilePlantElement, CaveVines {

    private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11F;

    public CaveVinesBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.DOWN, CaveVinesBlock.SHAPE, false, 0.1D);
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(CaveVinesBlock.AGE, 0)).set(CaveVinesBlock.BERRIES, false));
    }

    @Override
    protected int a(Random random) {
        return 1;
    }

    @Override
    protected boolean g(IBlockData iblockdata) {
        return iblockdata.isAir();
    }

    @Override
    protected Block c() {
        return Blocks.CAVE_VINES_PLANT;
    }

    @Override
    protected IBlockData a(IBlockData iblockdata, IBlockData iblockdata1) {
        return (IBlockData) iblockdata1.set(CaveVinesBlock.BERRIES, (Boolean) iblockdata.get(CaveVinesBlock.BERRIES));
    }

    @Override
    protected IBlockData a(IBlockData iblockdata, Random random) {
        return (IBlockData) super.a(iblockdata, random).set(CaveVinesBlock.BERRIES, random.nextFloat() < 0.11F);
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
        super.a(blockstatelist_a);
        blockstatelist_a.a(CaveVinesBlock.BERRIES);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return !(Boolean) iblockdata.get(CaveVinesBlock.BERRIES);
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(CaveVinesBlock.BERRIES, true), 2);
    }
}
