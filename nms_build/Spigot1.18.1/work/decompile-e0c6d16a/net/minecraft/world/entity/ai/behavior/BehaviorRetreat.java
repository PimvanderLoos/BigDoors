package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorRetreat<E extends EntityInsentient> extends Behavior<E> {

    private final int tooCloseDistance;
    private final float strafeSpeed;

    public BehaviorRetreat(int i, float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.tooCloseDistance = i;
        this.strafeSpeed = f;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return this.isTargetVisible(e0) && this.isTargetTooClose(e0);
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        e0.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(this.getTarget(e0), true)));
        e0.getMoveControl().strafe(-this.strafeSpeed, 0.0F);
        e0.setYRot(MathHelper.rotateIfNecessary(e0.getYRot(), e0.yHeadRot, 0.0F));
    }

    private boolean isTargetVisible(E e0) {
        return ((NearestVisibleLivingEntities) e0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains(this.getTarget(e0));
    }

    private boolean isTargetTooClose(E e0) {
        return this.getTarget(e0).closerThan(e0, (double) this.tooCloseDistance);
    }

    private EntityLiving getTarget(E e0) {
        return (EntityLiving) e0.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
