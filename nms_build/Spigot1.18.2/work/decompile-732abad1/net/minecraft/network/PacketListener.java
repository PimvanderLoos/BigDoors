package net.minecraft.network;

import net.minecraft.network.chat.IChatBaseComponent;

public interface PacketListener {

    void onDisconnect(IChatBaseComponent ichatbasecomponent);

    NetworkManager getConnection();

    default boolean shouldPropagateHandlingExceptions() {
        return true;
    }
}
