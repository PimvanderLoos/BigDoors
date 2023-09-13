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

    private int b;
    private BehaviorCrossbowAttack.BowState c;

    public BehaviorCrossbowAttack() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
        this.c = BehaviorCrossbowAttack.BowState.UNCHARGED;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        EntityLiving entityliving = a(e0);

        return e0.a(Items.CROSSBOW) && BehaviorUtil.c(e0, entityliving) && BehaviorUtil.a(e0, entityliving, 0);
    }

    protected boolean b(WorldServer worldserver, E e0, long i) {
        return e0.getBehaviorController().hasMemory(MemoryModuleType.ATTACK_TARGET) && this.a(worldserver, e0);
    }

    protected void d(WorldServer worldserver, E e0, long i) {
        EntityLiving entityliving = a(e0);

        this.b(e0, entityliving);
        this.a(e0, entityliving);
    }

    protected void c(WorldServer worldserver, E e0, long i) {
        if (e0.isHandRaised()) {
            e0.clearActiveItem();
        }

        if (e0.a(Items.CROSSBOW)) {
            ((ICrossbow) e0).b(false);
            ItemCrossbow.a(e0.getActiveItem(), false);
        }

    }

    private void a(E e0, EntityLiving entityliving) {
        if (this.c == BehaviorCrossbowAttack.BowState.UNCHARGED) {
            e0.c(ProjectileHelper.a((EntityLiving) e0, Items.CROSSBOW));
            this.c = BehaviorCrossbowAttack.BowState.CHARGING;
            ((ICrossbow) e0).b(true);
        } else if (this.c == BehaviorCrossbowAttack.BowState.CHARGING) {
            if (!e0.isHandRaised()) {
                this.c = BehaviorCrossbowAttack.BowState.UNCHARGED;
            }

            int i = e0.ea();
            ItemStack itemstack = e0.getActiveItem();

            if (i >= ItemCrossbow.g(itemstack)) {
                e0.releaseActiveItem();
                this.c = BehaviorCrossbowAttack.BowState.CHARGED;
                this.b = 20 + e0.getRandom().nextInt(20);
                ((ICrossbow) e0).b(false);
            }
        } else if (this.c == BehaviorCrossbowAttack.BowState.CHARGED) {
            --this.b;
            if (this.b == 0) {
                this.c = BehaviorCrossbowAttack.BowState.READY_TO_ATTACK;
            }
        } else if (this.c == BehaviorCrossbowAttack.BowState.READY_TO_ATTACK) {
            ((IRangedEntity) e0).a(entityliving, 1.0F);
            ItemStack itemstack1 = e0.b(ProjectileHelper.a((EntityLiving) e0, Items.CROSSBOW));

            ItemCrossbow.a(itemstack1, false);
            this.c = BehaviorCrossbowAttack.BowState.UNCHARGED;
        }

    }

    private void b(EntityInsentient entityinsentient, EntityLiving entityliving) {
        entityinsentient.getBehaviorController().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
    }

    private static EntityLiving a(EntityLiving entityliving) {
        return (EntityLiving) entityliving.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    static enum BowState {

        UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK;

        private BowState() {}
    }
}
