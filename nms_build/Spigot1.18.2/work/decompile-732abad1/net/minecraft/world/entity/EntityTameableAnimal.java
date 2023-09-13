package net.minecraft.world.entity;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.NameReferencingFileConverter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.scores.ScoreboardTeamBase;

public abstract class EntityTameableAnimal extends EntityAnimal implements OwnableEntity {

    protected static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.defineId(EntityTameableAnimal.class, DataWatcherRegistry.BYTE);
    protected static final DataWatcherObject<Optional<UUID>> DATA_OWNERUUID_ID = DataWatcher.defineId(EntityTameableAnimal.class, DataWatcherRegistry.OPTIONAL_UUID);
    private boolean orderedToSit;

    protected EntityTameableAnimal(EntityTypes<? extends EntityTameableAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.reassessTameGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityTameableAnimal.DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(EntityTameableAnimal.DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        if (this.getOwnerUUID() != null) {
            nbttagcompound.putUUID("Owner", this.getOwnerUUID());
        }

        nbttagcompound.putBoolean("Sitting", this.orderedToSit);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        UUID uuid;

        if (nbttagcompound.hasUUID("Owner")) {
            uuid = nbttagcompound.getUUID("Owner");
        } else {
            String s = nbttagcompound.getString("Owner");

            uuid = NameReferencingFileConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTame(true);
            } catch (Throwable throwable) {
                this.setTame(false);
            }
        }

        this.orderedToSit = nbttagcompound.getBoolean("Sitting");
        this.setInSittingPose(this.orderedToSit);
    }

    @Override
    public boolean canBeLeashed(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    protected void spawnTamingParticles(boolean flag) {
        ParticleType particletype = Particles.HEART;

        if (!flag) {
            particletype = Particles.SMOKE;
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particletype, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 7) {
            this.spawnTamingParticles(true);
        } else if (b0 == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(b0);
        }

    }

    public boolean isTame() {
        return ((Byte) this.entityData.get(EntityTameableAnimal.DATA_FLAGS_ID) & 4) != 0;
    }

    public void setTame(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityTameableAnimal.DATA_FLAGS_ID);

        if (flag) {
            this.entityData.set(EntityTameableAnimal.DATA_FLAGS_ID, (byte) (b0 | 4));
        } else {
            this.entityData.set(EntityTameableAnimal.DATA_FLAGS_ID, (byte) (b0 & -5));
        }

        this.reassessTameGoals();
    }

    protected void reassessTameGoals() {}

    public boolean isInSittingPose() {
        return ((Byte) this.entityData.get(EntityTameableAnimal.DATA_FLAGS_ID) & 1) != 0;
    }

    public void setInSittingPose(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityTameableAnimal.DATA_FLAGS_ID);

        if (flag) {
            this.entityData.set(EntityTameableAnimal.DATA_FLAGS_ID, (byte) (b0 | 1));
        } else {
            this.entityData.set(EntityTameableAnimal.DATA_FLAGS_ID, (byte) (b0 & -2));
        }

    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(EntityTameableAnimal.DATA_OWNERUUID_ID)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(EntityTameableAnimal.DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    public void tame(EntityHuman entityhuman) {
        this.setTame(true);
        this.setOwnerUUID(entityhuman.getUUID());
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.TAME_ANIMAL.trigger((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

    }

    @Nullable
    @Override
    public EntityLiving getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();

            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Override
    public boolean canAttack(EntityLiving entityliving) {
        return this.isOwnedBy(entityliving) ? false : super.canAttack(entityliving);
    }

    public boolean isOwnedBy(EntityLiving entityliving) {
        return entityliving == this.getOwner();
    }

    public boolean wantsToAttack(EntityLiving entityliving, EntityLiving entityliving1) {
        return true;
    }

    @Override
    public ScoreboardTeamBase getTeam() {
        if (this.isTame()) {
            EntityLiving entityliving = this.getOwner();

            if (entityliving != null) {
                return entityliving.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (this.isTame()) {
            EntityLiving entityliving = this.getOwner();

            if (entity == entityliving) {
                return true;
            }

            if (entityliving != null) {
                return entityliving.isAlliedTo(entity);
            }
        }

        return super.isAlliedTo(entity);
    }

    @Override
    public void die(DamageSource damagesource) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof EntityPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), SystemUtils.NIL_UUID);
        }

        super.die(damagesource);
    }

    public boolean isOrderedToSit() {
        return this.orderedToSit;
    }

    public void setOrderedToSit(boolean flag) {
        this.orderedToSit = flag;
    }
}
