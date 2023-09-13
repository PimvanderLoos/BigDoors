package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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

    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.bossbar.create.failed", new Object[]{object});
    });
    private static final DynamicCommandExceptionType c = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.bossbar.unknown", new Object[]{object});
    });
    private static final SimpleCommandExceptionType d = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.players.unchanged"));
    private static final SimpleCommandExceptionType e = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.name.unchanged"));
    private static final SimpleCommandExceptionType f = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.color.unchanged"));
    private static final SimpleCommandExceptionType g = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.style.unchanged"));
    private static final SimpleCommandExceptionType h = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.value.unchanged"));
    private static final SimpleCommandExceptionType i = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.max.unchanged"));
    private static final SimpleCommandExceptionType j = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.visibility.unchanged.hidden"));
    private static final SimpleCommandExceptionType k = new SimpleCommandExceptionType(new ChatMessage("commands.bossbar.set.visibility.unchanged.visible"));
    public static final SuggestionProvider<CommandListenerWrapper> a = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a((Iterable) ((CommandListenerWrapper) commandcontext.getSource()).getServer().getBossBattleCustomData().a(), suggestionsbuilder);
    };

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("bossbar").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("add").then(net.minecraft.commands.CommandDispatcher.a("id", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).then(net.minecraft.commands.CommandDispatcher.a("name", (ArgumentType) ArgumentChatComponent.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.e(commandcontext, "id"), ArgumentChatComponent.a(commandcontext, "name"));
        }))))).then(net.minecraft.commands.CommandDispatcher.a("remove").then(net.minecraft.commands.CommandDispatcher.a("id", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandBossBar.a).executes((commandcontext) -> {
            return e((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext));
        })))).then(net.minecraft.commands.CommandDispatcher.a("list").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("set").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("id", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandBossBar.a).then(net.minecraft.commands.CommandDispatcher.a("name").then(net.minecraft.commands.CommandDispatcher.a("name", (ArgumentType) ArgumentChatComponent.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), ArgumentChatComponent.a(commandcontext, "name"));
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("color").then(net.minecraft.commands.CommandDispatcher.a("pink").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarColor.PINK);
        }))).then(net.minecraft.commands.CommandDispatcher.a("blue").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarColor.BLUE);
        }))).then(net.minecraft.commands.CommandDispatcher.a("red").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarColor.RED);
        }))).then(net.minecraft.commands.CommandDispatcher.a("green").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarColor.GREEN);
        }))).then(net.minecraft.commands.CommandDispatcher.a("yellow").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarColor.YELLOW);
        }))).then(net.minecraft.commands.CommandDispatcher.a("purple").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarColor.PURPLE);
        }))).then(net.minecraft.commands.CommandDispatcher.a("white").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarColor.WHITE);
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("style").then(net.minecraft.commands.CommandDispatcher.a("progress").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarStyle.PROGRESS);
        }))).then(net.minecraft.commands.CommandDispatcher.a("notched_6").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarStyle.NOTCHED_6);
        }))).then(net.minecraft.commands.CommandDispatcher.a("notched_10").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarStyle.NOTCHED_10);
        }))).then(net.minecraft.commands.CommandDispatcher.a("notched_12").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarStyle.NOTCHED_12);
        }))).then(net.minecraft.commands.CommandDispatcher.a("notched_20").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BossBattle.BarStyle.NOTCHED_20);
        })))).then(net.minecraft.commands.CommandDispatcher.a("value").then(net.minecraft.commands.CommandDispatcher.a("value", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), IntegerArgumentType.getInteger(commandcontext, "value"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("max").then(net.minecraft.commands.CommandDispatcher.a("max", (ArgumentType) IntegerArgumentType.integer(1)).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), IntegerArgumentType.getInteger(commandcontext, "max"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("visible").then(net.minecraft.commands.CommandDispatcher.a("visible", (ArgumentType) BoolArgumentType.bool()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), BoolArgumentType.getBool(commandcontext, "visible"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("players").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), (Collection) Collections.emptyList());
        })).then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext), ArgumentEntity.d(commandcontext, "targets"));
        })))))).then(net.minecraft.commands.CommandDispatcher.a("get").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("id", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandBossBar.a).then(net.minecraft.commands.CommandDispatcher.a("value").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext));
        }))).then(net.minecraft.commands.CommandDispatcher.a("max").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext));
        }))).then(net.minecraft.commands.CommandDispatcher.a("visible").executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext));
        }))).then(net.minecraft.commands.CommandDispatcher.a("players").executes((commandcontext) -> {
            return d((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext));
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.get.value", new Object[]{bossbattlecustom.e(), bossbattlecustom.c()}), true);
        return bossbattlecustom.c();
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.get.max", new Object[]{bossbattlecustom.e(), bossbattlecustom.d()}), true);
        return bossbattlecustom.d();
    }

    private static int c(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        if (bossbattlecustom.g()) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.get.visible.visible", new Object[]{bossbattlecustom.e()}), true);
            return 1;
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.get.visible.hidden", new Object[]{bossbattlecustom.e()}), true);
            return 0;
        }
    }

    private static int d(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        if (bossbattlecustom.getPlayers().isEmpty()) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.get.players.none", new Object[]{bossbattlecustom.e()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.get.players.some", new Object[]{bossbattlecustom.e(), bossbattlecustom.getPlayers().size(), ChatComponentUtils.b(bossbattlecustom.getPlayers(), EntityHuman::getScoreboardDisplayName)}), true);
        }

        return bossbattlecustom.getPlayers().size();
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, boolean flag) throws CommandSyntaxException {
        if (bossbattlecustom.g() == flag) {
            if (flag) {
                throw CommandBossBar.k.create();
            } else {
                throw CommandBossBar.j.create();
            }
        } else {
            bossbattlecustom.setVisible(flag);
            if (flag) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.visible.success.visible", new Object[]{bossbattlecustom.e()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.visible.success.hidden", new Object[]{bossbattlecustom.e()}), true);
            }

            return 0;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, int i) throws CommandSyntaxException {
        if (bossbattlecustom.c() == i) {
            throw CommandBossBar.h.create();
        } else {
            bossbattlecustom.a(i);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.value.success", new Object[]{bossbattlecustom.e(), i}), true);
            return i;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, int i) throws CommandSyntaxException {
        if (bossbattlecustom.d() == i) {
            throw CommandBossBar.i.create();
        } else {
            bossbattlecustom.b(i);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.max.success", new Object[]{bossbattlecustom.e(), i}), true);
            return i;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, BossBattle.BarColor bossbattle_barcolor) throws CommandSyntaxException {
        if (bossbattlecustom.l().equals(bossbattle_barcolor)) {
            throw CommandBossBar.f.create();
        } else {
            bossbattlecustom.a(bossbattle_barcolor);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.color.success", new Object[]{bossbattlecustom.e()}), true);
            return 0;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, BossBattle.BarStyle bossbattle_barstyle) throws CommandSyntaxException {
        if (bossbattlecustom.m().equals(bossbattle_barstyle)) {
            throw CommandBossBar.g.create();
        } else {
            bossbattlecustom.a(bossbattle_barstyle);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.style.success", new Object[]{bossbattlecustom.e()}), true);
            return 0;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.filterForDisplay(commandlistenerwrapper, ichatbasecomponent, (Entity) null, 0);

        if (bossbattlecustom.j().equals(ichatmutablecomponent)) {
            throw CommandBossBar.e.create();
        } else {
            bossbattlecustom.a((IChatBaseComponent) ichatmutablecomponent);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.name.success", new Object[]{bossbattlecustom.e()}), true);
            return 0;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom, Collection<EntityPlayer> collection) throws CommandSyntaxException {
        boolean flag = bossbattlecustom.a(collection);

        if (!flag) {
            throw CommandBossBar.d.create();
        } else {
            if (bossbattlecustom.getPlayers().isEmpty()) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.players.success.none", new Object[]{bossbattlecustom.e()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.set.players.success.some", new Object[]{bossbattlecustom.e(), collection.size(), ChatComponentUtils.b(collection, EntityHuman::getScoreboardDisplayName)}), true);
            }

            return bossbattlecustom.getPlayers().size();
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) {
        Collection<BossBattleCustom> collection = commandlistenerwrapper.getServer().getBossBattleCustomData().getBattles();

        if (collection.isEmpty()) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.list.bars.none"), false);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.list.bars.some", new Object[]{collection.size(), ChatComponentUtils.b(collection, BossBattleCustom::e)}), false);
        }

        return collection.size();
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, MinecraftKey minecraftkey, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        BossBattleCustomData bossbattlecustomdata = commandlistenerwrapper.getServer().getBossBattleCustomData();

        if (bossbattlecustomdata.a(minecraftkey) != null) {
            throw CommandBossBar.b.create(minecraftkey.toString());
        } else {
            BossBattleCustom bossbattlecustom = bossbattlecustomdata.register(minecraftkey, ChatComponentUtils.filterForDisplay(commandlistenerwrapper, ichatbasecomponent, (Entity) null, 0));

            commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.create.success", new Object[]{bossbattlecustom.e()}), true);
            return bossbattlecustomdata.getBattles().size();
        }
    }

    private static int e(CommandListenerWrapper commandlistenerwrapper, BossBattleCustom bossbattlecustom) {
        BossBattleCustomData bossbattlecustomdata = commandlistenerwrapper.getServer().getBossBattleCustomData();

        bossbattlecustom.b();
        bossbattlecustomdata.remove(bossbattlecustom);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.bossbar.remove.success", new Object[]{bossbattlecustom.e()}), true);
        return bossbattlecustomdata.getBattles().size();
    }

    public static BossBattleCustom a(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
        MinecraftKey minecraftkey = ArgumentMinecraftKeyRegistered.e(commandcontext, "id");
        BossBattleCustom bossbattlecustom = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getBossBattleCustomData().a(minecraftkey);

        if (bossbattlecustom == null) {
            throw CommandBossBar.c.create(minecraftkey.toString());
        } else {
            return bossbattlecustom;
        }
    }
}
