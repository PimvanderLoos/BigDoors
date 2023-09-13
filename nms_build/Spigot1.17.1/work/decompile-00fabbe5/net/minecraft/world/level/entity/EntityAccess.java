package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AxisAlignedBB;

public interface EntityAccess {

    int getId();

    UUID getUniqueID();

    BlockPosition getChunkCoordinates();

    AxisAlignedBB getBoundingBox();

    void a(EntityInLevelCallback entityinlevelcallback);

    Stream<? extends EntityAccess> recursiveStream();

    Stream<? extends EntityAccess> cD();

    void setRemoved(Entity.RemovalReason entity_removalreason);

    boolean dm();

    boolean dn();
}
