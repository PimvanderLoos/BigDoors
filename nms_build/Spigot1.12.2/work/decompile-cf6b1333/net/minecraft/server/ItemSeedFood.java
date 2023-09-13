package net.minecraft.server;

public class ItemSeedFood extends ItemFood {

    private final Block b;
    private final Block c;

    public ItemSeedFood(int i, float f, Block block, Block block1) {
        super(i, f, false);
        this.b = block;
        this.c = block1;
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (enumdirection == EnumDirection.UP && entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack) && world.getType(blockposition).getBlock() == this.c && world.isEmpty(blockposition.up())) {
            world.setTypeAndData(blockposition.up(), this.b.getBlockData(), 11);
            itemstack.subtract(1);
            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.FAIL;
        }
    }
}
