package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class WorldGenFeatureNetherFossil extends NoiseAffectingStructureFeature<RangeConfiguration> {

    public WorldGenFeatureNetherFossil(Codec<RangeConfiguration> codec) {
        super(codec, WorldGenFeatureNetherFossil::pieceGeneratorSupplier);
    }

    private static Optional<PieceGenerator<RangeConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.a<RangeConfiguration> piecegeneratorsupplier_a) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureSeed(piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos().x, piecegeneratorsupplier_a.chunkPos().z);
        int i = piecegeneratorsupplier_a.chunkPos().getMinBlockX() + seededrandom.nextInt(16);
        int j = piecegeneratorsupplier_a.chunkPos().getMinBlockZ() + seededrandom.nextInt(16);
        int k = piecegeneratorsupplier_a.chunkGenerator().getSeaLevel();
        WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(piecegeneratorsupplier_a.chunkGenerator(), piecegeneratorsupplier_a.heightAccessor());
        int l = ((RangeConfiguration) piecegeneratorsupplier_a.config()).height.sample(seededrandom, worldgenerationcontext);
        BlockColumn blockcolumn = piecegeneratorsupplier_a.chunkGenerator().getBaseColumn(i, j, piecegeneratorsupplier_a.heightAccessor());
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, l, j);

        while (l > k) {
            IBlockData iblockdata = blockcolumn.getBlock(l);

            --l;
            IBlockData iblockdata1 = blockcolumn.getBlock(l);

            if (iblockdata.isAir() && (iblockdata1.is(Blocks.SOUL_SAND) || iblockdata1.isFaceSturdy(BlockAccessAir.INSTANCE, blockposition_mutableblockposition.setY(l), EnumDirection.UP))) {
                break;
            }
        }

        if (l <= k) {
            return Optional.empty();
        } else if (!piecegeneratorsupplier_a.validBiome().test(piecegeneratorsupplier_a.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(l), QuartPos.fromBlock(j)))) {
            return Optional.empty();
        } else {
            BlockPosition blockposition = new BlockPosition(i, l, j);

            return Optional.of((structurepiecesbuilder, piecegenerator_a) -> {
                WorldGenNetherFossil.addPieces(piecegeneratorsupplier_a.structureManager(), structurepiecesbuilder, seededrandom, blockposition);
            });
        }
    }
}
