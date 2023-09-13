package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {

    private static final Logger LOGGER = LogManager.getLogger();
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
        this.result = ChatComponentText.EMPTY;
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
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText(s.substring(1, s.length() - 1))).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_STRING);

        this.result = (new ChatComponentText(s1)).append((IChatBaseComponent) ichatmutablecomponent).append(s1);
    }

    @Override
    public void visitByte(NBTTagByte nbttagbyte) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("b")).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttagbyte.getAsNumber()))).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitShort(NBTTagShort nbttagshort) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("s")).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttagshort.getAsNumber()))).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitInt(NBTTagInt nbttagint) {
        this.result = (new ChatComponentText(String.valueOf(nbttagint.getAsNumber()))).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitLong(NBTTagLong nbttaglong) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("L")).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttaglong.getAsNumber()))).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitFloat(NBTTagFloat nbttagfloat) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("f")).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttagfloat.getAsFloat()))).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitDouble(NBTTagDouble nbttagdouble) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("d")).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttagdouble.getAsDouble()))).append((IChatBaseComponent) ichatmutablecomponent).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitByteArray(NBTTagByteArray nbttagbytearray) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("B")).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText("[")).append((IChatBaseComponent) ichatmutablecomponent).append(";");
        byte[] abyte = nbttagbytearray.getAsByteArray();

        for (int i = 0; i < abyte.length; ++i) {
            IChatMutableComponent ichatmutablecomponent2 = (new ChatComponentText(String.valueOf(abyte[i]))).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);

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
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("I")).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText("[")).append((IChatBaseComponent) ichatmutablecomponent).append(";");
        int[] aint = nbttagintarray.getAsIntArray();

        for (int i = 0; i < aint.length; ++i) {
            ichatmutablecomponent1.append(" ").append((IChatBaseComponent) (new ChatComponentText(String.valueOf(aint[i]))).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER));
            if (i != aint.length - 1) {
                ichatmutablecomponent1.append(TextComponentTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        ichatmutablecomponent1.append("]");
        this.result = ichatmutablecomponent1;
    }

    @Override
    public void visitLongArray(NBTTagLongArray nbttaglongarray) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("L")).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText("[")).append((IChatBaseComponent) ichatmutablecomponent).append(";");
        long[] along = nbttaglongarray.getAsLongArray();

        for (int i = 0; i < along.length; ++i) {
            IChatMutableComponent ichatmutablecomponent2 = (new ChatComponentText(String.valueOf(along[i]))).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);

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
            this.result = new ChatComponentText("[]");
        } else if (TextComponentTagVisitor.INLINE_ELEMENT_TYPES.contains(nbttaglist.getElementType()) && nbttaglist.size() <= 8) {
            String s = TextComponentTagVisitor.ELEMENT_SEPARATOR + " ";
            ChatComponentText chatcomponenttext = new ChatComponentText("[");

            for (int i = 0; i < nbttaglist.size(); ++i) {
                if (i != 0) {
                    chatcomponenttext.append(s);
                }

                chatcomponenttext.append((new TextComponentTagVisitor(this.indentation, this.depth)).visit(nbttaglist.get(i)));
            }

            chatcomponenttext.append("]");
            this.result = chatcomponenttext;
        } else {
            ChatComponentText chatcomponenttext1 = new ChatComponentText("[");

            if (!this.indentation.isEmpty()) {
                chatcomponenttext1.append("\n");
            }

            for (int j = 0; j < nbttaglist.size(); ++j) {
                ChatComponentText chatcomponenttext2 = new ChatComponentText(Strings.repeat(this.indentation, this.depth + 1));

                chatcomponenttext2.append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(nbttaglist.get(j)));
                if (j != nbttaglist.size() - 1) {
                    chatcomponenttext2.append(TextComponentTagVisitor.ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
                }

                chatcomponenttext1.append((IChatBaseComponent) chatcomponenttext2);
            }

            if (!this.indentation.isEmpty()) {
                chatcomponenttext1.append("\n").append(Strings.repeat(this.indentation, this.depth));
            }

            chatcomponenttext1.append("]");
            this.result = chatcomponenttext1;
        }
    }

    @Override
    public void visitCompound(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.isEmpty()) {
            this.result = new ChatComponentText("{}");
        } else {
            ChatComponentText chatcomponenttext = new ChatComponentText("{");
            Collection<String> collection = nbttagcompound.getAllKeys();

            if (TextComponentTagVisitor.LOGGER.isDebugEnabled()) {
                List<String> list = Lists.newArrayList(nbttagcompound.getAllKeys());

                Collections.sort(list);
                collection = list;
            }

            if (!this.indentation.isEmpty()) {
                chatcomponenttext.append("\n");
            }

            IChatMutableComponent ichatmutablecomponent;

            for (Iterator iterator = ((Collection) collection).iterator(); iterator.hasNext(); chatcomponenttext.append((IChatBaseComponent) ichatmutablecomponent)) {
                String s = (String) iterator.next();

                ichatmutablecomponent = (new ChatComponentText(Strings.repeat(this.indentation, this.depth + 1))).append(handleEscapePretty(s)).append(TextComponentTagVisitor.NAME_VALUE_SEPARATOR).append(" ").append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(nbttagcompound.get(s)));
                if (iterator.hasNext()) {
                    ichatmutablecomponent.append(TextComponentTagVisitor.ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
                }
            }

            if (!this.indentation.isEmpty()) {
                chatcomponenttext.append("\n").append(Strings.repeat(this.indentation, this.depth));
            }

            chatcomponenttext.append("}");
            this.result = chatcomponenttext;
        }
    }

    protected static IChatBaseComponent handleEscapePretty(String s) {
        if (TextComponentTagVisitor.SIMPLE_VALUE.matcher(s).matches()) {
            return (new ChatComponentText(s)).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_KEY);
        } else {
            String s1 = NBTTagString.quoteAndEscape(s);
            String s2 = s1.substring(0, 1);
            IChatMutableComponent ichatmutablecomponent = (new ChatComponentText(s1.substring(1, s1.length() - 1))).withStyle(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_KEY);

            return (new ChatComponentText(s2)).append((IChatBaseComponent) ichatmutablecomponent).append(s2);
        }
    }

    @Override
    public void visitEnd(NBTTagEnd nbttagend) {
        this.result = ChatComponentText.EMPTY;
    }
}
