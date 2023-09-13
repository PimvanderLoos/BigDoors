package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorWalkAwayOutOfRange extends Behavior<EntityInsentient> {

    private final float b;

    public BehaviorWalkAwayOutOfRange(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryStatus.REGISTERED));
        this.b = f;
    }

    protected void a(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        EntityLiving entityliving = (EntityLiving) entityinsentient.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET).get();

        if (BehaviorUtil.c(entityinsentient, entityliving) && BehaviorUtil.a(entityinsentient, entityliving, 1)) {
            this.a(entityinsentient);
        } else {
            this.a((EntityLiving) entityinsentient, entityliving);
        }

    }

    private void a(EntityLiving entityliving, EntityLiving entityliving1) {
        BehaviorController behaviorcontroller = entityliving.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving1, true)));
        MemoryTarget memorytarget = new MemoryTarget(new BehaviorPositionEntity(entityliving1, false), this.b, 0);

        behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) memorytarget);
    }

    private void a(EntityLiving entityliving) {
        entityliving.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
    }
}
