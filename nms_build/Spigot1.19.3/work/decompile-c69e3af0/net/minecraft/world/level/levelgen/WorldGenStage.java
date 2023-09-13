package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import net.minecraft.util.INamable;

public class WorldGenStage {

    public WorldGenStage() {}

    public static enum Features implements INamable {

        AIR("air"), LIQUID("liquid");

        public static final Codec<WorldGenStage.Features> CODEC = INamable.fromEnum(WorldGenStage.Features::values);
        private final String name;

        private Features(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static enum Decoration implements INamable {

        RAW_GENERATION("raw_generation"), LAKES("lakes"), LOCAL_MODIFICATIONS("local_modifications"), UNDERGROUND_STRUCTURES("underground_structures"), SURFACE_STRUCTURES("surface_structures"), STRONGHOLDS("strongholds"), UNDERGROUND_ORES("underground_ores"), UNDERGROUND_DECORATION("underground_decoration"), FLUID_SPRINGS("fluid_springs"), VEGETAL_DECORATION("vegetal_decoration"), TOP_LAYER_MODIFICATION("top_layer_modification");

        public static final Codec<WorldGenStage.Decoration> CODEC = INamable.fromEnum(WorldGenStage.Decoration::values);
        private final String name;

        private Decoration(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
