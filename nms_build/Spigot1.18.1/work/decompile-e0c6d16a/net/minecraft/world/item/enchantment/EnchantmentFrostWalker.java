package net.minecraft.world.item.enchantment;

import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class EnchantmentFrostWalker extends Enchantment {

    public EnchantmentFrostWalker(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_FEET, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return i * 10;
    }

    @Override
    public int getMaxCost(int i) {
        return this.getMinCost(i) + 15;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    public static void onEntityMoved(EntityLiving entityliving, World world, BlockPosition blockposition, int i) {
        if (entityliving.isOnGround()) {
            IBlockData iblockdata = Blocks.FROSTED_ICE.defaultBlockState();
            float f = (float) Math.min(16, 2 + i);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            Iterator iterator = BlockPosition.betweenClosed(blockposition.offset((double) (-f), -1.0D, (double) (-f)), blockposition.offset((double) f, -1.0D, (double) f)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if (blockposition1.closerThan((IPosition) entityliving.position(), (double) f)) {
                    blockposition_mutableblockposition.set(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ());
                    IBlockData iblockdata1 = world.getBlockState(blockposition_mutableblockposition);

                    if (iblockdata1.isAir()) {
                        IBlockData iblockdata2 = world.getBlockState(blockposition1);

                        if (iblockdata2.getMaterial() == Material.WATER && (Integer) iblockdata2.getValue(BlockFluids.LEVEL) == 0 && iblockdata.canSurvive(world, blockposition1) && world.isUnobstructed(iblockdata, blockposition1, VoxelShapeCollision.empty())) {
                            world.setBlockAndUpdate(blockposition1, iblockdata);
                            world.scheduleTick(blockposition1, Blocks.FROSTED_ICE, MathHelper.nextInt(entityliving.getRandom(), 60, 120));
                        }
                    }
                }
            }

        }
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != Enchantments.DEPTH_STRIDER;
    }
}
