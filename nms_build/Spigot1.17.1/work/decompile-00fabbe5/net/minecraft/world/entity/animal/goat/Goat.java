package net.minecraft.world.entity.animal.goat;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
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
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemLiquidUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderNormal;

public class Goat extends EntityAnimal {

    public static final EntitySize LONG_JUMPING_DIMENSIONS = EntitySize.b(0.9F, 1.3F).a(0.7F);
    private static final int ADULT_ATTACK_DAMAGE = 2;
    private static final int BABY_ATTACK_DAMAGE = 1;
    protected static final ImmutableList<SensorType<? extends Sensor<? super Goat>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.GOAT_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET});
    public static final int GOAT_FALL_DAMAGE_REDUCTION = 10;
    public static final double GOAT_SCREAMING_CHANCE = 0.02D;
    private static final DataWatcherObject<Boolean> DATA_IS_SCREAMING_GOAT = DataWatcher.a(Goat.class, DataWatcherRegistry.BOOLEAN);
    private boolean isLoweringHead;
    private int lowerHeadTick;

    public Goat(EntityTypes<? extends Goat> entitytypes, World world) {
        super(entitytypes, world);
        this.getNavigation().d(true);
    }

    @Override
    protected BehaviorController.b<Goat> dp() {
        return BehaviorController.a((Collection) Goat.MEMORY_TYPES, (Collection) Goat.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return GoatAi.a(this.dp().a(dynamic));
    }

    public static AttributeProvider.Builder p() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D).a(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void n() {
        if (this.isBaby()) {
            this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1.0D);
        } else {
            this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(2.0D);
        }

    }

    @Override
    protected int d(float f, float f1) {
        return super.d(f, f1) - 10;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_AMBIENT : SoundEffects.GOAT_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_HURT : SoundEffects.GOAT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_DEATH : SoundEffects.GOAT_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.GOAT_STEP, 0.15F, 1.0F);
    }

    protected SoundEffect t() {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_MILK : SoundEffects.GOAT_MILK;
    }

    @Override
    public Goat createChild(WorldServer worldserver, EntityAgeable entityageable) {
        Goat goat = (Goat) EntityTypes.GOAT.a((World) worldserver);

        if (goat != null) {
            GoatAi.a(goat);
            boolean flag = entityageable instanceof Goat && ((Goat) entityageable).isScreamingGoat();

            goat.setScreamingGoat(flag || worldserver.getRandom().nextDouble() < 0.02D);
        }

        return goat;
    }

    @Override
    public BehaviorController<Goat> getBehaviorController() {
        return super.getBehaviorController();
    }

    @Override
    protected void mobTick() {
        this.level.getMethodProfiler().enter("goatBrain");
        this.getBehaviorController().a((WorldServer) this.level, (EntityLiving) this);
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("goatActivityUpdate");
        GoatAi.b(this);
        this.level.getMethodProfiler().exit();
        super.mobTick();
    }

    @Override
    public int fa() {
        return 15;
    }

    @Override
    public void setHeadRotation(float f) {
        int i = this.fa();
        float f1 = MathHelper.c(this.yBodyRot, f);
        float f2 = MathHelper.a(f1, (float) (-i), (float) i);

        super.setHeadRotation(this.yBodyRot + f2);
    }

    @Override
    public SoundEffect e(ItemStack itemstack) {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_EAT : SoundEffects.GOAT_EAT;
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.a(Items.BUCKET) && !this.isBaby()) {
            entityhuman.playSound(this.t(), 1.0F, 1.0F);
            ItemStack itemstack1 = ItemLiquidUtil.a(itemstack, entityhuman, Items.MILK_BUCKET.createItemStack());

            entityhuman.a(enumhand, itemstack1);
            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = super.b(entityhuman, enumhand);

            if (enuminteractionresult.a() && this.isBreedItem(itemstack)) {
                this.level.playSound((EntityHuman) null, (Entity) this, this.e(itemstack), SoundCategory.NEUTRAL, 1.0F, MathHelper.b(this.level.random, 0.8F, 1.2F));
            }

            return enuminteractionresult;
        }
    }

    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        GoatAi.a(this);
        this.setScreamingGoat(worldaccess.getRandom().nextDouble() < 0.02D);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected void R() {
        super.R();
        PacketDebug.a((EntityLiving) this);
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return entitypose == EntityPose.LONG_JUMPING ? Goat.LONG_JUMPING_DIMENSIONS.a(this.dz()) : super.a(entitypose);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("IsScreamingGoat", this.isScreamingGoat());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setScreamingGoat(nbttagcompound.getBoolean("IsScreamingGoat"));
    }

    @Override
    public void a(byte b0) {
        if (b0 == 58) {
            this.isLoweringHead = true;
        } else if (b0 == 59) {
            this.isLoweringHead = false;
        } else {
            super.a(b0);
        }

    }

    @Override
    public void movementTick() {
        if (this.isLoweringHead) {
            ++this.lowerHeadTick;
        } else {
            this.lowerHeadTick -= 2;
        }

        this.lowerHeadTick = MathHelper.clamp(this.lowerHeadTick, 0, 20);
        super.movementTick();
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(Goat.DATA_IS_SCREAMING_GOAT, false);
    }

    public boolean isScreamingGoat() {
        return (Boolean) this.entityData.get(Goat.DATA_IS_SCREAMING_GOAT);
    }

    public void setScreamingGoat(boolean flag) {
        this.entityData.set(Goat.DATA_IS_SCREAMING_GOAT, flag);
    }

    public float fx() {
        return (float) this.lowerHeadTick / 20.0F * 30.0F * 0.017453292F;
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new Goat.b(this, world);
    }

    private static class b extends Navigation {

        b(Goat goat, World world) {
            super(goat, world);
        }

        @Override
        protected Pathfinder a(int i) {
            this.nodeEvaluator = new Goat.a();
            return new Pathfinder(this.nodeEvaluator, i);
        }
    }

    private static class a extends PathfinderNormal {

        private final BlockPosition.MutableBlockPosition belowPos = new BlockPosition.MutableBlockPosition();

        a() {}

        @Override
        public PathType a(IBlockAccess iblockaccess, int i, int j, int k) {
            this.belowPos.d(i, j - 1, k);
            PathType pathtype = b(iblockaccess, this.belowPos);

            return pathtype == PathType.POWDER_SNOW ? PathType.BLOCKED : a(iblockaccess, this.belowPos.c(EnumDirection.UP));
        }
    }
}
