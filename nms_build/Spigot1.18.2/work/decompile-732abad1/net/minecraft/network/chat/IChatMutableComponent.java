package net.minecraft.network.chat;

import java.util.function.UnaryOperator;
import net.minecraft.EnumChatFormat;

public interface IChatMutableComponent extends IChatBaseComponent {

    IChatMutableComponent setStyle(ChatModifier chatmodifier);

    default IChatMutableComponent append(String s) {
        return this.append((IChatBaseComponent) (new ChatComponentText(s)));
    }

    IChatMutableComponent append(IChatBaseComponent ichatbasecomponent);

    default IChatMutableComponent withStyle(UnaryOperator<ChatModifier> unaryoperator) {
        this.setStyle((ChatModifier) unaryoperator.apply(this.getStyle()));
        return this;
    }

    default IChatMutableComponent withStyle(ChatModifier chatmodifier) {
        this.setStyle(chatmodifier.applyTo(this.getStyle()));
        return this;
    }

    default IChatMutableComponent withStyle(EnumChatFormat... aenumchatformat) {
        this.setStyle(this.getStyle().applyFormats(aenumchatformat));
        return this;
    }

    default IChatMutableComponent withStyle(EnumChatFormat enumchatformat) {
        this.setStyle(this.getStyle().applyFormat(enumchatformat));
        return this;
    }
}
