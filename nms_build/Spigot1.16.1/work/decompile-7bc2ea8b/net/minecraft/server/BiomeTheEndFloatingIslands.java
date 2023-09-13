package net.minecraft.server;

public class BiomeTheEndFloatingIslands extends BiomeBase {

    public BiomeTheEndFloatingIslands() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.O).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.THEEND).a(0.1F).b(0.2F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(4159204).c(329011).a(10518688).a(CaveSoundSettings.b).a()).a((String) null));
        this.a(WorldGenStage.Decoration.RAW_GENERATION, WorldGenerator.END_ISLAND.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.J.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.f)));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 4, 4));
    }
}
