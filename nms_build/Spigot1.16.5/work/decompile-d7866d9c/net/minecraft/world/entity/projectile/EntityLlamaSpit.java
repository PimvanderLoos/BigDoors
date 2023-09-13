package net.minecraft.world.entity.projectile;

import net.minecraft.network.protocol.Packet;
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
        super.setShooter(entityllama);
        this.setPosition(entityllama.locX() - (double) (entityllama.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(entityllama.aA * 0.017453292F), entityllama.getHeadY() - 0.10000000149011612D, entityllama.locZ() + (double) (entityllama.getWidth() + 1.0F) * 0.5D * (double) MathHelper.cos(entityllama.aA * 0.017453292F));
    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d = this.getMot();
        MovingObjectPosition movingobjectposition = ProjectileHelper.a((Entity) this, this::a);

        if (movingobjectposition != null) {
            this.a(movingobjectposition);
        }

        double d0 = this.locX() + vec3d.x;
        double d1 = this.locY() + vec3d.y;
        double d2 = this.locZ() + vec3d.z;

        this.x();
        float f = 0.99F;
        float f1 = 0.06F;

        if (this.world.a(this.getBoundingBox()).noneMatch(BlockBase.BlockData::isAir)) {
            this.die();
        } else if (this.aH()) {
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
        if (!this.world.isClientSide) {
            this.die();
        }

    }

    @Override
    protected void initDatawatcher() {}

    @Override
    public Packet<?> P() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
