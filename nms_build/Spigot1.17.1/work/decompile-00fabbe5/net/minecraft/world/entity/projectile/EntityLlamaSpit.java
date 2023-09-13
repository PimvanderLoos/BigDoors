package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.Particles;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.horse.EntityLlama;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public class EntityLlamaSpit extends IProjectile {

    public EntityLlamaSpit(EntityTypes<? extends EntityLlamaSpit> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityLlamaSpit(World world, EntityLlama entityllama) {
        this(EntityTypes.LLAMA_SPIT, world);
        this.setShooter(entityllama);
        this.setPosition(entityllama.locX() - (double) (entityllama.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(entityllama.yBodyRot * 0.017453292F), entityllama.getHeadY() - 0.10000000149011612D, entityllama.locZ() + (double) (entityllama.getWidth() + 1.0F) * 0.5D * (double) MathHelper.cos(entityllama.yBodyRot * 0.017453292F));
    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d = this.getMot();
        MovingObjectPosition movingobjectposition = ProjectileHelper.a((Entity) this, this::a);

        this.a(movingobjectposition);
        double d0 = this.locX() + vec3d.x;
        double d1 = this.locY() + vec3d.y;
        double d2 = this.locZ() + vec3d.z;

        this.z();
        float f = 0.99F;
        float f1 = 0.06F;

        if (this.level.a(this.getBoundingBox()).noneMatch(BlockBase.BlockData::isAir)) {
            this.die();
        } else if (this.aO()) {
            this.die();
        } else {
            this.setMot(vec3d.a(0.9900000095367432D));
            if (!this.isNoGravity()) {
                this.setMot(this.getMot().add(0.0D, -0.05999999865889549D, 0.0D));
            }

            this.setPosition(d0, d1, d2);
        }
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        Entity entity = this.getShooter();

        if (entity instanceof EntityLiving) {
            movingobjectpositionentity.getEntity().damageEntity(DamageSource.a((Entity) this, (EntityLiving) entity).c(), 1.0F);
        }

    }

    @Override
    protected void a(MovingObjectPositionBlock movingobjectpositionblock) {
        super.a(movingobjectpositionblock);
        if (!this.level.isClientSide) {
            this.die();
        }

    }

    @Override
    protected void initDatawatcher() {}

    @Override
    public void a(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.a(packetplayoutspawnentity);
        double d0 = packetplayoutspawnentity.g();
        double d1 = packetplayoutspawnentity.h();
        double d2 = packetplayoutspawnentity.i();

        for (int i = 0; i < 7; ++i) {
            double d3 = 0.4D + 0.1D * (double) i;

            this.level.addParticle(Particles.SPIT, this.locX(), this.locY(), this.locZ(), d0 * d3, d1, d2 * d3);
        }

        this.setMot(d0, d1, d2);
    }
}
