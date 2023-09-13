package net.minecraft.server;

import javax.annotation.Nullable;

public interface INamableTileEntity {

    IChatBaseComponent getDisplayName();

    boolean hasCustomName();

    default IChatBaseComponent getScoreboardDisplayName() {
        return this.getDisplayName();
    }

    @Nullable
    IChatBaseComponent getCustomName();
}
