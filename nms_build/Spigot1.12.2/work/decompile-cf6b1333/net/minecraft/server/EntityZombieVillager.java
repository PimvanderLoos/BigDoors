package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public class EntityZombieVillager extends EntityZombie {

    private static final DataWatcherObject<Boolean> b = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.b);
    private int conversionTime;
    private UUID by;

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
        if (this.by != null) {
            nbttagcompound.a("ConversionPlayer", this.by);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setProfession(nbttagcompound.getInt("Profession"));
        if (nbttagcompound.hasKeyOfType("ConversionTime", 99) && nbttagcompound.getInt("ConversionTime") > -1) {
            this.a(nbttagcompound.b("ConversionPlayer") ? nbttagcompound.a("ConversionPlayer") : null, nbttagcompound.getInt("ConversionTime"));
        }

    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        this.setProfession(this.world.random.nextInt(6));
        return super.prepare(difficultydamagescaler, groupdataentity);
    }

    public void B_() {
        if (!this.world.isClientSide && this.isConverting()) {
            int i = this.du();

            this.conversionTime -= i;
            if (this.conversionTime <= 0) {
                this.dt();
            }
        }

        super.B_();
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.GOLDEN_APPLE && itemstack.getData() == 0 && this.hasEffect(MobEffects.WEAKNESS)) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.world.isClientSide) {
                this.a(entityhuman.getUniqueID(), this.random.nextInt(2401) + 3600);
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

    protected void a(@Nullable UUID uuid, int i) {
        this.by = uuid;
        this.conversionTime = i;
        this.getDataWatcher().set(EntityZombieVillager.b, Boolean.valueOf(true));
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffect(MobEffects.INCREASE_DAMAGE, i, Math.min(this.world.getDifficulty().a() - 1, 0)));
        this.world.broadcastEntityEffect(this, (byte) 16);
    }

    protected void dt() {
        EntityVillager entityvillager = new EntityVillager(this.world);

        entityvillager.u(this);
        entityvillager.setProfession(this.getProfession());
        entityvillager.a(this.world.D(new BlockPosition(entityvillager)), (GroupDataEntity) null, false);
        entityvillager.dp();
        if (this.isBaby()) {
            entityvillager.setAgeRaw(-24000);
        }

        this.world.kill(this);
        entityvillager.setNoAI(this.isNoAI());
        if (this.hasCustomName()) {
            entityvillager.setCustomName(this.getCustomName());
            entityvillager.setCustomNameVisible(this.getCustomNameVisible());
        }

        this.world.addEntity(entityvillager);
        if (this.by != null) {
            EntityHuman entityhuman = this.world.b(this.by);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.q.a((EntityPlayer) entityhuman, this, entityvillager);
            }
        }

        entityvillager.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
        this.world.a((EntityHuman) null, 1027, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0);
    }

    protected int du() {
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

    protected float cr() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundEffect F() {
        return SoundEffects.jx;
    }

    public SoundEffect d(DamageSource damagesource) {
        return SoundEffects.jB;
    }

    public SoundEffect cf() {
        return SoundEffects.jA;
    }

    public SoundEffect dm() {
        return SoundEffects.jC;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.as;
    }

    protected ItemStack dn() {
        return ItemStack.a;
    }
}
