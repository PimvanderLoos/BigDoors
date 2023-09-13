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
        this.indentation = s;
        this.depth = i;
    }

    public IChatBaseComponent a(NBTBase nbtbase) {
        nbtbase.a(this);
        return this.result;
    }

    @Override
    public void a(NBTTagString nbttagstring) {
        String s = NBTTagString.b(nbttagstring.asString());
        String s1 = s.substring(0, 1);
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText(s.substring(1, s.length() - 1))).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_STRING);

        this.result = (new ChatComponentText(s1)).addSibling(ichatmutablecomponent).c(s1);
    }

    @Override
    public void a(NBTTagByte nbttagbyte) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("b")).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttagbyte.k()))).addSibling(ichatmutablecomponent).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void a(NBTTagShort nbttagshort) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("s")).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttagshort.k()))).addSibling(ichatmutablecomponent).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void a(NBTTagInt nbttagint) {
        this.result = (new ChatComponentText(String.valueOf(nbttagint.k()))).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void a(NBTTagLong nbttaglong) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("L")).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttaglong.k()))).addSibling(ichatmutablecomponent).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void a(NBTTagFloat nbttagfloat) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("f")).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttagfloat.asFloat()))).addSibling(ichatmutablecomponent).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void a(NBTTagDouble nbttagdouble) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("d")).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);

        this.result = (new ChatComponentText(String.valueOf(nbttagdouble.asDouble()))).addSibling(ichatmutablecomponent).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void a(NBTTagByteArray nbttagbytearray) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("B")).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText("[")).addSibling(ichatmutablecomponent).c(";");
        byte[] abyte = nbttagbytearray.getBytes();

        for (int i = 0; i < abyte.length; ++i) {
            IChatMutableComponent ichatmutablecomponent2 = (new ChatComponentText(String.valueOf(abyte[i]))).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);

            ichatmutablecomponent1.c(" ").addSibling(ichatmutablecomponent2).addSibling(ichatmutablecomponent);
            if (i != abyte.length - 1) {
                ichatmutablecomponent1.c(TextComponentTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        ichatmutablecomponent1.c("]");
        this.result = ichatmutablecomponent1;
    }

    @Override
    public void a(NBTTagIntArray nbttagintarray) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("I")).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText("[")).addSibling(ichatmutablecomponent).c(";");
        int[] aint = nbttagintarray.getInts();

        for (int i = 0; i < aint.length; ++i) {
            ichatmutablecomponent1.c(" ").addSibling((new ChatComponentText(String.valueOf(aint[i]))).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER));
            if (i != aint.length - 1) {
                ichatmutablecomponent1.c(TextComponentTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        ichatmutablecomponent1.c("]");
        this.result = ichatmutablecomponent1;
    }

    @Override
    public void a(NBTTagLongArray nbttaglongarray) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("L")).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText("[")).addSibling(ichatmutablecomponent).c(";");
        long[] along = nbttaglongarray.getLongs();

        for (int i = 0; i < along.length; ++i) {
            IChatMutableComponent ichatmutablecomponent2 = (new ChatComponentText(String.valueOf(along[i]))).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_NUMBER);

            ichatmutablecomponent1.c(" ").addSibling(ichatmutablecomponent2).addSibling(ichatmutablecomponent);
            if (i != along.length - 1) {
                ichatmutablecomponent1.c(TextComponentTagVisitor.ELEMENT_SEPARATOR);
            }
        }

        ichatmutablecomponent1.c("]");
        this.result = ichatmutablecomponent1;
    }

    @Override
    public void a(NBTTagList nbttaglist) {
        if (nbttaglist.isEmpty()) {
            this.result = new ChatComponentText("[]");
        } else if (TextComponentTagVisitor.INLINE_ELEMENT_TYPES.contains(nbttaglist.e()) && nbttaglist.size() <= 8) {
            String s = TextComponentTagVisitor.ELEMENT_SEPARATOR + " ";
            ChatComponentText chatcomponenttext = new ChatComponentText("[");

            for (int i = 0; i < nbttaglist.size(); ++i) {
                if (i != 0) {
                    chatcomponenttext.c(s);
                }

                chatcomponenttext.addSibling((new TextComponentTagVisitor(this.indentation, this.depth)).a(nbttaglist.get(i)));
            }

            chatcomponenttext.c("]");
            this.result = chatcomponenttext;
        } else {
            ChatComponentText chatcomponenttext1 = new ChatComponentText("[");

            if (!this.indentation.isEmpty()) {
                chatcomponenttext1.c("\n");
            }

            for (int j = 0; j < nbttaglist.size(); ++j) {
                ChatComponentText chatcomponenttext2 = new ChatComponentText(Strings.repeat(this.indentation, this.depth + 1));

                chatcomponenttext2.addSibling((new TextComponentTagVisitor(this.indentation, this.depth + 1)).a(nbttaglist.get(j)));
                if (j != nbttaglist.size() - 1) {
                    chatcomponenttext2.c(TextComponentTagVisitor.ELEMENT_SEPARATOR).c(this.indentation.isEmpty() ? " " : "\n");
                }

                chatcomponenttext1.addSibling(chatcomponenttext2);
            }

            if (!this.indentation.isEmpty()) {
                chatcomponenttext1.c("\n").c(Strings.repeat(this.indentation, this.depth));
            }

            chatcomponenttext1.c("]");
            this.result = chatcomponenttext1;
        }
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.isEmpty()) {
            this.result = new ChatComponentText("{}");
        } else {
            ChatComponentText chatcomponenttext = new ChatComponentText("{");
            Collection<String> collection = nbttagcompound.getKeys();

            if (TextComponentTagVisitor.LOGGER.isDebugEnabled()) {
                List<String> list = Lists.newArrayList(nbttagcompound.getKeys());

                Collections.sort(list);
                collection = list;
            }

            if (!this.indentation.isEmpty()) {
                chatcomponenttext.c("\n");
            }

            IChatMutableComponent ichatmutablecomponent;

            for (Iterator iterator = ((Collection) collection).iterator(); iterator.hasNext(); chatcomponenttext.addSibling(ichatmutablecomponent)) {
                String s = (String) iterator.next();

                ichatmutablecomponent = (new ChatComponentText(Strings.repeat(this.indentation, this.depth + 1))).addSibling(a(s)).c(TextComponentTagVisitor.NAME_VALUE_SEPARATOR).c(" ").addSibling((new TextComponentTagVisitor(this.indentation, this.depth + 1)).a(nbttagcompound.get(s)));
                if (iterator.hasNext()) {
                    ichatmutablecomponent.c(TextComponentTagVisitor.ELEMENT_SEPARATOR).c(this.indentation.isEmpty() ? " " : "\n");
                }
            }

            if (!this.indentation.isEmpty()) {
                chatcomponenttext.c("\n").c(Strings.repeat(this.indentation, this.depth));
            }

            chatcomponenttext.c("}");
            this.result = chatcomponenttext;
        }
    }

    protected static IChatBaseComponent a(String s) {
        if (TextComponentTagVisitor.SIMPLE_VALUE.matcher(s).matches()) {
            return (new ChatComponentText(s)).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_KEY);
        } else {
            String s1 = NBTTagString.b(s);
            String s2 = s1.substring(0, 1);
            IChatMutableComponent ichatmutablecomponent = (new ChatComponentText(s1.substring(1, s1.length() - 1))).a(TextComponentTagVisitor.SYNTAX_HIGHLIGHTING_KEY);

            return (new ChatComponentText(s2)).addSibling(ichatmutablecomponent).c(s2);
        }
    }

    @Override
    public void a(NBTTagEnd nbttagend) {
        this.result = ChatComponentText.EMPTY;
    }
}
