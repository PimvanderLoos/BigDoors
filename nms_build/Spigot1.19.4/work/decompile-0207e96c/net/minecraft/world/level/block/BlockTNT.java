package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockTNT extends Block {

    public static final BlockStateBoolean UNSTABLE = BlockProperties.UNSTABLE;

    public BlockTNT(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) this.defaultBlockState().setValue(BlockTNT.UNSTABLE, false));
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock())) {
            if (world.hasNeighborSignal(blockposition)) {
                explode(world, blockposition);
                world.removeBlock(blockposition, false);
            }

        }
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (world.hasNeighborSignal(blockposition)) {
            explode(world, blockposition);
            world.removeBlock(blockposition, false);
        }

    }

    @Override
    public void playerWillDestroy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide() && !entityhuman.isCreative() && (Boolean) iblockdata.getValue(BlockTNT.UNSTABLE)) {
            explode(world, blockposition);
        }

        super.playerWillDestroy(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        if (!world.isClientSide) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, explosion.getIndirectSourceEntity());
            int i = entitytntprimed.getFuse();

            entitytntprimed.setFuse((short) (world.random.nextInt(i / 4) + i / 8));
            world.addFreshEntity(entitytntprimed);
        }
    }

    public static void explode(World world, BlockPosition blockposition) {
        explode(world, blockposition, (EntityLiving) null);
    }

    private static void explode(World world, BlockPosition blockposition, @Nullable EntityLiving entityliving) {
        if (!world.isClientSide) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, entityliving);

            world.addFreshEntity(entitytntprimed);
            world.playSound((EntityHuman) null, entitytntprimed.getX(), entitytntprimed.getY(), entitytntprimed.getZ(), SoundEffects.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.gameEvent((Entity) entityliving, GameEvent.PRIME_FUSE, blockposition);
        }
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!itemstack.is(Items.FLINT_AND_STEEL) && !itemstack.is(Items.FIRE_CHARGE)) {
            return super.use(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        } else {
            explode(world, blockposition, entityhuman);
            world.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 11);
            Item item = itemstack.getItem();

            if (!entityhuman.isCreative()) {
                if (itemstack.is(Items.FLINT_AND_STEEL)) {
                    itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                        entityhuman1.broadcastBreakEvent(enumhand);
                    });
                } else {
                    itemstack.shrink(1);
                }
            }

            entityhuman.awardStat(StatisticList.ITEM_USED.get(item));
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        }
    }

    @Override
    public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        if (!world.isClientSide) {
            BlockPosition blockposition = movingobjectpositionblock.getBlockPos();
            Entity entity = iprojectile.getOwner();

            if (iprojectile.isOnFire() && iprojectile.mayInteract(world, blockposition)) {
                explode(world, blockposition, entity instanceof EntityLiving ? (EntityLiving) entity : null);
                world.removeBlock(blockposition, false);
            }
        }

    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockTNT.UNSTABLE);
    }
}
