package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.datafixers.Products.P5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Optional;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.INamable;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public abstract class StructurePlacement {

    public static final Codec<StructurePlacement> CODEC = BuiltInRegistries.STRUCTURE_PLACEMENT.byNameCodec().dispatch(StructurePlacement::type, StructurePlacementType::codec);
    private static final int HIGHLY_ARBITRARY_RANDOM_SALT = 10387320;
    public final BaseBlockPosition locateOffset;
    public final StructurePlacement.c frequencyReductionMethod;
    public final float frequency;
    public final int salt;
    public final Optional<StructurePlacement.a> exclusionZone;

    protected static <S extends StructurePlacement> P5<Mu<S>, BaseBlockPosition, StructurePlacement.c, Float, Integer, Optional<StructurePlacement.a>> placementCodec(Instance<S> instance) {
        return instance.group(BaseBlockPosition.offsetCodec(16).optionalFieldOf("locate_offset", BaseBlockPosition.ZERO).forGetter(StructurePlacement::locateOffset), StructurePlacement.c.CODEC.optionalFieldOf("frequency_reduction_method", StructurePlacement.c.DEFAULT).forGetter(StructurePlacement::frequencyReductionMethod), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(StructurePlacement::frequency), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(StructurePlacement::salt), StructurePlacement.a.CODEC.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::exclusionZone));
    }

    protected StructurePlacement(BaseBlockPosition baseblockposition, StructurePlacement.c structureplacement_c, float f, int i, Optional<StructurePlacement.a> optional) {
        this.locateOffset = baseblockposition;
        this.frequencyReductionMethod = structureplacement_c;
        this.frequency = f;
        this.salt = i;
        this.exclusionZone = optional;
    }

    protected BaseBlockPosition locateOffset() {
        return this.locateOffset;
    }

    protected StructurePlacement.c frequencyReductionMethod() {
        return this.frequencyReductionMethod;
    }

    protected float frequency() {
        return this.frequency;
    }

    protected int salt() {
        return this.salt;
    }

    protected Optional<StructurePlacement.a> exclusionZone() {
        return this.exclusionZone;
    }

    public boolean isStructureChunk(ChunkGeneratorStructureState chunkgeneratorstructurestate, int i, int j) {
        return !this.isPlacementChunk(chunkgeneratorstructurestate, i, j) ? false : (this.frequency < 1.0F && !this.frequencyReductionMethod.shouldGenerate(chunkgeneratorstructurestate.getLevelSeed(), this.salt, i, j, this.frequency) ? false : !this.exclusionZone.isPresent() || !((StructurePlacement.a) this.exclusionZone.get()).isPlacementForbidden(chunkgeneratorstructurestate, i, j));
    }

    protected abstract boolean isPlacementChunk(ChunkGeneratorStructureState chunkgeneratorstructurestate, int i, int j);

    public BlockPosition getLocatePos(ChunkCoordIntPair chunkcoordintpair) {
        return (new BlockPosition(chunkcoordintpair.getMinBlockX(), 0, chunkcoordintpair.getMinBlockZ())).offset(this.locateOffset());
    }

    public abstract StructurePlacementType<?> type();

    private static boolean probabilityReducer(long i, int j, int k, int l, float f) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureWithSalt(i, j, k, l);
        return seededrandom.nextFloat() < f;
    }

    private static boolean legacyProbabilityReducerWithDouble(long i, int j, int k, int l, float f) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureSeed(i, k, l);
        return seededrandom.nextDouble() < (double) f;
    }

    private static boolean legacyArbitrarySaltProbabilityReducer(long i, int j, int k, int l, float f) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureWithSalt(i, k, l, 10387320);
        return seededrandom.nextFloat() < f;
    }

    private static boolean legacyPillagerOutpostReducer(long i, int j, int k, int l, float f) {
        int i1 = k >> 4;
        int j1 = l >> 4;
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setSeed((long) (i1 ^ j1 << 4) ^ i);
        seededrandom.nextInt();
        return seededrandom.nextInt((int) (1.0F / f)) == 0;
    }

    public static enum c implements INamable {

        DEFAULT("default", StructurePlacement::probabilityReducer), LEGACY_TYPE_1("legacy_type_1", StructurePlacement::legacyPillagerOutpostReducer), LEGACY_TYPE_2("legacy_type_2", StructurePlacement::legacyArbitrarySaltProbabilityReducer), LEGACY_TYPE_3("legacy_type_3", StructurePlacement::legacyProbabilityReducerWithDouble);

        public static final Codec<StructurePlacement.c> CODEC = INamable.fromEnum(StructurePlacement.c::values);
        private final String name;
        private final StructurePlacement.b reducer;

        private c(String s, StructurePlacement.b structureplacement_b) {
            this.name = s;
            this.reducer = structureplacement_b;
        }

        public boolean shouldGenerate(long i, int j, int k, int l, float f) {
            return this.reducer.shouldGenerate(i, j, k, l, f);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    /** @deprecated */
    @Deprecated
    public static record a(Holder<StructureSet> otherSet, int chunkCount) {

        public static final Codec<StructurePlacement.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(RegistryFileCodec.create(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC, false).fieldOf("other_set").forGetter(StructurePlacement.a::otherSet), Codec.intRange(1, 16).fieldOf("chunk_count").forGetter(StructurePlacement.a::chunkCount)).apply(instance, StructurePlacement.a::new);
        });

        boolean isPlacementForbidden(ChunkGeneratorStructureState chunkgeneratorstructurestate, int i, int j) {
            return chunkgeneratorstructurestate.hasStructureChunkInRange(this.otherSet, i, j, this.chunkCount);
        }
    }

    @FunctionalInterface
    public interface b {

        boolean shouldGenerate(long i, int j, int k, int l, float f);
    }
}
