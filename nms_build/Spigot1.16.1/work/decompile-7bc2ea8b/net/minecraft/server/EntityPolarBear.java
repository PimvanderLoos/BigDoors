package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPolarBear extends EntityAnimal implements IEntityAngerable {

    private static final DataWatcherObject<Boolean> bv = DataWatcher.a(EntityPolarBear.class, DataWatcherRegistry.i);
    private float bw;
    private float bx;
    private int by;
    private static final IntRange bz = TimeRange.a(20, 39);
    private int bA;
    private UUID bB;

    public EntityPolarBear(EntityTypes<? extends EntityPolarBear> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.POLAR_BEAR.a(this.world);
    }

    @Override
    public boolean k(ItemStack itemstack) {
        return false;
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityPolarBear.c());
        this.goalSelector.a(1, new EntityPolarBear.d());
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new EntityPolarBear.b());
        this.targetSelector.a(2, new EntityPolarBear.a());
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::b));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityFox.class, 10, true, true, (Predicate) null));
        this.targetSelector.a(5, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    public static AttributeProvider.Builder eL() {
        return EntityInsentient.p().a(GenericAttributes.MAX_HEALTH, 30.0D).a(GenericAttributes.FOLLOW_RANGE, 20.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D).a(GenericAttributes.ATTACK_DAMAGE, 6.0D);
    }

    public static boolean c(EntityTypes<EntityPolarBear> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BiomeBase biomebase = generatoraccess.getBiome(blockposition);

        return biomebase != Biomes.FROZEN_OCEAN && biomebase != Biomes.DEEP_FROZEN_OCEAN ? b(entitytypes, generatoraccess, enummobspawn, blockposition, random) : generatoraccess.getLightLevel(blockposition, 0) > 8 && generatoraccess.getType(blockposition.down()).a(Blocks.ICE);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.a((WorldServer) this.world, nbttagcompound);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.c(nbttagcompound);
    }

    @Override
    public void anger() {
        this.setAnger(EntityPolarBear.bz.a(this.random));
    }

    @Override
    public void setAnger(int i) {
        this.bA = i;
    }

    @Override
    public int getAnger() {
        return this.bA;
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.bB = uuid;
    }

    @Override
    public UUID getAngerTarget() {
        return this.bB;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isBaby() ? SoundEffects.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEffects.ENTITY_POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_POLAR_BEAR_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void eM() {
        if (this.by <= 0) {
            this.playSound(SoundEffects.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.dG());
            this.by = 40;
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPolarBear.bv, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClientSide) {
            if (this.bx != this.bw) {
                this.updateSize();
            }

            this.bw = this.bx;
            if (this.eN()) {
                this.bx = MathHelper.a(this.bx + 1.0F, 0.0F, 6.0F);
            } else {
                this.bx = MathHelper.a(this.bx - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.by > 0) {
            --this.by;
        }

        if (!this.world.isClientSide) {
            this.a((WorldServer) this.world, true);
        }

    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        if (this.bx > 0.0F) {
            float f = this.bx / 6.0F;
            float f1 = 1.0F + f;

            return super.a(entitypose).a(1.0F, f1);
        } else {
            return super.a(entitypose);
        }
    }

    @Override
    public boolean attackEntity(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.b(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public boolean eN() {
        return (Boolean) this.datawatcher.get(EntityPolarBear.bv);
    }

    public void t(boolean flag) {
        this.datawatcher.set(EntityPolarBear.bv, flag);
    }

    @Override
    protected float dL() {
        return 0.98F;
    }

    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a();
            ((EntityAgeable.a) groupdataentity).a(1.0F);
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    class d extends PathfinderGoalPanic {

        public d() {
            super(EntityPolarBear.this, 2.0D);
        }

        @Override
        public boolean a() {
            return !EntityPolarBear.this.isBaby() && !EntityPolarBear.this.isBurning() ? false : super.a();
        }
    }

    class c extends PathfinderGoalMeleeAttack {

        public c() {
            super(EntityPolarBear.this, 1.25D, true);
        }

        @Override
        protected void a(EntityLiving entityliving, double d0) {
            double d1 = this.a(entityliving);

            if (d0 <= d1 && this.h()) {
                this.g();
                this.a.attackEntity(entityliving);
                EntityPolarBear.this.t(false);
            } else if (d0 <= d1 * 2.0D) {
                if (this.h()) {
                    EntityPolarBear.this.t(false);
                    this.g();
                }

                if (this.j() <= 10) {
                    EntityPolarBear.this.t(true);
                    EntityPolarBear.this.eM();
                }
            } else {
                this.g();
                EntityPolarBear.this.t(false);
            }

        }

        @Override
        public void d() {
            EntityPolarBear.this.t(false);
            super.d();
        }

        @Override
        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.getWidth());
        }
    }

    class a extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public a() {
            super(EntityPolarBear.this, EntityHuman.class, 20, true, true, (Predicate) null);
        }

        @Override
        public boolean a() {
            if (EntityPolarBear.this.isBaby()) {
                return false;
            } else {
                if (super.a()) {
                    List<EntityPolarBear> list = EntityPolarBear.this.world.a(EntityPolarBear.class, EntityPolarBear.this.getBoundingBox().grow(8.0D, 4.0D, 8.0D));
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityPolarBear entitypolarbear = (EntityPolarBear) iterator.next();

                        if (entitypolarbear.isBaby()) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        @Override
        protected double k() {
            return super.k() * 0.5D;
        }
    }

    class b extends PathfinderGoalHurtByTarget {

        public b() {
            super(EntityPolarBear.this);
        }

        @Override
        public void c() {
            super.c();
            if (EntityPolarBear.this.isBaby()) {
                this.g();
                this.d();
            }

        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityPolarBear && !entityinsentient.isBaby()) {
                super.a(entityinsentient, entityliving);
            }

        }
    }
}
