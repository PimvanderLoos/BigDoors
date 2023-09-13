package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.world.IInventory;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public interface IHopper extends IInventory {

    VoxelShape a = Block.a(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    VoxelShape b = Block.a(0.0D, 16.0D, 0.0D, 16.0D, 32.0D, 16.0D);
    VoxelShape c = VoxelShapes.a(IHopper.a, IHopper.b);

    default VoxelShape aa_() {
        return IHopper.c;
    }

    @Nullable
    World getWorld();

    double x();

    double z();

    double A();
}
