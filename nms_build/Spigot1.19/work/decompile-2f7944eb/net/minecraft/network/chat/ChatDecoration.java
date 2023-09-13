package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.util.INamable;

public record ChatDecoration(String translationKey, List<ChatDecoration.a> parameters, ChatModifier style) {

    public static final Codec<ChatDecoration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.fieldOf("translation_key").forGetter(ChatDecoration::translationKey), ChatDecoration.a.CODEC.listOf().fieldOf("parameters").forGetter(ChatDecoration::parameters), ChatModifier.FORMATTING_CODEC.fieldOf("style").forGetter(ChatDecoration::style)).apply(instance, ChatDecoration::new);
    });

    public static ChatDecoration withSender(String s) {
        return new ChatDecoration(s, List.of(ChatDecoration.a.SENDER, ChatDecoration.a.CONTENT), ChatModifier.EMPTY);
    }

    public static ChatDecoration directMessage(String s) {
        ChatModifier chatmodifier = ChatModifier.EMPTY.withColor(EnumChatFormat.GRAY).withItalic(true);

        return new ChatDecoration(s, List.of(ChatDecoration.a.SENDER, ChatDecoration.a.CONTENT), chatmodifier);
    }

    public static ChatDecoration teamMessage(String s) {
        return new ChatDecoration(s, List.of(ChatDecoration.a.TEAM_NAME, ChatDecoration.a.SENDER, ChatDecoration.a.CONTENT), ChatModifier.EMPTY);
    }

    public IChatBaseComponent decorate(IChatBaseComponent ichatbasecomponent, @Nullable ChatSender chatsender) {
        IChatBaseComponent[] aichatbasecomponent = this.resolveParameters(ichatbasecomponent, chatsender);

        return IChatBaseComponent.translatable(this.translationKey, aichatbasecomponent).withStyle(this.style);
    }

    private IChatBaseComponent[] resolveParameters(IChatBaseComponent ichatbasecomponent, @Nullable ChatSender chatsender) {
        IChatBaseComponent[] aichatbasecomponent = new IChatBaseComponent[this.parameters.size()];

        for (int i = 0; i < aichatbasecomponent.length; ++i) {
            ChatDecoration.a chatdecoration_a = (ChatDecoration.a) this.parameters.get(i);

            aichatbasecomponent[i] = chatdecoration_a.select(ichatbasecomponent, chatsender);
        }

        return aichatbasecomponent;
    }

    public static enum a implements INamable {

        SENDER("sender", (ichatbasecomponent, chatsender) -> {
            return chatsender != null ? chatsender.name() : null;
        }), TEAM_NAME("team_name", (ichatbasecomponent, chatsender) -> {
            return chatsender != null ? chatsender.teamName() : null;
        }), CONTENT("content", (ichatbasecomponent, chatsender) -> {
            return ichatbasecomponent;
        });

        public static final Codec<ChatDecoration.a> CODEC = INamable.fromEnum(ChatDecoration.a::values);
        private final String name;
        private final ChatDecoration.a.a selector;

        private a(String s, ChatDecoration.a.a chatdecoration_a_a) {
            this.name = s;
            this.selector = chatdecoration_a_a;
        }

        public IChatBaseComponent select(IChatBaseComponent ichatbasecomponent, @Nullable ChatSender chatsender) {
            IChatBaseComponent ichatbasecomponent1 = this.selector.select(ichatbasecomponent, chatsender);

            return (IChatBaseComponent) Objects.requireNonNullElse(ichatbasecomponent1, CommonComponents.EMPTY);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public interface a {

            @Nullable
            IChatBaseComponent select(IChatBaseComponent ichatbasecomponent, @Nullable ChatSender chatsender);
        }
    }
}
