package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.locale.LocaleLanguage;
import net.minecraft.util.FormattedString;

public class IChatMutableComponent implements IChatBaseComponent {

    private final ComponentContents contents;
    private final List<IChatBaseComponent> siblings;
    private ChatModifier style;
    private FormattedString visualOrderText;
    @Nullable
    private LocaleLanguage decomposedWith;

    IChatMutableComponent(ComponentContents componentcontents, List<IChatBaseComponent> list, ChatModifier chatmodifier) {
        this.visualOrderText = FormattedString.EMPTY;
        this.contents = componentcontents;
        this.siblings = list;
        this.style = chatmodifier;
    }

    public static IChatMutableComponent create(ComponentContents componentcontents) {
        return new IChatMutableComponent(componentcontents, Lists.newArrayList(), ChatModifier.EMPTY);
    }

    @Override
    public ComponentContents getContents() {
        return this.contents;
    }

    @Override
    public List<IChatBaseComponent> getSiblings() {
        return this.siblings;
    }

    public IChatMutableComponent setStyle(ChatModifier chatmodifier) {
        this.style = chatmodifier;
        return this;
    }

    @Override
    public ChatModifier getStyle() {
        return this.style;
    }

    public IChatMutableComponent append(String s) {
        return this.append((IChatBaseComponent) IChatBaseComponent.literal(s));
    }

    public IChatMutableComponent append(IChatBaseComponent ichatbasecomponent) {
        this.siblings.add(ichatbasecomponent);
        return this;
    }

    public IChatMutableComponent withStyle(UnaryOperator<ChatModifier> unaryoperator) {
        this.setStyle((ChatModifier) unaryoperator.apply(this.getStyle()));
        return this;
    }

    public IChatMutableComponent withStyle(ChatModifier chatmodifier) {
        this.setStyle(chatmodifier.applyTo(this.getStyle()));
        return this;
    }

    public IChatMutableComponent withStyle(EnumChatFormat... aenumchatformat) {
        this.setStyle(this.getStyle().applyFormats(aenumchatformat));
        return this;
    }

    public IChatMutableComponent withStyle(EnumChatFormat enumchatformat) {
        this.setStyle(this.getStyle().applyFormat(enumchatformat));
        return this;
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
        } else if (!(object instanceof IChatMutableComponent)) {
            return false;
        } else {
            IChatMutableComponent ichatmutablecomponent = (IChatMutableComponent) object;

            return this.contents.equals(ichatmutablecomponent.contents) && this.style.equals(ichatmutablecomponent.style) && this.siblings.equals(ichatmutablecomponent.siblings);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.contents, this.style, this.siblings});
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder(this.contents.toString());
        boolean flag = !this.style.isEmpty();
        boolean flag1 = !this.siblings.isEmpty();

        if (flag || flag1) {
            stringbuilder.append('[');
            if (flag) {
                stringbuilder.append("style=");
                stringbuilder.append(this.style);
            }

            if (flag && flag1) {
                stringbuilder.append(", ");
            }

            if (flag1) {
                stringbuilder.append("siblings=");
                stringbuilder.append(this.siblings);
            }

            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }
}
