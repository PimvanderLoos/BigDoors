package net.minecraft.server;

import java.util.List;

public class ItemEndCrystal extends Item {

    public ItemEndCrystal() {
        this.c("end_crystal");
        this.b(CreativeModeTab.c);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() != Blocks.OBSIDIAN && iblockdata.getBlock() != Blocks.BEDROCK) {
            return EnumInteractionResult.FAIL;
        } else {
            BlockPosition blockposition1 = blockposition.up();
            ItemStack itemstack = entityhuman.b(enumhand);

            if (!entityhuman.a(blockposition1, enumdirection, itemstack)) {
                return EnumInteractionResult.FAIL;
            } else {
                BlockPosition blockposition2 = blockposition1.up();
                boolean flag = !world.isEmpty(blockposition1) && !world.getType(blockposition1).getBlock().a((IBlockAccess) world, blockposition1);

                flag |= !world.isEmpty(blockposition2) && !world.getType(blockposition2).getBlock().a((IBlockAccess) world, blockposition2);
                if (flag) {
                    return EnumInteractionResult.FAIL;
                } else {
                    double d0 = (double) blockposition1.getX();
                    double d1 = (double) blockposition1.getY();
                    double d2 = (double) blockposition1.getZ();
                    List list = world.getEntities((Entity) null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));

                    if (!list.isEmpty()) {
                        return EnumInteractionResult.FAIL;
                    } else {
                        if (!world.isClientSide) {
                            EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(world, (double) ((float) blockposition.getX() + 0.5F), (double) (blockposition.getY() + 1), (double) ((float) blockposition.getZ() + 0.5F));

                            entityendercrystal.setShowingBottom(false);
                            world.addEntity(entityendercrystal);
                            if (world.worldProvider instanceof WorldProviderTheEnd) {
                                EnderDragonBattle enderdragonbattle = ((WorldProviderTheEnd) world.worldProvider).t();

                                enderdragonbattle.e();
                            }
                        }

                        itemstack.subtract(1);
                        return EnumInteractionResult.SUCCESS;
                    }
                }
            }
        }
    }
}
