package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.ICrossbow;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.ItemCrossbow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BehaviorCrossbowAttack<E extends EntityInsentient & ICrossbow, T extends EntityLiving> extends Behavior<E> {

    private static final int TIMEOUT = 1200;
    private int attackDelay;
    private BehaviorCrossbowAttack.BowState crossbowState;

    public BehaviorCrossbowAttack() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
        this.crossbowState = BehaviorCrossbowAttack.BowState.UNCHARGED;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        EntityLiving entityliving = getAttackTarget(e0);

        return e0.isHolding(Items.CROSSBOW) && BehaviorUtil.canSee(e0, entityliving) && BehaviorUtil.isWithinAttackRange(e0, entityliving, 0);
    }

    protected boolean canStillUse(WorldServer worldserver, E e0, long i) {
        return e0.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(worldserver, e0);
    }

    protected void tick(WorldServer worldserver, E e0, long i) {
        EntityLiving entityliving = getAttackTarget(e0);

        this.lookAtTarget(e0, entityliving);
        this.crossbowAttack(e0, entityliving);
    }

    protected void stop(WorldServer worldserver, E e0, long i) {
        if (e0.isUsingItem()) {
            e0.stopUsingItem();
        }

        if (e0.isHolding(Items.CROSSBOW)) {
            ((ICrossbow) e0).setChargingCrossbow(false);
            ItemCrossbow.setCharged(e0.getUseItem(), false);
        }

    }

    private void crossbowAttack(E e0, EntityLiving entityliving) {
        if (this.crossbowState == BehaviorCrossbowAttack.BowState.UNCHARGED) {
            e0.startUsingItem(ProjectileHelper.getWeaponHoldingHand(e0, Items.CROSSBOW));
            this.crossbowState = BehaviorCrossbowAttack.BowState.CHARGING;
            ((ICrossbow) e0).setChargingCrossbow(true);
        } else if (this.crossbowState == BehaviorCrossbowAttack.BowState.CHARGING) {
            if (!e0.isUsingItem()) {
                this.crossbowState = BehaviorCrossbowAttack.BowState.UNCHARGED;
            }

            int i = e0.getTicksUsingItem();
            ItemStack itemstack = e0.getUseItem();

            if (i >= ItemCrossbow.getChargeDuration(itemstack)) {
                e0.releaseUsingItem();
                this.crossbowState = BehaviorCrossbowAttack.BowState.CHARGED;
                this.attackDelay = 20 + e0.getRandom().nextInt(20);
                ((ICrossbow) e0).setChargingCrossbow(false);
            }
        } else if (this.crossbowState == BehaviorCrossbowAttack.BowState.CHARGED) {
            --this.attackDelay;
            if (this.attackDelay == 0) {
                this.crossbowState = BehaviorCrossbowAttack.BowState.READY_TO_ATTACK;
            }
        } else if (this.crossbowState == BehaviorCrossbowAttack.BowState.READY_TO_ATTACK) {
            ((IRangedEntity) e0).performRangedAttack(entityliving, 1.0F);
            ItemStack itemstack1 = e0.getItemInHand(ProjectileHelper.getWeaponHoldingHand(e0, Items.CROSSBOW));

            ItemCrossbow.setCharged(itemstack1, false);
            this.crossbowState = BehaviorCrossbowAttack.BowState.UNCHARGED;
        }

    }

    private void lookAtTarget(EntityInsentient entityinsentient, EntityLiving entityliving) {
        entityinsentient.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
    }

    private static EntityLiving getAttackTarget(EntityLiving entityliving) {
        return (EntityLiving) entityliving.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static enum BowState {

        UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK;

        private BowState() {}
    }
}
