package net.minecraft.network.chat;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.game.ClientboundPlayerChatHeaderPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;

public interface OutgoingPlayerChatMessage {

    IChatBaseComponent serverContent();

    void sendToPlayer(EntityPlayer entityplayer, boolean flag, ChatMessageType.a chatmessagetype_a);

    void sendHeadersToRemainingPlayers(PlayerList playerlist);

    static OutgoingPlayerChatMessage create(PlayerChatMessage playerchatmessage) {
        return (OutgoingPlayerChatMessage) (playerchatmessage.signer().isSystem() ? new OutgoingPlayerChatMessage.a(playerchatmessage) : new OutgoingPlayerChatMessage.b(playerchatmessage));
    }

    public static class a implements OutgoingPlayerChatMessage {

        private final PlayerChatMessage message;

        public a(PlayerChatMessage playerchatmessage) {
            this.message = playerchatmessage;
        }

        @Override
        public IChatBaseComponent serverContent() {
            return this.message.serverContent();
        }

        @Override
        public void sendToPlayer(EntityPlayer entityplayer, boolean flag, ChatMessageType.a chatmessagetype_a) {
            PlayerChatMessage playerchatmessage = this.message.filter(flag);

            if (!playerchatmessage.isFullyFiltered()) {
                IRegistryCustom iregistrycustom = entityplayer.level.registryAccess();
                ChatMessageType.b chatmessagetype_b = chatmessagetype_a.toNetwork(iregistrycustom);

                entityplayer.connection.send(new ClientboundPlayerChatPacket(playerchatmessage, chatmessagetype_b));
                entityplayer.connection.addPendingMessage(playerchatmessage);
            }

        }

        @Override
        public void sendHeadersToRemainingPlayers(PlayerList playerlist) {}
    }

    public static class b implements OutgoingPlayerChatMessage {

        private final PlayerChatMessage message;
        private final Set<EntityPlayer> playersWithFullMessage = Sets.newIdentityHashSet();

        public b(PlayerChatMessage playerchatmessage) {
            this.message = playerchatmessage;
        }

        @Override
        public IChatBaseComponent serverContent() {
            return this.message.serverContent();
        }

        @Override
        public void sendToPlayer(EntityPlayer entityplayer, boolean flag, ChatMessageType.a chatmessagetype_a) {
            PlayerChatMessage playerchatmessage = this.message.filter(flag);

            if (!playerchatmessage.isFullyFiltered()) {
                this.playersWithFullMessage.add(entityplayer);
                IRegistryCustom iregistrycustom = entityplayer.level.registryAccess();
                ChatMessageType.b chatmessagetype_b = chatmessagetype_a.toNetwork(iregistrycustom);

                entityplayer.connection.send(new ClientboundPlayerChatPacket(playerchatmessage, chatmessagetype_b), PacketSendListener.exceptionallySend(() -> {
                    return new ClientboundPlayerChatHeaderPacket(this.message);
                }));
                entityplayer.connection.addPendingMessage(playerchatmessage);
            }

        }

        @Override
        public void sendHeadersToRemainingPlayers(PlayerList playerlist) {
            playerlist.broadcastMessageHeader(this.message, this.playersWithFullMessage);
        }
    }
}
