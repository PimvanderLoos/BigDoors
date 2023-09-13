package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public interface VoxelShapeCollision {

    static VoxelShapeCollision empty() {
        return VoxelShapeCollisionEntity.EMPTY;
    }

    static VoxelShapeCollision of(Entity entity) {
        return new VoxelShapeCollisionEntity(entity);
    }

    boolean isDescending();

    boolean isAbove(VoxelShape voxelshape, BlockPosition blockposition, boolean flag);

    boolean isHoldingItem(Item item);

    boolean canStandOnFluid(Fluid fluid, Fluid fluid1);
}
