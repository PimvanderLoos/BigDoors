package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public final class StructureStart {

    public static final String INVALID_START_ID = "INVALID";
    public static final StructureStart INVALID_START = new StructureStart((StructureFeature) null, new ChunkCoordIntPair(0, 0), 0, new PiecesContainer(List.of()));
    private final StructureFeature<?, ?> feature;
    private final PiecesContainer pieceContainer;
    private final ChunkCoordIntPair chunkPos;
    private int references;
    @Nullable
    private volatile StructureBoundingBox cachedBoundingBox;

    public StructureStart(StructureFeature<?, ?> structurefeature, ChunkCoordIntPair chunkcoordintpair, int i, PiecesContainer piecescontainer) {
        this.feature = structurefeature;
        this.chunkPos = chunkcoordintpair;
        this.references = i;
        this.pieceContainer = piecescontainer;
    }

    public StructureBoundingBox getBoundingBox() {
        StructureBoundingBox structureboundingbox = this.cachedBoundingBox;

        if (structureboundingbox == null) {
            structureboundingbox = this.feature.adjustBoundingBox(this.pieceContainer.calculateBoundingBox());
            this.cachedBoundingBox = structureboundingbox;
        }

        return structureboundingbox;
    }

    public void placeInChunk(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
        List<StructurePiece> list = this.pieceContainer.pieces();

        if (!list.isEmpty()) {
            StructureBoundingBox structureboundingbox1 = ((StructurePiece) list.get(0)).boundingBox;
            BlockPosition blockposition = structureboundingbox1.getCenter();
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), structureboundingbox1.minY(), blockposition.getZ());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                StructurePiece structurepiece = (StructurePiece) iterator.next();

                if (structurepiece.getBoundingBox().intersects(structureboundingbox)) {
                    structurepiece.postProcess(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition1);
                }
            }

            this.feature.feature.getPostPlacementProcessor().afterPlace(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, this.pieceContainer);
        }
    }

    public NBTTagCompound createTag(StructurePieceSerializationContext structurepieceserializationcontext, ChunkCoordIntPair chunkcoordintpair) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.isValid()) {
            nbttagcompound.putString("id", structurepieceserializationcontext.registryAccess().registryOrThrow(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).getKey(this.feature).toString());
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

    public StructureFeature<?, ?> getFeature() {
        return this.feature;
    }

    public List<StructurePiece> getPieces() {
        return this.pieceContainer.pieces();
    }
}
