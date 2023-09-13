package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockRepeater extends BlockDiodeAbstract {

    public static final BlockStateBoolean LOCKED = BlockProperties.s;
    public static final BlockStateInteger DELAY = BlockProperties.am;

    protected BlockRepeater(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRepeater.FACING, EnumDirection.NORTH)).set(BlockRepeater.DELAY, 1)).set(BlockRepeater.LOCKED, false)).set(BlockRepeater.c, false));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (!entityhuman.abilities.mayBuild) {
            return EnumInteractionResult.PASS;
        } else {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockRepeater.DELAY), 3);
            return EnumInteractionResult.a(world.isClientSide);
        }
    }

    @Override
    protected int g(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockRepeater.DELAY) * 2;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getPlacedState(blockactioncontext);

        return (IBlockData) iblockdata.set(BlockRepeater.LOCKED, this.a((IWorldReader) blockactioncontext.getWorld(), blockactioncontext.getClickPosition(), iblockdata));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return !generatoraccess.s_() && enumdirection.n() != ((EnumDirection) iblockdata.get(BlockRepeater.FACING)).n() ? (IBlockData) iblockdata.set(BlockRepeater.LOCKED, this.a((IWorldReader) generatoraccess, blockposition, iblockdata)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        return this.b(iworldreader, blockposition, iblockdata) > 0;
    }

    @Override
    protected boolean h(IBlockData iblockdata) {
        return isDiode(iblockdata);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRepeater.FACING, BlockRepeater.DELAY, BlockRepeater.LOCKED, BlockRepeater.c);
    }
}
