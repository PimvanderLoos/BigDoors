package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureStart<C extends WorldGenFeatureConfiguration> implements StructurePieceAccessor {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String INVALID_START_ID = "INVALID";
    public static final StructureStart<?> INVALID_START = new StructureStart<WorldGenMineshaftConfiguration>((StructureGenerator) null, new ChunkCoordIntPair(0, 0), 0, 0L) {
        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenMineshaftConfiguration worldgenmineshaftconfiguration, LevelHeightAccessor levelheightaccessor) {}

        @Override
        public boolean e() {
            return false;
        }
    };
    private final StructureGenerator<C> feature;
    protected final List<StructurePiece> pieces = Lists.newArrayList();
    private final ChunkCoordIntPair chunkPos;
    private int references;
    protected final SeededRandom random;
    @Nullable
    private StructureBoundingBox cachedBoundingBox;

    public StructureStart(StructureGenerator<C> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
        this.feature = structuregenerator;
        this.chunkPos = chunkcoordintpair;
        this.references = i;
        this.random = new SeededRandom();
        this.random.c(j, chunkcoordintpair.x, chunkcoordintpair.z);
    }

    public abstract void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, C c0, LevelHeightAccessor levelheightaccessor);

    public final StructureBoundingBox c() {
        if (this.cachedBoundingBox == null) {
            this.cachedBoundingBox = this.b();
        }

        return this.cachedBoundingBox;
    }

    protected StructureBoundingBox b() {
        List list = this.pieces;

        synchronized (this.pieces) {
            Stream stream = this.pieces.stream().map(StructurePiece::f);

            Objects.requireNonNull(stream);
            return (StructureBoundingBox) StructureBoundingBox.b(stream::iterator).orElseThrow(() -> {
                return new IllegalStateException("Unable to calculate boundingbox without pieces");
            });
        }
    }

    public List<StructurePiece> d() {
        return this.pieces;
    }

    public void a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
        List list = this.pieces;

        synchronized (this.pieces) {
            if (!this.pieces.isEmpty()) {
                StructureBoundingBox structureboundingbox1 = ((StructurePiece) this.pieces.get(0)).boundingBox;
                BlockPosition blockposition = structureboundingbox1.f();
                BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), structureboundingbox1.h(), blockposition.getZ());
                Iterator iterator = this.pieces.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    if (structurepiece.f().a(structureboundingbox) && !structurepiece.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition1)) {
                        iterator.remove();
                    }
                }

            }
        }
    }

    public NBTTagCompound a(WorldServer worldserver, ChunkCoordIntPair chunkcoordintpair) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.e()) {
            nbttagcompound.setString("id", IRegistry.STRUCTURE_FEATURE.getKey(this.k()).toString());
            nbttagcompound.setInt("ChunkX", chunkcoordintpair.x);
            nbttagcompound.setInt("ChunkZ", chunkcoordintpair.z);
            nbttagcompound.setInt("references", this.references);
            NBTTagList nbttaglist = new NBTTagList();
            List list = this.pieces;

            synchronized (this.pieces) {
                Iterator iterator = this.pieces.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    nbttaglist.add(structurepiece.a(worldserver));
                }
            }

            nbttagcompound.set("Children", nbttaglist);
            return nbttagcompound;
        } else {
            nbttagcompound.setString("id", "INVALID");
            return nbttagcompound;
        }
    }

    protected void a(int i, int j, Random random, int k) {
        int l = i - k;
        StructureBoundingBox structureboundingbox = this.c();
        int i1 = structureboundingbox.d() + j + 1;

        if (i1 < l) {
            i1 += random.nextInt(l - i1);
        }

        int j1 = i1 - structureboundingbox.k();

        this.a(j1);
    }

    protected void a(Random random, int i, int j) {
        StructureBoundingBox structureboundingbox = this.c();
        int k = j - i + 1 - structureboundingbox.d();
        int l;

        if (k > 1) {
            l = i + random.nextInt(k);
        } else {
            l = i;
        }

        int i1 = l - structureboundingbox.h();

        this.a(i1);
    }

    protected void a(int i) {
        Iterator iterator = this.pieces.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            structurepiece.a(0, i, 0);
        }

        this.n();
    }

    private void n() {
        this.cachedBoundingBox = null;
    }

    public boolean e() {
        return !this.pieces.isEmpty();
    }

    public ChunkCoordIntPair f() {
        return this.chunkPos;
    }

    public BlockPosition a() {
        return new BlockPosition(this.chunkPos.d(), 0, this.chunkPos.e());
    }

    public boolean g() {
        return this.references < this.j();
    }

    public void h() {
        ++this.references;
    }

    public int i() {
        return this.references;
    }

    protected int j() {
        return 1;
    }

    public StructureGenerator<?> k() {
        return this.feature;
    }

    @Override
    public void a(StructurePiece structurepiece) {
        this.pieces.add(structurepiece);
        this.n();
    }

    @Nullable
    @Override
    public StructurePiece a(StructureBoundingBox structureboundingbox) {
        return a(this.pieces, structureboundingbox);
    }

    public void l() {
        this.pieces.clear();
        this.n();
    }

    public boolean m() {
        return this.pieces.isEmpty();
    }

    @Nullable
    public static StructurePiece a(List<StructurePiece> list, StructureBoundingBox structureboundingbox) {
        Iterator iterator = list.iterator();

        StructurePiece structurepiece;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            structurepiece = (StructurePiece) iterator.next();
        } while (!structurepiece.f().a(structureboundingbox));

        return structurepiece;
    }

    protected boolean a(BlockPosition blockposition) {
        Iterator iterator = this.pieces.iterator();

        StructurePiece structurepiece;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            structurepiece = (StructurePiece) iterator.next();
        } while (!structurepiece.f().b((BaseBlockPosition) blockposition));

        return true;
    }
}
