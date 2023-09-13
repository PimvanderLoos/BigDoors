package net.minecraft.world.entity.projectile;

import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public abstract class EntityProjectileThrowable extends EntityProjectile implements ItemSupplier {

    private static final DataWatcherObject<ItemStack> DATA_ITEM_STACK = DataWatcher.defineId(EntityProjectileThrowable.class, DataWatcherRegistry.ITEM_STACK);

    public EntityProjectileThrowable(EntityTypes<? extends EntityProjectileThrowable> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityProjectileThrowable(EntityTypes<? extends EntityProjectileThrowable> entitytypes, double d0, double d1, double d2, World world) {
        super(entitytypes, d0, d1, d2, world);
    }

    public EntityProjectileThrowable(EntityTypes<? extends EntityProjectileThrowable> entitytypes, EntityLiving entityliving, World world) {
        super(entitytypes, entityliving, world);
    }

    public void setItem(ItemStack itemstack) {
        if (!itemstack.is(this.getDefaultItem()) || itemstack.hasTag()) {
            this.getEntityData().set(EntityProjectileThrowable.DATA_ITEM_STACK, (ItemStack) SystemUtils.make(itemstack.copy(), (itemstack1) -> {
                itemstack1.setCount(1);
            }));
        }

    }

    protected abstract Item getDefaultItem();

    public ItemStack getItemRaw() {
        return (ItemStack) this.getEntityData().get(EntityProjectileThrowable.DATA_ITEM_STACK);
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();

        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(EntityProjectileThrowable.DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        ItemStack itemstack = this.getItemRaw();

        if (!itemstack.isEmpty()) {
            nbttagcompound.put("Item", itemstack.save(new NBTTagCompound()));
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        ItemStack itemstack = ItemStack.of(nbttagcompound.getCompound("Item"));

        this.setItem(itemstack);
    }
}
