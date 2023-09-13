package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockHay extends BlockRotatable {

    public BlockHay(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockHay.AXIS, EnumDirection.EnumAxis.Y));
    }

    @Override
    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        entity.b(f, 0.2F);
    }
}
