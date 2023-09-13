package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.INamable;

public class WorldGenStage {
    public static enum Features implements INamable {

        AIR("air"), LIQUID("liquid");

        public static final Codec<WorldGenStage.Features> c = INamable.a(WorldGenStage.Features::values, WorldGenStage.Features::a);
        private static final Map<String, WorldGenStage.Features> d = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenStage.Features::b, (worldgenstage_features) -> {
            return worldgenstage_features;
        }));
        private final String e;

        private Features(String s) {
            this.e = s;
        }

        public String b() {
            return this.e;
        }

        @Nullable
        public static WorldGenStage.Features a(String s) {
            return (WorldGenStage.Features) WorldGenStage.Features.d.get(s);
        }

        @Override
        public String getName() {
            return this.e;
        }
    }

    public static enum Decoration {

        RAW_GENERATION, LAKES, LOCAL_MODIFICATIONS, UNDERGROUND_STRUCTURES, SURFACE_STRUCTURES, STRONGHOLDS, UNDERGROUND_ORES, UNDERGROUND_DECORATION, VEGETAL_DECORATION, TOP_LAYER_MODIFICATION;

        private Decoration() {}
    }
}
