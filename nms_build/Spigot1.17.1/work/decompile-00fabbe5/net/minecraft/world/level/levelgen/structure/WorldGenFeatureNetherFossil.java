package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureNetherFossil extends StructureGenerator<WorldGenFeatureChanceDecoratorRangeConfiguration> {

    public WorldGenFeatureNetherFossil(Codec<WorldGenFeatureChanceDecoratorRangeConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureChanceDecoratorRangeConfiguration> a() {
        return WorldGenFeatureNetherFossil.a::new;
    }

    public static class a extends NoiseAffectingStructureStart<WorldGenFeatureChanceDecoratorRangeConfiguration> {

        public a(StructureGenerator<WorldGenFeatureChanceDecoratorRangeConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureChanceDecoratorRangeConfiguration worldgenfeaturechancedecoratorrangeconfiguration, LevelHeightAccessor levelheightaccessor) {
            int i = chunkcoordintpair.d() + this.random.nextInt(16);
            int j = chunkcoordintpair.e() + this.random.nextInt(16);
            int k = chunkgenerator.getSeaLevel();
            WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(chunkgenerator, levelheightaccessor);
            int l = worldgenfeaturechancedecoratorrangeconfiguration.height.a(this.random, worldgenerationcontext);
            BlockColumn blockcolumn = chunkgenerator.getBaseColumn(i, j, levelheightaccessor);

            for (BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, l, j); l > k; --l) {
                IBlockData iblockdata = blockcolumn.a(blockposition_mutableblockposition);

                blockposition_mutableblockposition.c(EnumDirection.DOWN);
                IBlockData iblockdata1 = blockcolumn.a(blockposition_mutableblockposition);

                if (iblockdata.isAir() && (iblockdata1.a(Blocks.SOUL_SAND) || iblockdata1.d(BlockAccessAir.INSTANCE, blockposition_mutableblockposition, EnumDirection.UP))) {
                    break;
                }
            }

            if (l > k) {
                WorldGenNetherFossil.a(definedstructuremanager, this, this.random, new BlockPosition(i, l, j));
            }
        }
    }
}
