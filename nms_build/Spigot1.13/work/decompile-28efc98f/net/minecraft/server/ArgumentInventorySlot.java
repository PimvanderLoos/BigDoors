package net.minecraft.server;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class ArgumentInventorySlot implements ArgumentType<Integer> {

    private static final Collection<String> a = Arrays.asList(new String[] { "container.5", "12", "weapon"});
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("slot.unknown", new Object[] { object});
    });
    private static final Map<String, Integer> c = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        int i;

        for (i = 0; i < 54; ++i) {
            hashmap.put("container." + i, Integer.valueOf(i));
        }

        for (i = 0; i < 9; ++i) {
            hashmap.put("hotbar." + i, Integer.valueOf(i));
        }

        for (i = 0; i < 27; ++i) {
            hashmap.put("inventory." + i, Integer.valueOf(9 + i));
        }

        for (i = 0; i < 27; ++i) {
            hashmap.put("enderchest." + i, Integer.valueOf(200 + i));
        }

        for (i = 0; i < 8; ++i) {
            hashmap.put("villager." + i, Integer.valueOf(300 + i));
        }

        for (i = 0; i < 15; ++i) {
            hashmap.put("horse." + i, Integer.valueOf(500 + i));
        }

        hashmap.put("weapon", Integer.valueOf(98));
        hashmap.put("weapon.mainhand", Integer.valueOf(98));
        hashmap.put("weapon.offhand", Integer.valueOf(99));
        hashmap.put("armor.head", Integer.valueOf(100 + EnumItemSlot.HEAD.b()));
        hashmap.put("armor.chest", Integer.valueOf(100 + EnumItemSlot.CHEST.b()));
        hashmap.put("armor.legs", Integer.valueOf(100 + EnumItemSlot.LEGS.b()));
        hashmap.put("armor.feet", Integer.valueOf(100 + EnumItemSlot.FEET.b()));
        hashmap.put("horse.saddle", Integer.valueOf(400));
        hashmap.put("horse.armor", Integer.valueOf(401));
        hashmap.put("horse.chest", Integer.valueOf(499));
    });

    public ArgumentInventorySlot() {}

    public static ArgumentInventorySlot a() {
        return new ArgumentInventorySlot();
    }

    public static int a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return ((Integer) commandcontext.getArgument(s, Integer.class)).intValue();
    }

    public Integer a(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();

        if (!ArgumentInventorySlot.c.containsKey(s)) {
            throw ArgumentInventorySlot.b.create(s);
        } else {
            return (Integer) ArgumentInventorySlot.c.get(s);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.b(ArgumentInventorySlot.c.keySet(), suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return ArgumentInventorySlot.a;
    }

    public Object parse(StringReader stringreader) throws CommandSyntaxException {
        return this.a(stringreader);
    }
}
