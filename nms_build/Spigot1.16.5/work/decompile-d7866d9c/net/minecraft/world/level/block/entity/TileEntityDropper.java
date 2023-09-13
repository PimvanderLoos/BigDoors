package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public class TileEntityDropper extends TileEntityDispenser {

    public TileEntityDropper() {
        super(TileEntityTypes.DROPPER);
    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.dropper");
    }
}
