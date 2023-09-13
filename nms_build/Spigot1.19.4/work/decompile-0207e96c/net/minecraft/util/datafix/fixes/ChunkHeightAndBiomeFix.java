package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

public class ChunkHeightAndBiomeFix extends DataFix {

    public static final String DATAFIXER_CONTEXT_TAG = "__context";
    private static final String NAME = "ChunkHeightAndBiomeFix";
    private static final int OLD_SECTION_COUNT = 16;
    private static final int NEW_SECTION_COUNT = 24;
    private static final int NEW_MIN_SECTION_Y = -4;
    public static final int BLOCKS_PER_SECTION = 4096;
    private static final int LONGS_PER_SECTION = 64;
    private static final int HEIGHTMAP_BITS = 9;
    private static final long HEIGHTMAP_MASK = 511L;
    private static final int HEIGHTMAP_OFFSET = 64;
    private static final String[] HEIGHTMAP_TYPES = new String[]{"WORLD_SURFACE_WG", "WORLD_SURFACE", "WORLD_SURFACE_IGNORE_SNOW", "OCEAN_FLOOR_WG", "OCEAN_FLOOR", "MOTION_BLOCKING", "MOTION_BLOCKING_NO_LEAVES"};
    private static final Set<String> STATUS_IS_OR_AFTER_SURFACE = Set.of("surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
    private static final Set<String> STATUS_IS_OR_AFTER_NOISE = Set.of("noise", "surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
    private static final Set<String> BLOCKS_BEFORE_FEATURE_STATUS = Set.of("minecraft:air", "minecraft:basalt", "minecraft:bedrock", "minecraft:blackstone", "minecraft:calcite", "minecraft:cave_air", "minecraft:coarse_dirt", "minecraft:crimson_nylium", "minecraft:dirt", "minecraft:end_stone", "minecraft:grass_block", "minecraft:gravel", "minecraft:ice", "minecraft:lava", "minecraft:mycelium", "minecraft:nether_wart_block", "minecraft:netherrack", "minecraft:orange_terracotta", "minecraft:packed_ice", "minecraft:podzol", "minecraft:powder_snow", "minecraft:red_sand", "minecraft:red_sandstone", "minecraft:sand", "minecraft:sandstone", "minecraft:snow_block", "minecraft:soul_sand", "minecraft:soul_soil", "minecraft:stone", "minecraft:terracotta", "minecraft:warped_nylium", "minecraft:warped_wart_block", "minecraft:water", "minecraft:white_terracotta");
    private static final int BIOME_CONTAINER_LAYER_SIZE = 16;
    private static final int BIOME_CONTAINER_SIZE = 64;
    private static final int BIOME_CONTAINER_TOP_LAYER_OFFSET = 1008;
    public static final String DEFAULT_BIOME = "minecraft:plains";
    private static final Int2ObjectMap<String> BIOMES_BY_ID = new Int2ObjectOpenHashMap();

    public ChunkHeightAndBiomeFix(Schema schema) {
        super(schema, true);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        OpticFinder<?> opticfinder = type.findField("Level");
        OpticFinder<?> opticfinder1 = opticfinder.type().findField("Sections");
        Schema schema = this.getOutputSchema();
        Type<?> type1 = schema.getType(DataConverterTypes.CHUNK);
        Type<?> type2 = type1.findField("Level").type();
        Type<?> type3 = type2.findField("Sections").type();

        return this.fixTypeEverywhereTyped("ChunkHeightAndBiomeFix", type, type1, (typed) -> {
            return typed.updateTyped(opticfinder, type2, (typed1) -> {
                Dynamic<?> dynamic = (Dynamic) typed1.get(DSL.remainderFinder());
                OptionalDynamic<?> optionaldynamic = ((Dynamic) typed.get(DSL.remainderFinder())).get("__context");
                String s = (String) optionaldynamic.get("dimension").asString().result().orElse("");
                String s1 = (String) optionaldynamic.get("generator").asString().result().orElse("");
                boolean flag = "minecraft:overworld".equals(s);
                MutableBoolean mutableboolean = new MutableBoolean();
                int i = flag ? -4 : 0;
                Dynamic<?>[] adynamic = getBiomeContainers(dynamic, flag, i, mutableboolean);
                Dynamic<?> dynamic1 = makePalettedContainer(dynamic.createList(Stream.of(dynamic.createMap(ImmutableMap.of(dynamic.createString("Name"), dynamic.createString("minecraft:air"))))));
                Set<String> set = Sets.newHashSet();
                MutableObject<Supplier<ChunkProtoTickListFix.a>> mutableobject = new MutableObject(() -> {
                    return null;
                });

                typed1 = typed1.updateTyped(opticfinder1, type3, (typed2) -> {
                    IntOpenHashSet intopenhashset = new IntOpenHashSet();
                    Dynamic<?> dynamic2 = (Dynamic) typed2.write().result().orElseThrow(() -> {
                        return new IllegalStateException("Malformed Chunk.Level.Sections");
                    });
                    List<Dynamic<?>> list = (List) dynamic2.asStream().map((dynamic3) -> {
                        int j = dynamic3.get("Y").asInt(0);
                        Dynamic<?> dynamic4 = (Dynamic) DataFixUtils.orElse(dynamic3.get("Palette").result().flatMap((dynamic5) -> {
                            Stream stream = dynamic5.asStream().map((dynamic6) -> {
                                return dynamic6.get("Name").asString("minecraft:air");
                            });

                            Objects.requireNonNull(set);
                            stream.forEach(set::add);
                            return dynamic3.get("BlockStates").result().map((dynamic6) -> {
                                return makeOptimizedPalettedContainer(dynamic5, dynamic6);
                            });
                        }), dynamic1);
                        Dynamic<?> dynamic5 = dynamic3;
                        int k = j - i;

                        if (k >= 0 && k < adynamic.length) {
                            dynamic5 = dynamic3.set("biomes", adynamic[k]);
                        }

                        intopenhashset.add(j);
                        if (dynamic3.get("Y").asInt(Integer.MAX_VALUE) == 0) {
                            mutableobject.setValue(() -> {
                                List<? extends Dynamic<?>> list1 = dynamic4.get("palette").asList(Function.identity());
                                long[] along = dynamic4.get("data").asLongStream().toArray();

                                return new ChunkProtoTickListFix.a(list1, along);
                            });
                        }

                        return dynamic5.set("block_states", dynamic4).remove("Palette").remove("BlockStates");
                    }).collect(Collectors.toCollection(ArrayList::new));

                    for (int j = 0; j < adynamic.length; ++j) {
                        int k = j + i;

                        if (intopenhashset.add(k)) {
                            Dynamic<?> dynamic3 = dynamic.createMap(Map.of(dynamic.createString("Y"), dynamic.createInt(k)));

                            dynamic3 = dynamic3.set("block_states", dynamic1);
                            dynamic3 = dynamic3.set("biomes", adynamic[j]);
                            list.add(dynamic3);
                        }
                    }

                    return (Typed) ((Pair) type3.readTyped(dynamic.createList(list.stream())).result().orElseThrow(() -> {
                        return new IllegalStateException("ChunkHeightAndBiomeFix failed.");
                    })).getFirst();
                });
                return typed1.update(DSL.remainderFinder(), (dynamic2) -> {
                    if (flag) {
                        dynamic2 = this.predictChunkStatusBeforeSurface(dynamic2, set);
                    }

                    return updateChunkTag(dynamic2, flag, mutableboolean.booleanValue(), "minecraft:noise".equals(s1), (Supplier) mutableobject.getValue());
                });
            });
        });
    }

    private Dynamic<?> predictChunkStatusBeforeSurface(Dynamic<?> dynamic, Set<String> set) {
        return dynamic.update("Status", (dynamic1) -> {
            String s = dynamic1.asString("empty");

            if (ChunkHeightAndBiomeFix.STATUS_IS_OR_AFTER_SURFACE.contains(s)) {
                return dynamic1;
            } else {
                set.remove("minecraft:air");
                boolean flag = !set.isEmpty();

                set.removeAll(ChunkHeightAndBiomeFix.BLOCKS_BEFORE_FEATURE_STATUS);
                boolean flag1 = !set.isEmpty();

                return flag1 ? dynamic1.createString("liquid_carvers") : (!"noise".equals(s) && !flag ? ("biomes".equals(s) ? dynamic1.createString("structure_references") : dynamic1) : dynamic1.createString("noise"));
            }
        });
    }

    private static Dynamic<?>[] getBiomeContainers(Dynamic<?> dynamic, boolean flag, int i, MutableBoolean mutableboolean) {
        Dynamic<?>[] adynamic = new Dynamic[flag ? 24 : 16];
        int[] aint = (int[]) dynamic.get("Biomes").asIntStreamOpt().result().map(IntStream::toArray).orElse((Object) null);
        int j;

        if (aint != null && aint.length == 1536) {
            mutableboolean.setValue(true);

            for (j = 0; j < 24; ++j) {
                adynamic[j] = makeBiomeContainer(dynamic, (k) -> {
                    return getOldBiome(aint, j * 64 + k);
                });
            }
        } else if (aint != null && aint.length == 1024) {
            for (j = 0; j < 16; ++j) {
                int k = j - i;

                adynamic[k] = makeBiomeContainer(dynamic, (l) -> {
                    return getOldBiome(aint, j * 64 + l);
                });
            }

            if (flag) {
                Dynamic<?> dynamic1 = makeBiomeContainer(dynamic, (l) -> {
                    return getOldBiome(aint, l % 16);
                });
                Dynamic<?> dynamic2 = makeBiomeContainer(dynamic, (l) -> {
                    return getOldBiome(aint, l % 16 + 1008);
                });

                int l;

                for (l = 0; l < 4; ++l) {
                    adynamic[l] = dynamic1;
                }

                for (l = 20; l < 24; ++l) {
                    adynamic[l] = dynamic2;
                }
            }
        } else {
            Arrays.fill(adynamic, makePalettedContainer(dynamic.createList(Stream.of(dynamic.createString("minecraft:plains")))));
        }

        return adynamic;
    }

    private static int getOldBiome(int[] aint, int i) {
        return aint[i] & 255;
    }

    private static Dynamic<?> updateChunkTag(Dynamic<?> dynamic, boolean flag, boolean flag1, boolean flag2, Supplier<ChunkProtoTickListFix.a> supplier) {
        dynamic = dynamic.remove("Biomes");
        if (!flag) {
            return updateCarvingMasks(dynamic, 16, 0);
        } else if (flag1) {
            return updateCarvingMasks(dynamic, 24, 0);
        } else {
            dynamic = updateHeightmaps(dynamic);
            dynamic = addPaddingEntries(dynamic, "Lights");
            dynamic = addPaddingEntries(dynamic, "LiquidsToBeTicked");
            dynamic = addPaddingEntries(dynamic, "PostProcessing");
            dynamic = addPaddingEntries(dynamic, "ToBeTicked");
            dynamic = updateCarvingMasks(dynamic, 24, 4);
            dynamic = dynamic.update("UpgradeData", ChunkHeightAndBiomeFix::shiftUpgradeData);
            if (!flag2) {
                return dynamic;
            } else {
                Optional<? extends Dynamic<?>> optional = dynamic.get("Status").result();

                if (optional.isPresent()) {
                    Dynamic<?> dynamic1 = (Dynamic) optional.get();
                    String s = dynamic1.asString("");

                    if (!"empty".equals(s)) {
                        dynamic = dynamic.set("blending_data", dynamic.createMap(ImmutableMap.of(dynamic.createString("old_noise"), dynamic.createBoolean(ChunkHeightAndBiomeFix.STATUS_IS_OR_AFTER_NOISE.contains(s)))));
                        ChunkProtoTickListFix.a chunkprototicklistfix_a = (ChunkProtoTickListFix.a) supplier.get();

                        if (chunkprototicklistfix_a != null) {
                            BitSet bitset = new BitSet(256);
                            boolean flag3 = s.equals("noise");

                            for (int i = 0; i < 16; ++i) {
                                for (int j = 0; j < 16; ++j) {
                                    Dynamic<?> dynamic2 = chunkprototicklistfix_a.get(j, 0, i);
                                    boolean flag4 = dynamic2 != null && "minecraft:bedrock".equals(dynamic2.get("Name").asString(""));
                                    boolean flag5 = dynamic2 != null && "minecraft:air".equals(dynamic2.get("Name").asString(""));

                                    if (flag5) {
                                        bitset.set(i * 16 + j);
                                    }

                                    flag3 |= flag4;
                                }
                            }

                            if (flag3 && bitset.cardinality() != bitset.size()) {
                                Dynamic<?> dynamic3 = "full".equals(s) ? dynamic.createString("heightmaps") : dynamic1;

                                dynamic = dynamic.set("below_zero_retrogen", dynamic.createMap(ImmutableMap.of(dynamic.createString("target_status"), dynamic3, dynamic.createString("missing_bedrock"), dynamic.createLongList(LongStream.of(bitset.toLongArray())))));
                                dynamic = dynamic.set("Status", dynamic.createString("empty"));
                            }

                            dynamic = dynamic.set("isLightOn", dynamic.createBoolean(false));
                        }
                    }
                }

                return dynamic;
            }
        }
    }

    private static <T> Dynamic<T> shiftUpgradeData(Dynamic<T> dynamic) {
        return dynamic.update("Indices", (dynamic1) -> {
            Map<Dynamic<?>, Dynamic<?>> map = new HashMap();

            dynamic1.getMapValues().result().ifPresent((map1) -> {
                map1.forEach((dynamic2, dynamic3) -> {
                    try {
                        dynamic2.asString().result().map(Integer::parseInt).ifPresent((integer) -> {
                            int i = integer - -4;

                            map.put(dynamic2.createString(Integer.toString(i)), dynamic3);
                        });
                    } catch (NumberFormatException numberformatexception) {
                        ;
                    }

                });
            });
            return dynamic1.createMap(map);
        });
    }

    private static Dynamic<?> updateCarvingMasks(Dynamic<?> dynamic, int i, int j) {
        Dynamic<?> dynamic1 = dynamic.get("CarvingMasks").orElseEmptyMap();

        dynamic1 = dynamic1.updateMapValues((pair) -> {
            long[] along = BitSet.valueOf(((Dynamic) pair.getSecond()).asByteBuffer().array()).toLongArray();
            long[] along1 = new long[64 * i];

            System.arraycopy(along, 0, along1, 64 * j, along.length);
            return Pair.of((Dynamic) pair.getFirst(), dynamic.createLongList(LongStream.of(along1)));
        });
        return dynamic.set("CarvingMasks", dynamic1);
    }

    private static Dynamic<?> addPaddingEntries(Dynamic<?> dynamic, String s) {
        List<Dynamic<?>> list = (List) dynamic.get(s).orElseEmptyList().asStream().collect(Collectors.toCollection(ArrayList::new));

        if (list.size() == 24) {
            return dynamic;
        } else {
            Dynamic<?> dynamic1 = dynamic.emptyList();

            for (int i = 0; i < 4; ++i) {
                list.add(0, dynamic1);
                list.add(dynamic1);
            }

            return dynamic.set(s, dynamic.createList(list.stream()));
        }
    }

    private static Dynamic<?> updateHeightmaps(Dynamic<?> dynamic) {
        return dynamic.update("Heightmaps", (dynamic1) -> {
            String[] astring = ChunkHeightAndBiomeFix.HEIGHTMAP_TYPES;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String s = astring[j];

                dynamic1 = dynamic1.update(s, ChunkHeightAndBiomeFix::getFixedHeightmap);
            }

            return dynamic1;
        });
    }

    private static Dynamic<?> getFixedHeightmap(Dynamic<?> dynamic) {
        return dynamic.createLongList(dynamic.asLongStream().map((i) -> {
            long j = 0L;

            for (int k = 0; k + 9 <= 64; k += 9) {
                long l = i >> k & 511L;
                long i1;

                if (l == 0L) {
                    i1 = 0L;
                } else {
                    i1 = Math.min(l + 64L, 511L);
                }

                j |= i1 << k;
            }

            return j;
        }));
    }

    private static Dynamic<?> makeBiomeContainer(Dynamic<?> dynamic, Int2IntFunction int2intfunction) {
        Int2IntLinkedOpenHashMap int2intlinkedopenhashmap = new Int2IntLinkedOpenHashMap();

        int i;

        for (int j = 0; j < 64; ++j) {
            i = int2intfunction.applyAsInt(j);
            if (!int2intlinkedopenhashmap.containsKey(i)) {
                int2intlinkedopenhashmap.put(i, int2intlinkedopenhashmap.size());
            }
        }

        Dynamic<?> dynamic1 = dynamic.createList(int2intlinkedopenhashmap.keySet().stream().map((integer) -> {
            return dynamic.createString((String) ChunkHeightAndBiomeFix.BIOMES_BY_ID.getOrDefault(integer, "minecraft:plains"));
        }));

        i = ceillog2(int2intlinkedopenhashmap.size());
        if (i == 0) {
            return makePalettedContainer(dynamic1);
        } else {
            int k = 64 / i;
            int l = (64 + k - 1) / k;
            long[] along = new long[l];
            int i1 = 0;
            int j1 = 0;

            for (int k1 = 0; k1 < 64; ++k1) {
                int l1 = int2intfunction.applyAsInt(k1);

                along[i1] |= (long) int2intlinkedopenhashmap.get(l1) << j1;
                j1 += i;
                if (j1 + i > 64) {
                    ++i1;
                    j1 = 0;
                }
            }

            Dynamic<?> dynamic2 = dynamic.createLongList(Arrays.stream(along));

            return makePalettedContainer(dynamic1, dynamic2);
        }
    }

    private static Dynamic<?> makePalettedContainer(Dynamic<?> dynamic) {
        return dynamic.createMap(ImmutableMap.of(dynamic.createString("palette"), dynamic));
    }

    private static Dynamic<?> makePalettedContainer(Dynamic<?> dynamic, Dynamic<?> dynamic1) {
        return dynamic.createMap(ImmutableMap.of(dynamic.createString("palette"), dynamic, dynamic.createString("data"), dynamic1));
    }

    private static Dynamic<?> makeOptimizedPalettedContainer(Dynamic<?> dynamic, Dynamic<?> dynamic1) {
        List<Dynamic<?>> list = (List) dynamic.asStream().collect(Collectors.toCollection(ArrayList::new));

        if (list.size() == 1) {
            return makePalettedContainer(dynamic);
        } else {
            dynamic = padPaletteEntries(dynamic, dynamic1, list);
            return makePalettedContainer(dynamic, dynamic1);
        }
    }

    private static Dynamic<?> padPaletteEntries(Dynamic<?> dynamic, Dynamic<?> dynamic1, List<Dynamic<?>> list) {
        long i = dynamic1.asLongStream().count() * 64L;
        long j = i / 4096L;
        int k = list.size();
        int l = ceillog2(k);

        if (j <= (long) l) {
            return dynamic;
        } else {
            Dynamic<?> dynamic2 = dynamic.createMap(ImmutableMap.of(dynamic.createString("Name"), dynamic.createString("minecraft:air")));
            int i1 = (1 << (int) (j - 1L)) + 1;
            int j1 = i1 - k;

            for (int k1 = 0; k1 < j1; ++k1) {
                list.add(dynamic2);
            }

            return dynamic.createList(list.stream());
        }
    }

    public static int ceillog2(int i) {
        return i == 0 ? 0 : (int) Math.ceil(Math.log((double) i) / Math.log(2.0D));
    }

    static {
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(0, "minecraft:ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(1, "minecraft:plains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(2, "minecraft:desert");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(3, "minecraft:mountains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(4, "minecraft:forest");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(5, "minecraft:taiga");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(6, "minecraft:swamp");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(7, "minecraft:river");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(8, "minecraft:nether_wastes");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(9, "minecraft:the_end");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(10, "minecraft:frozen_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(11, "minecraft:frozen_river");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(12, "minecraft:snowy_tundra");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(13, "minecraft:snowy_mountains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(14, "minecraft:mushroom_fields");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(15, "minecraft:mushroom_field_shore");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(16, "minecraft:beach");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(17, "minecraft:desert_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(18, "minecraft:wooded_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(19, "minecraft:taiga_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(20, "minecraft:mountain_edge");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(21, "minecraft:jungle");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(22, "minecraft:jungle_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(23, "minecraft:jungle_edge");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(24, "minecraft:deep_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(25, "minecraft:stone_shore");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(26, "minecraft:snowy_beach");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(27, "minecraft:birch_forest");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(28, "minecraft:birch_forest_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(29, "minecraft:dark_forest");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(30, "minecraft:snowy_taiga");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(31, "minecraft:snowy_taiga_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(32, "minecraft:giant_tree_taiga");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(33, "minecraft:giant_tree_taiga_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(34, "minecraft:wooded_mountains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(35, "minecraft:savanna");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(36, "minecraft:savanna_plateau");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(37, "minecraft:badlands");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(38, "minecraft:wooded_badlands_plateau");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(39, "minecraft:badlands_plateau");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(40, "minecraft:small_end_islands");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(41, "minecraft:end_midlands");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(42, "minecraft:end_highlands");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(43, "minecraft:end_barrens");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(44, "minecraft:warm_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(45, "minecraft:lukewarm_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(46, "minecraft:cold_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(47, "minecraft:deep_warm_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(48, "minecraft:deep_lukewarm_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(49, "minecraft:deep_cold_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(50, "minecraft:deep_frozen_ocean");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(127, "minecraft:the_void");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(129, "minecraft:sunflower_plains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(130, "minecraft:desert_lakes");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(131, "minecraft:gravelly_mountains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(132, "minecraft:flower_forest");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(133, "minecraft:taiga_mountains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(134, "minecraft:swamp_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(140, "minecraft:ice_spikes");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(149, "minecraft:modified_jungle");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(151, "minecraft:modified_jungle_edge");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(155, "minecraft:tall_birch_forest");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(156, "minecraft:tall_birch_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(157, "minecraft:dark_forest_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(158, "minecraft:snowy_taiga_mountains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(160, "minecraft:giant_spruce_taiga");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(161, "minecraft:giant_spruce_taiga_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(162, "minecraft:modified_gravelly_mountains");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(163, "minecraft:shattered_savanna");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(164, "minecraft:shattered_savanna_plateau");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(165, "minecraft:eroded_badlands");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(166, "minecraft:modified_wooded_badlands_plateau");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(167, "minecraft:modified_badlands_plateau");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(168, "minecraft:bamboo_jungle");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(169, "minecraft:bamboo_jungle_hills");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(170, "minecraft:soul_sand_valley");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(171, "minecraft:crimson_forest");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(172, "minecraft:warped_forest");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(173, "minecraft:basalt_deltas");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(174, "minecraft:dripstone_caves");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(175, "minecraft:lush_caves");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(177, "minecraft:meadow");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(178, "minecraft:grove");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(179, "minecraft:snowy_slopes");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(180, "minecraft:snowcapped_peaks");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(181, "minecraft:lofty_peaks");
        ChunkHeightAndBiomeFix.BIOMES_BY_ID.put(182, "minecraft:stony_peaks");
    }
}
