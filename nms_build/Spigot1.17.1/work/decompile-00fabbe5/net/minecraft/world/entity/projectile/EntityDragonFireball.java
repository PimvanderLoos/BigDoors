package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;

public class EntityDragonFireball extends EntityFireball {

    public static final float SPLASH_RANGE = 4.0F;

    public EntityDragonFireball(EntityTypes<? extends EntityDragonFireball> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityDragonFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(EntityTypes.DRAGON_FIREBALL, entityliving, d0, d1, d2, world);
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        if (movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.ENTITY || !this.d(((MovingObjectPositionEntity) movingobjectposition).getEntity())) {
            if (!this.level.isClientSide) {
                List<EntityLiving> list = this.level.a(EntityLiving.class, this.getBoundingBox().grow(4.0D, 2.0D, 4.0D));
                EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.level, this.locX(), this.locY(), this.locZ());
                Entity entity = this.getShooter();

                if (entity instanceof EntityLiving) {
                    entityareaeffectcloud.setSource((EntityLiving) entity);
                }

                entityareaeffectcloud.setParticle(Particles.DRAGON_BREATH);
                entityareaeffectcloud.setRadius(3.0F);
                entityareaeffectcloud.setDuration(600);
                entityareaeffectcloud.setRadiusPerTick((7.0F - entityareaeffectcloud.getRadius()) / (float) entityareaeffectcloud.getDuration());
                entityareaeffectcloud.addEffect(new MobEffect(MobEffects.HARM, 1, 1));
                if (!list.isEmpty()) {
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityLiving entityliving = (EntityLiving) iterator.next();
                        double d0 = this.f(entityliving);

                        if (d0 < 16.0D) {
                            entityareaeffectcloud.setPosition(entityliving.locX(), entityliving.locY(), entityliving.locZ());
                            break;
                        }
                    }
                }

                this.level.triggerEffect(2006, this.getChunkCoordinates(), this.isSilent() ? -1 : 1);
                this.level.addEntity(entityareaeffectcloud);
                this.die();
            }

        }
    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    protected ParticleParam i() {
        return Particles.DRAGON_BREATH;
    }

    @Override
    protected boolean J_() {
        return false;
    }
}
