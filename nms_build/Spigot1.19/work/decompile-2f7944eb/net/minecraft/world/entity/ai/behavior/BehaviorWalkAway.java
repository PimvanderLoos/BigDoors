package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class BehaviorWalkAway<T> extends Behavior<EntityCreature> {

    private final MemoryModuleType<T> walkAwayFromMemory;
    private final float speedModifier;
    private final int desiredDistance;
    private final Function<T, Vec3D> toPosition;

    public BehaviorWalkAway(MemoryModuleType<T> memorymoduletype, float f, int i, boolean flag, Function<T, Vec3D> function) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, flag ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.walkAwayFromMemory = memorymoduletype;
        this.speedModifier = f;
        this.desiredDistance = i;
        this.toPosition = function;
    }

    public static BehaviorWalkAway<BlockPosition> pos(MemoryModuleType<BlockPosition> memorymoduletype, float f, int i, boolean flag) {
        return new BehaviorWalkAway<>(memorymoduletype, f, i, flag, Vec3D::atBottomCenterOf);
    }

    public static BehaviorWalkAway<? extends Entity> entity(MemoryModuleType<? extends Entity> memorymoduletype, float f, int i, boolean flag) {
        return new BehaviorWalkAway<>(memorymoduletype, f, i, flag, Entity::position);
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        return this.alreadyWalkingAwayFromPosWithSameSpeed(entitycreature) ? false : entitycreature.position().closerThan(this.getPosToAvoid(entitycreature), (double) this.desiredDistance);
    }

    private Vec3D getPosToAvoid(EntityCreature entitycreature) {
        return (Vec3D) this.toPosition.apply(entitycreature.getBrain().getMemory(this.walkAwayFromMemory).get());
    }

    private boolean alreadyWalkingAwayFromPosWithSameSpeed(EntityCreature entitycreature) {
        if (!entitycreature.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
            return false;
        } else {
            MemoryTarget memorytarget = (MemoryTarget) entitycreature.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get();

            if (memorytarget.getSpeedModifier() != this.speedModifier) {
                return false;
            } else {
                Vec3D vec3d = memorytarget.getTarget().currentPosition().subtract(entitycreature.position());
                Vec3D vec3d1 = this.getPosToAvoid(entitycreature).subtract(entitycreature.position());

                return vec3d.dot(vec3d1) < 0.0D;
            }
        }
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        moveAwayFrom(entitycreature, this.getPosToAvoid(entitycreature), this.speedModifier);
    }

    private static void moveAwayFrom(EntityCreature entitycreature, Vec3D vec3d, float f) {
        for (int i = 0; i < 10; ++i) {
            Vec3D vec3d1 = LandRandomPos.getPosAway(entitycreature, 16, 7, vec3d);

            if (vec3d1 != null) {
                entitycreature.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(vec3d1, f, 0)));
                return;
            }
        }

    }
}
