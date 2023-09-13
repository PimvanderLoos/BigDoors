package net.minecraft.world;

import java.util.UUID;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.IChatBaseComponent;

public abstract class BossBattle {

    private final UUID id;
    public IChatBaseComponent name;
    protected float progress;
    public BossBattle.BarColor color;
    public BossBattle.BarStyle overlay;
    protected boolean darkenScreen;
    protected boolean playBossMusic;
    protected boolean createWorldFog;

    public BossBattle(UUID uuid, IChatBaseComponent ichatbasecomponent, BossBattle.BarColor bossbattle_barcolor, BossBattle.BarStyle bossbattle_barstyle) {
        this.id = uuid;
        this.name = ichatbasecomponent;
        this.color = bossbattle_barcolor;
        this.overlay = bossbattle_barstyle;
        this.progress = 1.0F;
    }

    public UUID getId() {
        return this.id;
    }

    public IChatBaseComponent getName() {
        return this.name;
    }

    public void setName(IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float f) {
        this.progress = f;
    }

    public BossBattle.BarColor getColor() {
        return this.color;
    }

    public void setColor(BossBattle.BarColor bossbattle_barcolor) {
        this.color = bossbattle_barcolor;
    }

    public BossBattle.BarStyle getOverlay() {
        return this.overlay;
    }

    public void setOverlay(BossBattle.BarStyle bossbattle_barstyle) {
        this.overlay = bossbattle_barstyle;
    }

    public boolean shouldDarkenScreen() {
        return this.darkenScreen;
    }

    public BossBattle setDarkenScreen(boolean flag) {
        this.darkenScreen = flag;
        return this;
    }

    public boolean shouldPlayBossMusic() {
        return this.playBossMusic;
    }

    public BossBattle setPlayBossMusic(boolean flag) {
        this.playBossMusic = flag;
        return this;
    }

    public BossBattle setCreateWorldFog(boolean flag) {
        this.createWorldFog = flag;
        return this;
    }

    public boolean shouldCreateWorldFog() {
        return this.createWorldFog;
    }

    public static enum BarColor {

        PINK("pink", EnumChatFormat.RED), BLUE("blue", EnumChatFormat.BLUE), RED("red", EnumChatFormat.DARK_RED), GREEN("green", EnumChatFormat.GREEN), YELLOW("yellow", EnumChatFormat.YELLOW), PURPLE("purple", EnumChatFormat.DARK_BLUE), WHITE("white", EnumChatFormat.WHITE);

        private final String name;
        private final EnumChatFormat formatting;

        private BarColor(String s, EnumChatFormat enumchatformat) {
            this.name = s;
            this.formatting = enumchatformat;
        }

        public EnumChatFormat getFormatting() {
            return this.formatting;
        }

        public String getName() {
            return this.name;
        }

        public static BossBattle.BarColor byName(String s) {
            BossBattle.BarColor[] abossbattle_barcolor = values();
            int i = abossbattle_barcolor.length;

            for (int j = 0; j < i; ++j) {
                BossBattle.BarColor bossbattle_barcolor = abossbattle_barcolor[j];

                if (bossbattle_barcolor.name.equals(s)) {
                    return bossbattle_barcolor;
                }
            }

            return BossBattle.BarColor.WHITE;
        }
    }

    public static enum BarStyle {

        PROGRESS("progress"), NOTCHED_6("notched_6"), NOTCHED_10("notched_10"), NOTCHED_12("notched_12"), NOTCHED_20("notched_20");

        private final String name;

        private BarStyle(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        public static BossBattle.BarStyle byName(String s) {
            BossBattle.BarStyle[] abossbattle_barstyle = values();
            int i = abossbattle_barstyle.length;

            for (int j = 0; j < i; ++j) {
                BossBattle.BarStyle bossbattle_barstyle = abossbattle_barstyle[j];

                if (bossbattle_barstyle.name.equals(s)) {
                    return bossbattle_barstyle;
                }
            }

            return BossBattle.BarStyle.PROGRESS;
        }
    }
}
