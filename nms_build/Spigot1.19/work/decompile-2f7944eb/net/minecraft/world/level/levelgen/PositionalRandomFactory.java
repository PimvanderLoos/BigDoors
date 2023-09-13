package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.RandomSource;

public interface PositionalRandomFactory {

    default RandomSource at(BlockPosition blockposition) {
        return this.at(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    default RandomSource fromHashOf(MinecraftKey minecraftkey) {
        return this.fromHashOf(minecraftkey.toString());
    }

    RandomSource fromHashOf(String s);

    RandomSource at(int i, int j, int k);

    @VisibleForTesting
    void parityConfigString(StringBuilder stringbuilder);
}
