package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class BehaviorStrollPosition extends Behavior<EntityCreature> {

    private static final int MIN_TIME_BETWEEN_STROLLS = 180;
    private static final int STROLL_MAX_XZ_DIST = 8;
    private static final int STROLL_MAX_Y_DIST = 6;
    private final MemoryModuleType<GlobalPos> memoryType;
    private long nextOkStartTime;
    private final int maxDistanceFromPoi;
    private final float speedModifier;

    public BehaviorStrollPosition(MemoryModuleType<GlobalPos> memorymoduletype, float f, int i) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.memoryType = memorymoduletype;
        this.speedModifier = f;
        this.maxDistanceFromPoi = i;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        Optional<GlobalPos> optional = entitycreature.getBrain().getMemory(this.memoryType);

        return optional.isPresent() && worldserver.dimension() == ((GlobalPos) optional.get()).dimension() && ((GlobalPos) optional.get()).pos().closerThan((IPosition) entitycreature.position(), (double) this.maxDistanceFromPoi);
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (i > this.nextOkStartTime) {
            Optional<Vec3D> optional = Optional.ofNullable(LandRandomPos.getPos(entitycreature, 8, 6));

            entitycreature.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((vec3d) -> {
                return new MemoryTarget(vec3d, this.speedModifier, 1);
            }));
            this.nextOkStartTime = i + 180L;
        }

    }
}
