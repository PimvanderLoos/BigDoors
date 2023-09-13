package net.minecraft.nbt.visitors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagType;
import net.minecraft.nbt.StreamTagVisitor;

public class CollectFields extends CollectToTag {

    private int fieldsToGetCount;
    private final Set<NBTTagType<?>> wantedTypes;
    private final Deque<CollectFields.a> stack = new ArrayDeque();

    public CollectFields(CollectFields.b... acollectfields_b) {
        this.fieldsToGetCount = acollectfields_b.length;
        Builder<NBTTagType<?>> builder = ImmutableSet.builder();
        CollectFields.a collectfields_a = new CollectFields.a(1);
        CollectFields.b[] acollectfields_b1 = acollectfields_b;
        int i = acollectfields_b.length;

        for (int j = 0; j < i; ++j) {
            CollectFields.b collectfields_b = acollectfields_b1[j];

            collectfields_a.addEntry(collectfields_b);
            builder.add(collectfields_b.type);
        }

        this.stack.push(collectfields_a);
        builder.add(NBTTagCompound.TYPE);
        this.wantedTypes = builder.build();
    }

    @Override
    public StreamTagVisitor.b visitRootEntry(NBTTagType<?> nbttagtype) {
        return nbttagtype != NBTTagCompound.TYPE ? StreamTagVisitor.b.HALT : super.visitRootEntry(nbttagtype);
    }

    @Override
    public StreamTagVisitor.a visitEntry(NBTTagType<?> nbttagtype) {
        CollectFields.a collectfields_a = (CollectFields.a) this.stack.element();

        return this.depth() > collectfields_a.depth() ? super.visitEntry(nbttagtype) : (this.fieldsToGetCount <= 0 ? StreamTagVisitor.a.HALT : (!this.wantedTypes.contains(nbttagtype) ? StreamTagVisitor.a.SKIP : super.visitEntry(nbttagtype)));
    }

    @Override
    public StreamTagVisitor.a visitEntry(NBTTagType<?> nbttagtype, String s) {
        CollectFields.a collectfields_a = (CollectFields.a) this.stack.element();

        if (this.depth() > collectfields_a.depth()) {
            return super.visitEntry(nbttagtype, s);
        } else if (collectfields_a.fieldsToGet.remove(s, nbttagtype)) {
            --this.fieldsToGetCount;
            return super.visitEntry(nbttagtype, s);
        } else {
            if (nbttagtype == NBTTagCompound.TYPE) {
                CollectFields.a collectfields_a1 = (CollectFields.a) collectfields_a.fieldsToRecurse.get(s);

                if (collectfields_a1 != null) {
                    this.stack.push(collectfields_a1);
                    return super.visitEntry(nbttagtype, s);
                }
            }

            return StreamTagVisitor.a.SKIP;
        }
    }

    @Override
    public StreamTagVisitor.b visitContainerEnd() {
        if (this.depth() == ((CollectFields.a) this.stack.element()).depth()) {
            this.stack.pop();
        }

        return super.visitContainerEnd();
    }

    public int getMissingFieldCount() {
        return this.fieldsToGetCount;
    }

    private static record a(int a, Map<String, NBTTagType<?>> b, Map<String, CollectFields.a> c) {

        private final int depth;
        final Map<String, NBTTagType<?>> fieldsToGet;
        final Map<String, CollectFields.a> fieldsToRecurse;

        public a(int i) {
            this(i, new HashMap(), new HashMap());
        }

        private a(int i, Map<String, NBTTagType<?>> map, Map<String, CollectFields.a> map1) {
            this.depth = i;
            this.fieldsToGet = map;
            this.fieldsToRecurse = map1;
        }

        public void addEntry(CollectFields.b collectfields_b) {
            if (this.depth <= collectfields_b.path.size()) {
                ((CollectFields.a) this.fieldsToRecurse.computeIfAbsent((String) collectfields_b.path.get(this.depth - 1), (s) -> {
                    return new CollectFields.a(this.depth + 1);
                })).addEntry(collectfields_b);
            } else {
                this.fieldsToGet.put(collectfields_b.name, collectfields_b.type);
            }

        }

        public int depth() {
            return this.depth;
        }

        public Map<String, NBTTagType<?>> fieldsToGet() {
            return this.fieldsToGet;
        }

        public Map<String, CollectFields.a> fieldsToRecurse() {
            return this.fieldsToRecurse;
        }
    }

    public static record b(List<String> a, NBTTagType<?> b, String c) {

        final List<String> path;
        final NBTTagType<?> type;
        final String name;

        public b(NBTTagType<?> nbttagtype, String s) {
            this(List.of(), nbttagtype, s);
        }

        public b(String s, NBTTagType<?> nbttagtype, String s1) {
            this(List.of(s), nbttagtype, s1);
        }

        public b(String s, String s1, NBTTagType<?> nbttagtype, String s2) {
            this(List.of(s, s1), nbttagtype, s2);
        }

        public b(List<String> list, NBTTagType<?> nbttagtype, String s) {
            this.path = list;
            this.type = nbttagtype;
            this.name = s;
        }

        public List<String> path() {
            return this.path;
        }

        public NBTTagType<?> type() {
            return this.type;
        }

        public String name() {
            return this.name;
        }
    }
}
