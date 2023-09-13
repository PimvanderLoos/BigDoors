package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorStrollPlace extends Behavior<EntityCreature> {

    private final MemoryModuleType<GlobalPos> memoryType;
    private final int closeEnoughDist;
    private final int maxDistanceFromPoi;
    private final float speedModifier;
    private long nextOkStartTime;

    public BehaviorStrollPlace(MemoryModuleType<GlobalPos> memorymoduletype, float f, int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.memoryType = memorymoduletype;
        this.speedModifier = f;
        this.closeEnoughDist = i;
        this.maxDistanceFromPoi = j;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        Optional<GlobalPos> optional = entitycreature.getBrain().getMemory(this.memoryType);

        return optional.isPresent() && worldserver.dimension() == ((GlobalPos) optional.get()).dimension() && ((GlobalPos) optional.get()).pos().closerThan((IPosition) entitycreature.position(), (double) this.maxDistanceFromPoi);
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (i > this.nextOkStartTime) {
            BehaviorController<?> behaviorcontroller = entitycreature.getBrain();
            Optional<GlobalPos> optional = behaviorcontroller.getMemory(this.memoryType);

            optional.ifPresent((globalpos) -> {
                behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(globalpos.pos(), this.speedModifier, this.closeEnoughDist)));
            });
            this.nextOkStartTime = i + 80L;
        }

    }
}
