package net.minecraft.util;

import net.minecraft.network.chat.IChatBaseComponent;

public interface OptionEnum {

    int getId();

    String getKey();

    default IChatBaseComponent getCaption() {
        return IChatBaseComponent.translatable(this.getKey());
    }
}
