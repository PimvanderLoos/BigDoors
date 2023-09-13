package net.minecraft.world.phys.shapes;

import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypeFlowing;

public class VoxelShapeCollisionEntity implements VoxelShapeCollision {

    protected static final VoxelShapeCollision a = new VoxelShapeCollisionEntity(false, -1.7976931348623157E308D, Items.AIR, (fluidtype) -> {
        return false;
    }) {
        @Override
        public boolean a(VoxelShape voxelshape, BlockPosition blockposition, boolean flag) {
            return flag;
        }
    };
    private final boolean b;
    private final double c;
    private final Item d;
    private final Predicate<FluidType> e;

    protected VoxelShapeCollisionEntity(boolean flag, double d0, Item item, Predicate<FluidType> predicate) {
        this.b = flag;
        this.c = d0;
        this.d = item;
        this.e = predicate;
    }

    @Deprecated
    protected VoxelShapeCollisionEntity(Entity entity) {
        boolean flag = entity.by();
        double d0 = entity.locY();
        Item item = entity instanceof EntityLiving ? ((EntityLiving) entity).getItemInMainHand().getItem() : Items.AIR;
        Predicate predicate;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            ((EntityLiving) entity).getClass();
            predicate = entityliving::a;
        } else {
            predicate = (fluidtype) -> {
                return false;
            };
        }

        this(flag, d0, item, predicate);
    }

    @Override
    public boolean a(Item item) {
        return this.d == item;
    }

    @Override
    public boolean a(Fluid fluid, FluidTypeFlowing fluidtypeflowing) {
        return this.e.test(fluidtypeflowing) && !fluid.getType().a((FluidType) fluidtypeflowing);
    }

    @Override
    public boolean b() {
        return this.b;
    }

    @Override
    public boolean a(VoxelShape voxelshape, BlockPosition blockposition, boolean flag) {
        return this.c > (double) blockposition.getY() + voxelshape.c(EnumDirection.EnumAxis.Y) - 9.999999747378752E-6D;
    }
}
