package net.minecraft.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class WorldGenMineshaft extends StructureGenerator {

    private double a = 0.004D;

    public WorldGenMineshaft() {}

    public String a() {
        return "Mineshaft";
    }

    public WorldGenMineshaft(Map<String, String> map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).equals("chance")) {
                this.a = MathHelper.a((String) entry.getValue(), this.a);
            }
        }

    }

    protected boolean a(int i, int j) {
        return this.f.nextDouble() < this.a && this.f.nextInt(80) < Math.max(Math.abs(i), Math.abs(j));
    }

    public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition, boolean flag) {
        boolean flag1 = true;
        int i = blockposition.getX() >> 4;
        int j = blockposition.getZ() >> 4;

        for (int k = 0; k <= 1000; ++k) {
            for (int l = -k; l <= k; ++l) {
                boolean flag2 = l == -k || l == k;

                for (int i1 = -k; i1 <= k; ++i1) {
                    boolean flag3 = i1 == -k || i1 == k;

                    if (flag2 || flag3) {
                        int j1 = i + l;
                        int k1 = j + i1;

                        this.f.setSeed((long) (j1 ^ k1) ^ world.getSeed());
                        this.f.nextInt();
                        if (this.a(j1, k1) && (!flag || !world.b(j1, k1))) {
                            return new BlockPosition((j1 << 4) + 8, 64, (k1 << 4) + 8);
                        }
                    }
                }
            }
        }

        return null;
    }

    protected StructureStart b(int i, int j) {
        BiomeBase biomebase = this.g.getBiome(new BlockPosition((i << 4) + 8, 64, (j << 4) + 8));
        WorldGenMineshaft.Type worldgenmineshaft_type = biomebase instanceof BiomeMesa ? WorldGenMineshaft.Type.MESA : WorldGenMineshaft.Type.NORMAL;

        return new WorldGenMineshaftStart(this.g, this.f, i, j, worldgenmineshaft_type);
    }

    public static enum Type {

        NORMAL, MESA;

        private Type() {}

        public static WorldGenMineshaft.Type a(int i) {
            return i >= 0 && i < values().length ? values()[i] : WorldGenMineshaft.Type.NORMAL;
        }
    }
}
