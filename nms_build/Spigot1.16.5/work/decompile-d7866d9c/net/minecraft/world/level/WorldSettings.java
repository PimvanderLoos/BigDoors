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
    private final DataPackConfiguration g;

    public WorldSettings(String s, EnumGamemode enumgamemode, boolean flag, EnumDifficulty enumdifficulty, boolean flag1, GameRules gamerules, DataPackConfiguration datapackconfiguration) {
        this.levelName = s;
        this.gameType = enumgamemode;
        this.hardcore = flag;
        this.difficulty = enumdifficulty;
        this.allowCommands = flag1;
        this.gameRules = gamerules;
        this.g = datapackconfiguration;
    }

    public static WorldSettings a(Dynamic<?> dynamic, DataPackConfiguration datapackconfiguration) {
        EnumGamemode enumgamemode = EnumGamemode.getById(dynamic.get("GameType").asInt(0));

        return new WorldSettings(dynamic.get("LevelName").asString(""), enumgamemode, dynamic.get("hardcore").asBoolean(false), (EnumDifficulty) dynamic.get("Difficulty").asNumber().map((number) -> {
            return EnumDifficulty.getById(number.byteValue());
        }).result().orElse(EnumDifficulty.NORMAL), dynamic.get("allowCommands").asBoolean(enumgamemode == EnumGamemode.CREATIVE), new GameRules(dynamic.get("GameRules")), datapackconfiguration);
    }

    public String getLevelName() {
        return this.levelName;
    }

    public EnumGamemode getGameType() {
        return this.gameType;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    public boolean e() {
        return this.allowCommands;
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }

    public DataPackConfiguration g() {
        return this.g;
    }

    public WorldSettings a(EnumGamemode enumgamemode) {
        return new WorldSettings(this.levelName, enumgamemode, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.g);
    }

    public WorldSettings a(EnumDifficulty enumdifficulty) {
        return new WorldSettings(this.levelName, this.gameType, this.hardcore, enumdifficulty, this.allowCommands, this.gameRules, this.g);
    }

    public WorldSettings a(DataPackConfiguration datapackconfiguration) {
        return new WorldSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, datapackconfiguration);
    }

    public WorldSettings h() {
        return new WorldSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.b(), this.g);
    }
}
