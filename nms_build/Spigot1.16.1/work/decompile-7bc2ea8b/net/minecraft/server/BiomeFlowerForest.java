package net.minecraft.server;

import com.google.common.collect.ImmutableList;

public final class BiomeFlowerForest extends BiomeBase {

    public BiomeFlowerForest() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.D).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.FOREST).a(0.1F).b(0.4F).c(0.7F).d(0.8F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a("forest"));
        BiomeDecoratorGroups.b(this);
        this.a(BiomeDecoratorGroups.y);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomConfiguration(ImmutableList.of(WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.av), WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aw), WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ax), WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ag)), 2))).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE_BEES_002).a(0.2F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE_BEES_002).a(0.1F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE_BEES_002)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(6, 0.1F, 1)))));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ak).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(100)))));
        BiomeDecoratorGroups.Y(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ac(this);
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SHEEP, 12, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.PIG, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.CHICKEN, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.COW, 8, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.RABBIT, 4, 2, 3));
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 95, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));
    }
}
