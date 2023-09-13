package net.minecraft.world.level.block.entity;

import net.minecraft.world.IInventory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public interface IHopper extends IInventory {

    VoxelShape INSIDE = Block.a(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    VoxelShape ABOVE = Block.a(0.0D, 16.0D, 0.0D, 16.0D, 32.0D, 16.0D);
    VoxelShape SUCK = VoxelShapes.a(IHopper.INSIDE, IHopper.ABOVE);

    default VoxelShape K_() {
        return IHopper.SUCK;
    }

    double x();

    double z();

    double A();
}
