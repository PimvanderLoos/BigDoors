package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface IStructureAccess {

    @Nullable
    StructureStart<?> getStartForFeature(StructureGenerator<?> structuregenerator);

    void setStartForFeature(StructureGenerator<?> structuregenerator, StructureStart<?> structurestart);

    LongSet getReferencesForFeature(StructureGenerator<?> structuregenerator);

    void addReferenceForFeature(StructureGenerator<?> structuregenerator, long i);

    Map<StructureGenerator<?>, LongSet> getAllReferences();

    void setAllReferences(Map<StructureGenerator<?>, LongSet> map);
}
