package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;

public class EntityThrownExpBottle extends EntityProjectileThrowable {

    public EntityThrownExpBottle(EntityTypes<? extends EntityThrownExpBottle> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityThrownExpBottle(World world, EntityLiving entityliving) {
        super(EntityTypes.EXPERIENCE_BOTTLE, entityliving, world);
    }

    public EntityThrownExpBottle(World world, double d0, double d1, double d2) {
        super(EntityTypes.EXPERIENCE_BOTTLE, d0, d1, d2, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    protected float k() {
        return 0.07F;
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        if (!this.world.isClientSide) {
            this.world.triggerEffect(2002, this.getChunkCoordinates(), PotionUtil.a(Potions.WATER));
            int i = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5);

            while (i > 0) {
                int j = EntityExperienceOrb.getOrbValue(i);

                i -= j;
                this.world.addEntity(new EntityExperienceOrb(this.world, this.locX(), this.locY(), this.locZ(), j));
            }

            this.die();
        }

    }
}
