package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class BiomeSoulSandValley extends BiomeBase {

    protected BiomeSoulSandValley() {
        super((new BiomeBase.a()).a(WorldGenSurface.ae, WorldGenSurface.N).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(1787717).a(new BiomeParticles(Particles.ASH, 0.00625F)).a(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_LOOP).a(new CaveSoundSettings(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0D)).a(new CaveSound(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111D)).a(Musics.a(SoundEffects.MUSIC_NETHER_SOUL_SAND_VALLEY)).a()).a((String) null).a((List) ImmutableList.of(new BiomeBase.d(0.0F, -0.5F, 0.0F, 0.0F, 0.0F))));
        this.a(BiomeDecoratorGroups.o);
        this.a(BiomeDecoratorGroups.p);
        this.a(BiomeDecoratorGroups.E);
        this.a(BiomeDecoratorGroups.s);
        this.a(WorldGenStage.Features.AIR, a(WorldGenCarverAbstract.b, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.2F))));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aL).a(WorldGenDecorator.p.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 8, 16, 256)))));
        this.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.BASALT_PILLAR.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aM).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(8, 4, 8, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.GLOWSTONE_BLOB.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.I.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.GLOWSTONE_BLOB.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.au).a(WorldGenDecorator.r.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorRangeConfiguration(1.0F, 0, 0, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ap).a(WorldGenDecorator.A.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aq).a(WorldGenDecorator.A.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, Blocks.MAGMA_BLOCK.getBlockData(), 33))).a(WorldGenDecorator.B.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aO).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(16, 10, 20, 128)))));
        this.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, Blocks.SOUL_SAND.getBlockData(), 12))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(12, 0, 0, 32)))));
        BiomeDecoratorGroups.as(this);
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 20, 5, 5));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.GHAST, 50, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 1, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.STRIDER, 60, 1, 2));
        double d0 = 0.7D;
        double d1 = 0.15D;

        this.a(EntityTypes.SKELETON, 0.7D, 0.15D);
        this.a(EntityTypes.GHAST, 0.7D, 0.15D);
        this.a(EntityTypes.ENDERMAN, 0.7D, 0.15D);
        this.a(EntityTypes.STRIDER, 0.7D, 0.15D);
    }
}
