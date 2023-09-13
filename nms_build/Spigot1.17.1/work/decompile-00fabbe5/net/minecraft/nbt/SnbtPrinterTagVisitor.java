package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;

public class SnbtPrinterTagVisitor implements TagVisitor {

    private static final Map<String, List<String>> KEY_ORDER = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put("{}", Lists.newArrayList(new String[]{"DataVersion", "author", "size", "data", "entities", "palette", "palettes"}));
        hashmap.put("{}.data.[].{}", Lists.newArrayList(new String[]{"pos", "state", "nbt"}));
        hashmap.put("{}.entities.[].{}", Lists.newArrayList(new String[]{"blockPos", "pos"}));
    });
    private static final Set<String> NO_INDENTATION = Sets.newHashSet(new String[]{"{}.size.[]", "{}.data.[].{}", "{}.palette.[].{}", "{}.entities.[].{}"});
    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
    private static final String NAME_VALUE_SEPARATOR = String.valueOf(':');
    private static final String ELEMENT_SEPARATOR = String.valueOf(',');
    private static final String LIST_OPEN = "[";
    private static final String LIST_CLOSE = "]";
    private static final String LIST_TYPE_SEPARATOR = ";";
    private static final String ELEMENT_SPACING = " ";
    private static final String STRUCT_OPEN = "{";
    private static final String STRUCT_CLOSE = "}";
    private static final String NEWLINE = "\n";
    private final String indentation;
    private final int depth;
    private final List<String> path;
    private String result;

    public SnbtPrinterTagVisitor() {
        this("    ", 0, Lists.newArrayList());
    }

    public SnbtPrinterTagVisitor(String s, int i, List<String> list) {
        this.indentation = s;
        this.depth = i;
        this.path = list;
    }

    public String a(NBTBase nbtbase) {
        nbtbase.a(this);
        return this.result;
    }

    @Override
    public void a(NBTTagString nbttagstring) {
        this.result = NBTTagString.b(nbttagstring.asString());
    }

    @Override
    public void a(NBTTagByte nbttagbyte) {
        this.result = nbttagbyte.k() + "b";
    }

    @Override
    public void a(NBTTagShort nbttagshort) {
        this.result = nbttagshort.k() + "s";
    }

    @Override
    public void a(NBTTagInt nbttagint) {
        this.result = String.valueOf(nbttagint.k());
    }

    @Override
    public void a(NBTTagLong nbttaglong) {
        this.result = nbttaglong.k() + "L";
    }

    @Override
    public void a(NBTTagFloat nbttagfloat) {
        this.result = nbttagfloat.asFloat() + "f";
    }

    @Override
    public void a(NBTTagDouble nbttagdouble) {
        this.result = nbttagdouble.asDouble() + "d";
    }

    @Override
    public void a(NBTTagByteArray nbttagbytearray) {
        StringBuilder stringbuilder = (new StringBuilder("[")).append("B").append(";");
        byte[] abyte = nbttagbytearray.getBytes();

        for (int i = 0; i < abyte.length; ++i) {
            stringbuilder.append(" ").append(abyte[i]).append("B");
            if (i != abyte.length - 1) {
                stringbuilder.append(SnbtPrinterTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        stringbuilder.append("]");
        this.result = stringbuilder.toString();
    }

    @Override
    public void a(NBTTagIntArray nbttagintarray) {
        StringBuilder stringbuilder = (new StringBuilder("[")).append("I").append(";");
        int[] aint = nbttagintarray.getInts();

        for (int i = 0; i < aint.length; ++i) {
            stringbuilder.append(" ").append(aint[i]);
            if (i != aint.length - 1) {
                stringbuilder.append(SnbtPrinterTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        stringbuilder.append("]");
        this.result = stringbuilder.toString();
    }

    @Override
    public void a(NBTTagLongArray nbttaglongarray) {
        String s = "L";
        StringBuilder stringbuilder = (new StringBuilder("[")).append("L").append(";");
        long[] along = nbttaglongarray.getLongs();

        for (int i = 0; i < along.length; ++i) {
            stringbuilder.append(" ").append(along[i]).append("L");
            if (i != along.length - 1) {
                stringbuilder.append(SnbtPrinterTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        stringbuilder.append("]");
        this.result = stringbuilder.toString();
    }

    @Override
    public void a(NBTTagList nbttaglist) {
        if (nbttaglist.isEmpty()) {
            this.result = "[]";
        } else {
            StringBuilder stringbuilder = new StringBuilder("[");

            this.b("[]");
            String s = SnbtPrinterTagVisitor.NO_INDENTATION.contains(this.a()) ? "" : this.indentation;

            if (!s.isEmpty()) {
                stringbuilder.append("\n");
            }

            for (int i = 0; i < nbttaglist.size(); ++i) {
                stringbuilder.append(Strings.repeat(s, this.depth + 1));
                stringbuilder.append((new SnbtPrinterTagVisitor(s, this.depth + 1, this.path)).a(nbttaglist.get(i)));
                if (i != nbttaglist.size() - 1) {
                    stringbuilder.append(SnbtPrinterTagVisitor.ELEMENT_SEPARATOR).append(s.isEmpty() ? " " : "\n");
                }
            }

            if (!s.isEmpty()) {
                stringbuilder.append("\n").append(Strings.repeat(s, this.depth));
            }

            stringbuilder.append("]");
            this.result = stringbuilder.toString();
            this.b();
        }
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.isEmpty()) {
            this.result = "{}";
        } else {
            StringBuilder stringbuilder = new StringBuilder("{");

            this.b("{}");
            String s = SnbtPrinterTagVisitor.NO_INDENTATION.contains(this.a()) ? "" : this.indentation;

            if (!s.isEmpty()) {
                stringbuilder.append("\n");
            }

            Collection<String> collection = this.b(nbttagcompound);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();
                NBTBase nbtbase = nbttagcompound.get(s1);

                this.b(s1);
                stringbuilder.append(Strings.repeat(s, this.depth + 1)).append(a(s1)).append(SnbtPrinterTagVisitor.NAME_VALUE_SEPARATOR).append(" ").append((new SnbtPrinterTagVisitor(s, this.depth + 1, this.path)).a(nbtbase));
                this.b();
                if (iterator.hasNext()) {
                    stringbuilder.append(SnbtPrinterTagVisitor.ELEMENT_SEPARATOR).append(s.isEmpty() ? " " : "\n");
                }
            }

            if (!s.isEmpty()) {
                stringbuilder.append("\n").append(Strings.repeat(s, this.depth));
            }

            stringbuilder.append("}");
            this.result = stringbuilder.toString();
            this.b();
        }
    }

    private void b() {
        this.path.remove(this.path.size() - 1);
    }

    private void b(String s) {
        this.path.add(s);
    }

    protected List<String> b(NBTTagCompound nbttagcompound) {
        Set<String> set = Sets.newHashSet(nbttagcompound.getKeys());
        List<String> list = Lists.newArrayList();
        List<String> list1 = (List) SnbtPrinterTagVisitor.KEY_ORDER.get(this.a());

        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                if (set.remove(s)) {
                    list.add(s);
                }
            }

            if (!set.isEmpty()) {
                Stream stream = set.stream().sorted();

                Objects.requireNonNull(list);
                stream.forEach(list::add);
            }
        } else {
            list.addAll(set);
            Collections.sort(list);
        }

        return list;
    }

    public String a() {
        return String.join(".", this.path);
    }

    protected static String a(String s) {
        return SnbtPrinterTagVisitor.SIMPLE_VALUE.matcher(s).matches() ? s : NBTTagString.b(s);
    }

    @Override
    public void a(NBTTagEnd nbttagend) {}
}
