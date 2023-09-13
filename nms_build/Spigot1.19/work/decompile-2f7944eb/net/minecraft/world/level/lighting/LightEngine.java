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
        this.levelHeightAccessor = ilightaccess.getLevel();
        this.blockEngine = flag ? new LightEngineBlock(ilightaccess) : null;
        this.skyEngine = flag1 ? new LightEngineSky(ilightaccess) : null;
    }

    @Override
    public void checkBlock(BlockPosition blockposition) {
        if (this.blockEngine != null) {
            this.blockEngine.checkBlock(blockposition);
        }

        if (this.skyEngine != null) {
            this.skyEngine.checkBlock(blockposition);
        }

    }

    @Override
    public void onBlockEmissionIncrease(BlockPosition blockposition, int i) {
        if (this.blockEngine != null) {
            this.blockEngine.onBlockEmissionIncrease(blockposition, i);
        }

    }

    @Override
    public boolean hasLightWork() {
        return this.skyEngine != null && this.skyEngine.hasLightWork() ? true : this.blockEngine != null && this.blockEngine.hasLightWork();
    }

    @Override
    public int runUpdates(int i, boolean flag, boolean flag1) {
        if (this.blockEngine != null && this.skyEngine != null) {
            int j = i / 2;
            int k = this.blockEngine.runUpdates(j, flag, flag1);
            int l = i - j + k;
            int i1 = this.skyEngine.runUpdates(l, flag, flag1);

            return k == 0 && i1 > 0 ? this.blockEngine.runUpdates(i1, flag, flag1) : i1;
        } else {
            return this.blockEngine != null ? this.blockEngine.runUpdates(i, flag, flag1) : (this.skyEngine != null ? this.skyEngine.runUpdates(i, flag, flag1) : i);
        }
    }

    @Override
    public void updateSectionStatus(SectionPosition sectionposition, boolean flag) {
        if (this.blockEngine != null) {
            this.blockEngine.updateSectionStatus(sectionposition, flag);
        }

        if (this.skyEngine != null) {
            this.skyEngine.updateSectionStatus(sectionposition, flag);
        }

    }

    @Override
    public void enableLightSources(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        if (this.blockEngine != null) {
            this.blockEngine.enableLightSources(chunkcoordintpair, flag);
        }

        if (this.skyEngine != null) {
            this.skyEngine.enableLightSources(chunkcoordintpair, flag);
        }

    }

    public LightEngineLayerEventListener getLayerListener(EnumSkyBlock enumskyblock) {
        return (LightEngineLayerEventListener) (enumskyblock == EnumSkyBlock.BLOCK ? (this.blockEngine == null ? LightEngineLayerEventListener.Void.INSTANCE : this.blockEngine) : (this.skyEngine == null ? LightEngineLayerEventListener.Void.INSTANCE : this.skyEngine));
    }

    public String getDebugData(EnumSkyBlock enumskyblock, SectionPosition sectionposition) {
        if (enumskyblock == EnumSkyBlock.BLOCK) {
            if (this.blockEngine != null) {
                return this.blockEngine.getDebugData(sectionposition.asLong());
            }
        } else if (this.skyEngine != null) {
            return this.skyEngine.getDebugData(sectionposition.asLong());
        }

        return "n/a";
    }

    public void queueSectionData(EnumSkyBlock enumskyblock, SectionPosition sectionposition, @Nullable NibbleArray nibblearray, boolean flag) {
        if (enumskyblock == EnumSkyBlock.BLOCK) {
            if (this.blockEngine != null) {
                this.blockEngine.queueSectionData(sectionposition.asLong(), nibblearray, flag);
            }
        } else if (this.skyEngine != null) {
            this.skyEngine.queueSectionData(sectionposition.asLong(), nibblearray, flag);
        }

    }

    public void retainData(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        if (this.blockEngine != null) {
            this.blockEngine.retainData(chunkcoordintpair, flag);
        }

        if (this.skyEngine != null) {
            this.skyEngine.retainData(chunkcoordintpair, flag);
        }

    }

    public int getRawBrightness(BlockPosition blockposition, int i) {
        int j = this.skyEngine == null ? 0 : this.skyEngine.getLightValue(blockposition) - i;
        int k = this.blockEngine == null ? 0 : this.blockEngine.getLightValue(blockposition);

        return Math.max(k, j);
    }

    public int getLightSectionCount() {
        return this.levelHeightAccessor.getSectionsCount() + 2;
    }

    public int getMinLightSection() {
        return this.levelHeightAccessor.getMinSection() - 1;
    }

    public int getMaxLightSection() {
        return this.getMinLightSection() + this.getLightSectionCount();
    }
}
