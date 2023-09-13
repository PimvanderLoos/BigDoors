package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.npc.EntityVillager;

public class BehaviorStrollPlaceList extends Behavior<EntityVillager> {

    private final MemoryModuleType<List<GlobalPos>> strollToMemoryType;
    private final MemoryModuleType<GlobalPos> mustBeCloseToMemoryType;
    private final float speedModifier;
    private final int closeEnoughDist;
    private final int maxDistanceFromPoi;
    private long nextOkStartTime;
    @Nullable
    private GlobalPos targetPos;

    public BehaviorStrollPlaceList(MemoryModuleType<List<GlobalPos>> memorymoduletype, float f, int i, int j, MemoryModuleType<GlobalPos> memorymoduletype1) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, memorymoduletype, MemoryStatus.VALUE_PRESENT, memorymoduletype1, MemoryStatus.VALUE_PRESENT));
        this.strollToMemoryType = memorymoduletype;
        this.speedModifier = f;
        this.closeEnoughDist = i;
        this.maxDistanceFromPoi = j;
        this.mustBeCloseToMemoryType = memorymoduletype1;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        Optional<List<GlobalPos>> optional = entityvillager.getBrain().getMemory(this.strollToMemoryType);
        Optional<GlobalPos> optional1 = entityvillager.getBrain().getMemory(this.mustBeCloseToMemoryType);

        if (optional.isPresent() && optional1.isPresent()) {
            List<GlobalPos> list = (List) optional.get();

            if (!list.isEmpty()) {
                this.targetPos = (GlobalPos) list.get(worldserver.getRandom().nextInt(list.size()));
                return this.targetPos != null && worldserver.dimension() == this.targetPos.dimension() && ((GlobalPos) optional1.get()).pos().closerThan((IPosition) entityvillager.position(), (double) this.maxDistanceFromPoi);
            }
        }

        return false;
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if (i > this.nextOkStartTime && this.targetPos != null) {
            entityvillager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(this.targetPos.pos(), this.speedModifier, this.closeEnoughDist)));
            this.nextOkStartTime = i + 100L;
        }

    }
}
