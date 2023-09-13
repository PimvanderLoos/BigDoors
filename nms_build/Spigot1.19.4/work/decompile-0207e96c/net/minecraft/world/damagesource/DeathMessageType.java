package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.util.INamable;

public enum DeathMessageType implements INamable {

    DEFAULT("default"), FALL_VARIANTS("fall_variants"), INTENTIONAL_GAME_DESIGN("intentional_game_design");

    public static final Codec<DeathMessageType> CODEC = INamable.fromEnum(DeathMessageType::values);
    private final String id;

    private DeathMessageType(String s) {
        this.id = s;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
