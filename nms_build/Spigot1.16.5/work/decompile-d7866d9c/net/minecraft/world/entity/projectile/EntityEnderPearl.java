package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityEndermite;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;

public class EntityEnderPearl extends EntityProjectileThrowable {

    public EntityEnderPearl(EntityTypes<? extends EntityEnderPearl> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(EntityTypes.ENDER_PEARL, entityliving, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        movingobjectpositionentity.getEntity().damageEntity(DamageSource.projectile(this, this.getShooter()), 0.0F);
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        Entity entity = this.getShooter();

        for (int i = 0; i < 32; ++i) {
            this.world.addParticle(Particles.PORTAL, this.locX(), this.locY() + this.random.nextDouble() * 2.0D, this.locZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
        }

        if (!this.world.isClientSide && !this.dead) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entity;

                if (entityplayer.playerConnection.a().isConnected() && entityplayer.world == this.world && !entityplayer.isSleeping()) {
                    if (this.random.nextFloat() < 0.05F && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                        EntityEndermite entityendermite = (EntityEndermite) EntityTypes.ENDERMITE.a(this.world);

                        entityendermite.setPlayerSpawned(true);
                        entityendermite.setPositionRotation(entity.locX(), entity.locY(), entity.locZ(), entity.yaw, entity.pitch);
                        this.world.addEntity(entityendermite);
                    }

                    if (entity.isPassenger()) {
                        entity.stopRiding();
                    }

                    entity.enderTeleportTo(this.locX(), this.locY(), this.locZ());
                    entity.fallDistance = 0.0F;
                    entity.damageEntity(DamageSource.FALL, 5.0F);
                }
            } else if (entity != null) {
                entity.enderTeleportTo(this.locX(), this.locY(), this.locZ());
                entity.fallDistance = 0.0F;
            }

            this.die();
        }

    }

    @Override
    public void tick() {
        Entity entity = this.getShooter();

        if (entity instanceof EntityHuman && !entity.isAlive()) {
            this.die();
        } else {
            super.tick();
        }

    }

    @Nullable
    @Override
    public Entity b(WorldServer worldserver) {
        Entity entity = this.getShooter();

        if (entity != null && entity.world.getDimensionKey() != worldserver.getDimensionKey()) {
            this.setShooter((Entity) null);
        }

        return super.b(worldserver);
    }
}
