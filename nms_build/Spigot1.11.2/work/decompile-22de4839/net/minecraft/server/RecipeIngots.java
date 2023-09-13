package net.minecraft.server;

public class RecipeIngots {

    private final Object[][] a;

    public RecipeIngots() {
        this.a = new Object[][] { { Blocks.GOLD_BLOCK, new ItemStack(Items.GOLD_INGOT, 9)}, { Blocks.IRON_BLOCK, new ItemStack(Items.IRON_INGOT, 9)}, { Blocks.DIAMOND_BLOCK, new ItemStack(Items.DIAMOND, 9)}, { Blocks.EMERALD_BLOCK, new ItemStack(Items.EMERALD, 9)}, { Blocks.LAPIS_BLOCK, new ItemStack(Items.DYE, 9, EnumColor.BLUE.getInvColorIndex())}, { Blocks.REDSTONE_BLOCK, new ItemStack(Items.REDSTONE, 9)}, { Blocks.COAL_BLOCK, new ItemStack(Items.COAL, 9, 0)}, { Blocks.HAY_BLOCK, new ItemStack(Items.WHEAT, 9)}, { Blocks.SLIME, new ItemStack(Items.SLIME, 9)}};
    }

    public void a(CraftingManager craftingmanager) {
        Object[][] aobject = this.a;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object[] aobject1 = aobject[j];
            Block block = (Block) aobject1[0];
            ItemStack itemstack = (ItemStack) aobject1[1];

            craftingmanager.registerShapedRecipe(new ItemStack(block), new Object[] { "###", "###", "###", Character.valueOf('#'), itemstack});
            craftingmanager.registerShapedRecipe(itemstack, new Object[] { "#", Character.valueOf('#'), block});
        }

        craftingmanager.registerShapedRecipe(new ItemStack(Items.GOLD_INGOT), new Object[] { "###", "###", "###", Character.valueOf('#'), Items.GOLD_NUGGET});
        craftingmanager.registerShapedRecipe(new ItemStack(Items.GOLD_NUGGET, 9), new Object[] { "#", Character.valueOf('#'), Items.GOLD_INGOT});
        craftingmanager.registerShapedRecipe(new ItemStack(Items.IRON_INGOT), new Object[] { "###", "###", "###", Character.valueOf('#'), Items.da});
        craftingmanager.registerShapedRecipe(new ItemStack(Items.da, 9), new Object[] { "#", Character.valueOf('#'), Items.IRON_INGOT});
    }
}
