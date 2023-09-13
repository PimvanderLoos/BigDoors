package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.WorldChunkManagerHell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.flat.GeneratorSettingsFlat;

public class ChunkProviderFlat extends ChunkGenerator {

    public static final Codec<ChunkProviderFlat> CODEC = GeneratorSettingsFlat.CODEC.fieldOf("settings").xmap(ChunkProviderFlat::new, ChunkProviderFlat::h).codec();
    private final GeneratorSettingsFlat settings;

    public ChunkProviderFlat(GeneratorSettingsFlat generatorsettingsflat) {
        super(new WorldChunkManagerHell(generatorsettingsflat.c()), new WorldChunkManagerHell(generatorsettingsflat.e()), generatorsettingsflat.d(), 0L);
        this.settings = generatorsettingsflat;
    }

    @Override
    protected Codec<? extends ChunkGenerator> a() {
        return ChunkProviderFlat.CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long i) {
        return this;
    }

    public GeneratorSettingsFlat h() {
        return this.settings;
    }

    @Override
    public void buildBase(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {}

    @Override
    public int getSpawnHeight(LevelHeightAccessor levelheightaccessor) {
        return levelheightaccessor.getMinBuildHeight() + Math.min(levelheightaccessor.getHeight(), this.settings.g().size());
    }

    @Override
    public CompletableFuture<IChunkAccess> buildNoise(Executor executor, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        List<IBlockData> list = this.settings.g();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        HeightMap heightmap = ichunkaccess.a(HeightMap.Type.OCEAN_FLOOR_WG);
        HeightMap heightmap1 = ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG);

        for (int i = 0; i < Math.min(ichunkaccess.getHeight(), list.size()); ++i) {
            IBlockData iblockdata = (IBlockData) list.get(i);

            if (iblockdata != null) {
                int j = ichunkaccess.getMinBuildHeight() + i;

                for (int k = 0; k < 16; ++k) {
                    for (int l = 0; l < 16; ++l) {
                        ichunkaccess.setType(blockposition_mutableblockposition.d(k, j, l), iblockdata, false);
                        heightmap.a(k, j, l, iblockdata);
                        heightmap1.a(k, j, l, iblockdata);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(ichunkaccess);
    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        List<IBlockData> list = this.settings.g();

        for (int k = Math.min(list.size(), levelheightaccessor.getMaxBuildHeight()) - 1; k >= 0; --k) {
            IBlockData iblockdata = (IBlockData) list.get(k);

            if (iblockdata != null && heightmap_type.e().test(iblockdata)) {
                return levelheightaccessor.getMinBuildHeight() + k + 1;
            }
        }

        return levelheightaccessor.getMinBuildHeight();
    }

    @Override
    public BlockColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor) {
        return new BlockColumn(levelheightaccessor.getMinBuildHeight(), (IBlockData[]) this.settings.g().stream().limit((long) levelheightaccessor.getHeight()).map((iblockdata) -> {
            return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
        }).toArray((k) -> {
            return new IBlockData[k];
        }));
    }
}
