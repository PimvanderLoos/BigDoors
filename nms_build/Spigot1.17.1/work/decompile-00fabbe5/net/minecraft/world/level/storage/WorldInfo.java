package net.minecraft.world.level.storage;

import com.mojang.bridge.game.GameVersion;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.UtilColor;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.WorldSettings;
import org.apache.commons.lang3.StringUtils;

public class WorldInfo implements Comparable<WorldInfo> {

    private final WorldSettings settings;
    private final LevelVersion levelVersion;
    private final String levelId;
    private final boolean requiresConversion;
    private final boolean locked;
    private final File icon;
    @Nullable
    private IChatBaseComponent info;

    public WorldInfo(WorldSettings worldsettings, LevelVersion levelversion, String s, boolean flag, boolean flag1, File file) {
        this.settings = worldsettings;
        this.levelVersion = levelversion;
        this.levelId = s;
        this.locked = flag1;
        this.icon = file;
        this.requiresConversion = flag;
    }

    public String a() {
        return this.levelId;
    }

    public String b() {
        return StringUtils.isEmpty(this.settings.getLevelName()) ? this.levelId : this.settings.getLevelName();
    }

    public File c() {
        return this.icon;
    }

    public boolean d() {
        return this.requiresConversion;
    }

    public long e() {
        return this.levelVersion.b();
    }

    public int compareTo(WorldInfo worldinfo) {
        return this.levelVersion.b() < worldinfo.levelVersion.b() ? 1 : (this.levelVersion.b() > worldinfo.levelVersion.b() ? -1 : this.levelId.compareTo(worldinfo.levelId));
    }

    public WorldSettings f() {
        return this.settings;
    }

    public EnumGamemode g() {
        return this.settings.getGameType();
    }

    public boolean h() {
        return this.settings.isHardcore();
    }

    public boolean i() {
        return this.settings.e();
    }

    public IChatMutableComponent j() {
        return (IChatMutableComponent) (UtilColor.b(this.levelVersion.c()) ? new ChatMessage("selectWorld.versionUnknown") : new ChatComponentText(this.levelVersion.c()));
    }

    public LevelVersion k() {
        return this.levelVersion;
    }

    public boolean l() {
        return this.m() || !SharedConstants.getGameVersion().isStable() && !this.levelVersion.e() || this.n().a();
    }

    public boolean m() {
        return this.levelVersion.d() > SharedConstants.getGameVersion().getWorldVersion();
    }

    public WorldInfo.a n() {
        GameVersion gameversion = SharedConstants.getGameVersion();
        int i = gameversion.getWorldVersion();
        int j = this.levelVersion.d();

        return !gameversion.isStable() && j < i ? WorldInfo.a.UPGRADE_TO_SNAPSHOT : (j > i ? WorldInfo.a.DOWNGRADE : WorldInfo.a.NONE);
    }

    public boolean o() {
        return this.locked;
    }

    public boolean p() {
        int i = this.levelVersion.d();
        boolean flag = i > 2692 && i <= 2706;

        return flag;
    }

    public boolean q() {
        return this.o() || this.p();
    }

    public IChatBaseComponent r() {
        if (this.info == null) {
            this.info = this.s();
        }

        return this.info;
    }

    private IChatBaseComponent s() {
        if (this.o()) {
            return (new ChatMessage("selectWorld.locked")).a(EnumChatFormat.RED);
        } else if (this.p()) {
            return (new ChatMessage("selectWorld.pre_worldheight")).a(EnumChatFormat.RED);
        } else if (this.d()) {
            return new ChatMessage("selectWorld.conversion");
        } else {
            Object object = this.h() ? (new ChatComponentText("")).addSibling((new ChatMessage("gameMode.hardcore")).a(EnumChatFormat.DARK_RED)) : new ChatMessage("gameMode." + this.g().b());

            if (this.i()) {
                ((IChatMutableComponent) object).c(", ").addSibling(new ChatMessage("selectWorld.cheats"));
            }

            IChatMutableComponent ichatmutablecomponent = this.j();
            IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText(", ")).addSibling(new ChatMessage("selectWorld.version")).c(" ");

            if (this.l()) {
                ichatmutablecomponent1.addSibling(ichatmutablecomponent.a(this.m() ? EnumChatFormat.RED : EnumChatFormat.ITALIC));
            } else {
                ichatmutablecomponent1.addSibling(ichatmutablecomponent);
            }

            ((IChatMutableComponent) object).addSibling(ichatmutablecomponent1);
            return (IChatBaseComponent) object;
        }
    }

    public static enum a {

        NONE(false, false, ""), DOWNGRADE(true, true, "downgrade"), UPGRADE_TO_SNAPSHOT(true, false, "snapshot");

        private final boolean shouldBackup;
        private final boolean severe;
        private final String translationKey;

        private a(boolean flag, boolean flag1, String s) {
            this.shouldBackup = flag;
            this.severe = flag1;
            this.translationKey = s;
        }

        public boolean a() {
            return this.shouldBackup;
        }

        public boolean b() {
            return this.severe;
        }

        public String c() {
            return this.translationKey;
        }
    }
}
