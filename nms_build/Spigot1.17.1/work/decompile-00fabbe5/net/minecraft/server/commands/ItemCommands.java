package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentInventorySlot;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.item.ArgumentItemStack;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class ItemCommands {

    static final Dynamic3CommandExceptionType ERROR_TARGET_NOT_A_CONTAINER = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return new ChatMessage("commands.item.target.not_a_container", new Object[]{object, object1, object2});
    });
    private static final Dynamic3CommandExceptionType ERROR_SOURCE_NOT_A_CONTAINER = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return new ChatMessage("commands.item.source.not_a_container", new Object[]{object, object1, object2});
    });
    static final DynamicCommandExceptionType ERROR_TARGET_INAPPLICABLE_SLOT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.item.target.no_such_slot", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_SOURCE_INAPPLICABLE_SLOT = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.item.source.no_such_slot", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_TARGET_NO_CHANGES = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.item.target.no_changes", new Object[]{object});
    });
    private static final Dynamic2CommandExceptionType ERROR_TARGET_NO_CHANGES_KNOWN_ITEM = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.item.target.no_changed.known_item", new Object[]{object, object1});
    });
    private static final SuggestionProvider<CommandListenerWrapper> SUGGEST_MODIFIER = (commandcontext, suggestionsbuilder) -> {
        ItemModifierManager itemmodifiermanager = ((CommandListenerWrapper) commandcontext.getSource()).getServer().aJ();

        return ICompletionProvider.a((Iterable) itemmodifiermanager.a(), suggestionsbuilder);
    };

    public ItemCommands() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("item").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("replace").then(net.minecraft.commands.CommandDispatcher.a("block").then(net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentPosition.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("slot", (ArgumentType) ArgumentInventorySlot.a()).then(net.minecraft.commands.CommandDispatcher.a("with").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("item", (ArgumentType) ArgumentItemStack.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentItemStack.a(commandcontext, "item").a(1, false));
        })).then(net.minecraft.commands.CommandDispatcher.a("count", (ArgumentType) IntegerArgumentType.integer(1, 64)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentItemStack.a(commandcontext, "item").a(IntegerArgumentType.getInteger(commandcontext, "count"), true));
        }))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("from").then(net.minecraft.commands.CommandDispatcher.a("block").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentPosition.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceSlot", (ArgumentType) ArgumentInventorySlot.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "source"), ArgumentInventorySlot.a(commandcontext, "sourceSlot"), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"));
        })).then(net.minecraft.commands.CommandDispatcher.a("modifier", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(ItemCommands.SUGGEST_MODIFIER).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "source"), ArgumentInventorySlot.a(commandcontext, "sourceSlot"), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentMinecraftKeyRegistered.d(commandcontext, "modifier"));
        })))))).then(net.minecraft.commands.CommandDispatcher.a("entity").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentEntity.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceSlot", (ArgumentType) ArgumentInventorySlot.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "source"), ArgumentInventorySlot.a(commandcontext, "sourceSlot"), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"));
        })).then(net.minecraft.commands.CommandDispatcher.a("modifier", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(ItemCommands.SUGGEST_MODIFIER).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "source"), ArgumentInventorySlot.a(commandcontext, "sourceSlot"), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentMinecraftKeyRegistered.d(commandcontext, "modifier"));
        })))))))))).then(net.minecraft.commands.CommandDispatcher.a("entity").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("slot", (ArgumentType) ArgumentInventorySlot.a()).then(net.minecraft.commands.CommandDispatcher.a("with").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("item", (ArgumentType) ArgumentItemStack.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentItemStack.a(commandcontext, "item").a(1, false));
        })).then(net.minecraft.commands.CommandDispatcher.a("count", (ArgumentType) IntegerArgumentType.integer(1, 64)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentItemStack.a(commandcontext, "item").a(IntegerArgumentType.getInteger(commandcontext, "count"), true));
        }))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("from").then(net.minecraft.commands.CommandDispatcher.a("block").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentPosition.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceSlot", (ArgumentType) ArgumentInventorySlot.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "source"), ArgumentInventorySlot.a(commandcontext, "sourceSlot"), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"));
        })).then(net.minecraft.commands.CommandDispatcher.a("modifier", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(ItemCommands.SUGGEST_MODIFIER).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "source"), ArgumentInventorySlot.a(commandcontext, "sourceSlot"), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentMinecraftKeyRegistered.d(commandcontext, "modifier"));
        })))))).then(net.minecraft.commands.CommandDispatcher.a("entity").then(net.minecraft.commands.CommandDispatcher.a("source", (ArgumentType) ArgumentEntity.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("sourceSlot", (ArgumentType) ArgumentInventorySlot.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "source"), ArgumentInventorySlot.a(commandcontext, "sourceSlot"), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"));
        })).then(net.minecraft.commands.CommandDispatcher.a("modifier", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(ItemCommands.SUGGEST_MODIFIER).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "source"), ArgumentInventorySlot.a(commandcontext, "sourceSlot"), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentMinecraftKeyRegistered.d(commandcontext, "modifier"));
        }))))))))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("modify").then(net.minecraft.commands.CommandDispatcher.a("block").then(net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentPosition.a()).then(net.minecraft.commands.CommandDispatcher.a("slot", (ArgumentType) ArgumentInventorySlot.a()).then(net.minecraft.commands.CommandDispatcher.a("modifier", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(ItemCommands.SUGGEST_MODIFIER).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentMinecraftKeyRegistered.d(commandcontext, "modifier"));
        })))))).then(net.minecraft.commands.CommandDispatcher.a("entity").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).then(net.minecraft.commands.CommandDispatcher.a("slot", (ArgumentType) ArgumentInventorySlot.a()).then(net.minecraft.commands.CommandDispatcher.a("modifier", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(ItemCommands.SUGGEST_MODIFIER).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentMinecraftKeyRegistered.d(commandcontext, "modifier"));
        })))))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, int i, LootItemFunction lootitemfunction) throws CommandSyntaxException {
        IInventory iinventory = a(commandlistenerwrapper, blockposition, ItemCommands.ERROR_TARGET_NOT_A_CONTAINER);

        if (i >= 0 && i < iinventory.getSize()) {
            ItemStack itemstack = a(commandlistenerwrapper, lootitemfunction, iinventory.getItem(i));

            iinventory.setItem(i, itemstack);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.item.block.set.success", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ(), itemstack.G()}), true);
            return 1;
        } else {
            throw ItemCommands.ERROR_TARGET_INAPPLICABLE_SLOT.create(i);
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, int i, LootItemFunction lootitemfunction) throws CommandSyntaxException {
        Map<Entity, ItemStack> map = Maps.newHashMapWithExpectedSize(collection.size());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            SlotAccess slotaccess = entity.k(i);

            if (slotaccess != SlotAccess.NULL) {
                ItemStack itemstack = a(commandlistenerwrapper, lootitemfunction, slotaccess.a().cloneItemStack());

                if (slotaccess.a(itemstack)) {
                    map.put(entity, itemstack);
                    if (entity instanceof EntityPlayer) {
                        ((EntityPlayer) entity).containerMenu.d();
                    }
                }
            }
        }

        if (map.isEmpty()) {
            throw ItemCommands.ERROR_TARGET_NO_CHANGES.create(i);
        } else {
            if (map.size() == 1) {
                Entry<Entity, ItemStack> entry = (Entry) map.entrySet().iterator().next();

                commandlistenerwrapper.sendMessage(new ChatMessage("commands.item.entity.set.success.single", new Object[]{((Entity) entry.getKey()).getScoreboardDisplayName(), ((ItemStack) entry.getValue()).G()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.item.entity.set.success.multiple", new Object[]{map.size()}), true);
            }

            return map.size();
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, int i, ItemStack itemstack) throws CommandSyntaxException {
        IInventory iinventory = a(commandlistenerwrapper, blockposition, ItemCommands.ERROR_TARGET_NOT_A_CONTAINER);

        if (i >= 0 && i < iinventory.getSize()) {
            iinventory.setItem(i, itemstack);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.item.block.set.success", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ(), itemstack.G()}), true);
            return 1;
        } else {
            throw ItemCommands.ERROR_TARGET_INAPPLICABLE_SLOT.create(i);
        }
    }

    private static IInventory a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, Dynamic3CommandExceptionType dynamic3commandexceptiontype) throws CommandSyntaxException {
        TileEntity tileentity = commandlistenerwrapper.getWorld().getTileEntity(blockposition);

        if (!(tileentity instanceof IInventory)) {
            throw dynamic3commandexceptiontype.create(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        } else {
            return (IInventory) tileentity;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, int i, ItemStack itemstack) throws CommandSyntaxException {
        List<Entity> list = Lists.newArrayListWithCapacity(collection.size());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            SlotAccess slotaccess = entity.k(i);

            if (slotaccess != SlotAccess.NULL && slotaccess.a(itemstack.cloneItemStack())) {
                list.add(entity);
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).containerMenu.d();
                }
            }
        }

        if (list.isEmpty()) {
            throw ItemCommands.ERROR_TARGET_NO_CHANGES_KNOWN_ITEM.create(itemstack.G(), i);
        } else {
            if (list.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.item.entity.set.success.single", new Object[]{((Entity) list.iterator().next()).getScoreboardDisplayName(), itemstack.G()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.item.entity.set.success.multiple", new Object[]{list.size(), itemstack.G()}), true);
            }

            return list.size();
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, int i, Collection<? extends Entity> collection, int j) throws CommandSyntaxException {
        return a(commandlistenerwrapper, collection, j, a(commandlistenerwrapper, blockposition, i));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, int i, Collection<? extends Entity> collection, int j, LootItemFunction lootitemfunction) throws CommandSyntaxException {
        return a(commandlistenerwrapper, collection, j, a(commandlistenerwrapper, lootitemfunction, a(commandlistenerwrapper, blockposition, i)));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, int i, BlockPosition blockposition1, int j) throws CommandSyntaxException {
        return a(commandlistenerwrapper, blockposition1, j, a(commandlistenerwrapper, blockposition, i));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, int i, BlockPosition blockposition1, int j, LootItemFunction lootitemfunction) throws CommandSyntaxException {
        return a(commandlistenerwrapper, blockposition1, j, a(commandlistenerwrapper, lootitemfunction, a(commandlistenerwrapper, blockposition, i)));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Entity entity, int i, BlockPosition blockposition, int j) throws CommandSyntaxException {
        return a(commandlistenerwrapper, blockposition, j, a(entity, i));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Entity entity, int i, BlockPosition blockposition, int j, LootItemFunction lootitemfunction) throws CommandSyntaxException {
        return a(commandlistenerwrapper, blockposition, j, a(commandlistenerwrapper, lootitemfunction, a(entity, i)));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Entity entity, int i, Collection<? extends Entity> collection, int j) throws CommandSyntaxException {
        return a(commandlistenerwrapper, collection, j, a(entity, i));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Entity entity, int i, Collection<? extends Entity> collection, int j, LootItemFunction lootitemfunction) throws CommandSyntaxException {
        return a(commandlistenerwrapper, collection, j, a(commandlistenerwrapper, lootitemfunction, a(entity, i)));
    }

    private static ItemStack a(CommandListenerWrapper commandlistenerwrapper, LootItemFunction lootitemfunction, ItemStack itemstack) {
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).set(LootContextParameters.ORIGIN, commandlistenerwrapper.getPosition()).setOptional(LootContextParameters.THIS_ENTITY, commandlistenerwrapper.getEntity());

        return (ItemStack) lootitemfunction.apply(itemstack, loottableinfo_builder.build(LootContextParameterSets.COMMAND));
    }

    private static ItemStack a(Entity entity, int i) throws CommandSyntaxException {
        SlotAccess slotaccess = entity.k(i);

        if (slotaccess == SlotAccess.NULL) {
            throw ItemCommands.ERROR_SOURCE_INAPPLICABLE_SLOT.create(i);
        } else {
            return slotaccess.a().cloneItemStack();
        }
    }

    private static ItemStack a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, int i) throws CommandSyntaxException {
        IInventory iinventory = a(commandlistenerwrapper, blockposition, ItemCommands.ERROR_SOURCE_NOT_A_CONTAINER);

        if (i >= 0 && i < iinventory.getSize()) {
            return iinventory.getItem(i).cloneItemStack();
        } else {
            throw ItemCommands.ERROR_SOURCE_INAPPLICABLE_SLOT.create(i);
        }
    }
}
