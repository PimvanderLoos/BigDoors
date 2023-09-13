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

    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        Optional<GlobalPos> optional = entitycreature.getBehaviorController().getMemory(this.memoryType);

        return optional.isPresent() && worldserver.getDimensionKey() == ((GlobalPos) optional.get()).getDimensionManager() && ((GlobalPos) optional.get()).getBlockPosition().a((IPosition) entitycreature.getPositionVector(), (double) this.maxDistanceFromPoi);
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (i > this.nextOkStartTime) {
            BehaviorController<?> behaviorcontroller = entitycreature.getBehaviorController();
            Optional<GlobalPos> optional = behaviorcontroller.getMemory(this.memoryType);

            optional.ifPresent((globalpos) -> {
                behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(globalpos.getBlockPosition(), this.speedModifier, this.closeEnoughDist)));
            });
            this.nextOkStartTime = i + 80L;
        }

    }
}
