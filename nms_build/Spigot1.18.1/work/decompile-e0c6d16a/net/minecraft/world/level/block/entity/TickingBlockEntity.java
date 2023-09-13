package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;

public interface TickingBlockEntity {

    void tick();

    boolean isRemoved();

    BlockPosition getPos();

    String getType();
}
