package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.EntityPlayer;

@FunctionalInterface
public interface ChatDecorator {

    ChatDecorator PLAIN = (entityplayer, ichatbasecomponent) -> {
        return CompletableFuture.completedFuture(ichatbasecomponent);
    };

    CompletableFuture<IChatBaseComponent> decorate(@Nullable EntityPlayer entityplayer, IChatBaseComponent ichatbasecomponent);
}
