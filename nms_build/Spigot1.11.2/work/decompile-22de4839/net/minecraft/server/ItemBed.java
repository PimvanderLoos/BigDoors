package net.minecraft.server;

public class ItemBed extends Item {

    public ItemBed() {
        this.a(CreativeModeTab.c);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else if (enumdirection != EnumDirection.UP) {
            return EnumInteractionResult.FAIL;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();
            boolean flag = block.a((IBlockAccess) world, blockposition);

            if (!flag) {
                blockposition = blockposition.up();
            }

            int i = MathHelper.floor((double) (entityhuman.yaw * 4.0F / 360.0F) + 0.5D) & 3;
            EnumDirection enumdirection1 = EnumDirection.fromType2(i);
            BlockPosition blockposition1 = blockposition.shift(enumdirection1);
            ItemStack itemstack = entityhuman.b(enumhand);

            if (entityhuman.a(blockposition, enumdirection, itemstack) && entityhuman.a(blockposition1, enumdirection, itemstack)) {
                IBlockData iblockdata1 = world.getType(blockposition1);
                boolean flag1 = iblockdata1.getBlock().a((IBlockAccess) world, blockposition1);
                boolean flag2 = flag || world.isEmpty(blockposition);
                boolean flag3 = flag1 || world.isEmpty(blockposition1);

                if (flag2 && flag3 && world.getType(blockposition.down()).r() && world.getType(blockposition1.down()).r()) {
                    IBlockData iblockdata2 = Blocks.BED.getBlockData().set(BlockBed.OCCUPIED, Boolean.valueOf(false)).set(BlockBed.FACING, enumdirection1).set(BlockBed.PART, BlockBed.EnumBedPart.FOOT);

                    world.setTypeAndData(blockposition, iblockdata2, 10);
                    world.setTypeAndData(blockposition1, iblockdata2.set(BlockBed.PART, BlockBed.EnumBedPart.HEAD), 10);
                    world.update(blockposition, block, false);
                    world.update(blockposition1, iblockdata1.getBlock(), false);
                    SoundEffectType soundeffecttype = iblockdata2.getBlock().getStepSound();

                    world.a((EntityHuman) null, blockposition, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F);
                    itemstack.subtract(1);
                    return EnumInteractionResult.SUCCESS;
                } else {
                    return EnumInteractionResult.FAIL;
                }
            } else {
                return EnumInteractionResult.FAIL;
            }
        }
    }
}
