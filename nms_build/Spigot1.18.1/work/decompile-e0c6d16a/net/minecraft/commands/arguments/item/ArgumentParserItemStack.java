package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tags;
import net.minecraft.world.item.Item;

public class ArgumentParserItemStack {

    public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new ChatMessage("argument.item.tag.disallowed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.item.id.invalid", new Object[]{object});
    });
    private static final char SYNTAX_START_NBT = '{';
    private static final char SYNTAX_TAG = '#';
    private static final BiFunction<SuggestionsBuilder, Tags<Item>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (suggestionsbuilder, tags) -> {
        return suggestionsbuilder.buildFuture();
    };
    private final StringReader reader;
    private final boolean forTesting;
    private Item item;
    @Nullable
    private NBTTagCompound nbt;
    private MinecraftKey tag = new MinecraftKey("");
    private int tagCursor;
    private BiFunction<SuggestionsBuilder, Tags<Item>, CompletableFuture<Suggestions>> suggestions;

    public ArgumentParserItemStack(StringReader stringreader, boolean flag) {
        this.suggestions = ArgumentParserItemStack.SUGGEST_NOTHING;
        this.reader = stringreader;
        this.forTesting = flag;
    }

    public Item getItem() {
        return this.item;
    }

    @Nullable
    public NBTTagCompound getNbt() {
        return this.nbt;
    }

    public MinecraftKey getTag() {
        return this.tag;
    }

    public void readItem() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        MinecraftKey minecraftkey = MinecraftKey.read(this.reader);

        this.item = (Item) IRegistry.ITEM.getOptional(minecraftkey).orElseThrow(() -> {
            this.reader.setCursor(i);
            return ArgumentParserItemStack.ERROR_UNKNOWN_ITEM.createWithContext(this.reader, minecraftkey.toString());
        });
    }

    public void readTag() throws CommandSyntaxException {
        if (!this.forTesting) {
            throw ArgumentParserItemStack.ERROR_NO_TAGS_ALLOWED.create();
        } else {
            this.suggestions = this::suggestTag;
            this.reader.expect('#');
            this.tagCursor = this.reader.getCursor();
            this.tag = MinecraftKey.read(this.reader);
        }
    }

    public void readNbt() throws CommandSyntaxException {
        this.nbt = (new MojangsonParser(this.reader)).readStruct();
    }

    public ArgumentParserItemStack parse() throws CommandSyntaxException {
        this.suggestions = this::suggestItemIdOrTag;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
        } else {
            this.readItem();
            this.suggestions = this::suggestOpenNbt;
        }

        if (this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = ArgumentParserItemStack.SUGGEST_NOTHING;
            this.readNbt();
        }

        return this;
    }

    private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder suggestionsbuilder, Tags<Item> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf('{'));
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder suggestionsbuilder, Tags<Item> tags) {
        return ICompletionProvider.suggestResource((Iterable) tags.getAvailableTags(), suggestionsbuilder.createOffset(this.tagCursor));
    }

    private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder suggestionsbuilder, Tags<Item> tags) {
        if (this.forTesting) {
            ICompletionProvider.suggestResource(tags.getAvailableTags(), suggestionsbuilder, String.valueOf('#'));
        }

        return ICompletionProvider.suggestResource((Iterable) IRegistry.ITEM.keySet(), suggestionsbuilder);
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder suggestionsbuilder, Tags<Item> tags) {
        return (CompletableFuture) this.suggestions.apply(suggestionsbuilder.createOffset(this.reader.getCursor()), tags);
    }
}
