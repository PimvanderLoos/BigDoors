package net.minecraft.server;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityTameableAnimal extends EntityAnimal {

    protected static final DataWatcherObject<Byte> bw = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Optional<UUID>> bx = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.o);
    protected PathfinderGoalSit goalSit;

    protected EntityTameableAnimal(EntityTypes<? extends EntityTameableAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.er();
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityTameableAnimal.bw, (byte) 0);
        this.datawatcher.register(EntityTameableAnimal.bx, Optional.empty());
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.getOwnerUUID() == null) {
            nbttagcompound.setString("OwnerUUID", "");
        } else {
            nbttagcompound.setString("OwnerUUID", this.getOwnerUUID().toString());
        }

        nbttagcompound.setBoolean("Sitting", this.isSitting());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        String s;

        if (nbttagcompound.hasKeyOfType("OwnerUUID", 8)) {
            s = nbttagcompound.getString("OwnerUUID");
        } else {
            String s1 = nbttagcompound.getString("Owner");

            s = NameReferencingFileConverter.a(this.getMinecraftServer(), s1);
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

    @Override
    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    public boolean isTamed() {
        return ((Byte) this.datawatcher.get(EntityTameableAnimal.bw) & 4) != 0;
    }

    public void setTamed(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityTameableAnimal.bw);

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bw, (byte) (b0 | 4));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bw, (byte) (b0 & -5));
        }

        this.er();
    }

    protected void er() {}

    public boolean isSitting() {
        return ((Byte) this.datawatcher.get(EntityTameableAnimal.bw) & 1) != 0;
    }

    public void setSitting(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityTameableAnimal.bw);

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bw, (byte) (b0 | 1));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bw, (byte) (b0 & -2));
        }

    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.datawatcher.get(EntityTameableAnimal.bx)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.datawatcher.set(EntityTameableAnimal.bx, Optional.ofNullable(uuid));
    }

    public void tame(EntityHuman entityhuman) {
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

    @Override
    public boolean c(EntityLiving entityliving) {
        return this.i(entityliving) ? false : super.c(entityliving);
    }

    public boolean i(EntityLiving entityliving) {
        return entityliving == this.getOwner();
    }

    public PathfinderGoalSit getGoalSit() {
        return this.goalSit;
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

    @Override
    public void die(DamageSource damagesource) {
        if (!this.world.isClientSide && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof EntityPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
        }

        super.die(damagesource);
    }
}
