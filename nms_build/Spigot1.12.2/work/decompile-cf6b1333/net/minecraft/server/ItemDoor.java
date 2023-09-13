package net.minecraft.server;

public class ItemDoor extends Item {

    private final Block a;

    public ItemDoor(Block block) {
        this.a = block;
        this.b(CreativeModeTab.d);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (enumdirection != EnumDirection.UP) {
            return EnumInteractionResult.FAIL;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (!block.a((IBlockAccess) world, blockposition)) {
                blockposition = blockposition.shift(enumdirection);
            }

            ItemStack itemstack = entityhuman.b(enumhand);

            if (entityhuman.a(blockposition, enumdirection, itemstack) && this.a.canPlace(world, blockposition)) {
                EnumDirection enumdirection1 = EnumDirection.fromAngle((double) entityhuman.yaw);
                int i = enumdirection1.getAdjacentX();
                int j = enumdirection1.getAdjacentZ();
                boolean flag = i < 0 && f2 < 0.5F || i > 0 && f2 > 0.5F || j < 0 && f > 0.5F || j > 0 && f < 0.5F;

                a(world, blockposition, enumdirection1, this.a, flag);
                SoundEffectType soundeffecttype = this.a.getStepSound();

                world.a(entityhuman, blockposition, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F);
                itemstack.subtract(1);
                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.FAIL;
            }
        }
    }

    public static void a(World world, BlockPosition blockposition, EnumDirection enumdirection, Block block, boolean flag) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection.e());
        BlockPosition blockposition2 = blockposition.shift(enumdirection.f());
        int i = (world.getType(blockposition2).l() ? 1 : 0) + (world.getType(blockposition2.up()).l() ? 1 : 0);
        int j = (world.getType(blockposition1).l() ? 1 : 0) + (world.getType(blockposition1.up()).l() ? 1 : 0);
        boolean flag1 = world.getType(blockposition2).getBlock() == block || world.getType(blockposition2.up()).getBlock() == block;
        boolean flag2 = world.getType(blockposition1).getBlock() == block || world.getType(blockposition1.up()).getBlock() == block;

        if ((!flag1 || flag2) && j <= i) {
            if (flag2 && !flag1 || j < i) {
                flag = false;
            }
        } else {
            flag = true;
        }

        BlockPosition blockposition3 = blockposition.up();
        boolean flag3 = world.isBlockIndirectlyPowered(blockposition) || world.isBlockIndirectlyPowered(blockposition3);
        IBlockData iblockdata = block.getBlockData().set(BlockDoor.FACING, enumdirection).set(BlockDoor.HINGE, flag ? BlockDoor.EnumDoorHinge.RIGHT : BlockDoor.EnumDoorHinge.LEFT).set(BlockDoor.POWERED, Boolean.valueOf(flag3)).set(BlockDoor.OPEN, Boolean.valueOf(flag3));

        world.setTypeAndData(blockposition, iblockdata.set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
        world.setTypeAndData(blockposition3, iblockdata.set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
        world.applyPhysics(blockposition, block, false);
        world.applyPhysics(blockposition3, block, false);
    }
}
