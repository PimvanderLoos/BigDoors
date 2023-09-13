package net.minecraft.world.phys.shapes;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypeFlowing;

public class VoxelShapeCollisionEntity implements VoxelShapeCollision {

    protected static final VoxelShapeCollision EMPTY = new VoxelShapeCollisionEntity(false, -1.7976931348623157E308D, ItemStack.EMPTY, ItemStack.EMPTY, (fluidtype) -> {
        return false;
    }, Optional.empty()) {
        @Override
        public boolean a(VoxelShape voxelshape, BlockPosition blockposition, boolean flag) {
            return flag;
        }
    };
    private final boolean descending;
    private final double entityBottom;
    private final ItemStack heldItem;
    private final ItemStack footItem;
    private final Predicate<FluidType> canStandOnFluid;
    private final Optional<Entity> entity;

    protected VoxelShapeCollisionEntity(boolean flag, double d0, ItemStack itemstack, ItemStack itemstack1, Predicate<FluidType> predicate, Optional<Entity> optional) {
        this.descending = flag;
        this.entityBottom = d0;
        this.footItem = itemstack;
        this.heldItem = itemstack1;
        this.canStandOnFluid = predicate;
        this.entity = optional;
    }

    @Deprecated
    protected VoxelShapeCollisionEntity(Entity entity) {
        boolean flag = entity.bH();
        double d0 = entity.locY();
        ItemStack itemstack = entity instanceof EntityLiving ? ((EntityLiving) entity).getEquipment(EnumItemSlot.FEET) : ItemStack.EMPTY;
        ItemStack itemstack1 = entity instanceof EntityLiving ? ((EntityLiving) entity).getItemInMainHand() : ItemStack.EMPTY;
        Predicate predicate;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            Objects.requireNonNull((EntityLiving) entity);
            predicate = entityliving::a;
        } else {
            predicate = (fluidtype) -> {
                return false;
            };
        }

        this(flag, d0, itemstack, itemstack1, predicate, Optional.of(entity));
    }

    @Override
    public boolean a(Item item) {
        return this.footItem.a(item);
    }

    @Override
    public boolean b(Item item) {
        return this.heldItem.a(item);
    }

    @Override
    public boolean a(Fluid fluid, FluidTypeFlowing fluidtypeflowing) {
        return this.canStandOnFluid.test(fluidtypeflowing) && !fluid.getType().a((FluidType) fluidtypeflowing);
    }

    @Override
    public boolean b() {
        return this.descending;
    }

    @Override
    public boolean a(VoxelShape voxelshape, BlockPosition blockposition, boolean flag) {
        return this.entityBottom > (double) blockposition.getY() + voxelshape.c(EnumDirection.EnumAxis.Y) - 9.999999747378752E-6D;
    }

    public Optional<Entity> c() {
        return this.entity;
    }
}
