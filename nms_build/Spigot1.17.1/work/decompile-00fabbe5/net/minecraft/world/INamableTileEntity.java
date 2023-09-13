package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;

public interface INamableTileEntity {

    IChatBaseComponent getDisplayName();

    default boolean hasCustomName() {
        return this.getCustomName() != null;
    }

    default IChatBaseComponent getScoreboardDisplayName() {
        return this.getDisplayName();
    }

    @Nullable
    default IChatBaseComponent getCustomName() {
        return null;
    }
}
