package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.portal.BlockPortalShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockFireAbstract extends Block {

    private static final int SECONDS_ON_FIRE = 8;
    private final float fireDamage;
    protected static final float AABB_OFFSET = 1.0F;
    protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public BlockFireAbstract(BlockBase.Info blockbase_info, float f) {
        super(blockbase_info);
        this.fireDamage = f;
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return getState(blockactioncontext.getLevel(), blockactioncontext.getClickedPos());
    }

    public static IBlockData getState(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata = iblockaccess.getBlockState(blockposition1);

        return BlockSoulFire.canSurviveOnBlock(iblockdata) ? Blocks.SOUL_FIRE.defaultBlockState() : ((BlockFire) Blocks.FIRE).getStateForPlacement(iblockaccess, blockposition);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockFireAbstract.DOWN_AABB;
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        if (randomsource.nextInt(24) == 0) {
            world.playLocalSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + randomsource.nextFloat(), randomsource.nextFloat() * 0.7F + 0.3F, false);
        }

        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata1 = world.getBlockState(blockposition1);
        double d0;
        double d1;
        double d2;
        int i;

        if (!this.canBurn(iblockdata1) && !iblockdata1.isFaceSturdy(world, blockposition1, EnumDirection.UP)) {
            if (this.canBurn(world.getBlockState(blockposition.west()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) blockposition.getX() + randomsource.nextDouble() * 0.10000000149011612D;
                    d1 = (double) blockposition.getY() + randomsource.nextDouble();
                    d2 = (double) blockposition.getZ() + randomsource.nextDouble();
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.canBurn(world.getBlockState(blockposition.east()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) (blockposition.getX() + 1) - randomsource.nextDouble() * 0.10000000149011612D;
                    d1 = (double) blockposition.getY() + randomsource.nextDouble();
                    d2 = (double) blockposition.getZ() + randomsource.nextDouble();
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.canBurn(world.getBlockState(blockposition.north()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) blockposition.getX() + randomsource.nextDouble();
                    d1 = (double) blockposition.getY() + randomsource.nextDouble();
                    d2 = (double) blockposition.getZ() + randomsource.nextDouble() * 0.10000000149011612D;
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.canBurn(world.getBlockState(blockposition.south()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) blockposition.getX() + randomsource.nextDouble();
                    d1 = (double) blockposition.getY() + randomsource.nextDouble();
                    d2 = (double) (blockposition.getZ() + 1) - randomsource.nextDouble() * 0.10000000149011612D;
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.canBurn(world.getBlockState(blockposition.above()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) blockposition.getX() + randomsource.nextDouble();
                    d1 = (double) (blockposition.getY() + 1) - randomsource.nextDouble() * 0.10000000149011612D;
                    d2 = (double) blockposition.getZ() + randomsource.nextDouble();
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        } else {
            for (i = 0; i < 3; ++i) {
                d0 = (double) blockposition.getX() + randomsource.nextDouble();
                d1 = (double) blockposition.getY() + randomsource.nextDouble() * 0.5D + 0.5D;
                d2 = (double) blockposition.getZ() + randomsource.nextDouble();
                world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    protected abstract boolean canBurn(IBlockData iblockdata);

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!entity.fireImmune()) {
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
            if (entity.getRemainingFireTicks() == 0) {
                entity.setSecondsOnFire(8);
            }
        }

        entity.hurt(world.damageSources().inFire(), this.fireDamage);
        super.entityInside(iblockdata, world, blockposition, entity);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock())) {
            if (inPortalDimension(world)) {
                Optional<BlockPortalShape> optional = BlockPortalShape.findEmptyPortalShape(world, blockposition, EnumDirection.EnumAxis.X);

                if (optional.isPresent()) {
                    ((BlockPortalShape) optional.get()).createPortalBlocks();
                    return;
                }
            }

            if (!iblockdata.canSurvive(world, blockposition)) {
                world.removeBlock(blockposition, false);
            }

        }
    }

    private static boolean inPortalDimension(World world) {
        return world.dimension() == World.OVERWORLD || world.dimension() == World.NETHER;
    }

    @Override
    protected void spawnDestroyParticles(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata) {}

    @Override
    public void playerWillDestroy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide()) {
            world.levelEvent((EntityHuman) null, 1009, blockposition, 0);
        }

        super.playerWillDestroy(world, blockposition, iblockdata, entityhuman);
    }

    public static boolean canBePlacedAt(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = world.getBlockState(blockposition);

        return !iblockdata.isAir() ? false : getState(world, blockposition).canSurvive(world, blockposition) || isPortal(world, blockposition, enumdirection);
    }

    private static boolean isPortal(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        if (!inPortalDimension(world)) {
            return false;
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
            boolean flag = false;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection1 = aenumdirection[j];

                if (world.getBlockState(blockposition_mutableblockposition.set(blockposition).move(enumdirection1)).is(Blocks.OBSIDIAN)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            } else {
                EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis().isHorizontal() ? enumdirection.getCounterClockWise().getAxis() : EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomAxis(world.random);

                return BlockPortalShape.findEmptyPortalShape(world, blockposition, enumdirection_enumaxis).isPresent();
            }
        }
    }
}
