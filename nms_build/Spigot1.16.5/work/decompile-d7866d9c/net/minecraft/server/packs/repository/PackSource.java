package net.minecraft.server.packs.repository;

import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public interface PackSource {

    PackSource a = a();
    PackSource b = a("pack.source.builtin");
    PackSource c = a("pack.source.world");
    PackSource d = a("pack.source.server");

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
