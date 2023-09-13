package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;

public record RandomSpreadStructurePlacement(int c, int d, RandomSpreadType e, int f, BaseBlockPosition g) implements StructurePlacement {

    private final int spacing;
    private final int separation;
    private final RandomSpreadType spreadType;
    private final int salt;
    private final BaseBlockPosition locateOffset;
    public static final Codec<RandomSpreadStructurePlacement> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.intRange(0, 4096).fieldOf("spacing").forGetter(RandomSpreadStructurePlacement::spacing), Codec.intRange(0, 4096).fieldOf("separation").forGetter(RandomSpreadStructurePlacement::separation), RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::spreadType), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(RandomSpreadStructurePlacement::salt), BaseBlockPosition.offsetCodec(16).optionalFieldOf("locate_offset", BaseBlockPosition.ZERO).forGetter(RandomSpreadStructurePlacement::locateOffset)).apply(instance, RandomSpreadStructurePlacement::new);
    }).flatXmap((randomspreadstructureplacement) -> {
        return randomspreadstructureplacement.spacing <= randomspreadstructureplacement.separation ? DataResult.error("Spacing has to be larger than separation") : DataResult.success(randomspreadstructureplacement);
    }, DataResult::success).codec();

    public RandomSpreadStructurePlacement(int i, int j, RandomSpreadType randomspreadtype, int k) {
        this(i, j, randomspreadtype, k, BaseBlockPosition.ZERO);
    }

    public RandomSpreadStructurePlacement(int i, int j, RandomSpreadType randomspreadtype, int k, BaseBlockPosition baseblockposition) {
        this.spacing = i;
        this.separation = j;
        this.spreadType = randomspreadtype;
        this.salt = k;
        this.locateOffset = baseblockposition;
    }

    public ChunkCoordIntPair getPotentialFeatureChunk(long i, int j, int k) {
        int l = this.spacing();
        int i1 = this.separation();
        int j1 = Math.floorDiv(j, l);
        int k1 = Math.floorDiv(k, l);
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureWithSalt(i, j1, k1, this.salt());
        int l1 = l - i1;
        int i2 = this.spreadType().evaluate(seededrandom, l1);
        int j2 = this.spreadType().evaluate(seededrandom, l1);

        return new ChunkCoordIntPair(j1 * l + i2, k1 * l + j2);
    }

    @Override
    public boolean isFeatureChunk(ChunkGenerator chunkgenerator, long i, int j, int k) {
        ChunkCoordIntPair chunkcoordintpair = this.getPotentialFeatureChunk(i, j, k);

        return chunkcoordintpair.x == j && chunkcoordintpair.z == k;
    }

    @Override
    public StructurePlacementType<?> type() {
        return StructurePlacementType.RANDOM_SPREAD;
    }

    public int spacing() {
        return this.spacing;
    }

    public int separation() {
        return this.separation;
    }

    public RandomSpreadType spreadType() {
        return this.spreadType;
    }

    public int salt() {
        return this.salt;
    }

    public BaseBlockPosition locateOffset() {
        return this.locateOffset;
    }
}
