package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockRedstoneOre extends Block {

    public static final BlockStateBoolean LIT = BlockRedstoneTorch.LIT;

    public BlockRedstoneOre(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) this.getBlockData().set(BlockRedstoneOre.LIT, false));
    }

    @Override
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        interact(iblockdata, world, blockposition);
        super.attack(iblockdata, world, blockposition, entityhuman);
    }

    @Override
    public void stepOn(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        interact(iblockdata, world, blockposition);
        super.stepOn(world, blockposition, iblockdata, entity);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            playEffect(world, blockposition);
        } else {
            interact(iblockdata, world, blockposition);
        }

        ItemStack itemstack = entityhuman.b(enumhand);

        return itemstack.getItem() instanceof ItemBlock && (new BlockActionContext(entityhuman, enumhand, itemstack, movingobjectpositionblock)).b() ? EnumInteractionResult.PASS : EnumInteractionResult.SUCCESS;
    }

    private static void interact(IBlockData iblockdata, World world, BlockPosition blockposition) {
        playEffect(world, blockposition);
        if (!(Boolean) iblockdata.get(BlockRedstoneOre.LIT)) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneOre.LIT, true), 3);
        }

    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockRedstoneOre.LIT);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockRedstoneOre.LIT)) {
            worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneOre.LIT, false), 3);
        }

    }

    @Override
    public void dropNaturally(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
        super.dropNaturally(iblockdata, worldserver, blockposition, itemstack);
        if (EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            int i = 1 + worldserver.random.nextInt(5);

            this.dropExperience(worldserver, blockposition, i);
        }

    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockRedstoneOre.LIT)) {
            playEffect(world, blockposition);
        }

    }

    private static void playEffect(World world, BlockPosition blockposition) {
        double d0 = 0.5625D;
        Random random = world.random;
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            if (!world.getType(blockposition1).i(world, blockposition1)) {
                EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.n();
                double d1 = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? 0.5D + 0.5625D * (double) enumdirection.getAdjacentX() : (double) random.nextFloat();
                double d2 = enumdirection_enumaxis == EnumDirection.EnumAxis.Y ? 0.5D + 0.5625D * (double) enumdirection.getAdjacentY() : (double) random.nextFloat();
                double d3 = enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? 0.5D + 0.5625D * (double) enumdirection.getAdjacentZ() : (double) random.nextFloat();

                world.addParticle(ParticleParamRedstone.REDSTONE, (double) blockposition.getX() + d1, (double) blockposition.getY() + d2, (double) blockposition.getZ() + d3, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneOre.LIT);
    }
}
