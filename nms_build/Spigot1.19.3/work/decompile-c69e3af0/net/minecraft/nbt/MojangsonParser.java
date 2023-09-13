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
import net.minecraft.network.chat.IChatBaseComponent;

public class MojangsonParser {

    public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.nbt.expected.value"));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.nbt.list.mixed", object, object1);
    });
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.nbt.array.mixed", object, object1);
    });
    public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.nbt.array.invalid", object);
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

    public static NBTTagCompound parseTag(String s) throws CommandSyntaxException {
        return (new MojangsonParser(new StringReader(s))).readSingleStruct();
    }

    @VisibleForTesting
    NBTTagCompound readSingleStruct() throws CommandSyntaxException {
        NBTTagCompound nbttagcompound = this.readStruct();

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

    protected String readKey() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw MojangsonParser.ERROR_EXPECTED_KEY.createWithContext(this.reader);
        } else {
            return this.reader.readString();
        }
    }

    protected NBTBase readTypedValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int i = this.reader.getCursor();

        if (StringReader.isQuotedStringStart(this.reader.peek())) {
            return NBTTagString.valueOf(this.reader.readQuotedString());
        } else {
            String s = this.reader.readUnquotedString();

            if (s.isEmpty()) {
                this.reader.setCursor(i);
                throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            } else {
                return this.type(s);
            }
        }
    }

    public NBTBase type(String s) {
        try {
            if (MojangsonParser.FLOAT_PATTERN.matcher(s).matches()) {
                return NBTTagFloat.valueOf(Float.parseFloat(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.BYTE_PATTERN.matcher(s).matches()) {
                return NBTTagByte.valueOf(Byte.parseByte(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.LONG_PATTERN.matcher(s).matches()) {
                return NBTTagLong.valueOf(Long.parseLong(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.SHORT_PATTERN.matcher(s).matches()) {
                return NBTTagShort.valueOf(Short.parseShort(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.INT_PATTERN.matcher(s).matches()) {
                return NBTTagInt.valueOf(Integer.parseInt(s));
            }

            if (MojangsonParser.DOUBLE_PATTERN.matcher(s).matches()) {
                return NBTTagDouble.valueOf(Double.parseDouble(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.DOUBLE_PATTERN_NOSUFFIX.matcher(s).matches()) {
                return NBTTagDouble.valueOf(Double.parseDouble(s));
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

        return NBTTagString.valueOf(s);
    }

    public NBTBase readValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else {
            char c0 = this.reader.peek();

            return (NBTBase) (c0 == '{' ? this.readStruct() : (c0 == '[' ? this.readList() : this.readTypedValue()));
        }
    }

    protected NBTBase readList() throws CommandSyntaxException {
        return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
    }

    public NBTTagCompound readStruct() throws CommandSyntaxException {
        this.expect('{');
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.reader.skipWhitespace();

        while (this.reader.canRead() && this.reader.peek() != '}') {
            int i = this.reader.getCursor();
            String s = this.readKey();

            if (s.isEmpty()) {
                this.reader.setCursor(i);
                throw MojangsonParser.ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }

            this.expect(':');
            nbttagcompound.put(s, this.readValue());
            if (!this.hasElementSeparator()) {
                break;
            }

            if (!this.reader.canRead()) {
                throw MojangsonParser.ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }
        }

        this.expect('}');
        return nbttagcompound;
    }

    private NBTBase readListTag() throws CommandSyntaxException {
        this.expect('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else {
            NBTTagList nbttaglist = new NBTTagList();
            NBTTagType nbttagtype = null;

            while (this.reader.peek() != ']') {
                int i = this.reader.getCursor();
                NBTBase nbtbase = this.readValue();
                NBTTagType<?> nbttagtype1 = nbtbase.getType();

                if (nbttagtype == null) {
                    nbttagtype = nbttagtype1;
                } else if (nbttagtype1 != nbttagtype) {
                    this.reader.setCursor(i);
                    throw MojangsonParser.ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, nbttagtype1.getPrettyName(), nbttagtype.getPrettyName());
                }

                nbttaglist.add(nbtbase);
                if (!this.hasElementSeparator()) {
                    break;
                }

                if (!this.reader.canRead()) {
                    throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                }
            }

            this.expect(']');
            return nbttaglist;
        }
    }

    public NBTBase readArrayTag() throws CommandSyntaxException {
        this.expect('[');
        int i = this.reader.getCursor();
        char c0 = this.reader.read();

        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else if (c0 == 'B') {
            return new NBTTagByteArray(this.readArray(NBTTagByteArray.TYPE, NBTTagByte.TYPE));
        } else if (c0 == 'L') {
            return new NBTTagLongArray(this.readArray(NBTTagLongArray.TYPE, NBTTagLong.TYPE));
        } else if (c0 == 'I') {
            return new NBTTagIntArray(this.readArray(NBTTagIntArray.TYPE, NBTTagInt.TYPE));
        } else {
            this.reader.setCursor(i);
            throw MojangsonParser.ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(c0));
        }
    }

    private <T extends Number> List<T> readArray(NBTTagType<?> nbttagtype, NBTTagType<?> nbttagtype1) throws CommandSyntaxException {
        ArrayList arraylist = Lists.newArrayList();

        while (true) {
            if (this.reader.peek() != ']') {
                int i = this.reader.getCursor();
                NBTBase nbtbase = this.readValue();
                NBTTagType<?> nbttagtype2 = nbtbase.getType();

                if (nbttagtype2 != nbttagtype1) {
                    this.reader.setCursor(i);
                    throw MojangsonParser.ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, nbttagtype2.getPrettyName(), nbttagtype.getPrettyName());
                }

                if (nbttagtype1 == NBTTagByte.TYPE) {
                    arraylist.add(((NBTNumber) nbtbase).getAsByte());
                } else if (nbttagtype1 == NBTTagLong.TYPE) {
                    arraylist.add(((NBTNumber) nbtbase).getAsLong());
                } else {
                    arraylist.add(((NBTNumber) nbtbase).getAsInt());
                }

                if (this.hasElementSeparator()) {
                    if (!this.reader.canRead()) {
                        throw MojangsonParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                    }
                    continue;
                }
            }

            this.expect(']');
            return arraylist;
        }
    }

    private boolean hasElementSeparator() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    private void expect(char c0) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(c0);
    }
}
