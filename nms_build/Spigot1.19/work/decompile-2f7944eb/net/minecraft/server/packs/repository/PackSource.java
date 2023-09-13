package net.minecraft.server.packs.repository;

import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

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
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.translatable(s);

        return (ichatbasecomponent) -> {
            return IChatBaseComponent.translatable("pack.nameAndSource", ichatbasecomponent, ichatmutablecomponent).withStyle(EnumChatFormat.GRAY);
        };
    }
}
