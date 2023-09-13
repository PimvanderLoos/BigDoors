package net.minecraft.world.level.storage;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.DataPackConfiguration;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.GeneratorSettings;

public interface SaveData {

    DataPackConfiguration D();

    void a(DataPackConfiguration datapackconfiguration);

    boolean F();

    Set<String> G();

    void a(String s, boolean flag);

    default void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Known server brands", () -> {
            return String.join(", ", this.G());
        });
        crashreportsystemdetails.a("Level was modded", () -> {
            return Boolean.toString(this.F());
        });
        crashreportsystemdetails.a("Level storage version", () -> {
            int i = this.z();

            return String.format("0x%05X - %s", i, this.i(i));
        });
    }

    default String i(int i) {
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

    IWorldDataServer H();

    NBTTagCompound a(IRegistryCustom iregistrycustom, @Nullable NBTTagCompound nbttagcompound);

    boolean isHardcore();

    int z();

    String getName();

    EnumGamemode getGameType();

    void setGameType(EnumGamemode enumgamemode);

    boolean o();

    EnumDifficulty getDifficulty();

    void setDifficulty(EnumDifficulty enumdifficulty);

    boolean isDifficultyLocked();

    void d(boolean flag);

    GameRules q();

    NBTTagCompound y();

    NBTTagCompound C();

    void a(NBTTagCompound nbttagcompound);

    GeneratorSettings getGeneratorSettings();
}
