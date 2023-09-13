package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;

public class BehaviorHuntHoglin<E extends EntityPiglin> extends Behavior<E> {

    public BehaviorHuntHoglin() {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryStatus.REGISTERED));
    }

    protected boolean a(WorldServer worldserver, EntityPiglin entitypiglin) {
        return !entitypiglin.isBaby() && !PiglinAI.e(entitypiglin);
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        EntityHoglin entityhoglin = (EntityHoglin) e0.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();

        PiglinAI.c((EntityPiglinAbstract) e0, (EntityLiving) entityhoglin);
        PiglinAI.c((EntityPiglinAbstract) e0);
        PiglinAI.b((EntityPiglinAbstract) e0, (EntityLiving) entityhoglin);
        PiglinAI.f(e0);
    }
}
