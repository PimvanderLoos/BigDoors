package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.INamable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorGravity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

public class WorldGenFeatureDefinedStructurePoolTemplate {

    private static final int SIZE_UNSET = Integer.MIN_VALUE;
    private static final MutableObject<Codec<Holder<WorldGenFeatureDefinedStructurePoolTemplate>>> CODEC_REFERENCE = new MutableObject();
    public static final Codec<WorldGenFeatureDefinedStructurePoolTemplate> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        MutableObject mutableobject = WorldGenFeatureDefinedStructurePoolTemplate.CODEC_REFERENCE;

        Objects.requireNonNull(mutableobject);
        return instance.group(ExtraCodecs.lazyInitializedCodec(mutableobject::getValue).fieldOf("fallback").forGetter(WorldGenFeatureDefinedStructurePoolTemplate::getFallback), Codec.mapPair(WorldGenFeatureDefinedStructurePoolStructure.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter((worldgenfeaturedefinedstructurepooltemplate) -> {
            return worldgenfeaturedefinedstructurepooltemplate.rawTemplates;
        })).apply(instance, WorldGenFeatureDefinedStructurePoolTemplate::new);
    });
    public static final Codec<Holder<WorldGenFeatureDefinedStructurePoolTemplate>> CODEC;
    private final List<Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer>> rawTemplates;
    private final ObjectArrayList<WorldGenFeatureDefinedStructurePoolStructure> templates;
    private final Holder<WorldGenFeatureDefinedStructurePoolTemplate> fallback;
    private int maxSize = Integer.MIN_VALUE;

    public WorldGenFeatureDefinedStructurePoolTemplate(Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, List<Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer>> list) {
        this.rawTemplates = list;
        this.templates = new ObjectArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer> pair = (Pair) iterator.next();
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = (WorldGenFeatureDefinedStructurePoolStructure) pair.getFirst();

            for (int i = 0; i < (Integer) pair.getSecond(); ++i) {
                this.templates.add(worldgenfeaturedefinedstructurepoolstructure);
            }
        }

        this.fallback = holder;
    }

    public WorldGenFeatureDefinedStructurePoolTemplate(Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, List<Pair<Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, ? extends WorldGenFeatureDefinedStructurePoolStructure>, Integer>> list, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.rawTemplates = Lists.newArrayList();
        this.templates = new ObjectArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Pair<Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, ? extends WorldGenFeatureDefinedStructurePoolStructure>, Integer> pair = (Pair) iterator.next();
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = (WorldGenFeatureDefinedStructurePoolStructure) ((Function) pair.getFirst()).apply(worldgenfeaturedefinedstructurepooltemplate_matching);

            this.rawTemplates.add(Pair.of(worldgenfeaturedefinedstructurepoolstructure, (Integer) pair.getSecond()));

            for (int i = 0; i < (Integer) pair.getSecond(); ++i) {
                this.templates.add(worldgenfeaturedefinedstructurepoolstructure);
            }
        }

        this.fallback = holder;
    }

    public int getMaxSize(StructureTemplateManager structuretemplatemanager) {
        if (this.maxSize == Integer.MIN_VALUE) {
            this.maxSize = this.templates.stream().filter((worldgenfeaturedefinedstructurepoolstructure) -> {
                return worldgenfeaturedefinedstructurepoolstructure != WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE;
            }).mapToInt((worldgenfeaturedefinedstructurepoolstructure) -> {
                return worldgenfeaturedefinedstructurepoolstructure.getBoundingBox(structuretemplatemanager, BlockPosition.ZERO, EnumBlockRotation.NONE).getYSpan();
            }).max().orElse(0);
        }

        return this.maxSize;
    }

    public Holder<WorldGenFeatureDefinedStructurePoolTemplate> getFallback() {
        return this.fallback;
    }

    public WorldGenFeatureDefinedStructurePoolStructure getRandomTemplate(RandomSource randomsource) {
        return (WorldGenFeatureDefinedStructurePoolStructure) this.templates.get(randomsource.nextInt(this.templates.size()));
    }

    public List<WorldGenFeatureDefinedStructurePoolStructure> getShuffledTemplates(RandomSource randomsource) {
        return SystemUtils.shuffledCopy(this.templates, randomsource);
    }

    public int size() {
        return this.templates.size();
    }

    static {
        RegistryFileCodec registryfilecodec = RegistryFileCodec.create(Registries.TEMPLATE_POOL, WorldGenFeatureDefinedStructurePoolTemplate.DIRECT_CODEC);
        MutableObject mutableobject = WorldGenFeatureDefinedStructurePoolTemplate.CODEC_REFERENCE;

        Objects.requireNonNull(mutableobject);
        CODEC = (Codec) SystemUtils.make(registryfilecodec, mutableobject::setValue);
    }

    public static enum Matching implements INamable {

        TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new DefinedStructureProcessorGravity(HeightMap.Type.WORLD_SURFACE_WG, -1))), RIGID("rigid", ImmutableList.of());

        public static final INamable.a<WorldGenFeatureDefinedStructurePoolTemplate.Matching> CODEC = INamable.fromEnum(WorldGenFeatureDefinedStructurePoolTemplate.Matching::values);
        private final String name;
        private final ImmutableList<DefinedStructureProcessor> processors;

        private Matching(String s, ImmutableList immutablelist) {
            this.name = s;
            this.processors = immutablelist;
        }

        public String getName() {
            return this.name;
        }

        public static WorldGenFeatureDefinedStructurePoolTemplate.Matching byName(String s) {
            return (WorldGenFeatureDefinedStructurePoolTemplate.Matching) WorldGenFeatureDefinedStructurePoolTemplate.Matching.CODEC.byName(s);
        }

        public ImmutableList<DefinedStructureProcessor> getProcessors() {
            return this.processors;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
