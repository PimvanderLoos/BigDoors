package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentUUID;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class CommandAttribute {

    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.attribute.failed.entity", object);
    });
    private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("commands.attribute.failed.no_attribute", object, object1);
    });
    private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return IChatBaseComponent.translatable("commands.attribute.failed.no_modifier", object1, object, object2);
    });
    private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return IChatBaseComponent.translatable("commands.attribute.failed.modifier_already_present", object2, object1, object);
    });

    public CommandAttribute() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("attribute").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("target", ArgumentEntity.entity()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("attribute", ResourceKeyArgument.key(IRegistry.ATTRIBUTE_REGISTRY)).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("get").executes((commandcontext) -> {
            return getAttributeValue((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), 1.0D);
        })).then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
            return getAttributeValue((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), DoubleArgumentType.getDouble(commandcontext, "scale"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("base").then(net.minecraft.commands.CommandDispatcher.literal("set").then(net.minecraft.commands.CommandDispatcher.argument("value", DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
            return setAttributeBase((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), DoubleArgumentType.getDouble(commandcontext, "value"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("get").executes((commandcontext) -> {
            return getAttributeBase((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), 1.0D);
        })).then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
            return getAttributeBase((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), DoubleArgumentType.getDouble(commandcontext, "scale"));
        }))))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("modifier").then(net.minecraft.commands.CommandDispatcher.literal("add").then(net.minecraft.commands.CommandDispatcher.argument("uuid", ArgumentUUID.uuid()).then(net.minecraft.commands.CommandDispatcher.argument("name", StringArgumentType.string()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("value", DoubleArgumentType.doubleArg()).then(net.minecraft.commands.CommandDispatcher.literal("add").executes((commandcontext) -> {
            return addModifier((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), ArgumentUUID.getUuid(commandcontext, "uuid"), StringArgumentType.getString(commandcontext, "name"), DoubleArgumentType.getDouble(commandcontext, "value"), AttributeModifier.Operation.ADDITION);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("multiply").executes((commandcontext) -> {
            return addModifier((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), ArgumentUUID.getUuid(commandcontext, "uuid"), StringArgumentType.getString(commandcontext, "name"), DoubleArgumentType.getDouble(commandcontext, "value"), AttributeModifier.Operation.MULTIPLY_TOTAL);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("multiply_base").executes((commandcontext) -> {
            return addModifier((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), ArgumentUUID.getUuid(commandcontext, "uuid"), StringArgumentType.getString(commandcontext, "name"), DoubleArgumentType.getDouble(commandcontext, "value"), AttributeModifier.Operation.MULTIPLY_BASE);
        }))))))).then(net.minecraft.commands.CommandDispatcher.literal("remove").then(net.minecraft.commands.CommandDispatcher.argument("uuid", ArgumentUUID.uuid()).executes((commandcontext) -> {
            return removeModifier((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), ArgumentUUID.getUuid(commandcontext, "uuid"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("value").then(net.minecraft.commands.CommandDispatcher.literal("get").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("uuid", ArgumentUUID.uuid()).executes((commandcontext) -> {
            return getAttributeModifier((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), ArgumentUUID.getUuid(commandcontext, "uuid"), 1.0D);
        })).then(net.minecraft.commands.CommandDispatcher.argument("scale", DoubleArgumentType.doubleArg()).executes((commandcontext) -> {
            return getAttributeModifier((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ResourceKeyArgument.getAttribute(commandcontext, "attribute"), ArgumentUUID.getUuid(commandcontext, "uuid"), DoubleArgumentType.getDouble(commandcontext, "scale"));
        })))))))));
    }

    private static AttributeModifiable getAttributeInstance(Entity entity, AttributeBase attributebase) throws CommandSyntaxException {
        AttributeModifiable attributemodifiable = getLivingEntity(entity).getAttributes().getInstance(attributebase);

        if (attributemodifiable == null) {
            throw CommandAttribute.ERROR_NO_SUCH_ATTRIBUTE.create(entity.getName(), IChatBaseComponent.translatable(attributebase.getDescriptionId()));
        } else {
            return attributemodifiable;
        }
    }

    private static EntityLiving getLivingEntity(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof EntityLiving)) {
            throw CommandAttribute.ERROR_NOT_LIVING_ENTITY.create(entity.getName());
        } else {
            return (EntityLiving) entity;
        }
    }

    private static EntityLiving getEntityWithAttribute(Entity entity, AttributeBase attributebase) throws CommandSyntaxException {
        EntityLiving entityliving = getLivingEntity(entity);

        if (!entityliving.getAttributes().hasAttribute(attributebase)) {
            throw CommandAttribute.ERROR_NO_SUCH_ATTRIBUTE.create(entity.getName(), IChatBaseComponent.translatable(attributebase.getDescriptionId()));
        } else {
            return entityliving;
        }
    }

    private static int getAttributeValue(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, double d0) throws CommandSyntaxException {
        EntityLiving entityliving = getEntityWithAttribute(entity, attributebase);
        double d1 = entityliving.getAttributeValue(attributebase);

        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.attribute.value.get.success", IChatBaseComponent.translatable(attributebase.getDescriptionId()), entity.getName(), d1), false);
        return (int) (d1 * d0);
    }

    private static int getAttributeBase(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, double d0) throws CommandSyntaxException {
        EntityLiving entityliving = getEntityWithAttribute(entity, attributebase);
        double d1 = entityliving.getAttributeBaseValue(attributebase);

        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.attribute.base_value.get.success", IChatBaseComponent.translatable(attributebase.getDescriptionId()), entity.getName(), d1), false);
        return (int) (d1 * d0);
    }

    private static int getAttributeModifier(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, UUID uuid, double d0) throws CommandSyntaxException {
        EntityLiving entityliving = getEntityWithAttribute(entity, attributebase);
        AttributeMapBase attributemapbase = entityliving.getAttributes();

        if (!attributemapbase.hasModifier(attributebase, uuid)) {
            throw CommandAttribute.ERROR_NO_SUCH_MODIFIER.create(entity.getName(), IChatBaseComponent.translatable(attributebase.getDescriptionId()), uuid);
        } else {
            double d1 = attributemapbase.getModifierValue(attributebase, uuid);

            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.attribute.modifier.value.get.success", uuid, IChatBaseComponent.translatable(attributebase.getDescriptionId()), entity.getName(), d1), false);
            return (int) (d1 * d0);
        }
    }

    private static int setAttributeBase(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, double d0) throws CommandSyntaxException {
        getAttributeInstance(entity, attributebase).setBaseValue(d0);
        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.attribute.base_value.set.success", IChatBaseComponent.translatable(attributebase.getDescriptionId()), entity.getName(), d0), false);
        return 1;
    }

    private static int addModifier(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, UUID uuid, String s, double d0, AttributeModifier.Operation attributemodifier_operation) throws CommandSyntaxException {
        AttributeModifiable attributemodifiable = getAttributeInstance(entity, attributebase);
        AttributeModifier attributemodifier = new AttributeModifier(uuid, s, d0, attributemodifier_operation);

        if (attributemodifiable.hasModifier(attributemodifier)) {
            throw CommandAttribute.ERROR_MODIFIER_ALREADY_PRESENT.create(entity.getName(), IChatBaseComponent.translatable(attributebase.getDescriptionId()), uuid);
        } else {
            attributemodifiable.addPermanentModifier(attributemodifier);
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.attribute.modifier.add.success", uuid, IChatBaseComponent.translatable(attributebase.getDescriptionId()), entity.getName()), false);
            return 1;
        }
    }

    private static int removeModifier(CommandListenerWrapper commandlistenerwrapper, Entity entity, AttributeBase attributebase, UUID uuid) throws CommandSyntaxException {
        AttributeModifiable attributemodifiable = getAttributeInstance(entity, attributebase);

        if (attributemodifiable.removePermanentModifier(uuid)) {
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.attribute.modifier.remove.success", uuid, IChatBaseComponent.translatable(attributebase.getDescriptionId()), entity.getName()), false);
            return 1;
        } else {
            throw CommandAttribute.ERROR_NO_SUCH_MODIFIER.create(entity.getName(), IChatBaseComponent.translatable(attributebase.getDescriptionId()), uuid);
        }
    }
}
