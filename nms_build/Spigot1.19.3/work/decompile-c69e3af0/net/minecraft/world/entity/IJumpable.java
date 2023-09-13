package net.minecraft.world.entity;

import net.minecraft.world.entity.player.EntityHuman;

public interface IJumpable extends PlayerRideable {

    void onPlayerJump(int i);

    boolean canJump(EntityHuman entityhuman);

    void handleStartJump(int i);

    void handleStopJump();

    default int getJumpCooldown() {
        return 0;
    }
}
