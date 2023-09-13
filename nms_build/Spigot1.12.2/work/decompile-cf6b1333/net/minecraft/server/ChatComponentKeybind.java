package net.minecraft.server;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChatComponentKeybind extends ChatBaseComponent {

    public static Function<String, Supplier<String>> b = (var0) -> {
        return () -> {
            return s;
        };
    };
    private final String c;
    private Supplier<String> d;

    public ChatComponentKeybind(String s) {
        this.c = s;
    }

    public String getText() {
        if (this.d == null) {
            this.d = (Supplier) ChatComponentKeybind.b.apply(this.c);
        }

        return (String) this.d.get();
    }

    public ChatComponentKeybind g() {
        ChatComponentKeybind chatcomponentkeybind = new ChatComponentKeybind(this.c);

        chatcomponentkeybind.setChatModifier(this.getChatModifier().clone());
        Iterator iterator = this.a().iterator();

        while (iterator.hasNext()) {
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

            chatcomponentkeybind.addSibling(ichatbasecomponent.f());
        }

        return chatcomponentkeybind;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentKeybind)) {
            return false;
        } else {
            ChatComponentKeybind chatcomponentkeybind = (ChatComponentKeybind) object;

            return this.c.equals(chatcomponentkeybind.c) && super.equals(object);
        }
    }

    public String toString() {
        return "KeybindComponent{keybind=\'" + this.c + '\'' + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
    }

    public String h() {
        return this.c;
    }

    public IChatBaseComponent f() {
        return this.g();
    }
}
