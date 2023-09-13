package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventoryHolder;
import net.minecraft.world.IWorldInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockComposter extends Block implements IInventoryHolder {

    public static final int READY = 8;
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 7;
    public static final BlockStateInteger LEVEL = BlockProperties.LEVEL_COMPOSTER;
    public static final Object2FloatMap<IMaterial> COMPOSTABLES = new Object2FloatOpenHashMap();
    private static final int AABB_SIDE_THICKNESS = 2;
    private static final VoxelShape OUTER_SHAPE = VoxelShapes.block();
    private static final VoxelShape[] SHAPES = (VoxelShape[]) SystemUtils.make(new VoxelShape[9], (avoxelshape) -> {
        for (int i = 0; i < 8; ++i) {
            avoxelshape[i] = VoxelShapes.join(BlockComposter.OUTER_SHAPE, Block.box(2.0D, (double) Math.max(2, 1 + i * 2), 2.0D, 14.0D, 16.0D, 14.0D), OperatorBoolean.ONLY_FIRST);
        }

        avoxelshape[8] = avoxelshape[7];
    });

    public static void bootStrap() {
        BlockComposter.COMPOSTABLES.defaultReturnValue(-1.0F);
        float f = 0.3F;
        float f1 = 0.5F;
        float f2 = 0.65F;
        float f3 = 0.85F;
        float f4 = 1.0F;

        add(0.3F, Items.JUNGLE_LEAVES);
        add(0.3F, Items.OAK_LEAVES);
        add(0.3F, Items.SPRUCE_LEAVES);
        add(0.3F, Items.DARK_OAK_LEAVES);
        add(0.3F, Items.ACACIA_LEAVES);
        add(0.3F, Items.BIRCH_LEAVES);
        add(0.3F, Items.AZALEA_LEAVES);
        add(0.3F, Items.OAK_SAPLING);
        add(0.3F, Items.SPRUCE_SAPLING);
        add(0.3F, Items.BIRCH_SAPLING);
        add(0.3F, Items.JUNGLE_SAPLING);
        add(0.3F, Items.ACACIA_SAPLING);
        add(0.3F, Items.DARK_OAK_SAPLING);
        add(0.3F, Items.BEETROOT_SEEDS);
        add(0.3F, Items.DRIED_KELP);
        add(0.3F, Items.GRASS);
        add(0.3F, Items.KELP);
        add(0.3F, Items.MELON_SEEDS);
        add(0.3F, Items.PUMPKIN_SEEDS);
        add(0.3F, Items.SEAGRASS);
        add(0.3F, Items.SWEET_BERRIES);
        add(0.3F, Items.GLOW_BERRIES);
        add(0.3F, Items.WHEAT_SEEDS);
        add(0.3F, Items.MOSS_CARPET);
        add(0.3F, Items.SMALL_DRIPLEAF);
        add(0.3F, Items.HANGING_ROOTS);
        add(0.5F, Items.DRIED_KELP_BLOCK);
        add(0.5F, Items.TALL_GRASS);
        add(0.5F, Items.FLOWERING_AZALEA_LEAVES);
        add(0.5F, Items.CACTUS);
        add(0.5F, Items.SUGAR_CANE);
        add(0.5F, Items.VINE);
        add(0.5F, Items.NETHER_SPROUTS);
        add(0.5F, Items.WEEPING_VINES);
        add(0.5F, Items.TWISTING_VINES);
        add(0.5F, Items.MELON_SLICE);
        add(0.5F, Items.GLOW_LICHEN);
        add(0.65F, Items.SEA_PICKLE);
        add(0.65F, Items.LILY_PAD);
        add(0.65F, Items.PUMPKIN);
        add(0.65F, Items.CARVED_PUMPKIN);
        add(0.65F, Items.MELON);
        add(0.65F, Items.APPLE);
        add(0.65F, Items.BEETROOT);
        add(0.65F, Items.CARROT);
        add(0.65F, Items.COCOA_BEANS);
        add(0.65F, Items.POTATO);
        add(0.65F, Items.WHEAT);
        add(0.65F, Items.BROWN_MUSHROOM);
        add(0.65F, Items.RED_MUSHROOM);
        add(0.65F, Items.MUSHROOM_STEM);
        add(0.65F, Items.CRIMSON_FUNGUS);
        add(0.65F, Items.WARPED_FUNGUS);
        add(0.65F, Items.NETHER_WART);
        add(0.65F, Items.CRIMSON_ROOTS);
        add(0.65F, Items.WARPED_ROOTS);
        add(0.65F, Items.SHROOMLIGHT);
        add(0.65F, Items.DANDELION);
        add(0.65F, Items.POPPY);
        add(0.65F, Items.BLUE_ORCHID);
        add(0.65F, Items.ALLIUM);
        add(0.65F, Items.AZURE_BLUET);
        add(0.65F, Items.RED_TULIP);
        add(0.65F, Items.ORANGE_TULIP);
        add(0.65F, Items.WHITE_TULIP);
        add(0.65F, Items.PINK_TULIP);
        add(0.65F, Items.OXEYE_DAISY);
        add(0.65F, Items.CORNFLOWER);
        add(0.65F, Items.LILY_OF_THE_VALLEY);
        add(0.65F, Items.WITHER_ROSE);
        add(0.65F, Items.FERN);
        add(0.65F, Items.SUNFLOWER);
        add(0.65F, Items.LILAC);
        add(0.65F, Items.ROSE_BUSH);
        add(0.65F, Items.PEONY);
        add(0.65F, Items.LARGE_FERN);
        add(0.65F, Items.SPORE_BLOSSOM);
        add(0.65F, Items.AZALEA);
        add(0.65F, Items.MOSS_BLOCK);
        add(0.65F, Items.BIG_DRIPLEAF);
        add(0.85F, Items.HAY_BLOCK);
        add(0.85F, Items.BROWN_MUSHROOM_BLOCK);
        add(0.85F, Items.RED_MUSHROOM_BLOCK);
        add(0.85F, Items.NETHER_WART_BLOCK);
        add(0.85F, Items.WARPED_WART_BLOCK);
        add(0.85F, Items.FLOWERING_AZALEA);
        add(0.85F, Items.BREAD);
        add(0.85F, Items.BAKED_POTATO);
        add(0.85F, Items.COOKIE);
        add(1.0F, Items.CAKE);
        add(1.0F, Items.PUMPKIN_PIE);
    }

    private static void add(float f, IMaterial imaterial) {
        BlockComposter.COMPOSTABLES.put(imaterial.asItem(), f);
    }

    public BlockComposter(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockComposter.LEVEL, 0));
    }

    public static void handleFill(World world, BlockPosition blockposition, boolean flag) {
        IBlockData iblockdata = world.getBlockState(blockposition);

        world.playLocalSound((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), flag ? SoundEffects.COMPOSTER_FILL_SUCCESS : SoundEffects.COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        double d0 = iblockdata.getShape(world, blockposition).max(EnumDirection.EnumAxis.Y, 0.5D, 0.5D) + 0.03125D;
        double d1 = 0.13124999403953552D;
        double d2 = 0.737500011920929D;
        Random random = world.getRandom();

        for (int i = 0; i < 10; ++i) {
            double d3 = random.nextGaussian() * 0.02D;
            double d4 = random.nextGaussian() * 0.02D;
            double d5 = random.nextGaussian() * 0.02D;

            world.addParticle(Particles.COMPOSTER, (double) blockposition.getX() + 0.13124999403953552D + 0.737500011920929D * (double) random.nextFloat(), (double) blockposition.getY() + d0 + (double) random.nextFloat() * (1.0D - d0), (double) blockposition.getZ() + 0.13124999403953552D + 0.737500011920929D * (double) random.nextFloat(), d3, d4, d5);
        }

    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockComposter.SHAPES[(Integer) iblockdata.getValue(BlockComposter.LEVEL)];
    }

    @Override
    public VoxelShape getInteractionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockComposter.OUTER_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockComposter.SHAPES[0];
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if ((Integer) iblockdata.getValue(BlockComposter.LEVEL) == 7) {
            world.scheduleTick(blockposition, iblockdata.getBlock(), 20);
        }

    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        int i = (Integer) iblockdata.getValue(BlockComposter.LEVEL);
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (i < 8 && BlockComposter.COMPOSTABLES.containsKey(itemstack.getItem())) {
            if (i < 7 && !world.isClientSide) {
                IBlockData iblockdata1 = addItem(iblockdata, world, blockposition, itemstack);

                world.levelEvent(1500, blockposition, iblockdata != iblockdata1 ? 1 : 0);
                entityhuman.awardStat(StatisticList.ITEM_USED.get(itemstack.getItem()));
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else if (i == 8) {
            extractProduce(iblockdata, world, blockposition);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public static IBlockData insertItem(IBlockData iblockdata, WorldServer worldserver, ItemStack itemstack, BlockPosition blockposition) {
        int i = (Integer) iblockdata.getValue(BlockComposter.LEVEL);

        if (i < 7 && BlockComposter.COMPOSTABLES.containsKey(itemstack.getItem())) {
            IBlockData iblockdata1 = addItem(iblockdata, worldserver, blockposition, itemstack);

            itemstack.shrink(1);
            return iblockdata1;
        } else {
            return iblockdata;
        }
    }

    public static IBlockData extractProduce(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (!world.isClientSide) {
            float f = 0.7F;
            double d0 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            double d1 = (double) (world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double d2 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, new ItemStack(Items.BONE_MEAL));

            entityitem.setDefaultPickUpDelay();
            world.addFreshEntity(entityitem);
        }

        IBlockData iblockdata1 = empty(iblockdata, world, blockposition);

        world.playSound((EntityHuman) null, blockposition, SoundEffects.COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return iblockdata1;
    }

    static IBlockData empty(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(BlockComposter.LEVEL, 0);

        generatoraccess.setBlock(blockposition, iblockdata1, 3);
        return iblockdata1;
    }

    static IBlockData addItem(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, ItemStack itemstack) {
        int i = (Integer) iblockdata.getValue(BlockComposter.LEVEL);
        float f = BlockComposter.COMPOSTABLES.getFloat(itemstack.getItem());

        if ((i != 0 || f <= 0.0F) && generatoraccess.getRandom().nextDouble() >= (double) f) {
            return iblockdata;
        } else {
            int j = i + 1;
            IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(BlockComposter.LEVEL, j);

            generatoraccess.setBlock(blockposition, iblockdata1, 3);
            if (j == 7) {
                generatoraccess.scheduleTick(blockposition, iblockdata.getBlock(), 20);
            }

            return iblockdata1;
        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if ((Integer) iblockdata.getValue(BlockComposter.LEVEL) == 7) {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.cycle(BlockComposter.LEVEL), 3);
            worldserver.playSound((EntityHuman) null, blockposition, SoundEffects.COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.getValue(BlockComposter.LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockComposter.LEVEL);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public IWorldInventory getContainer(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        int i = (Integer) iblockdata.getValue(BlockComposter.LEVEL);

        return (IWorldInventory) (i == 8 ? new BlockComposter.ContainerOutput(iblockdata, generatoraccess, blockposition, new ItemStack(Items.BONE_MEAL)) : (i < 7 ? new BlockComposter.ContainerInput(iblockdata, generatoraccess, blockposition) : new BlockComposter.ContainerEmpty()));
    }

    public static class ContainerOutput extends InventorySubcontainer implements IWorldInventory {

        private final IBlockData state;
        private final GeneratorAccess level;
        private final BlockPosition pos;
        private boolean changed;

        public ContainerOutput(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, ItemStack itemstack) {
            super(itemstack);
            this.state = iblockdata;
            this.level = generatoraccess;
            this.pos = blockposition;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(EnumDirection enumdirection) {
            return enumdirection == EnumDirection.DOWN ? new int[]{0} : new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
            return !this.changed && enumdirection == EnumDirection.DOWN && itemstack.is(Items.BONE_MEAL);
        }

        @Override
        public void setChanged() {
            BlockComposter.empty(this.state, this.level, this.pos);
            this.changed = true;
        }
    }

    public static class ContainerInput extends InventorySubcontainer implements IWorldInventory {

        private final IBlockData state;
        private final GeneratorAccess level;
        private final BlockPosition pos;
        private boolean changed;

        public ContainerInput(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
            super(1);
            this.state = iblockdata;
            this.level = generatoraccess;
            this.pos = blockposition;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(EnumDirection enumdirection) {
            return enumdirection == EnumDirection.UP ? new int[]{0} : new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
            return !this.changed && enumdirection == EnumDirection.UP && BlockComposter.COMPOSTABLES.containsKey(itemstack.getItem());
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
            return false;
        }

        @Override
        public void setChanged() {
            ItemStack itemstack = this.getItem(0);

            if (!itemstack.isEmpty()) {
                this.changed = true;
                IBlockData iblockdata = BlockComposter.addItem(this.state, this.level, this.pos, itemstack);

                this.level.levelEvent(1500, this.pos, iblockdata != this.state ? 1 : 0);
                this.removeItemNoUpdate(0);
            }

        }
    }

    public static class ContainerEmpty extends InventorySubcontainer implements IWorldInventory {

        public ContainerEmpty() {
            super(0);
        }

        @Override
        public int[] getSlotsForFace(EnumDirection enumdirection) {
            return new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
            return false;
        }
    }
}
