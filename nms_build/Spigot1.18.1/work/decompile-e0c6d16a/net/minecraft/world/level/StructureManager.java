package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IStructureAccess;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureManager {

    private final GeneratorAccess level;
    private final GeneratorSettings worldGenSettings;
    private final StructureCheck structureCheck;

    public StructureManager(GeneratorAccess generatoraccess, GeneratorSettings generatorsettings, StructureCheck structurecheck) {
        this.level = generatoraccess;
        this.worldGenSettings = generatorsettings;
        this.structureCheck = structurecheck;
    }

    public StructureManager forWorldGenRegion(RegionLimitedWorldAccess regionlimitedworldaccess) {
        if (regionlimitedworldaccess.getLevel() != this.level) {
            WorldServer worldserver = regionlimitedworldaccess.getLevel();

            throw new IllegalStateException("Using invalid feature manager (source level: " + worldserver + ", region: " + regionlimitedworldaccess);
        } else {
            return new StructureManager(regionlimitedworldaccess, this.worldGenSettings, this.structureCheck);
        }
    }

    public List<? extends StructureStart<?>> startsForFeature(SectionPosition sectionposition, StructureGenerator<?> structuregenerator) {
        LongSet longset = this.level.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(structuregenerator);
        Builder<StructureStart<?>> builder = ImmutableList.builder();
        LongIterator longiterator = longset.iterator();

        while (longiterator.hasNext()) {
            long i = (Long) longiterator.next();
            SectionPosition sectionposition1 = SectionPosition.of(new ChunkCoordIntPair(i), this.level.getMinSection());
            StructureStart<?> structurestart = this.getStartForFeature(sectionposition1, structuregenerator, this.level.getChunk(sectionposition1.x(), sectionposition1.z(), ChunkStatus.STRUCTURE_STARTS));

            if (structurestart != null && structurestart.isValid()) {
                builder.add(structurestart);
            }
        }

        return builder.build();
    }

    @Nullable
    public StructureStart<?> getStartForFeature(SectionPosition sectionposition, StructureGenerator<?> structuregenerator, IStructureAccess istructureaccess) {
        return istructureaccess.getStartForFeature(structuregenerator);
    }

    public void setStartForFeature(SectionPosition sectionposition, StructureGenerator<?> structuregenerator, StructureStart<?> structurestart, IStructureAccess istructureaccess) {
        istructureaccess.setStartForFeature(structuregenerator, structurestart);
    }

    public void addReferenceForFeature(SectionPosition sectionposition, StructureGenerator<?> structuregenerator, long i, IStructureAccess istructureaccess) {
        istructureaccess.addReferenceForFeature(structuregenerator, i);
    }

    public boolean shouldGenerateFeatures() {
        return this.worldGenSettings.generateFeatures();
    }

    public StructureStart<?> getStructureAt(BlockPosition blockposition, StructureGenerator<?> structuregenerator) {
        Iterator iterator = this.startsForFeature(SectionPosition.of(blockposition), structuregenerator).iterator();

        StructureStart structurestart;

        do {
            if (!iterator.hasNext()) {
                return StructureStart.INVALID_START;
            }

            structurestart = (StructureStart) iterator.next();
        } while (!structurestart.getBoundingBox().isInside(blockposition));

        return structurestart;
    }

    public StructureStart<?> getStructureWithPieceAt(BlockPosition blockposition, StructureGenerator<?> structuregenerator) {
        Iterator iterator = this.startsForFeature(SectionPosition.of(blockposition), structuregenerator).iterator();

        while (iterator.hasNext()) {
            StructureStart<?> structurestart = (StructureStart) iterator.next();
            Iterator iterator1 = structurestart.getPieces().iterator();

            while (iterator1.hasNext()) {
                StructurePiece structurepiece = (StructurePiece) iterator1.next();

                if (structurepiece.getBoundingBox().isInside(blockposition)) {
                    return structurestart;
                }
            }
        }

        return StructureStart.INVALID_START;
    }

    public boolean hasAnyStructureAt(BlockPosition blockposition) {
        SectionPosition sectionposition = SectionPosition.of(blockposition);

        return this.level.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.STRUCTURE_REFERENCES).hasAnyStructureReferences();
    }

    public StructureCheckResult checkStructurePresence(ChunkCoordIntPair chunkcoordintpair, StructureGenerator<?> structuregenerator, boolean flag) {
        return this.structureCheck.checkStart(chunkcoordintpair, structuregenerator, flag);
    }

    public void addReference(StructureStart<?> structurestart) {
        structurestart.addReference();
        this.structureCheck.incrementReference(structurestart.getChunkPos(), structurestart.getFeature());
    }
}
