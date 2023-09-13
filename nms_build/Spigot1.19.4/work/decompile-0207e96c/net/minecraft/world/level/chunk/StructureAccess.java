package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface StructureAccess {

    @Nullable
    StructureStart getStartForStructure(Structure structure);

    void setStartForStructure(Structure structure, StructureStart structurestart);

    LongSet getReferencesForStructure(Structure structure);

    void addReferenceForStructure(Structure structure, long i);

    Map<Structure, LongSet> getAllReferences();

    void setAllReferences(Map<Structure, LongSet> map);
}
