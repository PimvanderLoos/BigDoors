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
    public IChatMutableComponent append(IChatBaseComponent ichatbasecomponent) {
        this.siblings.add(ichatbasecomponent);
        return this;
    }

    @Override
    public String getContents() {
        return "";
    }

    @Override
    public List<IChatBaseComponent> getSiblings() {
        return this.siblings;
    }

    @Override
    public IChatMutableComponent setStyle(ChatModifier chatmodifier) {
        this.style = chatmodifier;
        return this;
    }

    @Override
    public ChatModifier getStyle() {
        return this.style;
    }

    @Override
    public abstract ChatBaseComponent plainCopy();

    @Override
    public final IChatMutableComponent copy() {
        ChatBaseComponent chatbasecomponent = this.plainCopy();

        chatbasecomponent.siblings.addAll(this.siblings);
        chatbasecomponent.setStyle(this.style);
        return chatbasecomponent;
    }

    @Override
    public FormattedString getVisualOrderText() {
        LocaleLanguage localelanguage = LocaleLanguage.getInstance();

        if (this.decomposedWith != localelanguage) {
            this.visualOrderText = localelanguage.getVisualOrder((IChatFormatted) this);
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

            return this.siblings.equals(chatbasecomponent.siblings) && Objects.equals(this.getStyle(), chatbasecomponent.getStyle());
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getStyle(), this.siblings});
    }

    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + "}";
    }
}
