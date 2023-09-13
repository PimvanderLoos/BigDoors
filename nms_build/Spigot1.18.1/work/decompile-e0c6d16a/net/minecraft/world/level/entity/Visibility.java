package net.minecraft.world.level.entity;

import net.minecraft.server.level.PlayerChunk;

public enum Visibility {

    HIDDEN(false, false), TRACKED(true, false), TICKING(true, true);

    private final boolean accessible;
    private final boolean ticking;

    private Visibility(boolean flag, boolean flag1) {
        this.accessible = flag;
        this.ticking = flag1;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    public boolean isAccessible() {
        return this.accessible;
    }

    public static Visibility fromFullChunkStatus(PlayerChunk.State playerchunk_state) {
        return playerchunk_state.isOrAfter(PlayerChunk.State.ENTITY_TICKING) ? Visibility.TICKING : (playerchunk_state.isOrAfter(PlayerChunk.State.BORDER) ? Visibility.TRACKED : Visibility.HIDDEN);
    }
}
