package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.util.IntSpread;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public abstract class WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacer> d = IRegistry.FOLIAGE_PLACER_TYPE.dispatch(WorldGenFoilagePlacer::a, WorldGenFoilagePlacers::a);
    protected final IntSpread e;
    protected final IntSpread f;

    protected static <P extends WorldGenFoilagePlacer> P2<Mu<P>, IntSpread, IntSpread> b(Instance<P> instance) {
        return instance.group(IntSpread.a(0, 8, 8).fieldOf("radius").forGetter((worldgenfoilageplacer) -> {
            return worldgenfoilageplacer.e;
        }), IntSpread.a(0, 8, 8).fieldOf("offset").forGetter((worldgenfoilageplacer) -> {
            return worldgenfoilageplacer.f;
        }));
    }

    public WorldGenFoilagePlacer(IntSpread intspread, IntSpread intspread1) {
        this.e = intspread;
        this.f = intspread1;
    }

    protected abstract WorldGenFoilagePlacers<?> a();

    public void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, i, worldgenfoilageplacer_b, j, k, set, this.a(random), structureboundingbox);
    }

    protected abstract void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox);

    public abstract int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration);

    public int a(Random random, int i) {
        return this.e.a(random);
    }

    private int a(Random random) {
        return this.f.a(random);
    }

    protected abstract boolean a(Random random, int i, int j, int k, int l, boolean flag);

    protected boolean b(Random random, int i, int j, int k, int l, boolean flag) {
        int i1;
        int j1;

        if (flag) {
            i1 = Math.min(Math.abs(i), Math.abs(i - 1));
            j1 = Math.min(Math.abs(k), Math.abs(k - 1));
        } else {
            i1 = Math.abs(i);
            j1 = Math.abs(k);
        }

        return this.a(random, i1, j, j1, l, flag);
    }

    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, BlockPosition blockposition, int i, Set<BlockPosition> set, int j, boolean flag, StructureBoundingBox structureboundingbox) {
        int k = flag ? 1 : 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = -i; l <= i + k; ++l) {
            for (int i1 = -i; i1 <= i + k; ++i1) {
                if (!this.b(random, l, j, i1, i, flag)) {
                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, l, j, i1);
                    if (WorldGenTrees.e(virtuallevelwritable, blockposition_mutableblockposition)) {
                        virtuallevelwritable.setTypeAndData(blockposition_mutableblockposition, worldgenfeaturetreeconfiguration.c.a(random, blockposition_mutableblockposition), 19);
                        structureboundingbox.c(new StructureBoundingBox(blockposition_mutableblockposition, blockposition_mutableblockposition));
                        set.add(blockposition_mutableblockposition.immutableCopy());
                    }
                }
            }
        }

    }

    public static final class b {

        private final BlockPosition a;
        private final int b;
        private final boolean c;

        public b(BlockPosition blockposition, int i, boolean flag) {
            this.a = blockposition;
            this.b = i;
            this.c = flag;
        }

        public BlockPosition a() {
            return this.a;
        }

        public int b() {
            return this.b;
        }

        public boolean c() {
            return this.c;
        }
    }
}
