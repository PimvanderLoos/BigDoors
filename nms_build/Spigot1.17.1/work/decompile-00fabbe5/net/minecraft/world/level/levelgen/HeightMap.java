package net.minecraft.world.level.levelgen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.DataBits;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.BlockLeaves;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HeightMap {

    private static final Logger LOGGER = LogManager.getLogger();
    static final Predicate<IBlockData> NOT_AIR = (iblockdata) -> {
        return !iblockdata.isAir();
    };
    static final Predicate<IBlockData> MATERIAL_MOTION_BLOCKING = (iblockdata) -> {
        return iblockdata.getMaterial().isSolid();
    };
    private final DataBits data;
    private final Predicate<IBlockData> isOpaque;
    private final IChunkAccess chunk;

    public HeightMap(IChunkAccess ichunkaccess, HeightMap.Type heightmap_type) {
        this.isOpaque = heightmap_type.e();
        this.chunk = ichunkaccess;
        int i = MathHelper.e(ichunkaccess.getHeight() + 1);

        this.data = new DataBits(i, 256);
    }

    public static void a(IChunkAccess ichunkaccess, Set<HeightMap.Type> set) {
        int i = set.size();
        ObjectList<HeightMap> objectlist = new ObjectArrayList(i);
        ObjectListIterator<HeightMap> objectlistiterator = objectlist.iterator();
        int j = ichunkaccess.b() + 16;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                Iterator iterator = set.iterator();

                while (iterator.hasNext()) {
                    HeightMap.Type heightmap_type = (HeightMap.Type) iterator.next();

                    objectlist.add(ichunkaccess.a(heightmap_type));
                }

                for (int i1 = j - 1; i1 >= ichunkaccess.getMinBuildHeight(); --i1) {
                    blockposition_mutableblockposition.d(k, i1, l);
                    IBlockData iblockdata = ichunkaccess.getType(blockposition_mutableblockposition);

                    if (!iblockdata.a(Blocks.AIR)) {
                        while (objectlistiterator.hasNext()) {
                            HeightMap heightmap = (HeightMap) objectlistiterator.next();

                            if (heightmap.isOpaque.test(iblockdata)) {
                                heightmap.a(k, l, i1 + 1);
                                objectlistiterator.remove();
                            }
                        }

                        if (objectlist.isEmpty()) {
                            break;
                        }

                        objectlistiterator.back(i);
                    }
                }
            }
        }

    }

    public boolean a(int i, int j, int k, IBlockData iblockdata) {
        int l = this.a(i, k);

        if (j <= l - 2) {
            return false;
        } else {
            if (this.isOpaque.test(iblockdata)) {
                if (j >= l) {
                    this.a(i, k, j + 1);
                    return true;
                }
            } else if (l - 1 == j) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int i1 = j - 1; i1 >= this.chunk.getMinBuildHeight(); --i1) {
                    blockposition_mutableblockposition.d(i, i1, k);
                    if (this.isOpaque.test(this.chunk.getType(blockposition_mutableblockposition))) {
                        this.a(i, k, i1 + 1);
                        return true;
                    }
                }

                this.a(i, k, this.chunk.getMinBuildHeight());
                return true;
            }

            return false;
        }
    }

    public int a(int i, int j) {
        return this.a(c(i, j));
    }

    public int b(int i, int j) {
        return this.a(c(i, j)) - 1;
    }

    private int a(int i) {
        return this.data.a(i) + this.chunk.getMinBuildHeight();
    }

    private void a(int i, int j, int k) {
        this.data.b(c(i, j), k - this.chunk.getMinBuildHeight());
    }

    public void a(IChunkAccess ichunkaccess, HeightMap.Type heightmap_type, long[] along) {
        long[] along1 = this.data.a();

        if (along1.length == along.length) {
            System.arraycopy(along, 0, along1, 0, along.length);
        } else {
            Logger logger = HeightMap.LOGGER;
            ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

            logger.warn("Ignoring heightmap data for chunk " + chunkcoordintpair + ", size does not match; expected: " + along1.length + ", got: " + along.length);
            a(ichunkaccess, EnumSet.of(heightmap_type));
        }
    }

    public long[] a() {
        return this.data.a();
    }

    private static int c(int i, int j) {
        return i + j * 16;
    }

    public static enum Type implements INamable {

        WORLD_SURFACE_WG("WORLD_SURFACE_WG", HeightMap.Use.WORLDGEN, HeightMap.NOT_AIR), WORLD_SURFACE("WORLD_SURFACE", HeightMap.Use.CLIENT, HeightMap.NOT_AIR), OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", HeightMap.Use.WORLDGEN, HeightMap.MATERIAL_MOTION_BLOCKING), OCEAN_FLOOR("OCEAN_FLOOR", HeightMap.Use.LIVE_WORLD, HeightMap.MATERIAL_MOTION_BLOCKING), MOTION_BLOCKING("MOTION_BLOCKING", HeightMap.Use.CLIENT, (iblockdata) -> {
            return iblockdata.getMaterial().isSolid() || !iblockdata.getFluid().isEmpty();
        }), MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", HeightMap.Use.LIVE_WORLD, (iblockdata) -> {
            return (iblockdata.getMaterial().isSolid() || !iblockdata.getFluid().isEmpty()) && !(iblockdata.getBlock() instanceof BlockLeaves);
        });

        public static final Codec<HeightMap.Type> CODEC = INamable.a(HeightMap.Type::values, HeightMap.Type::a);
        private final String serializationKey;
        private final HeightMap.Use usage;
        private final Predicate<IBlockData> isOpaque;
        private static final Map<String, HeightMap.Type> REVERSE_LOOKUP = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
            HeightMap.Type[] aheightmap_type = values();
            int i = aheightmap_type.length;

            for (int j = 0; j < i; ++j) {
                HeightMap.Type heightmap_type = aheightmap_type[j];

                hashmap.put(heightmap_type.serializationKey, heightmap_type);
            }

        });

        private Type(String s, HeightMap.Use heightmap_use, Predicate predicate) {
            this.serializationKey = s;
            this.usage = heightmap_use;
            this.isOpaque = predicate;
        }

        public String a() {
            return this.serializationKey;
        }

        public boolean b() {
            return this.usage == HeightMap.Use.CLIENT;
        }

        public boolean d() {
            return this.usage != HeightMap.Use.WORLDGEN;
        }

        @Nullable
        public static HeightMap.Type a(String s) {
            return (HeightMap.Type) HeightMap.Type.REVERSE_LOOKUP.get(s);
        }

        public Predicate<IBlockData> e() {
            return this.isOpaque;
        }

        @Override
        public String getName() {
            return this.serializationKey;
        }
    }

    public static enum Use {

        WORLDGEN, LIVE_WORLD, CLIENT;

        private Use() {}
    }
}
