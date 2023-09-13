package net.minecraft.server;

public class ItemReed extends Item {

    private final Block a;

    public ItemReed(Block block) {
        this.a = block;
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (block == Blocks.SNOW_LAYER && ((Integer) iblockdata.get(BlockSnow.LAYERS)).intValue() < 1) {
            enumdirection = EnumDirection.UP;
        } else if (!block.a((IBlockAccess) world, blockposition)) {
            blockposition = blockposition.shift(enumdirection);
        }

        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.isEmpty() && entityhuman.a(blockposition, enumdirection, itemstack) && world.a(this.a, blockposition, false, enumdirection, (Entity) null)) {
            IBlockData iblockdata1 = this.a.getPlacedState(world, blockposition, enumdirection, f, f1, f2, 0, entityhuman);

            if (!world.setTypeAndData(blockposition, iblockdata1, 11)) {
                return EnumInteractionResult.FAIL;
            } else {
                iblockdata1 = world.getType(blockposition);
                if (iblockdata1.getBlock() == this.a) {
                    ItemBlock.a(world, entityhuman, blockposition, itemstack);
                    iblockdata1.getBlock().postPlace(world, blockposition, iblockdata1, entityhuman, itemstack);
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.x.a((EntityPlayer) entityhuman, blockposition, itemstack);
                    }
                }

                SoundEffectType soundeffecttype = this.a.getStepSound();

                world.a(entityhuman, blockposition, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F);
                itemstack.subtract(1);
                return EnumInteractionResult.SUCCESS;
            }
        } else {
            return EnumInteractionResult.FAIL;
        }
    }
}
