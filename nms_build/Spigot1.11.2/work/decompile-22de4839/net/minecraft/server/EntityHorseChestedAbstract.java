package net.minecraft.server;

public abstract class EntityHorseChestedAbstract extends EntityHorseAbstract {

    private static final DataWatcherObject<Boolean> bG = DataWatcher.a(EntityHorseChestedAbstract.class, DataWatcherRegistry.h);

    public EntityHorseChestedAbstract(World world) {
        super(world);
        this.bE = false;
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityHorseChestedAbstract.bG, Boolean.valueOf(false));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) this.dH());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.17499999701976776D);
        this.getAttributeInstance(EntityHorseChestedAbstract.attributeJumpStrength).setValue(0.5D);
    }

    public boolean isCarryingChest() {
        return ((Boolean) this.datawatcher.get(EntityHorseChestedAbstract.bG)).booleanValue();
    }

    public void setCarryingChest(boolean flag) {
        this.datawatcher.set(EntityHorseChestedAbstract.bG, Boolean.valueOf(flag));
    }

    protected int di() {
        return this.isCarryingChest() ? 17 : super.di();
    }

    public double ay() {
        return super.ay() - 0.25D;
    }

    protected SoundEffect dj() {
        super.dj();
        return SoundEffects.aA;
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (this.isCarryingChest()) {
            if (!this.world.isClientSide) {
                this.a(Item.getItemOf(Blocks.CHEST), 1);
            }

            this.setCarryingChest(false);
        }

    }

    public static void b(DataConverterManager dataconvertermanager, Class<?> oclass) {
        EntityHorseAbstract.c(dataconvertermanager, oclass);
        dataconvertermanager.a(DataConverterTypes.ENTITY, (DataInspector) (new DataInspectorItemList(oclass, new String[] { "Items"})));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("ChestedHorse", this.isCarryingChest());
        if (this.isCarryingChest()) {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 2; i < this.inventoryChest.getSize(); ++i) {
                ItemStack itemstack = this.inventoryChest.getItem(i);

                if (!itemstack.isEmpty()) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    nbttagcompound1.setByte("Slot", (byte) i);
                    itemstack.save(nbttagcompound1);
                    nbttaglist.add(nbttagcompound1);
                }
            }

            nbttagcompound.set("Items", nbttaglist);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setCarryingChest(nbttagcompound.getBoolean("ChestedHorse"));
        if (this.isCarryingChest()) {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

            this.dx();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
                int j = nbttagcompound1.getByte("Slot") & 255;

                if (j >= 2 && j < this.inventoryChest.getSize()) {
                    this.inventoryChest.setItem(j, new ItemStack(nbttagcompound1));
                }
            }
        }

        this.dy();
    }

    public boolean c(int i, ItemStack itemstack) {
        if (i == 499) {
            if (this.isCarryingChest() && itemstack.isEmpty()) {
                this.setCarryingChest(false);
                this.dx();
                return true;
            }

            if (!this.isCarryingChest() && itemstack.getItem() == Item.getItemOf(Blocks.CHEST)) {
                this.setCarryingChest(true);
                this.dx();
                return true;
            }
        }

        return super.c(i, itemstack);
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.SPAWN_EGG) {
            return super.a(entityhuman, enumhand);
        } else {
            if (!this.isBaby()) {
                if (this.isTamed() && entityhuman.isSneaking()) {
                    this.f(entityhuman);
                    return true;
                }

                if (this.isVehicle()) {
                    return super.a(entityhuman, enumhand);
                }
            }

            if (!itemstack.isEmpty()) {
                boolean flag = this.b(entityhuman, itemstack);

                if (!flag && !this.isTamed()) {
                    if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                        return true;
                    }

                    this.dF();
                    return true;
                }

                if (!flag && !this.isCarryingChest() && itemstack.getItem() == Item.getItemOf(Blocks.CHEST)) {
                    this.setCarryingChest(true);
                    this.dk();
                    flag = true;
                    this.dx();
                }

                if (!flag && !this.isBaby() && !this.dB() && itemstack.getItem() == Items.SADDLE) {
                    this.f(entityhuman);
                    return true;
                }

                if (flag) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    return true;
                }
            }

            if (this.isBaby()) {
                return super.a(entityhuman, enumhand);
            } else if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                return true;
            } else {
                this.g(entityhuman);
                return true;
            }
        }
    }

    protected void dk() {
        this.a(SoundEffects.aB, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public int dl() {
        return 5;
    }
}
