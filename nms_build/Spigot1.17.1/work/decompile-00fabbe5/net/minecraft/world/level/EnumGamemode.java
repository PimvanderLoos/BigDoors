package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.PlayerAbilities;

public enum EnumGamemode {

    SURVIVAL(0, "survival"), CREATIVE(1, "creative"), ADVENTURE(2, "adventure"), SPECTATOR(3, "spectator");

    public static final EnumGamemode DEFAULT_MODE = EnumGamemode.SURVIVAL;
    private static final int NOT_SET = -1;
    private final int id;
    private final String name;
    private final IChatBaseComponent shortName;
    private final IChatBaseComponent longName;

    private EnumGamemode(int i, String s) {
        this.id = i;
        this.name = s;
        this.shortName = new ChatMessage("selectWorld.gameMode." + s);
        this.longName = new ChatMessage("gameMode." + s);
    }

    public int getId() {
        return this.id;
    }

    public String b() {
        return this.name;
    }

    public IChatBaseComponent c() {
        return this.longName;
    }

    public IChatBaseComponent d() {
        return this.shortName;
    }

    public void a(PlayerAbilities playerabilities) {
        if (this == EnumGamemode.CREATIVE) {
            playerabilities.mayfly = true;
            playerabilities.instabuild = true;
            playerabilities.invulnerable = true;
        } else if (this == EnumGamemode.SPECTATOR) {
            playerabilities.mayfly = true;
            playerabilities.instabuild = false;
            playerabilities.invulnerable = true;
            playerabilities.flying = true;
        } else {
            playerabilities.mayfly = false;
            playerabilities.instabuild = false;
            playerabilities.invulnerable = false;
            playerabilities.flying = false;
        }

        playerabilities.mayBuild = !this.e();
    }

    public boolean e() {
        return this == EnumGamemode.ADVENTURE || this == EnumGamemode.SPECTATOR;
    }

    public boolean isCreative() {
        return this == EnumGamemode.CREATIVE;
    }

    public boolean g() {
        return this == EnumGamemode.SURVIVAL || this == EnumGamemode.ADVENTURE;
    }

    public static EnumGamemode getById(int i) {
        return a(i, EnumGamemode.DEFAULT_MODE);
    }

    public static EnumGamemode a(int i, EnumGamemode enumgamemode) {
        EnumGamemode[] aenumgamemode = values();
        int j = aenumgamemode.length;

        for (int k = 0; k < j; ++k) {
            EnumGamemode enumgamemode1 = aenumgamemode[k];

            if (enumgamemode1.id == i) {
                return enumgamemode1;
            }
        }

        return enumgamemode;
    }

    public static EnumGamemode a(String s) {
        return a(s, EnumGamemode.SURVIVAL);
    }

    public static EnumGamemode a(String s, EnumGamemode enumgamemode) {
        EnumGamemode[] aenumgamemode = values();
        int i = aenumgamemode.length;

        for (int j = 0; j < i; ++j) {
            EnumGamemode enumgamemode1 = aenumgamemode[j];

            if (enumgamemode1.name.equals(s)) {
                return enumgamemode1;
            }
        }

        return enumgamemode;
    }

    public static int a(@Nullable EnumGamemode enumgamemode) {
        return enumgamemode != null ? enumgamemode.id : -1;
    }

    @Nullable
    public static EnumGamemode b(int i) {
        return i == -1 ? null : getById(i);
    }
}
