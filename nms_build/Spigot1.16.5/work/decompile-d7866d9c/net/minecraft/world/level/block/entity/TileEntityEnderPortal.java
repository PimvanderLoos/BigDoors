package net.minecraft.world.level.block.entity;

public class TileEntityEnderPortal extends TileEntity {

    public TileEntityEnderPortal(TileEntityTypes<?> tileentitytypes) {
        super(tileentitytypes);
    }

    public TileEntityEnderPortal() {
        this(TileEntityTypes.END_PORTAL);
    }
}
