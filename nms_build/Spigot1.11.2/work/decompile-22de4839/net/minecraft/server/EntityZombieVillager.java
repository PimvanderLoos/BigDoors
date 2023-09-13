package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityZombieVillager extends EntityZombie {

    private static final DataWatcherObject<Boolean> b = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.b);
    private int conversionTime;

    public EntityZombieVillager(World world) {
        super(world);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityZombieVillager.b, Boolean.valueOf(false));
        this.datawatcher.register(EntityZombieVillager.c, Integer.valueOf(0));
    }

    public void setProfession(int i) {
        this.datawatcher.set(EntityZombieVillager.c, Integer.valueOf(i));
    }

    public int getProfession() {
        return Math.max(((Integer) this.datawatcher.get(EntityZombieVillager.c)).intValue() % 6, 0);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityZombieVillager.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Profession", this.getProfession());
        nbttagcompound.setInt("ConversionTime", this.isConverting() ? this.conversionTime : -1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setProfession(nbttagcompound.getInt("Profession"));
        if (nbttagcompound.hasKeyOfType("ConversionTime", 99) && nbttagcompound.getInt("ConversionTime") > -1) {
            this.b(nbttagcompound.getInt("ConversionTime"));
        }

    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        this.setProfession(this.world.random.nextInt(6));
        return super.prepare(difficultydamagescaler, groupdataentity);
    }

    public void A_() {
        if (!this.world.isClientSide && this.isConverting()) {
            int i = this.dq();

            this.conversionTime -= i;
            if (this.conversionTime <= 0) {
                this.dp();
            }
        }

        super.A_();
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.GOLDEN_APPLE && itemstack.getData() == 0 && this.hasEffect(MobEffects.WEAKNESS)) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.world.isClientSide) {
                this.b(this.random.nextInt(2401) + 3600);
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean isTypeNotPersistent() {
        return !this.isConverting();
    }

    public boolean isConverting() {
        return ((Boolean) this.getDataWatcher().get(EntityZombieVillager.b)).booleanValue();
    }

    protected void b(int i) {
        this.conversionTime = i;
        this.getDataWatcher().set(EntityZombieVillager.b, Boolean.valueOf(true));
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffect(MobEffects.INCREASE_DAMAGE, i, Math.min(this.world.getDifficulty().a() - 1, 0)));
        this.world.broadcastEntityEffect(this, (byte) 16);
    }

    protected void dp() {
        EntityVillager entityvillager = new EntityVillager(this.world);

        entityvillager.u(this);
        entityvillager.setProfession(this.getProfession());
        entityvillager.a(this.world.D(new BlockPosition(entityvillager)), (GroupDataEntity) null, false);
        entityvillager.dl();
        if (this.isBaby()) {
            entityvillager.setAgeRaw(-24000);
        }

        this.world.kill(this);
        entityvillager.setAI(this.hasAI());
        if (this.hasCustomName()) {
            entityvillager.setCustomName(this.getCustomName());
            entityvillager.setCustomNameVisible(this.getCustomNameVisible());
        }

        this.world.addEntity(entityvillager);
        entityvillager.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
        this.world.a((EntityHuman) null, 1027, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0);
    }

    protected int dq() {
        int i = 1;

        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = (int) this.locX - 4; k < (int) this.locX + 4 && j < 14; ++k) {
                for (int l = (int) this.locY - 4; l < (int) this.locY + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.locZ - 4; i1 < (int) this.locZ + 4 && j < 14; ++i1) {
                        Block block = this.world.getType(blockposition_mutableblockposition.c(k, l, i1)).getBlock();

                        if (block == Blocks.IRON_BARS || block == Blocks.BED) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    protected float cj() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundEffect G() {
        return SoundEffects.it;
    }

    public SoundEffect bW() {
        return SoundEffects.ix;
    }

    public SoundEffect bX() {
        return SoundEffects.iw;
    }

    public SoundEffect di() {
        return SoundEffects.iy;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.as;
    }

    protected ItemStack dj() {
        return ItemStack.a;
    }
}
