package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;

public abstract class StructureAbstract<C extends WorldGenFeatureConfiguration> extends StructureStart<C> {

    public StructureAbstract(StructureGenerator<C> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
        super(structuregenerator, i, j, structureboundingbox, k, l);
    }

    @Override
    protected void b() {
        super.b();
        boolean flag = true;

        this.c.a -= 12;
        this.c.b -= 12;
        this.c.c -= 12;
        this.c.d += 12;
        this.c.e += 12;
        this.c.f += 12;
    }
}
