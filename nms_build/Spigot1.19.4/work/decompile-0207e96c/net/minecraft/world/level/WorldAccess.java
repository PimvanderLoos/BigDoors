package net.minecraft.world.level;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;

public interface WorldAccess extends GeneratorAccess {

    WorldServer getLevel();

    default void addFreshEntityWithPassengers(Entity entity) {
        entity.getSelfAndPassengers().forEach(this::addFreshEntity);
    }
}
