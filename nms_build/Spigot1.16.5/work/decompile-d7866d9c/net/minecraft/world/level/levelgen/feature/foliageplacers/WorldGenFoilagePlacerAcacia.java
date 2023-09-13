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

public class WorldGenFoilagePlacerAcacia extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerAcacia> a = RecordCodecBuilder.create((instance) -> {
        return b(instance).apply(instance, WorldGenFoilagePlacerAcacia::new);
    });

    public WorldGenFoilagePlacerAcacia(IntSpread intspread, IntSpread intspread1) {
        super(intspread, intspread1);
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.d;
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        boolean flag = worldgenfoilageplacer_b.c();
        BlockPosition blockposition = worldgenfoilageplacer_b.a().up(l);

        this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k + worldgenfoilageplacer_b.b(), set, -1 - j, flag, structureboundingbox);
        this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k - 1, set, -j, flag, structureboundingbox);
        this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, k + worldgenfoilageplacer_b.b() - 1, set, 0, flag, structureboundingbox);
    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return 0;
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return j == 0 ? (i > 1 || k > 1) && i != 0 && k != 0 : i == l && k == l && l > 0;
    }
}
