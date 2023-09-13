package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.locale.LocaleLanguage;
import net.minecraft.util.FormattedString;

public abstract class ChatBaseComponent implements IChatMutableComponent {

    protected final List<IChatBaseComponent> siblings = Lists.newArrayList();
    private FormattedString visualOrderText;
    @Nullable
    private LocaleLanguage decomposedWith;
    private ChatModifier style;

    public ChatBaseComponent() {
        this.visualOrderText = FormattedString.EMPTY;
        this.style = ChatModifier.EMPTY;
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
        this.style = chatmodifier;
        return this;
    }

    @Override
    public ChatModifier getChatModifier() {
        return this.style;
    }

    @Override
    public abstract ChatBaseComponent g();

    @Override
    public final IChatMutableComponent mutableCopy() {
        ChatBaseComponent chatbasecomponent = this.g();

        chatbasecomponent.siblings.addAll(this.siblings);
        chatbasecomponent.setChatModifier(this.style);
        return chatbasecomponent;
    }

    @Override
    public FormattedString f() {
        LocaleLanguage localelanguage = LocaleLanguage.a();

        if (this.decomposedWith != localelanguage) {
            this.visualOrderText = localelanguage.a((IChatFormatted) this);
            this.decomposedWith = localelanguage;
        }

        return this.visualOrderText;
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
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + "}";
    }
}
