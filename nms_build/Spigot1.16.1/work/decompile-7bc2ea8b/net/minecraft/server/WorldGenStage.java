package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

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

    public static enum Decoration implements INamable {

        RAW_GENERATION("raw_generation"), LAKES("lakes"), LOCAL_MODIFICATIONS("local_modifications"), UNDERGROUND_STRUCTURES("underground_structures"), SURFACE_STRUCTURES("surface_structures"), STRONGHOLDS("strongholds"), UNDERGROUND_ORES("underground_ores"), UNDERGROUND_DECORATION("underground_decoration"), VEGETAL_DECORATION("vegetal_decoration"), TOP_LAYER_MODIFICATION("top_layer_modification");

        public static final Codec<WorldGenStage.Decoration> k = INamable.a(WorldGenStage.Decoration::values, WorldGenStage.Decoration::a);
        private static final Map<String, WorldGenStage.Decoration> l = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenStage.Decoration::b, (worldgenstage_decoration) -> {
            return worldgenstage_decoration;
        }));
        private final String m;

        private Decoration(String s) {
            this.m = s;
        }

        public String b() {
            return this.m;
        }

        public static WorldGenStage.Decoration a(String s) {
            return (WorldGenStage.Decoration) WorldGenStage.Decoration.l.get(s);
        }

        @Override
        public String getName() {
            return this.m;
        }
    }
}
