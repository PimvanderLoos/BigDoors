package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
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

    public static final Codec<ChunkProviderDebug> d = RegistryLookupCodec.a(IRegistry.ay).xmap(ChunkProviderDebug::new, ChunkProviderDebug::g).stable().codec();
    private static final List<IBlockData> g = (List) StreamSupport.stream(IRegistry.BLOCK.spliterator(), false).flatMap((block) -> {
        return block.getStates().a().stream();
    }).collect(Collectors.toList());
    private static final int h = MathHelper.f(MathHelper.c((float) ChunkProviderDebug.g.size()));
    private static final int i = MathHelper.f((float) ChunkProviderDebug.g.size() / (float) ChunkProviderDebug.h);
    protected static final IBlockData e = Blocks.AIR.getBlockData();
    protected static final IBlockData f = Blocks.BARRIER.getBlockData();
    private final IRegistry<BiomeBase> j;

    public ChunkProviderDebug(IRegistry<BiomeBase> iregistry) {
        super(new WorldChunkManagerHell((BiomeBase) iregistry.d(Biomes.PLAINS)), new StructureSettings(false));
        this.j = iregistry;
    }

    public IRegistry<BiomeBase> g() {
        return this.j;
    }

    @Override
    protected Codec<? extends ChunkGenerator> a() {
        return ChunkProviderDebug.d;
    }

    @Override
    public void buildBase(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {}

    @Override
    public void doCarving(long i, BiomeManager biomemanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {}

    @Override
    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                int i1 = (i << 4) + k;
                int j1 = (j << 4) + l;

                regionlimitedworldaccess.setTypeAndData(blockposition_mutableblockposition.d(i1, 60, j1), ChunkProviderDebug.f, 2);
                IBlockData iblockdata = b(i1, j1);

                if (iblockdata != null) {
                    regionlimitedworldaccess.setTypeAndData(blockposition_mutableblockposition.d(i1, 70, j1), iblockdata, 2);
                }
            }
        }

    }

    @Override
    public void buildNoise(GeneratorAccess generatoraccess, StructureManager structuremanager, IChunkAccess ichunkaccess) {}

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type) {
        return 0;
    }

    @Override
    public IBlockAccess a(int i, int j) {
        return new BlockColumn(new IBlockData[0]);
    }

    public static IBlockData b(int i, int j) {
        IBlockData iblockdata = ChunkProviderDebug.e;

        if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
            i /= 2;
            j /= 2;
            if (i <= ChunkProviderDebug.h && j <= ChunkProviderDebug.i) {
                int k = MathHelper.a(i * ChunkProviderDebug.h + j);

                if (k < ChunkProviderDebug.g.size()) {
                    iblockdata = (IBlockData) ChunkProviderDebug.g.get(k);
                }
            }
        }

        return iblockdata;
    }
}
