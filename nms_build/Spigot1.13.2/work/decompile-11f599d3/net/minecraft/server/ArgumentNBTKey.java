package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ArgumentNBTKey implements ArgumentType<ArgumentNBTKey.c> {

    private static final Collection<String> a = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", ".");
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.nbtpath.child.invalid", new Object[] { object});
    });
    private static final DynamicCommandExceptionType c = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.nbtpath.element.invalid", new Object[] { object});
    });
    private static final SimpleCommandExceptionType d = new SimpleCommandExceptionType(new ChatMessage("arguments.nbtpath.node.invalid", new Object[0]));

    public ArgumentNBTKey() {}

    public static ArgumentNBTKey a() {
        return new ArgumentNBTKey();
    }

    public static ArgumentNBTKey.c a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ArgumentNBTKey.c) commandcontext.getArgument(s, ArgumentNBTKey.c.class);
    }

    public ArgumentNBTKey.c parse(StringReader stringreader) throws CommandSyntaxException {
        List<ArgumentNBTKey.d> list = Lists.newArrayList();
        int i = stringreader.getCursor();

        while (stringreader.canRead() && stringreader.peek() != ' ') {
            switch (stringreader.peek()) {
            case '"':
                list.add(new ArgumentNBTKey.a(stringreader.readString()));
                break;
            case '[':
                stringreader.skip();
                list.add(new ArgumentNBTKey.b(stringreader.readInt()));
                stringreader.expect(']');
                break;
            default:
                list.add(new ArgumentNBTKey.a(this.b(stringreader)));
            }

            if (stringreader.canRead()) {
                char c0 = stringreader.peek();

                if (c0 != ' ' && c0 != '[') {
                    stringreader.expect('.');
                }
            }
        }

        return new ArgumentNBTKey.c(stringreader.getString().substring(i, stringreader.getCursor()), (ArgumentNBTKey.d[]) list.toArray(new ArgumentNBTKey.d[0]));
    }

    private String b(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        while (stringreader.canRead() && a(stringreader.peek())) {
            stringreader.skip();
        }

        if (stringreader.getCursor() == i) {
            throw ArgumentNBTKey.d.createWithContext(stringreader);
        } else {
            return stringreader.getString().substring(i, stringreader.getCursor());
        }
    }

    public Collection<String> getExamples() {
        return ArgumentNBTKey.a;
    }

    private static boolean a(char c0) {
        return c0 != ' ' && c0 != '"' && c0 != '[' && c0 != ']' && c0 != '.';
    }

    static class b implements ArgumentNBTKey.d {

        private final int a;

        public b(int i) {
            this.a = i;
        }

        public NBTBase a(NBTBase nbtbase) throws CommandSyntaxException {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;

                if (nbtlist.size() > this.a) {
                    return nbtlist.c(this.a);
                }
            }

            throw ArgumentNBTKey.c.create(this.a);
        }

        public NBTBase a(NBTBase nbtbase, Supplier<NBTBase> supplier) throws CommandSyntaxException {
            return this.a(nbtbase);
        }

        public NBTBase a() {
            return new NBTTagList();
        }

        public void a(NBTBase nbtbase, NBTBase nbtbase1) throws CommandSyntaxException {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;

                if (nbtlist.size() > this.a) {
                    nbtlist.a(this.a, nbtbase1);
                    return;
                }
            }

            throw ArgumentNBTKey.c.create(this.a);
        }

        public void b(NBTBase nbtbase) throws CommandSyntaxException {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;

                if (nbtlist.size() > this.a) {
                    nbtlist.b(this.a);
                    return;
                }
            }

            throw ArgumentNBTKey.c.create(this.a);
        }
    }

    static class a implements ArgumentNBTKey.d {

        private final String a;

        public a(String s) {
            this.a = s;
        }

        public NBTBase a(NBTBase nbtbase) throws CommandSyntaxException {
            if (nbtbase instanceof NBTTagCompound) {
                return ((NBTTagCompound) nbtbase).get(this.a);
            } else {
                throw ArgumentNBTKey.b.create(this.a);
            }
        }

        public NBTBase a(NBTBase nbtbase, Supplier<NBTBase> supplier) throws CommandSyntaxException {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

                if (nbttagcompound.hasKey(this.a)) {
                    return nbttagcompound.get(this.a);
                } else {
                    NBTBase nbtbase1 = (NBTBase) supplier.get();

                    nbttagcompound.set(this.a, nbtbase1);
                    return nbtbase1;
                }
            } else {
                throw ArgumentNBTKey.b.create(this.a);
            }
        }

        public NBTBase a() {
            return new NBTTagCompound();
        }

        public void a(NBTBase nbtbase, NBTBase nbtbase1) throws CommandSyntaxException {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

                nbttagcompound.set(this.a, nbtbase1);
            } else {
                throw ArgumentNBTKey.b.create(this.a);
            }
        }

        public void b(NBTBase nbtbase) throws CommandSyntaxException {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

                if (nbttagcompound.hasKey(this.a)) {
                    nbttagcompound.remove(this.a);
                    return;
                }
            }

            throw ArgumentNBTKey.b.create(this.a);
        }
    }

    interface d {

        NBTBase a(NBTBase nbtbase) throws CommandSyntaxException;

        NBTBase a(NBTBase nbtbase, Supplier<NBTBase> supplier) throws CommandSyntaxException;

        NBTBase a();

        void a(NBTBase nbtbase, NBTBase nbtbase1) throws CommandSyntaxException;

        void b(NBTBase nbtbase) throws CommandSyntaxException;
    }

    public static class c {

        private final String a;
        private final ArgumentNBTKey.d[] b;

        public c(String s, ArgumentNBTKey.d[] aargumentnbtkey_d) {
            this.a = s;
            this.b = aargumentnbtkey_d;
        }

        public NBTBase a(NBTBase nbtbase) throws CommandSyntaxException {
            ArgumentNBTKey.d[] aargumentnbtkey_d = this.b;
            int i = aargumentnbtkey_d.length;

            for (int j = 0; j < i; ++j) {
                ArgumentNBTKey.d argumentnbtkey_d = aargumentnbtkey_d[j];

                nbtbase = argumentnbtkey_d.a(nbtbase);
            }

            return nbtbase;
        }

        public NBTBase a(NBTBase nbtbase, NBTBase nbtbase1) throws CommandSyntaxException {
            for (int i = 0; i < this.b.length; ++i) {
                ArgumentNBTKey.d argumentnbtkey_d = this.b[i];

                if (i < this.b.length - 1) {
                    int j = i + 1;

                    nbtbase = argumentnbtkey_d.a(nbtbase, () -> {
                        return this.b[j].a();
                    });
                } else {
                    argumentnbtkey_d.a(nbtbase, nbtbase1);
                }
            }

            return nbtbase;
        }

        public String toString() {
            return this.a;
        }

        public void b(NBTBase nbtbase) throws CommandSyntaxException {
            for (int i = 0; i < this.b.length; ++i) {
                ArgumentNBTKey.d argumentnbtkey_d = this.b[i];

                if (i < this.b.length - 1) {
                    nbtbase = argumentnbtkey_d.a(nbtbase);
                } else {
                    argumentnbtkey_d.b(nbtbase);
                }
            }

        }
    }
}
