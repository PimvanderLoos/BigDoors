package net.minecraft.server;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityTameableAnimal extends EntityAnimal implements EntityOwnable {

    protected static final DataWatcherObject<Byte> bC = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Optional<UUID>> bD = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.o);
    protected PathfinderGoalSit goalSit;

    protected EntityTameableAnimal(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.dz();
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityTameableAnimal.bC, (byte) 0);
        this.datawatcher.register(EntityTameableAnimal.bD, Optional.empty());
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.getOwnerUUID() == null) {
            nbttagcompound.setString("OwnerUUID", "");
        } else {
            nbttagcompound.setString("OwnerUUID", this.getOwnerUUID().toString());
        }

        nbttagcompound.setBoolean("Sitting", this.isSitting());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        String s;

        if (nbttagcompound.hasKeyOfType("OwnerUUID", 8)) {
            s = nbttagcompound.getString("OwnerUUID");
        } else {
            String s1 = nbttagcompound.getString("Owner");

            s = NameReferencingFileConverter.a(this.bK(), s1);
        }

        if (!s.isEmpty()) {
            try {
                this.setOwnerUUID(UUID.fromString(s));
                this.setTamed(true);
            } catch (Throwable throwable) {
                this.setTamed(false);
            }
        }

        if (this.goalSit != null) {
            this.goalSit.setSitting(nbttagcompound.getBoolean("Sitting"));
        }

        this.setSitting(nbttagcompound.getBoolean("Sitting"));
    }

    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    protected void s(boolean flag) {
        ParticleType particletype = Particles.A;

        if (!flag) {
            particletype = Particles.M;
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.world.addParticle(particletype, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
        }

    }

    public boolean isTamed() {
        return ((Byte) this.datawatcher.get(EntityTameableAnimal.bC) & 4) != 0;
    }

    public void setTamed(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityTameableAnimal.bC);

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bC, (byte) (b0 | 4));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bC, (byte) (b0 & -5));
        }

        this.dz();
    }

    protected void dz() {}

    public boolean isSitting() {
        return ((Byte) this.datawatcher.get(EntityTameableAnimal.bC) & 1) != 0;
    }

    public void setSitting(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityTameableAnimal.bC);

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bC, (byte) (b0 | 1));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bC, (byte) (b0 & -2));
        }

    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.datawatcher.get(EntityTameableAnimal.bD)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.datawatcher.set(EntityTameableAnimal.bD, Optional.ofNullable(uuid));
    }

    public void c(EntityHuman entityhuman) {
        this.setTamed(true);
        this.setOwnerUUID(entityhuman.getUniqueID());
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.x.a((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

    }

    @Nullable
    public EntityLiving getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();

            return uuid == null ? null : this.world.b(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    public boolean f(EntityLiving entityliving) {
        return entityliving == this.getOwner();
    }

    public PathfinderGoalSit getGoalSit() {
        return this.goalSit;
    }

    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return true;
    }

    public ScoreboardTeamBase getScoreboardTeam() {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entityliving != null) {
                return entityliving.getScoreboardTeam();
            }
        }

        return super.getScoreboardTeam();
    }

    public boolean r(Entity entity) {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entity == entityliving) {
                return true;
            }

            if (entityliving != null) {
                return entityliving.r(entity);
            }
        }

        return super.r(entity);
    }

    public void die(DamageSource damagesource) {
        if (!this.world.isClientSide && this.world.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof EntityPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
        }

        super.die(damagesource);
    }
}
