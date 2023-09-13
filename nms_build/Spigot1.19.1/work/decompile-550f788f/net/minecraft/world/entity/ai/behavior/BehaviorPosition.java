package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public interface BehaviorPosition {

    Vec3D currentPosition();

    BlockPosition currentBlockPosition();

    boolean isVisibleBy(EntityLiving entityliving);
}
