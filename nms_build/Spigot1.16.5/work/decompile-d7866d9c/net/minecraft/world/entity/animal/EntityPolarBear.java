package net.minecraft.world.entity.animal;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.IntRange;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityPolarBear extends EntityAnimal implements IEntityAngerable {

    private static final DataWatcherObject<Boolean> bo = DataWatcher.a(EntityPolarBear.class, DataWatcherRegistry.i);
    private float bp;
    private float bq;
    private int br;
    private static final IntRange bs = TimeRange.a(20, 39);
    private int bt;
    private UUID bu;

    public EntityPolarBear(EntityTypes<? extends EntityPolarBear> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.POLAR_BEAR.a((World) worldserver);
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
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::a_));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityFox.class, 10, true, true, (Predicate) null));
        this.targetSelector.a(5, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    public static AttributeProvider.Builder eK() {
        return EntityInsentient.p().a(GenericAttributes.MAX_HEALTH, 30.0D).a(GenericAttributes.FOLLOW_RANGE, 20.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D).a(GenericAttributes.ATTACK_DAMAGE, 6.0D);
    }

    public static boolean c(EntityTypes<EntityPolarBear> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        Optional<ResourceKey<BiomeBase>> optional = generatoraccess.i(blockposition);

        return !Objects.equals(optional, Optional.of(Biomes.FROZEN_OCEAN)) && !Objects.equals(optional, Optional.of(Biomes.DEEP_FROZEN_OCEAN)) ? b(entitytypes, generatoraccess, enummobspawn, blockposition, random) : generatoraccess.getLightLevel(blockposition, 0) > 8 && generatoraccess.getType(blockposition.down()).a(Blocks.ICE);
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
        this.setAnger(EntityPolarBear.bs.a(this.random));
    }

    @Override
    public void setAnger(int i) {
        this.bt = i;
    }

    @Override
    public int getAnger() {
        return this.bt;
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.bu = uuid;
    }

    @Override
    public UUID getAngerTarget() {
        return this.bu;
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
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void eL() {
        if (this.br <= 0) {
            this.playSound(SoundEffects.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.dH());
            this.br = 40;
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPolarBear.bo, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClientSide) {
            if (this.bq != this.bp) {
                this.updateSize();
            }

            this.bp = this.bq;
            if (this.eM()) {
                this.bq = MathHelper.a(this.bq + 1.0F, 0.0F, 6.0F);
            } else {
                this.bq = MathHelper.a(this.bq - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.br > 0) {
            --this.br;
        }

        if (!this.world.isClientSide) {
            this.a((WorldServer) this.world, true);
        }

    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        if (this.bq > 0.0F) {
            float f = this.bq / 6.0F;
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

    public boolean eM() {
        return (Boolean) this.datawatcher.get(EntityPolarBear.bo);
    }

    public void t(boolean flag) {
        this.datawatcher.set(EntityPolarBear.bo, flag);
    }

    @Override
    protected float dM() {
        return 0.98F;
    }

    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(1.0F);
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
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
                    EntityPolarBear.this.eL();
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
