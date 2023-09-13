package net.minecraft.network.chat.contents;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.IChatBaseComponent;

public class KeybindResolver {

    static Function<String, Supplier<IChatBaseComponent>> keyResolver = (s) -> {
        return () -> {
            return IChatBaseComponent.literal(s);
        };
    };

    public KeybindResolver() {}

    public static void setKeyResolver(Function<String, Supplier<IChatBaseComponent>> function) {
        KeybindResolver.keyResolver = function;
    }
}
