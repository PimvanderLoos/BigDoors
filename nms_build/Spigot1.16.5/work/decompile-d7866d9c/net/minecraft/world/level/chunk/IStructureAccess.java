package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface IStructureAccess {

    @Nullable
    StructureStart<?> a(StructureGenerator<?> structuregenerator);

    void a(StructureGenerator<?> structuregenerator, StructureStart<?> structurestart);

    LongSet b(StructureGenerator<?> structuregenerator);

    void a(StructureGenerator<?> structuregenerator, long i);

    Map<StructureGenerator<?>, LongSet> v();

    void b(Map<StructureGenerator<?>, LongSet> map);
}
