package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityBlaze;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;

public class EntitySnowball extends EntityProjectileThrowable {

    public EntitySnowball(EntityTypes<? extends EntitySnowball> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntitySnowball(World world, EntityLiving entityliving) {
        super(EntityTypes.SNOWBALL, entityliving, world);
    }

    public EntitySnowball(World world, double d0, double d1, double d2) {
        super(EntityTypes.SNOWBALL, d0, d1, d2, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    private ParticleParam n() {
        ItemStack itemstack = this.getItem();

        return (ParticleParam) (itemstack.isEmpty() ? Particles.ITEM_SNOWBALL : new ParticleParamItem(Particles.ITEM, itemstack));
    }

    @Override
    public void a(byte b0) {
        if (b0 == 3) {
            ParticleParam particleparam = this.n();

            for (int i = 0; i < 8; ++i) {
                this.level.addParticle(particleparam, this.locX(), this.locY(), this.locZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        Entity entity = movingobjectpositionentity.getEntity();
        int i = entity instanceof EntityBlaze ? 3 : 0;

        entity.damageEntity(DamageSource.projectile(this, this.getShooter()), (float) i);
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEffect(this, (byte) 3);
            this.die();
        }

    }
}
