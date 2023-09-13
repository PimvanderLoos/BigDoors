package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class StringTagVisitor implements TagVisitor {

    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
    private final StringBuilder builder = new StringBuilder();

    public StringTagVisitor() {}

    public String a(NBTBase nbtbase) {
        nbtbase.a(this);
        return this.builder.toString();
    }

    @Override
    public void a(NBTTagString nbttagstring) {
        this.builder.append(NBTTagString.b(nbttagstring.asString()));
    }

    @Override
    public void a(NBTTagByte nbttagbyte) {
        this.builder.append(nbttagbyte.k()).append('b');
    }

    @Override
    public void a(NBTTagShort nbttagshort) {
        this.builder.append(nbttagshort.k()).append('s');
    }

    @Override
    public void a(NBTTagInt nbttagint) {
        this.builder.append(nbttagint.k());
    }

    @Override
    public void a(NBTTagLong nbttaglong) {
        this.builder.append(nbttaglong.k()).append('L');
    }

    @Override
    public void a(NBTTagFloat nbttagfloat) {
        this.builder.append(nbttagfloat.asFloat()).append('f');
    }

    @Override
    public void a(NBTTagDouble nbttagdouble) {
        this.builder.append(nbttagdouble.asDouble()).append('d');
    }

    @Override
    public void a(NBTTagByteArray nbttagbytearray) {
        this.builder.append("[B;");
        byte[] abyte = nbttagbytearray.getBytes();

        for (int i = 0; i < abyte.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }

            this.builder.append(abyte[i]).append('B');
        }

        this.builder.append(']');
    }

    @Override
    public void a(NBTTagIntArray nbttagintarray) {
        this.builder.append("[I;");
        int[] aint = nbttagintarray.getInts();

        for (int i = 0; i < aint.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }

            this.builder.append(aint[i]);
        }

        this.builder.append(']');
    }

    @Override
    public void a(NBTTagLongArray nbttaglongarray) {
        this.builder.append("[L;");
        long[] along = nbttaglongarray.getLongs();

        for (int i = 0; i < along.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }

            this.builder.append(along[i]).append('L');
        }

        this.builder.append(']');
    }

    @Override
    public void a(NBTTagList nbttaglist) {
        this.builder.append('[');

        for (int i = 0; i < nbttaglist.size(); ++i) {
            if (i != 0) {
                this.builder.append(',');
            }

            this.builder.append((new StringTagVisitor()).a(nbttaglist.get(i)));
        }

        this.builder.append(']');
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.builder.append('{');
        List<String> list = Lists.newArrayList(nbttagcompound.getKeys());

        Collections.sort(list);

        String s;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); this.builder.append(a(s)).append(':').append((new StringTagVisitor()).a(nbttagcompound.get(s)))) {
            s = (String) iterator.next();
            if (this.builder.length() != 1) {
                this.builder.append(',');
            }
        }

        this.builder.append('}');
    }

    protected static String a(String s) {
        return StringTagVisitor.SIMPLE_VALUE.matcher(s).matches() ? s : NBTTagString.b(s);
    }

    @Override
    public void a(NBTTagEnd nbttagend) {
        this.builder.append("END");
    }
}
