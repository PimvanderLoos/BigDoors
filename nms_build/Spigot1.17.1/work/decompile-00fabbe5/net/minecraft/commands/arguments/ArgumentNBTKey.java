package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ArgumentNBTKey implements ArgumentType<ArgumentNBTKey.g> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
    public static final SimpleCommandExceptionType ERROR_INVALID_NODE = new SimpleCommandExceptionType(new ChatMessage("arguments.nbtpath.node.invalid"));
    public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.nbtpath.nothing_found", new Object[]{object});
    });
    private static final char INDEX_MATCH_START = '[';
    private static final char INDEX_MATCH_END = ']';
    private static final char KEY_MATCH_START = '{';
    private static final char KEY_MATCH_END = '}';
    private static final char QUOTED_KEY_START = '"';

    public ArgumentNBTKey() {}

    public static ArgumentNBTKey a() {
        return new ArgumentNBTKey();
    }

    public static ArgumentNBTKey.g a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ArgumentNBTKey.g) commandcontext.getArgument(s, ArgumentNBTKey.g.class);
    }

    public ArgumentNBTKey.g parse(StringReader stringreader) throws CommandSyntaxException {
        List<ArgumentNBTKey.h> list = Lists.newArrayList();
        int i = stringreader.getCursor();
        Object2IntMap<ArgumentNBTKey.h> object2intmap = new Object2IntOpenHashMap();
        boolean flag = true;

        while (stringreader.canRead() && stringreader.peek() != ' ') {
            ArgumentNBTKey.h argumentnbtkey_h = a(stringreader, flag);

            list.add(argumentnbtkey_h);
            object2intmap.put(argumentnbtkey_h, stringreader.getCursor() - i);
            flag = false;
            if (stringreader.canRead()) {
                char c0 = stringreader.peek();

                if (c0 != ' ' && c0 != '[' && c0 != '{') {
                    stringreader.expect('.');
                }
            }
        }

        return new ArgumentNBTKey.g(stringreader.getString().substring(i, stringreader.getCursor()), (ArgumentNBTKey.h[]) list.toArray(new ArgumentNBTKey.h[0]), object2intmap);
    }

    private static ArgumentNBTKey.h a(StringReader stringreader, boolean flag) throws CommandSyntaxException {
        String s;

        switch (stringreader.peek()) {
            case '"':
                s = stringreader.readString();
                return a(stringreader, s);
            case '[':
                stringreader.skip();
                char c0 = stringreader.peek();

                if (c0 == '{') {
                    NBTTagCompound nbttagcompound = (new MojangsonParser(stringreader)).f();

                    stringreader.expect(']');
                    return new ArgumentNBTKey.d(nbttagcompound);
                } else {
                    if (c0 == ']') {
                        stringreader.skip();
                        return ArgumentNBTKey.a.INSTANCE;
                    }

                    int i = stringreader.readInt();

                    stringreader.expect(']');
                    return new ArgumentNBTKey.c(i);
                }
            case '{':
                if (!flag) {
                    throw ArgumentNBTKey.ERROR_INVALID_NODE.createWithContext(stringreader);
                }

                NBTTagCompound nbttagcompound1 = (new MojangsonParser(stringreader)).f();

                return new ArgumentNBTKey.f(nbttagcompound1);
            default:
                s = b(stringreader);
                return a(stringreader, s);
        }
    }

    private static ArgumentNBTKey.h a(StringReader stringreader, String s) throws CommandSyntaxException {
        if (stringreader.canRead() && stringreader.peek() == '{') {
            NBTTagCompound nbttagcompound = (new MojangsonParser(stringreader)).f();

            return new ArgumentNBTKey.e(s, nbttagcompound);
        } else {
            return new ArgumentNBTKey.b(s);
        }
    }

    private static String b(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        while (stringreader.canRead() && a(stringreader.peek())) {
            stringreader.skip();
        }

        if (stringreader.getCursor() == i) {
            throw ArgumentNBTKey.ERROR_INVALID_NODE.createWithContext(stringreader);
        } else {
            return stringreader.getString().substring(i, stringreader.getCursor());
        }
    }

    public Collection<String> getExamples() {
        return ArgumentNBTKey.EXAMPLES;
    }

    private static boolean a(char c0) {
        return c0 != ' ' && c0 != '"' && c0 != '[' && c0 != ']' && c0 != '.' && c0 != '{' && c0 != '}';
    }

    static Predicate<NBTBase> a(NBTTagCompound nbttagcompound) {
        return (nbtbase) -> {
            return GameProfileSerializer.a(nbttagcompound, nbtbase, true);
        };
    }

    public static class g {

        private final String original;
        private final Object2IntMap<ArgumentNBTKey.h> nodeToOriginalPosition;
        private final ArgumentNBTKey.h[] nodes;

        public g(String s, ArgumentNBTKey.h[] aargumentnbtkey_h, Object2IntMap<ArgumentNBTKey.h> object2intmap) {
            this.original = s;
            this.nodes = aargumentnbtkey_h;
            this.nodeToOriginalPosition = object2intmap;
        }

        public List<NBTBase> a(NBTBase nbtbase) throws CommandSyntaxException {
            List<NBTBase> list = Collections.singletonList(nbtbase);
            ArgumentNBTKey.h[] aargumentnbtkey_h = this.nodes;
            int i = aargumentnbtkey_h.length;

            for (int j = 0; j < i; ++j) {
                ArgumentNBTKey.h argumentnbtkey_h = aargumentnbtkey_h[j];

                list = argumentnbtkey_h.a(list);
                if (list.isEmpty()) {
                    throw this.a(argumentnbtkey_h);
                }
            }

            return list;
        }

        public int b(NBTBase nbtbase) {
            List<NBTBase> list = Collections.singletonList(nbtbase);
            ArgumentNBTKey.h[] aargumentnbtkey_h = this.nodes;
            int i = aargumentnbtkey_h.length;

            for (int j = 0; j < i; ++j) {
                ArgumentNBTKey.h argumentnbtkey_h = aargumentnbtkey_h[j];

                list = argumentnbtkey_h.a(list);
                if (list.isEmpty()) {
                    return 0;
                }
            }

            return list.size();
        }

        private List<NBTBase> d(NBTBase nbtbase) throws CommandSyntaxException {
            List<NBTBase> list = Collections.singletonList(nbtbase);

            for (int i = 0; i < this.nodes.length - 1; ++i) {
                ArgumentNBTKey.h argumentnbtkey_h = this.nodes[i];
                int j = i + 1;
                ArgumentNBTKey.h argumentnbtkey_h1 = this.nodes[j];

                Objects.requireNonNull(this.nodes[j]);
                list = argumentnbtkey_h.a(list, argumentnbtkey_h1::a);
                if (list.isEmpty()) {
                    throw this.a(argumentnbtkey_h);
                }
            }

            return list;
        }

        public List<NBTBase> a(NBTBase nbtbase, Supplier<NBTBase> supplier) throws CommandSyntaxException {
            List<NBTBase> list = this.d(nbtbase);
            ArgumentNBTKey.h argumentnbtkey_h = this.nodes[this.nodes.length - 1];

            return argumentnbtkey_h.a(list, supplier);
        }

        private static int a(List<NBTBase> list, Function<NBTBase, Integer> function) {
            return (Integer) list.stream().map(function).reduce(0, (integer, integer1) -> {
                return integer + integer1;
            });
        }

        public int a(NBTBase nbtbase, NBTBase nbtbase1) throws CommandSyntaxException {
            Objects.requireNonNull(nbtbase1);
            return this.b(nbtbase, nbtbase1::clone);
        }

        public int b(NBTBase nbtbase, Supplier<NBTBase> supplier) throws CommandSyntaxException {
            List<NBTBase> list = this.d(nbtbase);
            ArgumentNBTKey.h argumentnbtkey_h = this.nodes[this.nodes.length - 1];

            return a(list, (nbtbase1) -> {
                return argumentnbtkey_h.a(nbtbase1, supplier);
            });
        }

        public int c(NBTBase nbtbase) {
            List<NBTBase> list = Collections.singletonList(nbtbase);

            for (int i = 0; i < this.nodes.length - 1; ++i) {
                list = this.nodes[i].a(list);
            }

            ArgumentNBTKey.h argumentnbtkey_h = this.nodes[this.nodes.length - 1];

            Objects.requireNonNull(argumentnbtkey_h);
            return a(list, argumentnbtkey_h::a);
        }

        private CommandSyntaxException a(ArgumentNBTKey.h argumentnbtkey_h) {
            int i = this.nodeToOriginalPosition.getInt(argumentnbtkey_h);

            return ArgumentNBTKey.ERROR_NOTHING_FOUND.create(this.original.substring(0, i));
        }

        public String toString() {
            return this.original;
        }
    }

    private interface h {

        void a(NBTBase nbtbase, List<NBTBase> list);

        void a(NBTBase nbtbase, Supplier<NBTBase> supplier, List<NBTBase> list);

        NBTBase a();

        int a(NBTBase nbtbase, Supplier<NBTBase> supplier);

        int a(NBTBase nbtbase);

        default List<NBTBase> a(List<NBTBase> list) {
            return this.a(list, this::a);
        }

        default List<NBTBase> a(List<NBTBase> list, Supplier<NBTBase> supplier) {
            return this.a(list, (nbtbase, list1) -> {
                this.a(nbtbase, supplier, list1);
            });
        }

        default List<NBTBase> a(List<NBTBase> list, BiConsumer<NBTBase, List<NBTBase>> biconsumer) {
            List<NBTBase> list1 = Lists.newArrayList();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                NBTBase nbtbase = (NBTBase) iterator.next();

                biconsumer.accept(nbtbase, list1);
            }

            return list1;
        }
    }

    private static class f implements ArgumentNBTKey.h {

        private final Predicate<NBTBase> predicate;

        public f(NBTTagCompound nbttagcompound) {
            this.predicate = ArgumentNBTKey.a(nbttagcompound);
        }

        @Override
        public void a(NBTBase nbtbase, List<NBTBase> list) {
            if (nbtbase instanceof NBTTagCompound && this.predicate.test(nbtbase)) {
                list.add(nbtbase);
            }

        }

        @Override
        public void a(NBTBase nbtbase, Supplier<NBTBase> supplier, List<NBTBase> list) {
            this.a(nbtbase, list);
        }

        @Override
        public NBTBase a() {
            return new NBTTagCompound();
        }

        @Override
        public int a(NBTBase nbtbase, Supplier<NBTBase> supplier) {
            return 0;
        }

        @Override
        public int a(NBTBase nbtbase) {
            return 0;
        }
    }

    private static class d implements ArgumentNBTKey.h {

        private final NBTTagCompound pattern;
        private final Predicate<NBTBase> predicate;

        public d(NBTTagCompound nbttagcompound) {
            this.pattern = nbttagcompound;
            this.predicate = ArgumentNBTKey.a(nbttagcompound);
        }

        @Override
        public void a(NBTBase nbtbase, List<NBTBase> list) {
            if (nbtbase instanceof NBTTagList) {
                NBTTagList nbttaglist = (NBTTagList) nbtbase;
                Stream stream = nbttaglist.stream().filter(this.predicate);

                Objects.requireNonNull(list);
                stream.forEach(list::add);
            }

        }

        @Override
        public void a(NBTBase nbtbase, Supplier<NBTBase> supplier, List<NBTBase> list) {
            MutableBoolean mutableboolean = new MutableBoolean();

            if (nbtbase instanceof NBTTagList) {
                NBTTagList nbttaglist = (NBTTagList) nbtbase;

                nbttaglist.stream().filter(this.predicate).forEach((nbtbase1) -> {
                    list.add(nbtbase1);
                    mutableboolean.setTrue();
                });
                if (mutableboolean.isFalse()) {
                    NBTTagCompound nbttagcompound = this.pattern.clone();

                    nbttaglist.add(nbttagcompound);
                    list.add(nbttagcompound);
                }
            }

        }

        @Override
        public NBTBase a() {
            return new NBTTagList();
        }

        @Override
        public int a(NBTBase nbtbase, Supplier<NBTBase> supplier) {
            int i = 0;

            if (nbtbase instanceof NBTTagList) {
                NBTTagList nbttaglist = (NBTTagList) nbtbase;
                int j = nbttaglist.size();

                if (j == 0) {
                    nbttaglist.add((NBTBase) supplier.get());
                    ++i;
                } else {
                    for (int k = 0; k < j; ++k) {
                        NBTBase nbtbase1 = nbttaglist.get(k);

                        if (this.predicate.test(nbtbase1)) {
                            NBTBase nbtbase2 = (NBTBase) supplier.get();

                            if (!nbtbase2.equals(nbtbase1) && nbttaglist.a(k, nbtbase2)) {
                                ++i;
                            }
                        }
                    }
                }
            }

            return i;
        }

        @Override
        public int a(NBTBase nbtbase) {
            int i = 0;

            if (nbtbase instanceof NBTTagList) {
                NBTTagList nbttaglist = (NBTTagList) nbtbase;

                for (int j = nbttaglist.size() - 1; j >= 0; --j) {
                    if (this.predicate.test(nbttaglist.get(j))) {
                        nbttaglist.remove(j);
                        ++i;
                    }
                }
            }

            return i;
        }
    }

    private static class a implements ArgumentNBTKey.h {

        public static final ArgumentNBTKey.a INSTANCE = new ArgumentNBTKey.a();

        private a() {}

        @Override
        public void a(NBTBase nbtbase, List<NBTBase> list) {
            if (nbtbase instanceof NBTList) {
                list.addAll((NBTList) nbtbase);
            }

        }

        @Override
        public void a(NBTBase nbtbase, Supplier<NBTBase> supplier, List<NBTBase> list) {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;

                if (nbtlist.isEmpty()) {
                    NBTBase nbtbase1 = (NBTBase) supplier.get();

                    if (nbtlist.b(0, nbtbase1)) {
                        list.add(nbtbase1);
                    }
                } else {
                    list.addAll(nbtlist);
                }
            }

        }

        @Override
        public NBTBase a() {
            return new NBTTagList();
        }

        @Override
        public int a(NBTBase nbtbase, Supplier<NBTBase> supplier) {
            if (!(nbtbase instanceof NBTList)) {
                return 0;
            } else {
                NBTList<?> nbtlist = (NBTList) nbtbase;
                int i = nbtlist.size();

                if (i == 0) {
                    nbtlist.b(0, (NBTBase) supplier.get());
                    return 1;
                } else {
                    NBTBase nbtbase1 = (NBTBase) supplier.get();
                    Stream stream = nbtlist.stream();

                    Objects.requireNonNull(nbtbase1);
                    int j = i - (int) stream.filter(nbtbase1::equals).count();

                    if (j == 0) {
                        return 0;
                    } else {
                        nbtlist.clear();
                        if (!nbtlist.b(0, nbtbase1)) {
                            return 0;
                        } else {
                            for (int k = 1; k < i; ++k) {
                                nbtlist.b(k, (NBTBase) supplier.get());
                            }

                            return j;
                        }
                    }
                }
            }
        }

        @Override
        public int a(NBTBase nbtbase) {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;
                int i = nbtlist.size();

                if (i > 0) {
                    nbtlist.clear();
                    return i;
                }
            }

            return 0;
        }
    }

    private static class c implements ArgumentNBTKey.h {

        private final int index;

        public c(int i) {
            this.index = i;
        }

        @Override
        public void a(NBTBase nbtbase, List<NBTBase> list) {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;
                int i = nbtlist.size();
                int j = this.index < 0 ? i + this.index : this.index;

                if (0 <= j && j < i) {
                    list.add((NBTBase) nbtlist.get(j));
                }
            }

        }

        @Override
        public void a(NBTBase nbtbase, Supplier<NBTBase> supplier, List<NBTBase> list) {
            this.a(nbtbase, list);
        }

        @Override
        public NBTBase a() {
            return new NBTTagList();
        }

        @Override
        public int a(NBTBase nbtbase, Supplier<NBTBase> supplier) {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;
                int i = nbtlist.size();
                int j = this.index < 0 ? i + this.index : this.index;

                if (0 <= j && j < i) {
                    NBTBase nbtbase1 = (NBTBase) nbtlist.get(j);
                    NBTBase nbtbase2 = (NBTBase) supplier.get();

                    if (!nbtbase2.equals(nbtbase1) && nbtlist.a(j, nbtbase2)) {
                        return 1;
                    }
                }
            }

            return 0;
        }

        @Override
        public int a(NBTBase nbtbase) {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;
                int i = nbtlist.size();
                int j = this.index < 0 ? i + this.index : this.index;

                if (0 <= j && j < i) {
                    nbtlist.remove(j);
                    return 1;
                }
            }

            return 0;
        }
    }

    private static class e implements ArgumentNBTKey.h {

        private final String name;
        private final NBTTagCompound pattern;
        private final Predicate<NBTBase> predicate;

        public e(String s, NBTTagCompound nbttagcompound) {
            this.name = s;
            this.pattern = nbttagcompound;
            this.predicate = ArgumentNBTKey.a(nbttagcompound);
        }

        @Override
        public void a(NBTBase nbtbase, List<NBTBase> list) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTBase nbtbase1 = ((NBTTagCompound) nbtbase).get(this.name);

                if (this.predicate.test(nbtbase1)) {
                    list.add(nbtbase1);
                }
            }

        }

        @Override
        public void a(NBTBase nbtbase, Supplier<NBTBase> supplier, List<NBTBase> list) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                NBTBase nbtbase1 = nbttagcompound.get(this.name);

                if (nbtbase1 == null) {
                    NBTTagCompound nbttagcompound1 = this.pattern.clone();

                    nbttagcompound.set(this.name, nbttagcompound1);
                    list.add(nbttagcompound1);
                } else if (this.predicate.test(nbtbase1)) {
                    list.add(nbtbase1);
                }
            }

        }

        @Override
        public NBTBase a() {
            return new NBTTagCompound();
        }

        @Override
        public int a(NBTBase nbtbase, Supplier<NBTBase> supplier) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                NBTBase nbtbase1 = nbttagcompound.get(this.name);

                if (this.predicate.test(nbtbase1)) {
                    NBTBase nbtbase2 = (NBTBase) supplier.get();

                    if (!nbtbase2.equals(nbtbase1)) {
                        nbttagcompound.set(this.name, nbtbase2);
                        return 1;
                    }
                }
            }

            return 0;
        }

        @Override
        public int a(NBTBase nbtbase) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                NBTBase nbtbase1 = nbttagcompound.get(this.name);

                if (this.predicate.test(nbtbase1)) {
                    nbttagcompound.remove(this.name);
                    return 1;
                }
            }

            return 0;
        }
    }

    private static class b implements ArgumentNBTKey.h {

        private final String name;

        public b(String s) {
            this.name = s;
        }

        @Override
        public void a(NBTBase nbtbase, List<NBTBase> list) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTBase nbtbase1 = ((NBTTagCompound) nbtbase).get(this.name);

                if (nbtbase1 != null) {
                    list.add(nbtbase1);
                }
            }

        }

        @Override
        public void a(NBTBase nbtbase, Supplier<NBTBase> supplier, List<NBTBase> list) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                NBTBase nbtbase1;

                if (nbttagcompound.hasKey(this.name)) {
                    nbtbase1 = nbttagcompound.get(this.name);
                } else {
                    nbtbase1 = (NBTBase) supplier.get();
                    nbttagcompound.set(this.name, nbtbase1);
                }

                list.add(nbtbase1);
            }

        }

        @Override
        public NBTBase a() {
            return new NBTTagCompound();
        }

        @Override
        public int a(NBTBase nbtbase, Supplier<NBTBase> supplier) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                NBTBase nbtbase1 = (NBTBase) supplier.get();
                NBTBase nbtbase2 = nbttagcompound.set(this.name, nbtbase1);

                if (!nbtbase1.equals(nbtbase2)) {
                    return 1;
                }
            }

            return 0;
        }

        @Override
        public int a(NBTBase nbtbase) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

                if (nbttagcompound.hasKey(this.name)) {
                    nbttagcompound.remove(this.name);
                    return 1;
                }
            }

            return 0;
        }
    }
}
