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

    protected static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.BYTE);
    protected static final DataWatcherObject<Optional<UUID>> DATA_OWNERUUID_ID = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.OPTIONAL_UUID);
    private boolean orderedToSit;

    protected EntityTameableAnimal(EntityTypes<? extends EntityTameableAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.t();
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityTameableAnimal.DATA_FLAGS_ID, (byte) 0);
        this.entityData.register(EntityTameableAnimal.DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.getOwnerUUID() != null) {
            nbttagcompound.a("Owner", this.getOwnerUUID());
        }

        nbttagcompound.setBoolean("Sitting", this.orderedToSit);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        UUID uuid;

        if (nbttagcompound.b("Owner")) {
            uuid = nbttagcompound.a("Owner");
        } else {
            String s = nbttagcompound.getString("Owner");

            uuid = NameReferencingFileConverter.a(this.getMinecraftServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTamed(true);
            } catch (Throwable throwable) {
                this.setTamed(false);
            }
        }

        this.orderedToSit = nbttagcompound.getBoolean("Sitting");
        this.setSitting(this.orderedToSit);
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    protected void v(boolean flag) {
        ParticleType particletype = Particles.HEART;

        if (!flag) {
            particletype = Particles.SMOKE;
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particletype, this.d(1.0D), this.da() + 0.5D, this.g(1.0D), d0, d1, d2);
        }

    }

    @Override
    public void a(byte b0) {
        if (b0 == 7) {
            this.v(true);
        } else if (b0 == 6) {
            this.v(false);
        } else {
            super.a(b0);
        }

    }

    public boolean isTamed() {
        return ((Byte) this.entityData.get(EntityTameableAnimal.DATA_FLAGS_ID) & 4) != 0;
    }

    public void setTamed(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityTameableAnimal.DATA_FLAGS_ID);

        if (flag) {
            this.entityData.set(EntityTameableAnimal.DATA_FLAGS_ID, (byte) (b0 | 4));
        } else {
            this.entityData.set(EntityTameableAnimal.DATA_FLAGS_ID, (byte) (b0 & -5));
        }

        this.t();
    }

    protected void t() {}

    public boolean isSitting() {
        return ((Byte) this.entityData.get(EntityTameableAnimal.DATA_FLAGS_ID) & 1) != 0;
    }

    public void setSitting(boolean flag) {
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
        this.setTamed(true);
        this.setOwnerUUID(entityhuman.getUniqueID());
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.TAME_ANIMAL.a((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

    }

    @Nullable
    @Override
    public EntityLiving getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();

            return uuid == null ? null : this.level.b(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Override
    public boolean c(EntityLiving entityliving) {
        return this.j(entityliving) ? false : super.c(entityliving);
    }

    public boolean j(EntityLiving entityliving) {
        return entityliving == this.getOwner();
    }

    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return true;
    }

    @Override
    public ScoreboardTeamBase getScoreboardTeam() {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entityliving != null) {
                return entityliving.getScoreboardTeam();
            }
        }

        return super.getScoreboardTeam();
    }

    @Override
    public boolean p(Entity entity) {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entity == entityliving) {
                return true;
            }

            if (entityliving != null) {
                return entityliving.p(entity);
            }
        }

        return super.p(entity);
    }

    @Override
    public void die(DamageSource damagesource) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof EntityPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), SystemUtils.NIL_UUID);
        }

        super.die(damagesource);
    }

    public boolean isWillSit() {
        return this.orderedToSit;
    }

    public void setWillSit(boolean flag) {
        this.orderedToSit = flag;
    }
}
