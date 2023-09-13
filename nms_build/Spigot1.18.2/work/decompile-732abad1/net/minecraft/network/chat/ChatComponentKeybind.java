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

    public static void setKeyResolver(Function<String, Supplier<IChatBaseComponent>> function) {
        ChatComponentKeybind.keyResolver = function;
    }

    private IChatBaseComponent getNestedComponent() {
        if (this.nameResolver == null) {
            this.nameResolver = (Supplier) ChatComponentKeybind.keyResolver.apply(this.name);
        }

        return (IChatBaseComponent) this.nameResolver.get();
    }

    @Override
    public <T> Optional<T> visitSelf(IChatFormatted.a<T> ichatformatted_a) {
        return this.getNestedComponent().visit(ichatformatted_a);
    }

    @Override
    public <T> Optional<T> visitSelf(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        return this.getNestedComponent().visit(ichatformatted_b, chatmodifier);
    }

    @Override
    public ChatComponentKeybind plainCopy() {
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
        return "KeybindComponent{keybind='" + this.name + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
    }

    public String getName() {
        return this.name;
    }
}
