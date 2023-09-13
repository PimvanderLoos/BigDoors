package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
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
import net.minecraft.world.item.Item;

public class ArgumentParserItemStack {

    private static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.item.tag.disallowed"));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.item.id.invalid", object);
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("arguments.item.tag.unknown", object);
    });
    private static final char SYNTAX_START_NBT = '{';
    private static final char SYNTAX_TAG = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Item> items;
    private final StringReader reader;
    private final boolean allowTags;
    private Either<Holder<Item>, HolderSet<Item>> result;
    @Nullable
    private NBTTagCompound nbt;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;

    private ArgumentParserItemStack(HolderLookup<Item> holderlookup, StringReader stringreader, boolean flag) {
        this.suggestions = ArgumentParserItemStack.SUGGEST_NOTHING;
        this.items = holderlookup;
        this.reader = stringreader;
        this.allowTags = flag;
    }

    public static ArgumentParserItemStack.a parseForItem(HolderLookup<Item> holderlookup, StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        try {
            ArgumentParserItemStack argumentparseritemstack = new ArgumentParserItemStack(holderlookup, stringreader, false);

            argumentparseritemstack.parse();
            Holder<Item> holder = (Holder) argumentparseritemstack.result.left().orElseThrow(() -> {
                return new IllegalStateException("Parser returned unexpected tag name");
            });

            return new ArgumentParserItemStack.a(holder, argumentparseritemstack.nbt);
        } catch (CommandSyntaxException commandsyntaxexception) {
            stringreader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public static Either<ArgumentParserItemStack.a, ArgumentParserItemStack.b> parseForTesting(HolderLookup<Item> holderlookup, StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        try {
            ArgumentParserItemStack argumentparseritemstack = new ArgumentParserItemStack(holderlookup, stringreader, true);

            argumentparseritemstack.parse();
            return argumentparseritemstack.result.mapBoth((holder) -> {
                return new ArgumentParserItemStack.a(holder, argumentparseritemstack.nbt);
            }, (holderset) -> {
                return new ArgumentParserItemStack.b(holderset, argumentparseritemstack.nbt);
            });
        } catch (CommandSyntaxException commandsyntaxexception) {
            stringreader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Item> holderlookup, SuggestionsBuilder suggestionsbuilder, boolean flag) {
        StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

        stringreader.setCursor(suggestionsbuilder.getStart());
        ArgumentParserItemStack argumentparseritemstack = new ArgumentParserItemStack(holderlookup, stringreader, flag);

        try {
            argumentparseritemstack.parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return (CompletableFuture) argumentparseritemstack.suggestions.apply(suggestionsbuilder.createOffset(stringreader.getCursor()));
    }

    private void readItem() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        MinecraftKey minecraftkey = MinecraftKey.read(this.reader);
        Optional<? extends Holder<Item>> optional = this.items.get(ResourceKey.create(Registries.ITEM, minecraftkey));

        this.result = Either.left((Holder) optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return ArgumentParserItemStack.ERROR_UNKNOWN_ITEM.createWithContext(this.reader, minecraftkey);
        }));
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.allowTags) {
            throw ArgumentParserItemStack.ERROR_NO_TAGS_ALLOWED.createWithContext(this.reader);
        } else {
            int i = this.reader.getCursor();

            this.reader.expect('#');
            this.suggestions = this::suggestTag;
            MinecraftKey minecraftkey = MinecraftKey.read(this.reader);
            Optional<? extends HolderSet<Item>> optional = this.items.get(TagKey.create(Registries.ITEM, minecraftkey));

            this.result = Either.right((HolderSet) optional.orElseThrow(() -> {
                this.reader.setCursor(i);
                return ArgumentParserItemStack.ERROR_UNKNOWN_TAG.createWithContext(this.reader, minecraftkey);
            }));
        }
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = (new MojangsonParser(this.reader)).readStruct();
    }

    private void parse() throws CommandSyntaxException {
        if (this.allowTags) {
            this.suggestions = this::suggestItemIdOrTag;
        } else {
            this.suggestions = this::suggestItem;
        }

        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
        } else {
            this.readItem();
        }

        this.suggestions = this::suggestOpenNbt;
        if (this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = ArgumentParserItemStack.SUGGEST_NOTHING;
            this.readNbt();
        }

    }

    private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf('{'));
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggestResource(this.items.listTagIds().map(TagKey::location), suggestionsbuilder, String.valueOf('#'));
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggestResource(this.items.listElementIds().map(ResourceKey::location), suggestionsbuilder);
    }

    private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder suggestionsbuilder) {
        this.suggestTag(suggestionsbuilder);
        return this.suggestItem(suggestionsbuilder);
    }

    public static record a(Holder<Item> item, @Nullable NBTTagCompound nbt) {

    }

    public static record b(HolderSet<Item> tag, @Nullable NBTTagCompound nbt) {

    }
}
