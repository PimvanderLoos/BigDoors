package net.minecraft.server;

public final class BiomeTheEnd extends BiomeBase {

    public BiomeTheEnd() {
        super((new BiomeBase.a()).a(new WorldGenSurfaceComposite<>(BiomeTheEnd.au, BiomeTheEnd.at)).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.THEEND).a(0.1F).b(0.2F).c(0.5F).d(0.5F).a(4159204).b(329011).a((String) null));
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, a(WorldGenerator.au, WorldGenFeatureConfiguration.e, BiomeTheEnd.P, WorldGenFeatureDecoratorConfiguration.e));
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, a(WorldGenerator.q, new WorldGenEndCityConfiguration(), BiomeTheEnd.n, WorldGenFeatureDecoratorConfiguration.e));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 4, 4));
    }
}
