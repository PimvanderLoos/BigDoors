package net.minecraft.util.datafix.fixes;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.mutable.MutableInt;

public class ChunkProtoTickListFix extends DataFix {

    private static final int SECTION_WIDTH = 16;
    private static final ImmutableSet<String> ALWAYS_WATERLOGGED = ImmutableSet.of("minecraft:bubble_column", "minecraft:kelp", "minecraft:kelp_plant", "minecraft:seagrass", "minecraft:tall_seagrass");

    public ChunkProtoTickListFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        OpticFinder<?> opticfinder = type.findField("Level");
        OpticFinder<?> opticfinder1 = opticfinder.type().findField("Sections");
        OpticFinder<?> opticfinder2 = ((ListType) opticfinder1.type()).getElement().finder();
        OpticFinder<?> opticfinder3 = opticfinder2.type().findField("block_states");
        OpticFinder<?> opticfinder4 = opticfinder2.type().findField("biomes");
        OpticFinder<?> opticfinder5 = opticfinder3.type().findField("palette");
        OpticFinder<?> opticfinder6 = opticfinder.type().findField("TileTicks");

        return this.fixTypeEverywhereTyped("ChunkProtoTickListFix", type, (typed) -> {
            return typed.updateTyped(opticfinder, (typed1) -> {
                typed1 = typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    return (Dynamic) DataFixUtils.orElse(dynamic.get("LiquidTicks").result().map((dynamic1) -> {
                        return dynamic.set("fluid_ticks", dynamic1).remove("LiquidTicks");
                    }), dynamic);
                });
                Dynamic<?> dynamic = (Dynamic) typed1.get(DSL.remainderFinder());
                MutableInt mutableint = new MutableInt();
                Int2ObjectMap<Supplier<ChunkProtoTickListFix.a>> int2objectmap = new Int2ObjectArrayMap();

                typed1.getOptionalTyped(opticfinder1).ifPresent((typed2) -> {
                    typed2.getAllTyped(opticfinder2).forEach((typed3) -> {
                        Dynamic<?> dynamic1 = (Dynamic) typed3.get(DSL.remainderFinder());
                        int i = dynamic1.get("Y").asInt(Integer.MAX_VALUE);

                        if (i != Integer.MAX_VALUE) {
                            if (typed3.getOptionalTyped(opticfinder4).isPresent()) {
                                mutableint.setValue(Math.min(i, mutableint.getValue()));
                            }

                            typed3.getOptionalTyped(opticfinder3).ifPresent((typed4) -> {
                                int2objectmap.put(i, Suppliers.memoize(() -> {
                                    List<? extends Dynamic<?>> list = (List) typed4.getOptionalTyped(opticfinder5).map((typed5) -> {
                                        return (List) typed5.write().result().map((dynamic2) -> {
                                            return dynamic2.asList(Function.identity());
                                        }).orElse(Collections.emptyList());
                                    }).orElse(Collections.emptyList());
                                    long[] along = ((Dynamic) typed4.get(DSL.remainderFinder())).get("data").asLongStream().toArray();

                                    return new ChunkProtoTickListFix.a(list, along);
                                }));
                            });
                        }
                    });
                });
                byte b0 = mutableint.getValue().byteValue();

                typed1 = typed1.update(DSL.remainderFinder(), (dynamic1) -> {
                    return dynamic1.update("yPos", (dynamic2) -> {
                        return dynamic2.createByte(b0);
                    });
                });
                if (!typed1.getOptionalTyped(opticfinder6).isPresent() && !dynamic.get("fluid_ticks").result().isPresent()) {
                    int i = dynamic.get("xPos").asInt(0);
                    int j = dynamic.get("zPos").asInt(0);
                    Dynamic<?> dynamic1 = this.makeTickList(dynamic, int2objectmap, b0, i, j, "LiquidsToBeTicked", ChunkProtoTickListFix::getLiquid);
                    Dynamic<?> dynamic2 = this.makeTickList(dynamic, int2objectmap, b0, i, j, "ToBeTicked", ChunkProtoTickListFix::getBlock);
                    Optional<? extends Pair<? extends Typed<?>, ?>> optional = opticfinder6.type().readTyped(dynamic2).result();

                    if (optional.isPresent()) {
                        typed1 = typed1.set(opticfinder6, (Typed) ((Pair) optional.get()).getFirst());
                    }

                    return typed1.update(DSL.remainderFinder(), (dynamic3) -> {
                        return dynamic3.remove("ToBeTicked").remove("LiquidsToBeTicked").set("fluid_ticks", dynamic1);
                    });
                } else {
                    return typed1;
                }
            });
        });
    }

    private Dynamic<?> makeTickList(Dynamic<?> dynamic, Int2ObjectMap<Supplier<ChunkProtoTickListFix.a>> int2objectmap, byte b0, int i, int j, String s, Function<Dynamic<?>, String> function) {
        Stream<Dynamic<?>> stream = Stream.empty();
        List<? extends Dynamic<?>> list = dynamic.get(s).asList(Function.identity());

        for (int k = 0; k < list.size(); ++k) {
            int l = k + b0;
            Supplier<ChunkProtoTickListFix.a> supplier = (Supplier) int2objectmap.get(l);
            Stream<? extends Dynamic<?>> stream1 = ((Dynamic) list.get(k)).asStream().mapToInt((dynamic1) -> {
                return dynamic1.asShort((short) -1);
            }).filter((i1) -> {
                return i1 > 0;
            }).mapToObj((i1) -> {
                return this.createTick(dynamic, supplier, i, l, j, i1, function);
            });

            stream = Stream.concat(stream, stream1);
        }

        return dynamic.createList(stream);
    }

    private static String getBlock(@Nullable Dynamic<?> dynamic) {
        return dynamic != null ? dynamic.get("Name").asString("minecraft:air") : "minecraft:air";
    }

    private static String getLiquid(@Nullable Dynamic<?> dynamic) {
        if (dynamic == null) {
            return "minecraft:empty";
        } else {
            String s = dynamic.get("Name").asString("");

            return "minecraft:water".equals(s) ? (dynamic.get("Properties").get("level").asInt(0) == 0 ? "minecraft:water" : "minecraft:flowing_water") : ("minecraft:lava".equals(s) ? (dynamic.get("Properties").get("level").asInt(0) == 0 ? "minecraft:lava" : "minecraft:flowing_lava") : (!ChunkProtoTickListFix.ALWAYS_WATERLOGGED.contains(s) && !dynamic.get("Properties").get("waterlogged").asBoolean(false) ? "minecraft:empty" : "minecraft:water"));
        }
    }

    private Dynamic<?> createTick(Dynamic<?> dynamic, @Nullable Supplier<ChunkProtoTickListFix.a> supplier, int i, int j, int k, int l, Function<Dynamic<?>, String> function) {
        int i1 = l & 15;
        int j1 = l >>> 4 & 15;
        int k1 = l >>> 8 & 15;
        String s = (String) function.apply(supplier != null ? ((ChunkProtoTickListFix.a) supplier.get()).get(i1, j1, k1) : null);

        return dynamic.createMap(ImmutableMap.builder().put(dynamic.createString("i"), dynamic.createString(s)).put(dynamic.createString("x"), dynamic.createInt(i * 16 + i1)).put(dynamic.createString("y"), dynamic.createInt(j * 16 + j1)).put(dynamic.createString("z"), dynamic.createInt(k * 16 + k1)).put(dynamic.createString("t"), dynamic.createInt(0)).put(dynamic.createString("p"), dynamic.createInt(0)).build());
    }

    public static final class a {

        private static final long SIZE_BITS = 4L;
        private final List<? extends Dynamic<?>> palette;
        private final long[] data;
        private final int bits;
        private final long mask;
        private final int valuesPerLong;

        public a(List<? extends Dynamic<?>> list, long[] along) {
            this.palette = list;
            this.data = along;
            this.bits = Math.max(4, ChunkHeightAndBiomeFix.ceillog2(list.size()));
            this.mask = (1L << this.bits) - 1L;
            this.valuesPerLong = (char) (64 / this.bits);
        }

        @Nullable
        public Dynamic<?> get(int i, int j, int k) {
            int l = this.palette.size();

            if (l < 1) {
                return null;
            } else if (l == 1) {
                return (Dynamic) this.palette.get(0);
            } else {
                int i1 = this.getIndex(i, j, k);
                int j1 = i1 / this.valuesPerLong;

                if (j1 >= 0 && j1 < this.data.length) {
                    long k1 = this.data[j1];
                    int l1 = (i1 - j1 * this.valuesPerLong) * this.bits;
                    int i2 = (int) (k1 >> l1 & this.mask);

                    return i2 >= 0 && i2 < l ? (Dynamic) this.palette.get(i2) : null;
                } else {
                    return null;
                }
            }
        }

        private int getIndex(int i, int j, int k) {
            return (j << 4 | k) << 4 | i;
        }

        public List<? extends Dynamic<?>> palette() {
            return this.palette;
        }

        public long[] data() {
            return this.data;
        }
    }
}
