package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.INamable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class Structure {

    public static final Codec<Structure> DIRECT_CODEC = BuiltInRegistries.STRUCTURE_TYPE.byNameCodec().dispatch(Structure::type, StructureType::codec);
    public static final Codec<Holder<Structure>> CODEC = RegistryFileCodec.create(Registries.STRUCTURE, Structure.DIRECT_CODEC);
    protected final Structure.c settings;

    public static <S extends Structure> RecordCodecBuilder<S, Structure.c> settingsCodec(Instance<S> instance) {
        return Structure.c.CODEC.forGetter((structure) -> {
            return structure.settings;
        });
    }

    public static <S extends Structure> Codec<S> simpleCodec(Function<Structure.c, S> function) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(settingsCodec(instance)).apply(instance, function);
        });
    }

    protected Structure(Structure.c structure_c) {
        this.settings = structure_c;
    }

    public HolderSet<BiomeBase> biomes() {
        return this.settings.biomes;
    }

    public Map<EnumCreatureType, StructureSpawnOverride> spawnOverrides() {
        return this.settings.spawnOverrides;
    }

    public WorldGenStage.Decoration step() {
        return this.settings.step;
    }

    public TerrainAdjustment terrainAdaptation() {
        return this.settings.terrainAdaptation;
    }

    public StructureBoundingBox adjustBoundingBox(StructureBoundingBox structureboundingbox) {
        return this.terrainAdaptation() != TerrainAdjustment.NONE ? structureboundingbox.inflatedBy(12) : structureboundingbox;
    }

    public StructureStart generate(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, RandomState randomstate, StructureTemplateManager structuretemplatemanager, long i, ChunkCoordIntPair chunkcoordintpair, int j, LevelHeightAccessor levelheightaccessor, Predicate<Holder<BiomeBase>> predicate) {
        Structure.a structure_a = new Structure.a(iregistrycustom, chunkgenerator, worldchunkmanager, randomstate, structuretemplatemanager, i, chunkcoordintpair, levelheightaccessor, predicate);
        Optional<Structure.b> optional = this.findValidGenerationPoint(structure_a);

        if (optional.isPresent()) {
            StructurePiecesBuilder structurepiecesbuilder = ((Structure.b) optional.get()).getPiecesBuilder();
            StructureStart structurestart = new StructureStart(this, chunkcoordintpair, j, structurepiecesbuilder.build());

            if (structurestart.isValid()) {
                return structurestart;
            }
        }

        return StructureStart.INVALID_START;
    }

    protected static Optional<Structure.b> onTopOfChunkCenter(Structure.a structure_a, HeightMap.Type heightmap_type, Consumer<StructurePiecesBuilder> consumer) {
        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();
        int i = chunkcoordintpair.getMiddleBlockX();
        int j = chunkcoordintpair.getMiddleBlockZ();
        int k = structure_a.chunkGenerator().getFirstOccupiedHeight(i, j, heightmap_type, structure_a.heightAccessor(), structure_a.randomState());

        return Optional.of(new Structure.b(new BlockPosition(i, k, j), consumer));
    }

    private static boolean isValidBiome(Structure.b structure_b, Structure.a structure_a) {
        BlockPosition blockposition = structure_b.position();

        return structure_a.validBiome.test(structure_a.chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(blockposition.getX()), QuartPos.fromBlock(blockposition.getY()), QuartPos.fromBlock(blockposition.getZ()), structure_a.randomState.sampler()));
    }

    public void afterPlace(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, PiecesContainer piecescontainer) {}

    private static int[] getCornerHeights(Structure.a structure_a, int i, int j, int k, int l) {
        ChunkGenerator chunkgenerator = structure_a.chunkGenerator();
        LevelHeightAccessor levelheightaccessor = structure_a.heightAccessor();
        RandomState randomstate = structure_a.randomState();

        return new int[]{chunkgenerator.getFirstOccupiedHeight(i, k, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor, randomstate), chunkgenerator.getFirstOccupiedHeight(i, k + l, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor, randomstate), chunkgenerator.getFirstOccupiedHeight(i + j, k, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor, randomstate), chunkgenerator.getFirstOccupiedHeight(i + j, k + l, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor, randomstate)};
    }

    protected static int getLowestY(Structure.a structure_a, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();
        int k = chunkcoordintpair.getMinBlockX();
        int l = chunkcoordintpair.getMinBlockZ();

        return getLowestY(structure_a, k, l, i, j);
    }

    protected static int getLowestY(Structure.a structure_a, int i, int j, int k, int l) {
        int[] aint = getCornerHeights(structure_a, i, k, j, l);

        return Math.min(Math.min(aint[0], aint[1]), Math.min(aint[2], aint[3]));
    }

    /** @deprecated */
    @Deprecated
    protected BlockPosition getLowestYIn5by5BoxOffset7Blocks(Structure.a structure_a, EnumBlockRotation enumblockrotation) {
        byte b0 = 5;
        byte b1 = 5;

        if (enumblockrotation == EnumBlockRotation.CLOCKWISE_90) {
            b0 = -5;
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
            b0 = -5;
            b1 = -5;
        } else if (enumblockrotation == EnumBlockRotation.COUNTERCLOCKWISE_90) {
            b1 = -5;
        }

        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();
        int i = chunkcoordintpair.getBlockX(7);
        int j = chunkcoordintpair.getBlockZ(7);

        return new BlockPosition(i, getLowestY(structure_a, i, j, b0, b1), j);
    }

    protected abstract Optional<Structure.b> findGenerationPoint(Structure.a structure_a);

    public Optional<Structure.b> findValidGenerationPoint(Structure.a structure_a) {
        return this.findGenerationPoint(structure_a).filter((structure_b) -> {
            return isValidBiome(structure_b, structure_a);
        });
    }

    public abstract StructureType<?> type();

    public static record c(HolderSet<BiomeBase> biomes, Map<EnumCreatureType, StructureSpawnOverride> spawnOverrides, WorldGenStage.Decoration step, TerrainAdjustment terrainAdaptation) {

        public static final MapCodec<Structure.c> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(Structure.c::biomes), Codec.simpleMap(EnumCreatureType.CODEC, StructureSpawnOverride.CODEC, INamable.keys(EnumCreatureType.values())).fieldOf("spawn_overrides").forGetter(Structure.c::spawnOverrides), WorldGenStage.Decoration.CODEC.fieldOf("step").forGetter(Structure.c::step), TerrainAdjustment.CODEC.optionalFieldOf("terrain_adaptation", TerrainAdjustment.NONE).forGetter(Structure.c::terrainAdaptation)).apply(instance, Structure.c::new);
        });
    }

    public static record a(IRegistryCustom registryAccess, ChunkGenerator chunkGenerator, WorldChunkManager biomeSource, RandomState randomState, StructureTemplateManager structureTemplateManager, SeededRandom random, long seed, ChunkCoordIntPair chunkPos, LevelHeightAccessor heightAccessor, Predicate<Holder<BiomeBase>> validBiome) {

        public a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, RandomState randomstate, StructureTemplateManager structuretemplatemanager, long i, ChunkCoordIntPair chunkcoordintpair, LevelHeightAccessor levelheightaccessor, Predicate<Holder<BiomeBase>> predicate) {
            this(iregistrycustom, chunkgenerator, worldchunkmanager, randomstate, structuretemplatemanager, makeRandom(i, chunkcoordintpair), i, chunkcoordintpair, levelheightaccessor, predicate);
        }

        private static SeededRandom makeRandom(long i, ChunkCoordIntPair chunkcoordintpair) {
            SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

            seededrandom.setLargeFeatureSeed(i, chunkcoordintpair.x, chunkcoordintpair.z);
            return seededrandom;
        }
    }

    public static record b(BlockPosition position, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> generator) {

        public b(BlockPosition blockposition, Consumer<StructurePiecesBuilder> consumer) {
            this(blockposition, Either.left(consumer));
        }

        public StructurePiecesBuilder getPiecesBuilder() {
            return (StructurePiecesBuilder) this.generator.map((consumer) -> {
                StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();

                consumer.accept(structurepiecesbuilder);
                return structurepiecesbuilder;
            }, (structurepiecesbuilder) -> {
                return structurepiecesbuilder;
            });
        }
    }
}
