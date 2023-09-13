package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ArgumentMinecraftKeyRegistered implements ArgumentType<MinecraftKey> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("advancement.advancementNotFound", object);
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("recipe.notFound", object);
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("predicate.unknown", object);
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM_MODIFIER = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("item_modifier.unknown", object);
    });

    public ArgumentMinecraftKeyRegistered() {}

    public static ArgumentMinecraftKeyRegistered id() {
        return new ArgumentMinecraftKeyRegistered();
    }

    public static Advancement getAdvancement(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = getId(commandcontext, s);
        Advancement advancement = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancements().getAdvancement(minecraftkey);

        if (advancement == null) {
            throw ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_ADVANCEMENT.create(minecraftkey);
        } else {
            return advancement;
        }
    }

    public static IRecipe<?> getRecipe(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        CraftingManager craftingmanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getRecipeManager();
        MinecraftKey minecraftkey = getId(commandcontext, s);

        return (IRecipe) craftingmanager.byKey(minecraftkey).orElseThrow(() -> {
            return ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_RECIPE.create(minecraftkey);
        });
    }

    public static LootItemCondition getPredicate(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = getId(commandcontext, s);
        LootPredicateManager lootpredicatemanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPredicateManager();
        LootItemCondition lootitemcondition = lootpredicatemanager.get(minecraftkey);

        if (lootitemcondition == null) {
            throw ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_PREDICATE.create(minecraftkey);
        } else {
            return lootitemcondition;
        }
    }

    public static LootItemFunction getItemModifier(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = getId(commandcontext, s);
        ItemModifierManager itemmodifiermanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getItemModifierManager();
        LootItemFunction lootitemfunction = itemmodifiermanager.get(minecraftkey);

        if (lootitemfunction == null) {
            throw ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_ITEM_MODIFIER.create(minecraftkey);
        } else {
            return lootitemfunction;
        }
    }

    public static MinecraftKey getId(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
    }

    public MinecraftKey parse(StringReader stringreader) throws CommandSyntaxException {
        return MinecraftKey.read(stringreader);
    }

    public Collection<String> getExamples() {
        return ArgumentMinecraftKeyRegistered.EXAMPLES;
    }
}
