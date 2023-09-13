package net.minecraft.commands.arguments.item;

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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ArgumentItemStack implements ArgumentType<ArgumentPredicateItemStack> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
    private final HolderLookup<Item> items;

    public ArgumentItemStack(CommandBuildContext commandbuildcontext) {
        this.items = commandbuildcontext.holderLookup(Registries.ITEM);
    }

    public static ArgumentItemStack item(CommandBuildContext commandbuildcontext) {
        return new ArgumentItemStack(commandbuildcontext);
    }

    public ArgumentPredicateItemStack parse(StringReader stringreader) throws CommandSyntaxException {
        ArgumentParserItemStack.a argumentparseritemstack_a = ArgumentParserItemStack.parseForItem(this.items, stringreader);

        return new ArgumentPredicateItemStack(argumentparseritemstack_a.item(), argumentparseritemstack_a.nbt());
    }

    public static <S> ArgumentPredicateItemStack getItem(CommandContext<S> commandcontext, String s) {
        return (ArgumentPredicateItemStack) commandcontext.getArgument(s, ArgumentPredicateItemStack.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ArgumentParserItemStack.fillSuggestions(this.items, suggestionsbuilder, false);
    }

    public Collection<String> getExamples() {
        return ArgumentItemStack.EXAMPLES;
    }
}
