package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.INamable;

public class WorldGenStage {

    public WorldGenStage() {}

    public static enum Features implements INamable {

        AIR("air"), LIQUID("liquid");

        public static final Codec<WorldGenStage.Features> CODEC = INamable.a(WorldGenStage.Features::values, WorldGenStage.Features::a);
        private static final Map<String, WorldGenStage.Features> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenStage.Features::a, (worldgenstage_features) -> {
            return worldgenstage_features;
        }));
        private final String name;

        private Features(String s) {
            this.name = s;
        }

        public String a() {
            return this.name;
        }

        @Nullable
        public static WorldGenStage.Features a(String s) {
            return (WorldGenStage.Features) WorldGenStage.Features.BY_NAME.get(s);
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public static enum Decoration {

        RAW_GENERATION, LAKES, LOCAL_MODIFICATIONS, UNDERGROUND_STRUCTURES, SURFACE_STRUCTURES, STRONGHOLDS, UNDERGROUND_ORES, UNDERGROUND_DECORATION, VEGETAL_DECORATION, TOP_LAYER_MODIFICATION;

        private Decoration() {}
    }
}
