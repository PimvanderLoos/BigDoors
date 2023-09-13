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
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ArgumentMinecraftKeyRegistered implements ArgumentType<MinecraftKey> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("advancement.advancementNotFound", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("recipe.notFound", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("predicate.unknown", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ATTRIBUTE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("attribute.unknown", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM_MODIFIER = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("item_modifier.unknown", new Object[]{object});
    });

    public ArgumentMinecraftKeyRegistered() {}

    public static ArgumentMinecraftKeyRegistered id() {
        return new ArgumentMinecraftKeyRegistered();
    }

    public static Advancement getAdvancement(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
        Advancement advancement = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancements().getAdvancement(minecraftkey);

        if (advancement == null) {
            throw ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_ADVANCEMENT.create(minecraftkey);
        } else {
            return advancement;
        }
    }

    public static IRecipe<?> getRecipe(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        CraftingManager craftingmanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getRecipeManager();
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);

        return (IRecipe) craftingmanager.byKey(minecraftkey).orElseThrow(() -> {
            return ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_RECIPE.create(minecraftkey);
        });
    }

    public static LootItemCondition getPredicate(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
        LootPredicateManager lootpredicatemanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPredicateManager();
        LootItemCondition lootitemcondition = lootpredicatemanager.get(minecraftkey);

        if (lootitemcondition == null) {
            throw ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_PREDICATE.create(minecraftkey);
        } else {
            return lootitemcondition;
        }
    }

    public static LootItemFunction getItemModifier(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
        ItemModifierManager itemmodifiermanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getItemModifierManager();
        LootItemFunction lootitemfunction = itemmodifiermanager.get(minecraftkey);

        if (lootitemfunction == null) {
            throw ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_ITEM_MODIFIER.create(minecraftkey);
        } else {
            return lootitemfunction;
        }
    }

    public static AttributeBase getAttribute(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);

        return (AttributeBase) IRegistry.ATTRIBUTE.getOptional(minecraftkey).orElseThrow(() -> {
            return ArgumentMinecraftKeyRegistered.ERROR_UNKNOWN_ATTRIBUTE.create(minecraftkey);
        });
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
