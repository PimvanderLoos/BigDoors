package net.minecraft.commands.arguments.blocks;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class ArgumentBlock {

    public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new ChatMessage("argument.block.tag.disallowed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.block.id.invalid", new Object[]{object});
    });
    public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.block.property.unknown", new Object[]{object, object1});
    });
    public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.block.property.duplicate", new Object[]{object1, object});
    });
    public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return new ChatMessage("argument.block.property.invalid", new Object[]{object, object2, object1});
    });
    public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.block.property.novalue", new Object[]{object, object1});
    });
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType(new ChatMessage("argument.block.property.unclosed"));
    private static final char SYNTAX_START_PROPERTIES = '[';
    private static final char SYNTAX_START_NBT = '{';
    private static final char SYNTAX_END_PROPERTIES = ']';
    private static final char SYNTAX_EQUALS = '=';
    private static final char SYNTAX_PROPERTY_SEPARATOR = ',';
    private static final char SYNTAX_TAG = '#';
    private static final BiFunction<SuggestionsBuilder, Tags<Block>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (suggestionsbuilder, tags) -> {
        return suggestionsbuilder.buildFuture();
    };
    private final StringReader reader;
    private final boolean forTesting;
    private final Map<IBlockState<?>, Comparable<?>> properties = Maps.newHashMap();
    private final Map<String, String> vagueProperties = Maps.newHashMap();
    private MinecraftKey id = new MinecraftKey("");
    private BlockStateList<Block, IBlockData> definition;
    private IBlockData state;
    @Nullable
    private NBTTagCompound nbt;
    private MinecraftKey tag = new MinecraftKey("");
    private int tagCursor;
    private BiFunction<SuggestionsBuilder, Tags<Block>, CompletableFuture<Suggestions>> suggestions;

    public ArgumentBlock(StringReader stringreader, boolean flag) {
        this.suggestions = ArgumentBlock.SUGGEST_NOTHING;
        this.reader = stringreader;
        this.forTesting = flag;
    }

    public Map<IBlockState<?>, Comparable<?>> getStateMap() {
        return this.properties;
    }

    @Nullable
    public IBlockData getBlockData() {
        return this.state;
    }

    @Nullable
    public NBTTagCompound c() {
        return this.nbt;
    }

    @Nullable
    public MinecraftKey d() {
        return this.tag;
    }

    public ArgumentBlock a(boolean flag) throws CommandSyntaxException {
        this.suggestions = this::l;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.f();
            this.suggestions = this::i;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.h();
                this.suggestions = this::f;
            }
        } else {
            this.e();
            this.suggestions = this::j;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.g();
                this.suggestions = this::f;
            }
        }

        if (flag && this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = ArgumentBlock.SUGGEST_NOTHING;
            this.i();
        }

        return this;
    }

    private CompletableFuture<Suggestions> b(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        return this.d(suggestionsbuilder, tags);
    }

    private CompletableFuture<Suggestions> c(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        return this.e(suggestionsbuilder, tags);
    }

    private CompletableFuture<Suggestions> d(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);
        Iterator iterator = this.state.s().iterator();

        while (iterator.hasNext()) {
            IBlockState<?> iblockstate = (IBlockState) iterator.next();

            if (!this.properties.containsKey(iblockstate) && iblockstate.getName().startsWith(s)) {
                suggestionsbuilder.suggest(iblockstate.getName() + "=");
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> e(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);

        if (this.tag != null && !this.tag.getKey().isEmpty()) {
            Tag<Block> tag = tags.a(this.tag);

            if (tag != null) {
                Iterator iterator = tag.getTagged().iterator();

                while (iterator.hasNext()) {
                    Block block = (Block) iterator.next();
                    Iterator iterator1 = block.getStates().d().iterator();

                    while (iterator1.hasNext()) {
                        IBlockState<?> iblockstate = (IBlockState) iterator1.next();

                        if (!this.vagueProperties.containsKey(iblockstate.getName()) && iblockstate.getName().startsWith(s)) {
                            suggestionsbuilder.suggest(iblockstate.getName() + "=");
                        }
                    }
                }
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> f(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty() && this.a(tags)) {
            suggestionsbuilder.suggest(String.valueOf('{'));
        }

        return suggestionsbuilder.buildFuture();
    }

    private boolean a(Tags<Block> tags) {
        if (this.state != null) {
            return this.state.isTileEntity();
        } else {
            if (this.tag != null) {
                Tag<Block> tag = tags.a(this.tag);

                if (tag != null) {
                    Iterator iterator = tag.getTagged().iterator();

                    while (iterator.hasNext()) {
                        Block block = (Block) iterator.next();

                        if (block.getBlockData().isTileEntity()) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    private CompletableFuture<Suggestions> g(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf('='));
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> h(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        if (suggestionsbuilder.getRemaining().isEmpty() && this.properties.size() < this.state.s().size()) {
            suggestionsbuilder.suggest(String.valueOf(','));
        }

        return suggestionsbuilder.buildFuture();
    }

    private static <T extends Comparable<T>> SuggestionsBuilder a(SuggestionsBuilder suggestionsbuilder, IBlockState<T> iblockstate) {
        Iterator iterator = iblockstate.getValues().iterator();

        while (iterator.hasNext()) {
            T t0 = (Comparable) iterator.next();

            if (t0 instanceof Integer) {
                suggestionsbuilder.suggest((Integer) t0);
            } else {
                suggestionsbuilder.suggest(iblockstate.a(t0));
            }
        }

        return suggestionsbuilder;
    }

    private CompletableFuture<Suggestions> a(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags, String s) {
        boolean flag = false;

        if (this.tag != null && !this.tag.getKey().isEmpty()) {
            Tag<Block> tag = tags.a(this.tag);

            if (tag != null) {
                Iterator iterator = tag.getTagged().iterator();

                while (iterator.hasNext()) {
                    Block block = (Block) iterator.next();
                    IBlockState<?> iblockstate = block.getStates().a(s);

                    if (iblockstate != null) {
                        a(suggestionsbuilder, iblockstate);
                    }

                    if (!flag) {
                        Iterator iterator1 = block.getStates().d().iterator();

                        while (iterator1.hasNext()) {
                            IBlockState<?> iblockstate1 = (IBlockState) iterator1.next();

                            if (!this.vagueProperties.containsKey(iblockstate1.getName())) {
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (flag) {
            suggestionsbuilder.suggest(String.valueOf(','));
        }

        suggestionsbuilder.suggest(String.valueOf(']'));
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> i(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            Tag<Block> tag = tags.a(this.tag);

            if (tag != null) {
                boolean flag = false;
                boolean flag1 = false;
                Iterator iterator = tag.getTagged().iterator();

                while (iterator.hasNext()) {
                    Block block = (Block) iterator.next();

                    flag |= !block.getStates().d().isEmpty();
                    flag1 |= block.getBlockData().isTileEntity();
                    if (flag && flag1) {
                        break;
                    }
                }

                if (flag) {
                    suggestionsbuilder.suggest(String.valueOf('['));
                }

                if (flag1) {
                    suggestionsbuilder.suggest(String.valueOf('{'));
                }
            }
        }

        return this.k(suggestionsbuilder, tags);
    }

    private CompletableFuture<Suggestions> j(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            if (!this.state.getBlock().getStates().d().isEmpty()) {
                suggestionsbuilder.suggest(String.valueOf('['));
            }

            if (this.state.isTileEntity()) {
                suggestionsbuilder.suggest(String.valueOf('{'));
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> k(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        return ICompletionProvider.a((Iterable) tags.b(), suggestionsbuilder.createOffset(this.tagCursor).add(suggestionsbuilder));
    }

    private CompletableFuture<Suggestions> l(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        if (this.forTesting) {
            ICompletionProvider.a((Iterable) tags.b(), suggestionsbuilder, String.valueOf('#'));
        }

        ICompletionProvider.a((Iterable) IRegistry.BLOCK.keySet(), suggestionsbuilder);
        return suggestionsbuilder.buildFuture();
    }

    public void e() throws CommandSyntaxException {
        int i = this.reader.getCursor();

        this.id = MinecraftKey.a(this.reader);
        Block block = (Block) IRegistry.BLOCK.getOptional(this.id).orElseThrow(() -> {
            this.reader.setCursor(i);
            return ArgumentBlock.ERROR_UNKNOWN_BLOCK.createWithContext(this.reader, this.id.toString());
        });

        this.definition = block.getStates();
        this.state = block.getBlockData();
    }

    public void f() throws CommandSyntaxException {
        if (!this.forTesting) {
            throw ArgumentBlock.ERROR_NO_TAGS_ALLOWED.create();
        } else {
            this.suggestions = this::k;
            this.reader.expect('#');
            this.tagCursor = this.reader.getCursor();
            this.tag = MinecraftKey.a(this.reader);
        }
    }

    public void g() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::b;
        this.reader.skipWhitespace();

        while (true) {
            if (this.reader.canRead() && this.reader.peek() != ']') {
                this.reader.skipWhitespace();
                int i = this.reader.getCursor();
                String s = this.reader.readString();
                IBlockState<?> iblockstate = this.definition.a(s);

                if (iblockstate == null) {
                    this.reader.setCursor(i);
                    throw ArgumentBlock.ERROR_UNKNOWN_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
                }

                if (this.properties.containsKey(iblockstate)) {
                    this.reader.setCursor(i);
                    throw ArgumentBlock.ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
                }

                this.reader.skipWhitespace();
                this.suggestions = this::g;
                if (!this.reader.canRead() || this.reader.peek() != '=') {
                    throw ArgumentBlock.ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = (suggestionsbuilder, tags) -> {
                    return a(suggestionsbuilder, iblockstate).buildFuture();
                };
                int j = this.reader.getCursor();

                this.a(iblockstate, this.reader.readString(), j);
                this.suggestions = this::h;
                this.reader.skipWhitespace();
                if (!this.reader.canRead()) {
                    continue;
                }

                if (this.reader.peek() == ',') {
                    this.reader.skip();
                    this.suggestions = this::d;
                    continue;
                }

                if (this.reader.peek() != ']') {
                    throw ArgumentBlock.ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead()) {
                this.reader.skip();
                return;
            }

            throw ArgumentBlock.ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
        }
    }

    public void h() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::c;
        int i = -1;

        this.reader.skipWhitespace();

        while (true) {
            if (this.reader.canRead() && this.reader.peek() != ']') {
                this.reader.skipWhitespace();
                int j = this.reader.getCursor();
                String s = this.reader.readString();

                if (this.vagueProperties.containsKey(s)) {
                    this.reader.setCursor(j);
                    throw ArgumentBlock.ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
                }

                this.reader.skipWhitespace();
                if (!this.reader.canRead() || this.reader.peek() != '=') {
                    this.reader.setCursor(j);
                    throw ArgumentBlock.ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = (suggestionsbuilder, tags) -> {
                    return this.a(suggestionsbuilder, tags, s);
                };
                i = this.reader.getCursor();
                String s1 = this.reader.readString();

                this.vagueProperties.put(s, s1);
                this.reader.skipWhitespace();
                if (!this.reader.canRead()) {
                    continue;
                }

                i = -1;
                if (this.reader.peek() == ',') {
                    this.reader.skip();
                    this.suggestions = this::e;
                    continue;
                }

                if (this.reader.peek() != ']') {
                    throw ArgumentBlock.ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead()) {
                this.reader.skip();
                return;
            }

            if (i >= 0) {
                this.reader.setCursor(i);
            }

            throw ArgumentBlock.ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
        }
    }

    public void i() throws CommandSyntaxException {
        this.nbt = (new MojangsonParser(this.reader)).f();
    }

    private <T extends Comparable<T>> void a(IBlockState<T> iblockstate, String s, int i) throws CommandSyntaxException {
        Optional<T> optional = iblockstate.b(s);

        if (optional.isPresent()) {
            this.state = (IBlockData) this.state.set(iblockstate, (Comparable) optional.get());
            this.properties.put(iblockstate, (Comparable) optional.get());
        } else {
            this.reader.setCursor(i);
            throw ArgumentBlock.ERROR_INVALID_VALUE.createWithContext(this.reader, this.id.toString(), iblockstate.getName(), s);
        }
    }

    public static String a(IBlockData iblockdata) {
        StringBuilder stringbuilder = new StringBuilder(IRegistry.BLOCK.getKey(iblockdata.getBlock()).toString());

        if (!iblockdata.s().isEmpty()) {
            stringbuilder.append('[');
            boolean flag = false;

            for (UnmodifiableIterator unmodifiableiterator = iblockdata.getStateMap().entrySet().iterator(); unmodifiableiterator.hasNext(); flag = true) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();

                if (flag) {
                    stringbuilder.append(',');
                }

                a(stringbuilder, (IBlockState) entry.getKey(), (Comparable) entry.getValue());
            }

            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }

    private static <T extends Comparable<T>> void a(StringBuilder stringbuilder, IBlockState<T> iblockstate, Comparable<?> comparable) {
        stringbuilder.append(iblockstate.getName());
        stringbuilder.append('=');
        stringbuilder.append(iblockstate.a(comparable));
    }

    public CompletableFuture<Suggestions> a(SuggestionsBuilder suggestionsbuilder, Tags<Block> tags) {
        return (CompletableFuture) this.suggestions.apply(suggestionsbuilder.createOffset(this.reader.getCursor()), tags);
    }

    public Map<String, String> j() {
        return this.vagueProperties;
    }
}
