package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.INamable;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorGravity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureDefinedStructurePoolTemplate {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int SIZE_UNSET = Integer.MIN_VALUE;
    public static final Codec<WorldGenFeatureDefinedStructurePoolTemplate> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(MinecraftKey.CODEC.fieldOf("name").forGetter(WorldGenFeatureDefinedStructurePoolTemplate::b), MinecraftKey.CODEC.fieldOf("fallback").forGetter(WorldGenFeatureDefinedStructurePoolTemplate::a), Codec.mapPair(WorldGenFeatureDefinedStructurePoolStructure.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter((worldgenfeaturedefinedstructurepooltemplate) -> {
            return worldgenfeaturedefinedstructurepooltemplate.rawTemplates;
        })).apply(instance, WorldGenFeatureDefinedStructurePoolTemplate::new);
    });
    public static final Codec<Supplier<WorldGenFeatureDefinedStructurePoolTemplate>> CODEC = RegistryFileCodec.a(IRegistry.TEMPLATE_POOL_REGISTRY, WorldGenFeatureDefinedStructurePoolTemplate.DIRECT_CODEC);
    private final MinecraftKey name;
    private final List<Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer>> rawTemplates;
    private final List<WorldGenFeatureDefinedStructurePoolStructure> templates;
    private final MinecraftKey fallback;
    private int maxSize = Integer.MIN_VALUE;

    public WorldGenFeatureDefinedStructurePoolTemplate(MinecraftKey minecraftkey, MinecraftKey minecraftkey1, List<Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer>> list) {
        this.name = minecraftkey;
        this.rawTemplates = list;
        this.templates = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer> pair = (Pair) iterator.next();
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = (WorldGenFeatureDefinedStructurePoolStructure) pair.getFirst();

            for (int i = 0; i < (Integer) pair.getSecond(); ++i) {
                this.templates.add(worldgenfeaturedefinedstructurepoolstructure);
            }
        }

        this.fallback = minecraftkey1;
    }

    public WorldGenFeatureDefinedStructurePoolTemplate(MinecraftKey minecraftkey, MinecraftKey minecraftkey1, List<Pair<Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, ? extends WorldGenFeatureDefinedStructurePoolStructure>, Integer>> list, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.name = minecraftkey;
        this.rawTemplates = Lists.newArrayList();
        this.templates = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Pair<Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, ? extends WorldGenFeatureDefinedStructurePoolStructure>, Integer> pair = (Pair) iterator.next();
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = (WorldGenFeatureDefinedStructurePoolStructure) ((Function) pair.getFirst()).apply(worldgenfeaturedefinedstructurepooltemplate_matching);

            this.rawTemplates.add(Pair.of(worldgenfeaturedefinedstructurepoolstructure, (Integer) pair.getSecond()));

            for (int i = 0; i < (Integer) pair.getSecond(); ++i) {
                this.templates.add(worldgenfeaturedefinedstructurepoolstructure);
            }
        }

        this.fallback = minecraftkey1;
    }

    public int a(DefinedStructureManager definedstructuremanager) {
        if (this.maxSize == Integer.MIN_VALUE) {
            this.maxSize = this.templates.stream().filter((worldgenfeaturedefinedstructurepoolstructure) -> {
                return worldgenfeaturedefinedstructurepoolstructure != WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE;
            }).mapToInt((worldgenfeaturedefinedstructurepoolstructure) -> {
                return worldgenfeaturedefinedstructurepoolstructure.a(definedstructuremanager, BlockPosition.ZERO, EnumBlockRotation.NONE).d();
            }).max().orElse(0);
        }

        return this.maxSize;
    }

    public MinecraftKey a() {
        return this.fallback;
    }

    public WorldGenFeatureDefinedStructurePoolStructure a(Random random) {
        return (WorldGenFeatureDefinedStructurePoolStructure) this.templates.get(random.nextInt(this.templates.size()));
    }

    public List<WorldGenFeatureDefinedStructurePoolStructure> b(Random random) {
        return ImmutableList.copyOf((WorldGenFeatureDefinedStructurePoolStructure[]) ObjectArrays.shuffle((WorldGenFeatureDefinedStructurePoolStructure[]) this.templates.toArray(new WorldGenFeatureDefinedStructurePoolStructure[0]), random));
    }

    public MinecraftKey b() {
        return this.name;
    }

    public int c() {
        return this.templates.size();
    }

    public static enum Matching implements INamable {

        TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new DefinedStructureProcessorGravity(HeightMap.Type.WORLD_SURFACE_WG, -1))), RIGID("rigid", ImmutableList.of());

        public static final Codec<WorldGenFeatureDefinedStructurePoolTemplate.Matching> CODEC = INamable.a(WorldGenFeatureDefinedStructurePoolTemplate.Matching::values, WorldGenFeatureDefinedStructurePoolTemplate.Matching::a);
        private static final Map<String, WorldGenFeatureDefinedStructurePoolTemplate.Matching> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureDefinedStructurePoolTemplate.Matching::a, (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return worldgenfeaturedefinedstructurepooltemplate_matching;
        }));
        private final String name;
        private final ImmutableList<DefinedStructureProcessor> processors;

        private Matching(String s, ImmutableList immutablelist) {
            this.name = s;
            this.processors = immutablelist;
        }

        public String a() {
            return this.name;
        }

        public static WorldGenFeatureDefinedStructurePoolTemplate.Matching a(String s) {
            return (WorldGenFeatureDefinedStructurePoolTemplate.Matching) WorldGenFeatureDefinedStructurePoolTemplate.Matching.BY_NAME.get(s);
        }

        public ImmutableList<DefinedStructureProcessor> b() {
            return this.processors;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
