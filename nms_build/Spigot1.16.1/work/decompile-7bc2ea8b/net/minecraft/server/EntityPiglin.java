package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityPiglin extends EntityMonster implements ICrossbow {

    private static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bv = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bw = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bx = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.i);
    private static final UUID by = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
    private static final AttributeModifier bz = new AttributeModifier(EntityPiglin.by, "Baby speed boost", 0.20000000298023224D, AttributeModifier.Operation.MULTIPLY_BASE);
    private int bA = 0;
    private final InventorySubcontainer bB = new InventorySubcontainer(8);
    private boolean bC = false;
    protected static final ImmutableList<SensorType<? extends Sensor<? super EntityPiglin>>> b = ImmutableList.of(SensorType.c, SensorType.d, SensorType.b, SensorType.g, SensorType.e, SensorType.l);
    protected static final ImmutableList<MemoryModuleType<?>> c = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEAREST_ADULT_PIGLINS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.AVOID_TARGET, MemoryModuleType.ADMIRING_ITEM, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleType.DANCING, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_BABY_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_NEMSIS, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.RIDE_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.NEAREST_REPELLENT});

    public EntityPiglin(EntityTypes<? extends EntityMonster> entitytypes, World world) {
        super(entitytypes, world);
        this.setCanPickupLoot(true);
        ((Navigation) this.getNavigation()).a(true);
        this.f = 5;
        this.a(PathType.DANGER_FIRE, 16.0F);
        this.a(PathType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.setBoolean("IsBaby", true);
        }

        if (this.eT()) {
            nbttagcompound.setBoolean("IsImmuneToZombification", true);
        }

        if (this.bC) {
            nbttagcompound.setBoolean("CannotHunt", true);
        }

        nbttagcompound.setInt("TimeInOverworld", this.bA);
        nbttagcompound.set("Inventory", this.bB.g());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.a(nbttagcompound.getBoolean("IsBaby"));
        this.t(nbttagcompound.getBoolean("IsImmuneToZombification"));
        this.v(nbttagcompound.getBoolean("CannotHunt"));
        this.bA = nbttagcompound.getInt("TimeInOverworld");
        this.bB.a(nbttagcompound.getList("Inventory", 10));
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        this.bB.f().forEach(this::a);
    }

    protected ItemStack k(ItemStack itemstack) {
        return this.bB.a(itemstack);
    }

    protected boolean l(ItemStack itemstack) {
        return this.bB.b(itemstack);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPiglin.d, false);
        this.datawatcher.register(EntityPiglin.bw, false);
        this.datawatcher.register(EntityPiglin.bv, false);
        this.datawatcher.register(EntityPiglin.bx, false);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityPiglin.d.equals(datawatcherobject)) {
            this.updateSize();
        }

    }

    public static AttributeProvider.Builder eL() {
        return EntityMonster.eS().a(GenericAttributes.MAX_HEALTH, 16.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.3499999940395355D).a(GenericAttributes.ATTACK_DAMAGE, 5.0D);
    }

    public static boolean b(EntityTypes<EntityPiglin> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return !generatoraccess.getType(blockposition.down()).a(Blocks.NETHER_WART_BLOCK);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn != EnumMobSpawn.STRUCTURE) {
            if (generatoraccess.getRandom().nextFloat() < 0.2F) {
                this.a(true);
            } else if (this.eM()) {
                this.setSlot(EnumItemSlot.MAINHAND, this.eU());
            }
        }

        PiglinAI.a(this);
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected boolean L() {
        return false;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isPersistent();
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (this.eM()) {
            this.d(EnumItemSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.d(EnumItemSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.d(EnumItemSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.d(EnumItemSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }

    }

    private void d(EnumItemSlot enumitemslot, ItemStack itemstack) {
        if (this.world.random.nextFloat() < 0.1F) {
            this.setSlot(enumitemslot, itemstack);
        }

    }

    @Override
    protected BehaviorController.b<EntityPiglin> cJ() {
        return BehaviorController.a((Collection) EntityPiglin.c, (Collection) EntityPiglin.b);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return PiglinAI.a(this, this.cJ().a(dynamic));
    }

    @Override
    public BehaviorController<EntityPiglin> getBehaviorController() {
        return super.getBehaviorController();
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        EnumInteractionResult enuminteractionresult = super.b(entityhuman, enumhand);

        if (enuminteractionresult.a()) {
            return enuminteractionresult;
        } else if (!this.world.isClientSide) {
            return PiglinAI.a(this, entityhuman, enumhand);
        } else {
            boolean flag = PiglinAI.b(this, entityhuman.b(enumhand)) && this.eP() != EntityPiglin.ArmPose.ADMIRING_ITEM;

            return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    @Override
    public double aX() {
        return this.isBaby() ? -0.1D : -0.45D;
    }

    @Override
    public double aY() {
        return (double) this.getHeight() * 0.92D;
    }

    @Override
    public void a(boolean flag) {
        this.getDataWatcher().set(EntityPiglin.d, flag);
        if (!this.world.isClientSide) {
            AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributemodifiable.removeModifier(EntityPiglin.bz);
            if (flag) {
                attributemodifiable.b(EntityPiglin.bz);
            }
        }

    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityPiglin.d);
    }

    public boolean eM() {
        return !this.isBaby();
    }

    public void t(boolean flag) {
        this.getDataWatcher().set(EntityPiglin.bv, flag);
    }

    private boolean eT() {
        return (Boolean) this.getDataWatcher().get(EntityPiglin.bv);
    }

    private void v(boolean flag) {
        this.bC = flag;
    }

    protected boolean eN() {
        return !this.bC;
    }

    public boolean eO() {
        return !this.world.getDimensionManager().isPiglinSafe() && !this.eT() && !this.isNoAI();
    }

    @Override
    protected void mobTick() {
        this.world.getMethodProfiler().enter("piglinBrain");
        this.getBehaviorController().a((WorldServer) this.world, (EntityLiving) this);
        this.world.getMethodProfiler().exit();
        PiglinAI.b(this);
        if (this.eO()) {
            ++this.bA;
        } else {
            this.bA = 0;
        }

        if (this.bA > 300) {
            this.a(SoundEffects.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED);
            this.b((WorldServer) this.world);
        }

    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return this.f;
    }

    private void b(WorldServer worldserver) {
        PiglinAI.c(this);
        this.bB.f().forEach(this::a);
        EntityPigZombie entitypigzombie = (EntityPigZombie) this.b(EntityTypes.ZOMBIFIED_PIGLIN);

        entitypigzombie.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
    }

    @Nullable
    @Override
    public EntityLiving getGoalTarget() {
        return (EntityLiving) this.bn.getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object) null);
    }

    private ItemStack eU() {
        return (double) this.random.nextFloat() < 0.5D ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }

    private boolean eV() {
        return (Boolean) this.datawatcher.get(EntityPiglin.bw);
    }

    @Override
    public void b(boolean flag) {
        this.datawatcher.set(EntityPiglin.bw, flag);
    }

    @Override
    public void V_() {
        this.ticksFarFromPlayer = 0;
    }

    public EntityPiglin.ArmPose eP() {
        return this.eQ() ? EntityPiglin.ArmPose.DANCING : (PiglinAI.a(this.getItemInOffHand().getItem()) ? EntityPiglin.ArmPose.ADMIRING_ITEM : (this.isAggressive() && this.eW() ? EntityPiglin.ArmPose.ATTACKING_WITH_MELEE_WEAPON : (this.eV() ? EntityPiglin.ArmPose.CROSSBOW_CHARGE : (this.isAggressive() && this.a(Items.CROSSBOW) ? EntityPiglin.ArmPose.CROSSBOW_HOLD : EntityPiglin.ArmPose.DEFAULT))));
    }

    public boolean eQ() {
        return (Boolean) this.datawatcher.get(EntityPiglin.bx);
    }

    public void u(boolean flag) {
        this.datawatcher.set(EntityPiglin.bx, flag);
    }

    private boolean eW() {
        return this.getItemInMainHand().getItem() instanceof ItemToolMaterial;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);

        if (this.world.isClientSide) {
            return false;
        } else {
            if (flag && damagesource.getEntity() instanceof EntityLiving) {
                PiglinAI.a(this, (EntityLiving) damagesource.getEntity());
            }

            return flag;
        }
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        this.b(this, 1.6F);
    }

    @Override
    public void a(EntityLiving entityliving, ItemStack itemstack, IProjectile iprojectile, float f) {
        this.a(this, entityliving, iprojectile, f, 1.6F);
    }

    @Override
    public boolean a(ItemProjectileWeapon itemprojectileweapon) {
        return itemprojectileweapon == Items.CROSSBOW;
    }

    protected void m(ItemStack itemstack) {
        this.b(EnumItemSlot.MAINHAND, itemstack);
    }

    protected void n(ItemStack itemstack) {
        if (itemstack.getItem() == PiglinAI.a) {
            this.setSlot(EnumItemSlot.OFFHAND, itemstack);
            this.d(EnumItemSlot.OFFHAND);
        } else {
            this.b(EnumItemSlot.OFFHAND, itemstack);
        }

    }

    @Override
    public boolean i(ItemStack itemstack) {
        return this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && PiglinAI.a(this, itemstack);
    }

    protected boolean o(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.j(itemstack);
        ItemStack itemstack1 = this.getEquipment(enumitemslot);

        return this.a(itemstack, itemstack1);
    }

    @Override
    protected boolean a(ItemStack itemstack, ItemStack itemstack1) {
        if (EnchantmentManager.d(itemstack1)) {
            return false;
        } else {
            boolean flag = PiglinAI.a(itemstack.getItem()) || itemstack.getItem() == Items.CROSSBOW;
            boolean flag1 = PiglinAI.a(itemstack1.getItem()) || itemstack1.getItem() == Items.CROSSBOW;

            return flag && !flag1 ? true : (!flag && flag1 ? false : (this.eM() && itemstack.getItem() != Items.CROSSBOW && itemstack1.getItem() == Items.CROSSBOW ? false : super.a(itemstack, itemstack1)));
        }
    }

    @Override
    protected void b(EntityItem entityitem) {
        this.a(entityitem);
        PiglinAI.a(this, entityitem);
    }

    @Override
    public boolean a(Entity entity, boolean flag) {
        if (this.isBaby() && entity.getEntityType() == EntityTypes.HOGLIN) {
            entity = this.b(entity, 3);
        }

        return super.a(entity, flag);
    }

    private Entity b(Entity entity, int i) {
        List<Entity> list = entity.getPassengers();

        return i != 1 && !list.isEmpty() ? this.b((Entity) list.get(0), i - 1) : entity;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.world.isClientSide ? null : (SoundEffect) PiglinAI.d(this).orElse((Object) null);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_PIGLIN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PIGLIN_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_PIGLIN_STEP, 0.15F, 1.0F);
    }

    protected void a(SoundEffect soundeffect) {
        this.playSound(soundeffect, this.getSoundVolume(), this.dG());
    }

    @Override
    protected void M() {
        super.M();
        PacketDebug.a((EntityLiving) this);
    }

    public static enum ArmPose {

        ATTACKING_WITH_MELEE_WEAPON, CROSSBOW_HOLD, CROSSBOW_CHARGE, ADMIRING_ITEM, DANCING, DEFAULT;

        private ArmPose() {}
    }
}
