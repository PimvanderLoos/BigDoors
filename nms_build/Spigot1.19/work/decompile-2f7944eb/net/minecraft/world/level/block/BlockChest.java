package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.Statistic;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.InventoryLargeChest;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.monster.piglin.PiglinAI;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyChestType;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockChest extends BlockChestAbstract<TileEntityChest> implements IBlockWaterlogged {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateEnum<BlockPropertyChestType> TYPE = BlockProperties.CHEST_TYPE;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final int EVENT_SET_OPEN_COUNT = 1;
    protected static final int AABB_OFFSET = 1;
    protected static final int AABB_HEIGHT = 14;
    protected static final VoxelShape NORTH_AABB = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape EAST_AABB = Block.box(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
    protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final DoubleBlockFinder.Combiner<TileEntityChest, Optional<IInventory>> CHEST_COMBINER = new DoubleBlockFinder.Combiner<TileEntityChest, Optional<IInventory>>() {
        public Optional<IInventory> acceptDouble(TileEntityChest tileentitychest, TileEntityChest tileentitychest1) {
            return Optional.of(new InventoryLargeChest(tileentitychest, tileentitychest1));
        }

        public Optional<IInventory> acceptSingle(TileEntityChest tileentitychest) {
            return Optional.of(tileentitychest);
        }

        @Override
        public Optional<IInventory> acceptNone() {
            return Optional.empty();
        }
    };
    private static final DoubleBlockFinder.Combiner<TileEntityChest, Optional<ITileInventory>> MENU_PROVIDER_COMBINER = new DoubleBlockFinder.Combiner<TileEntityChest, Optional<ITileInventory>>() {
        public Optional<ITileInventory> acceptDouble(final TileEntityChest tileentitychest, final TileEntityChest tileentitychest1) {
            final InventoryLargeChest inventorylargechest = new InventoryLargeChest(tileentitychest, tileentitychest1);

            return Optional.of(new ITileInventory() {
                @Nullable
                @Override
                public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
                    if (tileentitychest.canOpen(entityhuman) && tileentitychest1.canOpen(entityhuman)) {
                        tileentitychest.unpackLootTable(playerinventory.player);
                        tileentitychest1.unpackLootTable(playerinventory.player);
                        return ContainerChest.sixRows(i, playerinventory, inventorylargechest);
                    } else {
                        return null;
                    }
                }

                @Override
                public IChatBaseComponent getDisplayName() {
                    return (IChatBaseComponent) (tileentitychest.hasCustomName() ? tileentitychest.getDisplayName() : (tileentitychest1.hasCustomName() ? tileentitychest1.getDisplayName() : IChatBaseComponent.translatable("container.chestDouble")));
                }
            });
        }

        public Optional<ITileInventory> acceptSingle(TileEntityChest tileentitychest) {
            return Optional.of(tileentitychest);
        }

        @Override
        public Optional<ITileInventory> acceptNone() {
            return Optional.empty();
        }
    };

    protected BlockChest(BlockBase.Info blockbase_info, Supplier<TileEntityTypes<? extends TileEntityChest>> supplier) {
        super(blockbase_info, supplier);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockChest.FACING, EnumDirection.NORTH)).setValue(BlockChest.TYPE, BlockPropertyChestType.SINGLE)).setValue(BlockChest.WATERLOGGED, false));
    }

    public static DoubleBlockFinder.BlockType getBlockType(IBlockData iblockdata) {
        BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata.getValue(BlockChest.TYPE);

        return blockpropertychesttype == BlockPropertyChestType.SINGLE ? DoubleBlockFinder.BlockType.SINGLE : (blockpropertychesttype == BlockPropertyChestType.RIGHT ? DoubleBlockFinder.BlockType.FIRST : DoubleBlockFinder.BlockType.SECOND);
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(BlockChest.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        if (iblockdata1.is((Block) this) && enumdirection.getAxis().isHorizontal()) {
            BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata1.getValue(BlockChest.TYPE);

            if (iblockdata.getValue(BlockChest.TYPE) == BlockPropertyChestType.SINGLE && blockpropertychesttype != BlockPropertyChestType.SINGLE && iblockdata.getValue(BlockChest.FACING) == iblockdata1.getValue(BlockChest.FACING) && getConnectedDirection(iblockdata1) == enumdirection.getOpposite()) {
                return (IBlockData) iblockdata.setValue(BlockChest.TYPE, blockpropertychesttype.getOpposite());
            }
        } else if (getConnectedDirection(iblockdata) == enumdirection) {
            return (IBlockData) iblockdata.setValue(BlockChest.TYPE, BlockPropertyChestType.SINGLE);
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        if (iblockdata.getValue(BlockChest.TYPE) == BlockPropertyChestType.SINGLE) {
            return BlockChest.AABB;
        } else {
            switch (getConnectedDirection(iblockdata)) {
                case NORTH:
                default:
                    return BlockChest.NORTH_AABB;
                case SOUTH:
                    return BlockChest.SOUTH_AABB;
                case WEST:
                    return BlockChest.WEST_AABB;
                case EAST:
                    return BlockChest.EAST_AABB;
            }
        }
    }

    public static EnumDirection getConnectedDirection(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockChest.FACING);

        return iblockdata.getValue(BlockChest.TYPE) == BlockPropertyChestType.LEFT ? enumdirection.getClockWise() : enumdirection.getCounterClockWise();
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        BlockPropertyChestType blockpropertychesttype = BlockPropertyChestType.SINGLE;
        EnumDirection enumdirection = blockactioncontext.getHorizontalDirection().getOpposite();
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        boolean flag = blockactioncontext.isSecondaryUseActive();
        EnumDirection enumdirection1 = blockactioncontext.getClickedFace();

        if (enumdirection1.getAxis().isHorizontal() && flag) {
            EnumDirection enumdirection2 = this.candidatePartnerFacing(blockactioncontext, enumdirection1.getOpposite());

            if (enumdirection2 != null && enumdirection2.getAxis() != enumdirection1.getAxis()) {
                enumdirection = enumdirection2;
                blockpropertychesttype = enumdirection2.getCounterClockWise() == enumdirection1.getOpposite() ? BlockPropertyChestType.RIGHT : BlockPropertyChestType.LEFT;
            }
        }

        if (blockpropertychesttype == BlockPropertyChestType.SINGLE && !flag) {
            if (enumdirection == this.candidatePartnerFacing(blockactioncontext, enumdirection.getClockWise())) {
                blockpropertychesttype = BlockPropertyChestType.LEFT;
            } else if (enumdirection == this.candidatePartnerFacing(blockactioncontext, enumdirection.getCounterClockWise())) {
                blockpropertychesttype = BlockPropertyChestType.RIGHT;
            }
        }

        return (IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockChest.FACING, enumdirection)).setValue(BlockChest.TYPE, blockpropertychesttype)).setValue(BlockChest.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockChest.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Nullable
    private EnumDirection candidatePartnerFacing(BlockActionContext blockactioncontext, EnumDirection enumdirection) {
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos().relative(enumdirection));

        return iblockdata.is((Block) this) && iblockdata.getValue(BlockChest.TYPE) == BlockPropertyChestType.SINGLE ? (EnumDirection) iblockdata.getValue(BlockChest.FACING) : null;
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasCustomHoverName()) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setCustomName(itemstack.getHoverName());
            }
        }

    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof IInventory) {
                InventoryUtils.dropContents(world, blockposition, (IInventory) tileentity);
                world.updateNeighbourForOutputSignal(blockposition, this);
            }

            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            ITileInventory itileinventory = this.getMenuProvider(iblockdata, world, blockposition);

            if (itileinventory != null) {
                entityhuman.openMenu(itileinventory);
                entityhuman.awardStat(this.getOpenChestStat());
                PiglinAI.angerNearbyPiglins(entityhuman, true);
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    protected Statistic<MinecraftKey> getOpenChestStat() {
        return StatisticList.CUSTOM.get(StatisticList.OPEN_CHEST);
    }

    public TileEntityTypes<? extends TileEntityChest> blockEntityType() {
        return (TileEntityTypes) this.blockEntityType.get();
    }

    @Nullable
    public static IInventory getContainer(BlockChest blockchest, IBlockData iblockdata, World world, BlockPosition blockposition, boolean flag) {
        return (IInventory) ((Optional) blockchest.combine(iblockdata, world, blockposition, flag).apply(BlockChest.CHEST_COMBINER)).orElse((Object) null);
    }

    @Override
    public DoubleBlockFinder.Result<? extends TileEntityChest> combine(IBlockData iblockdata, World world, BlockPosition blockposition, boolean flag) {
        BiPredicate bipredicate;

        if (flag) {
            bipredicate = (generatoraccess, blockposition1) -> {
                return false;
            };
        } else {
            bipredicate = BlockChest::isChestBlockedAt;
        }

        return DoubleBlockFinder.combineWithNeigbour((TileEntityTypes) this.blockEntityType.get(), BlockChest::getBlockType, BlockChest::getConnectedDirection, BlockChest.FACING, iblockdata, world, blockposition, bipredicate);
    }

    @Nullable
    @Override
    public ITileInventory getMenuProvider(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (ITileInventory) ((Optional) this.combine(iblockdata, world, blockposition, false).apply(BlockChest.MENU_PROVIDER_COMBINER)).orElse((Object) null);
    }

    public static DoubleBlockFinder.Combiner<TileEntityChest, Float2FloatFunction> opennessCombiner(final LidBlockEntity lidblockentity) {
        return new DoubleBlockFinder.Combiner<TileEntityChest, Float2FloatFunction>() {
            public Float2FloatFunction acceptDouble(TileEntityChest tileentitychest, TileEntityChest tileentitychest1) {
                return (f) -> {
                    return Math.max(tileentitychest.getOpenNess(f), tileentitychest1.getOpenNess(f));
                };
            }

            public Float2FloatFunction acceptSingle(TileEntityChest tileentitychest) {
                Objects.requireNonNull(tileentitychest);
                return tileentitychest::getOpenNess;
            }

            @Override
            public Float2FloatFunction acceptNone() {
                LidBlockEntity lidblockentity1 = lidblockentity;

                Objects.requireNonNull(lidblockentity);
                return lidblockentity1::getOpenNess;
            }
        };
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityChest(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? createTickerHelper(tileentitytypes, this.blockEntityType(), TileEntityChest::lidAnimateTick) : null;
    }

    public static boolean isChestBlockedAt(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return isBlockedChestByBlock(generatoraccess, blockposition) || isCatSittingOnChest(generatoraccess, blockposition);
    }

    private static boolean isBlockedChestByBlock(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.above();

        return iblockaccess.getBlockState(blockposition1).isRedstoneConductor(iblockaccess, blockposition1);
    }

    private static boolean isCatSittingOnChest(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        List<EntityCat> list = generatoraccess.getEntitiesOfClass(EntityCat.class, new AxisAlignedBB((double) blockposition.getX(), (double) (blockposition.getY() + 1), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 2), (double) (blockposition.getZ() + 1)));

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityCat entitycat = (EntityCat) iterator.next();

                if (entitycat.isInSittingPose()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.getRedstoneSignalFromContainer(getContainer(this, iblockdata, world, blockposition, false));
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockChest.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockChest.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockChest.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockChest.FACING, BlockChest.TYPE, BlockChest.WATERLOGGED);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        TileEntity tileentity = worldserver.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityChest) {
            ((TileEntityChest) tileentity).recheckOpen();
        }

    }
}
