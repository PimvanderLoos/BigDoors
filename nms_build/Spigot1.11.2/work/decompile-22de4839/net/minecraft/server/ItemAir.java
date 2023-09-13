package net.minecraft.server;

public class ItemAir extends Item {

    private final Block a;

    public ItemAir(Block block) {
        this.a = block;
    }

    public String a(ItemStack itemstack) {
        return this.a.a();
    }

    public String getName() {
        return this.a.a();
    }
}
