package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.EnumDirection8;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.BlockFacingHorizontal;
import net.minecraft.world.level.block.BlockStem;
import net.minecraft.world.level.block.BlockStemmed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyChestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkConverter {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final ChunkConverter EMPTY = new ChunkConverter(BlockAccessAir.INSTANCE);
    private static final String TAG_INDICES = "Indices";
    private static final EnumDirection8[] DIRECTIONS = EnumDirection8.values();
    private final EnumSet<EnumDirection8> sides;
    private final int[][] index;
    static final Map<Block, ChunkConverter.a> MAP = new IdentityHashMap();
    static final Set<ChunkConverter.a> CHUNKY_FIXERS = Sets.newHashSet();

    private ChunkConverter(LevelHeightAccessor levelheightaccessor) {
        this.sides = EnumSet.noneOf(EnumDirection8.class);
        this.index = new int[levelheightaccessor.getSectionsCount()][];
    }

    public ChunkConverter(NBTTagCompound nbttagcompound, LevelHeightAccessor levelheightaccessor) {
        this(levelheightaccessor);
        if (nbttagcompound.contains("Indices", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Indices");

            for (int i = 0; i < this.index.length; ++i) {
                String s = String.valueOf(i);

                if (nbttagcompound1.contains(s, 11)) {
                    this.index[i] = nbttagcompound1.getIntArray(s);
                }
            }
        }

        int j = nbttagcompound.getInt("Sides");
        EnumDirection8[] aenumdirection8 = EnumDirection8.values();
        int k = aenumdirection8.length;

        for (int l = 0; l < k; ++l) {
            EnumDirection8 enumdirection8 = aenumdirection8[l];

            if ((j & 1 << enumdirection8.ordinal()) != 0) {
                this.sides.add(enumdirection8);
            }
        }

    }

    public void upgrade(Chunk chunk) {
        this.upgradeInside(chunk);
        EnumDirection8[] aenumdirection8 = ChunkConverter.DIRECTIONS;
        int i = aenumdirection8.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection8 enumdirection8 = aenumdirection8[j];

            upgradeSides(chunk, enumdirection8);
        }

        World world = chunk.getLevel();

        ChunkConverter.CHUNKY_FIXERS.forEach((chunkconverter_a) -> {
            chunkconverter_a.processChunk(world);
        });
    }

    private static void upgradeSides(Chunk chunk, EnumDirection8 enumdirection8) {
        World world = chunk.getLevel();

        if (chunk.getUpgradeData().sides.remove(enumdirection8)) {
            Set<EnumDirection> set = enumdirection8.getDirections();
            boolean flag = false;
            boolean flag1 = true;
            boolean flag2 = set.contains(EnumDirection.EAST);
            boolean flag3 = set.contains(EnumDirection.WEST);
            boolean flag4 = set.contains(EnumDirection.SOUTH);
            boolean flag5 = set.contains(EnumDirection.NORTH);
            boolean flag6 = set.size() == 1;
            ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
            int i = chunkcoordintpair.getMinBlockX() + (flag6 && (flag5 || flag4) ? 1 : (flag3 ? 0 : 15));
            int j = chunkcoordintpair.getMinBlockX() + (flag6 && (flag5 || flag4) ? 14 : (flag3 ? 0 : 15));
            int k = chunkcoordintpair.getMinBlockZ() + (flag6 && (flag2 || flag3) ? 1 : (flag5 ? 0 : 15));
            int l = chunkcoordintpair.getMinBlockZ() + (flag6 && (flag2 || flag3) ? 14 : (flag5 ? 0 : 15));
            EnumDirection[] aenumdirection = EnumDirection.values();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            Iterator iterator = BlockPosition.betweenClosed(i, world.getMinBuildHeight(), k, j, world.getMaxBuildHeight() - 1, l).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition = (BlockPosition) iterator.next();
                IBlockData iblockdata = world.getBlockState(blockposition);
                IBlockData iblockdata1 = iblockdata;
                EnumDirection[] aenumdirection1 = aenumdirection;
                int i1 = aenumdirection.length;

                for (int j1 = 0; j1 < i1; ++j1) {
                    EnumDirection enumdirection = aenumdirection1[j1];

                    blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
                    iblockdata1 = updateState(iblockdata1, enumdirection, world, blockposition, blockposition_mutableblockposition);
                }

                Block.updateOrDestroy(iblockdata, iblockdata1, world, blockposition, 18);
            }

        }
    }

    private static IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return ((ChunkConverter.a) ChunkConverter.MAP.getOrDefault(iblockdata.getBlock(), ChunkConverter.Type.DEFAULT)).updateShape(iblockdata, enumdirection, generatoraccess.getBlockState(blockposition1), generatoraccess, blockposition, blockposition1);
    }

    private void upgradeInside(Chunk chunk) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        World world = chunk.getLevel();

        int i;

        for (i = 0; i < this.index.length; ++i) {
            ChunkSection chunksection = chunk.getSection(i);
            int[] aint = this.index[i];

            this.index[i] = null;
            if (aint != null && aint.length > 0) {
                EnumDirection[] aenumdirection = EnumDirection.values();
                DataPaletteBlock<IBlockData> datapaletteblock = chunksection.getStates();
                int[] aint1 = aint;
                int j = aint.length;

                for (int k = 0; k < j; ++k) {
                    int l = aint1[k];
                    int i1 = l & 15;
                    int j1 = l >> 8 & 15;
                    int k1 = l >> 4 & 15;

                    blockposition_mutableblockposition.set(chunkcoordintpair.getMinBlockX() + i1, chunksection.bottomBlockY() + j1, chunkcoordintpair.getMinBlockZ() + k1);
                    IBlockData iblockdata = (IBlockData) datapaletteblock.get(l);
                    IBlockData iblockdata1 = iblockdata;
                    EnumDirection[] aenumdirection1 = aenumdirection;
                    int l1 = aenumdirection.length;

                    for (int i2 = 0; i2 < l1; ++i2) {
                        EnumDirection enumdirection = aenumdirection1[i2];

                        blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, enumdirection);
                        if (SectionPosition.blockToSectionCoord(blockposition_mutableblockposition.getX()) == chunkcoordintpair.x && SectionPosition.blockToSectionCoord(blockposition_mutableblockposition.getZ()) == chunkcoordintpair.z) {
                            iblockdata1 = updateState(iblockdata1, enumdirection, world, blockposition_mutableblockposition, blockposition_mutableblockposition1);
                        }
                    }

                    Block.updateOrDestroy(iblockdata, iblockdata1, world, blockposition_mutableblockposition, 18);
                }
            }
        }

        for (i = 0; i < this.index.length; ++i) {
            if (this.index[i] != null) {
                ChunkConverter.LOGGER.warn("Discarding update data for section {} for chunk ({} {})", world.getSectionYFromSectionIndex(i), chunkcoordintpair.x, chunkcoordintpair.z);
            }

            this.index[i] = null;
        }

    }

    public boolean isEmpty() {
        int[][] aint = this.index;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int[] aint1 = aint[j];

            if (aint1 != null) {
                return false;
            }
        }

        return this.sides.isEmpty();
    }

    public NBTTagCompound write() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        int i;

        for (i = 0; i < this.index.length; ++i) {
            String s = String.valueOf(i);

            if (this.index[i] != null && this.index[i].length != 0) {
                nbttagcompound1.putIntArray(s, this.index[i]);
            }
        }

        if (!nbttagcompound1.isEmpty()) {
            nbttagcompound.put("Indices", nbttagcompound1);
        }

        i = 0;

        EnumDirection8 enumdirection8;

        for (Iterator iterator = this.sides.iterator(); iterator.hasNext(); i |= 1 << enumdirection8.ordinal()) {
            enumdirection8 = (EnumDirection8) iterator.next();
        }

        nbttagcompound.putByte("Sides", (byte) i);
        return nbttagcompound;
    }

    private static enum Type implements ChunkConverter.a {

        BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN}) {
            @Override
            public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                return iblockdata;
            }
        },
        DEFAULT(new Block[0]) {
            @Override
            public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                return iblockdata.updateShape(enumdirection, generatoraccess.getBlockState(blockposition1), generatoraccess, blockposition, blockposition1);
            }
        },
        CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}) {
            @Override
            public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                if (iblockdata1.is(iblockdata.getBlock()) && enumdirection.getAxis().isHorizontal() && iblockdata.getValue(BlockChest.TYPE) == BlockPropertyChestType.SINGLE && iblockdata1.getValue(BlockChest.TYPE) == BlockPropertyChestType.SINGLE) {
                    EnumDirection enumdirection1 = (EnumDirection) iblockdata.getValue(BlockChest.FACING);

                    if (enumdirection.getAxis() != enumdirection1.getAxis() && enumdirection1 == iblockdata1.getValue(BlockChest.FACING)) {
                        BlockPropertyChestType blockpropertychesttype = enumdirection == enumdirection1.getClockWise() ? BlockPropertyChestType.LEFT : BlockPropertyChestType.RIGHT;

                        generatoraccess.setBlock(blockposition1, (IBlockData) iblockdata1.setValue(BlockChest.TYPE, blockpropertychesttype.getOpposite()), 18);
                        if (enumdirection1 == EnumDirection.NORTH || enumdirection1 == EnumDirection.EAST) {
                            TileEntity tileentity = generatoraccess.getBlockEntity(blockposition);
                            TileEntity tileentity1 = generatoraccess.getBlockEntity(blockposition1);

                            if (tileentity instanceof TileEntityChest && tileentity1 instanceof TileEntityChest) {
                                TileEntityChest.swapContents((TileEntityChest) tileentity, (TileEntityChest) tileentity1);
                            }
                        }

                        return (IBlockData) iblockdata.setValue(BlockChest.TYPE, blockpropertychesttype);
                    }
                }

                return iblockdata;
            }
        },
        LEAVES(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}) {
            private final ThreadLocal<List<ObjectSet<BlockPosition>>> queue = ThreadLocal.withInitial(() -> {
                return Lists.newArrayListWithCapacity(7);
            });

            @Override
            public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                IBlockData iblockdata2 = iblockdata.updateShape(enumdirection, generatoraccess.getBlockState(blockposition1), generatoraccess, blockposition, blockposition1);

                if (iblockdata != iblockdata2) {
                    int i = (Integer) iblockdata2.getValue(BlockProperties.DISTANCE);
                    List<ObjectSet<BlockPosition>> list = (List) this.queue.get();

                    if (list.isEmpty()) {
                        for (int j = 0; j < 7; ++j) {
                            list.add(new ObjectOpenHashSet());
                        }
                    }

                    ((ObjectSet) list.get(i)).add(blockposition.immutable());
                }

                return iblockdata;
            }

            @Override
            public void processChunk(GeneratorAccess generatoraccess) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
                List<ObjectSet<BlockPosition>> list = (List) this.queue.get();

                for (int i = 2; i < list.size(); ++i) {
                    int j = i - 1;
                    ObjectSet<BlockPosition> objectset = (ObjectSet) list.get(j);
                    ObjectSet<BlockPosition> objectset1 = (ObjectSet) list.get(i);
                    ObjectIterator objectiterator = objectset.iterator();

                    while (objectiterator.hasNext()) {
                        BlockPosition blockposition = (BlockPosition) objectiterator.next();
                        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

                        if ((Integer) iblockdata.getValue(BlockProperties.DISTANCE) >= j) {
                            generatoraccess.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockProperties.DISTANCE, j), 18);
                            if (i != 7) {
                                EnumDirection[] aenumdirection = null.DIRECTIONS;
                                int k = aenumdirection.length;

                                for (int l = 0; l < k; ++l) {
                                    EnumDirection enumdirection = aenumdirection[l];

                                    blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
                                    IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition_mutableblockposition);

                                    if (iblockdata1.hasProperty(BlockProperties.DISTANCE) && (Integer) iblockdata.getValue(BlockProperties.DISTANCE) > i) {
                                        objectset1.add(blockposition_mutableblockposition.immutable());
                                    }
                                }
                            }
                        }
                    }
                }

                list.clear();
            }
        },
        STEM_BLOCK(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}) {
            @Override
            public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                if ((Integer) iblockdata.getValue(BlockStem.AGE) == 7) {
                    BlockStemmed blockstemmed = ((BlockStem) iblockdata.getBlock()).getFruit();

                    if (iblockdata1.is((Block) blockstemmed)) {
                        return (IBlockData) blockstemmed.getAttachedStem().defaultBlockState().setValue(BlockFacingHorizontal.FACING, enumdirection);
                    }
                }

                return iblockdata;
            }
        };

        public static final EnumDirection[] DIRECTIONS = EnumDirection.values();

        Type(Block... ablock) {
            this(false, ablock);
        }

        Type(boolean flag, Block... ablock) {
            Block[] ablock1 = ablock;
            int i = ablock.length;

            for (int j = 0; j < i; ++j) {
                Block block = ablock1[j];

                ChunkConverter.MAP.put(block, this);
            }

            if (flag) {
                ChunkConverter.CHUNKY_FIXERS.add(this);
            }

        }
    }

    public interface a {

        IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1);

        default void processChunk(GeneratorAccess generatoraccess) {}
    }
}
