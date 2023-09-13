package net.minecraft.world.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityBlaze;
import net.minecraft.world.item.Item;
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
        if (!this.world.isClientSide) {
            this.world.broadcastEntityEffect(this, (byte) 3);
            this.die();
        }

    }
}
