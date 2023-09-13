package net.minecraft.world.level.entity;

import net.minecraft.world.entity.Entity;

public interface EntityInLevelCallback {

    EntityInLevelCallback NULL = new EntityInLevelCallback() {
        @Override
        public void a() {}

        @Override
        public void a(Entity.RemovalReason entity_removalreason) {}
    };

    void a();

    void a(Entity.RemovalReason entity_removalreason);
}
