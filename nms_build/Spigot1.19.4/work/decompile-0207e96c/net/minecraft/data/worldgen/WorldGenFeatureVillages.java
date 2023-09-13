package net.minecraft.data.worldgen;

import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeatureVillages {

    public WorldGenFeatureVillages() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureDefinedStructurePoolTemplate> bootstapcontext) {
        WorldGenFeatureVillagePlain.bootstrap(bootstapcontext);
        WorldGenFeatureVillageSnowy.bootstrap(bootstapcontext);
        WorldGenFeatureVillageSavanna.bootstrap(bootstapcontext);
        WorldGenFeatureDesertVillage.bootstrap(bootstapcontext);
        WorldGenFeatureVillageTaiga.bootstrap(bootstapcontext);
    }
}
