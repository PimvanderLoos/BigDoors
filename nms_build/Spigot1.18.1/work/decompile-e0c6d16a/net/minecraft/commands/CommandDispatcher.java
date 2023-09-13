package net.minecraft.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.commands.synchronization.ArgumentRegistry;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.gametest.framework.GameTestHarnessTestCommand;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.game.PacketPlayOutCommands;
import net.minecraft.server.commands.CommandAdvancement;
import net.minecraft.server.commands.CommandAttribute;
import net.minecraft.server.commands.CommandBan;
import net.minecraft.server.commands.CommandBanIp;
import net.minecraft.server.commands.CommandBanList;
import net.minecraft.server.commands.CommandBossBar;
import net.minecraft.server.commands.CommandClear;
import net.minecraft.server.commands.CommandClone;
import net.minecraft.server.commands.CommandDatapack;
import net.minecraft.server.commands.CommandDebug;
import net.minecraft.server.commands.CommandDeop;
import net.minecraft.server.commands.CommandDifficulty;
import net.minecraft.server.commands.CommandEffect;
import net.minecraft.server.commands.CommandEnchant;
import net.minecraft.server.commands.CommandExecute;
import net.minecraft.server.commands.CommandFill;
import net.minecraft.server.commands.CommandForceload;
import net.minecraft.server.commands.CommandFunction;
import net.minecraft.server.commands.CommandGamemode;
import net.minecraft.server.commands.CommandGamemodeDefault;
import net.minecraft.server.commands.CommandGamerule;
import net.minecraft.server.commands.CommandGive;
import net.minecraft.server.commands.CommandHelp;
import net.minecraft.server.commands.CommandIdleTimeout;
import net.minecraft.server.commands.CommandKick;
import net.minecraft.server.commands.CommandKill;
import net.minecraft.server.commands.CommandList;
import net.minecraft.server.commands.CommandLocate;
import net.minecraft.server.commands.CommandLocateBiome;
import net.minecraft.server.commands.CommandLoot;
import net.minecraft.server.commands.CommandMe;
import net.minecraft.server.commands.CommandOp;
import net.minecraft.server.commands.CommandPardon;
import net.minecraft.server.commands.CommandPardonIP;
import net.minecraft.server.commands.CommandParticle;
import net.minecraft.server.commands.CommandPlaySound;
import net.minecraft.server.commands.CommandPublish;
import net.minecraft.server.commands.CommandRecipe;
import net.minecraft.server.commands.CommandReload;
import net.minecraft.server.commands.CommandSaveAll;
import net.minecraft.server.commands.CommandSaveOff;
import net.minecraft.server.commands.CommandSaveOn;
import net.minecraft.server.commands.CommandSay;
import net.minecraft.server.commands.CommandSchedule;
import net.minecraft.server.commands.CommandScoreboard;
import net.minecraft.server.commands.CommandSeed;
import net.minecraft.server.commands.CommandSetBlock;
import net.minecraft.server.commands.CommandSetWorldSpawn;
import net.minecraft.server.commands.CommandSpawnpoint;
import net.minecraft.server.commands.CommandSpectate;
import net.minecraft.server.commands.CommandSpreadPlayers;
import net.minecraft.server.commands.CommandStop;
import net.minecraft.server.commands.CommandStopSound;
import net.minecraft.server.commands.CommandSummon;
import net.minecraft.server.commands.CommandTag;
import net.minecraft.server.commands.CommandTeam;
import net.minecraft.server.commands.CommandTeamMsg;
import net.minecraft.server.commands.CommandTeleport;
import net.minecraft.server.commands.CommandTell;
import net.minecraft.server.commands.CommandTellRaw;
import net.minecraft.server.commands.CommandTime;
import net.minecraft.server.commands.CommandTitle;
import net.minecraft.server.commands.CommandTrigger;
import net.minecraft.server.commands.CommandWeather;
import net.minecraft.server.commands.CommandWhitelist;
import net.minecraft.server.commands.CommandWorldBorder;
import net.minecraft.server.commands.CommandXp;
import net.minecraft.server.commands.ItemCommands;
import net.minecraft.server.commands.JfrCommand;
import net.minecraft.server.commands.PerfCommand;
import net.minecraft.server.commands.data.CommandData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDispatcher {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final int LEVEL_ALL = 0;
    public static final int LEVEL_MODERATORS = 1;
    public static final int LEVEL_GAMEMASTERS = 2;
    public static final int LEVEL_ADMINS = 3;
    public static final int LEVEL_OWNERS = 4;
    private final com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> dispatcher = new com.mojang.brigadier.CommandDispatcher();

    public CommandDispatcher(CommandDispatcher.ServerType commanddispatcher_servertype) {
        CommandAdvancement.register(this.dispatcher);
        CommandAttribute.register(this.dispatcher);
        CommandExecute.register(this.dispatcher);
        CommandBossBar.register(this.dispatcher);
        CommandClear.register(this.dispatcher);
        CommandClone.register(this.dispatcher);
        CommandData.register(this.dispatcher);
        CommandDatapack.register(this.dispatcher);
        CommandDebug.register(this.dispatcher);
        CommandGamemodeDefault.register(this.dispatcher);
        CommandDifficulty.register(this.dispatcher);
        CommandEffect.register(this.dispatcher);
        CommandMe.register(this.dispatcher);
        CommandEnchant.register(this.dispatcher);
        CommandXp.register(this.dispatcher);
        CommandFill.register(this.dispatcher);
        CommandForceload.register(this.dispatcher);
        CommandFunction.register(this.dispatcher);
        CommandGamemode.register(this.dispatcher);
        CommandGamerule.register(this.dispatcher);
        CommandGive.register(this.dispatcher);
        CommandHelp.register(this.dispatcher);
        ItemCommands.register(this.dispatcher);
        CommandKick.register(this.dispatcher);
        CommandKill.register(this.dispatcher);
        CommandList.register(this.dispatcher);
        CommandLocate.register(this.dispatcher);
        CommandLocateBiome.register(this.dispatcher);
        CommandLoot.register(this.dispatcher);
        CommandTell.register(this.dispatcher);
        CommandParticle.register(this.dispatcher);
        CommandPlaySound.register(this.dispatcher);
        CommandReload.register(this.dispatcher);
        CommandRecipe.register(this.dispatcher);
        CommandSay.register(this.dispatcher);
        CommandSchedule.register(this.dispatcher);
        CommandScoreboard.register(this.dispatcher);
        CommandSeed.register(this.dispatcher, commanddispatcher_servertype != CommandDispatcher.ServerType.INTEGRATED);
        CommandSetBlock.register(this.dispatcher);
        CommandSpawnpoint.register(this.dispatcher);
        CommandSetWorldSpawn.register(this.dispatcher);
        CommandSpectate.register(this.dispatcher);
        CommandSpreadPlayers.register(this.dispatcher);
        CommandStopSound.register(this.dispatcher);
        CommandSummon.register(this.dispatcher);
        CommandTag.register(this.dispatcher);
        CommandTeam.register(this.dispatcher);
        CommandTeamMsg.register(this.dispatcher);
        CommandTeleport.register(this.dispatcher);
        CommandTellRaw.register(this.dispatcher);
        CommandTime.register(this.dispatcher);
        CommandTitle.register(this.dispatcher);
        CommandTrigger.register(this.dispatcher);
        CommandWeather.register(this.dispatcher);
        CommandWorldBorder.register(this.dispatcher);
        if (JvmProfiler.INSTANCE.isAvailable()) {
            JfrCommand.register(this.dispatcher);
        }

        if (SharedConstants.IS_RUNNING_IN_IDE) {
            GameTestHarnessTestCommand.register(this.dispatcher);
        }

        if (commanddispatcher_servertype.includeDedicated) {
            CommandBanIp.register(this.dispatcher);
            CommandBanList.register(this.dispatcher);
            CommandBan.register(this.dispatcher);
            CommandDeop.register(this.dispatcher);
            CommandOp.register(this.dispatcher);
            CommandPardon.register(this.dispatcher);
            CommandPardonIP.register(this.dispatcher);
            PerfCommand.register(this.dispatcher);
            CommandSaveAll.register(this.dispatcher);
            CommandSaveOff.register(this.dispatcher);
            CommandSaveOn.register(this.dispatcher);
            CommandIdleTimeout.register(this.dispatcher);
            CommandStop.register(this.dispatcher);
            CommandWhitelist.register(this.dispatcher);
        }

        if (commanddispatcher_servertype.includeIntegrated) {
            CommandPublish.register(this.dispatcher);
        }

        this.dispatcher.findAmbiguities((commandnode, commandnode1, commandnode2, collection) -> {
            CommandDispatcher.LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(commandnode1), this.dispatcher.getPath(commandnode2), collection);
        });
        this.dispatcher.setConsumer((commandcontext, flag, i) -> {
            ((CommandListenerWrapper) commandcontext.getSource()).onCommandComplete(commandcontext, flag, i);
        });
    }

    public int performCommand(CommandListenerWrapper commandlistenerwrapper, String s) {
        StringReader stringreader = new StringReader(s);

        if (stringreader.canRead() && stringreader.peek() == '/') {
            stringreader.skip();
        }

        commandlistenerwrapper.getServer().getProfiler().push(s);

        byte b0;

        try {
            byte b1;

            try {
                int i = this.dispatcher.execute(stringreader, commandlistenerwrapper);

                return i;
            } catch (CommandException commandexception) {
                commandlistenerwrapper.sendFailure(commandexception.getComponent());
                b1 = 0;
                return b1;
            } catch (CommandSyntaxException commandsyntaxexception) {
                commandlistenerwrapper.sendFailure(ChatComponentUtils.fromMessage(commandsyntaxexception.getRawMessage()));
                if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
                    int j = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
                    IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("")).withStyle(EnumChatFormat.GRAY).withStyle((chatmodifier) -> {
                        return chatmodifier.withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, s));
                    });

                    if (j > 10) {
                        ichatmutablecomponent.append("...");
                    }

                    ichatmutablecomponent.append(commandsyntaxexception.getInput().substring(Math.max(0, j - 10), j));
                    if (j < commandsyntaxexception.getInput().length()) {
                        IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText(commandsyntaxexception.getInput().substring(j))).withStyle(new EnumChatFormat[]{EnumChatFormat.RED, EnumChatFormat.UNDERLINE});

                        ichatmutablecomponent.append((IChatBaseComponent) ichatmutablecomponent1);
                    }

                    ichatmutablecomponent.append((IChatBaseComponent) (new ChatMessage("command.context.here")).withStyle(new EnumChatFormat[]{EnumChatFormat.RED, EnumChatFormat.ITALIC}));
                    commandlistenerwrapper.sendFailure(ichatmutablecomponent);
                }

                b1 = 0;
                return b1;
            } catch (Exception exception) {
                ChatComponentText chatcomponenttext = new ChatComponentText(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());

                if (CommandDispatcher.LOGGER.isDebugEnabled()) {
                    CommandDispatcher.LOGGER.error("Command exception: {}", s, exception);
                    StackTraceElement[] astacktraceelement = exception.getStackTrace();

                    for (int k = 0; k < Math.min(astacktraceelement.length, 3); ++k) {
                        chatcomponenttext.append("\n\n").append(astacktraceelement[k].getMethodName()).append("\n ").append(astacktraceelement[k].getFileName()).append(":").append(String.valueOf(astacktraceelement[k].getLineNumber()));
                    }
                }

                commandlistenerwrapper.sendFailure((new ChatMessage("command.failed")).withStyle((chatmodifier) -> {
                    return chatmodifier.withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, chatcomponenttext));
                }));
                if (SharedConstants.IS_RUNNING_IN_IDE) {
                    commandlistenerwrapper.sendFailure(new ChatComponentText(SystemUtils.describeError(exception)));
                    CommandDispatcher.LOGGER.error("'{}' threw an exception", s, exception);
                }

                b0 = 0;
            }
        } finally {
            commandlistenerwrapper.getServer().getProfiler().pop();
        }

        return b0;
    }

    public void sendCommands(EntityPlayer entityplayer) {
        Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map = Maps.newHashMap();
        RootCommandNode<ICompletionProvider> rootcommandnode = new RootCommandNode();

        map.put(this.dispatcher.getRoot(), rootcommandnode);
        this.fillUsableCommands(this.dispatcher.getRoot(), rootcommandnode, entityplayer.createCommandSourceStack(), map);
        entityplayer.connection.send(new PacketPlayOutCommands(rootcommandnode));
    }

    private void fillUsableCommands(CommandNode<CommandListenerWrapper> commandnode, CommandNode<ICompletionProvider> commandnode1, CommandListenerWrapper commandlistenerwrapper, Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map) {
        Iterator iterator = commandnode.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode<CommandListenerWrapper> commandnode2 = (CommandNode) iterator.next();

            if (commandnode2.canUse(commandlistenerwrapper)) {
                ArgumentBuilder<ICompletionProvider, ?> argumentbuilder = commandnode2.createBuilder();

                argumentbuilder.requires((icompletionprovider) -> {
                    return true;
                });
                if (argumentbuilder.getCommand() != null) {
                    argumentbuilder.executes((commandcontext) -> {
                        return 0;
                    });
                }

                if (argumentbuilder instanceof RequiredArgumentBuilder) {
                    RequiredArgumentBuilder<ICompletionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder) argumentbuilder;

                    if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                        requiredargumentbuilder.suggests(CompletionProviders.safelySwap(requiredargumentbuilder.getSuggestionsProvider()));
                    }
                }

                if (argumentbuilder.getRedirect() != null) {
                    argumentbuilder.redirect((CommandNode) map.get(argumentbuilder.getRedirect()));
                }

                CommandNode<ICompletionProvider> commandnode3 = argumentbuilder.build();

                map.put(commandnode2, commandnode3);
                commandnode1.addChild(commandnode3);
                if (!commandnode2.getChildren().isEmpty()) {
                    this.fillUsableCommands(commandnode2, commandnode3, commandlistenerwrapper, map);
                }
            }
        }

    }

    public static LiteralArgumentBuilder<CommandListenerWrapper> literal(String s) {
        return LiteralArgumentBuilder.literal(s);
    }

    public static <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String s, ArgumentType<T> argumenttype) {
        return RequiredArgumentBuilder.argument(s, argumenttype);
    }

    public static Predicate<String> createValidator(CommandDispatcher.b commanddispatcher_b) {
        return (s) -> {
            try {
                commanddispatcher_b.parse(new StringReader(s));
                return true;
            } catch (CommandSyntaxException commandsyntaxexception) {
                return false;
            }
        };
    }

    public com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> getDispatcher() {
        return this.dispatcher;
    }

    @Nullable
    public static <S> CommandSyntaxException getParseException(ParseResults<S> parseresults) {
        return !parseresults.getReader().canRead() ? null : (parseresults.getExceptions().size() == 1 ? (CommandSyntaxException) parseresults.getExceptions().values().iterator().next() : (parseresults.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseresults.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseresults.getReader())));
    }

    public static void validate() {
        RootCommandNode<CommandListenerWrapper> rootcommandnode = (new CommandDispatcher(CommandDispatcher.ServerType.ALL)).getDispatcher().getRoot();
        Set<ArgumentType<?>> set = ArgumentRegistry.findUsedArgumentTypes(rootcommandnode);
        Set<ArgumentType<?>> set1 = (Set) set.stream().filter((argumenttype) -> {
            return !ArgumentRegistry.isTypeRegistered(argumenttype);
        }).collect(Collectors.toSet());

        if (!set1.isEmpty()) {
            CommandDispatcher.LOGGER.warn("Missing type registration for following arguments:\n {}", set1.stream().map((argumenttype) -> {
                return "\t" + argumenttype;
            }).collect(Collectors.joining(",\n")));
            throw new IllegalStateException("Unregistered argument types");
        }
    }

    public static enum ServerType {

        ALL(true, true), DEDICATED(false, true), INTEGRATED(true, false);

        final boolean includeIntegrated;
        final boolean includeDedicated;

        private ServerType(boolean flag, boolean flag1) {
            this.includeIntegrated = flag;
            this.includeDedicated = flag1;
        }
    }

    @FunctionalInterface
    public interface b {

        void parse(StringReader stringreader) throws CommandSyntaxException;
    }
}
