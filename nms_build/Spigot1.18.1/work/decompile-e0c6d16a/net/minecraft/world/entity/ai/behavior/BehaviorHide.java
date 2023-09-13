package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorHide extends Behavior<EntityLiving> {

    private static final int HIDE_TIMEOUT = 300;
    private final int closeEnoughDist;
    private final int stayHiddenTicks;
    private int ticksHidden;

    public BehaviorHide(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
        this.stayHiddenTicks = i * 20;
        this.ticksHidden = 0;
        this.closeEnoughDist = j;
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        Optional<Long> optional = behaviorcontroller.getMemory(MemoryModuleType.HEARD_BELL_TIME);
        boolean flag = (Long) optional.get() + 300L <= i;

        if (this.ticksHidden <= this.stayHiddenTicks && !flag) {
            BlockPosition blockposition = ((GlobalPos) behaviorcontroller.getMemory(MemoryModuleType.HIDING_PLACE).get()).pos();

            if (blockposition.closerThan((BaseBlockPosition) entityliving.blockPosition(), (double) this.closeEnoughDist)) {
                ++this.ticksHidden;
            }

        } else {
            behaviorcontroller.eraseMemory(MemoryModuleType.HEARD_BELL_TIME);
            behaviorcontroller.eraseMemory(MemoryModuleType.HIDING_PLACE);
            behaviorcontroller.updateActivityFromSchedule(worldserver.getDayTime(), worldserver.getGameTime());
            this.ticksHidden = 0;
        }
    }
}
