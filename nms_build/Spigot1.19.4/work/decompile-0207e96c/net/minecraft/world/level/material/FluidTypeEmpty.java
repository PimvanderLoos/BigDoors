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
    public Item getBucket() {
        return Items.AIR;
    }

    @Override
    public boolean canBeReplacedWith(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection) {
        return true;
    }

    @Override
    public Vec3D getFlow(IBlockAccess iblockaccess, BlockPosition blockposition, Fluid fluid) {
        return Vec3D.ZERO;
    }

    @Override
    public int getTickDelay(IWorldReader iworldreader) {
        return 0;
    }

    @Override
    protected boolean isEmpty() {
        return true;
    }

    @Override
    protected float getExplosionResistance() {
        return 0.0F;
    }

    @Override
    public float getHeight(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 0.0F;
    }

    @Override
    public float getOwnHeight(Fluid fluid) {
        return 0.0F;
    }

    @Override
    protected IBlockData createLegacyBlock(Fluid fluid) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(Fluid fluid) {
        return false;
    }

    @Override
    public int getAmount(Fluid fluid) {
        return 0;
    }

    @Override
    public VoxelShape getShape(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.empty();
    }
}
