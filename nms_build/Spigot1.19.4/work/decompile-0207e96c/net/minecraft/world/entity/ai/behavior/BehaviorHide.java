package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.apache.commons.lang3.mutable.MutableInt;

public class BehaviorHide {

    private static final int HIDE_TIMEOUT = 300;

    public BehaviorHide() {}

    public static BehaviorControl<EntityLiving> create(int i, int j) {
        int k = i * 20;
        MutableInt mutableint = new MutableInt(0);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.HIDING_PLACE), behaviorbuilder_b.present(MemoryModuleType.HEARD_BELL_TIME)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, l) -> {
                    long i1 = (Long) behaviorbuilder_b.get(memoryaccessor1);
                    boolean flag = i1 + 300L <= l;

                    if (mutableint.getValue() <= k && !flag) {
                        BlockPosition blockposition = ((GlobalPos) behaviorbuilder_b.get(memoryaccessor)).pos();

                        if (blockposition.closerThan(entityliving.blockPosition(), (double) j)) {
                            mutableint.increment();
                        }

                        return true;
                    } else {
                        memoryaccessor1.erase();
                        memoryaccessor.erase();
                        entityliving.getBrain().updateActivityFromSchedule(worldserver.getDayTime(), worldserver.getGameTime());
                        mutableint.setValue(0);
                        return true;
                    }
                };
            });
        });
    }
}
