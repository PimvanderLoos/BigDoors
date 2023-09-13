package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.Statistic;
import net.minecraft.stats.StatisticList;
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
import net.minecraft.world.level.IWorldReader;
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
    protected static final VoxelShape NORTH_AABB = Block.a(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape SOUTH_AABB = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.a(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape EAST_AABB = Block.a(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
    protected static final VoxelShape AABB = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final DoubleBlockFinder.Combiner<TileEntityChest, Optional<IInventory>> CHEST_COMBINER = new DoubleBlockFinder.Combiner<TileEntityChest, Optional<IInventory>>() {
        public Optional<IInventory> a(TileEntityChest tileentitychest, TileEntityChest tileentitychest1) {
            return Optional.of(new InventoryLargeChest(tileentitychest, tileentitychest1));
        }

        public Optional<IInventory> a(TileEntityChest tileentitychest) {
            return Optional.of(tileentitychest);
        }

        @Override
        public Optional<IInventory> b() {
            return Optional.empty();
        }
    };
    private static final DoubleBlockFinder.Combiner<TileEntityChest, Optional<ITileInventory>> MENU_PROVIDER_COMBINER = new DoubleBlockFinder.Combiner<TileEntityChest, Optional<ITileInventory>>() {
        public Optional<ITileInventory> a(final TileEntityChest tileentitychest, final TileEntityChest tileentitychest1) {
            final InventoryLargeChest inventorylargechest = new InventoryLargeChest(tileentitychest, tileentitychest1);

            return Optional.of(new ITileInventory() {
                @Nullable
                @Override
                public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
                    if (tileentitychest.d(entityhuman) && tileentitychest1.d(entityhuman)) {
                        tileentitychest.e(playerinventory.player);
                        tileentitychest1.e(playerinventory.player);
                        return ContainerChest.b(i, playerinventory, (IInventory) inventorylargechest);
                    } else {
                        return null;
                    }
                }

                @Override
                public IChatBaseComponent getScoreboardDisplayName() {
                    return (IChatBaseComponent) (tileentitychest.hasCustomName() ? tileentitychest.getScoreboardDisplayName() : (tileentitychest1.hasCustomName() ? tileentitychest1.getScoreboardDisplayName() : new ChatMessage("container.chestDouble")));
                }
            });
        }

        public Optional<ITileInventory> a(TileEntityChest tileentitychest) {
            return Optional.of(tileentitychest);
        }

        @Override
        public Optional<ITileInventory> b() {
            return Optional.empty();
        }
    };

    protected BlockChest(BlockBase.Info blockbase_info, Supplier<TileEntityTypes<? extends TileEntityChest>> supplier) {
        super(blockbase_info, supplier);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockChest.FACING, EnumDirection.NORTH)).set(BlockChest.TYPE, BlockPropertyChestType.SINGLE)).set(BlockChest.WATERLOGGED, false));
    }

    public static DoubleBlockFinder.BlockType g(IBlockData iblockdata) {
        BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata.get(BlockChest.TYPE);

        return blockpropertychesttype == BlockPropertyChestType.SINGLE ? DoubleBlockFinder.BlockType.SINGLE : (blockpropertychesttype == BlockPropertyChestType.RIGHT ? DoubleBlockFinder.BlockType.FIRST : DoubleBlockFinder.BlockType.SECOND);
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockChest.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        if (iblockdata1.a((Block) this) && enumdirection.n().d()) {
            BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata1.get(BlockChest.TYPE);

            if (iblockdata.get(BlockChest.TYPE) == BlockPropertyChestType.SINGLE && blockpropertychesttype != BlockPropertyChestType.SINGLE && iblockdata.get(BlockChest.FACING) == iblockdata1.get(BlockChest.FACING) && h(iblockdata1) == enumdirection.opposite()) {
                return (IBlockData) iblockdata.set(BlockChest.TYPE, blockpropertychesttype.a());
            }
        } else if (h(iblockdata) == enumdirection) {
            return (IBlockData) iblockdata.set(BlockChest.TYPE, BlockPropertyChestType.SINGLE);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        if (iblockdata.get(BlockChest.TYPE) == BlockPropertyChestType.SINGLE) {
            return BlockChest.AABB;
        } else {
            switch (h(iblockdata)) {
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

    public static EnumDirection h(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockChest.FACING);

        return iblockdata.get(BlockChest.TYPE) == BlockPropertyChestType.LEFT ? enumdirection.g() : enumdirection.h();
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        BlockPropertyChestType blockpropertychesttype = BlockPropertyChestType.SINGLE;
        EnumDirection enumdirection = blockactioncontext.g().opposite();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        boolean flag = blockactioncontext.isSneaking();
        EnumDirection enumdirection1 = blockactioncontext.getClickedFace();

        if (enumdirection1.n().d() && flag) {
            EnumDirection enumdirection2 = this.a(blockactioncontext, enumdirection1.opposite());

            if (enumdirection2 != null && enumdirection2.n() != enumdirection1.n()) {
                enumdirection = enumdirection2;
                blockpropertychesttype = enumdirection2.h() == enumdirection1.opposite() ? BlockPropertyChestType.RIGHT : BlockPropertyChestType.LEFT;
            }
        }

        if (blockpropertychesttype == BlockPropertyChestType.SINGLE && !flag) {
            if (enumdirection == this.a(blockactioncontext, enumdirection.g())) {
                blockpropertychesttype = BlockPropertyChestType.LEFT;
            } else if (enumdirection == this.a(blockactioncontext, enumdirection.h())) {
                blockpropertychesttype = BlockPropertyChestType.RIGHT;
            }
        }

        return (IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockChest.FACING, enumdirection)).set(BlockChest.TYPE, blockpropertychesttype)).set(BlockChest.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockChest.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Nullable
    private EnumDirection a(BlockActionContext blockactioncontext, EnumDirection enumdirection) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().shift(enumdirection));

        return iblockdata.a((Block) this) && iblockdata.get(BlockChest.TYPE) == BlockPropertyChestType.SINGLE ? (EnumDirection) iblockdata.get(BlockChest.FACING) : null;
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof IInventory) {
                InventoryUtils.dropInventory(world, blockposition, (IInventory) tileentity);
                world.updateAdjacentComparators(blockposition, this);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            ITileInventory itileinventory = this.getInventory(iblockdata, world, blockposition);

            if (itileinventory != null) {
                entityhuman.openContainer(itileinventory);
                entityhuman.b(this.d());
                PiglinAI.a(entityhuman, true);
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    protected Statistic<MinecraftKey> d() {
        return StatisticList.CUSTOM.b(StatisticList.OPEN_CHEST);
    }

    public TileEntityTypes<? extends TileEntityChest> e() {
        return (TileEntityTypes) this.blockEntityType.get();
    }

    @Nullable
    public static IInventory getInventory(BlockChest blockchest, IBlockData iblockdata, World world, BlockPosition blockposition, boolean flag) {
        return (IInventory) ((Optional) blockchest.a(iblockdata, world, blockposition, flag).apply(BlockChest.CHEST_COMBINER)).orElse((Object) null);
    }

    @Override
    public DoubleBlockFinder.Result<? extends TileEntityChest> a(IBlockData iblockdata, World world, BlockPosition blockposition, boolean flag) {
        BiPredicate bipredicate;

        if (flag) {
            bipredicate = (generatoraccess, blockposition1) -> {
                return false;
            };
        } else {
            bipredicate = BlockChest::a;
        }

        return DoubleBlockFinder.a((TileEntityTypes) this.blockEntityType.get(), BlockChest::g, BlockChest::h, BlockChest.FACING, iblockdata, world, blockposition, bipredicate);
    }

    @Nullable
    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (ITileInventory) ((Optional) this.a(iblockdata, world, blockposition, false).apply(BlockChest.MENU_PROVIDER_COMBINER)).orElse((Object) null);
    }

    public static DoubleBlockFinder.Combiner<TileEntityChest, Float2FloatFunction> a(final LidBlockEntity lidblockentity) {
        return new DoubleBlockFinder.Combiner<TileEntityChest, Float2FloatFunction>() {
            public Float2FloatFunction a(TileEntityChest tileentitychest, TileEntityChest tileentitychest1) {
                return (f) -> {
                    return Math.max(tileentitychest.a(f), tileentitychest1.a(f));
                };
            }

            public Float2FloatFunction a(TileEntityChest tileentitychest) {
                Objects.requireNonNull(tileentitychest);
                return tileentitychest::a;
            }

            @Override
            public Float2FloatFunction b() {
                LidBlockEntity lidblockentity1 = lidblockentity;

                Objects.requireNonNull(lidblockentity);
                return lidblockentity1::a;
            }
        };
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityChest(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? a(tileentitytypes, this.e(), TileEntityChest::a) : null;
    }

    public static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return a((IBlockAccess) generatoraccess, blockposition) || b(generatoraccess, blockposition);
    }

    private static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();

        return iblockaccess.getType(blockposition1).isOccluding(iblockaccess, blockposition1);
    }

    private static boolean b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        List<EntityCat> list = generatoraccess.a(EntityCat.class, new AxisAlignedBB((double) blockposition.getX(), (double) (blockposition.getY() + 1), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 2), (double) (blockposition.getZ() + 1)));

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityCat entitycat = (EntityCat) iterator.next();

                if (entitycat.isSitting()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.b(getInventory(this, iblockdata, world, blockposition, false));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockChest.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockChest.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockChest.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockChest.FACING, BlockChest.TYPE, BlockChest.WATERLOGGED);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        TileEntity tileentity = worldserver.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityChest) {
            ((TileEntityChest) tileentity).h();
        }

    }
}
