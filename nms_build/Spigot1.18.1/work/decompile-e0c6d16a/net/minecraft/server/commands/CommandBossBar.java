package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.bossevents.BossBattleCustom;
import net.minecraft.server.bossevents.BossBattleCustomData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.BossBattle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;

public class CommandBossBar {

    private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.bossbar.create.failed", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.bossbar.unknown", new Object[]{object});
    });
    private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.players.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.name.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.color.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.style.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.value.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.max.unchanged"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.visibility.unchanged.hidden"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.visibility.unchanged.visible"));
    public static final SuggestionProvider<CommandListenerWrapper> SUGGEST_BOSS_BAR = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.suggestResource((Iterable) ((CommandListenerWrapper) commandcontext.getSource()).getServer().getCustomBossEvents().getIds(), suggestionsbuilder);
    };

    public CommandBossBar() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("bossbar").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("add").then(net.minecraft.commands.CommandDispatcher.argument("id", ArgumentMinecraftKeyRegistered.id()).then(net.minecraft.commands.CommandDispatcher.argument("name", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return createBar((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.getId(commandcontext, "id"), ArgumentChatComponent.getComponent(commandcontext, "name"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("remove").then(net.minecraft.commands.CommandDispatcher.argument("id", ArgumentMinecraftKeyRegistered.id()).suggests(CommandBossBar.SUGGEST_BOSS_BAR).executes((commandcontext) -> {
            return removeBar((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("list").executes((commandcontext) -> {
            return listBars((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.literal("set").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("id", ArgumentMinecraftKeyRegistered.id()).suggests(CommandBossBar.SUGGEST_BOSS_BAR).then(net.minecraft.commands.CommandDispatcher.literal("name").then(net.minecraft.commands.CommandDispatcher.argument("name", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return setName((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), ArgumentChatComponent.getComponent(commandcontext, "name"));
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("color").then(net.minecraft.commands.CommandDispatcher.literal("pink").executes((commandcontext) -> {
            return setColor((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarColor.PINK);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("blue").executes((commandcontext) -> {
            return setColor((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarColor.BLUE);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("red").executes((commandcontext) -> {
            return setColor((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarColor.RED);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("green").executes((commandcontext) -> {
            return setColor((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarColor.GREEN);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("yellow").executes((commandcontext) -> {
            return setColor((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarColor.YELLOW);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("purple").executes((commandcontext) -> {
            return setColor((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarColor.PURPLE);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("white").executes((commandcontext) -> {
            return setColor((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarColor.WHITE);
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("style").then(net.minecraft.commands.CommandDispatcher.literal("progress").executes((commandcontext) -> {
            return setStyle((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarStyle.PROGRESS);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("notched_6").executes((commandcontext) -> {
            return setStyle((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarStyle.NOTCHED_6);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("notched_10").executes((commandcontext) -> {
            return setStyle((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarStyle.NOTCHED_10);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("notched_12").executes((commandcontext) -> {
            return setStyle((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarStyle.NOTCHED_12);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("notched_20").executes((commandcontext) -> {
            return setStyle((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BossBattle.BarStyle.NOTCHED_20);
        })))).then(net.minecraft.commands.CommandDispatcher.literal("value").then(net.minecraft.commands.CommandDispatcher.argument("value", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return setValue((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), IntegerArgumentType.getInteger(commandcontext, "value"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("max").then(net.minecraft.commands.CommandDispatcher.argument("max", IntegerArgumentType.integer(1)).executes((commandcontext) -> {
            return setMax((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), IntegerArgumentType.getInteger(commandcontext, "max"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("visible").then(net.minecraft.commands.CommandDispatcher.argument("visible", BoolArgumentType.bool()).executes((commandcontext) -> {
            return setVisible((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), BoolArgumentType.getBool(commandcontext, "visible"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("players").executes((commandcontext) -> {
            return setPlayers((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), Collections.emptyList());
        })).then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).executes((commandcontext) -> {
            return setPlayers((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext), ArgumentEntity.getOptionalPlayers(commandcontext, "targets"));
        })))))).then(net.minecraft.commands.CommandDispatcher.literal("get").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("id", ArgumentMinecraftKeyRegistered.id()).suggests(CommandBossBar.SUGGEST_BOSS_BAR).then(net.minecraft.commands.CommandDispatcher.literal("value").executes((commandcontext) -> {
            return getValue((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext));
        }))).then(net.minecraft.commands.CommandDispatcher.literal("max").executes((commandcontext) -> {
            return getMax((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext));
        }))).then(net.minecraft.commands.CommandDispatcher.literal("visible").executes((commandcontext) -> {
            return getVisible((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext));
        }))).then(net.minecraft.commands.CommandDispatcher.literal("players").executes((commandcontext) -> {
            return getPlayers((CommandListenerWrapper) commandcontext.getSource(), getBossBar(commandcontext));
        })))));
    }

    private static int getValue(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.get.value", new Object[]{bossbattlecustom.getDisplayName(), bossbattlecustom.getValue()}), true);
        return bossbattlecustom.getValue();
    }

    private static int getMax(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.get.max", new Object[]{bossbattlecustom.getDisplayName(), bossbattlecustom.getMax()}), true);
        return bossbattlecustom.getMax();
    }

    private static int getVisible(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        if (bossbattlecustom.isVisible()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.get.visible.visible", new Object[]{bossbattlecustom.getDisplayName()}), true);
            return 1;
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.get.visible.hidden", new Object[]{bossbattlecustom.getDisplayName()}), true);
            return 0;
        }
    }

    private static int getPlayers(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        if (bossbattlecustom.getPlayers().isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.get.players.none", new Object[]{bossbattlecustom.getDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.get.players.some", new Object[]{bossbattlecustom.getDisplayName(), bossbattlecustom.getPlayers().size(), ChatComponentUtils.formatList(bossbattlecustom.getPlayers(), EntityHuman::getDisplayName)}), true);
        }

        return bossbattlecustom.getPlayers().size();
    }

    private static int setVisible(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, boolean flag) throws CommandSyntaxException {
        if (bossbattlecustom.isVisible() == flag) {
            if (flag) {
                throw CommandBossBar.ERROR_ALREADY_VISIBLE.create();
            } else {
                throw CommandBossBar.ERROR_ALREADY_HIDDEN.create();
            }
        } else {
            bossbattlecustom.setVisible(flag);
            if (flag) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.visible.success.visible", new Object[]{bossbattlecustom.getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.visible.success.hidden", new Object[]{bossbattlecustom.getDisplayName()}), true);
            }

            return 0;
        }
    }

    private static int setValue(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, int i) throws CommandSyntaxException {
        if (bossbattlecustom.getValue() == i) {
            throw CommandBossBar.ERROR_NO_VALUE_CHANGE.create();
        } else {
            bossbattlecustom.setValue(i);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.value.success", new Object[]{bossbattlecustom.getDisplayName(), i}), true);
            return i;
        }
    }

    private static int setMax(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, int i) throws CommandSyntaxException {
        if (bossbattlecustom.getMax() == i) {
            throw CommandBossBar.ERROR_NO_MAX_CHANGE.create();
        } else {
            bossbattlecustom.setMax(i);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.max.success", new Object[]{bossbattlecustom.getDisplayName(), i}), true);
            return i;
        }
    }

    private static int setColor(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, BossBattle.BarColor bossbattle_barcolor) throws CommandSyntaxException {
        if (bossbattlecustom.getColor().equals(bossbattle_barcolor)) {
            throw CommandBossBar.ERROR_NO_COLOR_CHANGE.create();
        } else {
            bossbattlecustom.setColor(bossbattle_barcolor);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.color.success", new Object[]{bossbattlecustom.getDisplayName()}), true);
            return 0;
        }
    }

    private static int setStyle(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, BossBattle.BarStyle bossbattle_barstyle) throws CommandSyntaxException {
        if (bossbattlecustom.getOverlay().equals(bossbattle_barstyle)) {
            throw CommandBossBar.ERROR_NO_STYLE_CHANGE.create();
        } else {
            bossbattlecustom.setOverlay(bossbattle_barstyle);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.style.success", new Object[]{bossbattlecustom.getDisplayName()}), true);
            return 0;
        }
    }

    private static int setName(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.updateForEntity(commandlistenerwrapper, ichatbasecomponent, (Entity) null, 0);

        if (bossbattlecustom.getName().equals(ichatmutablecomponent)) {
            throw CommandBossBar.ERROR_NO_NAME_CHANGE.create();
        } else {
            bossbattlecustom.setName(ichatmutablecomponent);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.name.success", new Object[]{bossbattlecustom.getDisplayName()}), true);
            return 0;
        }
    }

    private static int setPlayers(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, Collection<EntityPlayer> collection) throws CommandSyntaxException {
        boolean flag = bossbattlecustom.setPlayers(collection);

        if (!flag) {
            throw CommandBossBar.ERROR_NO_PLAYER_CHANGE.create();
        } else {
            if (bossbattlecustom.getPlayers().isEmpty()) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.players.success.none", new Object[]{bossbattlecustom.getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.set.players.success.some", new Object[]{bossbattlecustom.getDisplayName(), collection.size(), ChatComponentUtils.formatList(collection, EntityHuman::getDisplayName)}), true);
            }

            return bossbattlecustom.getPlayers().size();
        }
    }

    private static int listBars(CommandListenerWrapper commandlistenerwrapper) {
        Collection<BossBattleCustom> collection = commandlistenerwrapper.getServer().getCustomBossEvents().getEvents();

        if (collection.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.list.bars.none"), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.list.bars.some", new Object[]{collection.size(), ChatComponentUtils.formatList(collection, BossBattleCustom::getDisplayName)}), false);
        }

        return collection.size();
    }

    private static int createBar(CommandListenerWrapper commandlistenerwrapper, MinecraftKey minecraftkey, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        BossBattleCustomData bossbattlecustomdata = commandlistenerwrapper.getServer().getCustomBossEvents();

        if (bossbattlecustomdata.get(minecraftkey) != null) {
            throw CommandBossBar.ERROR_ALREADY_EXISTS.create(minecraftkey.toString());
        } else {
            BossBattleCustom bossbattlecustom = bossbattlecustomdata.create(minecraftkey, ChatComponentUtils.updateForEntity(commandlistenerwrapper, ichatbasecomponent, (Entity) null, 0));

            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.create.success", new Object[]{bossbattlecustom.getDisplayName()}), true);
            return bossbattlecustomdata.getEvents().size();
        }
    }

    private static int removeBar(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        BossBattleCustomData bossbattlecustomdata = commandlistenerwrapper.getServer().getCustomBossEvents();

        bossbattlecustom.removeAllPlayers();
        bossbattlecustomdata.remove(bossbattlecustom);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.bossbar.remove.success", new Object[]{bossbattlecustom.getDisplayName()}), true);
        return bossbattlecustomdata.getEvents().size();
    }

    public static BossBattleCustom getBossBar(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
        MinecraftKey minecraftkey = ArgumentMinecraftKeyRegistered.getId(commandcontext, "id");
        BossBattleCustom bossbattlecustom = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getCustomBossEvents().get(minecraftkey);

        if (bossbattlecustom == null) {
            throw CommandBossBar.ERROR_DOESNT_EXIST.create(minecraftkey.toString());
        } else {
            return bossbattlecustom;
        }
    }
}
