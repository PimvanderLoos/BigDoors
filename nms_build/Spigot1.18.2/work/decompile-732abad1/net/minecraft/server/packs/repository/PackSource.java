package net.minecraft.server.packs.repository;

import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public interface PackSource {

    PackSource DEFAULT = passThrough();
    PackSource BUILT_IN = decorating("pack.source.builtin");
    PackSource WORLD = decorating("pack.source.world");
    PackSource SERVER = decorating("pack.source.server");

    IChatBaseComponent decorate(IChatBaseComponent ichatbasecomponent);

    static PackSource passThrough() {
        return (ichatbasecomponent) -> {
            return ichatbasecomponent;
        };
    }

    static PackSource decorating(String s) {
        ChatMessage chatmessage = new ChatMessage(s);

        return (ichatbasecomponent) -> {
            return (new ChatMessage("pack.nameAndSource", new Object[]{ichatbasecomponent, chatmessage})).withStyle(EnumChatFormat.GRAY);
        };
    }
}
