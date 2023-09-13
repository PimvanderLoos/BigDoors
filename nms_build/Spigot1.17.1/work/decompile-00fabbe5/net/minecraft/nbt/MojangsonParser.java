package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.network.chat.ChatMessage;

public class MojangsonParser {

    public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(new ChatMessage("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(new ChatMessage("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(new ChatMessage("argument.nbt.expected.value"));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.nbt.list.mixed", new Object[]{object, object1});
    });
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.nbt.array.mixed", new Object[]{object, object1});
    });
    public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.nbt.array.invalid", new Object[]{object});
    });
    public static final char ELEMENT_SEPARATOR = ',';
    public static final char NAME_VALUE_SEPARATOR = ':';
    private static final char LIST_OPEN = '[';
    private static final char LIST_CLOSE = ']';
    private static final char STRUCT_CLOSE = '}';
    private static final char STRUCT_OPEN = '{';
    private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    public static NBTTagCompound parse(String s) throws CommandSyntaxException {
        return (new MojangsonParser(new StringReader(s))).a();
    }

    @VisibleForTesting
    NBTTagCompound a() throws CommandSyntaxException {
        NBTTagCompound nbttagcompound = this.f();

        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw MojangsonParser.ERROR_TRAILING_DATA.createWithContext(this.reader);
        } else {
            return nbttagcompound;
        }
    }

    public MojangsonParser(StringReader stringreader) {
        this.reader = stringreader;
    }

    protected String b() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw MojangsonParser.ERROR_EXPECTED_KEY.createWithContext(this.reader);
        } else {
            return this.reader.readString();
        }
    }

    protected NBTBase c() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int i = this.reader.getCursor();

        if (StringReader.isQuotedStringStart(this.reader.peek())) {
            return NBTTagString.a(this.reader.readQuotedString());
        } else {
            String s = this.reader.readUnquotedString();

            if (s.isEmpty()) {
                this.reader.setCursor(i);
                throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            } else {
                return this.parseLiteral(s);
            }
        }
    }

    public NBTBase parseLiteral(String s) {
        try {
            if (MojangsonParser.FLOAT_PATTERN.matcher(s).matches()) {
                return NBTTagFloat.a(Float.parseFloat(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.BYTE_PATTERN.matcher(s).matches()) {
                return NBTTagByte.a(Byte.parseByte(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.LONG_PATTERN.matcher(s).matches()) {
                return NBTTagLong.a(Long.parseLong(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.SHORT_PATTERN.matcher(s).matches()) {
                return NBTTagShort.a(Short.parseShort(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.INT_PATTERN.matcher(s).matches()) {
                return NBTTagInt.a(Integer.parseInt(s));
            }

            if (MojangsonParser.DOUBLE_PATTERN.matcher(s).matches()) {
                return NBTTagDouble.a(Double.parseDouble(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.DOUBLE_PATTERN_NOSUFFIX.matcher(s).matches()) {
                return NBTTagDouble.a(Double.parseDouble(s));
            }

            if ("true".equalsIgnoreCase(s)) {
                return NBTTagByte.ONE;
            }

            if ("false".equalsIgnoreCase(s)) {
                return NBTTagByte.ZERO;
            }
        } catch (NumberFormatException numberformatexception) {
            ;
        }

        return NBTTagString.a(s);
    }

    public NBTBase d() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else {
            char c0 = this.reader.peek();

            return (NBTBase) (c0 == '{' ? this.f() : (c0 == '[' ? this.e() : this.c()));
        }
    }

    protected NBTBase e() throws CommandSyntaxException {
        return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.parseArray() : this.g();
    }

    public NBTTagCompound f() throws CommandSyntaxException {
        this.a('{');
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.reader.skipWhitespace();

        while (this.reader.canRead() && this.reader.peek() != '}') {
            int i = this.reader.getCursor();
            String s = this.b();

            if (s.isEmpty()) {
                this.reader.setCursor(i);
                throw MojangsonParser.ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }

            this.a(':');
            nbttagcompound.set(s, this.d());
            if (!this.i()) {
                break;
            }

            if (!this.reader.canRead()) {
                throw MojangsonParser.ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }
        }

        this.a('}');
        return nbttagcompound;
    }

    private NBTBase g() throws CommandSyntaxException {
        this.a('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else {
            NBTTagList nbttaglist = new NBTTagList();
            NBTTagType nbttagtype = null;

            while (this.reader.peek() != ']') {
                int i = this.reader.getCursor();
                NBTBase nbtbase = this.d();
                NBTTagType<?> nbttagtype1 = nbtbase.b();

                if (nbttagtype == null) {
                    nbttagtype = nbttagtype1;
                } else if (nbttagtype1 != nbttagtype) {
                    this.reader.setCursor(i);
                    throw MojangsonParser.ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, nbttagtype1.b(), nbttagtype.b());
                }

                nbttaglist.add(nbtbase);
                if (!this.i()) {
                    break;
                }

                if (!this.reader.canRead()) {
                    throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                }
            }

            this.a(']');
            return nbttaglist;
        }
    }

    public NBTBase parseArray() throws CommandSyntaxException {
        this.a('[');
        int i = this.reader.getCursor();
        char c0 = this.reader.read();

        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else if (c0 == 'B') {
            return new NBTTagByteArray(this.a(NBTTagByteArray.TYPE, NBTTagByte.TYPE));
        } else if (c0 == 'L') {
            return new NBTTagLongArray(this.a(NBTTagLongArray.TYPE, NBTTagLong.TYPE));
        } else if (c0 == 'I') {
            return new NBTTagIntArray(this.a(NBTTagIntArray.TYPE, NBTTagInt.TYPE));
        } else {
            this.reader.setCursor(i);
            throw MojangsonParser.ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(c0));
        }
    }

    private <T extends Number> List<T> a(NBTTagType<?> nbttagtype, NBTTagType<?> nbttagtype1) throws CommandSyntaxException {
        ArrayList arraylist = Lists.newArrayList();

        while (true) {
            if (this.reader.peek() != ']') {
                int i = this.reader.getCursor();
                NBTBase nbtbase = this.d();
                NBTTagType<?> nbttagtype2 = nbtbase.b();

                if (nbttagtype2 != nbttagtype1) {
                    this.reader.setCursor(i);
                    throw MojangsonParser.ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, nbttagtype2.b(), nbttagtype.b());
                }

                if (nbttagtype1 == NBTTagByte.TYPE) {
                    arraylist.add(((NBTNumber) nbtbase).asByte());
                } else if (nbttagtype1 == NBTTagLong.TYPE) {
                    arraylist.add(((NBTNumber) nbtbase).asLong());
                } else {
                    arraylist.add(((NBTNumber) nbtbase).asInt());
                }

                if (this.i()) {
                    if (!this.reader.canRead()) {
                        throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                    }
                    continue;
                }
            }

            this.a(']');
            return arraylist;
        }
    }

    private boolean i() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    private void a(char c0) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(c0);
    }
}
