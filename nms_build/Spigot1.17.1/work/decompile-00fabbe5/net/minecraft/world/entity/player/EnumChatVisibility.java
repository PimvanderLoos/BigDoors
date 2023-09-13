package net.minecraft.world.entity.player;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.MathHelper;

public enum EnumChatVisibility {

    FULL(0, "options.chat.visibility.full"), SYSTEM(1, "options.chat.visibility.system"), HIDDEN(2, "options.chat.visibility.hidden");

    private static final EnumChatVisibility[] BY_ID = (EnumChatVisibility[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EnumChatVisibility::a)).toArray((i) -> {
        return new EnumChatVisibility[i];
    });
    private final int id;
    private final String key;

    private EnumChatVisibility(int i, String s) {
        this.id = i;
        this.key = s;
    }

    public int a() {
        return this.id;
    }

    public String b() {
        return this.key;
    }

    public static EnumChatVisibility a(int i) {
        return EnumChatVisibility.BY_ID[MathHelper.b(i, EnumChatVisibility.BY_ID.length)];
    }
}
