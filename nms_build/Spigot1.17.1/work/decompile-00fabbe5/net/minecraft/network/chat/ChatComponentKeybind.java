package net.minecraft.network.chat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChatComponentKeybind extends ChatBaseComponent {

    private static Function<String, Supplier<IChatBaseComponent>> keyResolver = (s) -> {
        return () -> {
            return new ChatComponentText(s);
        };
    };
    private final String name;
    private Supplier<IChatBaseComponent> nameResolver;

    public ChatComponentKeybind(String s) {
        this.name = s;
    }

    public static void a(Function<String, Supplier<IChatBaseComponent>> function) {
        ChatComponentKeybind.keyResolver = function;
    }

    private IChatBaseComponent j() {
        if (this.nameResolver == null) {
            this.nameResolver = (Supplier) ChatComponentKeybind.keyResolver.apply(this.name);
        }

        return (IChatBaseComponent) this.nameResolver.get();
    }

    @Override
    public <T> Optional<T> b(IChatFormatted.a<T> ichatformatted_a) {
        return this.j().a(ichatformatted_a);
    }

    @Override
    public <T> Optional<T> b(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        return this.j().a(ichatformatted_b, chatmodifier);
    }

    @Override
    public ChatComponentKeybind g() {
        return new ChatComponentKeybind(this.name);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentKeybind)) {
            return false;
        } else {
            ChatComponentKeybind chatcomponentkeybind = (ChatComponentKeybind) object;

            return this.name.equals(chatcomponentkeybind.name) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.name + "', siblings=" + this.siblings + ", style=" + this.getChatModifier() + "}";
    }

    public String i() {
        return this.name;
    }
}
