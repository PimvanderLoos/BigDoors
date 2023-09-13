package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;

public class BehavorMove extends Behavior<EntityInsentient> {

    private static final int MAX_COOLDOWN_BEFORE_RETRYING = 40;
    private int remainingCooldown;
    @Nullable
    private PathEntity path;
    @Nullable
    private BlockPosition lastTargetPos;
    private float speedModifier;

    public BehavorMove() {
        this(150, 250);
    }

    public BehavorMove(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), i, j);
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityInsentient entityinsentient) {
        if (this.remainingCooldown > 0) {
            --this.remainingCooldown;
            return false;
        } else {
            BehaviorController<?> behaviorcontroller = entityinsentient.getBrain();
            MemoryTarget memorytarget = (MemoryTarget) behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET).get();
            boolean flag = this.reachedTarget(entityinsentient, memorytarget);

            if (!flag && this.tryComputePath(entityinsentient, memorytarget, worldserver.getGameTime())) {
                this.lastTargetPos = memorytarget.getTarget().currentBlockPosition();
                return true;
            } else {
                behaviorcontroller.eraseMemory(MemoryModuleType.WALK_TARGET);
                if (flag) {
                    behaviorcontroller.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                }

                return false;
            }
        }
    }

    protected boolean canStillUse(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (this.path != null && this.lastTargetPos != null) {
            Optional<MemoryTarget> optional = entityinsentient.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
            NavigationAbstract navigationabstract = entityinsentient.getNavigation();

            return !navigationabstract.isDone() && optional.isPresent() && !this.reachedTarget(entityinsentient, (MemoryTarget) optional.get());
        } else {
            return false;
        }
    }

    protected void stop(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (entityinsentient.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET) && !this.reachedTarget(entityinsentient, (MemoryTarget) entityinsentient.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get()) && entityinsentient.getNavigation().isStuck()) {
            this.remainingCooldown = worldserver.getRandom().nextInt(40);
        }

        entityinsentient.getNavigation().stop();
        entityinsentient.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entityinsentient.getBrain().eraseMemory(MemoryModuleType.PATH);
        this.path = null;
    }

    protected void start(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getBrain().setMemory(MemoryModuleType.PATH, (Object) this.path);
        entityinsentient.getNavigation().moveTo(this.path, (double) this.speedModifier);
    }

    protected void tick(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        PathEntity pathentity = entityinsentient.getNavigation().getPath();
        BehaviorController<?> behaviorcontroller = entityinsentient.getBrain();

        if (this.path != pathentity) {
            this.path = pathentity;
            behaviorcontroller.setMemory(MemoryModuleType.PATH, (Object) pathentity);
        }

        if (pathentity != null && this.lastTargetPos != null) {
            MemoryTarget memorytarget = (MemoryTarget) behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET).get();

            if (memorytarget.getTarget().currentBlockPosition().distSqr(this.lastTargetPos) > 4.0D && this.tryComputePath(entityinsentient, memorytarget, worldserver.getGameTime())) {
                this.lastTargetPos = memorytarget.getTarget().currentBlockPosition();
                this.start(worldserver, entityinsentient, i);
            }

        }
    }

    private boolean tryComputePath(EntityInsentient entityinsentient, MemoryTarget memorytarget, long i) {
        BlockPosition blockposition = memorytarget.getTarget().currentBlockPosition();

        this.path = entityinsentient.getNavigation().createPath(blockposition, 0);
        this.speedModifier = memorytarget.getSpeedModifier();
        BehaviorController<?> behaviorcontroller = entityinsentient.getBrain();

        if (this.reachedTarget(entityinsentient, memorytarget)) {
            behaviorcontroller.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        } else {
            boolean flag = this.path != null && this.path.canReach();

            if (flag) {
                behaviorcontroller.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (!behaviorcontroller.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                behaviorcontroller.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object) i);
            }

            if (this.path != null) {
                return true;
            }

            Vec3D vec3d = DefaultRandomPos.getPosTowards((EntityCreature) entityinsentient, 10, 7, Vec3D.atBottomCenterOf(blockposition), 1.5707963705062866D);

            if (vec3d != null) {
                this.path = entityinsentient.getNavigation().createPath(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.path != null;
            }
        }

        return false;
    }

    private boolean reachedTarget(EntityInsentient entityinsentient, MemoryTarget memorytarget) {
        return memorytarget.getTarget().currentBlockPosition().distManhattan(entityinsentient.blockPosition()) <= memorytarget.getCloseEnoughDist();
    }
}
