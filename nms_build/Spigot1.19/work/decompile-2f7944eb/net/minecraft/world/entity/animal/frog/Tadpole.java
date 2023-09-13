package net.minecraft.world.entity.animal.frog;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.EntityFish;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class Tadpole extends EntityFish {

    @VisibleForTesting
    public static int ticksToBeFrog = Math.abs(-24000);
    public static float HITBOX_WIDTH = 0.4F;
    public static float HITBOX_HEIGHT = 0.3F;
    public int age;
    protected static final ImmutableList<SensorType<? extends Sensor<? super Tadpole>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.FROG_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING);

    public Tadpole(EntityTypes<? extends EntityFish> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new NavigationGuardian(this, world);
    }

    @Override
    protected BehaviorController.b<Tadpole> brainProvider() {
        return BehaviorController.provider(Tadpole.MEMORY_TYPES, Tadpole.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return TadpoleAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public BehaviorController<Tadpole> getBrain() {
        return super.getBrain();
    }

    @Override
    protected SoundEffect getFlopSound() {
        return SoundEffects.TADPOLE_FLOP;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("tadpoleBrain");
        this.getBrain().tick((WorldServer) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("tadpoleActivityUpdate");
        TadpoleAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MOVEMENT_SPEED, 1.0D).add(GenericAttributes.MAX_HEALTH, 6.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.setAge(this.age + 1);
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Age", this.age);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setAge(nbttagcompound.getInt("Age"));
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.TADPOLE_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.TADPOLE_DEATH;
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (this.isFood(itemstack)) {
            this.feed(entityhuman, itemstack);
            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return (EnumInteractionResult) Bucketable.bucketMobPickup(entityhuman, enumhand, this).orElse(super.mobInteract(entityhuman, enumhand));
        }
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    @Override
    public boolean fromBucket() {
        return true;
    }

    @Override
    public void setFromBucket(boolean flag) {}

    @Override
    public void saveToBucketTag(ItemStack itemstack) {
        Bucketable.saveDefaultDataToBucketTag(this, itemstack);
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        nbttagcompound.putInt("Age", this.getAge());
    }

    @Override
    public void loadFromBucketTag(NBTTagCompound nbttagcompound) {
        Bucketable.loadDefaultDataFromBucketTag(this, nbttagcompound);
        if (nbttagcompound.contains("Age")) {
            this.setAge(nbttagcompound.getInt("Age"));
        }

    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.TADPOLE_BUCKET);
    }

    @Override
    public SoundEffect getPickupSound() {
        return SoundEffects.BUCKET_FILL_TADPOLE;
    }

    private boolean isFood(ItemStack itemstack) {
        return Frog.TEMPTATION_ITEM.test(itemstack);
    }

    private void feed(EntityHuman entityhuman, ItemStack itemstack) {
        this.usePlayerItem(entityhuman, itemstack);
        this.ageUp(EntityAgeable.getSpeedUpSecondsWhenFeeding(this.getTicksLeftUntilAdult()));
        this.level.addParticle(Particles.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
    }

    private void usePlayerItem(EntityHuman entityhuman, ItemStack itemstack) {
        if (!entityhuman.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

    }

    private int getAge() {
        return this.age;
    }

    private void ageUp(int i) {
        this.setAge(this.age + i * 20);
    }

    private void setAge(int i) {
        this.age = i;
        if (this.age >= Tadpole.ticksToBeFrog) {
            this.ageUp();
        }

    }

    private void ageUp() {
        World world = this.level;

        if (world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;
            Frog frog = (Frog) EntityTypes.FROG.create(this.level);

            frog.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            frog.finalizeSpawn(worldserver, this.level.getCurrentDifficultyAt(frog.blockPosition()), EnumMobSpawn.CONVERSION, (GroupDataEntity) null, (NBTTagCompound) null);
            frog.setNoAi(this.isNoAi());
            if (this.hasCustomName()) {
                frog.setCustomName(this.getCustomName());
                frog.setCustomNameVisible(this.isCustomNameVisible());
            }

            frog.setPersistenceRequired();
            this.playSound(SoundEffects.TADPOLE_GROW_UP, 0.15F, 1.0F);
            worldserver.addFreshEntityWithPassengers(frog);
            this.discard();
        }

    }

    private int getTicksLeftUntilAdult() {
        return Math.max(0, Tadpole.ticksToBeFrog - this.age);
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }
}
