package net.minecraft.server;

import com.google.common.base.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityTameableAnimal extends EntityAnimal implements EntityOwnable {

    protected static final DataWatcherObject<Byte> bx = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Optional<UUID>> by = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.m);
    protected PathfinderGoalSit goalSit;

    public EntityTameableAnimal(World world) {
        super(world);
        this.dm();
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityTameableAnimal.bx, Byte.valueOf((byte) 0));
        this.datawatcher.register(EntityTameableAnimal.by, Optional.absent());
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

            s = NameReferencingFileConverter.a(this.C_(), s1);
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

    protected void p(boolean flag) {
        EnumParticle enumparticle = EnumParticle.HEART;

        if (!flag) {
            enumparticle = EnumParticle.SMOKE_NORMAL;
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.world.addParticle(enumparticle, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2, new int[0]);
        }

    }

    public boolean isTamed() {
        return (((Byte) this.datawatcher.get(EntityTameableAnimal.bx)).byteValue() & 4) != 0;
    }

    public void setTamed(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityTameableAnimal.bx)).byteValue();

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bx, Byte.valueOf((byte) (b0 | 4)));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bx, Byte.valueOf((byte) (b0 & -5)));
        }

        this.dm();
    }

    protected void dm() {}

    public boolean isSitting() {
        return (((Byte) this.datawatcher.get(EntityTameableAnimal.bx)).byteValue() & 1) != 0;
    }

    public void setSitting(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityTameableAnimal.bx)).byteValue();

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bx, Byte.valueOf((byte) (b0 | 1)));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bx, Byte.valueOf((byte) (b0 & -2)));
        }

    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.datawatcher.get(EntityTameableAnimal.by)).orNull();
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.datawatcher.set(EntityTameableAnimal.by, Optional.fromNullable(uuid));
    }

    public void c(EntityHuman entityhuman) {
        this.setTamed(true);
        this.setOwnerUUID(entityhuman.getUniqueID());
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.w.a((EntityPlayer) entityhuman, (EntityAnimal) this);
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

    public boolean e(EntityLiving entityliving) {
        return entityliving == this.getOwner();
    }

    public PathfinderGoalSit getGoalSit() {
        return this.goalSit;
    }

    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return true;
    }

    public ScoreboardTeamBase aY() {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entityliving != null) {
                return entityliving.aY();
            }
        }

        return super.aY();
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

    @Nullable
    public Entity getOwner() {
        return this.getOwner();
    }
}
