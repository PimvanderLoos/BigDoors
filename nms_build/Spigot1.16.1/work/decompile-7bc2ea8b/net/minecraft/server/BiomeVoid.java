package net.minecraft.server;

public final class BiomeVoid extends BiomeBase {

    public BiomeVoid() {
        super((new BiomeBase.a()).a(WorldGenSurface.ag, WorldGenSurface.F).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NONE).a(0.1F).b(0.2F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a((String) null));
        this.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, WorldGenerator.VOID_START_PLATFORM.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k));
    }
}
