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
import net.minecraft.SystemUtils;
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
    public static final Codec<WorldGenFeatureDefinedStructurePoolTemplate> a = RecordCodecBuilder.create((instance) -> {
        RecordCodecBuilder recordcodecbuilder = MinecraftKey.a.fieldOf("name").forGetter(WorldGenFeatureDefinedStructurePoolTemplate::b);
        RecordCodecBuilder recordcodecbuilder1 = MinecraftKey.a.fieldOf("fallback").forGetter(WorldGenFeatureDefinedStructurePoolTemplate::a);
        Codec codec = Codec.mapPair(WorldGenFeatureDefinedStructurePoolStructure.e.fieldOf("element"), Codec.INT.fieldOf("weight")).codec().listOf();
        Logger logger = WorldGenFeatureDefinedStructurePoolTemplate.LOGGER;

        logger.getClass();
        return instance.group(recordcodecbuilder, recordcodecbuilder1, codec.promotePartial(SystemUtils.a("Pool element: ", logger::error)).fieldOf("elements").forGetter((worldgenfeaturedefinedstructurepooltemplate) -> {
            return worldgenfeaturedefinedstructurepooltemplate.e;
        })).apply(instance, WorldGenFeatureDefinedStructurePoolTemplate::new);
    });
    public static final Codec<Supplier<WorldGenFeatureDefinedStructurePoolTemplate>> b = RegistryFileCodec.a(IRegistry.ax, WorldGenFeatureDefinedStructurePoolTemplate.a);
    private final MinecraftKey d;
    private final List<Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer>> e;
    private final List<WorldGenFeatureDefinedStructurePoolStructure> f;
    private final MinecraftKey g;
    private int h = Integer.MIN_VALUE;

    public WorldGenFeatureDefinedStructurePoolTemplate(MinecraftKey minecraftkey, MinecraftKey minecraftkey1, List<Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer>> list) {
        this.d = minecraftkey;
        this.e = list;
        this.f = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer> pair = (Pair) iterator.next();
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = (WorldGenFeatureDefinedStructurePoolStructure) pair.getFirst();

            for (int i = 0; i < (Integer) pair.getSecond(); ++i) {
                this.f.add(worldgenfeaturedefinedstructurepoolstructure);
            }
        }

        this.g = minecraftkey1;
    }

    public WorldGenFeatureDefinedStructurePoolTemplate(MinecraftKey minecraftkey, MinecraftKey minecraftkey1, List<Pair<Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, ? extends WorldGenFeatureDefinedStructurePoolStructure>, Integer>> list, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.d = minecraftkey;
        this.e = Lists.newArrayList();
        this.f = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Pair<Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, ? extends WorldGenFeatureDefinedStructurePoolStructure>, Integer> pair = (Pair) iterator.next();
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = (WorldGenFeatureDefinedStructurePoolStructure) ((Function) pair.getFirst()).apply(worldgenfeaturedefinedstructurepooltemplate_matching);

            this.e.add(Pair.of(worldgenfeaturedefinedstructurepoolstructure, pair.getSecond()));

            for (int i = 0; i < (Integer) pair.getSecond(); ++i) {
                this.f.add(worldgenfeaturedefinedstructurepoolstructure);
            }
        }

        this.g = minecraftkey1;
    }

    public int a(DefinedStructureManager definedstructuremanager) {
        if (this.h == Integer.MIN_VALUE) {
            this.h = this.f.stream().mapToInt((worldgenfeaturedefinedstructurepoolstructure) -> {
                return worldgenfeaturedefinedstructurepoolstructure.a(definedstructuremanager, BlockPosition.ZERO, EnumBlockRotation.NONE).e();
            }).max().orElse(0);
        }

        return this.h;
    }

    public MinecraftKey a() {
        return this.g;
    }

    public WorldGenFeatureDefinedStructurePoolStructure a(Random random) {
        return (WorldGenFeatureDefinedStructurePoolStructure) this.f.get(random.nextInt(this.f.size()));
    }

    public List<WorldGenFeatureDefinedStructurePoolStructure> b(Random random) {
        return ImmutableList.copyOf(ObjectArrays.shuffle(this.f.toArray(new WorldGenFeatureDefinedStructurePoolStructure[0]), random));
    }

    public MinecraftKey b() {
        return this.d;
    }

    public int c() {
        return this.f.size();
    }

    public static enum Matching implements INamable {

        TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new DefinedStructureProcessorGravity(HeightMap.Type.WORLD_SURFACE_WG, -1))), RIGID("rigid", ImmutableList.of());

        public static final Codec<WorldGenFeatureDefinedStructurePoolTemplate.Matching> c = INamable.a(WorldGenFeatureDefinedStructurePoolTemplate.Matching::values, WorldGenFeatureDefinedStructurePoolTemplate.Matching::a);
        private static final Map<String, WorldGenFeatureDefinedStructurePoolTemplate.Matching> d = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureDefinedStructurePoolTemplate.Matching::b, (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return worldgenfeaturedefinedstructurepooltemplate_matching;
        }));
        private final String e;
        private final ImmutableList<DefinedStructureProcessor> f;

        private Matching(String s, ImmutableList immutablelist) {
            this.e = s;
            this.f = immutablelist;
        }

        public String b() {
            return this.e;
        }

        public static WorldGenFeatureDefinedStructurePoolTemplate.Matching a(String s) {
            return (WorldGenFeatureDefinedStructurePoolTemplate.Matching) WorldGenFeatureDefinedStructurePoolTemplate.Matching.d.get(s);
        }

        public ImmutableList<DefinedStructureProcessor> c() {
            return this.f;
        }

        @Override
        public String getName() {
            return this.e;
        }
    }
}
