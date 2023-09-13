package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.WorldChunkManagerHell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.flat.GeneratorSettingsFlat;

public class ChunkProviderFlat extends ChunkGenerator {

    public static final Codec<ChunkProviderFlat> d = GeneratorSettingsFlat.a.fieldOf("settings").xmap(ChunkProviderFlat::new, ChunkProviderFlat::g).codec();
    private final GeneratorSettingsFlat e;

    public ChunkProviderFlat(GeneratorSettingsFlat generatorsettingsflat) {
        super(new WorldChunkManagerHell(generatorsettingsflat.c()), new WorldChunkManagerHell(generatorsettingsflat.e()), generatorsettingsflat.d(), 0L);
        this.e = generatorsettingsflat;
    }

    @Override
    protected Codec<? extends ChunkGenerator> a() {
        return ChunkProviderFlat.d;
    }

    public GeneratorSettingsFlat g() {
        return this.e;
    }

    @Override
    public void buildBase(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {}

    @Override
    public int getSpawnHeight() {
        IBlockData[] aiblockdata = this.e.g();

        for (int i = 0; i < aiblockdata.length; ++i) {
            IBlockData iblockdata = aiblockdata[i] == null ? Blocks.AIR.getBlockData() : aiblockdata[i];

            if (!HeightMap.Type.MOTION_BLOCKING.e().test(iblockdata)) {
                return i - 1;
            }
        }

        return aiblockdata.length;
    }

    @Override
    public void buildNoise(GeneratorAccess generatoraccess, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        IBlockData[] aiblockdata = this.e.g();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        HeightMap heightmap = ichunkaccess.a(HeightMap.Type.OCEAN_FLOOR_WG);
        HeightMap heightmap1 = ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG);

        for (int i = 0; i < aiblockdata.length; ++i) {
            IBlockData iblockdata = aiblockdata[i];

            if (iblockdata != null) {
                for (int j = 0; j < 16; ++j) {
                    for (int k = 0; k < 16; ++k) {
                        ichunkaccess.setType(blockposition_mutableblockposition.d(j, i, k), iblockdata, false);
                        heightmap.a(j, i, k, iblockdata);
                        heightmap1.a(j, i, k, iblockdata);
                    }
                }
            }
        }

    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type) {
        IBlockData[] aiblockdata = this.e.g();

        for (int k = aiblockdata.length - 1; k >= 0; --k) {
            IBlockData iblockdata = aiblockdata[k];

            if (iblockdata != null && heightmap_type.e().test(iblockdata)) {
                return k + 1;
            }
        }

        return 0;
    }

    @Override
    public IBlockAccess a(int i, int j) {
        return new BlockColumn((IBlockData[]) Arrays.stream(this.e.g()).map((iblockdata) -> {
            return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
        }).toArray((k) -> {
            return new IBlockData[k];
        }));
    }
}
