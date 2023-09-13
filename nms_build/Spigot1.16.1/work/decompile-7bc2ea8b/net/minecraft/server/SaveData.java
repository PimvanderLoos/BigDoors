package net.minecraft.server;

import java.util.Set;
import javax.annotation.Nullable;

public interface SaveData {

    DataPackConfiguration C();

    void a(DataPackConfiguration datapackconfiguration);

    boolean E();

    Set<String> F();

    void a(String s, boolean flag);

    default void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Known server brands", () -> {
            return String.join(", ", this.F());
        });
        crashreportsystemdetails.a("Level was modded", () -> {
            return Boolean.toString(this.E());
        });
        crashreportsystemdetails.a("Level storage version", () -> {
            int i = this.y();

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

    IWorldDataServer G();

    NBTTagCompound a(IRegistryCustom iregistrycustom, @Nullable NBTTagCompound nbttagcompound);

    boolean isHardcore();

    int y();

    String getName();

    EnumGamemode getGameType();

    void setGameType(EnumGamemode enumgamemode);

    boolean n();

    EnumDifficulty getDifficulty();

    void setDifficulty(EnumDifficulty enumdifficulty);

    boolean isDifficultyLocked();

    void d(boolean flag);

    GameRules p();

    NBTTagCompound x();

    NBTTagCompound B();

    void a(NBTTagCompound nbttagcompound);

    GeneratorSettings getGeneratorSettings();
}
