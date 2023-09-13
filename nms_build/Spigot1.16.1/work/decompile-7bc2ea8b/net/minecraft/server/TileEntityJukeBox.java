package net.minecraft.server;

public class TileEntityJukeBox extends TileEntity implements Clearable {

    private ItemStack a;

    public TileEntityJukeBox() {
        super(TileEntityTypes.JUKEBOX);
        this.a = ItemStack.b;
    }

    @Override
    public void load(IBlockData iblockdata, NBTTagCompound nbttagcompound) {
        super.load(iblockdata, nbttagcompound);
        if (nbttagcompound.hasKeyOfType("RecordItem", 10)) {
            this.setRecord(ItemStack.a(nbttagcompound.getCompound("RecordItem")));
        }

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (!this.getRecord().isEmpty()) {
            nbttagcompound.set("RecordItem", this.getRecord().save(new NBTTagCompound()));
        }

        return nbttagcompound;
    }

    public ItemStack getRecord() {
        return this.a;
    }

    public void setRecord(ItemStack itemstack) {
        this.a = itemstack;
        this.update();
    }

    @Override
    public void clear() {
        this.setRecord(ItemStack.b);
    }
}
