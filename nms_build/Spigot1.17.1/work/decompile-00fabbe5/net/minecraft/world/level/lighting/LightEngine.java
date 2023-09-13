package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ILightAccess;
import net.minecraft.world.level.chunk.NibbleArray;

public class LightEngine implements ILightEngine {

    public static final int MAX_SOURCE_LEVEL = 15;
    public static final int LIGHT_SECTION_PADDING = 1;
    protected final LevelHeightAccessor levelHeightAccessor;
    @Nullable
    private final LightEngineLayer<?, ?> blockEngine;
    @Nullable
    private final LightEngineLayer<?, ?> skyEngine;

    public LightEngine(ILightAccess ilightaccess, boolean flag, boolean flag1) {
        this.levelHeightAccessor = ilightaccess.getWorld();
        this.blockEngine = flag ? new LightEngineBlock(ilightaccess) : null;
        this.skyEngine = flag1 ? new LightEngineSky(ilightaccess) : null;
    }

    @Override
    public void a(BlockPosition blockposition) {
        if (this.blockEngine != null) {
            this.blockEngine.a(blockposition);
        }

        if (this.skyEngine != null) {
            this.skyEngine.a(blockposition);
        }

    }

    @Override
    public void a(BlockPosition blockposition, int i) {
        if (this.blockEngine != null) {
            this.blockEngine.a(blockposition, i);
        }

    }

    @Override
    public boolean z_() {
        return this.skyEngine != null && this.skyEngine.z_() ? true : this.blockEngine != null && this.blockEngine.z_();
    }

    @Override
    public int a(int i, boolean flag, boolean flag1) {
        if (this.blockEngine != null && this.skyEngine != null) {
            int j = i / 2;
            int k = this.blockEngine.a(j, flag, flag1);
            int l = i - j + k;
            int i1 = this.skyEngine.a(l, flag, flag1);

            return k == 0 && i1 > 0 ? this.blockEngine.a(i1, flag, flag1) : i1;
        } else {
            return this.blockEngine != null ? this.blockEngine.a(i, flag, flag1) : (this.skyEngine != null ? this.skyEngine.a(i, flag, flag1) : i);
        }
    }

    @Override
    public void a(SectionPosition sectionposition, boolean flag) {
        if (this.blockEngine != null) {
            this.blockEngine.a(sectionposition, flag);
        }

        if (this.skyEngine != null) {
            this.skyEngine.a(sectionposition, flag);
        }

    }

    @Override
    public void a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        if (this.blockEngine != null) {
            this.blockEngine.a(chunkcoordintpair, flag);
        }

        if (this.skyEngine != null) {
            this.skyEngine.a(chunkcoordintpair, flag);
        }

    }

    public LightEngineLayerEventListener a(EnumSkyBlock enumskyblock) {
        return (LightEngineLayerEventListener) (enumskyblock == EnumSkyBlock.BLOCK ? (this.blockEngine == null ? LightEngineLayerEventListener.Void.INSTANCE : this.blockEngine) : (this.skyEngine == null ? LightEngineLayerEventListener.Void.INSTANCE : this.skyEngine));
    }

    public String a(EnumSkyBlock enumskyblock, SectionPosition sectionposition) {
        if (enumskyblock == EnumSkyBlock.BLOCK) {
            if (this.blockEngine != null) {
                return this.blockEngine.b(sectionposition.s());
            }
        } else if (this.skyEngine != null) {
            return this.skyEngine.b(sectionposition.s());
        }

        return "n/a";
    }

    public void a(EnumSkyBlock enumskyblock, SectionPosition sectionposition, @Nullable NibbleArray nibblearray, boolean flag) {
        if (enumskyblock == EnumSkyBlock.BLOCK) {
            if (this.blockEngine != null) {
                this.blockEngine.a(sectionposition.s(), nibblearray, flag);
            }
        } else if (this.skyEngine != null) {
            this.skyEngine.a(sectionposition.s(), nibblearray, flag);
        }

    }

    public void b(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        if (this.blockEngine != null) {
            this.blockEngine.b(chunkcoordintpair, flag);
        }

        if (this.skyEngine != null) {
            this.skyEngine.b(chunkcoordintpair, flag);
        }

    }

    public int b(BlockPosition blockposition, int i) {
        int j = this.skyEngine == null ? 0 : this.skyEngine.b(blockposition) - i;
        int k = this.blockEngine == null ? 0 : this.blockEngine.b(blockposition);

        return Math.max(k, j);
    }

    public int b() {
        return this.levelHeightAccessor.getSectionsCount() + 2;
    }

    public int c() {
        return this.levelHeightAccessor.getMinSection() - 1;
    }

    public int d() {
        return this.c() + this.b();
    }
}
