package net.minecraft.world.level;

import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.INamable;
import net.minecraft.world.entity.player.PlayerAbilities;
import org.jetbrains.annotations.Contract;

public enum EnumGamemode implements INamable {

    SURVIVAL(0, "survival"), CREATIVE(1, "creative"), ADVENTURE(2, "adventure"), SPECTATOR(3, "spectator");

    public static final EnumGamemode DEFAULT_MODE = EnumGamemode.SURVIVAL;
    public static final INamable.a<EnumGamemode> CODEC = INamable.fromEnum(EnumGamemode::values);
    private static final IntFunction<EnumGamemode> BY_ID = ByIdMap.continuous(EnumGamemode::getId, values(), ByIdMap.a.ZERO);
    private static final int NOT_SET = -1;
    private final int id;
    private final String name;
    private final IChatBaseComponent shortName;
    private final IChatBaseComponent longName;

    private EnumGamemode(int i, String s) {
        this.id = i;
        this.name = s;
        this.shortName = IChatBaseComponent.translatable("selectWorld.gameMode." + s);
        this.longName = IChatBaseComponent.translatable("gameMode." + s);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public IChatBaseComponent getLongDisplayName() {
        return this.longName;
    }

    public IChatBaseComponent getShortDisplayName() {
        return this.shortName;
    }

    public void updatePlayerAbilities(PlayerAbilities playerabilities) {
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

        playerabilities.mayBuild = !this.isBlockPlacingRestricted();
    }

    public boolean isBlockPlacingRestricted() {
        return this == EnumGamemode.ADVENTURE || this == EnumGamemode.SPECTATOR;
    }

    public boolean isCreative() {
        return this == EnumGamemode.CREATIVE;
    }

    public boolean isSurvival() {
        return this == EnumGamemode.SURVIVAL || this == EnumGamemode.ADVENTURE;
    }

    public static EnumGamemode byId(int i) {
        return (EnumGamemode) EnumGamemode.BY_ID.apply(i);
    }

    public static EnumGamemode byName(String s) {
        return byName(s, EnumGamemode.SURVIVAL);
    }

    @Nullable
    @Contract("_,!null->!null;_,null->_")
    public static EnumGamemode byName(String s, @Nullable EnumGamemode enumgamemode) {
        EnumGamemode enumgamemode1 = (EnumGamemode) EnumGamemode.CODEC.byName(s);

        return enumgamemode1 != null ? enumgamemode1 : enumgamemode;
    }

    public static int getNullableId(@Nullable EnumGamemode enumgamemode) {
        return enumgamemode != null ? enumgamemode.id : -1;
    }

    @Nullable
    public static EnumGamemode byNullableId(int i) {
        return i == -1 ? null : byId(i);
    }
}
