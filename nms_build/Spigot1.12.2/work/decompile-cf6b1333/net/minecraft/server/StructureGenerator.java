package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class StructureGenerator extends WorldGenBase {

    private PersistentStructure a;
    protected Long2ObjectMap<StructureStart> c = new Long2ObjectOpenHashMap(1024);

    public StructureGenerator() {}

    public abstract String a();

    protected final synchronized void a(World world, final int i, final int j, int k, int l, ChunkSnapshot chunksnapshot) {
        this.a(world);
        if (!this.c.containsKey(ChunkCoordIntPair.a(i, j))) {
            this.f.nextInt();

            try {
                if (this.a(i, j)) {
                    StructureStart structurestart = this.b(i, j);

                    this.c.put(ChunkCoordIntPair.a(i, j), structurestart);
                    if (structurestart.a()) {
                        this.a(i, j, structurestart);
                    }
                }

            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception preparing structure feature");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Feature being prepared");

                crashreportsystemdetails.a("Is feature chunk", new CrashReportCallable() {
                    public String a() throws Exception {
                        return StructureGenerator.this.a(i, j) ? "True" : "False";
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                crashreportsystemdetails.a("Chunk location", (Object) String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j)}));
                crashreportsystemdetails.a("Chunk pos hash", new CrashReportCallable() {
                    public String a() throws Exception {
                        return String.valueOf(ChunkCoordIntPair.a(i, j));
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                crashreportsystemdetails.a("Structure type", new CrashReportCallable() {
                    public String a() throws Exception {
                        return StructureGenerator.this.getClass().getCanonicalName();
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public synchronized boolean a(World world, Random random, ChunkCoordIntPair chunkcoordintpair) {
        this.a(world);
        int i = (chunkcoordintpair.x << 4) + 8;
        int j = (chunkcoordintpair.z << 4) + 8;
        boolean flag = false;
        ObjectIterator objectiterator = this.c.values().iterator();

        while (objectiterator.hasNext()) {
            StructureStart structurestart = (StructureStart) objectiterator.next();

            if (structurestart.a() && structurestart.a(chunkcoordintpair) && structurestart.b().a(i, j, i + 15, j + 15)) {
                structurestart.a(world, random, new StructureBoundingBox(i, j, i + 15, j + 15));
                structurestart.b(chunkcoordintpair);
                flag = true;
                this.a(structurestart.e(), structurestart.f(), structurestart);
            }
        }

        return flag;
    }

    public boolean b(BlockPosition blockposition) {
        if (this.g == null) {
            return false;
        } else {
            this.a(this.g);
            return this.c(blockposition) != null;
        }
    }

    @Nullable
    protected StructureStart c(BlockPosition blockposition) {
        ObjectIterator objectiterator = this.c.values().iterator();

        while (objectiterator.hasNext()) {
            StructureStart structurestart = (StructureStart) objectiterator.next();

            if (structurestart.a() && structurestart.b().b((BaseBlockPosition) blockposition)) {
                Iterator iterator = structurestart.c().iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    if (structurepiece.d().b((BaseBlockPosition) blockposition)) {
                        return structurestart;
                    }
                }
            }
        }

        return null;
    }

    public boolean a(World world, BlockPosition blockposition) {
        this.a(world);
        ObjectIterator objectiterator = this.c.values().iterator();

        StructureStart structurestart;

        do {
            if (!objectiterator.hasNext()) {
                return false;
            }

            structurestart = (StructureStart) objectiterator.next();
        } while (!structurestart.a() || !structurestart.b().b((BaseBlockPosition) blockposition));

        return true;
    }

    @Nullable
    public abstract BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition, boolean flag);

    protected void a(World world) {
        if (this.a == null && world != null) {
            this.a = (PersistentStructure) world.a(PersistentStructure.class, this.a());
            if (this.a == null) {
                this.a = new PersistentStructure(this.a());
                world.a(this.a(), (PersistentBase) this.a);
            } else {
                NBTTagCompound nbttagcompound = this.a.a();
                Iterator iterator = nbttagcompound.c().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    NBTBase nbtbase = nbttagcompound.get(s);

                    if (nbtbase.getTypeId() == 10) {
                        NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;

                        if (nbttagcompound1.hasKey("ChunkX") && nbttagcompound1.hasKey("ChunkZ")) {
                            int i = nbttagcompound1.getInt("ChunkX");
                            int j = nbttagcompound1.getInt("ChunkZ");
                            StructureStart structurestart = WorldGenFactory.a(nbttagcompound1, world);

                            if (structurestart != null) {
                                this.c.put(ChunkCoordIntPair.a(i, j), structurestart);
                            }
                        }
                    }
                }
            }
        }

    }

    private void a(int i, int j, StructureStart structurestart) {
        this.a.a(structurestart.a(i, j), i, j);
        this.a.c();
    }

    protected abstract boolean a(int i, int j);

    protected abstract StructureStart b(int i, int j);

    protected static BlockPosition a(World world, StructureGenerator structuregenerator, BlockPosition blockposition, int i, int j, int k, boolean flag, int l, boolean flag1) {
        int i1 = blockposition.getX() >> 4;
        int j1 = blockposition.getZ() >> 4;
        int k1 = 0;

        for (Random random = new Random(); k1 <= l; ++k1) {
            for (int l1 = -k1; l1 <= k1; ++l1) {
                boolean flag2 = l1 == -k1 || l1 == k1;

                for (int i2 = -k1; i2 <= k1; ++i2) {
                    boolean flag3 = i2 == -k1 || i2 == k1;

                    if (flag2 || flag3) {
                        int j2 = i1 + i * l1;
                        int k2 = j1 + i * i2;

                        if (j2 < 0) {
                            j2 -= i - 1;
                        }

                        if (k2 < 0) {
                            k2 -= i - 1;
                        }

                        int l2 = j2 / i;
                        int i3 = k2 / i;
                        Random random1 = world.a(l2, i3, k);

                        l2 *= i;
                        i3 *= i;
                        if (flag) {
                            l2 += (random1.nextInt(i - j) + random1.nextInt(i - j)) / 2;
                            i3 += (random1.nextInt(i - j) + random1.nextInt(i - j)) / 2;
                        } else {
                            l2 += random1.nextInt(i - j);
                            i3 += random1.nextInt(i - j);
                        }

                        WorldGenBase.a(world.getSeed(), random, l2, i3);
                        random.nextInt();
                        if (structuregenerator.a(l2, i3)) {
                            if (!flag1 || !world.b(l2, i3)) {
                                return new BlockPosition((l2 << 4) + 8, 64, (i3 << 4) + 8);
                            }
                        } else if (k1 == 0) {
                            break;
                        }
                    }
                }

                if (k1 == 0) {
                    break;
                }
            }
        }

        return null;
    }
}
