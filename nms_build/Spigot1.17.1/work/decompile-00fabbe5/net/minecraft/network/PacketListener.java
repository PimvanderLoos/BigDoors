package net.minecraft.network;

import net.minecraft.network.chat.IChatBaseComponent;

public interface PacketListener {

    void a(IChatBaseComponent ichatbasecomponent);

    NetworkManager a();
}
