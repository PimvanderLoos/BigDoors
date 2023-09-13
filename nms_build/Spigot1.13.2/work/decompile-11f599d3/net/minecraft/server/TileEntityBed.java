package net.minecraft.server;

public class TileEntityBed extends TileEntity {

    public EnumColor color;

    public TileEntityBed() {
        super(TileEntityTypes.BED);
    }

    public TileEntityBed(EnumColor enumcolor) {
        this();
        this.a(enumcolor);
    }

    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 11, this.aa_());
    }

    public void a(EnumColor enumcolor) {
        this.color = enumcolor;
    }
}
