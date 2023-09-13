package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class CommandLocateBiome {

    public static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locatebiome.invalid", new Object[]{object});
    });
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locatebiome.notFound", new Object[]{object});
    });

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("locatebiome").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(CommandDispatcher.a("biome", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CompletionProviders.d).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), (MinecraftKey) commandcontext.getArgument("biome", MinecraftKey.class));
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, MinecraftKey minecraftkey) throws CommandSyntaxException {
        BiomeBase biomebase = (BiomeBase) commandlistenerwrapper.getServer().aX().b(IRegistry.ay).getOptional(minecraftkey).orElseThrow(() -> {
            return CommandLocateBiome.a.create(minecraftkey);
        });
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = commandlistenerwrapper.getWorld().a(biomebase, blockposition, 6400, 8);
        String s = minecraftkey.toString();

        if (blockposition1 == null) {
            throw CommandLocateBiome.b.create(s);
        } else {
            return CommandLocate.a(commandlistenerwrapper, s, blockposition, blockposition1, "commands.locatebiome.success");
        }
    }
}
