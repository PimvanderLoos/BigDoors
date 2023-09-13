package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.FilteredText;

@FunctionalInterface
public interface ChatDecorator {

    ChatDecorator PLAIN = (entityplayer, ichatbasecomponent) -> {
        return CompletableFuture.completedFuture(ichatbasecomponent);
    };

    CompletableFuture<IChatBaseComponent> decorate(@Nullable EntityPlayer entityplayer, IChatBaseComponent ichatbasecomponent);

    default CompletableFuture<FilteredText<IChatBaseComponent>> decorateFiltered(@Nullable EntityPlayer entityplayer, FilteredText<IChatBaseComponent> filteredtext) {
        CompletableFuture<IChatBaseComponent> completablefuture = this.decorate(entityplayer, (IChatBaseComponent) filteredtext.raw());

        if (!filteredtext.isFiltered()) {
            return completablefuture.thenApply(FilteredText::passThrough);
        } else if (filteredtext.filtered() == null) {
            return completablefuture.thenApply(FilteredText::fullyFiltered);
        } else {
            CompletableFuture<IChatBaseComponent> completablefuture1 = this.decorate(entityplayer, (IChatBaseComponent) filteredtext.filtered());

            return CompletableFuture.allOf(completablefuture, completablefuture1).thenApply((ovoid) -> {
                return new FilteredText<>((IChatBaseComponent) completablefuture.join(), (IChatBaseComponent) completablefuture1.join());
            });
        }
    }

    default CompletableFuture<FilteredText<PlayerChatMessage>> decorateChat(@Nullable EntityPlayer entityplayer, FilteredText<IChatBaseComponent> filteredtext, MessageSignature messagesignature, boolean flag) {
        return this.decorateFiltered(entityplayer, filteredtext).thenApply((filteredtext1) -> {
            return PlayerChatMessage.filteredSigned(filteredtext, filteredtext1, messagesignature, flag);
        });
    }
}
