package net.minecraft.server;

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
import java.util.stream.Collectors;
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
            return worldgenfeaturedefinedstructurepooltemplate.f;
        }), WorldGenFeatureDefinedStructurePoolTemplate.Matching.c.fieldOf("projection").forGetter((worldgenfeaturedefinedstructurepooltemplate) -> {
            return worldgenfeaturedefinedstructurepooltemplate.i;
        })).apply(instance, WorldGenFeatureDefinedStructurePoolTemplate::new);
    });
    public static final WorldGenFeatureDefinedStructurePoolTemplate b = new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("empty"), new MinecraftKey("empty"), ImmutableList.of(), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID);
    public static final WorldGenFeatureDefinedStructurePoolTemplate c = new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("invalid"), new MinecraftKey("invalid"), ImmutableList.of(), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID);
    private final MinecraftKey e;
    private final ImmutableList<Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer>> f;
    private final List<WorldGenFeatureDefinedStructurePoolStructure> g;
    private final MinecraftKey h;
    private final WorldGenFeatureDefinedStructurePoolTemplate.Matching i;
    private int j = Integer.MIN_VALUE;

    public WorldGenFeatureDefinedStructurePoolTemplate(MinecraftKey minecraftkey, MinecraftKey minecraftkey1, List<Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer>> list, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.e = minecraftkey;
        this.f = ImmutableList.copyOf(list);
        this.g = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Pair<WorldGenFeatureDefinedStructurePoolStructure, Integer> pair = (Pair) iterator.next();

            for (int i = 0; i < (Integer) pair.getSecond(); ++i) {
                this.g.add(((WorldGenFeatureDefinedStructurePoolStructure) pair.getFirst()).a(worldgenfeaturedefinedstructurepooltemplate_matching));
            }
        }

        this.h = minecraftkey1;
        this.i = worldgenfeaturedefinedstructurepooltemplate_matching;
    }

    public int a(DefinedStructureManager definedstructuremanager) {
        if (this.j == Integer.MIN_VALUE) {
            this.j = this.g.stream().mapToInt((worldgenfeaturedefinedstructurepoolstructure) -> {
                return worldgenfeaturedefinedstructurepoolstructure.a(definedstructuremanager, BlockPosition.ZERO, EnumBlockRotation.NONE).e();
            }).max().orElse(0);
        }

        return this.j;
    }

    public MinecraftKey a() {
        return this.h;
    }

    public WorldGenFeatureDefinedStructurePoolStructure a(Random random) {
        return (WorldGenFeatureDefinedStructurePoolStructure) this.g.get(random.nextInt(this.g.size()));
    }

    public List<WorldGenFeatureDefinedStructurePoolStructure> b(Random random) {
        return ImmutableList.copyOf(ObjectArrays.shuffle(this.g.toArray(new WorldGenFeatureDefinedStructurePoolStructure[0]), random));
    }

    public MinecraftKey b() {
        return this.e;
    }

    public int c() {
        return this.g.size();
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
