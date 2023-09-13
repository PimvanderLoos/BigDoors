package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockFire extends BlockFireAbstract {

    public static final int MAX_AGE = 15;
    public static final BlockStateInteger AGE = BlockProperties.AGE_15;
    public static final BlockStateBoolean NORTH = BlockSprawling.NORTH;
    public static final BlockStateBoolean EAST = BlockSprawling.EAST;
    public static final BlockStateBoolean SOUTH = BlockSprawling.SOUTH;
    public static final BlockStateBoolean WEST = BlockSprawling.WEST;
    public static final BlockStateBoolean UP = BlockSprawling.UP;
    private static final Map<EnumDirection, BlockStateBoolean> PROPERTY_BY_DIRECTION = (Map) BlockSprawling.PROPERTY_BY_DIRECTION.entrySet().stream().filter((entry) -> {
        return entry.getKey() != EnumDirection.DOWN;
    }).collect(SystemUtils.toMap());
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private final Map<IBlockData, VoxelShape> shapesCache;
    private static final int FLAME_INSTANT = 60;
    private static final int FLAME_EASY = 30;
    private static final int FLAME_MEDIUM = 15;
    private static final int FLAME_HARD = 5;
    private static final int BURN_INSTANT = 100;
    private static final int BURN_EASY = 60;
    private static final int BURN_MEDIUM = 20;
    private static final int BURN_HARD = 5;
    public final Object2IntMap<Block> flameOdds = new Object2IntOpenHashMap();
    private final Object2IntMap<Block> burnOdds = new Object2IntOpenHashMap();

    public BlockFire(BlockBase.Info blockbase_info) {
        super(blockbase_info, 1.0F);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockFire.AGE, 0)).setValue(BlockFire.NORTH, false)).setValue(BlockFire.EAST, false)).setValue(BlockFire.SOUTH, false)).setValue(BlockFire.WEST, false)).setValue(BlockFire.UP, false));
        this.shapesCache = ImmutableMap.copyOf((Map) this.stateDefinition.getPossibleStates().stream().filter((iblockdata) -> {
            return (Integer) iblockdata.getValue(BlockFire.AGE) == 0;
        }).collect(Collectors.toMap(Function.identity(), BlockFire::calculateShape)));
    }

    private static VoxelShape calculateShape(IBlockData iblockdata) {
        VoxelShape voxelshape = VoxelShapes.empty();

        if ((Boolean) iblockdata.getValue(BlockFire.UP)) {
            voxelshape = BlockFire.UP_AABB;
        }

        if ((Boolean) iblockdata.getValue(BlockFire.NORTH)) {
            voxelshape = VoxelShapes.or(voxelshape, BlockFire.NORTH_AABB);
        }

        if ((Boolean) iblockdata.getValue(BlockFire.SOUTH)) {
            voxelshape = VoxelShapes.or(voxelshape, BlockFire.SOUTH_AABB);
        }

        if ((Boolean) iblockdata.getValue(BlockFire.EAST)) {
            voxelshape = VoxelShapes.or(voxelshape, BlockFire.EAST_AABB);
        }

        if ((Boolean) iblockdata.getValue(BlockFire.WEST)) {
            voxelshape = VoxelShapes.or(voxelshape, BlockFire.WEST_AABB);
        }

        return voxelshape.isEmpty() ? BlockFire.DOWN_AABB : voxelshape;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return this.canSurvive(iblockdata, generatoraccess, blockposition) ? this.getStateWithAge(generatoraccess, blockposition, (Integer) iblockdata.getValue(BlockFire.AGE)) : Blocks.AIR.defaultBlockState();
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.shapesCache.get(iblockdata.setValue(BlockFire.AGE, 0));
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return this.getStateForPlacement(blockactioncontext.getLevel(), blockactioncontext.getClickedPos());
    }

    protected IBlockData getStateForPlacement(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata = iblockaccess.getBlockState(blockposition1);

        if (!this.canBurn(iblockdata) && !iblockdata.isFaceSturdy(iblockaccess, blockposition1, EnumDirection.UP)) {
            IBlockData iblockdata1 = this.defaultBlockState();
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];
                BlockStateBoolean blockstateboolean = (BlockStateBoolean) BlockFire.PROPERTY_BY_DIRECTION.get(enumdirection);

                if (blockstateboolean != null) {
                    iblockdata1 = (IBlockData) iblockdata1.setValue(blockstateboolean, this.canBurn(iblockaccess.getBlockState(blockposition.relative(enumdirection))));
                }
            }

            return iblockdata1;
        } else {
            return this.defaultBlockState();
        }
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();

        return iworldreader.getBlockState(blockposition1).isFaceSturdy(iworldreader, blockposition1, EnumDirection.UP) || this.isValidFireLocation(iworldreader, blockposition);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        worldserver.scheduleTick(blockposition, (Block) this, getFireTickDelay(worldserver.random));
        if (worldserver.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            if (!iblockdata.canSurvive(worldserver, blockposition)) {
                worldserver.removeBlock(blockposition, false);
            }

            IBlockData iblockdata1 = worldserver.getBlockState(blockposition.below());
            boolean flag = iblockdata1.is(worldserver.dimensionType().infiniburn());
            int i = (Integer) iblockdata.getValue(BlockFire.AGE);

            if (!flag && worldserver.isRaining() && this.isNearRain(worldserver, blockposition) && random.nextFloat() < 0.2F + (float) i * 0.03F) {
                worldserver.removeBlock(blockposition, false);
            } else {
                int j = Math.min(15, i + random.nextInt(3) / 2);

                if (i != j) {
                    iblockdata = (IBlockData) iblockdata.setValue(BlockFire.AGE, j);
                    worldserver.setBlock(blockposition, iblockdata, 4);
                }

                if (!flag) {
                    if (!this.isValidFireLocation(worldserver, blockposition)) {
                        BlockPosition blockposition1 = blockposition.below();

                        if (!worldserver.getBlockState(blockposition1).isFaceSturdy(worldserver, blockposition1, EnumDirection.UP) || i > 3) {
                            worldserver.removeBlock(blockposition, false);
                        }

                        return;
                    }

                    if (i == 15 && random.nextInt(4) == 0 && !this.canBurn(worldserver.getBlockState(blockposition.below()))) {
                        worldserver.removeBlock(blockposition, false);
                        return;
                    }
                }

                boolean flag1 = worldserver.isHumidAt(blockposition);
                int k = flag1 ? -50 : 0;

                this.checkBurnOut(worldserver, blockposition.east(), 300 + k, random, i);
                this.checkBurnOut(worldserver, blockposition.west(), 300 + k, random, i);
                this.checkBurnOut(worldserver, blockposition.below(), 250 + k, random, i);
                this.checkBurnOut(worldserver, blockposition.above(), 250 + k, random, i);
                this.checkBurnOut(worldserver, blockposition.north(), 300 + k, random, i);
                this.checkBurnOut(worldserver, blockposition.south(), 300 + k, random, i);
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int l = -1; l <= 1; ++l) {
                    for (int i1 = -1; i1 <= 1; ++i1) {
                        for (int j1 = -1; j1 <= 4; ++j1) {
                            if (l != 0 || j1 != 0 || i1 != 0) {
                                int k1 = 100;

                                if (j1 > 1) {
                                    k1 += (j1 - 1) * 100;
                                }

                                blockposition_mutableblockposition.setWithOffset(blockposition, l, j1, i1);
                                int l1 = this.getFireOdds(worldserver, blockposition_mutableblockposition);

                                if (l1 > 0) {
                                    int i2 = (l1 + 40 + worldserver.getDifficulty().getId() * 7) / (i + 30);

                                    if (flag1) {
                                        i2 /= 2;
                                    }

                                    if (i2 > 0 && random.nextInt(k1) <= i2 && (!worldserver.isRaining() || !this.isNearRain(worldserver, blockposition_mutableblockposition))) {
                                        int j2 = Math.min(15, i + random.nextInt(5) / 4);

                                        worldserver.setBlock(blockposition_mutableblockposition, this.getStateWithAge(worldserver, blockposition_mutableblockposition, j2), 3);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    protected boolean isNearRain(World world, BlockPosition blockposition) {
        return world.isRainingAt(blockposition) || world.isRainingAt(blockposition.west()) || world.isRainingAt(blockposition.east()) || world.isRainingAt(blockposition.north()) || world.isRainingAt(blockposition.south());
    }

    private int getBurnOdd(IBlockData iblockdata) {
        return iblockdata.hasProperty(BlockProperties.WATERLOGGED) && (Boolean) iblockdata.getValue(BlockProperties.WATERLOGGED) ? 0 : this.burnOdds.getInt(iblockdata.getBlock());
    }

    private int getFlameOdds(IBlockData iblockdata) {
        return iblockdata.hasProperty(BlockProperties.WATERLOGGED) && (Boolean) iblockdata.getValue(BlockProperties.WATERLOGGED) ? 0 : this.flameOdds.getInt(iblockdata.getBlock());
    }

    private void checkBurnOut(World world, BlockPosition blockposition, int i, Random random, int j) {
        int k = this.getBurnOdd(world.getBlockState(blockposition));

        if (random.nextInt(i) < k) {
            IBlockData iblockdata = world.getBlockState(blockposition);

            if (random.nextInt(j + 10) < 5 && !world.isRainingAt(blockposition)) {
                int l = Math.min(j + random.nextInt(5) / 4, 15);

                world.setBlock(blockposition, this.getStateWithAge(world, blockposition, l), 3);
            } else {
                world.removeBlock(blockposition, false);
            }

            Block block = iblockdata.getBlock();

            if (block instanceof BlockTNT) {
                BlockTNT blocktnt = (BlockTNT) block;

                BlockTNT.explode(world, blockposition);
            }
        }

    }

    private IBlockData getStateWithAge(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        IBlockData iblockdata = getState(generatoraccess, blockposition);

        return iblockdata.is(Blocks.FIRE) ? (IBlockData) iblockdata.setValue(BlockFire.AGE, i) : iblockdata;
    }

    private boolean isValidFireLocation(IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.canBurn(iblockaccess.getBlockState(blockposition.relative(enumdirection)))) {
                return true;
            }
        }

        return false;
    }

    private int getFireOdds(IWorldReader iworldreader, BlockPosition blockposition) {
        if (!iworldreader.isEmptyBlock(blockposition)) {
            return 0;
        } else {
            int i = 0;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];
                IBlockData iblockdata = iworldreader.getBlockState(blockposition.relative(enumdirection));

                i = Math.max(this.getFlameOdds(iblockdata), i);
            }

            return i;
        }
    }

    @Override
    protected boolean canBurn(IBlockData iblockdata) {
        return this.getFlameOdds(iblockdata) > 0;
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        super.onPlace(iblockdata, world, blockposition, iblockdata1, flag);
        world.scheduleTick(blockposition, (Block) this, getFireTickDelay(world.random));
    }

    private static int getFireTickDelay(Random random) {
        return 30 + random.nextInt(10);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockFire.AGE, BlockFire.NORTH, BlockFire.EAST, BlockFire.SOUTH, BlockFire.WEST, BlockFire.UP);
    }

    private void setFlammable(Block block, int i, int j) {
        this.flameOdds.put(block, i);
        this.burnOdds.put(block, j);
    }

    public static void bootStrap() {
        BlockFire blockfire = (BlockFire) Blocks.FIRE;

        blockfire.setFlammable(Blocks.OAK_PLANKS, 5, 20);
        blockfire.setFlammable(Blocks.SPRUCE_PLANKS, 5, 20);
        blockfire.setFlammable(Blocks.BIRCH_PLANKS, 5, 20);
        blockfire.setFlammable(Blocks.JUNGLE_PLANKS, 5, 20);
        blockfire.setFlammable(Blocks.ACACIA_PLANKS, 5, 20);
        blockfire.setFlammable(Blocks.DARK_OAK_PLANKS, 5, 20);
        blockfire.setFlammable(Blocks.OAK_SLAB, 5, 20);
        blockfire.setFlammable(Blocks.SPRUCE_SLAB, 5, 20);
        blockfire.setFlammable(Blocks.BIRCH_SLAB, 5, 20);
        blockfire.setFlammable(Blocks.JUNGLE_SLAB, 5, 20);
        blockfire.setFlammable(Blocks.ACACIA_SLAB, 5, 20);
        blockfire.setFlammable(Blocks.DARK_OAK_SLAB, 5, 20);
        blockfire.setFlammable(Blocks.OAK_FENCE_GATE, 5, 20);
        blockfire.setFlammable(Blocks.SPRUCE_FENCE_GATE, 5, 20);
        blockfire.setFlammable(Blocks.BIRCH_FENCE_GATE, 5, 20);
        blockfire.setFlammable(Blocks.JUNGLE_FENCE_GATE, 5, 20);
        blockfire.setFlammable(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
        blockfire.setFlammable(Blocks.ACACIA_FENCE_GATE, 5, 20);
        blockfire.setFlammable(Blocks.OAK_FENCE, 5, 20);
        blockfire.setFlammable(Blocks.SPRUCE_FENCE, 5, 20);
        blockfire.setFlammable(Blocks.BIRCH_FENCE, 5, 20);
        blockfire.setFlammable(Blocks.JUNGLE_FENCE, 5, 20);
        blockfire.setFlammable(Blocks.DARK_OAK_FENCE, 5, 20);
        blockfire.setFlammable(Blocks.ACACIA_FENCE, 5, 20);
        blockfire.setFlammable(Blocks.OAK_STAIRS, 5, 20);
        blockfire.setFlammable(Blocks.BIRCH_STAIRS, 5, 20);
        blockfire.setFlammable(Blocks.SPRUCE_STAIRS, 5, 20);
        blockfire.setFlammable(Blocks.JUNGLE_STAIRS, 5, 20);
        blockfire.setFlammable(Blocks.ACACIA_STAIRS, 5, 20);
        blockfire.setFlammable(Blocks.DARK_OAK_STAIRS, 5, 20);
        blockfire.setFlammable(Blocks.OAK_LOG, 5, 5);
        blockfire.setFlammable(Blocks.SPRUCE_LOG, 5, 5);
        blockfire.setFlammable(Blocks.BIRCH_LOG, 5, 5);
        blockfire.setFlammable(Blocks.JUNGLE_LOG, 5, 5);
        blockfire.setFlammable(Blocks.ACACIA_LOG, 5, 5);
        blockfire.setFlammable(Blocks.DARK_OAK_LOG, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_OAK_LOG, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_OAK_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.OAK_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.SPRUCE_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.BIRCH_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.JUNGLE_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.ACACIA_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.DARK_OAK_WOOD, 5, 5);
        blockfire.setFlammable(Blocks.OAK_LEAVES, 30, 60);
        blockfire.setFlammable(Blocks.SPRUCE_LEAVES, 30, 60);
        blockfire.setFlammable(Blocks.BIRCH_LEAVES, 30, 60);
        blockfire.setFlammable(Blocks.JUNGLE_LEAVES, 30, 60);
        blockfire.setFlammable(Blocks.ACACIA_LEAVES, 30, 60);
        blockfire.setFlammable(Blocks.DARK_OAK_LEAVES, 30, 60);
        blockfire.setFlammable(Blocks.BOOKSHELF, 30, 20);
        blockfire.setFlammable(Blocks.TNT, 15, 100);
        blockfire.setFlammable(Blocks.GRASS, 60, 100);
        blockfire.setFlammable(Blocks.FERN, 60, 100);
        blockfire.setFlammable(Blocks.DEAD_BUSH, 60, 100);
        blockfire.setFlammable(Blocks.SUNFLOWER, 60, 100);
        blockfire.setFlammable(Blocks.LILAC, 60, 100);
        blockfire.setFlammable(Blocks.ROSE_BUSH, 60, 100);
        blockfire.setFlammable(Blocks.PEONY, 60, 100);
        blockfire.setFlammable(Blocks.TALL_GRASS, 60, 100);
        blockfire.setFlammable(Blocks.LARGE_FERN, 60, 100);
        blockfire.setFlammable(Blocks.DANDELION, 60, 100);
        blockfire.setFlammable(Blocks.POPPY, 60, 100);
        blockfire.setFlammable(Blocks.BLUE_ORCHID, 60, 100);
        blockfire.setFlammable(Blocks.ALLIUM, 60, 100);
        blockfire.setFlammable(Blocks.AZURE_BLUET, 60, 100);
        blockfire.setFlammable(Blocks.RED_TULIP, 60, 100);
        blockfire.setFlammable(Blocks.ORANGE_TULIP, 60, 100);
        blockfire.setFlammable(Blocks.WHITE_TULIP, 60, 100);
        blockfire.setFlammable(Blocks.PINK_TULIP, 60, 100);
        blockfire.setFlammable(Blocks.OXEYE_DAISY, 60, 100);
        blockfire.setFlammable(Blocks.CORNFLOWER, 60, 100);
        blockfire.setFlammable(Blocks.LILY_OF_THE_VALLEY, 60, 100);
        blockfire.setFlammable(Blocks.WITHER_ROSE, 60, 100);
        blockfire.setFlammable(Blocks.WHITE_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.ORANGE_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.MAGENTA_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.LIGHT_BLUE_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.YELLOW_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.LIME_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.PINK_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.GRAY_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.LIGHT_GRAY_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.CYAN_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.PURPLE_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.BLUE_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.BROWN_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.GREEN_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.RED_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.BLACK_WOOL, 30, 60);
        blockfire.setFlammable(Blocks.VINE, 15, 100);
        blockfire.setFlammable(Blocks.COAL_BLOCK, 5, 5);
        blockfire.setFlammable(Blocks.HAY_BLOCK, 60, 20);
        blockfire.setFlammable(Blocks.TARGET, 15, 20);
        blockfire.setFlammable(Blocks.WHITE_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.ORANGE_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.MAGENTA_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.LIGHT_BLUE_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.YELLOW_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.LIME_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.PINK_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.GRAY_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.LIGHT_GRAY_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.CYAN_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.PURPLE_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.BLUE_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.BROWN_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.GREEN_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.RED_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.BLACK_CARPET, 60, 20);
        blockfire.setFlammable(Blocks.DRIED_KELP_BLOCK, 30, 60);
        blockfire.setFlammable(Blocks.BAMBOO, 60, 60);
        blockfire.setFlammable(Blocks.SCAFFOLDING, 60, 60);
        blockfire.setFlammable(Blocks.LECTERN, 30, 20);
        blockfire.setFlammable(Blocks.COMPOSTER, 5, 20);
        blockfire.setFlammable(Blocks.SWEET_BERRY_BUSH, 60, 100);
        blockfire.setFlammable(Blocks.BEEHIVE, 5, 20);
        blockfire.setFlammable(Blocks.BEE_NEST, 30, 20);
        blockfire.setFlammable(Blocks.AZALEA_LEAVES, 30, 60);
        blockfire.setFlammable(Blocks.FLOWERING_AZALEA_LEAVES, 30, 60);
        blockfire.setFlammable(Blocks.CAVE_VINES, 15, 60);
        blockfire.setFlammable(Blocks.CAVE_VINES_PLANT, 15, 60);
        blockfire.setFlammable(Blocks.SPORE_BLOSSOM, 60, 100);
        blockfire.setFlammable(Blocks.AZALEA, 30, 60);
        blockfire.setFlammable(Blocks.FLOWERING_AZALEA, 30, 60);
        blockfire.setFlammable(Blocks.BIG_DRIPLEAF, 60, 100);
        blockfire.setFlammable(Blocks.BIG_DRIPLEAF_STEM, 60, 100);
        blockfire.setFlammable(Blocks.SMALL_DRIPLEAF, 60, 100);
        blockfire.setFlammable(Blocks.HANGING_ROOTS, 30, 60);
        blockfire.setFlammable(Blocks.GLOW_LICHEN, 15, 100);
    }
}
