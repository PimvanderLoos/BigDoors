package net.minecraft.network.chat;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.EntityPlayer;

@FunctionalInterface
public interface ChatDecorator {

    ChatDecorator PLAIN = (entityplayer, ichatbasecomponent) -> {
        return CompletableFuture.completedFuture(ichatbasecomponent);
    };

    CompletableFuture<IChatBaseComponent> decorate(@Nullable EntityPlayer entityplayer, IChatBaseComponent ichatbasecomponent);

    default CompletableFuture<PlayerChatMessage> decorate(@Nullable EntityPlayer entityplayer, PlayerChatMessage playerchatmessage) {
        if (playerchatmessage.signedContent().isDecorated()) {
            return CompletableFuture.completedFuture(playerchatmessage);
        } else {
            CompletableFuture completablefuture = this.decorate(entityplayer, playerchatmessage.serverContent());

            Objects.requireNonNull(playerchatmessage);
            return completablefuture.thenApply(playerchatmessage::withUnsignedContent);
        }
    }

    static PlayerChatMessage attachIfNotDecorated(PlayerChatMessage playerchatmessage, IChatBaseComponent ichatbasecomponent) {
        return !playerchatmessage.signedContent().isDecorated() ? playerchatmessage.withUnsignedContent(ichatbasecomponent) : playerchatmessage;
    }
}
