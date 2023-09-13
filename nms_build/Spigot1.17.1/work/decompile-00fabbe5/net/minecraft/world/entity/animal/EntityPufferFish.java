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

    private static final DataWatcherObject<Integer> PUFF_STATE = DataWatcher.a(EntityPufferFish.class, DataWatcherRegistry.INT);
    int inflateCounter;
    int deflateTimer;
    private static final Predicate<EntityLiving> SCARY_MOB = (entityliving) -> {
        return entityliving instanceof EntityHuman && ((EntityHuman) entityliving).isCreative() ? false : entityliving.getEntityType() == EntityTypes.AXOLOTL || entityliving.getMonsterType() != EnumMonsterType.WATER;
    };
    static final PathfinderTargetCondition targetingConditions = PathfinderTargetCondition.b().e().d().a(EntityPufferFish.SCARY_MOB);
    public static final int STATE_SMALL = 0;
    public static final int STATE_MID = 1;
    public static final int STATE_FULL = 2;

    public EntityPufferFish(EntityTypes<? extends EntityPufferFish> entitytypes, World world) {
        super(entitytypes, world);
        this.updateSize();
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityPufferFish.PUFF_STATE, 0);
    }

    public int getPuffState() {
        return (Integer) this.entityData.get(EntityPufferFish.PUFF_STATE);
    }

    public void setPuffState(int i) {
        this.entityData.set(EntityPufferFish.PUFF_STATE, i);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPufferFish.PUFF_STATE.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("PuffState", this.getPuffState());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setPuffState(nbttagcompound.getInt("PuffState"));
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(1, new EntityPufferFish.a(this));
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.doAITick()) {
            if (this.inflateCounter > 0) {
                if (this.getPuffState() == 0) {
                    this.playSound(SoundEffects.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.ep());
                    this.setPuffState(1);
                } else if (this.inflateCounter > 40 && this.getPuffState() == 1) {
                    this.playSound(SoundEffects.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.ep());
                    this.setPuffState(2);
                }

                ++this.inflateCounter;
            } else if (this.getPuffState() != 0) {
                if (this.deflateTimer > 60 && this.getPuffState() == 2) {
                    this.playSound(SoundEffects.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.ep());
                    this.setPuffState(1);
                } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
                    this.playSound(SoundEffects.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.ep());
                    this.setPuffState(0);
                }

                ++this.deflateTimer;
            }
        }

        super.tick();
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.isAlive() && this.getPuffState() > 0) {
            List<EntityInsentient> list = this.level.a(EntityInsentient.class, this.getBoundingBox().g(0.3D), (entityinsentient) -> {
                return EntityPufferFish.targetingConditions.a(this, entityinsentient);
            });
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();

                if (entityinsentient.isAlive()) {
                    this.a(entityinsentient);
                }
            }
        }

    }

    private void a(EntityInsentient entityinsentient) {
        int i = this.getPuffState();

        if (entityinsentient.damageEntity(DamageSource.mobAttack(this), (float) (1 + i))) {
            entityinsentient.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0), this);
            this.playSound(SoundEffects.PUFFER_FISH_STING, 1.0F, 1.0F);
        }

    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        int i = this.getPuffState();

        if (entityhuman instanceof EntityPlayer && i > 0 && entityhuman.damageEntity(DamageSource.mobAttack(this), (float) (1 + i))) {
            if (!this.isSilent()) {
                ((EntityPlayer) entityhuman).connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.PUFFER_FISH_STING, 0.0F));
            }

            entityhuman.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0), this);
        }

    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.PUFFER_FISH_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.PUFFER_FISH_HURT;
    }

    @Override
    protected SoundEffect getSoundFlop() {
        return SoundEffects.PUFFER_FISH_FLOP;
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return super.a(entitypose).a(t(this.getPuffState()));
    }

    private static float t(int i) {
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
        public boolean a() {
            List<EntityLiving> list = this.fish.level.a(EntityLiving.class, this.fish.getBoundingBox().g(2.0D), (entityliving) -> {
                return EntityPufferFish.targetingConditions.a(this.fish, entityliving);
            });

            return !list.isEmpty();
        }

        @Override
        public void c() {
            this.fish.inflateCounter = 1;
            this.fish.deflateTimer = 0;
        }

        @Override
        public void d() {
            this.fish.inflateCounter = 0;
        }
    }
}
