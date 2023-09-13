package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import org.slf4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int INLINE_LIST_THRESHOLD = 8;
    private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList(1, 2, 3, 4, 5, 6));
    private static final EnumChatFormat SYNTAX_HIGHLIGHTING_KEY = EnumChatFormat.AQUA;
    private static final EnumChatFormat SYNTAX_HIGHLIGHTING_STRING = EnumChatFormat.GREEN;
    private static final EnumChatFormat SYNTAX_HIGHLIGHTING_NUMBER = EnumChatFormat.GOLD;
    private static final EnumChatFormat SYNTAX_HIGHLIGHTING_NUMBER_TYPE = EnumChatFormat.RED;
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
    private IChatBaseComponent result;

    public TextComponentTagVisitor(String s, int i) {
        this.result = CommonComponents.EMPTY;
        this.indentation = s;
        this.depth = i;
    }

    public IChatBaseComponent visit(NBTBase nbtbase) {
        nbtbase.accept((TagVisitor) this);
        return this.result;
    }

    @Override
    public void visitString(NBTTagString nbttagstring) {
        String s = NBTTagString.quoteAndEscape(nbttagstring.getAsString());
        String s1 = s.substring(0, 1);
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal(s.substring(1, s.length() - 1)).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_STRING);

        this.result = IChatBaseComponent.literal(s1).append((IChatBaseComponent) ichatmutablecomponent).append(s1);
    }

    @Override
    public void visitByte(NBTTagByte nbttagbyte) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("b").withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = IChatBaseComponent.literal(String.valueOf(nbttagbyte.getAsNumber())).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitShort(NBTTagShort nbttagshort) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("s").withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = IChatBaseComponent.literal(String.valueOf(nbttagshort.getAsNumber())).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitInt(NBTTagInt nbttagint) {
        this.result = IChatBaseComponent.literal(String.valueOf(nbttagint.getAsNumber())).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitLong(NBTTagLong nbttaglong) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("L").withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = IChatBaseComponent.literal(String.valueOf(nbttaglong.getAsNumber())).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitFloat(NBTTagFloat nbttagfloat) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("f").withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = IChatBaseComponent.literal(String.valueOf(nbttagfloat.getAsFloat())).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitDouble(NBTTagDouble nbttagdouble) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("d").withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = IChatBaseComponent.literal(String.valueOf(nbttagdouble.getAsDouble())).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitByteArray(NBTTagByteArray nbttagbytearray) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("B").withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = IChatBaseComponent.literal("[").append((IChatBaseComponent) ichatmutablecomponent).append(";");
        byte[] abyte = nbttagbytearray.getAsByteArray();

        for (int i = 0; i < abyte.length; ++i) {
            IChatMutableComponent ichatmutablecomponent2 = IChatBaseComponent.literal(String.valueOf(abyte[i])).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);

            ichatmutablecomponent1.append(" ").append((IChatBaseComponent) ichatmutablecomponent2).append((IChatBaseComponent) ichatmutablecomponent);
            if (i != abyte.length - 1) {
                ichatmutablecomponent1.append(TextComponentTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        ichatmutablecomponent1.append("]");
        this.result = ichatmutablecomponent1;
    }

    @Override
    public void visitIntArray(NBTTagIntArray nbttagintarray) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("I").withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = IChatBaseComponent.literal("[").append((IChatBaseComponent) ichatmutablecomponent).append(";");
        int[] aint = nbttagintarray.getAsIntArray();

        for (int i = 0; i < aint.length; ++i) {
            ichatmutablecomponent1.append(" ").append((IChatBaseComponent) IChatBaseComponent.literal(String.valueOf(aint[i])).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER));
            if (i != aint.length - 1) {
                ichatmutablecomponent1.append(TextComponentTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        ichatmutablecomponent1.append("]");
        this.result = ichatmutablecomponent1;
    }

    @Override
    public void visitLongArray(NBTTagLongArray nbttaglongarray) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("L").withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = IChatBaseComponent.literal("[").append((IChatBaseComponent) ichatmutablecomponent).append(";");
        long[] along = nbttaglongarray.getAsLongArray();

        for (int i = 0; i < along.length; ++i) {
            IChatMutableComponent ichatmutablecomponent2 = IChatBaseComponent.literal(String.valueOf(along[i])).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);

            ichatmutablecomponent1.append(" ").append((IChatBaseComponent) ichatmutablecomponent2).append((IChatBaseComponent) ichatmutablecomponent);
            if (i != along.length - 1) {
                ichatmutablecomponent1.append(TextComponentTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        ichatmutablecomponent1.append("]");
        this.result = ichatmutablecomponent1;
    }

    @Override
    public void visitList(NBTTagList nbttaglist) {
        if (nbttaglist.isEmpty()) {
            this.result = IChatBaseComponent.literal("[]");
        } else if (TextComponentTagVisitor.INLINE_ELEMENT_TYPES.contains(nbttaglist.getElementType()) && nbttaglist.size() <= 8) {
            String s = TextComponentTagVisitor.ELEMENT_SEPARATOR + " ";
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("[");

            for (int i = 0; i < nbttaglist.size(); ++i) {
                if (i != 0) {
                    ichatmutablecomponent.append(s);
                }

                ichatmutablecomponent.append((new TextComponentTagVisitor(this.indentation, this.depth)).visit(nbttaglist.get(i)));
            }

            ichatmutablecomponent.append("]");
            this.result = ichatmutablecomponent;
        } else {
            IChatMutableComponent ichatmutablecomponent1 = IChatBaseComponent.literal("[");

            if (!this.indentation.isEmpty()) {
                ichatmutablecomponent1.append("\n");
            }

            for (int j = 0; j < nbttaglist.size(); ++j) {
                IChatMutableComponent ichatmutablecomponent2 = IChatBaseComponent.literal(Strings.repeat(this.indentation, this.depth + 1));

                ichatmutablecomponent2.append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(nbttaglist.get(j)));
                if (j != nbttaglist.size() - 1) {
                    ichatmutablecomponent2.append(TextComponentTagVisitor.ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
                }

                ichatmutablecomponent1.append((IChatBaseComponent) ichatmutablecomponent2);
            }

            if (!this.indentation.isEmpty()) {
                ichatmutablecomponent1.append("\n").append(Strings.repeat(this.indentation, this.depth));
            }

            ichatmutablecomponent1.append("]");
            this.result = ichatmutablecomponent1;
        }
    }

    @Override
    public void visitCompound(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.isEmpty()) {
            this.result = IChatBaseComponent.literal("{}");
        } else {
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("{");
            Collection<String> collection = nbttagcompound.getAllKeys();

            if (TextComponentTagVisitor.LOGGER.isDebugEnabled()) {
                List<String> list = Lists.newArrayList(nbttagcompound.getAllKeys());

                Collections.sort(list);
                collection = list;
            }

            if (!this.indentation.isEmpty()) {
                ichatmutablecomponent.append("\n");
            }

            IChatMutableComponent ichatmutablecomponent1;

            for (Iterator iterator = ((Collection) collection).iterator(); iterator.hasNext(); ichatmutablecomponent.append((IChatBaseComponent) ichatmutablecomponent1)) {
                String s = (String) iterator.next();

                ichatmutablecomponent1 = IChatBaseComponent.literal(Strings.repeat(this.indentation, this.depth + 1)).append(handleEscapePretty(s)).append(TextComponentTagVisitor.NAME_VALUE_SEPARATOR).append(" ").append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(nbttagcompound.get(s)));
                if (iterator.hasNext()) {
                    ichatmutablecomponent1.append(TextComponentTagVisitor.ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
                }
            }

            if (!this.indentation.isEmpty()) {
                ichatmutablecomponent.append("\n").append(Strings.repeat(this.indentation, this.depth));
            }

            ichatmutablecomponent.append("}");
            this.result = ichatmutablecomponent;
        }
    }

    protected static IChatBaseComponent handleEscapePretty(String s) {
        if (TextComponentTagVisitor.SIMPLE_VALUE.matcher(s).matches()) {
            return IChatBaseComponent.literal(s).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_KEY);
        } else {
            String s1 = NBTTagString.quoteAndEscape(s);
            String s2 = s1.substring(0, 1);
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal(s1.substring(1, s1.length() - 1)).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_KEY);

            return IChatBaseComponent.literal(s2).append((IChatBaseComponent) ichatmutablecomponent).append(s2);
        }
    }

    @Override
    public void visitEnd(NBTTagEnd nbttagend) {
        this.result = CommonComponents.EMPTY;
    }
}
