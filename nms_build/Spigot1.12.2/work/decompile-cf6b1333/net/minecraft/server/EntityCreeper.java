package net.minecraft.server;

import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

public class EntityCreeper extends EntityMonster {

    private static final DataWatcherObject<Integer> a = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> b = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Boolean> c = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.h);
    private int bx;
    private int fuseTicks;
    private int maxFuseTicks = 30;
    private int explosionRadius = 3;
    private int bB;

    public EntityCreeper(World world) {
        super(world);
        this.setSize(0.6F, 1.7F);
    }

    protected void r() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSwell(this));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    public int bg() {
        return this.getGoalTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
    }

    public void e(float f, float f1) {
        super.e(f, f1);
        this.fuseTicks = (int) ((float) this.fuseTicks + f * 1.5F);
        if (this.fuseTicks > this.maxFuseTicks - 5) {
            this.fuseTicks = this.maxFuseTicks - 5;
        }

    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityCreeper.a, Integer.valueOf(-1));
        this.datawatcher.register(EntityCreeper.b, Boolean.valueOf(false));
        this.datawatcher.register(EntityCreeper.c, Boolean.valueOf(false));
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityCreeper.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (((Boolean) this.datawatcher.get(EntityCreeper.b)).booleanValue()) {
            nbttagcompound.setBoolean("powered", true);
        }

        nbttagcompound.setShort("Fuse", (short) this.maxFuseTicks);
        nbttagcompound.setByte("ExplosionRadius", (byte) this.explosionRadius);
        nbttagcompound.setBoolean("ignited", this.isIgnited());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.datawatcher.set(EntityCreeper.b, Boolean.valueOf(nbttagcompound.getBoolean("powered")));
        if (nbttagcompound.hasKeyOfType("Fuse", 99)) {
            this.maxFuseTicks = nbttagcompound.getShort("Fuse");
        }

        if (nbttagcompound.hasKeyOfType("ExplosionRadius", 99)) {
            this.explosionRadius = nbttagcompound.getByte("ExplosionRadius");
        }

        if (nbttagcompound.getBoolean("ignited")) {
            this.do_();
        }

    }

    public void B_() {
        if (this.isAlive()) {
            this.bx = this.fuseTicks;
            if (this.isIgnited()) {
                this.a(1);
            }

            int i = this.dm();

            if (i > 0 && this.fuseTicks == 0) {
                this.a(SoundEffects.ay, 1.0F, 0.5F);
            }

            this.fuseTicks += i;
            if (this.fuseTicks < 0) {
                this.fuseTicks = 0;
            }

            if (this.fuseTicks >= this.maxFuseTicks) {
                this.fuseTicks = this.maxFuseTicks;
                this.dr();
            }
        }

        super.B_();
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ax;
    }

    protected SoundEffect cf() {
        return SoundEffects.aw;
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (this.world.getGameRules().getBoolean("doMobLoot")) {
            if (damagesource.getEntity() instanceof EntitySkeleton) {
                int i = Item.getId(Items.RECORD_13);
                int j = Item.getId(Items.RECORD_WAIT);
                int k = i + this.random.nextInt(j - i + 1);

                this.a(Item.getById(k), 1);
            } else if (damagesource.getEntity() instanceof EntityCreeper && damagesource.getEntity() != this && ((EntityCreeper) damagesource.getEntity()).isPowered() && ((EntityCreeper) damagesource.getEntity()).canCauseHeadDrop()) {
                ((EntityCreeper) damagesource.getEntity()).setCausedHeadDrop();
                this.a(new ItemStack(Items.SKULL, 1, 4), 0.0F);
            }
        }

    }

    public boolean B(Entity entity) {
        return true;
    }

    public boolean isPowered() {
        return ((Boolean) this.datawatcher.get(EntityCreeper.b)).booleanValue();
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.r;
    }

    public int dm() {
        return ((Integer) this.datawatcher.get(EntityCreeper.a)).intValue();
    }

    public void a(int i) {
        this.datawatcher.set(EntityCreeper.a, Integer.valueOf(i));
    }

    public void onLightningStrike(EntityLightning entitylightning) {
        super.onLightningStrike(entitylightning);
        this.datawatcher.set(EntityCreeper.b, Boolean.valueOf(true));
    }

    protected boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
            this.world.a(entityhuman, this.locX, this.locY, this.locZ, SoundEffects.bO, this.bK(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            entityhuman.a(enumhand);
            if (!this.world.isClientSide) {
                this.do_();
                itemstack.damage(1, entityhuman);
                return true;
            }
        }

        return super.a(entityhuman, enumhand);
    }

    private void dr() {
        if (!this.world.isClientSide) {
            boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
            float f = this.isPowered() ? 2.0F : 1.0F;

            this.aU = true;
            this.world.explode(this, this.locX, this.locY, this.locZ, (float) this.explosionRadius * f, flag);
            this.die();
            this.ds();
        }

    }

    private void ds() {
        Collection collection = this.getEffects();

        if (!collection.isEmpty()) {
            EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.locX, this.locY, this.locZ);

            entityareaeffectcloud.setRadius(2.5F);
            entityareaeffectcloud.setRadiusOnUse(-0.5F);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                entityareaeffectcloud.a(new MobEffect(mobeffect));
            }

            this.world.addEntity(entityareaeffectcloud);
        }

    }

    public boolean isIgnited() {
        return ((Boolean) this.datawatcher.get(EntityCreeper.c)).booleanValue();
    }

    public void do_() {
        this.datawatcher.set(EntityCreeper.c, Boolean.valueOf(true));
    }

    public boolean canCauseHeadDrop() {
        return this.bB < 1 && this.world.getGameRules().getBoolean("doMobLoot");
    }

    public void setCausedHeadDrop() {
        ++this.bB;
    }
}
