package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenBlockPlacerColumn extends WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacerColumn> b = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("min_size").forGetter((worldgenblockplacercolumn) -> {
            return worldgenblockplacercolumn.c;
        }), Codec.INT.fieldOf("extra_size").forGetter((worldgenblockplacercolumn) -> {
            return worldgenblockplacercolumn.d;
        })).apply(instance, WorldGenBlockPlacerColumn::new);
    });
    private final int c;
    private final int d;

    public WorldGenBlockPlacerColumn(int i, int j) {
        this.c = i;
        this.d = j;
    }

    @Override
    protected WorldGenBlockPlacers<?> a() {
        return WorldGenBlockPlacers.c;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        int i = this.c + random.nextInt(random.nextInt(this.d + 1) + 1);

        for (int j = 0; j < i; ++j) {
            generatoraccess.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

    }
}
