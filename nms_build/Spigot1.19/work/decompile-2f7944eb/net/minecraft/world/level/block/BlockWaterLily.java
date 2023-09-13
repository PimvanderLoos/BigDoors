package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockWaterLily extends BlockPlant {

    protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    protected BlockWaterLily(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        super.entityInside(iblockdata, world, blockposition, entity);
        if (world instanceof WorldServer && entity instanceof EntityBoat) {
            world.destroyBlock(new BlockPosition(blockposition), true, entity);
        }

    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockWaterLily.AABB;
    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Fluid fluid = iblockaccess.getFluidState(blockposition);
        Fluid fluid1 = iblockaccess.getFluidState(blockposition.above());

        return (fluid.getType() == FluidTypes.WATER || iblockdata.getMaterial() == Material.ICE) && fluid1.getType() == FluidTypes.EMPTY;
    }
}
