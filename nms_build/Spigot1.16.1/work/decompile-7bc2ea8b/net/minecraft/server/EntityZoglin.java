package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EntityZoglin extends EntityMonster implements IMonster, IOglin {

    private static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityZoglin.class, DataWatcherRegistry.i);
    private int bv;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super EntityZoglin>>> b = ImmutableList.of(SensorType.c, SensorType.d);
    protected static final ImmutableList<? extends MemoryModuleType<?>> c = ImmutableList.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);

    public EntityZoglin(EntityTypes<? extends EntityZoglin> entitytypes, World world) {
        super(entitytypes, world);
        this.f = 5;
    }

    @Override
    protected BehaviorController.b<EntityZoglin> cJ() {
        return BehaviorController.a((Collection) EntityZoglin.c, (Collection) EntityZoglin.b);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        BehaviorController<EntityZoglin> behaviorcontroller = this.cJ().a(dynamic);

        a(behaviorcontroller);
        b(behaviorcontroller);
        c(behaviorcontroller);
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.e();
        return behaviorcontroller;
    }

    private static void a(BehaviorController<EntityZoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.CORE, 0, ImmutableList.of(new BehaviorLook(45, 90), new BehavorMove(200)));
    }

    private static void b(BehaviorController<EntityZoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.IDLE, 10, ImmutableList.of(new BehaviorAttackTargetSet<>(EntityZoglin::eP), new BehaviorRunSometimes<>(new BehaviorLookTarget(8.0F), IntRange.a(30, 60)), new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(0.4F), 2), Pair.of(new BehaviorLookWalk(0.4F, 3), 2), Pair.of(new BehaviorNop(30, 60), 1)))));
    }

    private static void c(BehaviorController<EntityZoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.FLIGHT, 10, ImmutableList.of(new BehaviorWalkAwayOutOfRange(1.0F), new BehaviorRunIf<>(EntityZoglin::eL, new BehaviorAttack(40)), new BehaviorRunIf<>(EntityZoglin::isBaby, new BehaviorAttack(15)), new BehaviorAttackTargetForget<>()), MemoryModuleType.ATTACK_TARGET);
    }

    private Optional<? extends EntityLiving> eP() {
        return ((List) this.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of())).stream().filter(EntityZoglin::j).findFirst();
    }

    private static boolean j(EntityLiving entityliving) {
        EntityTypes<?> entitytypes = entityliving.getEntityType();

        return entitytypes != EntityTypes.ZOGLIN && entitytypes != EntityTypes.CREEPER && IEntitySelector.f.test(entityliving);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityZoglin.d, false);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityZoglin.d.equals(datawatcherobject)) {
            this.updateSize();
        }

    }

    public static AttributeProvider.Builder m() {
        return EntityMonster.eS().a(GenericAttributes.MAX_HEALTH, 40.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.KNOCKBACK_RESISTANCE, 0.6000000238418579D).a(GenericAttributes.ATTACK_KNOCKBACK, 1.0D).a(GenericAttributes.ATTACK_DAMAGE, 6.0D);
    }

    public boolean eL() {
        return !this.isBaby();
    }

    @Override
    public boolean attackEntity(Entity entity) {
        if (!(entity instanceof EntityLiving)) {
            return false;
        } else {
            this.bv = 10;
            this.world.broadcastEntityEffect(this, (byte) 4);
            this.playSound(SoundEffects.ENTITY_ZOGLIN_ATTACK, 1.0F, this.dG());
            return IOglin.a(this, (EntityLiving) entity);
        }
    }

    @Override
    protected void f(EntityLiving entityliving) {
        if (!this.isBaby()) {
            IOglin.b(this, entityliving);
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);

        if (this.world.isClientSide) {
            return false;
        } else if (flag && damagesource.getEntity() instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) damagesource.getEntity();

            if (IEntitySelector.f.test(entityliving) && !BehaviorUtil.a(this, entityliving, 4.0D)) {
                this.k(entityliving);
            }

            return flag;
        } else {
            return flag;
        }
    }

    private void k(EntityLiving entityliving) {
        this.bn.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        this.bn.a(MemoryModuleType.ATTACK_TARGET, entityliving, 200L);
    }

    @Override
    public BehaviorController<EntityZoglin> getBehaviorController() {
        return super.getBehaviorController();
    }

    protected void eM() {
        Activity activity = (Activity) this.bn.f().orElse((Object) null);

        this.bn.a((List) ImmutableList.of(Activity.FLIGHT, Activity.IDLE));
        Activity activity1 = (Activity) this.bn.f().orElse((Object) null);

        if (activity1 == Activity.FLIGHT && activity != Activity.FLIGHT) {
            this.eO();
        }

        this.setAggressive(this.bn.hasMemory(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    protected void mobTick() {
        this.world.getMethodProfiler().enter("zoglinBrain");
        this.getBehaviorController().a((WorldServer) this.world, (EntityLiving) this);
        this.world.getMethodProfiler().exit();
        this.eM();
    }

    @Override
    public void a(boolean flag) {
        this.getDataWatcher().set(EntityZoglin.d, flag);
        if (!this.world.isClientSide && flag) {
            this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(0.5D);
        }

    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityZoglin.d);
    }

    @Override
    public void movementTick() {
        if (this.bv > 0) {
            --this.bv;
        }

        super.movementTick();
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.world.isClientSide ? null : (this.bn.hasMemory(MemoryModuleType.ATTACK_TARGET) ? SoundEffects.ENTITY_ZOGLIN_ANGRY : SoundEffects.ENTITY_ZOGLIN_AMBIENT);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOGLIN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ZOGLIN_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_ZOGLIN_STEP, 0.15F, 1.0F);
    }

    protected void eO() {
        this.playSound(SoundEffects.ENTITY_ZOGLIN_ANGRY, 1.0F, this.dG());
    }

    @Override
    protected void M() {
        super.M();
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
            this.a(true);
        }

    }
}
