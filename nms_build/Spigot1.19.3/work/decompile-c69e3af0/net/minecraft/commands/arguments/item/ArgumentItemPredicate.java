package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArgumentItemPredicate implements ArgumentType<ArgumentItemPredicate.a> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
    private final HolderLookup<Item> items;

    public ArgumentItemPredicate(CommandBuildContext commandbuildcontext) {
        this.items = commandbuildcontext.holderLookup(Registries.ITEM);
    }

    public static ArgumentItemPredicate itemPredicate(CommandBuildContext commandbuildcontext) {
        return new ArgumentItemPredicate(commandbuildcontext);
    }

    public ArgumentItemPredicate.a parse(StringReader stringreader) throws CommandSyntaxException {
        Either<ArgumentParserItemStack.a, ArgumentParserItemStack.b> either = ArgumentParserItemStack.parseForTesting(this.items, stringreader);

        return (ArgumentItemPredicate.a) either.map((argumentparseritemstack_a) -> {
            return createResult((holder) -> {
                return holder == argumentparseritemstack_a.item();
            }, argumentparseritemstack_a.nbt());
        }, (argumentparseritemstack_b) -> {
            HolderSet holderset = argumentparseritemstack_b.tag();

            Objects.requireNonNull(holderset);
            return createResult(holderset::contains, argumentparseritemstack_b.nbt());
        });
    }

    public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (Predicate) commandcontext.getArgument(s, ArgumentItemPredicate.a.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ArgumentParserItemStack.fillSuggestions(this.items, suggestionsbuilder, true);
    }

    public Collection<String> getExamples() {
        return ArgumentItemPredicate.EXAMPLES;
    }

    private static ArgumentItemPredicate.a createResult(Predicate<Holder<Item>> predicate, @Nullable NBTTagCompound nbttagcompound) {
        return nbttagcompound != null ? (itemstack) -> {
            return itemstack.is(predicate) && GameProfileSerializer.compareNbt(nbttagcompound, itemstack.getTag(), true);
        } : (itemstack) -> {
            return itemstack.is(predicate);
        };
    }

    public interface a extends Predicate<ItemStack> {}
}
