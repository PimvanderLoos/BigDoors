package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArgumentItemPredicate implements ArgumentType<ArgumentItemPredicate.b> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.item.tag.unknown", new Object[]{object});
    });

    public ArgumentItemPredicate() {}

    public static ArgumentItemPredicate itemPredicate() {
        return new ArgumentItemPredicate();
    }

    public ArgumentItemPredicate.b parse(StringReader stringreader) throws CommandSyntaxException {
        ArgumentParserItemStack argumentparseritemstack = (new ArgumentParserItemStack(stringreader, true)).parse();

        if (argumentparseritemstack.getItem() != null) {
            ArgumentItemPredicate.a argumentitempredicate_a = new ArgumentItemPredicate.a(argumentparseritemstack.getItem(), argumentparseritemstack.getNbt());

            return (commandcontext) -> {
                return argumentitempredicate_a;
            };
        } else {
            MinecraftKey minecraftkey = argumentparseritemstack.getTag();

            return (commandcontext) -> {
                Tag<Item> tag = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getTags().getTagOrThrow(IRegistry.ITEM_REGISTRY, minecraftkey, (minecraftkey1) -> {
                    return ArgumentItemPredicate.ERROR_UNKNOWN_TAG.create(minecraftkey1.toString());
                });

                return new ArgumentItemPredicate.c(tag, argumentparseritemstack.getNbt());
            };
        }
    }

    public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentItemPredicate.b) commandcontext.getArgument(s, ArgumentItemPredicate.b.class)).create(commandcontext);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

        stringreader.setCursor(suggestionsbuilder.getStart());
        ArgumentParserItemStack argumentparseritemstack = new ArgumentParserItemStack(stringreader, true);

        try {
            argumentparseritemstack.parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return argumentparseritemstack.fillSuggestions(suggestionsbuilder, TagsItem.getAllTags());
    }

    public Collection<String> getExamples() {
        return ArgumentItemPredicate.EXAMPLES;
    }

    private static class a implements Predicate<ItemStack> {

        private final Item item;
        @Nullable
        private final NBTTagCompound nbt;

        public a(Item item, @Nullable NBTTagCompound nbttagcompound) {
            this.item = item;
            this.nbt = nbttagcompound;
        }

        public boolean test(ItemStack itemstack) {
            return itemstack.is(this.item) && GameProfileSerializer.compareNbt(this.nbt, itemstack.getTag(), true);
        }
    }

    public interface b {

        Predicate<ItemStack> create(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;
    }

    private static class c implements Predicate<ItemStack> {

        private final Tag<Item> tag;
        @Nullable
        private final NBTTagCompound nbt;

        public c(Tag<Item> tag, @Nullable NBTTagCompound nbttagcompound) {
            this.tag = tag;
            this.nbt = nbttagcompound;
        }

        public boolean test(ItemStack itemstack) {
            return itemstack.is(this.tag) && GameProfileSerializer.compareNbt(this.nbt, itemstack.getTag(), true);
        }
    }
}
