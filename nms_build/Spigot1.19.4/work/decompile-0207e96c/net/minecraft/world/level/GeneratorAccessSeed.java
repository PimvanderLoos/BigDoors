package net.minecraft.world.level;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;

public interface GeneratorAccessSeed extends WorldAccess {

    long getSeed();

    default boolean ensureCanWrite(BlockPosition blockposition) {
        return true;
    }

    default void setCurrentlyGenerating(@Nullable Supplier<String> supplier) {}
}
