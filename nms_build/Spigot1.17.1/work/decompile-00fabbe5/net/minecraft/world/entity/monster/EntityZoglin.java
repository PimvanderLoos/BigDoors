package net.minecraft.world.entity.monster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.behavior.BehaviorAttack;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetForget;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetSet;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorRunIf;
import net.minecraft.world.entity.ai.behavior.BehaviorRunSometimes;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.behavior.BehaviorWalkAwayOutOfRange;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.hoglin.IOglin;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityZoglin extends EntityMonster implements IMonster, IOglin {

    private static final DataWatcherObject<Boolean> DATA_BABY_ID = DataWatcher.a(EntityZoglin.class, DataWatcherRegistry.BOOLEAN);
    private static final int MAX_HEALTH = 40;
    private static final int ATTACK_KNOCKBACK = 1;
    private static final float KNOCKBACK_RESISTANCE = 0.6F;
    private static final int ATTACK_DAMAGE = 6;
    private static final float BABY_ATTACK_DAMAGE = 0.5F;
    private static final int ATTACK_INTERVAL = 40;
    private static final int BABY_ATTACK_INTERVAL = 15;
    private static final int ATTACK_DURATION = 200;
    private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.4F;
    private int attackAnimationRemainingTicks;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super EntityZoglin>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);

    public EntityZoglin(EntityTypes<? extends EntityZoglin> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 5;
    }

    @Override
    protected BehaviorController.b<EntityZoglin> dp() {
        return BehaviorController.a((Collection) EntityZoglin.MEMORY_TYPES, (Collection) EntityZoglin.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        BehaviorController<EntityZoglin> behaviorcontroller = this.dp().a(dynamic);

        a(behaviorcontroller);
        b(behaviorcontroller);
        c(behaviorcontroller);
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.e();
        return behaviorcontroller;
    }

    private static void a(BehaviorController<EntityZoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.CORE, 0, ImmutableList.of(new BehaviorLook(45, 90), new BehavorMove()));
    }

    private static void b(BehaviorController<EntityZoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.IDLE, 10, ImmutableList.of(new BehaviorAttackTargetSet<>(EntityZoglin::fy), new BehaviorRunSometimes<>(new BehaviorLookTarget(8.0F), UniformInt.a(30, 60)), new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(0.4F), 2), Pair.of(new BehaviorLookWalk(0.4F, 3), 2), Pair.of(new BehaviorNop(30, 60), 1)))));
    }

    private static void c(BehaviorController<EntityZoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.FIGHT, 10, ImmutableList.of(new BehaviorWalkAwayOutOfRange(1.0F), new BehaviorRunIf<>(EntityZoglin::p, new BehaviorAttack(40)), new BehaviorRunIf<>(EntityZoglin::isBaby, new BehaviorAttack(15)), new BehaviorAttackTargetForget<>()), MemoryModuleType.ATTACK_TARGET);
    }

    private Optional<? extends EntityLiving> fy() {
        return ((List) this.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(ImmutableList.of())).stream().filter(this::j).findFirst();
    }

    private boolean j(EntityLiving entityliving) {
        EntityTypes<?> entitytypes = entityliving.getEntityType();

        return entitytypes != EntityTypes.ZOGLIN && entitytypes != EntityTypes.CREEPER && Sensor.c(this, entityliving);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityZoglin.DATA_BABY_ID, false);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityZoglin.DATA_BABY_ID.equals(datawatcherobject)) {
            this.updateSize();
        }

    }

    public static AttributeProvider.Builder n() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 40.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.KNOCKBACK_RESISTANCE, 0.6000000238418579D).a(GenericAttributes.ATTACK_KNOCKBACK, 1.0D).a(GenericAttributes.ATTACK_DAMAGE, 6.0D);
    }

    public boolean p() {
        return !this.isBaby();
    }

    @Override
    public boolean attackEntity(Entity entity) {
        if (!(entity instanceof EntityLiving)) {
            return false;
        } else {
            this.attackAnimationRemainingTicks = 10;
            this.level.broadcastEntityEffect(this, (byte) 4);
            this.playSound(SoundEffects.ZOGLIN_ATTACK, 1.0F, this.ep());
            return IOglin.a(this, (EntityLiving) entity);
        }
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    @Override
    protected void e(EntityLiving entityliving) {
        if (!this.isBaby()) {
            IOglin.b(this, entityliving);
        }

    }

    @Override
    public double bl() {
        return (double) this.getHeight() - (this.isBaby() ? 0.2D : 0.15D);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);

        if (this.level.isClientSide) {
            return false;
        } else if (flag && damagesource.getEntity() instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) damagesource.getEntity();

            if (this.c(entityliving) && !BehaviorUtil.a(this, entityliving, 4.0D)) {
                this.k(entityliving);
            }

            return flag;
        } else {
            return flag;
        }
    }

    private void k(EntityLiving entityliving) {
        this.brain.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        this.brain.a(MemoryModuleType.ATTACK_TARGET, entityliving, 200L);
    }

    @Override
    public BehaviorController<EntityZoglin> getBehaviorController() {
        return super.getBehaviorController();
    }

    protected void t() {
        Activity activity = (Activity) this.brain.f().orElse((Object) null);

        this.brain.a((List) ImmutableList.of(Activity.FIGHT, Activity.IDLE));
        Activity activity1 = (Activity) this.brain.f().orElse((Object) null);

        if (activity1 == Activity.FIGHT && activity != Activity.FIGHT) {
            this.fx();
        }

        this.setAggressive(this.brain.hasMemory(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    protected void mobTick() {
        this.level.getMethodProfiler().enter("zoglinBrain");
        this.getBehaviorController().a((WorldServer) this.level, (EntityLiving) this);
        this.level.getMethodProfiler().exit();
        this.t();
    }

    @Override
    public void setBaby(boolean flag) {
        this.getDataWatcher().set(EntityZoglin.DATA_BABY_ID, flag);
        if (!this.level.isClientSide && flag) {
            this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(0.5D);
        }

    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityZoglin.DATA_BABY_ID);
    }

    @Override
    public void movementTick() {
        if (this.attackAnimationRemainingTicks > 0) {
            --this.attackAnimationRemainingTicks;
        }

        super.movementTick();
    }

    @Override
    public void a(byte b0) {
        if (b0 == 4) {
            this.attackAnimationRemainingTicks = 10;
            this.playSound(SoundEffects.ZOGLIN_ATTACK, 1.0F, this.ep());
        } else {
            super.a(b0);
        }

    }

    @Override
    public int fw() {
        return this.attackAnimationRemainingTicks;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.level.isClientSide ? null : (this.brain.hasMemory(MemoryModuleType.ATTACK_TARGET) ? SoundEffects.ZOGLIN_ANGRY : SoundEffects.ZOGLIN_AMBIENT);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ZOGLIN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ZOGLIN_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ZOGLIN_STEP, 0.15F, 1.0F);
    }

    protected void fx() {
        this.playSound(SoundEffects.ZOGLIN_ANGRY, 1.0F, this.ep());
    }

    @Override
    protected void R() {
        super.R();
        PacketDebug.a((EntityLiving) this);
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.setBoolean("IsBaby", true);
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.getBoolean("IsBaby")) {
            this.setBaby(true);
        }

    }
}
