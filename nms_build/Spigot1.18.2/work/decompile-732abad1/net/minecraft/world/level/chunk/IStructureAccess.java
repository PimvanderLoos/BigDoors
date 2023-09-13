package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface IStructureAccess {

    @Nullable
    StructureStart getStartForFeature(StructureFeature<?, ?> structurefeature);

    void setStartForFeature(StructureFeature<?, ?> structurefeature, StructureStart structurestart);

    LongSet getReferencesForFeature(StructureFeature<?, ?> structurefeature);

    void addReferenceForFeature(StructureFeature<?, ?> structurefeature, long i);

    Map<StructureFeature<?, ?>, LongSet> getAllReferences();

    void setAllReferences(Map<StructureFeature<?, ?>, LongSet> map);
}
