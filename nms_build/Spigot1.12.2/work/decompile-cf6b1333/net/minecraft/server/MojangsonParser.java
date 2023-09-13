package net.minecraft.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MojangsonParser {

    private static final Pattern a = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern b = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern c = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern d = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern e = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern f = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern g = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final String h;
    private int i;

    public static NBTTagCompound parse(String s) throws MojangsonParseException {
        return (new MojangsonParser(s)).a();
    }

    @VisibleForTesting
    NBTTagCompound a() throws MojangsonParseException {
        NBTTagCompound nbttagcompound = this.f();

        this.l();
        if (this.g()) {
            ++this.i;
            throw this.b("Trailing data found");
        } else {
            return nbttagcompound;
        }
    }

    @VisibleForTesting
    MojangsonParser(String s) {
        this.h = s;
    }

    protected String b() throws MojangsonParseException {
        this.l();
        if (!this.g()) {
            throw this.b("Expected key");
        } else {
            return this.n() == 34 ? this.h() : this.i();
        }
    }

    private MojangsonParseException b(String s) {
        return new MojangsonParseException(s, this.h, this.i);
    }

    protected NBTBase c() throws MojangsonParseException {
        this.l();
        if (this.n() == 34) {
            return new NBTTagString(this.h());
        } else {
            String s = this.i();

            if (s.isEmpty()) {
                throw this.b("Expected value");
            } else {
                return this.c(s);
            }
        }
    }

    private NBTBase c(String s) {
        try {
            if (MojangsonParser.c.matcher(s).matches()) {
                return new NBTTagFloat(Float.parseFloat(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.d.matcher(s).matches()) {
                return new NBTTagByte(Byte.parseByte(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.e.matcher(s).matches()) {
                return new NBTTagLong(Long.parseLong(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.f.matcher(s).matches()) {
                return new NBTTagShort(Short.parseShort(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.g.matcher(s).matches()) {
                return new NBTTagInt(Integer.parseInt(s));
            }

            if (MojangsonParser.b.matcher(s).matches()) {
                return new NBTTagDouble(Double.parseDouble(s.substring(0, s.length() - 1)));
            }

            if (MojangsonParser.a.matcher(s).matches()) {
                return new NBTTagDouble(Double.parseDouble(s));
            }

            if ("true".equalsIgnoreCase(s)) {
                return new NBTTagByte((byte) 1);
            }

            if ("false".equalsIgnoreCase(s)) {
                return new NBTTagByte((byte) 0);
            }
        } catch (NumberFormatException numberformatexception) {
            ;
        }

        return new NBTTagString(s);
    }

    private String h() throws MojangsonParseException {
        int i = ++this.i;
        StringBuilder stringbuilder = null;
        boolean flag = false;

        while (this.g()) {
            char c0 = this.o();

            if (flag) {
                if (c0 != 92 && c0 != 34) {
                    throw this.b("Invalid escape of \'" + c0 + "\'");
                }

                flag = false;
            } else {
                if (c0 == 92) {
                    flag = true;
                    if (stringbuilder == null) {
                        stringbuilder = new StringBuilder(this.h.substring(i, this.i - 1));
                    }
                    continue;
                }

                if (c0 == 34) {
                    return stringbuilder == null ? this.h.substring(i, this.i - 1) : stringbuilder.toString();
                }
            }

            if (stringbuilder != null) {
                stringbuilder.append(c0);
            }
        }

        throw this.b("Missing termination quote");
    }

    private String i() {
        int i;

        for (i = this.i; this.g() && this.a(this.n()); ++this.i) {
            ;
        }

        return this.h.substring(i, this.i);
    }

    protected NBTBase d() throws MojangsonParseException {
        this.l();
        if (!this.g()) {
            throw this.b("Expected value");
        } else {
            char c0 = this.n();

            return (NBTBase) (c0 == 123 ? this.f() : (c0 == 91 ? this.e() : this.c()));
        }
    }

    protected NBTBase e() throws MojangsonParseException {
        return this.a((int) 2) && this.b((int) 1) != 34 && this.b((int) 2) == 59 ? this.k() : this.j();
    }

    protected NBTTagCompound f() throws MojangsonParseException {
        this.b('{');
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.l();

        while (this.g() && this.n() != 125) {
            String s = this.b();

            if (s.isEmpty()) {
                throw this.b("Expected non-empty key");
            }

            this.b(':');
            nbttagcompound.set(s, this.d());
            if (!this.m()) {
                break;
            }

            if (!this.g()) {
                throw this.b("Expected key");
            }
        }

        this.b('}');
        return nbttagcompound;
    }

    private NBTBase j() throws MojangsonParseException {
        this.b('[');
        this.l();
        if (!this.g()) {
            throw this.b("Expected value");
        } else {
            NBTTagList nbttaglist = new NBTTagList();
            byte b0 = -1;

            while (this.n() != 93) {
                NBTBase nbtbase = this.d();
                byte b1 = nbtbase.getTypeId();

                if (b0 < 0) {
                    b0 = b1;
                } else if (b1 != b0) {
                    throw this.b("Unable to insert " + NBTBase.j(b1) + " into ListTag of type " + NBTBase.j(b0));
                }

                nbttaglist.add(nbtbase);
                if (!this.m()) {
                    break;
                }

                if (!this.g()) {
                    throw this.b("Expected value");
                }
            }

            this.b(']');
            return nbttaglist;
        }
    }

    private NBTBase k() throws MojangsonParseException {
        this.b('[');
        char c0 = this.o();

        this.o();
        this.l();
        if (!this.g()) {
            throw this.b("Expected value");
        } else if (c0 == 66) {
            return new NBTTagByteArray(this.a((byte) 7, (byte) 1));
        } else if (c0 == 76) {
            return new NBTTagLongArray(this.a((byte) 12, (byte) 4));
        } else if (c0 == 73) {
            return new NBTTagIntArray(this.a((byte) 11, (byte) 3));
        } else {
            throw this.b("Invalid array type \'" + c0 + "\' found");
        }
    }

    private <T extends Number> List<T> a(byte b0, byte b1) throws MojangsonParseException {
        ArrayList arraylist = Lists.newArrayList();

        while (true) {
            if (this.n() != 93) {
                NBTBase nbtbase = this.d();
                byte b2 = nbtbase.getTypeId();

                if (b2 != b1) {
                    throw this.b("Unable to insert " + NBTBase.j(b2) + " into " + NBTBase.j(b0));
                }

                if (b1 == 1) {
                    arraylist.add(Byte.valueOf(((NBTNumber) nbtbase).g()));
                } else if (b1 == 4) {
                    arraylist.add(Long.valueOf(((NBTNumber) nbtbase).d()));
                } else {
                    arraylist.add(Integer.valueOf(((NBTNumber) nbtbase).e()));
                }

                if (this.m()) {
                    if (!this.g()) {
                        throw this.b("Expected value");
                    }
                    continue;
                }
            }

            this.b(']');
            return arraylist;
        }
    }

    private void l() {
        while (this.g() && Character.isWhitespace(this.n())) {
            ++this.i;
        }

    }

    private boolean m() {
        this.l();
        if (this.g() && this.n() == 44) {
            ++this.i;
            this.l();
            return true;
        } else {
            return false;
        }
    }

    private void b(char c0) throws MojangsonParseException {
        this.l();
        boolean flag = this.g();

        if (flag && this.n() == c0) {
            ++this.i;
        } else {
            throw new MojangsonParseException("Expected \'" + c0 + "\' but got \'" + (flag ? Character.valueOf(this.n()) : "<EOF>") + "\'", this.h, this.i + 1);
        }
    }

    protected boolean a(char c0) {
        return c0 >= 48 && c0 <= 57 || c0 >= 65 && c0 <= 90 || c0 >= 97 && c0 <= 122 || c0 == 95 || c0 == 45 || c0 == 46 || c0 == 43;
    }

    private boolean a(int i) {
        return this.i + i < this.h.length();
    }

    boolean g() {
        return this.a((int) 0);
    }

    private char b(int i) {
        return this.h.charAt(this.i + i);
    }

    private char n() {
        return this.b((int) 0);
    }

    private char o() {
        return this.h.charAt(this.i++);
    }
}
