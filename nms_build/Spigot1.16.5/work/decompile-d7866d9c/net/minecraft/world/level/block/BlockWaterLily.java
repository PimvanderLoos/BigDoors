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

    protected static final VoxelShape a = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    protected BlockWaterLily(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        super.a(iblockdata, world, blockposition, entity);
        if (world instanceof WorldServer && entity instanceof EntityBoat) {
            world.a(new BlockPosition(blockposition), true, entity);
        }

    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockWaterLily.a;
    }

    @Override
    protected boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Fluid fluid = iblockaccess.getFluid(blockposition);
        Fluid fluid1 = iblockaccess.getFluid(blockposition.up());

        return (fluid.getType() == FluidTypes.WATER || iblockdata.getMaterial() == Material.ICE) && fluid1.getType() == FluidTypes.EMPTY;
    }
}
