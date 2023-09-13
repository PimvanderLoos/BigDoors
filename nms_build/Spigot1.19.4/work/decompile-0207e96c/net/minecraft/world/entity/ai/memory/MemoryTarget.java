package net.minecraft.world.entity.ai.memory;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.BehaviorPosition;
import net.minecraft.world.entity.ai.behavior.BehaviorPositionEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorTarget;
import net.minecraft.world.phys.Vec3D;

public class MemoryTarget {

    private final BehaviorPosition target;
    private final float speedModifier;
    private final int closeEnoughDist;

    public MemoryTarget(BlockPosition blockposition, float f, int i) {
        this((BehaviorPosition) (new BehaviorTarget(blockposition)), f, i);
    }

    public MemoryTarget(Vec3D vec3d, float f, int i) {
        this((BehaviorPosition) (new BehaviorTarget(BlockPosition.containing(vec3d))), f, i);
    }

    public MemoryTarget(Entity entity, float f, int i) {
        this((BehaviorPosition) (new BehaviorPositionEntity(entity, false)), f, i);
    }

    public MemoryTarget(BehaviorPosition behaviorposition, float f, int i) {
        this.target = behaviorposition;
        this.speedModifier = f;
        this.closeEnoughDist = i;
    }

    public BehaviorPosition getTarget() {
        return this.target;
    }

    public float getSpeedModifier() {
        return this.speedModifier;
    }

    public int getCloseEnoughDist() {
        return this.closeEnoughDist;
    }
}
