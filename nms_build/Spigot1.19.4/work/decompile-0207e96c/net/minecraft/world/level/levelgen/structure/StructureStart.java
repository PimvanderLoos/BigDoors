package net.minecraft.world.level.levelgen.structure;

import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import org.slf4j.Logger;

public final class StructureStart {

    public static final String INVALID_START_ID = "INVALID";
    public static final StructureStart INVALID_START = new StructureStart((Structure) null, new ChunkCoordIntPair(0, 0), 0, new PiecesContainer(List.of()));
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Structure structure;
    private final PiecesContainer pieceContainer;
    private final ChunkCoordIntPair chunkPos;
    private int references;
    @Nullable
    private volatile StructureBoundingBox cachedBoundingBox;

    public StructureStart(Structure structure, ChunkCoordIntPair chunkcoordintpair, int i, PiecesContainer piecescontainer) {
        this.structure = structure;
        this.chunkPos = chunkcoordintpair;
        this.references = i;
        this.pieceContainer = piecescontainer;
    }

    @Nullable
    public static StructureStart loadStaticStart(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound, long i) {
        String s = nbttagcompound.getString("id");

        if ("INVALID".equals(s)) {
            return StructureStart.INVALID_START;
        } else {
            IRegistry<Structure> iregistry = structurepieceserializationcontext.registryAccess().registryOrThrow(Registries.STRUCTURE);
            Structure structure = (Structure) iregistry.get(new MinecraftKey(s));

            if (structure == null) {
                StructureStart.LOGGER.error("Unknown stucture id: {}", s);
                return null;
            } else {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(nbttagcompound.getInt("ChunkX"), nbttagcompound.getInt("ChunkZ"));
                int j = nbttagcompound.getInt("references");
                NBTTagList nbttaglist = nbttagcompound.getList("Children", 10);

                try {
                    PiecesContainer piecescontainer = PiecesContainer.load(nbttaglist, structurepieceserializationcontext);

                    if (structure instanceof OceanMonumentStructure) {
                        piecescontainer = OceanMonumentStructure.regeneratePiecesAfterLoad(chunkcoordintpair, i, piecescontainer);
                    }

                    return new StructureStart(structure, chunkcoordintpair, j, piecescontainer);
                } catch (Exception exception) {
                    StructureStart.LOGGER.error("Failed Start with id {}", s, exception);
                    return null;
                }
            }
        }
    }

    public StructureBoundingBox getBoundingBox() {
        StructureBoundingBox structureboundingbox = this.cachedBoundingBox;

        if (structureboundingbox == null) {
            structureboundingbox = this.structure.adjustBoundingBox(this.pieceContainer.calculateBoundingBox());
            this.cachedBoundingBox = structureboundingbox;
        }

        return structureboundingbox;
    }

    public void placeInChunk(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
        List<StructurePiece> list = this.pieceContainer.pieces();

        if (!list.isEmpty()) {
            StructureBoundingBox structureboundingbox1 = ((StructurePiece) list.get(0)).boundingBox;
            BlockPosition blockposition = structureboundingbox1.getCenter();
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), structureboundingbox1.minY(), blockposition.getZ());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                StructurePiece structurepiece = (StructurePiece) iterator.next();

                if (structurepiece.getBoundingBox().intersects(structureboundingbox)) {
                    structurepiece.postProcess(generatoraccessseed, structuremanager, chunkgenerator, randomsource, structureboundingbox, chunkcoordintpair, blockposition1);
                }
            }

            this.structure.afterPlace(generatoraccessseed, structuremanager, chunkgenerator, randomsource, structureboundingbox, chunkcoordintpair, this.pieceContainer);
        }
    }

    public NBTTagCompound createTag(StructurePieceSerializationContext structurepieceserializationcontext, ChunkCoordIntPair chunkcoordintpair) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.isValid()) {
            nbttagcompound.putString("id", structurepieceserializationcontext.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(this.structure).toString());
            nbttagcompound.putInt("ChunkX", chunkcoordintpair.x);
            nbttagcompound.putInt("ChunkZ", chunkcoordintpair.z);
            nbttagcompound.putInt("references", this.references);
            nbttagcompound.put("Children", this.pieceContainer.save(structurepieceserializationcontext));
            return nbttagcompound;
        } else {
            nbttagcompound.putString("id", "INVALID");
            return nbttagcompound;
        }
    }

    public boolean isValid() {
        return !this.pieceContainer.isEmpty();
    }

    public ChunkCoordIntPair getChunkPos() {
        return this.chunkPos;
    }

    public boolean canBeReferenced() {
        return this.references < this.getMaxReferences();
    }

    public void addReference() {
        ++this.references;
    }

    public int getReferences() {
        return this.references;
    }

    protected int getMaxReferences() {
        return 1;
    }

    public Structure getStructure() {
        return this.structure;
    }

    public List<StructurePiece> getPieces() {
        return this.pieceContainer.pieces();
    }
}
