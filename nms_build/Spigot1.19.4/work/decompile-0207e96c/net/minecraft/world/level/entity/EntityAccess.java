package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AxisAlignedBB;

public interface EntityAccess {

    int getId();

    UUID getUUID();

    BlockPosition blockPosition();

    AxisAlignedBB getBoundingBox();

    void setLevelCallback(EntityInLevelCallback entityinlevelcallback);

    Stream<? extends EntityAccess> getSelfAndPassengers();

    Stream<? extends EntityAccess> getPassengersAndSelf();

    void setRemoved(Entity.RemovalReason entity_removalreason);

    boolean shouldBeSaved();

    boolean isAlwaysTicking();
}
