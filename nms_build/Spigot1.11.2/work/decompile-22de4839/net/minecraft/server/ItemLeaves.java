package net.minecraft.server;

public class ItemLeaves extends ItemBlock {

    private final BlockLeaves b;

    public ItemLeaves(BlockLeaves blockleaves) {
        super(blockleaves);
        this.b = blockleaves;
        this.setMaxDurability(0);
        this.a(true);
    }

    public int filterData(int i) {
        return i | 4;
    }

    public String a(ItemStack itemstack) {
        return super.getName() + "." + this.b.e(itemstack.getData()).d();
    }
}
