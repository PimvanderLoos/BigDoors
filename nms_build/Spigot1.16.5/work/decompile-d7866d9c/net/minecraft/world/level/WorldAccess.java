package net.minecraft.world.level;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;

public interface WorldAccess extends GeneratorAccess {

    WorldServer getMinecraftWorld();

    default void addAllEntities(Entity entity) {
        entity.recursiveStream().forEach(this::addEntity);
    }
}
