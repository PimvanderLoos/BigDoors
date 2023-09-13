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
import net.minecraft.world.level.block.state.properties.IBlockState;
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
    private static final VoxelShape OUTER_SHAPE = VoxelShapes.b();
    private static final VoxelShape[] SHAPES = (VoxelShape[]) SystemUtils.a((Object) (new VoxelShape[9]), (avoxelshape) -> {
        for (int i = 0; i < 8; ++i) {
            avoxelshape[i] = VoxelShapes.a(BlockComposter.OUTER_SHAPE, Block.a(2.0D, (double) Math.max(2, 1 + i * 2), 2.0D, 14.0D, 16.0D, 14.0D), OperatorBoolean.ONLY_FIRST);
        }

        avoxelshape[8] = avoxelshape[7];
    });

    public static void c() {
        BlockComposter.COMPOSTABLES.defaultReturnValue(-1.0F);
        float f = 0.3F;
        float f1 = 0.5F;
        float f2 = 0.65F;
        float f3 = 0.85F;
        float f4 = 1.0F;

        a(0.3F, Items.JUNGLE_LEAVES);
        a(0.3F, Items.OAK_LEAVES);
        a(0.3F, Items.SPRUCE_LEAVES);
        a(0.3F, Items.DARK_OAK_LEAVES);
        a(0.3F, Items.ACACIA_LEAVES);
        a(0.3F, Items.BIRCH_LEAVES);
        a(0.3F, Items.AZALEA_LEAVES);
        a(0.3F, Items.OAK_SAPLING);
        a(0.3F, Items.SPRUCE_SAPLING);
        a(0.3F, Items.BIRCH_SAPLING);
        a(0.3F, Items.JUNGLE_SAPLING);
        a(0.3F, Items.ACACIA_SAPLING);
        a(0.3F, Items.DARK_OAK_SAPLING);
        a(0.3F, Items.BEETROOT_SEEDS);
        a(0.3F, Items.DRIED_KELP);
        a(0.3F, Items.GRASS);
        a(0.3F, Items.KELP);
        a(0.3F, Items.MELON_SEEDS);
        a(0.3F, Items.PUMPKIN_SEEDS);
        a(0.3F, Items.SEAGRASS);
        a(0.3F, Items.SWEET_BERRIES);
        a(0.3F, Items.GLOW_BERRIES);
        a(0.3F, Items.WHEAT_SEEDS);
        a(0.3F, Items.MOSS_CARPET);
        a(0.3F, Items.SMALL_DRIPLEAF);
        a(0.3F, Items.HANGING_ROOTS);
        a(0.5F, Items.DRIED_KELP_BLOCK);
        a(0.5F, Items.TALL_GRASS);
        a(0.5F, Items.AZALEA_LEAVES_FLOWERS);
        a(0.5F, Items.CACTUS);
        a(0.5F, Items.SUGAR_CANE);
        a(0.5F, Items.VINE);
        a(0.5F, Items.NETHER_SPROUTS);
        a(0.5F, Items.WEEPING_VINES);
        a(0.5F, Items.TWISTING_VINES);
        a(0.5F, Items.MELON_SLICE);
        a(0.5F, Items.GLOW_LICHEN);
        a(0.65F, Items.SEA_PICKLE);
        a(0.65F, Items.LILY_PAD);
        a(0.65F, Items.PUMPKIN);
        a(0.65F, Items.CARVED_PUMPKIN);
        a(0.65F, Items.MELON);
        a(0.65F, Items.APPLE);
        a(0.65F, Items.BEETROOT);
        a(0.65F, Items.CARROT);
        a(0.65F, Items.COCOA_BEANS);
        a(0.65F, Items.POTATO);
        a(0.65F, Items.WHEAT);
        a(0.65F, Items.BROWN_MUSHROOM);
        a(0.65F, Items.RED_MUSHROOM);
        a(0.65F, Items.MUSHROOM_STEM);
        a(0.65F, Items.CRIMSON_FUNGUS);
        a(0.65F, Items.WARPED_FUNGUS);
        a(0.65F, Items.NETHER_WART);
        a(0.65F, Items.CRIMSON_ROOTS);
        a(0.65F, Items.WARPED_ROOTS);
        a(0.65F, Items.SHROOMLIGHT);
        a(0.65F, Items.DANDELION);
        a(0.65F, Items.POPPY);
        a(0.65F, Items.BLUE_ORCHID);
        a(0.65F, Items.ALLIUM);
        a(0.65F, Items.AZURE_BLUET);
        a(0.65F, Items.RED_TULIP);
        a(0.65F, Items.ORANGE_TULIP);
        a(0.65F, Items.WHITE_TULIP);
        a(0.65F, Items.PINK_TULIP);
        a(0.65F, Items.OXEYE_DAISY);
        a(0.65F, Items.CORNFLOWER);
        a(0.65F, Items.LILY_OF_THE_VALLEY);
        a(0.65F, Items.WITHER_ROSE);
        a(0.65F, Items.FERN);
        a(0.65F, Items.SUNFLOWER);
        a(0.65F, Items.LILAC);
        a(0.65F, Items.ROSE_BUSH);
        a(0.65F, Items.PEONY);
        a(0.65F, Items.LARGE_FERN);
        a(0.65F, Items.SPORE_BLOSSOM);
        a(0.65F, Items.AZALEA);
        a(0.65F, Items.MOSS_BLOCK);
        a(0.65F, Items.BIG_DRIPLEAF);
        a(0.85F, Items.HAY_BLOCK);
        a(0.85F, Items.BROWN_MUSHROOM_BLOCK);
        a(0.85F, Items.RED_MUSHROOM_BLOCK);
        a(0.85F, Items.NETHER_WART_BLOCK);
        a(0.85F, Items.WARPED_WART_BLOCK);
        a(0.85F, Items.FLOWERING_AZALEA);
        a(0.85F, Items.BREAD);
        a(0.85F, Items.BAKED_POTATO);
        a(0.85F, Items.COOKIE);
        a(1.0F, Items.CAKE);
        a(1.0F, Items.PUMPKIN_PIE);
    }

    private static void a(float f, IMaterial imaterial) {
        BlockComposter.COMPOSTABLES.put(imaterial.getItem(), f);
    }

    public BlockComposter(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockComposter.LEVEL, 0));
    }

    public static void a(World world, BlockPosition blockposition, boolean flag) {
        IBlockData iblockdata = world.getType(blockposition);

        world.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), flag ? SoundEffects.COMPOSTER_FILL_SUCCESS : SoundEffects.COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        double d0 = iblockdata.getShape(world, blockposition).b(EnumDirection.EnumAxis.Y, 0.5D, 0.5D) + 0.03125D;
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
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockComposter.SHAPES[(Integer) iblockdata.get(BlockComposter.LEVEL)];
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockComposter.OUTER_SHAPE;
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockComposter.SHAPES[0];
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if ((Integer) iblockdata.get(BlockComposter.LEVEL) == 7) {
            world.getBlockTickList().a(blockposition, iblockdata.getBlock(), 20);
        }

    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        int i = (Integer) iblockdata.get(BlockComposter.LEVEL);
        ItemStack itemstack = entityhuman.b(enumhand);

        if (i < 8 && BlockComposter.COMPOSTABLES.containsKey(itemstack.getItem())) {
            if (i < 7 && !world.isClientSide) {
                IBlockData iblockdata1 = a(iblockdata, (GeneratorAccess) world, blockposition, itemstack);

                world.triggerEffect(1500, blockposition, iblockdata != iblockdata1 ? 1 : 0);
                entityhuman.b(StatisticList.ITEM_USED.b(itemstack.getItem()));
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.subtract(1);
                }
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else if (i == 8) {
            d(iblockdata, world, blockposition);
            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public static IBlockData a(IBlockData iblockdata, WorldServer worldserver, ItemStack itemstack, BlockPosition blockposition) {
        int i = (Integer) iblockdata.get(BlockComposter.LEVEL);

        if (i < 7 && BlockComposter.COMPOSTABLES.containsKey(itemstack.getItem())) {
            IBlockData iblockdata1 = a(iblockdata, (GeneratorAccess) worldserver, blockposition, itemstack);

            itemstack.subtract(1);
            return iblockdata1;
        } else {
            return iblockdata;
        }
    }

    public static IBlockData d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (!world.isClientSide) {
            float f = 0.7F;
            double d0 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            double d1 = (double) (world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double d2 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, new ItemStack(Items.BONE_MEAL));

            entityitem.defaultPickupDelay();
            world.addEntity(entityitem);
        }

        IBlockData iblockdata1 = c(iblockdata, (GeneratorAccess) world, blockposition);

        world.playSound((EntityHuman) null, blockposition, SoundEffects.COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return iblockdata1;
    }

    static IBlockData c(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockComposter.LEVEL, 0);

        generatoraccess.setTypeAndData(blockposition, iblockdata1, 3);
        return iblockdata1;
    }

    static IBlockData a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, ItemStack itemstack) {
        int i = (Integer) iblockdata.get(BlockComposter.LEVEL);
        float f = BlockComposter.COMPOSTABLES.getFloat(itemstack.getItem());

        if ((i != 0 || f <= 0.0F) && generatoraccess.getRandom().nextDouble() >= (double) f) {
            return iblockdata;
        } else {
            int j = i + 1;
            IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockComposter.LEVEL, j);

            generatoraccess.setTypeAndData(blockposition, iblockdata1, 3);
            if (j == 7) {
                generatoraccess.getBlockTickList().a(blockposition, iblockdata.getBlock(), 20);
            }

            return iblockdata1;
        }
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if ((Integer) iblockdata.get(BlockComposter.LEVEL) == 7) {
            worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockComposter.LEVEL), 3);
            worldserver.playSound((EntityHuman) null, blockposition, SoundEffects.COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.get(BlockComposter.LEVEL);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockComposter.LEVEL);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public IWorldInventory a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        int i = (Integer) iblockdata.get(BlockComposter.LEVEL);

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
            return !this.changed && enumdirection == EnumDirection.DOWN && itemstack.a(Items.BONE_MEAL);
        }

        @Override
        public void update() {
            BlockComposter.c(this.state, this.level, this.pos);
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
        public void update() {
            ItemStack itemstack = this.getItem(0);

            if (!itemstack.isEmpty()) {
                this.changed = true;
                IBlockData iblockdata = BlockComposter.a(this.state, this.level, this.pos, itemstack);

                this.level.triggerEffect(1500, this.pos, iblockdata != this.state ? 1 : 0);
                this.splitWithoutUpdate(0);
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
