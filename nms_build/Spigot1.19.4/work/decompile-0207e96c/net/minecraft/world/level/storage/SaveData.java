package net.minecraft.world.level.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.levelgen.WorldOptions;

public interface SaveData {

    int ANVIL_VERSION_ID = 19133;
    int MCREGION_VERSION_ID = 19132;

    WorldDataConfiguration getDataConfiguration();

    void setDataConfiguration(WorldDataConfiguration worlddataconfiguration);

    boolean wasModded();

    Set<String> getKnownServerBrands();

    void setModdedInfo(String s, boolean flag);

    default void fillCrashReportCategory(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.setDetail("Known server brands", () -> {
            return String.join(", ", this.getKnownServerBrands());
        });
        crashreportsystemdetails.setDetail("Level was modded", () -> {
            return Boolean.toString(this.wasModded());
        });
        crashreportsystemdetails.setDetail("Level storage version", () -> {
            int i = this.getVersion();

            return String.format(Locale.ROOT, "0x%05X - %s", i, this.getStorageVersionName(i));
        });
    }

    default String getStorageVersionName(int i) {
        switch (i) {
            case 19132:
                return "McRegion";
            case 19133:
                return "Anvil";
            default:
                return "Unknown?";
        }
    }

    @Nullable
    NBTTagCompound getCustomBossEvents();

    void setCustomBossEvents(@Nullable NBTTagCompound nbttagcompound);

    IWorldDataServer overworldData();

    WorldSettings getLevelSettings();

    NBTTagCompound createTag(IRegistryCustom iregistrycustom, @Nullable NBTTagCompound nbttagcompound);

    boolean isHardcore();

    int getVersion();

    String getLevelName();

    EnumGamemode getGameType();

    void setGameType(EnumGamemode enumgamemode);

    boolean getAllowCommands();

    EnumDifficulty getDifficulty();

    void setDifficulty(EnumDifficulty enumdifficulty);

    boolean isDifficultyLocked();

    void setDifficultyLocked(boolean flag);

    GameRules getGameRules();

    @Nullable
    NBTTagCompound getLoadedPlayerTag();

    NBTTagCompound endDragonFightData();

    void setEndDragonFightData(NBTTagCompound nbttagcompound);

    WorldOptions worldGenOptions();

    boolean isFlatWorld();

    boolean isDebugWorld();

    Lifecycle worldGenSettingsLifecycle();

    default FeatureFlagSet enabledFeatures() {
        return this.getDataConfiguration().enabledFeatures();
    }
}
