package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypeFlowing;

public interface VoxelShapeCollision {

    static VoxelShapeCollision a() {
        return VoxelShapeCollisionEntity.a;
    }

    static VoxelShapeCollision a(Entity entity) {
        return new VoxelShapeCollisionEntity(entity);
    }

    boolean b();

    boolean a(VoxelShape voxelshape, BlockPosition blockposition, boolean flag);

    boolean a(Item item);

    boolean a(Fluid fluid, FluidTypeFlowing fluidtypeflowing);
}
