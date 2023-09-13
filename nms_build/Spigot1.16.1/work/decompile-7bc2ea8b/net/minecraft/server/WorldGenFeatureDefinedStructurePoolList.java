package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldGenFeatureDefinedStructurePoolList extends WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolList> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureDefinedStructurePoolStructure.e.listOf().fieldOf("elements").forGetter((worldgenfeaturedefinedstructurepoollist) -> {
            return worldgenfeaturedefinedstructurepoollist.b;
        }), d()).apply(instance, WorldGenFeatureDefinedStructurePoolList::new);
    });
    private final List<WorldGenFeatureDefinedStructurePoolStructure> b;

    @Deprecated
    public WorldGenFeatureDefinedStructurePoolList(List<WorldGenFeatureDefinedStructurePoolStructure> list) {
        this(list, WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID);
    }

    public WorldGenFeatureDefinedStructurePoolList(List<WorldGenFeatureDefinedStructurePoolStructure> list, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        super(worldgenfeaturedefinedstructurepooltemplate_matching);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        } else {
            this.b = list;
            this.b(worldgenfeaturedefinedstructurepooltemplate_matching);
        }
    }

    @Override
    public List<DefinedStructure.BlockInfo> a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random) {
        return ((WorldGenFeatureDefinedStructurePoolStructure) this.b.get(0)).a(definedstructuremanager, blockposition, enumblockrotation, random);
    }

    @Override
    public StructureBoundingBox a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.a();
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = (WorldGenFeatureDefinedStructurePoolStructure) iterator.next();
            StructureBoundingBox structureboundingbox1 = worldgenfeaturedefinedstructurepoolstructure.a(definedstructuremanager, blockposition, enumblockrotation);

            structureboundingbox.c(structureboundingbox1);
        }

        return structureboundingbox;
    }

    @Override
    public boolean a(DefinedStructureManager definedstructuremanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, Random random, boolean flag) {
        Iterator iterator = this.b.iterator();

        WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            worldgenfeaturedefinedstructurepoolstructure = (WorldGenFeatureDefinedStructurePoolStructure) iterator.next();
        } while (worldgenfeaturedefinedstructurepoolstructure.a(definedstructuremanager, generatoraccessseed, structuremanager, chunkgenerator, blockposition, blockposition1, enumblockrotation, structureboundingbox, random, flag));

        return false;
    }

    @Override
    public WorldGenFeatureDefinedStructurePools<?> a() {
        return WorldGenFeatureDefinedStructurePools.b;
    }

    @Override
    public WorldGenFeatureDefinedStructurePoolStructure a(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        super.a(worldgenfeaturedefinedstructurepooltemplate_matching);
        this.b(worldgenfeaturedefinedstructurepooltemplate_matching);
        return this;
    }

    public String toString() {
        return "List[" + (String) this.b.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    private void b(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.b.forEach((worldgenfeaturedefinedstructurepoolstructure) -> {
            worldgenfeaturedefinedstructurepoolstructure.a(worldgenfeaturedefinedstructurepooltemplate_matching);
        });
    }
}
