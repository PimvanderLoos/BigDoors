package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;

public class BlockDirtSnow extends Block {

    public static final BlockStateBoolean a = BlockProperties.z;

    protected BlockDirtSnow(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockDirtSnow.a, false));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection != EnumDirection.UP ? super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1) : (IBlockData) iblockdata.set(BlockDirtSnow.a, iblockdata1.a(Blocks.SNOW_BLOCK) || iblockdata1.a(Blocks.SNOW));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().up());

        return (IBlockData) this.getBlockData().set(BlockDirtSnow.a, iblockdata.a(Blocks.SNOW_BLOCK) || iblockdata.a(Blocks.SNOW));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockDirtSnow.a);
    }
}
