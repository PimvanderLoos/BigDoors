package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.SectionPosition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IStructureAccess;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
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

    public List<StructureStart> startsForFeature(SectionPosition sectionposition, Predicate<StructureFeature<?, ?>> predicate) {
        Map<StructureFeature<?, ?>, LongSet> map = this.level.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
        Builder<StructureStart> builder = ImmutableList.builder();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StructureFeature<?, ?>, LongSet> entry = (Entry) iterator.next();
            StructureFeature<?, ?> structurefeature = (StructureFeature) entry.getKey();

            if (predicate.test(structurefeature)) {
                LongSet longset = (LongSet) entry.getValue();

                Objects.requireNonNull(builder);
                this.fillStartsForFeature(structurefeature, longset, builder::add);
            }
        }

        return builder.build();
    }

    public List<StructureStart> startsForFeature(SectionPosition sectionposition, StructureFeature<?, ?> structurefeature) {
        LongSet longset = this.level.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(structurefeature);
        Builder<StructureStart> builder = ImmutableList.builder();

        Objects.requireNonNull(builder);
        this.fillStartsForFeature(structurefeature, longset, builder::add);
        return builder.build();
    }

    public void fillStartsForFeature(StructureFeature<?, ?> structurefeature, LongSet longset, Consumer<StructureStart> consumer) {
        LongIterator longiterator = longset.iterator();

        while (longiterator.hasNext()) {
            long i = (Long) longiterator.next();
            SectionPosition sectionposition = SectionPosition.of(new ChunkCoordIntPair(i), this.level.getMinSection());
            StructureStart structurestart = this.getStartForFeature(sectionposition, structurefeature, this.level.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.STRUCTURE_STARTS));

            if (structurestart != null && structurestart.isValid()) {
                consumer.accept(structurestart);
            }
        }

    }

    @Nullable
    public StructureStart getStartForFeature(SectionPosition sectionposition, StructureFeature<?, ?> structurefeature, IStructureAccess istructureaccess) {
        return istructureaccess.getStartForFeature(structurefeature);
    }

    public void setStartForFeature(SectionPosition sectionposition, StructureFeature<?, ?> structurefeature, StructureStart structurestart, IStructureAccess istructureaccess) {
        istructureaccess.setStartForFeature(structurefeature, structurestart);
    }

    public void addReferenceForFeature(SectionPosition sectionposition, StructureFeature<?, ?> structurefeature, long i, IStructureAccess istructureaccess) {
        istructureaccess.addReferenceForFeature(structurefeature, i);
    }

    public boolean shouldGenerateFeatures() {
        return this.worldGenSettings.generateFeatures();
    }

    public StructureStart getStructureAt(BlockPosition blockposition, StructureFeature<?, ?> structurefeature) {
        Iterator iterator = this.startsForFeature(SectionPosition.of(blockposition), structurefeature).iterator();

        StructureStart structurestart;

        do {
            if (!iterator.hasNext()) {
                return StructureStart.INVALID_START;
            }

            structurestart = (StructureStart) iterator.next();
        } while (!structurestart.getBoundingBox().isInside(blockposition));

        return structurestart;
    }

    public StructureStart getStructureWithPieceAt(BlockPosition blockposition, ResourceKey<StructureFeature<?, ?>> resourcekey) {
        StructureFeature<?, ?> structurefeature = (StructureFeature) this.registryAccess().registryOrThrow(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(resourcekey);

        return structurefeature == null ? StructureStart.INVALID_START : this.getStructureWithPieceAt(blockposition, structurefeature);
    }

    public StructureStart getStructureWithPieceAt(BlockPosition blockposition, StructureFeature<?, ?> structurefeature) {
        Iterator iterator = this.startsForFeature(SectionPosition.of(blockposition), structurefeature).iterator();

        StructureStart structurestart;

        do {
            if (!iterator.hasNext()) {
                return StructureStart.INVALID_START;
            }

            structurestart = (StructureStart) iterator.next();
        } while (!this.structureHasPieceAt(blockposition, structurestart));

        return structurestart;
    }

    public boolean structureHasPieceAt(BlockPosition blockposition, StructureStart structurestart) {
        Iterator iterator = structurestart.getPieces().iterator();

        StructurePiece structurepiece;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            structurepiece = (StructurePiece) iterator.next();
        } while (!structurepiece.getBoundingBox().isInside(blockposition));

        return true;
    }

    public boolean hasAnyStructureAt(BlockPosition blockposition) {
        SectionPosition sectionposition = SectionPosition.of(blockposition);

        return this.level.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.STRUCTURE_REFERENCES).hasAnyStructureReferences();
    }

    public Map<StructureFeature<?, ?>, LongSet> getAllStructuresAt(BlockPosition blockposition) {
        SectionPosition sectionposition = SectionPosition.of(blockposition);

        return this.level.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
    }

    public StructureCheckResult checkStructurePresence(ChunkCoordIntPair chunkcoordintpair, StructureFeature<?, ?> structurefeature, boolean flag) {
        return this.structureCheck.checkStart(chunkcoordintpair, structurefeature, flag);
    }

    public void addReference(StructureStart structurestart) {
        structurestart.addReference();
        this.structureCheck.incrementReference(structurestart.getChunkPos(), structurestart.getFeature());
    }

    public IRegistryCustom registryAccess() {
        return this.level.registryAccess();
    }
}
