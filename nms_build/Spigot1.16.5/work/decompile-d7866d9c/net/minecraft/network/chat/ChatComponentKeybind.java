package net.minecraft.network.chat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChatComponentKeybind extends ChatBaseComponent {

    private static Function<String, Supplier<IChatBaseComponent>> d = (s) -> {
        return () -> {
            return new ChatComponentText(s);
        };
    };
    private final String e;
    private Supplier<IChatBaseComponent> f;

    public ChatComponentKeybind(String s) {
        this.e = s;
    }

    private IChatBaseComponent j() {
        if (this.f == null) {
            this.f = (Supplier) ChatComponentKeybind.d.apply(this.e);
        }

        return (IChatBaseComponent) this.f.get();
    }

    @Override
    public <T> Optional<T> b(IChatFormatted.a<T> ichatformatted_a) {
        return this.j().a(ichatformatted_a);
    }

    @Override
    public ChatComponentKeybind g() {
        return new ChatComponentKeybind(this.e);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentKeybind)) {
            return false;
        } else {
            ChatComponentKeybind chatcomponentkeybind = (ChatComponentKeybind) object;

            return this.e.equals(chatcomponentkeybind.e) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.e + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatModifier() + '}';
    }

    public String i() {
        return this.e;
    }
}
