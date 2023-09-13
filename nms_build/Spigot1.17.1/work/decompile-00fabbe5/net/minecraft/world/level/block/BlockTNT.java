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
        this.k((IBlockData) this.getBlockData().set(BlockTNT.UNSTABLE, false));
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.a(iblockdata.getBlock())) {
            if (world.isBlockIndirectlyPowered(blockposition)) {
                a(world, blockposition);
                world.a(blockposition, false);
            }

        }
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (world.isBlockIndirectlyPowered(blockposition)) {
            a(world, blockposition);
            world.a(blockposition, false);
        }

    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide() && !entityhuman.isCreative() && (Boolean) iblockdata.get(BlockTNT.UNSTABLE)) {
            a(world, blockposition);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        if (!world.isClientSide) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, explosion.getSource());
            int i = entitytntprimed.getFuseTicks();

            entitytntprimed.setFuseTicks((short) (world.random.nextInt(i / 4) + i / 8));
            world.addEntity(entitytntprimed);
        }
    }

    public static void a(World world, BlockPosition blockposition) {
        a(world, blockposition, (EntityLiving) null);
    }

    private static void a(World world, BlockPosition blockposition, @Nullable EntityLiving entityliving) {
        if (!world.isClientSide) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, entityliving);

            world.addEntity(entitytntprimed);
            world.playSound((EntityHuman) null, entitytntprimed.locX(), entitytntprimed.locY(), entitytntprimed.locZ(), SoundEffects.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.a((Entity) entityliving, GameEvent.PRIME_FUSE, blockposition);
        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.a(Items.FLINT_AND_STEEL) && !itemstack.a(Items.FIRE_CHARGE)) {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        } else {
            a(world, blockposition, (EntityLiving) entityhuman);
            world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
            Item item = itemstack.getItem();

            if (!entityhuman.isCreative()) {
                if (itemstack.a(Items.FLINT_AND_STEEL)) {
                    itemstack.damage(1, entityhuman, (entityhuman1) -> {
                        entityhuman1.broadcastItemBreak(enumhand);
                    });
                } else {
                    itemstack.subtract(1);
                }
            }

            entityhuman.b(StatisticList.ITEM_USED.b(item));
            return EnumInteractionResult.a(world.isClientSide);
        }
    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        if (!world.isClientSide) {
            BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
            Entity entity = iprojectile.getShooter();

            if (iprojectile.isBurning() && iprojectile.a(world, blockposition)) {
                a(world, blockposition, entity instanceof EntityLiving ? (EntityLiving) entity : null);
                world.a(blockposition, false);
            }
        }

    }

    @Override
    public boolean a(Explosion explosion) {
        return false;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTNT.UNSTABLE);
    }
}
