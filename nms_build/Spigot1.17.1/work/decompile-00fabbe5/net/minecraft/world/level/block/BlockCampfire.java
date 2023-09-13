package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.item.crafting.RecipeCampfire;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityCampfire;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockCampfire extends BlockTileEntity implements IBlockWaterlogged {

    protected static final VoxelShape SHAPE = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
    public static final BlockStateBoolean LIT = BlockProperties.LIT;
    public static final BlockStateBoolean SIGNAL_FIRE = BlockProperties.SIGNAL_FIRE;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final BlockStateDirection FACING = BlockProperties.HORIZONTAL_FACING;
    private static final VoxelShape VIRTUAL_FENCE_POST = Block.a(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private static final int SMOKE_DISTANCE = 5;
    private final boolean spawnParticles;
    private final int fireDamage;

    public BlockCampfire(boolean flag, int i, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.spawnParticles = flag;
        this.fireDamage = i;
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockCampfire.LIT, true)).set(BlockCampfire.SIGNAL_FIRE, false)).set(BlockCampfire.WATERLOGGED, false)).set(BlockCampfire.FACING, EnumDirection.NORTH));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityCampfire) {
            TileEntityCampfire tileentitycampfire = (TileEntityCampfire) tileentity;
            ItemStack itemstack = entityhuman.b(enumhand);
            Optional<RecipeCampfire> optional = tileentitycampfire.a(itemstack);

            if (optional.isPresent()) {
                if (!world.isClientSide && tileentitycampfire.a(entityhuman.getAbilities().instabuild ? itemstack.cloneItemStack() : itemstack, ((RecipeCampfire) optional.get()).getCookingTime())) {
                    entityhuman.a(StatisticList.INTERACT_WITH_CAMPFIRE);
                    return EnumInteractionResult.SUCCESS;
                }

                return EnumInteractionResult.CONSUME;
            }
        }

        return EnumInteractionResult.PASS;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isFireProof() && (Boolean) iblockdata.get(BlockCampfire.LIT) && entity instanceof EntityLiving && !EnchantmentManager.i((EntityLiving) entity)) {
            entity.damageEntity(DamageSource.IN_FIRE, (float) this.fireDamage);
        }

        super.a(iblockdata, world, blockposition, entity);
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityCampfire) {
                InventoryUtils.a(world, blockposition, ((TileEntityCampfire) tileentity).getItems());
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        boolean flag = world.getFluid(blockposition).getType() == FluidTypes.WATER;

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockCampfire.WATERLOGGED, flag)).set(BlockCampfire.SIGNAL_FIRE, this.n(world.getType(blockposition.down())))).set(BlockCampfire.LIT, !flag)).set(BlockCampfire.FACING, blockactioncontext.g());
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockCampfire.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection == EnumDirection.DOWN ? (IBlockData) iblockdata.set(BlockCampfire.SIGNAL_FIRE, this.n(iblockdata1)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private boolean n(IBlockData iblockdata) {
        return iblockdata.a(Blocks.HAY_BLOCK);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockCampfire.SHAPE;
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockCampfire.LIT)) {
            if (random.nextInt(10) == 0) {
                world.a((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            }

            if (this.spawnParticles && random.nextInt(5) == 0) {
                for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                    world.addParticle(Particles.LAVA, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, (double) (random.nextFloat() / 2.0F), 5.0E-5D, (double) (random.nextFloat() / 2.0F));
                }
            }

        }
    }

    public static void a(@Nullable Entity entity, GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if (generatoraccess.isClientSide()) {
            for (int i = 0; i < 20; ++i) {
                a((World) generatoraccess, blockposition, (Boolean) iblockdata.get(BlockCampfire.SIGNAL_FIRE), true);
            }
        }

        TileEntity tileentity = generatoraccess.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityCampfire) {
            ((TileEntityCampfire) tileentity).f();
        }

        generatoraccess.a(entity, GameEvent.BLOCK_CHANGE, blockposition);
    }

    @Override
    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockProperties.WATERLOGGED) && fluid.getType() == FluidTypes.WATER) {
            boolean flag = (Boolean) iblockdata.get(BlockCampfire.LIT);

            if (flag) {
                if (!generatoraccess.isClientSide()) {
                    generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                a((Entity) null, generatoraccess, blockposition, iblockdata);
            }

            generatoraccess.setTypeAndData(blockposition, (IBlockData) ((IBlockData) iblockdata.set(BlockCampfire.WATERLOGGED, true)).set(BlockCampfire.LIT, false), 3);
            generatoraccess.getFluidTickList().a(blockposition, fluid.getType(), fluid.getType().a((IWorldReader) generatoraccess));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();

        if (!world.isClientSide && iprojectile.isBurning() && iprojectile.a(world, blockposition) && !(Boolean) iblockdata.get(BlockCampfire.LIT) && !(Boolean) iblockdata.get(BlockCampfire.WATERLOGGED)) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.LIT, true), 11);
        }

    }

    public static void a(World world, BlockPosition blockposition, boolean flag, boolean flag1) {
        Random random = world.getRandom();
        ParticleType particletype = flag ? Particles.CAMPFIRE_SIGNAL_SMOKE : Particles.CAMPFIRE_COSY_SMOKE;

        world.b(particletype, true, (double) blockposition.getX() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1), (double) blockposition.getY() + random.nextDouble() + random.nextDouble(), (double) blockposition.getZ() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
        if (flag1) {
            world.addParticle(Particles.SMOKE, (double) blockposition.getX() + 0.5D + random.nextDouble() / 4.0D * (double) (random.nextBoolean() ? 1 : -1), (double) blockposition.getY() + 0.4D, (double) blockposition.getZ() + 0.5D + random.nextDouble() / 4.0D * (double) (random.nextBoolean() ? 1 : -1), 0.0D, 0.005D, 0.0D);
        }

    }

    public static boolean a(World world, BlockPosition blockposition) {
        for (int i = 1; i <= 5; ++i) {
            BlockPosition blockposition1 = blockposition.down(i);
            IBlockData iblockdata = world.getType(blockposition1);

            if (g(iblockdata)) {
                return true;
            }

            boolean flag = VoxelShapes.c(BlockCampfire.VIRTUAL_FENCE_POST, iblockdata.b((IBlockAccess) world, blockposition, VoxelShapeCollision.a()), OperatorBoolean.AND);

            if (flag) {
                IBlockData iblockdata1 = world.getType(blockposition1.down());

                return g(iblockdata1);
            }
        }

        return false;
    }

    public static boolean g(IBlockData iblockdata) {
        return iblockdata.b(BlockCampfire.LIT) && iblockdata.a((Tag) TagsBlock.CAMPFIRES) && (Boolean) iblockdata.get(BlockCampfire.LIT);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockCampfire.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockCampfire.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockCampfire.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockCampfire.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCampfire.LIT, BlockCampfire.SIGNAL_FIRE, BlockCampfire.WATERLOGGED, BlockCampfire.FACING);
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityCampfire(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? ((Boolean) iblockdata.get(BlockCampfire.LIT) ? a(tileentitytypes, TileEntityTypes.CAMPFIRE, TileEntityCampfire::c) : null) : ((Boolean) iblockdata.get(BlockCampfire.LIT) ? a(tileentitytypes, TileEntityTypes.CAMPFIRE, TileEntityCampfire::a) : a(tileentitytypes, TileEntityTypes.CAMPFIRE, TileEntityCampfire::b));
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    public static boolean h(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.CAMPFIRES, (blockbase_blockdata) -> {
            return blockbase_blockdata.b(BlockCampfire.WATERLOGGED) && blockbase_blockdata.b(BlockCampfire.LIT);
        }) && !(Boolean) iblockdata.get(BlockCampfire.WATERLOGGED) && !(Boolean) iblockdata.get(BlockCampfire.LIT);
    }
}
