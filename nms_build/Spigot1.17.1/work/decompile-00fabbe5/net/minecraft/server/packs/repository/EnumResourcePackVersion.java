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
        this.description = (new ChatMessage("pack.incompatible." + s)).a(EnumChatFormat.GRAY);
        this.confirmation = new ChatMessage("pack.incompatible.confirm." + s);
    }

    public boolean a() {
        return this == EnumResourcePackVersion.COMPATIBLE;
    }

    public static EnumResourcePackVersion a(int i, EnumResourcePackType enumresourcepacktype) {
        int j = enumresourcepacktype.a(SharedConstants.getGameVersion());

        return i < j ? EnumResourcePackVersion.TOO_OLD : (i > j ? EnumResourcePackVersion.TOO_NEW : EnumResourcePackVersion.COMPATIBLE);
    }

    public static EnumResourcePackVersion a(ResourcePackInfo resourcepackinfo, EnumResourcePackType enumresourcepacktype) {
        return a(resourcepackinfo.b(), enumresourcepacktype);
    }

    public IChatBaseComponent b() {
        return this.description;
    }

    public IChatBaseComponent c() {
        return this.confirmation;
    }
}
