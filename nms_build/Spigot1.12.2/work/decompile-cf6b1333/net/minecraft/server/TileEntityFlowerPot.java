package net.minecraft.server;

import javax.annotation.Nullable;

public class TileEntityFlowerPot extends TileEntity {

    private Item a;
    private int f;

    public TileEntityFlowerPot() {}

    public TileEntityFlowerPot(Item item, int i) {
        this.a = item;
        this.f = i;
    }

    public static void a(DataConverterManager dataconvertermanager) {}

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        MinecraftKey minecraftkey = (MinecraftKey) Item.REGISTRY.b(this.a);

        nbttagcompound.setString("Item", minecraftkey == null ? "" : minecraftkey.toString());
        nbttagcompound.setInt("Data", this.f);
        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Item", 8)) {
            this.a = Item.b(nbttagcompound.getString("Item"));
        } else {
            this.a = Item.getById(nbttagcompound.getInt("Item"));
        }

        this.f = nbttagcompound.getInt("Data");
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 5, this.d());
    }

    public NBTTagCompound d() {
        return this.save(new NBTTagCompound());
    }

    public void setContents(ItemStack itemstack) {
        this.a = itemstack.getItem();
        this.f = itemstack.getData();
    }

    public ItemStack getContents() {
        return this.a == null ? ItemStack.a : new ItemStack(this.a, 1, this.f);
    }

    @Nullable
    public Item getItem() {
        return this.a;
    }

    public int getData() {
        return this.f;
    }
}
