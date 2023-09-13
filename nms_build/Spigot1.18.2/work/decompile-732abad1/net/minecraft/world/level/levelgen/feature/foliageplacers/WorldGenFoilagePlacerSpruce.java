package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerSpruce extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerSpruce> CODEC = RecordCodecBuilder.create((instance) -> {
        return foliagePlacerParts(instance).and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter((worldgenfoilageplacerspruce) -> {
            return worldgenfoilageplacerspruce.trunkHeight;
        })).apply(instance, WorldGenFoilagePlacerSpruce::new);
    });
    private final IntProvider trunkHeight;

    public WorldGenFoilagePlacerSpruce(IntProvider intprovider, IntProvider intprovider1, IntProvider intprovider2) {
        super(intprovider, intprovider1);
        this.trunkHeight = intprovider2;
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.SPRUCE_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        BlockPosition blockposition = worldgenfoilageplacer_a.pos();
        int i1 = random.nextInt(2);
        int j1 = 1;
        byte b0 = 0;

        for (int k1 = l; k1 >= -j; --k1) {
            this.placeLeavesRow(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, i1, k1, worldgenfoilageplacer_a.doubleTrunk());
            if (i1 >= j1) {
                i1 = b0;
                b0 = 1;
                j1 = Math.min(j1 + 1, k + worldgenfoilageplacer_a.radiusOffset());
            } else {
                ++i1;
            }
        }

    }

    @Override
    public int foliageHeight(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return Math.max(4, i - this.trunkHeight.sample(random));
    }

    @Override
    protected boolean shouldSkipLocation(Random random, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && l > 0;
    }
}
