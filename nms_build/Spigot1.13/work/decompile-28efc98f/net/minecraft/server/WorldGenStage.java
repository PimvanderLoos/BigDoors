package net.minecraft.server;

public class WorldGenStage {
    public static enum Features {

        AIR, LIQUID;

        private Features() {}
    }

    public static enum Decoration {

        RAW_GENERATION, LOCAL_MODIFICATIONS, UNDERGROUND_STRUCTURES, SURFACE_STRUCTURES, UNDERGROUND_ORES, UNDERGROUND_DECORATION, VEGETAL_DECORATION, TOP_LAYER_MODIFICATION;

        private Decoration() {}
    }
}
