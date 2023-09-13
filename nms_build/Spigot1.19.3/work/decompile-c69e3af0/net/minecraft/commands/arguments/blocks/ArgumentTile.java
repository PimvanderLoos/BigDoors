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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public class ArgumentTile implements ArgumentType<ArgumentTileLocation> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");
    private final HolderLookup<Block> blocks;

    public ArgumentTile(CommandBuildContext commandbuildcontext) {
        this.blocks = commandbuildcontext.holderLookup(Registries.BLOCK);
    }

    public static ArgumentTile block(CommandBuildContext commandbuildcontext) {
        return new ArgumentTile(commandbuildcontext);
    }

    public ArgumentTileLocation parse(StringReader stringreader) throws CommandSyntaxException {
        ArgumentBlock.a argumentblock_a = ArgumentBlock.parseForBlock(this.blocks, stringreader, true);

        return new ArgumentTileLocation(argumentblock_a.blockState(), argumentblock_a.properties().keySet(), argumentblock_a.nbt());
    }

    public static ArgumentTileLocation getBlock(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ArgumentTileLocation) commandcontext.getArgument(s, ArgumentTileLocation.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ArgumentBlock.fillSuggestions(this.blocks, suggestionsbuilder, false, true);
    }

    public Collection<String> getExamples() {
        return ArgumentTile.EXAMPLES;
    }
}
