package net.minecraft.advancements;

import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public enum AdvancementFrameType {

    TASK("task", 0, EnumChatFormat.GREEN), CHALLENGE("challenge", 26, EnumChatFormat.DARK_PURPLE), GOAL("goal", 52, EnumChatFormat.GREEN);

    private final String name;
    private final int texture;
    private final EnumChatFormat chatColor;
    private final IChatBaseComponent displayName;

    private AdvancementFrameType(String s, int i, EnumChatFormat enumchatformat) {
        this.name = s;
        this.texture = i;
        this.chatColor = enumchatformat;
        this.displayName = new ChatMessage("advancements.toast." + s);
    }

    public String a() {
        return this.name;
    }

    public int b() {
        return this.texture;
    }

    public static AdvancementFrameType a(String s) {
        AdvancementFrameType[] aadvancementframetype = values();
        int i = aadvancementframetype.length;

        for (int j = 0; j < i; ++j) {
            AdvancementFrameType advancementframetype = aadvancementframetype[j];

            if (advancementframetype.name.equals(s)) {
                return advancementframetype;
            }
        }

        throw new IllegalArgumentException("Unknown frame type '" + s + "'");
    }

    public EnumChatFormat c() {
        return this.chatColor;
    }

    public IChatBaseComponent d() {
        return this.displayName;
    }
}
