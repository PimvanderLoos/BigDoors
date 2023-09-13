package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.util.INamable;

public enum DamageScaling implements INamable {

    NEVER("never"), WHEN_CAUSED_BY_LIVING_NON_PLAYER("when_caused_by_living_non_player"), ALWAYS("always");

    public static final Codec<DamageScaling> CODEC = INamable.fromEnum(DamageScaling::values);
    private final String id;

    private DamageScaling(String s) {
        this.id = s;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
