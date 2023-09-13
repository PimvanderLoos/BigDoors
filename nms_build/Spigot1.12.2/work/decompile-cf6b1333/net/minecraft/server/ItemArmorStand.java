package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class ItemArmorStand extends Item {

    public ItemArmorStand() {
        this.b(CreativeModeTab.c);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (enumdirection == EnumDirection.DOWN) {
            return EnumInteractionResult.FAIL;
        } else {
            boolean flag = world.getType(blockposition).getBlock().a((IBlockAccess) world, blockposition);
            BlockPosition blockposition1 = flag ? blockposition : blockposition.shift(enumdirection);
            ItemStack itemstack = entityhuman.b(enumhand);

            if (!entityhuman.a(blockposition1, enumdirection, itemstack)) {
                return EnumInteractionResult.FAIL;
            } else {
                BlockPosition blockposition2 = blockposition1.up();
                boolean flag1 = !world.isEmpty(blockposition1) && !world.getType(blockposition1).getBlock().a((IBlockAccess) world, blockposition1);

                flag1 |= !world.isEmpty(blockposition2) && !world.getType(blockposition2).getBlock().a((IBlockAccess) world, blockposition2);
                if (flag1) {
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
                            world.setAir(blockposition1);
                            world.setAir(blockposition2);
                            EntityArmorStand entityarmorstand = new EntityArmorStand(world, d0 + 0.5D, d1, d2 + 0.5D);
                            float f3 = (float) MathHelper.d((MathHelper.g(entityhuman.yaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;

                            entityarmorstand.setPositionRotation(d0 + 0.5D, d1, d2 + 0.5D, f3, 0.0F);
                            this.a(entityarmorstand, world.random);
                            ItemMonsterEgg.a(world, entityhuman, itemstack, (Entity) entityarmorstand);
                            world.addEntity(entityarmorstand);
                            world.a((EntityHuman) null, entityarmorstand.locX, entityarmorstand.locY, entityarmorstand.locZ, SoundEffects.m, SoundCategory.BLOCKS, 0.75F, 0.8F);
                        }

                        itemstack.subtract(1);
                        return EnumInteractionResult.SUCCESS;
                    }
                }
            }
        }
    }

    private void a(EntityArmorStand entityarmorstand, Random random) {
        Vector3f vector3f = entityarmorstand.u();
        float f = random.nextFloat() * 5.0F;
        float f1 = random.nextFloat() * 20.0F - 10.0F;
        Vector3f vector3f1 = new Vector3f(vector3f.getX() + f, vector3f.getY() + f1, vector3f.getZ());

        entityarmorstand.setHeadPose(vector3f1);
        vector3f = entityarmorstand.w();
        f = random.nextFloat() * 10.0F - 5.0F;
        vector3f1 = new Vector3f(vector3f.getX(), vector3f.getY() + f, vector3f.getZ());
        entityarmorstand.setBodyPose(vector3f1);
    }
}
