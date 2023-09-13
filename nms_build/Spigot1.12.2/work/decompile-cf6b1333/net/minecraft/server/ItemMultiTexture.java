package net.minecraft.server;

public class ItemMultiTexture extends ItemBlock {

    protected final Block b;
    protected final ItemMultiTexture.a c;

    public ItemMultiTexture(Block block, Block block1, ItemMultiTexture.a itemmultitexture_a) {
        super(block);
        this.b = block1;
        this.c = itemmultitexture_a;
        this.setMaxDurability(0);
        this.a(true);
    }

    public ItemMultiTexture(Block block, Block block1, final String[] astring) {
        this(block, block1, new ItemMultiTexture.a() {
            public String a(ItemStack itemstack) {
                int i = itemstack.getData();

                if (i < 0 || i >= astring.length) {
                    i = 0;
                }

                return astring[i];
            }
        });
    }

    public int filterData(int i) {
        return i;
    }

    public String a(ItemStack itemstack) {
        return super.getName() + "." + this.c.a(itemstack);
    }

    public interface a {

        String a(ItemStack itemstack);
    }
}
