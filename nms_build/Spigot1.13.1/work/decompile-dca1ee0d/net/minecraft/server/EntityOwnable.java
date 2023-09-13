package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public interface EntityOwnable {

    @Nullable
    UUID getOwnerUUID();

    @Nullable
    Entity getOwner();
}
