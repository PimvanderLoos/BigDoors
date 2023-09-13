package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.EnumItemSlot;

public class ArgumentInventorySlot implements ArgumentType<Integer> {

    private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("slot.unknown", object);
    });
    private static final Map<String, Integer> SLOTS = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
        int i;

        for (i = 0; i < 54; ++i) {
            hashmap.put("container." + i, i);
        }

        for (i = 0; i < 9; ++i) {
            hashmap.put("hotbar." + i, i);
        }

        for (i = 0; i < 27; ++i) {
            hashmap.put("inventory." + i, 9 + i);
        }

        for (i = 0; i < 27; ++i) {
            hashmap.put("enderchest." + i, 200 + i);
        }

        for (i = 0; i < 8; ++i) {
            hashmap.put("villager." + i, 300 + i);
        }

        for (i = 0; i < 15; ++i) {
            hashmap.put("horse." + i, 500 + i);
        }

        hashmap.put("weapon", EnumItemSlot.MAINHAND.getIndex(98));
        hashmap.put("weapon.mainhand", EnumItemSlot.MAINHAND.getIndex(98));
        hashmap.put("weapon.offhand", EnumItemSlot.OFFHAND.getIndex(98));
        hashmap.put("armor.head", EnumItemSlot.HEAD.getIndex(100));
        hashmap.put("armor.chest", EnumItemSlot.CHEST.getIndex(100));
        hashmap.put("armor.legs", EnumItemSlot.LEGS.getIndex(100));
        hashmap.put("armor.feet", EnumItemSlot.FEET.getIndex(100));
        hashmap.put("horse.saddle", 400);
        hashmap.put("horse.armor", 401);
        hashmap.put("horse.chest", 499);
    });

    public ArgumentInventorySlot() {}

    public static ArgumentInventorySlot slot() {
        return new ArgumentInventorySlot();
    }

    public static int getSlot(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (Integer) commandcontext.getArgument(s, Integer.class);
    }

    public Integer parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();

        if (!ArgumentInventorySlot.SLOTS.containsKey(s)) {
            throw ArgumentInventorySlot.ERROR_UNKNOWN_SLOT.create(s);
        } else {
            return (Integer) ArgumentInventorySlot.SLOTS.get(s);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggest((Iterable) ArgumentInventorySlot.SLOTS.keySet(), suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return ArgumentInventorySlot.EXAMPLES;
    }
}
