package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.INamable;

public record ChatMessageType(Optional<ChatMessageType.b> chat, Optional<ChatMessageType.b> overlay, Optional<ChatMessageType.a> narration) {

    public static final Codec<ChatMessageType> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ChatMessageType.b.CODEC.optionalFieldOf("chat").forGetter(ChatMessageType::chat), ChatMessageType.b.CODEC.optionalFieldOf("overlay").forGetter(ChatMessageType::overlay), ChatMessageType.a.CODEC.optionalFieldOf("narration").forGetter(ChatMessageType::narration)).apply(instance, ChatMessageType::new);
    });
    public static final ResourceKey<ChatMessageType> CHAT = create("chat");
    public static final ResourceKey<ChatMessageType> SYSTEM = create("system");
    public static final ResourceKey<ChatMessageType> GAME_INFO = create("game_info");
    public static final ResourceKey<ChatMessageType> SAY_COMMAND = create("say_command");
    public static final ResourceKey<ChatMessageType> MSG_COMMAND = create("msg_command");
    public static final ResourceKey<ChatMessageType> TEAM_MSG_COMMAND = create("team_msg_command");
    public static final ResourceKey<ChatMessageType> EMOTE_COMMAND = create("emote_command");
    public static final ResourceKey<ChatMessageType> TELLRAW_COMMAND = create("tellraw_command");

    private static ResourceKey<ChatMessageType> create(String s) {
        return ResourceKey.create(IRegistry.CHAT_TYPE_REGISTRY, new MinecraftKey(s));
    }

    public static Holder<ChatMessageType> bootstrap(IRegistry<ChatMessageType> iregistry) {
        RegistryGeneration.register(iregistry, ChatMessageType.CHAT, new ChatMessageType(Optional.of(ChatMessageType.b.decorated(ChatDecoration.withSender("chat.type.text"))), Optional.empty(), Optional.of(ChatMessageType.a.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatMessageType.a.a.CHAT))));
        RegistryGeneration.register(iregistry, ChatMessageType.SYSTEM, new ChatMessageType(Optional.of(ChatMessageType.b.undecorated()), Optional.empty(), Optional.of(ChatMessageType.a.undecorated(ChatMessageType.a.a.SYSTEM))));
        RegistryGeneration.register(iregistry, ChatMessageType.GAME_INFO, new ChatMessageType(Optional.empty(), Optional.of(ChatMessageType.b.undecorated()), Optional.empty()));
        RegistryGeneration.register(iregistry, ChatMessageType.SAY_COMMAND, new ChatMessageType(Optional.of(ChatMessageType.b.decorated(ChatDecoration.withSender("chat.type.announcement"))), Optional.empty(), Optional.of(ChatMessageType.a.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatMessageType.a.a.CHAT))));
        RegistryGeneration.register(iregistry, ChatMessageType.MSG_COMMAND, new ChatMessageType(Optional.of(ChatMessageType.b.decorated(ChatDecoration.directMessage("commands.message.display.incoming"))), Optional.empty(), Optional.of(ChatMessageType.a.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatMessageType.a.a.CHAT))));
        RegistryGeneration.register(iregistry, ChatMessageType.TEAM_MSG_COMMAND, new ChatMessageType(Optional.of(ChatMessageType.b.decorated(ChatDecoration.teamMessage("chat.type.team.text"))), Optional.empty(), Optional.of(ChatMessageType.a.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatMessageType.a.a.CHAT))));
        RegistryGeneration.register(iregistry, ChatMessageType.EMOTE_COMMAND, new ChatMessageType(Optional.of(ChatMessageType.b.decorated(ChatDecoration.withSender("chat.type.emote"))), Optional.empty(), Optional.of(ChatMessageType.a.decorated(ChatDecoration.withSender("chat.type.emote"), ChatMessageType.a.a.CHAT))));
        return RegistryGeneration.register(iregistry, ChatMessageType.TELLRAW_COMMAND, new ChatMessageType(Optional.of(ChatMessageType.b.undecorated()), Optional.empty(), Optional.of(ChatMessageType.a.undecorated(ChatMessageType.a.a.CHAT))));
    }

    public static record b(Optional<ChatDecoration> decoration) {

        public static final Codec<ChatMessageType.b> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ChatDecoration.CODEC.optionalFieldOf("decoration").forGetter(ChatMessageType.b::decoration)).apply(instance, ChatMessageType.b::new);
        });

        public static ChatMessageType.b undecorated() {
            return new ChatMessageType.b(Optional.empty());
        }

        public static ChatMessageType.b decorated(ChatDecoration chatdecoration) {
            return new ChatMessageType.b(Optional.of(chatdecoration));
        }

        public IChatBaseComponent decorate(IChatBaseComponent ichatbasecomponent, @Nullable ChatSender chatsender) {
            return (IChatBaseComponent) this.decoration.map((chatdecoration) -> {
                return chatdecoration.decorate(ichatbasecomponent, chatsender);
            }).orElse(ichatbasecomponent);
        }
    }

    public static record a(Optional<ChatDecoration> decoration, ChatMessageType.a.a priority) {

        public static final Codec<ChatMessageType.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ChatDecoration.CODEC.optionalFieldOf("decoration").forGetter(ChatMessageType.a::decoration), ChatMessageType.a.a.CODEC.fieldOf("priority").forGetter(ChatMessageType.a::priority)).apply(instance, ChatMessageType.a::new);
        });

        public static ChatMessageType.a undecorated(ChatMessageType.a.a chatmessagetype_a_a) {
            return new ChatMessageType.a(Optional.empty(), chatmessagetype_a_a);
        }

        public static ChatMessageType.a decorated(ChatDecoration chatdecoration, ChatMessageType.a.a chatmessagetype_a_a) {
            return new ChatMessageType.a(Optional.of(chatdecoration), chatmessagetype_a_a);
        }

        public IChatBaseComponent decorate(IChatBaseComponent ichatbasecomponent, @Nullable ChatSender chatsender) {
            return (IChatBaseComponent) this.decoration.map((chatdecoration) -> {
                return chatdecoration.decorate(ichatbasecomponent, chatsender);
            }).orElse(ichatbasecomponent);
        }

        public static enum a implements INamable {

            CHAT("chat", false), SYSTEM("system", true);

            public static final Codec<ChatMessageType.a.a> CODEC = INamable.fromEnum(ChatMessageType.a.a::values);
            private final String name;
            private final boolean interrupts;

            private a(String s, boolean flag) {
                this.name = s;
                this.interrupts = flag;
            }

            public boolean interrupts() {
                return this.interrupts;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }
}
