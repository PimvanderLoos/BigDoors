package net.minecraft.server;

import com.google.common.collect.ImmutableList;

public final class BiomeRoofedForestMutated extends BiomeBase {

    public BiomeRoofedForestMutated() {
        super((new BiomeBase.a()).a(WorldGenSurface.G, WorldGenSurface.v).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.FOREST).a(0.2F).b(0.4F).c(0.7F).d(0.8F).a(4159204).b(329011).a("dark_forest"));
        this.a(WorldGenerator.WOODLAND_MANSION.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e));
        this.a(WorldGenerator.MINESHAFT.b((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.NORMAL))));
        this.a(WorldGenerator.STRONGHOLD.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e));
        BiomeDecoratorGroups.a(this);
        BiomeDecoratorGroups.c(this);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.HUGE_RED_MUSHROOM.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.HUGE_RED_MUSHROOM).a(0.025F), WorldGenerator.HUGE_BROWN_MUSHROOM.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.HUGE_BROWN_MUSHROOM).a(0.05F), WorldGenerator.DARK_OAK_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.DARK_OAK_TREE).a(0.6666667F), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE).a(0.2F), WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.G.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        BiomeDecoratorGroups.N(this);
        BiomeDecoratorGroups.g(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.l(this);
        BiomeDecoratorGroups.U(this);
        BiomeDecoratorGroups.O(this);
        BiomeDecoratorGroups.Z(this);
        BiomeDecoratorGroups.aa(this);
        BiomeDecoratorGroups.am(this);
        BiomeDecoratorGroups.ap(this);
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SHEEP, 12, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.PIG, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.CHICKEN, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.COW, 8, 4, 4));
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
