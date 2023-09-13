package net.minecraft.server;

public class ItemFlintAndSteel extends Item {

    public ItemFlintAndSteel() {
        this.maxStackSize = 1;
        this.setMaxDurability(64);
        this.a(CreativeModeTab.i);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        blockposition = blockposition.shift(enumdirection);
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!entityhuman.a(blockposition, enumdirection, itemstack)) {
            return EnumInteractionResult.FAIL;
        } else {
            if (world.getType(blockposition).getMaterial() == Material.AIR) {
                world.a(entityhuman, blockposition, SoundEffects.bI, SoundCategory.BLOCKS, 1.0F, ItemFlintAndSteel.j.nextFloat() * 0.4F + 0.8F);
                world.setTypeAndData(blockposition, Blocks.FIRE.getBlockData(), 11);
            }

            itemstack.damage(1, entityhuman);
            return EnumInteractionResult.SUCCESS;
        }
    }
}
