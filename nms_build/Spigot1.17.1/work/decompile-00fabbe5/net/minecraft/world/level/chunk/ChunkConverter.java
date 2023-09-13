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
import net.minecraft.core.BaseBlockPosition;
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
        if (nbttagcompound.hasKeyOfType("Indices", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Indices");

            for (int i = 0; i < this.index.length; ++i) {
                String s = String.valueOf(i);

                if (nbttagcompound1.hasKeyOfType(s, 11)) {
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

    public void a(Chunk chunk) {
        this.b(chunk);
        EnumDirection8[] aenumdirection8 = ChunkConverter.DIRECTIONS;
        int i = aenumdirection8.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection8 enumdirection8 = aenumdirection8[j];

            a(chunk, enumdirection8);
        }

        World world = chunk.getWorld();

        ChunkConverter.CHUNKY_FIXERS.forEach((chunkconverter_a) -> {
            chunkconverter_a.a(world);
        });
    }

    private static void a(Chunk chunk, EnumDirection8 enumdirection8) {
        World world = chunk.getWorld();

        if (chunk.q().sides.remove(enumdirection8)) {
            Set<EnumDirection> set = enumdirection8.a();
            boolean flag = false;
            boolean flag1 = true;
            boolean flag2 = set.contains(EnumDirection.EAST);
            boolean flag3 = set.contains(EnumDirection.WEST);
            boolean flag4 = set.contains(EnumDirection.SOUTH);
            boolean flag5 = set.contains(EnumDirection.NORTH);
            boolean flag6 = set.size() == 1;
            ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
            int i = chunkcoordintpair.d() + (flag6 && (flag5 || flag4) ? 1 : (flag3 ? 0 : 15));
            int j = chunkcoordintpair.d() + (flag6 && (flag5 || flag4) ? 14 : (flag3 ? 0 : 15));
            int k = chunkcoordintpair.e() + (flag6 && (flag2 || flag3) ? 1 : (flag5 ? 0 : 15));
            int l = chunkcoordintpair.e() + (flag6 && (flag2 || flag3) ? 14 : (flag5 ? 0 : 15));
            EnumDirection[] aenumdirection = EnumDirection.values();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            Iterator iterator = BlockPosition.b(i, world.getMinBuildHeight(), k, j, world.getMaxBuildHeight() - 1, l).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition = (BlockPosition) iterator.next();
                IBlockData iblockdata = world.getType(blockposition);
                IBlockData iblockdata1 = iblockdata;
                EnumDirection[] aenumdirection1 = aenumdirection;
                int i1 = aenumdirection.length;

                for (int j1 = 0; j1 < i1; ++j1) {
                    EnumDirection enumdirection = aenumdirection1[j1];

                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
                    iblockdata1 = a(iblockdata1, enumdirection, world, blockposition, blockposition_mutableblockposition);
                }

                Block.a(iblockdata, iblockdata1, world, blockposition, 18);
            }

        }
    }

    private static IBlockData a(IBlockData iblockdata, EnumDirection enumdirection, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return ((ChunkConverter.a) ChunkConverter.MAP.getOrDefault(iblockdata.getBlock(), ChunkConverter.Type.DEFAULT)).a(iblockdata, enumdirection, generatoraccess.getType(blockposition1), generatoraccess, blockposition, blockposition1);
    }

    private void b(Chunk chunk) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        World world = chunk.getWorld();

        int i;

        for (i = 0; i < this.index.length; ++i) {
            ChunkSection chunksection = chunk.getSections()[i];
            int[] aint = this.index[i];

            this.index[i] = null;
            if (chunksection != null && aint != null && aint.length > 0) {
                EnumDirection[] aenumdirection = EnumDirection.values();
                DataPaletteBlock<IBlockData> datapaletteblock = chunksection.getBlocks();
                int[] aint1 = aint;
                int j = aint.length;

                for (int k = 0; k < j; ++k) {
                    int l = aint1[k];
                    int i1 = l & 15;
                    int j1 = l >> 8 & 15;
                    int k1 = l >> 4 & 15;

                    blockposition_mutableblockposition.d(chunkcoordintpair.d() + i1, chunksection.getYPosition() + j1, chunkcoordintpair.e() + k1);
                    IBlockData iblockdata = (IBlockData) datapaletteblock.a(l);
                    IBlockData iblockdata1 = iblockdata;
                    EnumDirection[] aenumdirection1 = aenumdirection;
                    int l1 = aenumdirection.length;

                    for (int i2 = 0; i2 < l1; ++i2) {
                        EnumDirection enumdirection = aenumdirection1[i2];

                        blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, enumdirection);
                        if (SectionPosition.a(blockposition_mutableblockposition.getX()) == chunkcoordintpair.x && SectionPosition.a(blockposition_mutableblockposition.getZ()) == chunkcoordintpair.z) {
                            iblockdata1 = a(iblockdata1, enumdirection, world, blockposition_mutableblockposition, blockposition_mutableblockposition1);
                        }
                    }

                    Block.a(iblockdata, iblockdata1, world, blockposition_mutableblockposition, 18);
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

    public boolean a() {
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

    public NBTTagCompound b() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        int i;

        for (i = 0; i < this.index.length; ++i) {
            String s = String.valueOf(i);

            if (this.index[i] != null && this.index[i].length != 0) {
                nbttagcompound1.setIntArray(s, this.index[i]);
            }
        }

        if (!nbttagcompound1.isEmpty()) {
            nbttagcompound.set("Indices", nbttagcompound1);
        }

        i = 0;

        EnumDirection8 enumdirection8;

        for (Iterator iterator = this.sides.iterator(); iterator.hasNext(); i |= 1 << enumdirection8.ordinal()) {
            enumdirection8 = (EnumDirection8) iterator.next();
        }

        nbttagcompound.setByte("Sides", (byte) i);
        return nbttagcompound;
    }

    private static enum Type implements ChunkConverter.a {

        BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN}) {
            @Override
            public IBlockData a(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                return iblockdata;
            }
        },
        DEFAULT(new Block[0]) {
            @Override
            public IBlockData a(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                return iblockdata.updateState(enumdirection, generatoraccess.getType(blockposition1), generatoraccess, blockposition, blockposition1);
            }
        },
        CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}) {
            @Override
            public IBlockData a(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                if (iblockdata1.a(iblockdata.getBlock()) && enumdirection.n().d() && iblockdata.get(BlockChest.TYPE) == BlockPropertyChestType.SINGLE && iblockdata1.get(BlockChest.TYPE) == BlockPropertyChestType.SINGLE) {
                    EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockChest.FACING);

                    if (enumdirection.n() != enumdirection1.n() && enumdirection1 == iblockdata1.get(BlockChest.FACING)) {
                        BlockPropertyChestType blockpropertychesttype = enumdirection == enumdirection1.g() ? BlockPropertyChestType.LEFT : BlockPropertyChestType.RIGHT;

                        generatoraccess.setTypeAndData(blockposition1, (IBlockData) iblockdata1.set(BlockChest.TYPE, blockpropertychesttype.a()), 18);
                        if (enumdirection1 == EnumDirection.NORTH || enumdirection1 == EnumDirection.EAST) {
                            TileEntity tileentity = generatoraccess.getTileEntity(blockposition);
                            TileEntity tileentity1 = generatoraccess.getTileEntity(blockposition1);

                            if (tileentity instanceof TileEntityChest && tileentity1 instanceof TileEntityChest) {
                                TileEntityChest.a((TileEntityChest) tileentity, (TileEntityChest) tileentity1);
                            }
                        }

                        return (IBlockData) iblockdata.set(BlockChest.TYPE, blockpropertychesttype);
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
            public IBlockData a(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                IBlockData iblockdata2 = iblockdata.updateState(enumdirection, generatoraccess.getType(blockposition1), generatoraccess, blockposition, blockposition1);

                if (iblockdata != iblockdata2) {
                    int i = (Integer) iblockdata2.get(BlockProperties.DISTANCE);
                    List<ObjectSet<BlockPosition>> list = (List) this.queue.get();

                    if (list.isEmpty()) {
                        for (int j = 0; j < 7; ++j) {
                            list.add(new ObjectOpenHashSet());
                        }
                    }

                    ((ObjectSet) list.get(i)).add(blockposition.immutableCopy());
                }

                return iblockdata;
            }

            @Override
            public void a(GeneratorAccess generatoraccess) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
                List<ObjectSet<BlockPosition>> list = (List) this.queue.get();

                for (int i = 2; i < list.size(); ++i) {
                    int j = i - 1;
                    ObjectSet<BlockPosition> objectset = (ObjectSet) list.get(j);
                    ObjectSet<BlockPosition> objectset1 = (ObjectSet) list.get(i);
                    ObjectIterator objectiterator = objectset.iterator();

                    while (objectiterator.hasNext()) {
                        BlockPosition blockposition = (BlockPosition) objectiterator.next();
                        IBlockData iblockdata = generatoraccess.getType(blockposition);

                        if ((Integer) iblockdata.get(BlockProperties.DISTANCE) >= j) {
                            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.DISTANCE, j), 18);
                            if (i != 7) {
                                EnumDirection[] aenumdirection = null.DIRECTIONS;
                                int k = aenumdirection.length;

                                for (int l = 0; l < k; ++l) {
                                    EnumDirection enumdirection = aenumdirection[l];

                                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
                                    IBlockData iblockdata1 = generatoraccess.getType(blockposition_mutableblockposition);

                                    if (iblockdata1.b(BlockProperties.DISTANCE) && (Integer) iblockdata.get(BlockProperties.DISTANCE) > i) {
                                        objectset1.add(blockposition_mutableblockposition.immutableCopy());
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
            public IBlockData a(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
                if ((Integer) iblockdata.get(BlockStem.AGE) == 7) {
                    BlockStemmed blockstemmed = ((BlockStem) iblockdata.getBlock()).c();

                    if (iblockdata1.a((Block) blockstemmed)) {
                        return (IBlockData) blockstemmed.d().getBlockData().set(BlockFacingHorizontal.FACING, enumdirection);
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

        IBlockData a(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1);

        default void a(GeneratorAccess generatoraccess) {}
    }
}
