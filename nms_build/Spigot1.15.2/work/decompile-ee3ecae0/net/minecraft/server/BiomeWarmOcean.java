package net.minecraft.server;

import com.google.common.collect.ImmutableList;

public class BiomeWarmOcean extends BiomeBase {

    public BiomeWarmOcean() {
        super((new BiomeBase.a()).a(WorldGenSurface.G, WorldGenSurface.B).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.OCEAN).a(-1.0F).b(0.1F).c(0.5F).d(0.5F).a(4445678).b(270131).a((String) null));
        this.a(WorldGenerator.OCEAN_RUIN.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.WARM, 0.3F, 0.9F))));
        this.a(WorldGenerator.MINESHAFT.b((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.NORMAL))));
        this.a(WorldGenerator.SHIPWRECK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(false))));
        BiomeDecoratorGroups.b(this);
        BiomeDecoratorGroups.c(this);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.g(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.l(this);
        BiomeDecoratorGroups.u(this);
        BiomeDecoratorGroups.U(this);
        BiomeDecoratorGroups.W(this);
        BiomeDecoratorGroups.Z(this);
        BiomeDecoratorGroups.aa(this);
        BiomeDecoratorGroups.am(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SIMPLE_RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandom2(ImmutableList.of(WorldGenerator.CORAL_TREE.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e), WorldGenerator.CORAL_CLAW.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e), WorldGenerator.CORAL_MUSHROOM.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e))))).a(WorldGenDecorator.x.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorNoiseConfiguration(20, 400.0D, 0.0D, HeightMap.Type.OCEAN_FLOOR_WG)))));
        BiomeDecoratorGroups.aj(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SEA_PICKLE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureKelpConfiguration(20))).a(WorldGenDecorator.l.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(16)))));
        BiomeDecoratorGroups.ap(this);
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SQUID, 10, 4, 4));
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.PUFFERFISH, 15, 1, 3));
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.TROPICAL_FISH, 25, 8, 8));
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.DOLPHIN, 2, 1, 2));
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
