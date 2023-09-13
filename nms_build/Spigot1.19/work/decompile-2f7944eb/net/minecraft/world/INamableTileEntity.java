package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;

public interface INamableTileEntity {

    IChatBaseComponent getName();

    default boolean hasCustomName() {
        return this.getCustomName() != null;
    }

    default IChatBaseComponent getDisplayName() {
        return this.getName();
    }

    @Nullable
    default IChatBaseComponent getCustomName() {
        return null;
    }
}
