package net.minecraft.network.chat;

import net.minecraft.server.level.EntityPlayer;

public interface OutgoingChatMessage {

    IChatBaseComponent content();

    void sendToPlayer(EntityPlayer entityplayer, boolean flag, ChatMessageType.a chatmessagetype_a);

    static OutgoingChatMessage create(PlayerChatMessage playerchatmessage) {
        return (OutgoingChatMessage) (playerchatmessage.isSystem() ? new OutgoingChatMessage.a(playerchatmessage.decoratedContent()) : new OutgoingChatMessage.b(playerchatmessage));
    }

    public static record a(IChatBaseComponent content) implements OutgoingChatMessage {

        @Override
        public void sendToPlayer(EntityPlayer entityplayer, boolean flag, ChatMessageType.a chatmessagetype_a) {
            entityplayer.connection.sendDisguisedChatMessage(this.content, chatmessagetype_a);
        }
    }

    public static record b(PlayerChatMessage message) implements OutgoingChatMessage {

        @Override
        public IChatBaseComponent content() {
            return this.message.decoratedContent();
        }

        @Override
        public void sendToPlayer(EntityPlayer entityplayer, boolean flag, ChatMessageType.a chatmessagetype_a) {
            PlayerChatMessage playerchatmessage = this.message.filter(flag);

            if (!playerchatmessage.isFullyFiltered()) {
                entityplayer.connection.sendPlayerChatMessage(playerchatmessage, chatmessagetype_a);
            }

        }
    }
}
