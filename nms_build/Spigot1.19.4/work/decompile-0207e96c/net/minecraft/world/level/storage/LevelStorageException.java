package net.minecraft.world.level.storage;

import net.minecraft.network.chat.IChatBaseComponent;

public class LevelStorageException extends RuntimeException {

    private final IChatBaseComponent messageComponent;

    public LevelStorageException(IChatBaseComponent ichatbasecomponent) {
        super(ichatbasecomponent.getString());
        this.messageComponent = ichatbasecomponent;
    }

    public IChatBaseComponent getMessageComponent() {
        return this.messageComponent;
    }
}
