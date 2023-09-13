package net.minecraft.advancements;

import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public enum AdvancementFrameType {

    TASK("task", 0, EnumChatFormat.GREEN), CHALLENGE("challenge", 26, EnumChatFormat.DARK_PURPLE), GOAL("goal", 52, EnumChatFormat.GREEN);

    private final String d;
    private final int e;
    private final EnumChatFormat f;
    private final IChatBaseComponent g;

    private AdvancementFrameType(String s, int i, EnumChatFormat enumchatformat) {
        this.d = s;
        this.e = i;
        this.f = enumchatformat;
        this.g = new ChatMessage("advancements.toast." + s);
    }

    public String a() {
        return this.d;
    }

    public static AdvancementFrameType a(String s) {
        AdvancementFrameType[] aadvancementframetype = values();
        int i = aadvancementframetype.length;

        for (int j = 0; j < i; ++j) {
            AdvancementFrameType advancementframetype = aadvancementframetype[j];

            if (advancementframetype.d.equals(s)) {
                return advancementframetype;
            }
        }

        throw new IllegalArgumentException("Unknown frame type '" + s + "'");
    }

    public EnumChatFormat c() {
        return this.f;
    }
}
