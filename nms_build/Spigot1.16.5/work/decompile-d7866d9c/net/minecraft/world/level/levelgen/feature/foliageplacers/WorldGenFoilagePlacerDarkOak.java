package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.IntSpread;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class WorldGenFoilagePlacerDarkOak extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerDarkOak> a = RecordCodecBuilder.create((instance) -> {
        return b(instance).apply(instance, WorldGenFoilagePlacerDarkOak::new);
    });

    public WorldGenFoilagePlacerDarkOak(IntSpread intspread, IntSpread intspread1) {
        super(intspread, intspread1);
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.i;
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        BlockPosition blockposition = worldgenfoilageplacer_b.a().up(l);
        boolean flag = worldgenfoilageplacer_b.c();

        if (flag) {
            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, set, -1, flag, structureboundingbox);
            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k + 3, set, 0, flag, structureboundingbox);
            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, set, 1, flag, structureboundingbox);
            if (random.nextBoolean()) {
                this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k, set, 2, flag, structureboundingbox);
            }
        } else {
            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, set, -1, flag, structureboundingbox);
            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k + 1, set, 0, flag, structureboundingbox);
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return 4;
    }

    @Override
    protected boolean b(Random random, int i, int j, int k, int l, boolean flag) {
        return j == 0 && flag && (i == -l || i >= l) && (k == -l || k >= l) ? true : super.b(random, i, j, k, l, flag);
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return j == -1 && !flag ? i == l && k == l : (j == 1 ? i + k > l * 2 - 2 : false);
    }
}
