package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class BiomeBasaltDeltas extends BiomeBase {

    protected BiomeBasaltDeltas() {
        super((new BiomeBase.a()).a(WorldGenSurface.af, WorldGenSurface.R).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(4341314).a(6840176).a(new BiomeParticles(Particles.WHITE_ASH, 0.118093334F)).a(SoundEffects.AMBIENT_BASALT_DELTAS_LOOP).a(new CaveSoundSettings(SoundEffects.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0D)).a(new CaveSound(SoundEffects.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111D)).a(Musics.a(SoundEffects.MUSIC_NETHER_BASALT_DELTAS)).a()).a((String) null).a((List) ImmutableList.of(new BiomeBase.d(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F))));
        this.a(BiomeDecoratorGroups.E);
        this.a(WorldGenStage.Features.AIR, a(WorldGenCarverAbstract.b, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.2F))));
        this.a(BiomeDecoratorGroups.o);
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.DELTA_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aV).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(40)))));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aL).a(WorldGenDecorator.p.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(40, 8, 16, 256)))));
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.BASALT_COLUMNS.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aR).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.BASALT_COLUMNS.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aS).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(2)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.NETHERRACK_REPLACE_BLOBS.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aT).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(75, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.NETHERRACK_REPLACE_BLOBS.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aU).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(25, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aN).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(16, 4, 8, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ap).a(WorldGenDecorator.A.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aq).a(WorldGenDecorator.A.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.GLOWSTONE_BLOB.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.I.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.GLOWSTONE_BLOB.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.at).a(WorldGenDecorator.r.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorRangeConfiguration(0.5F, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.as).a(WorldGenDecorator.r.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorRangeConfiguration(0.5F, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, Blocks.MAGMA_BLOCK.getBlockData(), 33))).a(WorldGenDecorator.B.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aO).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(32, 10, 20, 128)))));
        BiomeDecoratorGroups.a(this, 20, 32);
        BiomeDecoratorGroups.at(this);
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.GHAST, 40, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.MAGMA_CUBE, 100, 2, 5));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.STRIDER, 60, 1, 2));
    }
}
