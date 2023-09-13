package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.annotation.Nullable;

public class DataConverterLeaves extends DataFix {

    private static final int[][] a = new int[][] { { -1, 0, 0}, { 1, 0, 0}, { 0, -1, 0}, { 0, 1, 0}, { 0, 0, -1}, { 0, 0, 1}};
    private static final Object2IntMap<String> b = (Object2IntMap) DataFixUtils.make(new Object2IntOpenHashMap(), (object2intopenhashmap) -> {
        object2intopenhashmap.put("minecraft:acacia_leaves", 0);
        object2intopenhashmap.put("minecraft:birch_leaves", 1);
        object2intopenhashmap.put("minecraft:dark_oak_leaves", 2);
        object2intopenhashmap.put("minecraft:jungle_leaves", 3);
        object2intopenhashmap.put("minecraft:oak_leaves", 4);
        object2intopenhashmap.put("minecraft:spruce_leaves", 5);
    });
    private static final Set<String> c = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", new String[] { "minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

    public DataConverterLeaves(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(DataConverterTypes.c);
        OpticFinder opticfinder = type.findField("Level");
        OpticFinder opticfinder1 = opticfinder.type().findField("Sections");
        Type type1 = opticfinder1.type();

        if (!(type1 instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
        } else {
            Type type2 = ((ListType) type1).getElement();
            OpticFinder opticfinder2 = DSL.typeFinder(type2);

            return this.fixTypeEverywhereTyped("Leaves fix", type, (typed) -> {
                return typed.updateTyped(opticfinder, (typedx) -> {
                    int[] aint = new int[] { 0};
                    Typed typed1 = typedx.updateTyped(opticfinder, (typed) -> {
                        Int2ObjectOpenHashMap int2objectopenhashmap = new Int2ObjectOpenHashMap((Map) typed.getAllTyped(opticfinder).stream().map((typedx) -> {
                            return new DataConverterLeaves.a(typedx);
                        }).collect(Collectors.toMap(DataConverterLeaves.a::b, (dataconverterleaves_a) -> {
                            return dataconverterleaves_a;
                        })));

                        if (int2objectopenhashmap.values().stream().allMatch(DataConverterLeaves.a::a)) {
                            return typed;
                        } else {
                            ArrayList arraylist = Lists.newArrayList();

                            int i;

                            for (i = 0; i < 7; ++i) {
                                arraylist.add(new IntOpenHashSet());
                            }

                            ObjectIterator objectiterator = int2objectopenhashmap.values().iterator();

                            int j;
                            int k;

                            while (objectiterator.hasNext()) {
                                DataConverterLeaves.a dataconverterleaves_a = (DataConverterLeaves.a) objectiterator.next();

                                if (!dataconverterleaves_a.a()) {
                                    for (int l = 0; l < 4096; ++l) {
                                        int i1 = dataconverterleaves_a.a(l);

                                        if (dataconverterleaves_a.b(i1)) {
                                            ((IntSet) arraylist.get(0)).add(dataconverterleaves_a.b() << 12 | l);
                                        } else if (dataconverterleaves_a.c(i1)) {
                                            j = this.a(l);
                                            k = this.c(l);
                                            aint[0] |= a(j == 0, j == 15, k == 0, k == 15);
                                        }
                                    }
                                }
                            }

                            for (i = 1; i < 7; ++i) {
                                IntSet intset = (IntSet) arraylist.get(i - 1);
                                IntSet intset1 = (IntSet) arraylist.get(i);
                                IntIterator intiterator = intset.iterator();

                                while (intiterator.hasNext()) {
                                    j = intiterator.nextInt();
                                    k = this.a(j);
                                    int j1 = this.b(j);
                                    int k1 = this.c(j);
                                    int[][] aint1 = DataConverterLeaves.a;
                                    int l1 = aint1.length;

                                    for (int i2 = 0; i2 < l1; ++i2) {
                                        int[] aint2 = aint1[i2];
                                        int j2 = k + aint2[0];
                                        int k2 = j1 + aint2[1];
                                        int l2 = k1 + aint2[2];

                                        if (j2 >= 0 && j2 <= 15 && l2 >= 0 && l2 <= 15 && k2 >= 0 && k2 <= 255) {
                                            DataConverterLeaves.a dataconverterleaves_a1 = (DataConverterLeaves.a) int2objectopenhashmap.get(k2 >> 4);

                                            if (dataconverterleaves_a1 != null && !dataconverterleaves_a1.a()) {
                                                int i3 = a(j2, k2 & 15, l2);
                                                int j3 = dataconverterleaves_a1.a(i3);

                                                if (dataconverterleaves_a1.c(j3)) {
                                                    int k3 = dataconverterleaves_a1.d(j3);

                                                    if (k3 > i) {
                                                        dataconverterleaves_a1.a(i3, j3, i);
                                                        intset1.add(a(j2, k2, l2));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            return typed.updateTyped(opticfinder, (typedx) -> {
                                return ((DataConverterLeaves.a) int2objectmap.get(((Dynamic) typedx.get(DSL.remainderFinder())).getInt("Y"))).a(typedx);
                            });
                        }
                    });

                    if (aint[0] != 0) {
                        typed1 = typed1.update(DSL.remainderFinder(), (dynamic) -> {
                            Dynamic dynamic1 = (Dynamic) DataFixUtils.orElse(dynamic.get("UpgradeData"), dynamic.emptyMap());

                            return dynamic.set("UpgradeData", dynamic1.set("Sides", dynamic.createByte((byte) (dynamic1.getByte("Sides") | aint[0]))));
                        });
                    }

                    return typed1;
                });
            });
        }
    }

    private static int a(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private int a(int i) {
        return i & 15;
    }

    private int b(int i) {
        return i >> 8 & 255;
    }

    private int c(int i) {
        return i >> 4 & 15;
    }

    public static int a(boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        int i = 0;

        if (flag2) {
            if (flag1) {
                i |= 2;
            } else if (flag) {
                i |= 128;
            } else {
                i |= 1;
            }
        } else if (flag3) {
            if (flag) {
                i |= 32;
            } else if (flag1) {
                i |= 8;
            } else {
                i |= 16;
            }
        } else if (flag1) {
            i |= 4;
        } else if (flag) {
            i |= 64;
        }

        return i;
    }

    final class a {

        final Type<Pair<String, Dynamic<?>>> a;
        private final OpticFinder<List<Pair<String, Dynamic<?>>>> c;
        private final List<Dynamic<?>> d;
        @Nullable
        private DataBits e;
        private final IntSet f;
        private final IntSet g;
        private final Int2IntMap h;
        private final int i;

        public a(Typed typed) {
            this.a = DSL.named(DataConverterTypes.l.typeName(), DSL.remainderType());
            this.c = DSL.fieldFinder("Palette", DSL.list(this.a));
            if (!Objects.equals(DataConverterLeaves.this.getInputSchema().getType(DataConverterTypes.l), this.a)) {
                throw new IllegalStateException("Block state type is not what was expected.");
            } else {
                this.f = new IntOpenHashSet();
                this.g = new IntOpenHashSet();
                this.h = new Int2IntOpenHashMap();
                Optional optional = typed.getOptional(this.c);

                this.d = (List) optional.map((list) -> {
                    return (List) list.stream().map(Pair::getSecond).collect(Collectors.toList());
                }).orElse(ImmutableList.of());

                for (int i = 0; i < this.d.size(); ++i) {
                    Dynamic dynamic = (Dynamic) this.d.get(i);
                    String s = dynamic.getString("Name");

                    if (DataConverterLeaves.b.containsKey(s)) {
                        boolean flag = Objects.equals(dynamic.get("Properties").flatMap((dynamic) -> {
                            return dynamic.get("decayable");
                        }).flatMap(Dynamic::getStringValue).orElse(""), "false");

                        this.f.add(i);
                        this.h.put(this.a(s, flag, 7), i);
                        this.d.set(i, this.a(dynamic, s, flag, 7));
                    }

                    if (DataConverterLeaves.c.contains(s)) {
                        this.g.add(i);
                    }
                }

                Dynamic dynamic1 = (Dynamic) typed.get(DSL.remainderFinder());

                if (this.f.isEmpty() && this.g.isEmpty()) {
                    this.e = null;
                } else {
                    long[] along = ((LongStream) dynamic1.get("BlockStates").flatMap(Dynamic::getLongStream).get()).toArray();
                    int j = Math.max(4, DataFixUtils.ceillog2(this.d.size()));

                    this.e = new DataBits(j, 4096, along);
                }

                this.i = dynamic1.getInt("Y");
            }
        }

        private Dynamic<?> a(Dynamic<?> dynamic, String s, boolean flag, int i) {
            Dynamic dynamic1 = dynamic.emptyMap();

            dynamic1 = dynamic1.set("persistent", dynamic1.createString(flag ? "true" : "false"));
            dynamic1 = dynamic1.set("distance", dynamic1.createString(Integer.toString(i)));
            Dynamic dynamic2 = dynamic.emptyMap();

            dynamic2 = dynamic2.set("Properties", dynamic1);
            dynamic2 = dynamic2.set("Name", dynamic2.createString(s));
            return dynamic2;
        }

        private Typed<?> a(Typed<?> typed) {
            return this.a() ? typed : typed.update(DSL.remainderFinder(), (dynamic) -> {
                return dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.e.a())));
            }).set(this.c, this.d.stream().map((dynamic) -> {
                return Pair.of(DataConverterTypes.l.typeName(), dynamic);
            }).collect(Collectors.toList()));
        }

        public boolean a() {
            return this.e == null;
        }

        public int a(int i) {
            return this.e.a(i);
        }

        public boolean b(int i) {
            return this.g.contains(i);
        }

        public boolean c(int i) {
            return this.f.contains(i);
        }

        private int d(int i) {
            return this.b(i) ? 0 : Integer.parseInt((String) ((Dynamic) this.d.get(i)).get("Properties").flatMap((dynamic) -> {
                return dynamic.get("distance");
            }).flatMap(Dynamic::getStringValue).orElse(""));
        }

        private void a(int i, int j, int k) {
            Dynamic dynamic = (Dynamic) this.d.get(j);
            String s = dynamic.getString("Name");
            boolean flag = Objects.equals(dynamic.get("Properties").flatMap((dynamic) -> {
                return dynamic.get("persistent");
            }).flatMap(Dynamic::getStringValue).orElse(""), "true");
            int l = this.a(s, flag, k);
            int i1;

            if (!this.h.containsKey(l)) {
                i1 = this.d.size();
                this.f.add(i1);
                this.h.put(l, i1);
                this.d.add(this.a(dynamic, s, flag, k));
            }

            i1 = this.h.get(l);
            if (1 << this.e.c() <= i1) {
                DataBits databits = new DataBits(this.e.c() + 1, 4096);

                for (int j1 = 0; j1 < 4096; ++j1) {
                    databits.a(j1, this.e.a(j1));
                }

                this.e = databits;
            }

            this.e.a(i, i1);
        }

        private int a(String s, boolean flag, int i) {
            return ((Integer) DataConverterLeaves.b.get(s)).intValue() << 5 | (flag ? 16 : 0) | i;
        }

        int b() {
            return this.i;
        }
    }
}
