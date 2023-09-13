package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.EnumDifficulty;

public final class WorldSettings {

    public String levelName;
    private final EnumGamemode gameType;
    public boolean hardcore;
    private final EnumDifficulty difficulty;
    private final boolean allowCommands;
    private final GameRules gameRules;
    private final WorldDataConfiguration dataConfiguration;

    public WorldSettings(String s, EnumGamemode enumgamemode, boolean flag, EnumDifficulty enumdifficulty, boolean flag1, GameRules gamerules, WorldDataConfiguration worlddataconfiguration) {
        this.levelName = s;
        this.gameType = enumgamemode;
        this.hardcore = flag;
        this.difficulty = enumdifficulty;
        this.allowCommands = flag1;
        this.gameRules = gamerules;
        this.dataConfiguration = worlddataconfiguration;
    }

    public static WorldSettings parse(Dynamic<?> dynamic, WorldDataConfiguration worlddataconfiguration) {
        EnumGamemode enumgamemode = EnumGamemode.byId(dynamic.get("GameType").asInt(0));

        return new WorldSettings(dynamic.get("LevelName").asString(""), enumgamemode, dynamic.get("hardcore").asBoolean(false), (EnumDifficulty) dynamic.get("Difficulty").asNumber().map((number) -> {
            return EnumDifficulty.byId(number.byteValue());
        }).result().orElse(EnumDifficulty.NORMAL), dynamic.get("allowCommands").asBoolean(enumgamemode == EnumGamemode.CREATIVE), new GameRules(dynamic.get("GameRules")), worlddataconfiguration);
    }

    public String levelName() {
        return this.levelName;
    }

    public EnumGamemode gameType() {
        return this.gameType;
    }

    public boolean hardcore() {
        return this.hardcore;
    }

    public EnumDifficulty difficulty() {
        return this.difficulty;
    }

    public boolean allowCommands() {
        return this.allowCommands;
    }

    public GameRules gameRules() {
        return this.gameRules;
    }

    public WorldDataConfiguration getDataConfiguration() {
        return this.dataConfiguration;
    }

    public WorldSettings withGameType(EnumGamemode enumgamemode) {
        return new WorldSettings(this.levelName, enumgamemode, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataConfiguration);
    }

    public WorldSettings withDifficulty(EnumDifficulty enumdifficulty) {
        return new WorldSettings(this.levelName, this.gameType, this.hardcore, enumdifficulty, this.allowCommands, this.gameRules, this.dataConfiguration);
    }

    public WorldSettings withDataConfiguration(WorldDataConfiguration worlddataconfiguration) {
        return new WorldSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, worlddataconfiguration);
    }

    public WorldSettings copy() {
        return new WorldSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.copy(), this.dataConfiguration);
    }
}
