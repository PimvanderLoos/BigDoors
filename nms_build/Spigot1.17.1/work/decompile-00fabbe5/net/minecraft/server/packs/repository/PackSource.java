package net.minecraft.server.packs.repository;

import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public interface PackSource {

    PackSource DEFAULT = a();
    PackSource BUILT_IN = a("pack.source.builtin");
    PackSource WORLD = a("pack.source.world");
    PackSource SERVER = a("pack.source.server");

    IChatBaseComponent decorate(IChatBaseComponent ichatbasecomponent);

    static PackSource a() {
        return (ichatbasecomponent) -> {
            return ichatbasecomponent;
        };
    }

    static PackSource a(String s) {
        ChatMessage chatmessage = new ChatMessage(s);

        return (ichatbasecomponent) -> {
            return (new ChatMessage("pack.nameAndSource", new Object[]{ichatbasecomponent, chatmessage})).a(EnumChatFormat.GRAY);
        };
    }
}
