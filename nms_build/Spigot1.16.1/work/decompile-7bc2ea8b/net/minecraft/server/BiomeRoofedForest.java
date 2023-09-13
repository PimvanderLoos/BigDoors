package net.minecraft.server;

import com.google.common.collect.ImmutableList;

public final class BiomeRoofedForest extends BiomeBase {

    public BiomeRoofedForest() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.D).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.FOREST).a(0.1F).b(0.2F).c(0.7F).d(0.8F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a((String) null));
        this.a(BiomeDecoratorGroups.d);
        BiomeDecoratorGroups.b(this);
        this.a(BiomeDecoratorGroups.y);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.HUGE_BROWN_MUSHROOM.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.HUGE_BROWN_MUSHROOM).a(0.025F), WorldGenerator.HUGE_RED_MUSHROOM.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.HUGE_RED_MUSHROOM).a(0.05F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.DARK_OAK_TREE).a(0.6666667F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE).a(0.2F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.G.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.f)));
        BiomeDecoratorGroups.P(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.W(this);
        BiomeDecoratorGroups.Q(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ac(this);
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ar(this);
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
