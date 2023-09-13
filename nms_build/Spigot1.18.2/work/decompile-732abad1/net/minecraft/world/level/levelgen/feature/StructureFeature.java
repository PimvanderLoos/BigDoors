package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class StructureFeature<FC extends WorldGenFeatureConfiguration, F extends StructureGenerator<FC>> {

    public static final Codec<StructureFeature<?, ?>> DIRECT_CODEC = IRegistry.STRUCTURE_FEATURE.byNameCodec().dispatch((structurefeature) -> {
        return structurefeature.feature;
    }, StructureGenerator::configuredStructureCodec);
    public static final Codec<Holder<StructureFeature<?, ?>>> CODEC = RegistryFileCodec.create(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeature.DIRECT_CODEC);
    public static final Codec<HolderSet<StructureFeature<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeature.DIRECT_CODEC);
    public final F feature;
    public final FC config;
    public final HolderSet<BiomeBase> biomes;
    public final Map<EnumCreatureType, StructureSpawnOverride> spawnOverrides;
    public final boolean adaptNoise;

    public StructureFeature(F f0, FC fc, HolderSet<BiomeBase> holderset, boolean flag, Map<EnumCreatureType, StructureSpawnOverride> map) {
        this.feature = f0;
        this.config = fc;
        this.biomes = holderset;
        this.adaptNoise = flag;
        this.spawnOverrides = map;
    }

    public StructureStart generate(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, DefinedStructureManager definedstructuremanager, long i, ChunkCoordIntPair chunkcoordintpair, int j, LevelHeightAccessor levelheightaccessor, Predicate<Holder<BiomeBase>> predicate) {
        Optional<PieceGenerator<FC>> optional = this.feature.pieceGeneratorSupplier().createGenerator(new PieceGeneratorSupplier.a<>(chunkgenerator, worldchunkmanager, i, chunkcoordintpair, this.config, levelheightaccessor, predicate, definedstructuremanager, iregistrycustom));

        if (optional.isPresent()) {
            StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();
            SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

            seededrandom.setLargeFeatureSeed(i, chunkcoordintpair.x, chunkcoordintpair.z);
            ((PieceGenerator) optional.get()).generatePieces(structurepiecesbuilder, new PieceGenerator.a<>(this.config, chunkgenerator, definedstructuremanager, chunkcoordintpair, levelheightaccessor, seededrandom, i));
            StructureStart structurestart = new StructureStart(this, chunkcoordintpair, j, structurepiecesbuilder.build());

            if (structurestart.isValid()) {
                return structurestart;
            }
        }

        return StructureStart.INVALID_START;
    }

    public HolderSet<BiomeBase> biomes() {
        return this.biomes;
    }

    public StructureBoundingBox adjustBoundingBox(StructureBoundingBox structureboundingbox) {
        return this.adaptNoise ? structureboundingbox.inflatedBy(12) : structureboundingbox;
    }
}
