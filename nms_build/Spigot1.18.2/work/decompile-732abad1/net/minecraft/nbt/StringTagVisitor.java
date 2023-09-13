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

    public String visit(NBTBase nbtbase) {
        nbtbase.accept((TagVisitor) this);
        return this.builder.toString();
    }

    @Override
    public void visitString(NBTTagString nbttagstring) {
        this.builder.append(NBTTagString.quoteAndEscape(nbttagstring.getAsString()));
    }

    @Override
    public void visitByte(NBTTagByte nbttagbyte) {
        this.builder.append(nbttagbyte.getAsNumber()).append('b');
    }

    @Override
    public void visitShort(NBTTagShort nbttagshort) {
        this.builder.append(nbttagshort.getAsNumber()).append('s');
    }

    @Override
    public void visitInt(NBTTagInt nbttagint) {
        this.builder.append(nbttagint.getAsNumber());
    }

    @Override
    public void visitLong(NBTTagLong nbttaglong) {
        this.builder.append(nbttaglong.getAsNumber()).append('L');
    }

    @Override
    public void visitFloat(NBTTagFloat nbttagfloat) {
        this.builder.append(nbttagfloat.getAsFloat()).append('f');
    }

    @Override
    public void visitDouble(NBTTagDouble nbttagdouble) {
        this.builder.append(nbttagdouble.getAsDouble()).append('d');
    }

    @Override
    public void visitByteArray(NBTTagByteArray nbttagbytearray) {
        this.builder.append("[B;");
        byte[] abyte = nbttagbytearray.getAsByteArray();

        for (int i = 0; i < abyte.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }

            this.builder.append(abyte[i]).append('B');
        }

        this.builder.append(']');
    }

    @Override
    public void visitIntArray(NBTTagIntArray nbttagintarray) {
        this.builder.append("[I;");
        int[] aint = nbttagintarray.getAsIntArray();

        for (int i = 0; i < aint.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }

            this.builder.append(aint[i]);
        }

        this.builder.append(']');
    }

    @Override
    public void visitLongArray(NBTTagLongArray nbttaglongarray) {
        this.builder.append("[L;");
        long[] along = nbttaglongarray.getAsLongArray();

        for (int i = 0; i < along.length; ++i) {
            if (i != 0) {
                this.builder.append(',');
            }

            this.builder.append(along[i]).append('L');
        }

        this.builder.append(']');
    }

    @Override
    public void visitList(NBTTagList nbttaglist) {
        this.builder.append('[');

        for (int i = 0; i < nbttaglist.size(); ++i) {
            if (i != 0) {
                this.builder.append(',');
            }

            this.builder.append((new StringTagVisitor()).visit(nbttaglist.get(i)));
        }

        this.builder.append(']');
    }

    @Override
    public void visitCompound(NBTTagCompound nbttagcompound) {
        this.builder.append('{');
        List<String> list = Lists.newArrayList(nbttagcompound.getAllKeys());

        Collections.sort(list);

        String s;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); this.builder.append(handleEscape(s)).append(':').append((new StringTagVisitor()).visit(nbttagcompound.get(s)))) {
            s = (String) iterator.next();
            if (this.builder.length() != 1) {
                this.builder.append(',');
            }
        }

        this.builder.append('}');
    }

    protected static String handleEscape(String s) {
        return StringTagVisitor.SIMPLE_VALUE.matcher(s).matches() ? s : NBTTagString.quoteAndEscape(s);
    }

    @Override
    public void visitEnd(NBTTagEnd nbttagend) {
        this.builder.append("END");
    }
}
