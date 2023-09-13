package net.minecraft.world.level.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
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
    private final boolean requiresManualConversion;
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
        this.requiresManualConversion = flag;
    }

    public String getLevelId() {
        return this.levelId;
    }

    public String getLevelName() {
        return StringUtils.isEmpty(this.settings.levelName()) ? this.levelId : this.settings.levelName();
    }

    public File getIcon() {
        return this.icon;
    }

    public boolean requiresManualConversion() {
        return this.requiresManualConversion;
    }

    public long getLastPlayed() {
        return this.levelVersion.lastPlayed();
    }

    public int compareTo(WorldInfo worldinfo) {
        return this.levelVersion.lastPlayed() < worldinfo.levelVersion.lastPlayed() ? 1 : (this.levelVersion.lastPlayed() > worldinfo.levelVersion.lastPlayed() ? -1 : this.levelId.compareTo(worldinfo.levelId));
    }

    public WorldSettings getSettings() {
        return this.settings;
    }

    public EnumGamemode getGameMode() {
        return this.settings.gameType();
    }

    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    public boolean hasCheats() {
        return this.settings.allowCommands();
    }

    public IChatMutableComponent getWorldVersionName() {
        return (IChatMutableComponent) (UtilColor.isNullOrEmpty(this.levelVersion.minecraftVersionName()) ? new ChatMessage("selectWorld.versionUnknown") : new ChatComponentText(this.levelVersion.minecraftVersionName()));
    }

    public LevelVersion levelVersion() {
        return this.levelVersion;
    }

    public boolean markVersionInList() {
        return this.askToOpenWorld() || !SharedConstants.getCurrentVersion().isStable() && !this.levelVersion.snapshot() || this.backupStatus().shouldBackup();
    }

    public boolean askToOpenWorld() {
        return this.levelVersion.minecraftVersion().getVersion() > SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    }

    public WorldInfo.a backupStatus() {
        WorldVersion worldversion = SharedConstants.getCurrentVersion();
        int i = worldversion.getDataVersion().getVersion();
        int j = this.levelVersion.minecraftVersion().getVersion();

        return !worldversion.isStable() && j < i ? WorldInfo.a.UPGRADE_TO_SNAPSHOT : (j > i ? WorldInfo.a.DOWNGRADE : WorldInfo.a.NONE);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isDisabled() {
        return !this.isLocked() && !this.requiresManualConversion() ? !this.isCompatible() : true;
    }

    public boolean isCompatible() {
        return SharedConstants.getCurrentVersion().getDataVersion().isCompatible(this.levelVersion.minecraftVersion());
    }

    public IChatBaseComponent getInfo() {
        if (this.info == null) {
            this.info = this.createInfo();
        }

        return this.info;
    }

    private IChatBaseComponent createInfo() {
        if (this.isLocked()) {
            return (new ChatMessage("selectWorld.locked")).withStyle(EnumChatFormat.RED);
        } else if (this.requiresManualConversion()) {
            return (new ChatMessage("selectWorld.conversion")).withStyle(EnumChatFormat.RED);
        } else if (!this.isCompatible()) {
            return (new ChatMessage("selectWorld.incompatible_series")).withStyle(EnumChatFormat.RED);
        } else {
            Object object = this.isHardcore() ? (new ChatComponentText("")).append((IChatBaseComponent) (new ChatMessage("gameMode.hardcore")).withStyle(EnumChatFormat.DARK_RED)) : new ChatMessage("gameMode." + this.getGameMode().getName());

            if (this.hasCheats()) {
                ((IChatMutableComponent) object).append(", ").append((IChatBaseComponent) (new ChatMessage("selectWorld.cheats")));
            }

            IChatMutableComponent ichatmutablecomponent = this.getWorldVersionName();
            IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText(", ")).append((IChatBaseComponent) (new ChatMessage("selectWorld.version"))).append(" ");

            if (this.markVersionInList()) {
                ichatmutablecomponent1.append((IChatBaseComponent) ichatmutablecomponent.withStyle(this.askToOpenWorld() ? EnumChatFormat.RED : EnumChatFormat.ITALIC));
            } else {
                ichatmutablecomponent1.append((IChatBaseComponent) ichatmutablecomponent);
            }

            ((IChatMutableComponent) object).append((IChatBaseComponent) ichatmutablecomponent1);
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

        public boolean shouldBackup() {
            return this.shouldBackup;
        }

        public boolean isSevere() {
            return this.severe;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }
    }
}
