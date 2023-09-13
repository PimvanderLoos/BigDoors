package net.minecraft.server;

public final class BiomeHell extends BiomeBase {

    protected BiomeHell() {
        super((new BiomeBase.a()).a(WorldGenSurface.Q, WorldGenSurface.E).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a(4159204).b(329011).a((String) null));
        this.a(WorldGenerator.NETHER_BRIDGE.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e));
        this.a(WorldGenStage.Features.AIR, a(WorldGenCarverAbstract.b, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.2F))));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ac).a(WorldGenDecorator.p.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 8, 16, 256)))));
        BiomeDecoratorGroups.Z(this);
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.NETHER_BRIDGE.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ad).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(8, 4, 8, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.K).a(WorldGenDecorator.A.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.GLOWSTONE_BLOB.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.I.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.GLOWSTONE_BLOB.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.N).a(WorldGenDecorator.r.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorRangeConfiguration(0.5F, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.M).a(WorldGenDecorator.r.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorRangeConfiguration(0.5F, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.getBlockData(), 14))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(16, 10, 20, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, Blocks.MAGMA_BLOCK.getBlockData(), 33))).a(WorldGenDecorator.B.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ae).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(16, 10, 20, 128)))));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.GHAST, 50, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_PIGMAN, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.MAGMA_CUBE, 2, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 1, 4, 4));
    }
}
