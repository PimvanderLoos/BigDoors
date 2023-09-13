package net.minecraft.server;

public class TileEntityBed extends TileEntity {

    private EnumColor a;

    public TileEntityBed() {
        this.a = EnumColor.RED;
    }

    public void a(ItemStack itemstack) {
        this.a(EnumColor.fromColorIndex(itemstack.getData()));
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKey("color")) {
            this.a = EnumColor.fromColorIndex(nbttagcompound.getInt("color"));
        }

    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("color", this.a.getColorIndex());
        return nbttagcompound;
    }

    public NBTTagCompound d() {
        return this.save(new NBTTagCompound());
    }

    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 11, this.d());
    }

    public EnumColor a() {
        return this.a;
    }

    public void a(EnumColor enumcolor) {
        this.a = enumcolor;
        this.update();
    }

    public ItemStack f() {
        return new ItemStack(Items.BED, 1, this.a.getColorIndex());
    }
}
