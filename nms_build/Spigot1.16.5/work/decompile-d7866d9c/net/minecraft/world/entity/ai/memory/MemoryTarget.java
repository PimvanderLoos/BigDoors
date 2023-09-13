package net.minecraft.world.entity.ai.memory;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.behavior.BehaviorPosition;
import net.minecraft.world.entity.ai.behavior.BehaviorTarget;
import net.minecraft.world.phys.Vec3D;

public class MemoryTarget {

    private final BehaviorPosition a;
    private final float b;
    private final int c;

    public MemoryTarget(BlockPosition blockposition, float f, int i) {
        this((BehaviorPosition) (new BehaviorTarget(blockposition)), f, i);
    }

    public MemoryTarget(Vec3D vec3d, float f, int i) {
        this((BehaviorPosition) (new BehaviorTarget(new BlockPosition(vec3d))), f, i);
    }

    public MemoryTarget(BehaviorPosition behaviorposition, float f, int i) {
        this.a = behaviorposition;
        this.b = f;
        this.c = i;
    }

    public BehaviorPosition a() {
        return this.a;
    }

    public float b() {
        return this.b;
    }

    public int c() {
        return this.c;
    }
}
