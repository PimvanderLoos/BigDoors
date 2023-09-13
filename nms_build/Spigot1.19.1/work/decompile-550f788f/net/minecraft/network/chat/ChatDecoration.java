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
        return instance.group(Codec.STRING.fieldOf("translation_key").forGetter(ChatDecoration::translationKey), ChatDecoration.a.CODEC.listOf().fieldOf("parameters").forGetter(ChatDecoration::parameters), ChatModifier.FORMATTING_CODEC.optionalFieldOf("style", ChatModifier.EMPTY).forGetter(ChatDecoration::style)).apply(instance, ChatDecoration::new);
    });

    public static ChatDecoration withSender(String s) {
        return new ChatDecoration(s, List.of(ChatDecoration.a.SENDER, ChatDecoration.a.CONTENT), ChatModifier.EMPTY);
    }

    public static ChatDecoration incomingDirectMessage(String s) {
        ChatModifier chatmodifier = ChatModifier.EMPTY.withColor(EnumChatFormat.GRAY).withItalic(true);

        return new ChatDecoration(s, List.of(ChatDecoration.a.SENDER, ChatDecoration.a.CONTENT), chatmodifier);
    }

    public static ChatDecoration outgoingDirectMessage(String s) {
        ChatModifier chatmodifier = ChatModifier.EMPTY.withColor(EnumChatFormat.GRAY).withItalic(true);

        return new ChatDecoration(s, List.of(ChatDecoration.a.TARGET, ChatDecoration.a.CONTENT), chatmodifier);
    }

    public static ChatDecoration teamMessage(String s) {
        return new ChatDecoration(s, List.of(ChatDecoration.a.TARGET, ChatDecoration.a.SENDER, ChatDecoration.a.CONTENT), ChatModifier.EMPTY);
    }

    public IChatBaseComponent decorate(IChatBaseComponent ichatbasecomponent, ChatMessageType.a chatmessagetype_a) {
        IChatBaseComponent[] aichatbasecomponent = this.resolveParameters(ichatbasecomponent, chatmessagetype_a);

        return IChatBaseComponent.translatable(this.translationKey, aichatbasecomponent).withStyle(this.style);
    }

    private IChatBaseComponent[] resolveParameters(IChatBaseComponent ichatbasecomponent, ChatMessageType.a chatmessagetype_a) {
        IChatBaseComponent[] aichatbasecomponent = new IChatBaseComponent[this.parameters.size()];

        for (int i = 0; i < aichatbasecomponent.length; ++i) {
            ChatDecoration.a chatdecoration_a = (ChatDecoration.a) this.parameters.get(i);

            aichatbasecomponent[i] = chatdecoration_a.select(ichatbasecomponent, chatmessagetype_a);
        }

        return aichatbasecomponent;
    }

    public static enum a implements INamable {

        SENDER("sender", (ichatbasecomponent, chatmessagetype_a) -> {
            return chatmessagetype_a.name();
        }), TARGET("target", (ichatbasecomponent, chatmessagetype_a) -> {
            return chatmessagetype_a.targetName();
        }), CONTENT("content", (ichatbasecomponent, chatmessagetype_a) -> {
            return ichatbasecomponent;
        });

        public static final Codec<ChatDecoration.a> CODEC = INamable.fromEnum(ChatDecoration.a::values);
        private final String name;
        private final ChatDecoration.a.a selector;

        private a(String s, ChatDecoration.a.a chatdecoration_a_a) {
            this.name = s;
            this.selector = chatdecoration_a_a;
        }

        public IChatBaseComponent select(IChatBaseComponent ichatbasecomponent, ChatMessageType.a chatmessagetype_a) {
            IChatBaseComponent ichatbasecomponent1 = this.selector.select(ichatbasecomponent, chatmessagetype_a);

            return (IChatBaseComponent) Objects.requireNonNullElse(ichatbasecomponent1, CommonComponents.EMPTY);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public interface a {

            @Nullable
            IChatBaseComponent select(IChatBaseComponent ichatbasecomponent, ChatMessageType.a chatmessagetype_a);
        }
    }
}
