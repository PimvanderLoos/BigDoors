package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.INamable;

public enum TerrainAdjustment implements INamable {

    NONE("none"), BURY("bury"), BEARD_THIN("beard_thin"), BEARD_BOX("beard_box");

    public static final Codec<TerrainAdjustment> CODEC = INamable.fromEnum(TerrainAdjustment::values);
    private final String id;

    private TerrainAdjustment(String s) {
        this.id = s;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
