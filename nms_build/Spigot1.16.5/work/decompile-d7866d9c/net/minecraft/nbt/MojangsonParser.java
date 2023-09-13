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

    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("argument.nbt.expected.value"));
    public static final Dynamic2CommandExceptionType d = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.nbt.list.mixed", new Object[]{object, object1});
    });
    public static final Dynamic2CommandExceptionType e = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.nbt.array.mixed", new Object[]{object, object1});
    });
    public static final DynamicCommandExceptionType f = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.nbt.array.invalid", new Object[]{object});
    });
    private static final Pattern g = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern h = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern i = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern j = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern k = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern l = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern m = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader n;

    public static NBTTagCompound parse(String s) throws CommandSyntaxException {
        return (new MojangsonParser(new StringReader(s))).a();
    }

    @VisibleForTesting
    NBTTagCompound a() throws CommandSyntaxException {
        NBTTagCompound nbttagcompound = this.f();

        this.n.skipWhitespace();
        if (this.n.canRead()) {
            throw MojangsonParser.a.createWithContext(this.n);
        } else {
            return nbttagcompound;
        }
    }

    public MojangsonParser(StringReader stringreader) {
        this.n = stringreader;
    }

    protected String b() throws CommandSyntaxException {
        this.n.skipWhitespace();
        if (!this.n.canRead()) {
            throw MojangsonParser.b.createWithContext(this.n);
        } else {
            return this.n.readString();
        }
    }

    protected NBTBase c() throws CommandSyntaxException {
        this.n.skipWhitespace();
        int i = this.n.getCursor();

        if (StringReader.isQuotedStringStart(this.n.peek())) {
            return NBTTagString.a(this.n.readQuotedString());
        } else {
            String s = this.n.readUnquotedString();

            if (s.isEmpty()) {
                this.n.setCursor(i);
                throw MojangsonParser.c.createWithContext(this.n);
            } else {
                return this.parseLiteral(s);
            }
        }
    }

    public NBTBase parseLiteral(String s) {
        try {
            if (MojangsonParser.i.matcher(s).matches()) {
                return NBTTagFloat.a(Float.parseFloat(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.j.matcher(s).matches()) {
                return NBTTagByte.a(Byte.parseByte(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.k.matcher(s).matches()) {
                return NBTTagLong.a(Long.parseLong(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.l.matcher(s).matches()) {
                return NBTTagShort.a(Short.parseShort(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.m.matcher(s).matches()) {
                return NBTTagInt.a(Integer.parseInt(s));
            }

            if (MojangsonParser.h.matcher(s).matches()) {
                return NBTTagDouble.a(Double.parseDouble(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.g.matcher(s).matches()) {
                return NBTTagDouble.a(Double.parseDouble(s));
            }

            if ("true".equalsIgnoreCase(s)) {
                return NBTTagByte.c;
            }

            if ("false".equalsIgnoreCase(s)) {
                return NBTTagByte.b;
            }
        } catch (NumberFormatException numberformatexception) {
            ;
        }

        return NBTTagString.a(s);
    }

    public NBTBase d() throws CommandSyntaxException {
        this.n.skipWhitespace();
        if (!this.n.canRead()) {
            throw MojangsonParser.c.createWithContext(this.n);
        } else {
            char c0 = this.n.peek();

            return (NBTBase) (c0 == '{' ? this.f() : (c0 == '[' ? this.e() : this.c()));
        }
    }

    protected NBTBase e() throws CommandSyntaxException {
        return this.n.canRead(3) && !StringReader.isQuotedStringStart(this.n.peek(1)) && this.n.peek(2) == ';' ? this.parseArray() : this.g();
    }

    public NBTTagCompound f() throws CommandSyntaxException {
        this.a('{');
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.n.skipWhitespace();

        while (this.n.canRead() && this.n.peek() != '}') {
            int i = this.n.getCursor();
            String s = this.b();

            if (s.isEmpty()) {
                this.n.setCursor(i);
                throw MojangsonParser.b.createWithContext(this.n);
            }

            this.a(':');
            nbttagcompound.set(s, this.d());
            if (!this.i()) {
                break;
            }

            if (!this.n.canRead()) {
                throw MojangsonParser.b.createWithContext(this.n);
            }
        }

        this.a('}');
        return nbttagcompound;
    }

    private NBTBase g() throws CommandSyntaxException {
        this.a('[');
        this.n.skipWhitespace();
        if (!this.n.canRead()) {
            throw MojangsonParser.c.createWithContext(this.n);
        } else {
            NBTTagList nbttaglist = new NBTTagList();
            NBTTagType nbttagtype = null;

            while (this.n.peek() != ']') {
                int i = this.n.getCursor();
                NBTBase nbtbase = this.d();
                NBTTagType<?> nbttagtype1 = nbtbase.b();

                if (nbttagtype == null) {
                    nbttagtype = nbttagtype1;
                } else if (nbttagtype1 != nbttagtype) {
                    this.n.setCursor(i);
                    throw MojangsonParser.d.createWithContext(this.n, nbttagtype1.b(), nbttagtype.b());
                }

                nbttaglist.add(nbtbase);
                if (!this.i()) {
                    break;
                }

                if (!this.n.canRead()) {
                    throw MojangsonParser.c.createWithContext(this.n);
                }
            }

            this.a(']');
            return nbttaglist;
        }
    }

    public NBTBase parseArray() throws CommandSyntaxException {
        this.a('[');
        int i = this.n.getCursor();
        char c0 = this.n.read();

        this.n.read();
        this.n.skipWhitespace();
        if (!this.n.canRead()) {
            throw MojangsonParser.c.createWithContext(this.n);
        } else if (c0 == 'B') {
            return new NBTTagByteArray(this.a(NBTTagByteArray.a, NBTTagByte.a));
        } else if (c0 == 'L') {
            return new NBTTagLongArray(this.a(NBTTagLongArray.a, NBTTagLong.a));
        } else if (c0 == 'I') {
            return new NBTTagIntArray(this.a(NBTTagIntArray.a, NBTTagInt.a));
        } else {
            this.n.setCursor(i);
            throw MojangsonParser.f.createWithContext(this.n, String.valueOf(c0));
        }
    }

    private <T extends Number> List<T> a(NBTTagType<?> nbttagtype, NBTTagType<?> nbttagtype1) throws CommandSyntaxException {
        ArrayList arraylist = Lists.newArrayList();

        while (true) {
            if (this.n.peek() != ']') {
                int i = this.n.getCursor();
                NBTBase nbtbase = this.d();
                NBTTagType<?> nbttagtype2 = nbtbase.b();

                if (nbttagtype2 != nbttagtype1) {
                    this.n.setCursor(i);
                    throw MojangsonParser.e.createWithContext(this.n, nbttagtype2.b(), nbttagtype.b());
                }

                if (nbttagtype1 == NBTTagByte.a) {
                    arraylist.add(((NBTNumber) nbtbase).asByte());
                } else if (nbttagtype1 == NBTTagLong.a) {
                    arraylist.add(((NBTNumber) nbtbase).asLong());
                } else {
                    arraylist.add(((NBTNumber) nbtbase).asInt());
                }

                if (this.i()) {
                    if (!this.n.canRead()) {
                        throw MojangsonParser.c.createWithContext(this.n);
                    }
                    continue;
                }
            }

            this.a(']');
            return arraylist;
        }
    }

    private boolean i() {
        this.n.skipWhitespace();
        if (this.n.canRead() && this.n.peek() == ',') {
            this.n.skip();
            this.n.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    private void a(char c0) throws CommandSyntaxException {
        this.n.skipWhitespace();
        this.n.expect(c0);
    }
}
