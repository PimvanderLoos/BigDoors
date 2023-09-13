package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorBedJump extends Behavior<EntityInsentient> {

    private static final int MAX_TIME_TO_REACH_BED = 100;
    private static final int MIN_JUMPS = 3;
    private static final int MAX_JUMPS = 6;
    private static final int COOLDOWN_BETWEEN_JUMPS = 5;
    private final float speedModifier;
    @Nullable
    private BlockPosition targetBed;
    private int remainingTimeToReachBed;
    private int remainingJumps;
    private int remainingCooldownUntilNextJump;

    public BehaviorBedJump(float f) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = f;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityInsentient entityinsentient) {
        return entityinsentient.isBaby() && this.nearBed(worldserver, entityinsentient);
    }

    protected void start(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        super.start(worldserver, entityinsentient, i);
        this.getNearestBed(entityinsentient).ifPresent((blockposition) -> {
            this.targetBed = blockposition;
            this.remainingTimeToReachBed = 100;
            this.remainingJumps = 3 + worldserver.random.nextInt(4);
            this.remainingCooldownUntilNextJump = 0;
            this.startWalkingTowardsBed(entityinsentient, blockposition);
        });
    }

    protected void stop(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        super.stop(worldserver, entityinsentient, i);
        this.targetBed = null;
        this.remainingTimeToReachBed = 0;
        this.remainingJumps = 0;
        this.remainingCooldownUntilNextJump = 0;
    }

    protected boolean canStillUse(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        return entityinsentient.isBaby() && this.targetBed != null && this.isBed(worldserver, this.targetBed) && !this.tiredOfWalking(worldserver, entityinsentient) && !this.tiredOfJumping(worldserver, entityinsentient);
    }

    @Override
    protected boolean timedOut(long i) {
        return false;
    }

    protected void tick(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (!this.onOrOverBed(worldserver, entityinsentient)) {
            --this.remainingTimeToReachBed;
        } else if (this.remainingCooldownUntilNextJump > 0) {
            --this.remainingCooldownUntilNextJump;
        } else {
            if (this.onBedSurface(worldserver, entityinsentient)) {
                entityinsentient.getJumpControl().jump();
                --this.remainingJumps;
                this.remainingCooldownUntilNextJump = 5;
            }

        }
    }

    private void startWalkingTowardsBed(EntityInsentient entityinsentient, BlockPosition blockposition) {
        entityinsentient.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(blockposition, this.speedModifier, 0)));
    }

    private boolean nearBed(WorldServer worldserver, EntityInsentient entityinsentient) {
        return this.onOrOverBed(worldserver, entityinsentient) || this.getNearestBed(entityinsentient).isPresent();
    }

    private boolean onOrOverBed(WorldServer worldserver, EntityInsentient entityinsentient) {
        BlockPosition blockposition = entityinsentient.blockPosition();
        BlockPosition blockposition1 = blockposition.below();

        return this.isBed(worldserver, blockposition) || this.isBed(worldserver, blockposition1);
    }

    private boolean onBedSurface(WorldServer worldserver, EntityInsentient entityinsentient) {
        return this.isBed(worldserver, entityinsentient.blockPosition());
    }

    private boolean isBed(WorldServer worldserver, BlockPosition blockposition) {
        return worldserver.getBlockState(blockposition).is(TagsBlock.BEDS);
    }

    private Optional<BlockPosition> getNearestBed(EntityInsentient entityinsentient) {
        return entityinsentient.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
    }

    private boolean tiredOfWalking(WorldServer worldserver, EntityInsentient entityinsentient) {
        return !this.onOrOverBed(worldserver, entityinsentient) && this.remainingTimeToReachBed <= 0;
    }

    private boolean tiredOfJumping(WorldServer worldserver, EntityInsentient entityinsentient) {
        return this.onOrOverBed(worldserver, entityinsentient) && this.remainingJumps <= 0;
    }
}
