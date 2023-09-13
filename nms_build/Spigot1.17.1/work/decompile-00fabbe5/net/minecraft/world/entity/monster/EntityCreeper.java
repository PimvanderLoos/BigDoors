package net.minecraft.world.entity.monster;

import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSwell;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;

public class EntityCreeper extends EntityMonster implements PowerableMob {

    private static final DataWatcherObject<Integer> DATA_SWELL_DIR = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_IS_POWERED = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_IS_IGNITED = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.BOOLEAN);
    private int oldSwell;
    public int swell;
    public int maxSwell = 30;
    public int explosionRadius = 3;
    private int droppedSkulls;

    public EntityCreeper(EntityTypes<? extends EntityCreeper> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSwell(this));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityCat.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
    }

    public static AttributeProvider.Builder n() {
        return EntityMonster.fB().a(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public int ce() {
        return this.getGoalTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        boolean flag = super.a(f, f1, damagesource);

        this.swell = (int) ((float) this.swell + f * 1.5F);
        if (this.swell > this.maxSwell - 5) {
            this.swell = this.maxSwell - 5;
        }

        return flag;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityCreeper.DATA_SWELL_DIR, -1);
        this.entityData.register(EntityCreeper.DATA_IS_POWERED, false);
        this.entityData.register(EntityCreeper.DATA_IS_IGNITED, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if ((Boolean) this.entityData.get(EntityCreeper.DATA_IS_POWERED)) {
            nbttagcompound.setBoolean("powered", true);
        }

        nbttagcompound.setShort("Fuse", (short) this.maxSwell);
        nbttagcompound.setByte("ExplosionRadius", (byte) this.explosionRadius);
        nbttagcompound.setBoolean("ignited", this.isIgnited());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.entityData.set(EntityCreeper.DATA_IS_POWERED, nbttagcompound.getBoolean("powered"));
        if (nbttagcompound.hasKeyOfType("Fuse", 99)) {
            this.maxSwell = nbttagcompound.getShort("Fuse");
        }

        if (nbttagcompound.hasKeyOfType("ExplosionRadius", 99)) {
            this.explosionRadius = nbttagcompound.getByte("ExplosionRadius");
        }

        if (nbttagcompound.getBoolean("ignited")) {
            this.ignite();
        }

    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            if (this.isIgnited()) {
                this.a((int) 1);
            }

            int i = this.p();

            if (i > 0 && this.swell == 0) {
                this.playSound(SoundEffects.CREEPER_PRIMED, 1.0F, 0.5F);
                this.a(GameEvent.PRIME_FUSE);
            }

            this.swell += i;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explode();
            }
        }

        super.tick();
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        if (!(entityliving instanceof Goat)) {
            super.setGoalTarget(entityliving);
        }
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.CREEPER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.CREEPER_DEATH;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        Entity entity = damagesource.getEntity();

        if (entity != this && entity instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) entity;

            if (entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                this.a((IMaterial) Items.CREEPER_HEAD);
            }
        }

    }

    @Override
    public boolean attackEntity(Entity entity) {
        return true;
    }

    @Override
    public boolean isPowered() {
        return (Boolean) this.entityData.get(EntityCreeper.DATA_IS_POWERED);
    }

    public float z(float f) {
        return MathHelper.h(f, (float) this.oldSwell, (float) this.swell) / (float) (this.maxSwell - 2);
    }

    public int p() {
        return (Integer) this.entityData.get(EntityCreeper.DATA_SWELL_DIR);
    }

    public void a(int i) {
        this.entityData.set(EntityCreeper.DATA_SWELL_DIR, i);
    }

    @Override
    public void onLightningStrike(WorldServer worldserver, EntityLightning entitylightning) {
        super.onLightningStrike(worldserver, entitylightning);
        this.entityData.set(EntityCreeper.DATA_IS_POWERED, true);
    }

    @Override
    protected EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.a(Items.FLINT_AND_STEEL)) {
            this.level.playSound(entityhuman, this.locX(), this.locY(), this.locZ(), SoundEffects.FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level.isClientSide) {
                this.ignite();
                itemstack.damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastItemBreak(enumhand);
                });
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    public void explode() {
        if (!this.level.isClientSide) {
            Explosion.Effect explosion_effect = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.Effect.DESTROY : Explosion.Effect.NONE;
            float f = this.isPowered() ? 2.0F : 1.0F;

            this.dead = true;
            this.level.explode(this, this.locX(), this.locY(), this.locZ(), (float) this.explosionRadius * f, explosion_effect);
            this.die();
            this.createEffectCloud();
        }

    }

    private void createEffectCloud() {
        Collection<MobEffect> collection = this.getEffects();

        if (!collection.isEmpty()) {
            EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.level, this.locX(), this.locY(), this.locZ());

            entityareaeffectcloud.setRadius(2.5F);
            entityareaeffectcloud.setRadiusOnUse(-0.5F);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                entityareaeffectcloud.addEffect(new MobEffect(mobeffect));
            }

            this.level.addEntity(entityareaeffectcloud);
        }

    }

    public boolean isIgnited() {
        return (Boolean) this.entityData.get(EntityCreeper.DATA_IS_IGNITED);
    }

    public void ignite() {
        this.entityData.set(EntityCreeper.DATA_IS_IGNITED, true);
    }

    public boolean canCauseHeadDrop() {
        return this.isPowered() && this.droppedSkulls < 1;
    }

    public void setCausedHeadDrop() {
        ++this.droppedSkulls;
    }
}
