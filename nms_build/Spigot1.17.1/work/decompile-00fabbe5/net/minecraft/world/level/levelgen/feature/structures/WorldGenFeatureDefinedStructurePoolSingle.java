package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.properties.BlockPropertyStructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorJigsawReplacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;

public class WorldGenFeatureDefinedStructurePoolSingle extends WorldGenFeatureDefinedStructurePoolStructure {

    private static final Codec<Either<MinecraftKey, DefinedStructure>> TEMPLATE_CODEC = Codec.of(WorldGenFeatureDefinedStructurePoolSingle::a, MinecraftKey.CODEC.map(Either::left));
    public static final Codec<WorldGenFeatureDefinedStructurePoolSingle> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(c(), b(), d()).apply(instance, WorldGenFeatureDefinedStructurePoolSingle::new);
    });
    protected final Either<MinecraftKey, DefinedStructure> template;
    protected final Supplier<ProcessorList> processors;

    private static <T> DataResult<T> a(Either<MinecraftKey, DefinedStructure> either, DynamicOps<T> dynamicops, T t0) {
        Optional<MinecraftKey> optional = either.left();

        return !optional.isPresent() ? DataResult.error("Can not serialize a runtime pool element") : MinecraftKey.CODEC.encode((MinecraftKey) optional.get(), dynamicops, t0);
    }

    protected static <E extends WorldGenFeatureDefinedStructurePoolSingle> RecordCodecBuilder<E, Supplier<ProcessorList>> b() {
        return DefinedStructureStructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter((worldgenfeaturedefinedstructurepoolsingle) -> {
            return worldgenfeaturedefinedstructurepoolsingle.processors;
        });
    }

    protected static <E extends WorldGenFeatureDefinedStructurePoolSingle> RecordCodecBuilder<E, Either<MinecraftKey, DefinedStructure>> c() {
        return WorldGenFeatureDefinedStructurePoolSingle.TEMPLATE_CODEC.fieldOf("location").forGetter((worldgenfeaturedefinedstructurepoolsingle) -> {
            return worldgenfeaturedefinedstructurepoolsingle.template;
        });
    }

    protected WorldGenFeatureDefinedStructurePoolSingle(Either<MinecraftKey, DefinedStructure> either, Supplier<ProcessorList> supplier, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        super(worldgenfeaturedefinedstructurepooltemplate_matching);
        this.template = either;
        this.processors = supplier;
    }

    public WorldGenFeatureDefinedStructurePoolSingle(DefinedStructure definedstructure) {
        this(Either.right(definedstructure), () -> {
            return ProcessorLists.EMPTY;
        }, WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID);
    }

    @Override
    public BaseBlockPosition a(DefinedStructureManager definedstructuremanager, EnumBlockRotation enumblockrotation) {
        DefinedStructure definedstructure = this.a(definedstructuremanager);

        return definedstructure.a(enumblockrotation);
    }

    private DefinedStructure a(DefinedStructureManager definedstructuremanager) {
        Either either = this.template;

        Objects.requireNonNull(definedstructuremanager);
        return (DefinedStructure) either.map(definedstructuremanager::a, Function.identity());
    }

    public List<DefinedStructure.BlockInfo> a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, boolean flag) {
        DefinedStructure definedstructure = this.a(definedstructuremanager);
        List<DefinedStructure.BlockInfo> list = definedstructure.a(blockposition, (new DefinedStructureInfo()).a(enumblockrotation), Blocks.STRUCTURE_BLOCK, flag);
        List<DefinedStructure.BlockInfo> list1 = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();

            if (definedstructure_blockinfo.nbt != null) {
                BlockPropertyStructureMode blockpropertystructuremode = BlockPropertyStructureMode.valueOf(definedstructure_blockinfo.nbt.getString("mode"));

                if (blockpropertystructuremode == BlockPropertyStructureMode.DATA) {
                    list1.add(definedstructure_blockinfo);
                }
            }
        }

        return list1;
    }

    @Override
    public List<DefinedStructure.BlockInfo> a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random) {
        DefinedStructure definedstructure = this.a(definedstructuremanager);
        List<DefinedStructure.BlockInfo> list = definedstructure.a(blockposition, (new DefinedStructureInfo()).a(enumblockrotation), Blocks.JIGSAW, true);

        Collections.shuffle(list, random);
        return list;
    }

    @Override
    public StructureBoundingBox a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
        DefinedStructure definedstructure = this.a(definedstructuremanager);

        return definedstructure.b((new DefinedStructureInfo()).a(enumblockrotation), blockposition);
    }

    @Override
    public boolean a(DefinedStructureManager definedstructuremanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, Random random, boolean flag) {
        DefinedStructure definedstructure = this.a(definedstructuremanager);
        DefinedStructureInfo definedstructureinfo = this.a(enumblockrotation, structureboundingbox, flag);

        if (!definedstructure.a(generatoraccessseed, blockposition, blockposition1, definedstructureinfo, random, 18)) {
            return false;
        } else {
            List<DefinedStructure.BlockInfo> list = DefinedStructure.a((GeneratorAccess) generatoraccessseed, blockposition, blockposition1, definedstructureinfo, this.a(definedstructuremanager, blockposition, enumblockrotation, false));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();

                this.a(generatoraccessseed, definedstructure_blockinfo, blockposition, enumblockrotation, random, structureboundingbox);
            }

            return true;
        }
    }

    protected DefinedStructureInfo a(EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, boolean flag) {
        DefinedStructureInfo definedstructureinfo = new DefinedStructureInfo();

        definedstructureinfo.a(structureboundingbox);
        definedstructureinfo.a(enumblockrotation);
        definedstructureinfo.c(true);
        definedstructureinfo.a(false);
        definedstructureinfo.a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK);
        definedstructureinfo.d(true);
        if (!flag) {
            definedstructureinfo.a((DefinedStructureProcessor) DefinedStructureProcessorJigsawReplacement.INSTANCE);
        }

        List list = ((ProcessorList) this.processors.get()).a();

        Objects.requireNonNull(definedstructureinfo);
        list.forEach(definedstructureinfo::a);
        ImmutableList immutablelist = this.e().b();

        Objects.requireNonNull(definedstructureinfo);
        immutablelist.forEach(definedstructureinfo::a);
        return definedstructureinfo;
    }

    @Override
    public WorldGenFeatureDefinedStructurePools<?> a() {
        return WorldGenFeatureDefinedStructurePools.SINGLE;
    }

    public String toString() {
        return "Single[" + this.template + "]";
    }
}
