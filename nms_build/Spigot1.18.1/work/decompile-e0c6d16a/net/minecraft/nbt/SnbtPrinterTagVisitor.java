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

    private static final Map<String, List<String>> KEY_ORDER = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
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
        this.result = "";
        this.indentation = s;
        this.depth = i;
        this.path = list;
    }

    public String visit(NBTBase nbtbase) {
        nbtbase.accept((TagVisitor) this);
        return this.result;
    }

    @Override
    public void visitString(NBTTagString nbttagstring) {
        this.result = NBTTagString.quoteAndEscape(nbttagstring.getAsString());
    }

    @Override
    public void visitByte(NBTTagByte nbttagbyte) {
        this.result = nbttagbyte.getAsNumber() + "b";
    }

    @Override
    public void visitShort(NBTTagShort nbttagshort) {
        this.result = nbttagshort.getAsNumber() + "s";
    }

    @Override
    public void visitInt(NBTTagInt nbttagint) {
        this.result = String.valueOf(nbttagint.getAsNumber());
    }

    @Override
    public void visitLong(NBTTagLong nbttaglong) {
        this.result = nbttaglong.getAsNumber() + "L";
    }

    @Override
    public void visitFloat(NBTTagFloat nbttagfloat) {
        this.result = nbttagfloat.getAsFloat() + "f";
    }

    @Override
    public void visitDouble(NBTTagDouble nbttagdouble) {
        this.result = nbttagdouble.getAsDouble() + "d";
    }

    @Override
    public void visitByteArray(NBTTagByteArray nbttagbytearray) {
        StringBuilder stringbuilder = (new StringBuilder("[")).append("B").append(";");
        byte[] abyte = nbttagbytearray.getAsByteArray();

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
    public void visitIntArray(NBTTagIntArray nbttagintarray) {
        StringBuilder stringbuilder = (new StringBuilder("[")).append("I").append(";");
        int[] aint = nbttagintarray.getAsIntArray();

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
    public void visitLongArray(NBTTagLongArray nbttaglongarray) {
        String s = "L";
        StringBuilder stringbuilder = (new StringBuilder("[")).append("L").append(";");
        long[] along = nbttaglongarray.getAsLongArray();

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
    public void visitList(NBTTagList nbttaglist) {
        if (nbttaglist.isEmpty()) {
            this.result = "[]";
        } else {
            StringBuilder stringbuilder = new StringBuilder("[");

            this.pushPath("[]");
            String s = SnbtPrinterTagVisitor.NO_INDENTATION.contains(this.pathString()) ? "" : this.indentation;

            if (!s.isEmpty()) {
                stringbuilder.append("\n");
            }

            for (int i = 0; i < nbttaglist.size(); ++i) {
                stringbuilder.append(Strings.repeat(s, this.depth + 1));
                stringbuilder.append((new SnbtPrinterTagVisitor(s, this.depth + 1, this.path)).visit(nbttaglist.get(i)));
                if (i != nbttaglist.size() - 1) {
                    stringbuilder.append(SnbtPrinterTagVisitor.ELEMENT_SEPARATOR).append(s.isEmpty() ? " " : "\n");
                }
            }

            if (!s.isEmpty()) {
                stringbuilder.append("\n").append(Strings.repeat(s, this.depth));
            }

            stringbuilder.append("]");
            this.result = stringbuilder.toString();
            this.popPath();
        }
    }

    @Override
    public void visitCompound(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.isEmpty()) {
            this.result = "{}";
        } else {
            StringBuilder stringbuilder = new StringBuilder("{");

            this.pushPath("{}");
            String s = SnbtPrinterTagVisitor.NO_INDENTATION.contains(this.pathString()) ? "" : this.indentation;

            if (!s.isEmpty()) {
                stringbuilder.append("\n");
            }

            Collection<String> collection = this.getKeys(nbttagcompound);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();
                NBTBase nbtbase = nbttagcompound.get(s1);

                this.pushPath(s1);
                stringbuilder.append(Strings.repeat(s, this.depth + 1)).append(handleEscapePretty(s1)).append(SnbtPrinterTagVisitor.NAME_VALUE_SEPARATOR).append(" ").append((new SnbtPrinterTagVisitor(s, this.depth + 1, this.path)).visit(nbtbase));
                this.popPath();
                if (iterator.hasNext()) {
                    stringbuilder.append(SnbtPrinterTagVisitor.ELEMENT_SEPARATOR).append(s.isEmpty() ? " " : "\n");
                }
            }

            if (!s.isEmpty()) {
                stringbuilder.append("\n").append(Strings.repeat(s, this.depth));
            }

            stringbuilder.append("}");
            this.result = stringbuilder.toString();
            this.popPath();
        }
    }

    private void popPath() {
        this.path.remove(this.path.size() - 1);
    }

    private void pushPath(String s) {
        this.path.add(s);
    }

    protected List<String> getKeys(NBTTagCompound nbttagcompound) {
        Set<String> set = Sets.newHashSet(nbttagcompound.getAllKeys());
        List<String> list = Lists.newArrayList();
        List<String> list1 = (List) SnbtPrinterTagVisitor.KEY_ORDER.get(this.pathString());

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

    public String pathString() {
        return String.join(".", this.path);
    }

    protected static String handleEscapePretty(String s) {
        return SnbtPrinterTagVisitor.SIMPLE_VALUE.matcher(s).matches() ? s : NBTTagString.quoteAndEscape(s);
    }

    @Override
    public void visitEnd(NBTTagEnd nbttagend) {}
}
