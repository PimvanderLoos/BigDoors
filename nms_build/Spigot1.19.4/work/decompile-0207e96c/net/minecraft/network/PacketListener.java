package net.minecraft.network;

import net.minecraft.network.chat.IChatBaseComponent;

public interface PacketListener {

    void onDisconnect(IChatBaseComponent ichatbasecomponent);

    boolean isAcceptingMessages();

    default boolean shouldPropagateHandlingExceptions() {
        return true;
    }
}
