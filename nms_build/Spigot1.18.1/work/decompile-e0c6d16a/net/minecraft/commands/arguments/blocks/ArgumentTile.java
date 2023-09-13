package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.tags.TagsBlock;

public class ArgumentTile implements ArgumentType<ArgumentTileLocation> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

    public ArgumentTile() {}

    public static ArgumentTile block() {
        return new ArgumentTile();
    }

    public ArgumentTileLocation parse(StringReader stringreader) throws CommandSyntaxException {
        ArgumentBlock argumentblock = (new ArgumentBlock(stringreader, false)).parse(true);

        return new ArgumentTileLocation(argumentblock.getState(), argumentblock.getProperties().keySet(), argumentblock.getNbt());
    }

    public static ArgumentTileLocation getBlock(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ArgumentTileLocation) commandcontext.getArgument(s, ArgumentTileLocation.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

        stringreader.setCursor(suggestionsbuilder.getStart());
        ArgumentBlock argumentblock = new ArgumentBlock(stringreader, false);

        try {
            argumentblock.parse(true);
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return argumentblock.fillSuggestions(suggestionsbuilder, TagsBlock.getAllTags());
    }

    public Collection<String> getExamples() {
        return ArgumentTile.EXAMPLES;
    }
}
