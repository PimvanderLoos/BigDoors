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
import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class ArgumentBlock {

    public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.block.tag.disallowed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.block.id.invalid", object);
    });
    public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.block.property.unknown", object, object1);
    });
    public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.block.property.duplicate", object1, object);
    });
    public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return IChatBaseComponent.translatable("argument.block.property.invalid", object, object2, object1);
    });
    public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.block.property.novalue", object, object1);
    });
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.block.property.unclosed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("arguments.block.tag.unknown", object);
    });
    private static final char SYNTAX_START_PROPERTIES = '[';
    private static final char SYNTAX_START_NBT = '{';
    private static final char SYNTAX_END_PROPERTIES = ']';
    private static final char SYNTAX_EQUALS = '=';
    private static final char SYNTAX_PROPERTY_SEPARATOR = ',';
    private static final char SYNTAX_TAG = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Block> blocks;
    private final StringReader reader;
    private final boolean forTesting;
    private final boolean allowNbt;
    private final Map<IBlockState<?>, Comparable<?>> properties = Maps.newHashMap();
    private final Map<String, String> vagueProperties = Maps.newHashMap();
    private MinecraftKey id = new MinecraftKey("");
    @Nullable
    private BlockStateList<Block, IBlockData> definition;
    @Nullable
    private IBlockData state;
    @Nullable
    private NBTTagCompound nbt;
    @Nullable
    private HolderSet<Block> tag;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;

    private ArgumentBlock(HolderLookup<Block> holderlookup, StringReader stringreader, boolean flag, boolean flag1) {
        this.suggestions = ArgumentBlock.SUGGEST_NOTHING;
        this.blocks = holderlookup;
        this.reader = stringreader;
        this.forTesting = flag;
        this.allowNbt = flag1;
    }

    public static ArgumentBlock.a parseForBlock(HolderLookup<Block> holderlookup, String s, boolean flag) throws CommandSyntaxException {
        return parseForBlock(holderlookup, new StringReader(s), flag);
    }

    public static ArgumentBlock.a parseForBlock(HolderLookup<Block> holderlookup, StringReader stringreader, boolean flag) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        try {
            ArgumentBlock argumentblock = new ArgumentBlock(holderlookup, stringreader, false, flag);

            argumentblock.parse();
            return new ArgumentBlock.a(argumentblock.state, argumentblock.properties, argumentblock.nbt);
        } catch (CommandSyntaxException commandsyntaxexception) {
            stringreader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public static Either<ArgumentBlock.a, ArgumentBlock.b> parseForTesting(HolderLookup<Block> holderlookup, String s, boolean flag) throws CommandSyntaxException {
        return parseForTesting(holderlookup, new StringReader(s), flag);
    }

    public static Either<ArgumentBlock.a, ArgumentBlock.b> parseForTesting(HolderLookup<Block> holderlookup, StringReader stringreader, boolean flag) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        try {
            ArgumentBlock argumentblock = new ArgumentBlock(holderlookup, stringreader, true, flag);

            argumentblock.parse();
            return argumentblock.tag != null ? Either.right(new ArgumentBlock.b(argumentblock.tag, argumentblock.vagueProperties, argumentblock.nbt)) : Either.left(new ArgumentBlock.a(argumentblock.state, argumentblock.properties, argumentblock.nbt));
        } catch (CommandSyntaxException commandsyntaxexception) {
            stringreader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Block> holderlookup, SuggestionsBuilder suggestionsbuilder, boolean flag, boolean flag1) {
        StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

        stringreader.setCursor(suggestionsbuilder.getStart());
        ArgumentBlock argumentblock = new ArgumentBlock(holderlookup, stringreader, flag, flag1);

        try {
            argumentblock.parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return (CompletableFuture) argumentblock.suggestions.apply(suggestionsbuilder.createOffset(stringreader.getCursor()));
    }

    private void parse() throws CommandSyntaxException {
        if (this.forTesting) {
            this.suggestions = this::suggestBlockIdOrTag;
        } else {
            this.suggestions = this::suggestItem;
        }

        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
            this.suggestions = this::suggestOpenVaguePropertiesOrNbt;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.readVagueProperties();
                this.suggestions = this::suggestOpenNbt;
            }
        } else {
            this.readBlock();
            this.suggestions = this::suggestOpenPropertiesOrNbt;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.readProperties();
                this.suggestions = this::suggestOpenNbt;
            }
        }

        if (this.allowNbt && this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = ArgumentBlock.SUGGEST_NOTHING;
            this.readNbt();
        }

    }

    private CompletableFuture<Suggestions> suggestPropertyNameOrEnd(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        return this.suggestPropertyName(suggestionsbuilder);
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyNameOrEnd(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        return this.suggestVaguePropertyName(suggestionsbuilder);
    }

    private CompletableFuture<Suggestions> suggestPropertyName(SuggestionsBuilder suggestionsbuilder) {
        String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);
        Iterator iterator = this.state.getProperties().iterator();

        while (iterator.hasNext()) {
            IBlockState<?> iblockstate = (IBlockState) iterator.next();

            if (!this.properties.containsKey(iblockstate) && iblockstate.getName().startsWith(s)) {
                suggestionsbuilder.suggest(iblockstate.getName() + "=");
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyName(SuggestionsBuilder suggestionsbuilder) {
        String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);

        if (this.tag != null) {
            Iterator iterator = this.tag.iterator();

            while (iterator.hasNext()) {
                Holder<Block> holder = (Holder) iterator.next();
                Iterator iterator1 = ((Block) holder.value()).getStateDefinition().getProperties().iterator();

                while (iterator1.hasNext()) {
                    IBlockState<?> iblockstate = (IBlockState) iterator1.next();

                    if (!this.vagueProperties.containsKey(iblockstate.getName()) && iblockstate.getName().startsWith(s)) {
                        suggestionsbuilder.suggest(iblockstate.getName() + "=");
                    }
                }
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty() && this.hasBlockEntity()) {
            suggestionsbuilder.suggest(String.valueOf('{'));
        }

        return suggestionsbuilder.buildFuture();
    }

    private boolean hasBlockEntity() {
        if (this.state != null) {
            return this.state.hasBlockEntity();
        } else {
            if (this.tag != null) {
                Iterator iterator = this.tag.iterator();

                while (iterator.hasNext()) {
                    Holder<Block> holder = (Holder) iterator.next();

                    if (((Block) holder.value()).defaultBlockState().hasBlockEntity()) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf('='));
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestNextPropertyOrEnd(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        if (suggestionsbuilder.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size()) {
            suggestionsbuilder.suggest(String.valueOf(','));
        }

        return suggestionsbuilder.buildFuture();
    }

    private static <T extends Comparable<T>> SuggestionsBuilder addSuggestions(SuggestionsBuilder suggestionsbuilder, IBlockState<T> iblockstate) {
        Iterator iterator = iblockstate.getPossibleValues().iterator();

        while (iterator.hasNext()) {
            T t0 = (Comparable) iterator.next();

            if (t0 instanceof Integer) {
                Integer integer = (Integer) t0;

                suggestionsbuilder.suggest(integer);
            } else {
                suggestionsbuilder.suggest(iblockstate.getName(t0));
            }
        }

        return suggestionsbuilder;
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyValue(SuggestionsBuilder suggestionsbuilder, String s) {
        boolean flag = false;

        if (this.tag != null) {
            Iterator iterator = this.tag.iterator();

            while (iterator.hasNext()) {
                Holder<Block> holder = (Holder) iterator.next();
                Block block = (Block) holder.value();
                IBlockState<?> iblockstate = block.getStateDefinition().getProperty(s);

                if (iblockstate != null) {
                    addSuggestions(suggestionsbuilder, iblockstate);
                }

                if (!flag) {
                    Iterator iterator1 = block.getStateDefinition().getProperties().iterator();

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

        if (flag) {
            suggestionsbuilder.suggest(String.valueOf(','));
        }

        suggestionsbuilder.suggest(String.valueOf(']'));
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenVaguePropertiesOrNbt(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty() && this.tag != null) {
            boolean flag = false;
            boolean flag1 = false;
            Iterator iterator = this.tag.iterator();

            while (iterator.hasNext()) {
                Holder<Block> holder = (Holder) iterator.next();
                Block block = (Block) holder.value();

                flag |= !block.getStateDefinition().getProperties().isEmpty();
                flag1 |= block.defaultBlockState().hasBlockEntity();
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

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenPropertiesOrNbt(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            if (!this.definition.getProperties().isEmpty()) {
                suggestionsbuilder.suggest(String.valueOf('['));
            }

            if (this.state.hasBlockEntity()) {
                suggestionsbuilder.suggest(String.valueOf('{'));
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggestResource(this.blocks.listTagIds().map(TagKey::location), suggestionsbuilder, String.valueOf('#'));
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggestResource(this.blocks.listElementIds().map(ResourceKey::location), suggestionsbuilder);
    }

    private CompletableFuture<Suggestions> suggestBlockIdOrTag(SuggestionsBuilder suggestionsbuilder) {
        this.suggestTag(suggestionsbuilder);
        this.suggestItem(suggestionsbuilder);
        return suggestionsbuilder.buildFuture();
    }

    private void readBlock() throws CommandSyntaxException {
        int i = this.reader.getCursor();

        this.id = MinecraftKey.read(this.reader);
        Block block = (Block) ((Holder.c) this.blocks.get(ResourceKey.create(Registries.BLOCK, this.id)).orElseThrow(() -> {
            this.reader.setCursor(i);
            return ArgumentBlock.ERROR_UNKNOWN_BLOCK.createWithContext(this.reader, this.id.toString());
        })).value();

        this.definition = block.getStateDefinition();
        this.state = block.defaultBlockState();
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.forTesting) {
            throw ArgumentBlock.ERROR_NO_TAGS_ALLOWED.createWithContext(this.reader);
        } else {
            int i = this.reader.getCursor();

            this.reader.expect('#');
            this.suggestions = this::suggestTag;
            MinecraftKey minecraftkey = MinecraftKey.read(this.reader);

            this.tag = (HolderSet) this.blocks.get(TagKey.create(Registries.BLOCK, minecraftkey)).orElseThrow(() -> {
                this.reader.setCursor(i);
                return ArgumentBlock.ERROR_UNKNOWN_TAG.createWithContext(this.reader, minecraftkey.toString());
            });
        }
    }

    private void readProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestPropertyNameOrEnd;
        this.reader.skipWhitespace();

        while (true) {
            if (this.reader.canRead() && this.reader.peek() != ']') {
                this.reader.skipWhitespace();
                int i = this.reader.getCursor();
                String s = this.reader.readString();
                IBlockState<?> iblockstate = this.definition.getProperty(s);

                if (iblockstate == null) {
                    this.reader.setCursor(i);
                    throw ArgumentBlock.ERROR_UNKNOWN_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
                }

                if (this.properties.containsKey(iblockstate)) {
                    this.reader.setCursor(i);
                    throw ArgumentBlock.ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
                }

                this.reader.skipWhitespace();
                this.suggestions = this::suggestEquals;
                if (!this.reader.canRead() || this.reader.peek() != '=') {
                    throw ArgumentBlock.ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = (suggestionsbuilder) -> {
                    return addSuggestions(suggestionsbuilder, iblockstate).buildFuture();
                };
                int j = this.reader.getCursor();

                this.setValue(iblockstate, this.reader.readString(), j);
                this.suggestions = this::suggestNextPropertyOrEnd;
                this.reader.skipWhitespace();
                if (!this.reader.canRead()) {
                    continue;
                }

                if (this.reader.peek() == ',') {
                    this.reader.skip();
                    this.suggestions = this::suggestPropertyName;
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

    private void readVagueProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestVaguePropertyNameOrEnd;
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
                this.suggestions = (suggestionsbuilder) -> {
                    return this.suggestVaguePropertyValue(suggestionsbuilder, s);
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
                    this.suggestions = this::suggestVaguePropertyName;
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

    private void readNbt() throws CommandSyntaxException {
        this.nbt = (new MojangsonParser(this.reader)).readStruct();
    }

    private <T extends Comparable<T>> void setValue(IBlockState<T> iblockstate, String s, int i) throws CommandSyntaxException {
        Optional<T> optional = iblockstate.getValue(s);

        if (optional.isPresent()) {
            this.state = (IBlockData) this.state.setValue(iblockstate, (Comparable) optional.get());
            this.properties.put(iblockstate, (Comparable) optional.get());
        } else {
            this.reader.setCursor(i);
            throw ArgumentBlock.ERROR_INVALID_VALUE.createWithContext(this.reader, this.id.toString(), iblockstate.getName(), s);
        }
    }

    public static String serialize(IBlockData iblockdata) {
        StringBuilder stringbuilder = new StringBuilder((String) iblockdata.getBlockHolder().unwrapKey().map((resourcekey) -> {
            return resourcekey.location().toString();
        }).orElse("air"));

        if (!iblockdata.getProperties().isEmpty()) {
            stringbuilder.append('[');
            boolean flag = false;

            for (UnmodifiableIterator unmodifiableiterator = iblockdata.getValues().entrySet().iterator(); unmodifiableiterator.hasNext(); flag = true) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();

                if (flag) {
                    stringbuilder.append(',');
                }

                appendProperty(stringbuilder, (IBlockState) entry.getKey(), (Comparable) entry.getValue());
            }

            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }

    private static <T extends Comparable<T>> void appendProperty(StringBuilder stringbuilder, IBlockState<T> iblockstate, Comparable<?> comparable) {
        stringbuilder.append(iblockstate.getName());
        stringbuilder.append('=');
        stringbuilder.append(iblockstate.getName(comparable));
    }

    public static record a(IBlockData blockState, Map<IBlockState<?>, Comparable<?>> properties, @Nullable NBTTagCompound nbt) {

    }

    public static record b(HolderSet<Block> tag, Map<String, String> vagueProperties, @Nullable NBTTagCompound nbt) {

    }
}
