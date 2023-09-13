package net.minecraft.network.chat;

import java.util.Optional;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record PlayerChatMessage(IChatBaseComponent signedContent, MessageSignature signature, Optional<IChatBaseComponent> unsignedContent) {

    public static PlayerChatMessage signed(IChatBaseComponent ichatbasecomponent, MessageSignature messagesignature) {
        return new PlayerChatMessage(ichatbasecomponent, messagesignature, Optional.empty());
    }

    public static PlayerChatMessage signed(String s, MessageSignature messagesignature) {
        return signed((IChatBaseComponent) IChatBaseComponent.literal(s), messagesignature);
    }

    public static PlayerChatMessage signed(IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, MessageSignature messagesignature, boolean flag) {
        return ichatbasecomponent.equals(ichatbasecomponent1) ? signed(ichatbasecomponent, messagesignature) : (!flag ? signed(ichatbasecomponent, messagesignature).withUnsignedContent(ichatbasecomponent1) : signed(ichatbasecomponent1, messagesignature));
    }

    public static FilteredText<PlayerChatMessage> filteredSigned(FilteredText<IChatBaseComponent> filteredtext, FilteredText<IChatBaseComponent> filteredtext1, MessageSignature messagesignature, boolean flag) {
        IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) filteredtext.raw();
        IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) filteredtext1.raw();
        PlayerChatMessage playerchatmessage = signed(ichatbasecomponent, ichatbasecomponent1, messagesignature, flag);

        if (filteredtext1.isFiltered()) {
            PlayerChatMessage playerchatmessage1 = (PlayerChatMessage) SystemUtils.mapNullable((IChatBaseComponent) filteredtext1.filtered(), PlayerChatMessage::unsigned);

            return new FilteredText<>(playerchatmessage, playerchatmessage1);
        } else {
            return FilteredText.passThrough(playerchatmessage);
        }
    }

    public static PlayerChatMessage unsigned(IChatBaseComponent ichatbasecomponent) {
        return new PlayerChatMessage(ichatbasecomponent, MessageSignature.unsigned(), Optional.empty());
    }

    public PlayerChatMessage withUnsignedContent(IChatBaseComponent ichatbasecomponent) {
        return new PlayerChatMessage(this.signedContent, this.signature, Optional.of(ichatbasecomponent));
    }

    public boolean verify(ProfilePublicKey profilepublickey) {
        return this.signature.verify(profilepublickey.createSignatureValidator(), this.signedContent);
    }

    public boolean verify(EntityPlayer entityplayer) {
        ProfilePublicKey profilepublickey = entityplayer.getProfilePublicKey();

        return profilepublickey == null || this.verify(profilepublickey);
    }

    public boolean verify(CommandListenerWrapper commandlistenerwrapper) {
        EntityPlayer entityplayer = commandlistenerwrapper.getPlayer();

        return entityplayer == null || this.verify(entityplayer);
    }

    public IChatBaseComponent serverContent() {
        return (IChatBaseComponent) this.unsignedContent.orElse(this.signedContent);
    }
}
