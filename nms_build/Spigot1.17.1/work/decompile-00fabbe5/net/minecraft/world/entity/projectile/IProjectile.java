package net.minecraft.world.entity.projectile;

import com.google.common.base.MoreObjects;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public abstract class IProjectile extends Entity {

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private boolean leftOwner;
    private boolean hasBeenShot;

    IProjectile(EntityTypes<? extends IProjectile> entitytypes, World world) {
        super(entitytypes, world);
    }

    public void setShooter(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUUID = entity.getUniqueID();
            this.cachedOwner = entity;
        }

    }

    @Nullable
    public Entity getShooter() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof WorldServer) {
            this.cachedOwner = ((WorldServer) this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    public Entity x() {
        return (Entity) MoreObjects.firstNonNull(this.getShooter(), this);
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        if (this.ownerUUID != null) {
            nbttagcompound.a("Owner", this.ownerUUID);
        }

        if (this.leftOwner) {
            nbttagcompound.setBoolean("LeftOwner", true);
        }

        nbttagcompound.setBoolean("HasBeenShot", this.hasBeenShot);
    }

    protected boolean d(Entity entity) {
        return entity.getUniqueID().equals(this.ownerUUID);
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.b("Owner")) {
            this.ownerUUID = nbttagcompound.a("Owner");
        }

        this.leftOwner = nbttagcompound.getBoolean("LeftOwner");
        this.hasBeenShot = nbttagcompound.getBoolean("HasBeenShot");
    }

    @Override
    public void tick() {
        if (!this.hasBeenShot) {
            this.a(GameEvent.PROJECTILE_SHOOT, this.getShooter(), this.getChunkCoordinates());
            this.hasBeenShot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.i();
        }

        super.tick();
    }

    private boolean i() {
        Entity entity = this.getShooter();

        if (entity != null) {
            Iterator iterator = this.level.getEntities(this, this.getBoundingBox().b(this.getMot()).g(1.0D), (entity1) -> {
                return !entity1.isSpectator() && entity1.isInteractable();
            }).iterator();

            while (iterator.hasNext()) {
                Entity entity1 = (Entity) iterator.next();

                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        Vec3D vec3d = (new Vec3D(d0, d1, d2)).d().add(this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1).a((double) f);

        this.setMot(vec3d);
        double d3 = vec3d.h();

        this.setYRot((float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D));
        this.setXRot((float) (MathHelper.d(vec3d.y, d3) * 57.2957763671875D));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void a(Entity entity, float f, float f1, float f2, float f3, float f4) {
        float f5 = -MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
        float f6 = -MathHelper.sin((f + f2) * 0.017453292F);
        float f7 = MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);

        this.shoot((double) f5, (double) f6, (double) f7, f3, f4);
        Vec3D vec3d = entity.getMot();

        this.setMot(this.getMot().add(vec3d.x, entity.isOnGround() ? 0.0D : vec3d.y, vec3d.z));
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        MovingObjectPosition.EnumMovingObjectType movingobjectposition_enummovingobjecttype = movingobjectposition.getType();

        if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
            this.a((MovingObjectPositionEntity) movingobjectposition);
        } else if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            this.a((MovingObjectPositionBlock) movingobjectposition);
        }

        if (movingobjectposition_enummovingobjecttype != MovingObjectPosition.EnumMovingObjectType.MISS) {
            this.a(GameEvent.PROJECTILE_LAND, this.getShooter());
        }

    }

    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {}

    protected void a(MovingObjectPositionBlock movingobjectpositionblock) {
        IBlockData iblockdata = this.level.getType(movingobjectpositionblock.getBlockPosition());

        iblockdata.a(this.level, iblockdata, movingobjectpositionblock, this);
    }

    @Override
    public void k(double d0, double d1, double d2) {
        this.setMot(d0, d1, d2);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);

            this.setXRot((float) (MathHelper.d(d1, d3) * 57.2957763671875D));
            this.setYRot((float) (MathHelper.d(d0, d2) * 57.2957763671875D));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.getYRot(), this.getXRot());
        }

    }

    protected boolean a(Entity entity) {
        if (!entity.isSpectator() && entity.isAlive() && entity.isInteractable()) {
            Entity entity1 = this.getShooter();

            return entity1 == null || this.leftOwner || !entity1.isSameVehicle(entity);
        } else {
            return false;
        }
    }

    protected void z() {
        Vec3D vec3d = this.getMot();
        double d0 = vec3d.h();

        this.setXRot(d(this.xRotO, (float) (MathHelper.d(vec3d.y, d0) * 57.2957763671875D)));
        this.setYRot(d(this.yRotO, (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D)));
    }

    protected static float d(float f, float f1) {
        while (f1 - f < -180.0F) {
            f -= 360.0F;
        }

        while (f1 - f >= 180.0F) {
            f += 360.0F;
        }

        return MathHelper.h(0.2F, f, f1);
    }

    @Override
    public Packet<?> getPacket() {
        Entity entity = this.getShooter();

        return new PacketPlayOutSpawnEntity(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void a(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.a(packetplayoutspawnentity);
        Entity entity = this.level.getEntity(packetplayoutspawnentity.m());

        if (entity != null) {
            this.setShooter(entity);
        }

    }

    @Override
    public boolean a(World world, BlockPosition blockposition) {
        Entity entity = this.getShooter();

        return entity instanceof EntityHuman ? entity.a(world, blockposition) : entity == null || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }
}
