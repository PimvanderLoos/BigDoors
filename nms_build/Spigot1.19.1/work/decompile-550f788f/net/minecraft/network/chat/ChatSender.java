package net.minecraft.network.chat;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record ChatSender(UUID profileId, @Nullable ProfilePublicKey profilePublicKey) {

    public static final ChatSender SYSTEM = new ChatSender(SystemUtils.NIL_UUID, (ProfilePublicKey) null);

    public boolean isSystem() {
        return ChatSender.SYSTEM.equals(this);
    }
}
