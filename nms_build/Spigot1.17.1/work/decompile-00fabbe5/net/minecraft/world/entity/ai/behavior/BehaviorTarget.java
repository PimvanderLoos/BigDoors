package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public class BehaviorTarget implements BehaviorPosition {

    private final BlockPosition blockPos;
    private final Vec3D centerPosition;

    public BehaviorTarget(BlockPosition blockposition) {
        this.blockPos = blockposition;
        this.centerPosition = Vec3D.a((BaseBlockPosition) blockposition);
    }

    @Override
    public Vec3D a() {
        return this.centerPosition;
    }

    @Override
    public BlockPosition b() {
        return this.blockPos;
    }

    @Override
    public boolean a(EntityLiving entityliving) {
        return true;
    }

    public String toString() {
        return "BlockPosTracker{blockPos=" + this.blockPos + ", centerPosition=" + this.centerPosition + "}";
    }
}
