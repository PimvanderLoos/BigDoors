package net.minecraft.data.worldgen;

public class WorldGenFeatureVillages {

    public WorldGenFeatureVillages() {}

    public static void bootstrap() {
        WorldGenFeatureVillagePlain.bootstrap();
        WorldGenFeatureVillageSnowy.bootstrap();
        WorldGenFeatureVillageSavanna.bootstrap();
        WorldGenFeatureDesertVillage.bootstrap();
        WorldGenFeatureVillageTaiga.bootstrap();
    }
}
