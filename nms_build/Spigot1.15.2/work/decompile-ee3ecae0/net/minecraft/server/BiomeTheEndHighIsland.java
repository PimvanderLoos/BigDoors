package net.minecraft.server;

public class BiomeTheEndHighIsland extends BiomeBase {

    public BiomeTheEndHighIsland() {
        super((new BiomeBase.a()).a(WorldGenSurface.G, WorldGenSurface.F).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.THEEND).a(0.1F).b(0.2F).c(0.5F).d(0.5F).a(4159204).b(329011).a((String) null));
        this.a(WorldGenerator.END_CITY.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e));
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.END_GATEWAY.b((WorldGenFeatureConfiguration) WorldGenEndGatewayConfiguration.a(WorldProviderTheEnd.f, true)).a(WorldGenDecorator.L.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        BiomeDecoratorGroups.aq(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.CHORUS_PLANT.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.K.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 4, 4));
    }
}
