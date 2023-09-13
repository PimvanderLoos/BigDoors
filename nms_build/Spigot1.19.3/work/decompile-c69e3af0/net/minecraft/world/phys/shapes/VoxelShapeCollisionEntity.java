package net.minecraft.world.phys.shapes;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class VoxelShapeCollisionEntity implements VoxelShapeCollision {

    protected static final VoxelShapeCollision EMPTY = new VoxelShapeCollisionEntity(false, -1.7976931348623157E308D, ItemStack.EMPTY, (fluid) -> {
        return false;
    }, (Entity) null) {
        @Override
        public boolean isAbove(VoxelShape voxelshape, BlockPosition blockposition, boolean flag) {
            return flag;
        }
    };
    private final boolean descending;
    private final double entityBottom;
    private final ItemStack heldItem;
    private final Predicate<Fluid> canStandOnFluid;
    @Nullable
    private final Entity entity;

    protected VoxelShapeCollisionEntity(boolean flag, double d0, ItemStack itemstack, Predicate<Fluid> predicate, @Nullable Entity entity) {
        this.descending = flag;
        this.entityBottom = d0;
        this.heldItem = itemstack;
        this.canStandOnFluid = predicate;
        this.entity = entity;
    }

    /** @deprecated */
    @Deprecated
    protected VoxelShapeCollisionEntity(Entity entity) {
        boolean flag = entity.isDescending();
        double d0 = entity.getY();
        ItemStack itemstack = entity instanceof EntityLiving ? ((EntityLiving) entity).getMainHandItem() : ItemStack.EMPTY;
        Predicate predicate;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            Objects.requireNonNull((EntityLiving) entity);
            predicate = entityliving::canStandOnFluid;
        } else {
            predicate = (fluid) -> {
                return false;
            };
        }

        this(flag, d0, itemstack, predicate, entity);
    }

    @Override
    public boolean isHoldingItem(Item item) {
        return this.heldItem.is(item);
    }

    @Override
    public boolean canStandOnFluid(Fluid fluid, Fluid fluid1) {
        return this.canStandOnFluid.test(fluid1) && !fluid.getType().isSame(fluid1.getType());
    }

    @Override
    public boolean isDescending() {
        return this.descending;
    }

    @Override
    public boolean isAbove(VoxelShape voxelshape, BlockPosition blockposition, boolean flag) {
        return this.entityBottom > (double) blockposition.getY() + voxelshape.max(EnumDirection.EnumAxis.Y) - 9.999999747378752E-6D;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }
}
