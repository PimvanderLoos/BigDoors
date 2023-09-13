package net.minecraft.server;

import java.util.stream.Stream;

public interface GeneratorAccessSeed extends WorldAccess {

    long getSeed();

    Stream<? extends StructureStart<?>> a(SectionPosition sectionposition, StructureGenerator<?> structuregenerator);
}
