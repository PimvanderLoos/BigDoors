package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public abstract class StructureStart<C extends WorldGenFeatureConfiguration> {

    public static final StructureStart<?> a = new StructureStart<WorldGenMineshaftConfiguration>(StructureGenerator.MINESHAFT, 0, 0, StructureBoundingBox.a(), 0, 0L) {
        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenMineshaftConfiguration worldgenmineshaftconfiguration) {}
    };
    private final StructureGenerator<C> e;
    protected final List<StructurePiece> b = Lists.newArrayList();
    protected StructureBoundingBox c;
    private final int f;
    private final int g;
    private int h;
    protected final SeededRandom d;

    public StructureStart(StructureGenerator<C> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
        this.e = structuregenerator;
        this.f = i;
        this.g = j;
        this.h = k;
        this.d = new SeededRandom();
        this.d.c(l, i, j);
        this.c = structureboundingbox;
    }

    public abstract void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, C c0);

    public StructureBoundingBox c() {
        return this.c;
    }

    public List<StructurePiece> d() {
        return this.b;
    }

    public void a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
        List list = this.b;

        synchronized (this.b) {
            if (!this.b.isEmpty()) {
                StructureBoundingBox structureboundingbox1 = ((StructurePiece) this.b.get(0)).n;
                BaseBlockPosition baseblockposition = structureboundingbox1.g();
                BlockPosition blockposition = new BlockPosition(baseblockposition.getX(), structureboundingbox1.b, baseblockposition.getZ());
                Iterator iterator = this.b.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    if (structurepiece.g().b(structureboundingbox) && !structurepiece.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition)) {
                        iterator.remove();
                    }
                }

                this.b();
            }
        }
    }

    protected void b() {
        this.c = StructureBoundingBox.a();
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            this.c.c(structurepiece.g());
        }

    }

    public NBTTagCompound a(int i, int j) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.e()) {
            nbttagcompound.setString("id", IRegistry.STRUCTURE_FEATURE.getKey(this.l()).toString());
            nbttagcompound.setInt("ChunkX", i);
            nbttagcompound.setInt("ChunkZ", j);
            nbttagcompound.setInt("references", this.h);
            nbttagcompound.set("BB", this.c.h());
            NBTTagList nbttaglist = new NBTTagList();
            List list = this.b;

            synchronized (this.b) {
                Iterator iterator = this.b.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    nbttaglist.add(structurepiece.f());
                }
            }

            nbttagcompound.set("Children", nbttaglist);
            return nbttagcompound;
        } else {
            nbttagcompound.setString("id", "INVALID");
            return nbttagcompound;
        }
    }

    protected void a(int i, Random random, int j) {
        int k = i - j;
        int l = this.c.e() + 1;

        if (l < k) {
            l += random.nextInt(k - l);
        }

        int i1 = l - this.c.e;

        this.c.a(0, i1, 0);
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            structurepiece.a(0, i1, 0);
        }

    }

    protected void a(Random random, int i, int j) {
        int k = j - i + 1 - this.c.e();
        int l;

        if (k > 1) {
            l = i + random.nextInt(k);
        } else {
            l = i;
        }

        int i1 = l - this.c.b;

        this.c.a(0, i1, 0);
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            structurepiece.a(0, i1, 0);
        }

    }

    public boolean e() {
        return !this.b.isEmpty();
    }

    public int f() {
        return this.f;
    }

    public int g() {
        return this.g;
    }

    public BlockPosition a() {
        return new BlockPosition(this.f << 4, 0, this.g << 4);
    }

    public boolean h() {
        return this.h < this.k();
    }

    public void i() {
        ++this.h;
    }

    public int j() {
        return this.h;
    }

    protected int k() {
        return 1;
    }

    public StructureGenerator<?> l() {
        return this.e;
    }
}
