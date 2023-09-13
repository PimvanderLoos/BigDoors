package net.minecraft.world.entity.vehicle;

import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class EntityMinecartRideable extends EntityMinecartAbstract {

    public EntityMinecartRideable(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityMinecartRideable(World world, double d0, double d1, double d2) {
        super(EntityTypes.MINECART, world, d0, d1, d2);
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        return entityhuman.eq() ? EnumInteractionResult.PASS : (this.isVehicle() ? EnumInteractionResult.PASS : (!this.world.isClientSide ? (entityhuman.startRiding(this) ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS) : EnumInteractionResult.SUCCESS));
    }

    @Override
    public void a(int i, int j, int k, boolean flag) {
        if (flag) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }

            if (this.getType() == 0) {
                this.d(-this.n());
                this.c(10);
                this.setDamage(50.0F);
                this.velocityChanged();
            }
        }

    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.RIDEABLE;
    }
}
