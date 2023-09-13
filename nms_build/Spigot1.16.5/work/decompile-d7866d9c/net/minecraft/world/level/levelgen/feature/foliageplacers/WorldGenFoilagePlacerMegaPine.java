package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.IntSpread;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class WorldGenFoilagePlacerMegaPine extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerMegaPine> a = RecordCodecBuilder.create((instance) -> {
        return b(instance).and(IntSpread.a(0, 16, 8).fieldOf("crown_height").forGetter((worldgenfoilageplacermegapine) -> {
            return worldgenfoilageplacermegapine.b;
        })).apply(instance, WorldGenFoilagePlacerMegaPine::new);
    });
    private final IntSpread b;

    public WorldGenFoilagePlacerMegaPine(IntSpread intspread, IntSpread intspread1, IntSpread intspread2) {
        super(intspread, intspread1);
        this.b = intspread2;
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.h;
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        BlockPosition blockposition = worldgenfoilageplacer_b.a();
        int i1 = 0;

        for (int j1 = blockposition.getY() - j + l; j1 <= blockposition.getY() + l; ++j1) {
            int k1 = blockposition.getY() - j1;
            int l1 = k + worldgenfoilageplacer_b.b() + MathHelper.d((float) k1 / (float) j * 3.5F);
            int i2;

            if (k1 > 0 && l1 == i1 && (j1 & 1) == 0) {
                i2 = l1 + 1;
            } else {
                i2 = l1;
            }

            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, new BlockPosition(blockposition.getX(), j1, blockposition.getZ()), i2, set, 0, worldgenfoilageplacer_b.c(), structureboundingbox);
            i1 = l1;
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.b.a(random);
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i + k >= 7 ? true : i * i + k * k > l * l;
    }
}
