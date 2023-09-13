package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.UUID;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentUUID;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class CommandAttribute {

    private static final SuggestionProvider<CommandListenerWrapper> a = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a((Iterable) IRegistry.ATTRIBUTE.keySet(), suggestionsbuilder);
    };
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.attribute.failed.entity", new Object[]{object});
    });
    private static final Dynamic2CommandExceptionType c = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.attribute.failed.no_attribute", new Object[]{object, object1});
    });
    private static final Dynamic3CommandExceptionType d = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return new ChatMessage("commands.attribute.failed.no_modifier", new Object[]{object1, object, object2});
    });
    private static final Dynamic3CommandExceptionType e = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return new ChatMessage("commands.attribute.failed.modifier_already_present", new Object[]{object2, object1, object});
    });

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("attribute").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("target", (ArgumentType) ArgumentEntity.a()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("attribute", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAttribute.a).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("get").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), 1.0D);
        })).then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), DoubleArgumentType.getDouble(commandcontext, "scale"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("base").then(net.minecraft.commands.CommandDispatcher.a("set").then(net.minecraft.commands.CommandDispatcher.a("value", (ArgumentType) DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), DoubleArgumentType.getDouble(commandcontext, "value"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("get").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), 1.0D);
        })).then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), DoubleArgumentType.getDouble(commandcontext, "scale"));
        }))))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("modifier").then(net.minecraft.commands.CommandDispatcher.a("add").then(net.minecraft.commands.CommandDispatcher.a("uuid", (ArgumentType) ArgumentUUID.a()).then(net.minecraft.commands.CommandDispatcher.a("name", (ArgumentType) StringArgumentType.string()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("value", (ArgumentType) DoubleArgumentType.doubleArg()).then(net.minecraft.commands.CommandDispatcher.a("add").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), ArgumentUUID.a(commandcontext, "uuid"), StringArgumentType.getString(commandcontext, "name"), DoubleArgumentType.getDouble(commandcontext, "value"), AttributeModifier.Operation.ADDITION);
        }))).then(net.minecraft.commands.CommandDispatcher.a("multiply").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), ArgumentUUID.a(commandcontext, "uuid"), StringArgumentType.getString(commandcontext, "name"), DoubleArgumentType.getDouble(commandcontext, "value"), AttributeModifier.Operation.MULTIPLY_TOTAL);
        }))).then(net.minecraft.commands.CommandDispatcher.a("multiply_base").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), ArgumentUUID.a(commandcontext, "uuid"), StringArgumentType.getString(commandcontext, "name"), DoubleArgumentType.getDouble(commandcontext, "value"), AttributeModifier.Operation.MULTIPLY_BASE);
        }))))))).then(net.minecraft.commands.CommandDispatcher.a("remove").then(net.minecraft.commands.CommandDispatcher.a("uuid", (ArgumentType) ArgumentUUID.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), ArgumentUUID.a(commandcontext, "uuid"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("value").then(net.minecraft.commands.CommandDispatcher.a("get").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("uuid", (ArgumentType) ArgumentUUID.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), ArgumentUUID.a(commandcontext, "uuid"), 1.0D);
        })).then(net.minecraft.commands.CommandDispatcher.a("scale", (ArgumentType) DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentMinecraftKeyRegistered.d(commandcontext, "attribute"), ArgumentUUID.a(commandcontext, "uuid"), DoubleArgumentType.getDouble(commandcontext, "scale"));
        })))))))));
    }

    private static AttributeModifiable a(Entity entity, AttributeBase attributebase) throws CommandSyntaxException {
        AttributeModifiable attributemodifiable = a(entity).getAttributeMap().a(attributebase);

        if (attributemodifiable == null) {
            throw CommandAttribute.c.create(entity.getDisplayName(), new ChatMessage(attributebase.getName()));
        } else {
            return attributemodifiable;
        }
    }

    private static EntityLiving a(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof EntityLiving)) {
            throw CommandAttribute.b.create(entity.getDisplayName());
        } else {
            return (EntityLiving) entity;
        }
    }

    private static EntityLiving b(Entity entity, AttributeBase attributebase) throws CommandSyntaxException {
        EntityLiving entityliving = a(entity);

        if (!entityliving.getAttributeMap().b(attributebase)) {
            throw CommandAttribute.c.create(entity.getDisplayName(), new ChatMessage(attributebase.getName()));
        } else {
            return entityliving;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, double d0) throws CommandSyntaxException {
        EntityLiving entityliving = b(entity, attributebase);
        double d1 = entityliving.b(attributebase);

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.attribute.value.get.success", new Object[]{new ChatMessage(attributebase.getName()), entity.getDisplayName(), d1}), false);
        return (int) (d1 * d0);
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, double d0) throws CommandSyntaxException {
        EntityLiving entityliving = b(entity, attributebase);
        double d1 = entityliving.c(attributebase);

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.attribute.base_value.get.success", new Object[]{new ChatMessage(attributebase.getName()), entity.getDisplayName(), d1}), false);
        return (int) (d1 * d0);
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, UUID uuid, double d0) throws CommandSyntaxException {
        EntityLiving entityliving = b(entity, attributebase);
        AttributeMapBase attributemapbase = entityliving.getAttributeMap();

        if (!attributemapbase.a(attributebase, uuid)) {
            throw CommandAttribute.d.create(entity.getDisplayName(), new ChatMessage(attributebase.getName()), uuid);
        } else {
            double d1 = attributemapbase.b(attributebase, uuid);

            commandlistenerwrapper.sendMessage(new ChatMessage("commands.attribute.modifier.value.get.success", new Object[]{uuid, new ChatMessage(attributebase.getName()), entity.getDisplayName(), d1}), false);
            return (int) (d1 * d0);
        }
    }

    private static int c(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, double d0) throws CommandSyntaxException {
        a(entity, attributebase).setValue(d0);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.attribute.base_value.set.success", new Object[]{new ChatMessage(attributebase.getName()), entity.getDisplayName(), d0}), false);
        return 1;
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, UUID uuid, String s, double d0, AttributeModifier.Operation attributemodifier_operation) throws CommandSyntaxException {
        AttributeModifiable attributemodifiable = a(entity, attributebase);
        AttributeModifier attributemodifier = new AttributeModifier(uuid, s, d0, attributemodifier_operation);

        if (attributemodifiable.a(attributemodifier)) {
            throw CommandAttribute.e.create(entity.getDisplayName(), new ChatMessage(attributebase.getName()), uuid);
        } else {
            attributemodifiable.addModifier(attributemodifier);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.attribute.modifier.add.success", new Object[]{uuid, new ChatMessage(attributebase.getName()), entity.getDisplayName()}), false);
            return 1;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, UUID uuid) throws CommandSyntaxException {
        AttributeModifiable attributemodifiable = a(entity, attributebase);

        if (attributemodifiable.c(uuid)) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.attribute.modifier.remove.success", new Object[]{uuid, new ChatMessage(attributebase.getName()), entity.getDisplayName()}), false);
            return 1;
        } else {
            throw CommandAttribute.d.create(entity.getDisplayName(), new ChatMessage(attributebase.getName()), uuid);
        }
    }
}
