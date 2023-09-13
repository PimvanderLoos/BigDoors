package net.minecraft.server;

public class TileEntityDropper extends TileEntityDispenser {

    public TileEntityDropper() {
        super(TileEntityTypes.DROPPER);
    }

    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        return (IChatBaseComponent) (ichatbasecomponent != null ? ichatbasecomponent : new ChatMessage("container.dropper", new Object[0]));
    }

    public String getContainerName() {
        return "minecraft:dropper";
    }
}
