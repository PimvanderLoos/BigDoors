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
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        return entityhuman.isSecondaryUseActive() ? EnumInteractionResult.PASS : (this.isVehicle() ? EnumInteractionResult.PASS : (!this.level.isClientSide ? (entityhuman.startRiding(this) ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS) : EnumInteractionResult.SUCCESS));
    }

    @Override
    public void activateMinecart(int i, int j, int k, boolean flag) {
        if (flag) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }

            if (this.getHurtTime() == 0) {
                this.setHurtDir(-this.getHurtDir());
                this.setHurtTime(10);
                this.setDamage(50.0F);
                this.markHurt();
            }
        }

    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.RIDEABLE;
    }
}
