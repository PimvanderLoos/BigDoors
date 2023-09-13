package net.minecraft.world.level.material;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class FluidTypeEmpty extends FluidType {

    public FluidTypeEmpty() {}

    @Override
    public Item a() {
        return Items.AIR;
    }

    @Override
    public boolean a(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection) {
        return true;
    }

    @Override
    public Vec3D a(IBlockAccess iblockaccess, BlockPosition blockposition, Fluid fluid) {
        return Vec3D.ORIGIN;
    }

    @Override
    public int a(IWorldReader iworldreader) {
        return 0;
    }

    @Override
    protected boolean b() {
        return true;
    }

    @Override
    protected float c() {
        return 0.0F;
    }

    @Override
    public float a(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 0.0F;
    }

    @Override
    public float a(Fluid fluid) {
        return 0.0F;
    }

    @Override
    protected IBlockData b(Fluid fluid) {
        return Blocks.AIR.getBlockData();
    }

    @Override
    public boolean c(Fluid fluid) {
        return false;
    }

    @Override
    public int d(Fluid fluid) {
        return 0;
    }

    @Override
    public VoxelShape b(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }
}
