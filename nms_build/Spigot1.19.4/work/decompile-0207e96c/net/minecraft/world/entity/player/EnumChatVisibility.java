package net.minecraft.world.entity.player;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum EnumChatVisibility implements OptionEnum {

    FULL(0, "options.chat.visibility.full"), SYSTEM(1, "options.chat.visibility.system"), HIDDEN(2, "options.chat.visibility.hidden");

    private static final IntFunction<EnumChatVisibility> BY_ID = ByIdMap.continuous(EnumChatVisibility::getId, values(), ByIdMap.a.WRAP);
    private final int id;
    private final String key;

    private EnumChatVisibility(int i, String s) {
        this.id = i;
        this.key = s;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public static EnumChatVisibility byId(int i) {
        return (EnumChatVisibility) EnumChatVisibility.BY_ID.apply(i);
    }
}
