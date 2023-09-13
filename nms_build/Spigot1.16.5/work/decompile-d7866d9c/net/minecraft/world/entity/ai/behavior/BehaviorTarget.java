package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public class BehaviorTarget implements BehaviorPosition {

    private final BlockPosition a;
    private final Vec3D b;

    public BehaviorTarget(BlockPosition blockposition) {
        this.a = blockposition;
        this.b = Vec3D.a((BaseBlockPosition) blockposition);
    }

    @Override
    public Vec3D a() {
        return this.b;
    }

    @Override
    public BlockPosition b() {
        return this.a;
    }

    @Override
    public boolean a(EntityLiving entityliving) {
        return true;
    }

    public String toString() {
        return "BlockPosTracker{blockPos=" + this.a + ", centerPosition=" + this.b + '}';
    }
}
