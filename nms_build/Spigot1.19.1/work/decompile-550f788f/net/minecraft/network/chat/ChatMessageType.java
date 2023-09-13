package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;

public record ChatMessageType(ChatDecoration chat, ChatDecoration narration) {

    public static final Codec<ChatMessageType> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ChatDecoration.CODEC.fieldOf("chat").forGetter(ChatMessageType::chat), ChatDecoration.CODEC.fieldOf("narration").forGetter(ChatMessageType::narration)).apply(instance, ChatMessageType::new);
    });
    public static final ChatDecoration DEFAULT_CHAT_DECORATION = ChatDecoration.withSender("chat.type.text");
    public static final ResourceKey<ChatMessageType> CHAT = create("chat");
    public static final ResourceKey<ChatMessageType> SAY_COMMAND = create("say_command");
    public static final ResourceKey<ChatMessageType> MSG_COMMAND_INCOMING = create("msg_command_incoming");
    public static final ResourceKey<ChatMessageType> MSG_COMMAND_OUTGOING = create("msg_command_outgoing");
    public static final ResourceKey<ChatMessageType> TEAM_MSG_COMMAND_INCOMING = create("team_msg_command_incoming");
    public static final ResourceKey<ChatMessageType> TEAM_MSG_COMMAND_OUTGOING = create("team_msg_command_outgoing");
    public static final ResourceKey<ChatMessageType> EMOTE_COMMAND = create("emote_command");

    private static ResourceKey<ChatMessageType> create(String s) {
        return ResourceKey.create(IRegistry.CHAT_TYPE_REGISTRY, new MinecraftKey(s));
    }

    public static Holder<ChatMessageType> bootstrap(IRegistry<ChatMessageType> iregistry) {
        RegistryGeneration.register(iregistry, ChatMessageType.CHAT, new ChatMessageType(ChatMessageType.DEFAULT_CHAT_DECORATION, ChatDecoration.withSender("chat.type.text.narrate")));
        RegistryGeneration.register(iregistry, ChatMessageType.SAY_COMMAND, new ChatMessageType(ChatDecoration.withSender("chat.type.announcement"), ChatDecoration.withSender("chat.type.text.narrate")));
        RegistryGeneration.register(iregistry, ChatMessageType.MSG_COMMAND_INCOMING, new ChatMessageType(ChatDecoration.incomingDirectMessage("commands.message.display.incoming"), ChatDecoration.withSender("chat.type.text.narrate")));
        RegistryGeneration.register(iregistry, ChatMessageType.MSG_COMMAND_OUTGOING, new ChatMessageType(ChatDecoration.outgoingDirectMessage("commands.message.display.outgoing"), ChatDecoration.withSender("chat.type.text.narrate")));
        RegistryGeneration.register(iregistry, ChatMessageType.TEAM_MSG_COMMAND_INCOMING, new ChatMessageType(ChatDecoration.teamMessage("chat.type.team.text"), ChatDecoration.withSender("chat.type.text.narrate")));
        RegistryGeneration.register(iregistry, ChatMessageType.TEAM_MSG_COMMAND_OUTGOING, new ChatMessageType(ChatDecoration.teamMessage("chat.type.team.sent"), ChatDecoration.withSender("chat.type.text.narrate")));
        return RegistryGeneration.register(iregistry, ChatMessageType.EMOTE_COMMAND, new ChatMessageType(ChatDecoration.withSender("chat.type.emote"), ChatDecoration.withSender("chat.type.emote")));
    }

    public static ChatMessageType.a bind(ResourceKey<ChatMessageType> resourcekey, Entity entity) {
        return bind(resourcekey, entity.level.registryAccess(), entity.getDisplayName());
    }

    public static ChatMessageType.a bind(ResourceKey<ChatMessageType> resourcekey, CommandListenerWrapper commandlistenerwrapper) {
        return bind(resourcekey, commandlistenerwrapper.registryAccess(), commandlistenerwrapper.getDisplayName());
    }

    public static ChatMessageType.a bind(ResourceKey<ChatMessageType> resourcekey, IRegistryCustom iregistrycustom, IChatBaseComponent ichatbasecomponent) {
        IRegistry<ChatMessageType> iregistry = iregistrycustom.registryOrThrow(IRegistry.CHAT_TYPE_REGISTRY);

        return ((ChatMessageType) iregistry.getOrThrow(resourcekey)).bind(ichatbasecomponent);
    }

    public ChatMessageType.a bind(IChatBaseComponent ichatbasecomponent) {
        return new ChatMessageType.a(this, ichatbasecomponent);
    }

    public static record a(ChatMessageType chatType, IChatBaseComponent name, @Nullable IChatBaseComponent targetName) {

        a(ChatMessageType chatmessagetype, IChatBaseComponent ichatbasecomponent) {
            this(chatmessagetype, ichatbasecomponent, (IChatBaseComponent) null);
        }

        public IChatBaseComponent decorate(IChatBaseComponent ichatbasecomponent) {
            return this.chatType.chat().decorate(ichatbasecomponent, this);
        }

        public IChatBaseComponent decorateNarration(IChatBaseComponent ichatbasecomponent) {
            return this.chatType.narration().decorate(ichatbasecomponent, this);
        }

        public ChatMessageType.a withTargetName(IChatBaseComponent ichatbasecomponent) {
            return new ChatMessageType.a(this.chatType, this.name, ichatbasecomponent);
        }

        public ChatMessageType.b toNetwork(IRegistryCustom iregistrycustom) {
            IRegistry<ChatMessageType> iregistry = iregistrycustom.registryOrThrow(IRegistry.CHAT_TYPE_REGISTRY);

            return new ChatMessageType.b(iregistry.getId(this.chatType), this.name, this.targetName);
        }
    }

    public static record b(int chatType, IChatBaseComponent name, @Nullable IChatBaseComponent targetName) {

        public b(PacketDataSerializer packetdataserializer) {
            this(packetdataserializer.readVarInt(), packetdataserializer.readComponent(), (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeVarInt(this.chatType);
            packetdataserializer.writeComponent(this.name);
            packetdataserializer.writeNullable(this.targetName, PacketDataSerializer::writeComponent);
        }

        public Optional<ChatMessageType.a> resolve(IRegistryCustom iregistrycustom) {
            IRegistry<ChatMessageType> iregistry = iregistrycustom.registryOrThrow(IRegistry.CHAT_TYPE_REGISTRY);
            ChatMessageType chatmessagetype = (ChatMessageType) iregistry.byId(this.chatType);

            return Optional.ofNullable(chatmessagetype).map((chatmessagetype1) -> {
                return new ChatMessageType.a(chatmessagetype1, this.name, this.targetName);
            });
        }
    }
}
