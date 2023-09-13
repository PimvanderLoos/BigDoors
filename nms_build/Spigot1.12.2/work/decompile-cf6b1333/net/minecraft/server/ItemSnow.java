package net.minecraft.server;

public class ItemSnow extends ItemBlock {

    public ItemSnow(Block block) {
        super(block);
        this.setMaxDurability(0);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.isEmpty() && entityhuman.a(blockposition, enumdirection, itemstack)) {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();
            BlockPosition blockposition1 = blockposition;

            if ((enumdirection != EnumDirection.UP || block != this.a) && !block.a((IBlockAccess) world, blockposition)) {
                blockposition1 = blockposition.shift(enumdirection);
                iblockdata = world.getType(blockposition1);
                block = iblockdata.getBlock();
            }

            if (block == this.a) {
                int i = ((Integer) iblockdata.get(BlockSnow.LAYERS)).intValue();

                if (i < 8) {
                    IBlockData iblockdata1 = iblockdata.set(BlockSnow.LAYERS, Integer.valueOf(i + 1));
                    AxisAlignedBB axisalignedbb = iblockdata1.d(world, blockposition1);

                    if (axisalignedbb != Block.k && world.b(axisalignedbb.a(blockposition1)) && world.setTypeAndData(blockposition1, iblockdata1, 10)) {
                        SoundEffectType soundeffecttype = this.a.getStepSound();

                        world.a(entityhuman, blockposition1, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F);
                        if (entityhuman instanceof EntityPlayer) {
                            CriterionTriggers.x.a((EntityPlayer) entityhuman, blockposition, itemstack);
                        }

                        itemstack.subtract(1);
                        return EnumInteractionResult.SUCCESS;
                    }
                }
            }

            return super.a(entityhuman, world, blockposition, enumhand, enumdirection, f, f1, f2);
        } else {
            return EnumInteractionResult.FAIL;
        }
    }

    public int filterData(int i) {
        return i;
    }
}
