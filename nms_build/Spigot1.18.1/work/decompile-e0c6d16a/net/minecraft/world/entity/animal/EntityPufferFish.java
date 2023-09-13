package net.minecraft.world.entity.animal;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class EntityPufferFish extends EntityFish {

    private static final DataWatcherObject<Integer> PUFF_STATE = DataWatcher.defineId(EntityPufferFish.class, DataWatcherRegistry.INT);
    int inflateCounter;
    int deflateTimer;
    private static final Predicate<EntityLiving> SCARY_MOB = (entityliving) -> {
        return entityliving instanceof EntityHuman && ((EntityHuman) entityliving).isCreative() ? false : entityliving.getType() == EntityTypes.AXOLOTL || entityliving.getMobType() != EnumMonsterType.WATER;
    };
    static final PathfinderTargetCondition targetingConditions = PathfinderTargetCondition.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight().selector(EntityPufferFish.SCARY_MOB);
    public static final int STATE_SMALL = 0;
    public static final int STATE_MID = 1;
    public static final int STATE_FULL = 2;

    public EntityPufferFish(EntityTypes<? extends EntityPufferFish> entitytypes, World world) {
        super(entitytypes, world);
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityPufferFish.PUFF_STATE, 0);
    }

    public int getPuffState() {
        return (Integer) this.entityData.get(EntityPufferFish.PUFF_STATE);
    }

    public void setPuffState(int i) {
        this.entityData.set(EntityPufferFish.PUFF_STATE, i);
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntityPufferFish.PUFF_STATE.equals(datawatcherobject)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("PuffState", this.getPuffState());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setPuffState(nbttagcompound.getInt("PuffState"));
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EntityPufferFish.a(this));
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            if (this.inflateCounter > 0) {
                if (this.getPuffState() == 0) {
                    this.playSound(SoundEffects.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
                    this.setPuffState(1);
                } else if (this.inflateCounter > 40 && this.getPuffState() == 1) {
                    this.playSound(SoundEffects.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
                    this.setPuffState(2);
                }

                ++this.inflateCounter;
            } else if (this.getPuffState() != 0) {
                if (this.deflateTimer > 60 && this.getPuffState() == 2) {
                    this.playSound(SoundEffects.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
                    this.setPuffState(1);
                } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
                    this.playSound(SoundEffects.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
                    this.setPuffState(0);
                }

                ++this.deflateTimer;
            }
        }

        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.getPuffState() > 0) {
            List<EntityInsentient> list = this.level.getEntitiesOfClass(EntityInsentient.class, this.getBoundingBox().inflate(0.3D), (entityinsentient) -> {
                return EntityPufferFish.targetingConditions.test(this, entityinsentient);
            });
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();

                if (entityinsentient.isAlive()) {
                    this.touch(entityinsentient);
                }
            }
        }

    }

    private void touch(EntityInsentient entityinsentient) {
        int i = this.getPuffState();

        if (entityinsentient.hurt(DamageSource.mobAttack(this), (float) (1 + i))) {
            entityinsentient.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0), this);
            this.playSound(SoundEffects.PUFFER_FISH_STING, 1.0F, 1.0F);
        }

    }

    @Override
    public void playerTouch(EntityHuman entityhuman) {
        int i = this.getPuffState();

        if (entityhuman instanceof EntityPlayer && i > 0 && entityhuman.hurt(DamageSource.mobAttack(this), (float) (1 + i))) {
            if (!this.isSilent()) {
                ((EntityPlayer) entityhuman).connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.PUFFER_FISH_STING, 0.0F));
            }

            entityhuman.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0), this);
        }

    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.PUFFER_FISH_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.PUFFER_FISH_HURT;
    }

    @Override
    protected SoundEffect getFlopSound() {
        return SoundEffects.PUFFER_FISH_FLOP;
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return super.getDimensions(entitypose).scale(getScale(this.getPuffState()));
    }

    private static float getScale(int i) {
        switch (i) {
            case 0:
                return 0.5F;
            case 1:
                return 0.7F;
            default:
                return 1.0F;
        }
    }

    private static class a extends PathfinderGoal {

        private final EntityPufferFish fish;

        public a(EntityPufferFish entitypufferfish) {
            this.fish = entitypufferfish;
        }

        @Override
        public boolean canUse() {
            List<EntityLiving> list = this.fish.level.getEntitiesOfClass(EntityLiving.class, this.fish.getBoundingBox().inflate(2.0D), (entityliving) -> {
                return EntityPufferFish.targetingConditions.test(this.fish, entityliving);
            });

            return !list.isEmpty();
        }

        @Override
        public void start() {
            this.fish.inflateCounter = 1;
            this.fish.deflateTimer = 0;
        }

        @Override
        public void stop() {
            this.fish.inflateCounter = 0;
        }
    }
}
