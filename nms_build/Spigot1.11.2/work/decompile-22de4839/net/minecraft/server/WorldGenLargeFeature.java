package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class WorldGenLargeFeature extends StructureGenerator {

    private static final List<BiomeBase> a = Arrays.asList(new BiomeBase[] { Biomes.d, Biomes.s, Biomes.w, Biomes.x, Biomes.h, Biomes.n, Biomes.F});
    private final List<BiomeBase.BiomeMeta> b;
    private int d;
    private final int h;

    public WorldGenLargeFeature() {
        this.b = Lists.newArrayList();
        this.d = 32;
        this.h = 8;
        this.b.add(new BiomeBase.BiomeMeta(EntityWitch.class, 1, 1, 1));
    }

    public WorldGenLargeFeature(Map<String, String> map) {
        this();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).equals("distance")) {
                this.d = MathHelper.a((String) entry.getValue(), this.d, 9);
            }
        }

    }

    public String a() {
        return "Temple";
    }

    protected boolean a(int i, int j) {
        int k = i;
        int l = j;

        if (i < 0) {
            i -= this.d - 1;
        }

        if (j < 0) {
            j -= this.d - 1;
        }

        int i1 = i / this.d;
        int j1 = j / this.d;
        Random random = this.g.a(i1, j1, 14357617);

        i1 *= this.d;
        j1 *= this.d;
        i1 += random.nextInt(this.d - 8);
        j1 += random.nextInt(this.d - 8);
        if (k == i1 && l == j1) {
            BiomeBase biomebase = this.g.getWorldChunkManager().getBiome(new BlockPosition(k * 16 + 8, 0, l * 16 + 8));

            if (biomebase == null) {
                return false;
            }

            Iterator iterator = WorldGenLargeFeature.a.iterator();

            while (iterator.hasNext()) {
                BiomeBase biomebase1 = (BiomeBase) iterator.next();

                if (biomebase == biomebase1) {
                    return true;
                }
            }
        }

        return false;
    }

    public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition, boolean flag) {
        this.g = world;
        return a(world, this, blockposition, this.d, 8, 14357617, false, 100, flag);
    }

    protected StructureStart b(int i, int j) {
        return new WorldGenLargeFeature.WorldGenLargeFeatureStart(this.g, this.f, i, j);
    }

    public boolean a(BlockPosition blockposition) {
        StructureStart structurestart = this.c(blockposition);

        if (structurestart != null && structurestart instanceof WorldGenLargeFeature.WorldGenLargeFeatureStart && !structurestart.a.isEmpty()) {
            StructurePiece structurepiece = (StructurePiece) structurestart.a.get(0);

            return structurepiece instanceof WorldGenRegistration.WorldGenWitchHut;
        } else {
            return false;
        }
    }

    public List<BiomeBase.BiomeMeta> b() {
        return this.b;
    }

    public static class WorldGenLargeFeatureStart extends StructureStart {

        public WorldGenLargeFeatureStart() {}

        public WorldGenLargeFeatureStart(World world, Random random, int i, int j) {
            this(world, random, i, j, world.getBiome(new BlockPosition(i * 16 + 8, 0, j * 16 + 8)));
        }

        public WorldGenLargeFeatureStart(World world, Random random, int i, int j, BiomeBase biomebase) {
            super(i, j);
            if (biomebase != Biomes.w && biomebase != Biomes.x) {
                if (biomebase == Biomes.h) {
                    WorldGenRegistration.WorldGenWitchHut worldgenregistration_worldgenwitchhut = new WorldGenRegistration.WorldGenWitchHut(random, i * 16, j * 16);

                    this.a.add(worldgenregistration_worldgenwitchhut);
                } else if (biomebase != Biomes.d && biomebase != Biomes.s) {
                    if (biomebase == Biomes.n || biomebase == Biomes.F) {
                        WorldGenRegistration.b worldgenregistration_b = new WorldGenRegistration.b(random, i * 16, j * 16);

                        this.a.add(worldgenregistration_b);
                    }
                } else {
                    WorldGenRegistration.WorldGenPyramidPiece worldgenregistration_worldgenpyramidpiece = new WorldGenRegistration.WorldGenPyramidPiece(random, i * 16, j * 16);

                    this.a.add(worldgenregistration_worldgenpyramidpiece);
                }
            } else {
                WorldGenRegistration.WorldGenJungleTemple worldgenregistration_worldgenjungletemple = new WorldGenRegistration.WorldGenJungleTemple(random, i * 16, j * 16);

                this.a.add(worldgenregistration_worldgenjungletemple);
            }

            this.d();
        }
    }
}
