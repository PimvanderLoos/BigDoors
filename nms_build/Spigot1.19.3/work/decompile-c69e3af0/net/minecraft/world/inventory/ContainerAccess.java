package net.minecraft.world.inventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;

public interface ContainerAccess {

    ContainerAccess NULL = new ContainerAccess() {
        @Override
        public <T> Optional<T> evaluate(BiFunction<World, BlockPosition, T> bifunction) {
            return Optional.empty();
        }
    };

    static ContainerAccess create(final World world, final BlockPosition blockposition) {
        return new ContainerAccess() {
            @Override
            public <T> Optional<T> evaluate(BiFunction<World, BlockPosition, T> bifunction) {
                return Optional.of(bifunction.apply(world, blockposition));
            }
        };
    }

    <T> Optional<T> evaluate(BiFunction<World, BlockPosition, T> bifunction);

    default <T> T evaluate(BiFunction<World, BlockPosition, T> bifunction, T t0) {
        return this.evaluate(bifunction).orElse(t0);
    }

    default void execute(BiConsumer<World, BlockPosition> biconsumer) {
        this.evaluate((world, blockposition) -> {
            biconsumer.accept(world, blockposition);
            return Optional.empty();
        });
    }
}
