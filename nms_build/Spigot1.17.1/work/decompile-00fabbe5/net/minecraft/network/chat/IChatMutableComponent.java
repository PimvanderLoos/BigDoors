package net.minecraft.network.chat;

import java.util.function.UnaryOperator;
import net.minecraft.EnumChatFormat;

public interface IChatMutableComponent extends IChatBaseComponent {

    IChatMutableComponent setChatModifier(ChatModifier chatmodifier);

    default IChatMutableComponent c(String s) {
        return this.addSibling(new ChatComponentText(s));
    }

    IChatMutableComponent addSibling(IChatBaseComponent ichatbasecomponent);

    default IChatMutableComponent format(UnaryOperator<ChatModifier> unaryoperator) {
        this.setChatModifier((ChatModifier) unaryoperator.apply(this.getChatModifier()));
        return this;
    }

    default IChatMutableComponent c(ChatModifier chatmodifier) {
        this.setChatModifier(chatmodifier.setChatModifier(this.getChatModifier()));
        return this;
    }

    default IChatMutableComponent a(EnumChatFormat... aenumchatformat) {
        this.setChatModifier(this.getChatModifier().a(aenumchatformat));
        return this;
    }

    default IChatMutableComponent a(EnumChatFormat enumchatformat) {
        this.setChatModifier(this.getChatModifier().b(enumchatformat));
        return this;
    }
}
