package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.level.GameRules;

public class CommandGamerule {

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        final LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = (LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("gamerule").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        });

        GameRules.a(new GameRules.GameRuleVisitor() {
            @Override
            public <T extends GameRules.GameRuleValue<T>> void a(GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
                literalargumentbuilder.then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a(gamerules_gamerulekey.a()).executes((commandcontext) -> {
                    return CommandGamerule.b((CommandListenerWrapper) commandcontext.getSource(), gamerules_gamerulekey);
                })).then(gamerules_gameruledefinition.a("value").executes((commandcontext) -> {
                    return CommandGamerule.b(commandcontext, gamerules_gamerulekey);
                })));
            }
        });
        commanddispatcher.register(literalargumentbuilder);
    }

    private static <T extends GameRules.GameRuleValue<T>> int b(CommandContext<CommandListenerWrapper> commandcontext, GameRules.GameRuleKey<T> gamerules_gamerulekey) {
        CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
        T t0 = commandlistenerwrapper.getServer().getGameRules().get(gamerules_gamerulekey);

        t0.b(commandcontext, "value");
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.gamerule.set", new Object[]{gamerules_gamerulekey.a(), t0.toString()}), true);
        return t0.getIntValue();
    }

    private static <T extends GameRules.GameRuleValue<T>> int b(CommandListenerWrapper commandlistenerwrapper, GameRules.GameRuleKey<T> gamerules_gamerulekey) {
        T t0 = commandlistenerwrapper.getServer().getGameRules().get(gamerules_gamerulekey);

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.gamerule.query", new Object[]{gamerules_gamerulekey.a(), t0.toString()}), false);
        return t0.getIntValue();
    }
}
