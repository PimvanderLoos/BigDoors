package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;

public abstract class ChatBaseComponent implements IChatMutableComponent {

    protected final List<IChatBaseComponent> siblings = Lists.newArrayList();
    private ChatModifier d;

    public ChatBaseComponent() {
        this.d = ChatModifier.b;
    }

    @Override
    public IChatMutableComponent addSibling(IChatBaseComponent ichatbasecomponent) {
        this.siblings.add(ichatbasecomponent);
        return this;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public List<IChatBaseComponent> getSiblings() {
        return this.siblings;
    }

    @Override
    public IChatMutableComponent setChatModifier(ChatModifier chatmodifier) {
        this.d = chatmodifier;
        return this;
    }

    @Override
    public ChatModifier getChatModifier() {
        return this.d;
    }

    @Override
    public abstract ChatBaseComponent f();

    @Override
    public final IChatMutableComponent mutableCopy() {
        ChatBaseComponent chatbasecomponent = this.f();

        chatbasecomponent.siblings.addAll(this.siblings);
        chatbasecomponent.setChatModifier(this.d);
        return chatbasecomponent;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatBaseComponent)) {
            return false;
        } else {
            ChatBaseComponent chatbasecomponent = (ChatBaseComponent) object;

            return this.siblings.equals(chatbasecomponent.siblings) && Objects.equals(this.getChatModifier(), chatbasecomponent.getChatModifier());
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getChatModifier(), this.siblings});
    }

    public String toString() {
        return "BaseComponent{style=" + this.d + ", siblings=" + this.siblings + '}';
    }
}
