package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityItemFrame extends EntityHanging {

    private static final DataWatcherObject<ItemStack> c = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.f);
    private static final DataWatcherObject<Integer> d = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.b);
    private float e = 1.0F;

    public EntityItemFrame(World world) {
        super(world);
    }

    public EntityItemFrame(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        super(world, blockposition);
        this.setDirection(enumdirection);
    }

    protected void i() {
        this.getDataWatcher().register(EntityItemFrame.c, ItemStack.a);
        this.getDataWatcher().register(EntityItemFrame.d, Integer.valueOf(0));
    }

    public float aI() {
        return 0.0F;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!damagesource.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.world.isClientSide) {
                this.b(damagesource.getEntity(), false);
                this.a(SoundEffects.du, 1.0F, 1.0F);
                this.setItem(ItemStack.a);
            }

            return true;
        } else {
            return super.damageEntity(damagesource, f);
        }
    }

    public int getWidth() {
        return 12;
    }

    public int getHeight() {
        return 12;
    }

    public void a(@Nullable Entity entity) {
        this.a(SoundEffects.ds, 1.0F, 1.0F);
        this.b(entity, true);
    }

    public void p() {
        this.a(SoundEffects.dt, 1.0F, 1.0F);
    }

    public void b(@Nullable Entity entity, boolean flag) {
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            ItemStack itemstack = this.getItem();

            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                if (entityhuman.abilities.canInstantlyBuild) {
                    this.b(itemstack);
                    return;
                }
            }

            if (flag) {
                this.a(new ItemStack(Items.ITEM_FRAME), 0.0F);
            }

            if (!itemstack.isEmpty() && this.random.nextFloat() < this.e) {
                itemstack = itemstack.cloneItemStack();
                this.b(itemstack);
                this.a(itemstack, 0.0F);
            }

        }
    }

    private void b(ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Items.FILLED_MAP) {
                WorldMap worldmap = ((ItemWorldMap) itemstack.getItem()).getSavedMap(itemstack, this.world);

                worldmap.decorations.remove("frame-" + this.getId());
            }

            itemstack.a((EntityItemFrame) null);
        }
    }

    public ItemStack getItem() {
        return (ItemStack) this.getDataWatcher().get(EntityItemFrame.c);
    }

    public void setItem(ItemStack itemstack) {
        this.setItem(itemstack, true);
    }

    private void setItem(ItemStack itemstack, boolean flag) {
        if (!itemstack.isEmpty()) {
            itemstack = itemstack.cloneItemStack();
            itemstack.setCount(1);
            itemstack.a(this);
        }

        this.getDataWatcher().set(EntityItemFrame.c, itemstack);
        this.getDataWatcher().markDirty(EntityItemFrame.c);
        if (!itemstack.isEmpty()) {
            this.a(SoundEffects.dr, 1.0F, 1.0F);
        }

        if (flag && this.blockPosition != null) {
            this.world.updateAdjacentComparators(this.blockPosition, Blocks.AIR);
        }

    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (datawatcherobject.equals(EntityItemFrame.c)) {
            ItemStack itemstack = this.getItem();

            if (!itemstack.isEmpty() && itemstack.A() != this) {
                itemstack.a(this);
            }
        }

    }

    public int getRotation() {
        return ((Integer) this.getDataWatcher().get(EntityItemFrame.d)).intValue();
    }

    public void setRotation(int i) {
        this.setRotation(i, true);
    }

    private void setRotation(int i, boolean flag) {
        this.getDataWatcher().set(EntityItemFrame.d, Integer.valueOf(i % 8));
        if (flag && this.blockPosition != null) {
            this.world.updateAdjacentComparators(this.blockPosition, Blocks.AIR);
        }

    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.ENTITY, (DataInspector) (new DataInspectorItem(EntityItemFrame.class, new String[] { "Item"})));
    }

    public void b(NBTTagCompound nbttagcompound) {
        if (!this.getItem().isEmpty()) {
            nbttagcompound.set("Item", this.getItem().save(new NBTTagCompound()));
            nbttagcompound.setByte("ItemRotation", (byte) this.getRotation());
            nbttagcompound.setFloat("ItemDropChance", this.e);
        }

        super.b(nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.isEmpty()) {
            this.setItem(new ItemStack(nbttagcompound1), false);
            this.setRotation(nbttagcompound.getByte("ItemRotation"), false);
            if (nbttagcompound.hasKeyOfType("ItemDropChance", 99)) {
                this.e = nbttagcompound.getFloat("ItemDropChance");
            }
        }

        super.a(nbttagcompound);
    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.world.isClientSide) {
            if (this.getItem().isEmpty()) {
                if (!itemstack.isEmpty()) {
                    this.setItem(itemstack);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }
                }
            } else {
                this.a(SoundEffects.dv, 1.0F, 1.0F);
                this.setRotation(this.getRotation() + 1);
            }
        }

        return true;
    }

    public int t() {
        return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
    }
}
