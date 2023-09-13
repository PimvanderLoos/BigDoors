package net.minecraft.server.packs.repository;

import net.minecraft.EnumChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.metadata.pack.ResourcePackInfo;

public enum EnumResourcePackVersion {

    TOO_OLD("old"), TOO_NEW("new"), COMPATIBLE("compatible");

    private final IChatBaseComponent description;
    private final IChatBaseComponent confirmation;

    private EnumResourcePackVersion(String s) {
        this.description = (new ChatMessage("pack.incompatible." + s)).withStyle(EnumChatFormat.GRAY);
        this.confirmation = new ChatMessage("pack.incompatible.confirm." + s);
    }

    public boolean isCompatible() {
        return this == EnumResourcePackVersion.COMPATIBLE;
    }

    public static EnumResourcePackVersion forFormat(int i, EnumResourcePackType enumresourcepacktype) {
        int j = enumresourcepacktype.getVersion(SharedConstants.getCurrentVersion());

        return i < j ? EnumResourcePackVersion.TOO_OLD : (i > j ? EnumResourcePackVersion.TOO_NEW : EnumResourcePackVersion.COMPATIBLE);
    }

    public static EnumResourcePackVersion forMetadata(ResourcePackInfo resourcepackinfo, EnumResourcePackType enumresourcepacktype) {
        return forFormat(resourcepackinfo.getPackFormat(), enumresourcepacktype);
    }

    public IChatBaseComponent getDescription() {
        return this.description;
    }

    public IChatBaseComponent getConfirmation() {
        return this.confirmation;
    }
}
