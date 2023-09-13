package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class BiomeWarmOcean extends BiomeBase {

    public BiomeWarmOcean() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.J).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.OCEAN).a(-1.0F).b(0.1F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(4445678).c(270131).a(12638463).a(CaveSoundSettings.b).a()).a((String) null).a((List) ImmutableList.of(new BiomeBase.d(0.0F, 0.0F, -0.25F, 0.0F, 1.0F))));
        this.a(BiomeDecoratorGroups.n);
        BiomeDecoratorGroups.c(this);
        this.a(BiomeDecoratorGroups.D);
        BiomeDecoratorGroups.e(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.w(this);
        BiomeDecoratorGroups.W(this);
        BiomeDecoratorGroups.Y(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ac(this);
        BiomeDecoratorGroups.ao(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SIMPLE_RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandom2(ImmutableList.of(WorldGenerator.CORAL_TREE.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k), WorldGenerator.CORAL_CLAW.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k), WorldGenerator.CORAL_MUSHROOM.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k))))).a(WorldGenDecorator.x.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorNoiseConfiguration(20, 400.0D, 0.0D, HeightMap.Type.OCEAN_FLOOR_WG)))));
        BiomeDecoratorGroups.al(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SEA_PICKLE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureKelpConfiguration(20))).a(WorldGenDecorator.l.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(16)))));
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SQUID, 10, 4, 4));
        this.a(EnumCreatureType.WATER_AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.PUFFERFISH, 15, 1, 3));
        this.a(EnumCreatureType.WATER_AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.TROPICAL_FISH, 25, 8, 8));
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
