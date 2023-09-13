package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.DataBitsPacked;

public class DataConverterLeaves extends DataFix {

    private static final int NORTH_WEST_MASK = 128;
    private static final int WEST_MASK = 64;
    private static final int SOUTH_WEST_MASK = 32;
    private static final int SOUTH_MASK = 16;
    private static final int SOUTH_EAST_MASK = 8;
    private static final int EAST_MASK = 4;
    private static final int NORTH_EAST_MASK = 2;
    private static final int NORTH_MASK = 1;
    private static final int[][] DIRECTIONS = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
    private static final int DECAY_DISTANCE = 7;
    private static final int SIZE_BITS = 12;
    private static final int SIZE = 4096;
    static final Object2IntMap<String> LEAVES = (Object2IntMap) DataFixUtils.make(new Object2IntOpenHashMap(), (object2intopenhashmap) -> {
        object2intopenhashmap.put("minecraft:acacia_leaves", 0);
        object2intopenhashmap.put("minecraft:birch_leaves", 1);
        object2intopenhashmap.put("minecraft:dark_oak_leaves", 2);
        object2intopenhashmap.put("minecraft:jungle_leaves", 3);
        object2intopenhashmap.put("minecraft:oak_leaves", 4);
        object2intopenhashmap.put("minecraft:spruce_leaves", 5);
    });
    static final Set<String> LOGS = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", new String[]{"minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

    public DataConverterLeaves(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        OpticFinder<?> opticfinder = type.findField("Level");
        OpticFinder<?> opticfinder1 = opticfinder.type().findField("Sections");
        Type<?> type1 = opticfinder1.type();

        if (!(type1 instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
        } else {
            Type<?> type2 = ((ListType) type1).getElement();
            OpticFinder<?> opticfinder2 = DSL.typeFinder(type2);

            return this.fixTypeEverywhereTyped("Leaves fix", type, (typed) -> {
                return typed.updateTyped(opticfinder, (typed1) -> {
                    int[] aint = new int[]{0};
                    Typed<?> typed2 = typed1.updateTyped(opticfinder1, (typed3) -> {
                        Int2ObjectMap<DataConverterLeaves.a> int2objectmap = new Int2ObjectOpenHashMap((Map) typed3.getAllTyped(opticfinder2).stream().map((typed4) -> {
                            return new DataConverterLeaves.a(typed4, this.getInputSchema());
                        }).collect(Collectors.toMap(DataConverterLeaves.b::getIndex, (dataconverterleaves_a) -> {
                            return dataconverterleaves_a;
                        })));

                        if (int2objectmap.values().stream().allMatch(DataConverterLeaves.b::isSkippable)) {
                            return typed3;
                        } else {
                            List<IntSet> list = Lists.newArrayList();

                            int i;

                            for (i = 0; i < 7; ++i) {
                                list.add(new IntOpenHashSet());
                            }

                            ObjectIterator objectiterator = int2objectmap.values().iterator();

                            int j;
                            int k;

                            while (objectiterator.hasNext()) {
                                DataConverterLeaves.a dataconverterleaves_a = (DataConverterLeaves.a) objectiterator.next();

                                if (!dataconverterleaves_a.isSkippable()) {
                                    for (int l = 0; l < 4096; ++l) {
                                        int i1 = dataconverterleaves_a.getBlock(l);

                                        if (dataconverterleaves_a.isLog(i1)) {
                                            ((IntSet) list.get(0)).add(dataconverterleaves_a.getIndex() << 12 | l);
                                        } else if (dataconverterleaves_a.isLeaf(i1)) {
                                            j = this.getX(l);
                                            k = this.getZ(l);
                                            aint[0] |= getSideMask(j == 0, j == 15, k == 0, k == 15);
                                        }
                                    }
                                }
                            }

                            for (i = 1; i < 7; ++i) {
                                IntSet intset = (IntSet) list.get(i - 1);
                                IntSet intset1 = (IntSet) list.get(i);
                                IntIterator intiterator = intset.iterator();

                                while (intiterator.hasNext()) {
                                    j = intiterator.nextInt();
                                    k = this.getX(j);
                                    int j1 = this.getY(j);
                                    int k1 = this.getZ(j);
                                    int[][] aint1 = DataConverterLeaves.DIRECTIONS;
                                    int l1 = aint1.length;

                                    for (int i2 = 0; i2 < l1; ++i2) {
                                        int[] aint2 = aint1[i2];
                                        int j2 = k + aint2[0];
                                        int k2 = j1 + aint2[1];
                                        int l2 = k1 + aint2[2];

                                        if (j2 >= 0 && j2 <= 15 && l2 >= 0 && l2 <= 15 && k2 >= 0 && k2 <= 255) {
                                            DataConverterLeaves.a dataconverterleaves_a1 = (DataConverterLeaves.a) int2objectmap.get(k2 >> 4);

                                            if (dataconverterleaves_a1 != null && !dataconverterleaves_a1.isSkippable()) {
                                                int i3 = getIndex(j2, k2 & 15, l2);
                                                int j3 = dataconverterleaves_a1.getBlock(i3);

                                                if (dataconverterleaves_a1.isLeaf(j3)) {
                                                    int k3 = dataconverterleaves_a1.getDistance(j3);

                                                    if (k3 > i) {
                                                        dataconverterleaves_a1.setDistance(i3, j3, i);
                                                        intset1.add(getIndex(j2, k2, l2));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            return typed3.updateTyped(opticfinder2, (typed4) -> {
                                return ((DataConverterLeaves.a) int2objectmap.get(((Dynamic) typed4.get(DSL.remainderFinder())).get("Y").asInt(0))).write(typed4);
                            });
                        }
                    });

                    if (aint[0] != 0) {
                        typed2 = typed2.update(DSL.remainderFinder(), (dynamic) -> {
                            Dynamic<?> dynamic1 = (Dynamic) DataFixUtils.orElse(dynamic.get("UpgradeData").result(), dynamic.emptyMap());

                            return dynamic.set("UpgradeData", dynamic1.set("Sides", dynamic.createByte((byte) (dynamic1.get("Sides").asByte((byte) 0) | aint[0]))));
                        });
                    }

                    return typed2;
                });
            });
        }
    }

    public static int getIndex(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private int getX(int i) {
        return i & 15;
    }

    private int getY(int i) {
        return i >> 8 & 255;
    }

    private int getZ(int i) {
        return i >> 4 & 15;
    }

    public static int getSideMask(boolean flag, boolean flag1, boolean flag2, boolean flag3) {
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

    public static final class a extends DataConverterLeaves.b {

        private static final String PERSISTENT = "persistent";
        private static final String DECAYABLE = "decayable";
        private static final String DISTANCE = "distance";
        @Nullable
        private IntSet leaveIds;
        @Nullable
        private IntSet logIds;
        @Nullable
        private Int2IntMap stateToIdMap;

        public a(Typed<?> typed, Schema schema) {
            super(typed, schema);
        }

        @Override
        protected boolean skippable() {
            this.leaveIds = new IntOpenHashSet();
            this.logIds = new IntOpenHashSet();
            this.stateToIdMap = new Int2IntOpenHashMap();

            for (int i = 0; i < this.palette.size(); ++i) {
                Dynamic<?> dynamic = (Dynamic) this.palette.get(i);
                String s = dynamic.get("Name").asString("");

                if (DataConverterLeaves.LEAVES.containsKey(s)) {
                    boolean flag = Objects.equals(dynamic.get("Properties").get("decayable").asString(""), "false");

                    this.leaveIds.add(i);
                    this.stateToIdMap.put(this.getStateId(s, flag, 7), i);
                    this.palette.set(i, this.makeLeafTag(dynamic, s, flag, 7));
                }

                if (DataConverterLeaves.LOGS.contains(s)) {
                    this.logIds.add(i);
                }
            }

            return this.leaveIds.isEmpty() && this.logIds.isEmpty();
        }

        private Dynamic<?> makeLeafTag(Dynamic<?> dynamic, String s, boolean flag, int i) {
            Dynamic<?> dynamic1 = dynamic.emptyMap();

            dynamic1 = dynamic1.set("persistent", dynamic1.createString(flag ? "true" : "false"));
            dynamic1 = dynamic1.set("distance", dynamic1.createString(Integer.toString(i)));
            Dynamic<?> dynamic2 = dynamic.emptyMap();

            dynamic2 = dynamic2.set("Properties", dynamic1);
            dynamic2 = dynamic2.set("Name", dynamic2.createString(s));
            return dynamic2;
        }

        public boolean isLog(int i) {
            return this.logIds.contains(i);
        }

        public boolean isLeaf(int i) {
            return this.leaveIds.contains(i);
        }

        int getDistance(int i) {
            return this.isLog(i) ? 0 : Integer.parseInt(((Dynamic) this.palette.get(i)).get("Properties").get("distance").asString(""));
        }

        void setDistance(int i, int j, int k) {
            Dynamic<?> dynamic = (Dynamic) this.palette.get(j);
            String s = dynamic.get("Name").asString("");
            boolean flag = Objects.equals(dynamic.get("Properties").get("persistent").asString(""), "true");
            int l = this.getStateId(s, flag, k);
            int i1;

            if (!this.stateToIdMap.containsKey(l)) {
                i1 = this.palette.size();
                this.leaveIds.add(i1);
                this.stateToIdMap.put(l, i1);
                this.palette.add(this.makeLeafTag(dynamic, s, flag, k));
            }

            i1 = this.stateToIdMap.get(l);
            if (1 << this.storage.getBits() <= i1) {
                DataBitsPacked databitspacked = new DataBitsPacked(this.storage.getBits() + 1, 4096);

                for (int j1 = 0; j1 < 4096; ++j1) {
                    databitspacked.set(j1, this.storage.get(j1));
                }

                this.storage = databitspacked;
            }

            this.storage.set(i, i1);
        }
    }

    public abstract static class b {

        protected static final String BLOCK_STATES_TAG = "BlockStates";
        protected static final String NAME_TAG = "Name";
        protected static final String PROPERTIES_TAG = "Properties";
        private final Type<Pair<String, Dynamic<?>>> blockStateType;
        protected final OpticFinder<List<Pair<String, Dynamic<?>>>> paletteFinder;
        protected final List<Dynamic<?>> palette;
        protected final int index;
        @Nullable
        protected DataBitsPacked storage;

        public b(Typed<?> typed, Schema schema) {
            this.blockStateType = DSL.named(DataConverterTypes.BLOCK_STATE.typeName(), DSL.remainderType());
            this.paletteFinder = DSL.fieldFinder("Palette", DSL.list(this.blockStateType));
            if (!Objects.equals(schema.getType(DataConverterTypes.BLOCK_STATE), this.blockStateType)) {
                throw new IllegalStateException("Block state type is not what was expected.");
            } else {
                Optional<List<Pair<String, Dynamic<?>>>> optional = typed.getOptional(this.paletteFinder);

                this.palette = (List) optional.map((list) -> {
                    return (List) list.stream().map(Pair::getSecond).collect(Collectors.toList());
                }).orElse(ImmutableList.of());
                Dynamic<?> dynamic = (Dynamic) typed.get(DSL.remainderFinder());

                this.index = dynamic.get("Y").asInt(0);
                this.readStorage(dynamic);
            }
        }

        protected void readStorage(Dynamic<?> dynamic) {
            if (this.skippable()) {
                this.storage = null;
            } else {
                long[] along = dynamic.get("BlockStates").asLongStream().toArray();
                int i = Math.max(4, DataFixUtils.ceillog2(this.palette.size()));

                this.storage = new DataBitsPacked(i, 4096, along);
            }

        }

        public Typed<?> write(Typed<?> typed) {
            return this.isSkippable() ? typed : typed.update(DSL.remainderFinder(), (dynamic) -> {
                return dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.storage.getRaw())));
            }).set(this.paletteFinder, (List) this.palette.stream().map((dynamic) -> {
                return Pair.of(DataConverterTypes.BLOCK_STATE.typeName(), dynamic);
            }).collect(Collectors.toList()));
        }

        public boolean isSkippable() {
            return this.storage == null;
        }

        public int getBlock(int i) {
            return this.storage.get(i);
        }

        protected int getStateId(String s, boolean flag, int i) {
            return DataConverterLeaves.LEAVES.get(s) << 5 | (flag ? 16 : 0) | i;
        }

        int getIndex() {
            return this.index;
        }

        protected abstract boolean skippable();
    }
}
