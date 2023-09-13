package net.minecraft.world.level;

import com.mojang.datafixers.DataFixUtils;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IStructureAccess;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureManager {

    private final GeneratorAccess level;
    private final GeneratorSettings worldGenSettings;

    public StructureManager(GeneratorAccess generatoraccess, GeneratorSettings generatorsettings) {
        this.level = generatoraccess;
        this.worldGenSettings = generatorsettings;
    }

    public StructureManager a(RegionLimitedWorldAccess regionlimitedworldaccess) {
        if (regionlimitedworldaccess.getLevel() != this.level) {
            WorldServer worldserver = regionlimitedworldaccess.getLevel();

            throw new IllegalStateException("Using invalid feature manager (source level: " + worldserver + ", region: " + regionlimitedworldaccess);
        } else {
            return new StructureManager(regionlimitedworldaccess, this.worldGenSettings);
        }
    }

    public Stream<? extends StructureStart<?>> a(SectionPosition sectionposition, StructureGenerator<?> structuregenerator) {
        return this.level.getChunkAt(sectionposition.a(), sectionposition.c(), ChunkStatus.STRUCTURE_REFERENCES).b(structuregenerator).stream().map((olong) -> {
            return SectionPosition.a(new ChunkCoordIntPair(olong), this.level.getMinSection());
        }).map((sectionposition1) -> {
            return this.a(sectionposition1, structuregenerator, this.level.getChunkAt(sectionposition1.a(), sectionposition1.c(), ChunkStatus.STRUCTURE_STARTS));
        }).filter((structurestart) -> {
            return structurestart != null && structurestart.e();
        });
    }

    @Nullable
    public StructureStart<?> a(SectionPosition sectionposition, StructureGenerator<?> structuregenerator, IStructureAccess istructureaccess) {
        return istructureaccess.a(structuregenerator);
    }

    public void a(SectionPosition sectionposition, StructureGenerator<?> structuregenerator, StructureStart<?> structurestart, IStructureAccess istructureaccess) {
        istructureaccess.a(structuregenerator, structurestart);
    }

    public void a(SectionPosition sectionposition, StructureGenerator<?> structuregenerator, long i, IStructureAccess istructureaccess) {
        istructureaccess.a(structuregenerator, i);
    }

    public boolean a() {
        return this.worldGenSettings.shouldGenerateMapFeatures();
    }

    public StructureStart<?> a(BlockPosition blockposition, boolean flag, StructureGenerator<?> structuregenerator) {
        return (StructureStart) DataFixUtils.orElse(this.a(SectionPosition.a(blockposition), structuregenerator).filter((structurestart) -> {
            return flag ? structurestart.d().stream().anyMatch((structurepiece) -> {
                return structurepiece.f().b((BaseBlockPosition) blockposition);
            }) : structurestart.c().b((BaseBlockPosition) blockposition);
        }).findFirst(), StructureStart.INVALID_START);
    }
}
