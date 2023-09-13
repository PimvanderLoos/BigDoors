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
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ArgumentMinecraftKeyRegistered implements ArgumentType<MinecraftKey> {

    private static final Collection<String> a = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("advancement.advancementNotFound", new Object[]{object});
    });
    private static final DynamicCommandExceptionType c = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("recipe.notFound", new Object[]{object});
    });
    private static final DynamicCommandExceptionType d = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("predicate.unknown", new Object[]{object});
    });
    private static final DynamicCommandExceptionType e = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("attribute.unknown", new Object[]{object});
    });

    public ArgumentMinecraftKeyRegistered() {}

    public static ArgumentMinecraftKeyRegistered a() {
        return new ArgumentMinecraftKeyRegistered();
    }

    public static Advancement a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
        Advancement advancement = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancementData().a(minecraftkey);

        if (advancement == null) {
            throw ArgumentMinecraftKeyRegistered.b.create(minecraftkey);
        } else {
            return advancement;
        }
    }

    public static IRecipe<?> b(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        CraftingManager craftingmanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getCraftingManager();
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);

        return (IRecipe) craftingmanager.getRecipe(minecraftkey).orElseThrow(() -> {
            return ArgumentMinecraftKeyRegistered.c.create(minecraftkey);
        });
    }

    public static LootItemCondition c(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
        LootPredicateManager lootpredicatemanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getLootPredicateManager();
        LootItemCondition lootitemcondition = lootpredicatemanager.a(minecraftkey);

        if (lootitemcondition == null) {
            throw ArgumentMinecraftKeyRegistered.d.create(minecraftkey);
        } else {
            return lootitemcondition;
        }
    }

    public static AttributeBase d(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);

        return (AttributeBase) IRegistry.ATTRIBUTE.getOptional(minecraftkey).orElseThrow(() -> {
            return ArgumentMinecraftKeyRegistered.e.create(minecraftkey);
        });
    }

    public static MinecraftKey e(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
    }

    public MinecraftKey parse(StringReader stringreader) throws CommandSyntaxException {
        return MinecraftKey.a(stringreader);
    }

    public Collection<String> getExamples() {
        return ArgumentMinecraftKeyRegistered.a;
    }
}
