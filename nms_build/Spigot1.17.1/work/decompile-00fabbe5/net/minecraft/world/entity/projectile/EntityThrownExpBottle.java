package net.minecraft.world.entity.projectile;

import net.minecraft.server.level.WorldServer;
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
    protected float l() {
        return 0.07F;
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        if (this.level instanceof WorldServer) {
            this.level.triggerEffect(2002, this.getChunkCoordinates(), PotionUtil.a(Potions.WATER));
            int i = 3 + this.level.random.nextInt(5) + this.level.random.nextInt(5);

            EntityExperienceOrb.a((WorldServer) this.level, this.getPositionVector(), i);
            this.die();
        }

    }
}
