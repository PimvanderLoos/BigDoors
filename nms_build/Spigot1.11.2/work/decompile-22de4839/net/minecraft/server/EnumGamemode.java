package net.minecraft.server;

public enum EnumGamemode {

    NOT_SET(-1, "", ""), SURVIVAL(0, "survival", "s"), CREATIVE(1, "creative", "c"), ADVENTURE(2, "adventure", "a"), SPECTATOR(3, "spectator", "sp");

    int f;
    String g;
    String h;

    private EnumGamemode(int i, String s, String s1) {
        this.f = i;
        this.g = s;
        this.h = s1;
    }

    public int getId() {
        return this.f;
    }

    public String b() {
        return this.g;
    }

    public void a(PlayerAbilities playerabilities) {
        if (this == EnumGamemode.CREATIVE) {
            playerabilities.canFly = true;
            playerabilities.canInstantlyBuild = true;
            playerabilities.isInvulnerable = true;
        } else if (this == EnumGamemode.SPECTATOR) {
            playerabilities.canFly = true;
            playerabilities.canInstantlyBuild = false;
            playerabilities.isInvulnerable = true;
            playerabilities.isFlying = true;
        } else {
            playerabilities.canFly = false;
            playerabilities.canInstantlyBuild = false;
            playerabilities.isInvulnerable = false;
            playerabilities.isFlying = false;
        }

        playerabilities.mayBuild = !this.c();
    }

    public boolean c() {
        return this == EnumGamemode.ADVENTURE || this == EnumGamemode.SPECTATOR;
    }

    public boolean isCreative() {
        return this == EnumGamemode.CREATIVE;
    }

    public boolean e() {
        return this == EnumGamemode.SURVIVAL || this == EnumGamemode.ADVENTURE;
    }

    public static EnumGamemode getById(int i) {
        return a(i, EnumGamemode.SURVIVAL);
    }

    public static EnumGamemode a(int i, EnumGamemode enumgamemode) {
        EnumGamemode[] aenumgamemode = values();
        int j = aenumgamemode.length;

        for (int k = 0; k < j; ++k) {
            EnumGamemode enumgamemode1 = aenumgamemode[k];

            if (enumgamemode1.f == i) {
                return enumgamemode1;
            }
        }

        return enumgamemode;
    }

    public static EnumGamemode a(String s, EnumGamemode enumgamemode) {
        EnumGamemode[] aenumgamemode = values();
        int i = aenumgamemode.length;

        for (int j = 0; j < i; ++j) {
            EnumGamemode enumgamemode1 = aenumgamemode[j];

            if (enumgamemode1.g.equals(s) || enumgamemode1.h.equals(s)) {
                return enumgamemode1;
            }
        }

        return enumgamemode;
    }
}
