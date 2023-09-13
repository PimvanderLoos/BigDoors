package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.List;

public final class BiomeOcean extends BiomeBase {

    public BiomeOcean() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.D).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.OCEAN).a(-1.0F).b(0.1F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a((String) null).a((List) ImmutableList.of(new BiomeBase.d(0.0F, 0.0F, -0.5F, 0.0F, 1.0F))));
        BiomeDecoratorGroups.c(this);
        this.a(BiomeDecoratorGroups.m);
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
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SEAGRASS.b((WorldGenFeatureConfiguration) (new WorldGenFeatureSeaGrassConfiguration(48, 0.3D))).a(WorldGenDecorator.v.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.f)));
        BiomeDecoratorGroups.ak(this);
        BiomeDecoratorGroups.aj(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SQUID, 1, 1, 4));
        this.a(EnumCreatureType.WATER_AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.COD, 10, 3, 6));
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.DOLPHIN, 1, 1, 2));
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 95, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.DROWNED, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));
    }
}
