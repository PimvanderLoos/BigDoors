package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;

public class EntityEvokerFangs extends Entity {

    public static final int ATTACK_DURATION = 20;
    public static final int LIFE_OFFSET = 2;
    public static final int ATTACK_TRIGGER_TICKS = 14;
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks;
    private boolean clientSideAttackStarted;
    @Nullable
    private EntityLiving owner;
    @Nullable
    private UUID ownerUUID;

    public EntityEvokerFangs(EntityTypes<? extends EntityEvokerFangs> entitytypes, World world) {
        super(entitytypes, world);
        this.lifeTicks = 22;
    }

    public EntityEvokerFangs(World world, double d0, double d1, double d2, float f, int i, EntityLiving entityliving) {
        this(EntityTypes.EVOKER_FANGS, world);
        this.warmupDelayTicks = i;
        this.a(entityliving);
        this.setYRot(f * 57.295776F);
        this.setPosition(d0, d1, d2);
    }

    @Override
    protected void initDatawatcher() {}

    public void a(@Nullable EntityLiving entityliving) {
        this.owner = entityliving;
        this.ownerUUID = entityliving == null ? null : entityliving.getUniqueID();
    }

    @Nullable
    public EntityLiving getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof WorldServer) {
            Entity entity = ((WorldServer) this.level).getEntity(this.ownerUUID);

            if (entity instanceof EntityLiving) {
                this.owner = (EntityLiving) entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        this.warmupDelayTicks = nbttagcompound.getInt("Warmup");
        if (nbttagcompound.b("Owner")) {
            this.ownerUUID = nbttagcompound.a("Owner");
        }

    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Warmup", this.warmupDelayTicks);
        if (this.ownerUUID != null) {
            nbttagcompound.a("Owner", this.ownerUUID);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d0 = this.locX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getWidth() * 0.5D;
                        double d1 = this.locY() + 0.05D + this.random.nextDouble();
                        double d2 = this.locZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getWidth() * 0.5D;
                        double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                        double d4 = 0.3D + this.random.nextDouble() * 0.3D;
                        double d5 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;

                        this.level.addParticle(Particles.CRIT, d0, d1 + 1.0D, d2, d3, d4, d5);
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                List<EntityLiving> list = this.level.a(EntityLiving.class, this.getBoundingBox().grow(0.2D, 0.0D, 0.2D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityLiving entityliving = (EntityLiving) iterator.next();

                    this.c(entityliving);
                }
            }

            if (!this.sentSpikeEvent) {
                this.level.broadcastEntityEffect(this, (byte) 4);
                this.sentSpikeEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.die();
            }
        }

    }

    private void c(EntityLiving entityliving) {
        EntityLiving entityliving1 = this.getOwner();

        if (entityliving.isAlive() && !entityliving.isInvulnerable() && entityliving != entityliving1) {
            if (entityliving1 == null) {
                entityliving.damageEntity(DamageSource.MAGIC, 6.0F);
            } else {
                if (entityliving1.p(entityliving)) {
                    return;
                }

                entityliving.damageEntity(DamageSource.c(this, entityliving1), 6.0F);
            }

        }
    }

    @Override
    public void a(byte b0) {
        super.a(b0);
        if (b0 == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level.a(this.locX(), this.locY(), this.locZ(), SoundEffects.EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }

    }

    public float a(float f) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        } else {
            int i = this.lifeTicks - 2;

            return i <= 0 ? 1.0F : 1.0F - ((float) i - f) / 20.0F;
        }
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
