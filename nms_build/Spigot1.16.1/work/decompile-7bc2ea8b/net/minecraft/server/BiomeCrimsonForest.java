package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class BiomeCrimsonForest extends BiomeBase {

    protected BiomeCrimsonForest() {
        super((new BiomeBase.a()).a(WorldGenSurface.ad, WorldGenSurface.P).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(3343107).a(new BiomeParticles(Particles.CRIMSON_SPORE, 0.025F)).a(SoundEffects.AMBIENT_CRIMSON_FOREST_LOOP).a(new CaveSoundSettings(SoundEffects.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)).a(new CaveSound(SoundEffects.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D)).a(Musics.a(SoundEffects.MUSIC_NETHER_CRIMSON_FOREST)).a()).a((String) null).a((List) ImmutableList.of(new BiomeBase.d(0.4F, 0.0F, 0.0F, 0.0F, 0.0F))));
        this.a(BiomeDecoratorGroups.E);
        this.a(WorldGenStage.Features.AIR, a(WorldGenCarverAbstract.b, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.2F))));
        this.a(BiomeDecoratorGroups.o);
        this.a(BiomeDecoratorGroups.s);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aL).a(WorldGenDecorator.p.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 8, 16, 256)))));
        BiomeDecoratorGroups.ab(this);
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aM).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(8, 4, 8, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ap).a(WorldGenDecorator.A.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.GLOWSTONE_BLOB.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.I.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.GLOWSTONE_BLOB.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, Blocks.MAGMA_BLOCK.getBlockData(), 33))).a(WorldGenDecorator.B.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aO).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(16, 10, 20, 128)))));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.WEEPING_VINES.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 128)))));
        BiomeDecoratorGroups.au(this);
        BiomeDecoratorGroups.as(this);
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIFIED_PIGLIN, 1, 2, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.HOGLIN, 9, 3, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.PIGLIN, 5, 3, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.STRIDER, 60, 1, 2));
    }
}
