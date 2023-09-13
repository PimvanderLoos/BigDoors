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

    public boolean a() {
        return this.ticking;
    }

    public boolean b() {
        return this.accessible;
    }

    public static Visibility a(PlayerChunk.State playerchunk_state) {
        return playerchunk_state.isAtLeast(PlayerChunk.State.ENTITY_TICKING) ? Visibility.TICKING : (playerchunk_state.isAtLeast(PlayerChunk.State.BORDER) ? Visibility.TRACKED : Visibility.HIDDEN);
    }
}
