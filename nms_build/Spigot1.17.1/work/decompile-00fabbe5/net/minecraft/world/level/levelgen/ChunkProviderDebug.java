package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.WorldChunkManagerHell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.IChunkAccess;

public class ChunkProviderDebug extends ChunkGenerator {

    public static final Codec<ChunkProviderDebug> CODEC = RegistryLookupCodec.a(IRegistry.BIOME_REGISTRY).xmap(ChunkProviderDebug::new, ChunkProviderDebug::h).stable().codec();
    private static final int BLOCK_MARGIN = 2;
    private static final List<IBlockData> ALL_BLOCKS = (List) StreamSupport.stream(IRegistry.BLOCK.spliterator(), false).flatMap((block) -> {
        return block.getStates().a().stream();
    }).collect(Collectors.toList());
    private static final int GRID_WIDTH = MathHelper.f(MathHelper.c((float) ChunkProviderDebug.ALL_BLOCKS.size()));
    private static final int GRID_HEIGHT = MathHelper.f((float) ChunkProviderDebug.ALL_BLOCKS.size() / (float) ChunkProviderDebug.GRID_WIDTH);
    protected static final IBlockData AIR = Blocks.AIR.getBlockData();
    protected static final IBlockData BARRIER = Blocks.BARRIER.getBlockData();
    public static final int HEIGHT = 70;
    public static final int BARRIER_HEIGHT = 60;
    private final IRegistry<BiomeBase> biomes;

    public ChunkProviderDebug(IRegistry<BiomeBase> iregistry) {
        super(new WorldChunkManagerHell((BiomeBase) iregistry.d(Biomes.PLAINS)), new StructureSettings(false));
        this.biomes = iregistry;
    }

    public IRegistry<BiomeBase> h() {
        return this.biomes;
    }

    @Override
    protected Codec<? extends ChunkGenerator> a() {
        return ChunkProviderDebug.CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long i) {
        return this;
    }

    @Override
    public void buildBase(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {}

    @Override
    public void doCarving(long i, BiomeManager biomemanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {}

    @Override
    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        ChunkCoordIntPair chunkcoordintpair = regionlimitedworldaccess.a();

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = SectionPosition.a(chunkcoordintpair.x, i);
                int l = SectionPosition.a(chunkcoordintpair.z, j);

                regionlimitedworldaccess.setTypeAndData(blockposition_mutableblockposition.d(k, 60, l), ChunkProviderDebug.BARRIER, 2);
                IBlockData iblockdata = a(k, l);

                if (iblockdata != null) {
                    regionlimitedworldaccess.setTypeAndData(blockposition_mutableblockposition.d(k, 70, l), iblockdata, 2);
                }
            }
        }

    }

    @Override
    public CompletableFuture<IChunkAccess> buildNoise(Executor executor, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        return CompletableFuture.completedFuture(ichunkaccess);
    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        return 0;
    }

    @Override
    public BlockColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor) {
        return new BlockColumn(0, new IBlockData[0]);
    }

    public static IBlockData a(int i, int j) {
        IBlockData iblockdata = ChunkProviderDebug.AIR;

        if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
            i /= 2;
            j /= 2;
            if (i <= ChunkProviderDebug.GRID_WIDTH && j <= ChunkProviderDebug.GRID_HEIGHT) {
                int k = MathHelper.a(i * ChunkProviderDebug.GRID_WIDTH + j);

                if (k < ChunkProviderDebug.ALL_BLOCKS.size()) {
                    iblockdata = (IBlockData) ChunkProviderDebug.ALL_BLOCKS.get(k);
                }
            }
        }

        return iblockdata;
    }
}
