package net.minecraft.server;

import java.util.Iterator;

public class EnchantmentFrostWalker extends Enchantment {

    public EnchantmentFrostWalker(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_FEET, aenumitemslot);
        this.c("frostWalker");
    }

    public int a(int i) {
        return i * 10;
    }

    public int b(int i) {
        return this.a(i) + 15;
    }

    public boolean isTreasure() {
        return true;
    }

    public int getMaxLevel() {
        return 2;
    }

    public static void a(EntityLiving entityliving, World world, BlockPosition blockposition, int i) {
        if (entityliving.onGround) {
            float f = (float) Math.min(16, 2 + i);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(0, 0, 0);
            Iterator iterator = BlockPosition.b(blockposition.a((double) (-f), -1.0D, (double) (-f)), blockposition.a((double) f, -1.0D, (double) f)).iterator();

            while (iterator.hasNext()) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = (BlockPosition.MutableBlockPosition) iterator.next();

                if (blockposition_mutableblockposition1.g(entityliving.locX, entityliving.locY, entityliving.locZ) <= (double) (f * f)) {
                    blockposition_mutableblockposition.c(blockposition_mutableblockposition1.getX(), blockposition_mutableblockposition1.getY() + 1, blockposition_mutableblockposition1.getZ());
                    IBlockData iblockdata = world.getType(blockposition_mutableblockposition);

                    if (iblockdata.getMaterial() == Material.AIR) {
                        IBlockData iblockdata1 = world.getType(blockposition_mutableblockposition1);

                        if (iblockdata1.getMaterial() == Material.WATER && ((Integer) iblockdata1.get(BlockFluids.LEVEL)).intValue() == 0 && world.a(Blocks.FROSTED_ICE, blockposition_mutableblockposition1, false, EnumDirection.DOWN, (Entity) null)) {
                            world.setTypeUpdate(blockposition_mutableblockposition1, Blocks.FROSTED_ICE.getBlockData());
                            world.a(blockposition_mutableblockposition1.h(), Blocks.FROSTED_ICE, MathHelper.nextInt(entityliving.getRandom(), 60, 120));
                        }
                    }
                }
            }

        }
    }

    public boolean a(Enchantment enchantment) {
        return super.a(enchantment) && enchantment != Enchantments.DEPTH_STRIDER;
    }
}
