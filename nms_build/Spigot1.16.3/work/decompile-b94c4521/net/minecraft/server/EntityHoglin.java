package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Random;
import javax.annotation.Nullable;

public class EntityHoglin extends EntityAnimal implements IMonster, IOglin {

    private static final DataWatcherObject<Boolean> bq = DataWatcher.a(EntityHoglin.class, DataWatcherRegistry.i);
    private int br;
    public int conversionTicks = 0;
    public boolean cannotBeHunted = false;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super EntityHoglin>>> bo = ImmutableList.of(SensorType.c, SensorType.d, SensorType.n, SensorType.m);
    protected static final ImmutableList<? extends MemoryModuleType<?>> bp = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, new MemoryModuleType[]{MemoryModuleType.AVOID_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.NEAREST_VISIBLE_ADULY, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.PACIFIED});

    public EntityHoglin(EntityTypes<? extends EntityHoglin> entitytypes, World world) {
        super(entitytypes, world);
        this.f = 5;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    public static AttributeProvider.Builder eK() {
        return EntityMonster.eR().a(GenericAttributes.MAX_HEALTH, 40.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.KNOCKBACK_RESISTANCE, 0.6000000238418579D).a(GenericAttributes.ATTACK_KNOCKBACK, 1.0D).a(GenericAttributes.ATTACK_DAMAGE, 6.0D);
    }

    @Override
    public boolean attackEntity(Entity entity) {
        if (!(entity instanceof EntityLiving)) {
            return false;
        } else {
            this.br = 10;
            this.world.broadcastEntityEffect(this, (byte) 4);
            this.playSound(SoundEffects.ENTITY_HOGLIN_ATTACK, 1.0F, this.dG());
            HoglinAI.a(this, (EntityLiving) entity);
            return IOglin.a(this, (EntityLiving) entity);
        }
    }

    @Override
    protected void e(EntityLiving entityliving) {
        if (this.eL()) {
            IOglin.b(this, entityliving);
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);

        if (this.world.isClientSide) {
            return false;
        } else {
            if (flag && damagesource.getEntity() instanceof EntityLiving) {
                HoglinAI.b(this, (EntityLiving) damagesource.getEntity());
            }

            return flag;
        }
    }

    @Override
    protected BehaviorController.b<EntityHoglin> cJ() {
        return BehaviorController.a((Collection) EntityHoglin.bp, (Collection) EntityHoglin.bo);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return HoglinAI.a(this.cJ().a(dynamic));
    }

    @Override
    public BehaviorController<EntityHoglin> getBehaviorController() {
        return super.getBehaviorController();
    }

    @Override
    protected void mobTick() {
        this.world.getMethodProfiler().enter("hoglinBrain");
        this.getBehaviorController().a((WorldServer) this.world, (EntityLiving) this);
        this.world.getMethodProfiler().exit();
        HoglinAI.a(this);
        if (this.isConverting()) {
            ++this.conversionTicks;
            if (this.conversionTicks > 300) {
                this.a(SoundEffects.ENTITY_HOGLIN_CONVERTED_TO_ZOMBIFIED);
                this.c((WorldServer) this.world);
            }
        } else {
            this.conversionTicks = 0;
        }

    }

    @Override
    public void movementTick() {
        if (this.br > 0) {
            --this.br;
        }

        super.movementTick();
    }

    @Override
    protected void m() {
        if (this.isBaby()) {
            this.f = 3;
            this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(0.5D);
        } else {
            this.f = 5;
            this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
        }

    }

    public static boolean c(EntityTypes<EntityHoglin> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return !generatoraccess.getType(blockposition.down()).a(Blocks.NETHER_WART_BLOCK);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (worldaccess.getRandom().nextFloat() < 0.2F) {
            this.setBaby(true);
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isPersistent();
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return HoglinAI.a(this, blockposition) ? -1.0F : (iworldreader.getType(blockposition.down()).a(Blocks.CRIMSON_NYLIUM) ? 10.0F : 0.0F);
    }

    @Override
    public double bb() {
        return (double) this.getHeight() - (this.isBaby() ? 0.2D : 0.15D);
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        EnumInteractionResult enuminteractionresult = super.b(entityhuman, enumhand);

        if (enuminteractionresult.a()) {
            this.setPersistent();
        }

        return enuminteractionresult;
    }

    @Override
    protected boolean isDropExperience() {
        return true;
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return this.f;
    }

    private void c(WorldServer worldserver) {
        EntityZoglin entityzoglin = (EntityZoglin) this.a(EntityTypes.ZOGLIN, true);

        if (entityzoglin != null) {
            entityzoglin.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
        }

    }

    @Override
    public boolean k(ItemStack itemstack) {
        return itemstack.getItem() == Items.bw;
    }

    public boolean eL() {
        return !this.isBaby();
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityHoglin.bq, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.isImmuneToZombification()) {
            nbttagcompound.setBoolean("IsImmuneToZombification", true);
        }

        nbttagcompound.setInt("TimeInOverworld", this.conversionTicks);
        if (this.cannotBeHunted) {
            nbttagcompound.setBoolean("CannotBeHunted", true);
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setImmuneToZombification(nbttagcompound.getBoolean("IsImmuneToZombification"));
        this.conversionTicks = nbttagcompound.getInt("TimeInOverworld");
        this.u(nbttagcompound.getBoolean("CannotBeHunted"));
    }

    public void setImmuneToZombification(boolean flag) {
        this.getDataWatcher().set(EntityHoglin.bq, flag);
    }

    public boolean isImmuneToZombification() {
        return (Boolean) this.getDataWatcher().get(EntityHoglin.bq);
    }

    public boolean isConverting() {
        return !this.world.getDimensionManager().isPiglinSafe() && !this.isImmuneToZombification() && !this.isNoAI();
    }

    private void u(boolean flag) {
        this.cannotBeHunted = flag;
    }

    public boolean eO() {
        return this.eL() && !this.cannotBeHunted;
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityHoglin entityhoglin = (EntityHoglin) EntityTypes.HOGLIN.a((World) worldserver);

        if (entityhoglin != null) {
            entityhoglin.setPersistent();
        }

        return entityhoglin;
    }

    @Override
    public boolean eP() {
        return !HoglinAI.c(this) && super.eP();
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.world.isClientSide ? null : (SoundEffect) HoglinAI.b(this).orElse((Object) null);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_HOGLIN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_HOGLIN_DEATH;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected SoundEffect getSoundSplash() {
        return SoundEffects.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_HOGLIN_STEP, 0.15F, 1.0F);
    }

    protected void a(SoundEffect soundeffect) {
        this.playSound(soundeffect, this.getSoundVolume(), this.dG());
    }

    @Override
    protected void M() {
        super.M();
        PacketDebug.a((EntityLiving) this);
    }
}
