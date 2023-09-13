package net.minecraft.server;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class ChatBaseComponent implements IChatBaseComponent {

    protected List<IChatBaseComponent> a = Lists.newArrayList();
    private ChatModifier b;

    public ChatBaseComponent() {}

    public IChatBaseComponent addSibling(IChatBaseComponent ichatbasecomponent) {
        ichatbasecomponent.getChatModifier().setChatModifier(this.getChatModifier());
        this.a.add(ichatbasecomponent);
        return this;
    }

    public List<IChatBaseComponent> a() {
        return this.a;
    }

    public IChatBaseComponent a(String s) {
        return this.addSibling(new ChatComponentText(s));
    }

    public IChatBaseComponent setChatModifier(ChatModifier chatmodifier) {
        this.b = chatmodifier;
        Iterator iterator = this.a.iterator();

        while (iterator.hasNext()) {
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

            ichatbasecomponent.getChatModifier().setChatModifier(this.getChatModifier());
        }

        return this;
    }

    public ChatModifier getChatModifier() {
        if (this.b == null) {
            this.b = new ChatModifier();
            Iterator iterator = this.a.iterator();

            while (iterator.hasNext()) {
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

                ichatbasecomponent.getChatModifier().setChatModifier(this.b);
            }
        }

        return this.b;
    }

    public Iterator<IChatBaseComponent> iterator() {
        return Iterators.concat(Iterators.forArray(new ChatBaseComponent[] { this}), a((Iterable) this.a));
    }

    public final String getString() {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = this.iterator();

        while (iterator.hasNext()) {
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

            stringbuilder.append(ichatbasecomponent.getText());
        }

        return stringbuilder.toString();
    }

    public final String c() {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = this.iterator();

        while (iterator.hasNext()) {
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();
            String s = ichatbasecomponent.getText();

            if (!s.isEmpty()) {
                stringbuilder.append(ichatbasecomponent.getChatModifier().k());
                stringbuilder.append(s);
                stringbuilder.append(EnumChatFormat.RESET);
            }
        }

        return stringbuilder.toString();
    }

    public static Iterator<IChatBaseComponent> a(Iterable<IChatBaseComponent> iterable) {
        return Streams.stream(iterable).flatMap(Streams::stream).map((ichatbasecomponent) -> {
            IChatBaseComponent ichatbasecomponent1 = ichatbasecomponent.e();

            ichatbasecomponent1.setChatModifier(ichatbasecomponent.getChatModifier().n());
            return ichatbasecomponent1;
        }).iterator();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatBaseComponent)) {
            return false;
        } else {
            ChatBaseComponent chatbasecomponent = (ChatBaseComponent) object;

            return this.a.equals(chatbasecomponent.a) && this.getChatModifier().equals(chatbasecomponent.getChatModifier());
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.getChatModifier(), this.a});
    }

    public String toString() {
        return "BaseComponent{style=" + this.b + ", siblings=" + this.a + '}';
    }
}
