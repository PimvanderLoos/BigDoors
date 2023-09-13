package net.minecraft.server;

public class ItemStep extends ItemBlock {

    private final BlockStepAbstract b;
    private final BlockStepAbstract c;

    public ItemStep(Block block, BlockStepAbstract blockstepabstract, BlockStepAbstract blockstepabstract1) {
        super(block);
        this.b = blockstepabstract;
        this.c = blockstepabstract1;
        this.setMaxDurability(0);
        this.a(true);
    }

    public int filterData(int i) {
        return i;
    }

    public String a(ItemStack itemstack) {
        return this.b.b(itemstack.getData());
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.isEmpty() && entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
            Comparable comparable = this.b.a(itemstack);
            IBlockData iblockdata = world.getType(blockposition);

            if (iblockdata.getBlock() == this.b) {
                IBlockState iblockstate = this.b.g();
                Comparable comparable1 = iblockdata.get(iblockstate);
                BlockStepAbstract.EnumSlabHalf blockstepabstract_enumslabhalf = (BlockStepAbstract.EnumSlabHalf) iblockdata.get(BlockStepAbstract.HALF);

                if ((enumdirection == EnumDirection.UP && blockstepabstract_enumslabhalf == BlockStepAbstract.EnumSlabHalf.BOTTOM || enumdirection == EnumDirection.DOWN && blockstepabstract_enumslabhalf == BlockStepAbstract.EnumSlabHalf.TOP) && comparable1 == comparable) {
                    IBlockData iblockdata1 = this.a(iblockstate, comparable1);
                    AxisAlignedBB axisalignedbb = iblockdata1.d(world, blockposition);

                    if (axisalignedbb != Block.k && world.b(axisalignedbb.a(blockposition)) && world.setTypeAndData(blockposition, iblockdata1, 11)) {
                        SoundEffectType soundeffecttype = this.c.getStepSound();

                        world.a(entityhuman, blockposition, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F);
                        itemstack.subtract(1);
                        if (entityhuman instanceof EntityPlayer) {
                            CriterionTriggers.x.a((EntityPlayer) entityhuman, blockposition, itemstack);
                        }
                    }

                    return EnumInteractionResult.SUCCESS;
                }
            }

            return this.a(entityhuman, itemstack, world, blockposition.shift(enumdirection), (Object) comparable) ? EnumInteractionResult.SUCCESS : super.a(entityhuman, world, blockposition, enumhand, enumdirection, f, f1, f2);
        } else {
            return EnumInteractionResult.FAIL;
        }
    }

    private boolean a(EntityHuman entityhuman, ItemStack itemstack, World world, BlockPosition blockposition, Object object) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() == this.b) {
            Comparable comparable = iblockdata.get(this.b.g());

            if (comparable == object) {
                IBlockData iblockdata1 = this.a(this.b.g(), comparable);
                AxisAlignedBB axisalignedbb = iblockdata1.d(world, blockposition);

                if (axisalignedbb != Block.k && world.b(axisalignedbb.a(blockposition)) && world.setTypeAndData(blockposition, iblockdata1, 11)) {
                    SoundEffectType soundeffecttype = this.c.getStepSound();

                    world.a(entityhuman, blockposition, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F);
                    itemstack.subtract(1);
                }

                return true;
            }
        }

        return false;
    }

    protected <T extends Comparable<T>> IBlockData a(IBlockState<T> iblockstate, Comparable<?> comparable) {
        return this.c.getBlockData().set(iblockstate, comparable);
    }
}
