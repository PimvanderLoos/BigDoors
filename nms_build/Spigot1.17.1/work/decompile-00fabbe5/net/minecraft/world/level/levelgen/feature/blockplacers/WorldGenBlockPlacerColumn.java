package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenBlockPlacerColumn extends WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacerColumn> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IntProvider.NON_NEGATIVE_CODEC.fieldOf("size").forGetter((worldgenblockplacercolumn) -> {
            return worldgenblockplacercolumn.size;
        })).apply(instance, WorldGenBlockPlacerColumn::new);
    });
    private final IntProvider size;

    public WorldGenBlockPlacerColumn(IntProvider intprovider) {
        this.size = intprovider;
    }

    @Override
    protected WorldGenBlockPlacers<?> a() {
        return WorldGenBlockPlacers.COLUMN_PLACER;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        int i = this.size.a(random);

        for (int j = 0; j < i; ++j) {
            generatoraccess.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

    }
}
