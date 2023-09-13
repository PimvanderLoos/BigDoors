package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.ResourcePackRepository;
import net.minecraft.world.level.storage.SaveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandReload {

    private static final Logger LOGGER = LogManager.getLogger();

    public CommandReload() {}

    public static void reloadPacks(Collection<String> collection, CommandListenerWrapper commandlistenerwrapper) {
        commandlistenerwrapper.getServer().reloadResources(collection).exceptionally((throwable) -> {
            CommandReload.LOGGER.warn("Failed to execute reload", throwable);
            commandlistenerwrapper.sendFailure(new ChatMessage("commands.reload.failure"));
            return null;
        });
    }

    private static Collection<String> discoverNewPacks(ResourcePackRepository resourcepackrepository, SaveData savedata, Collection<String> collection) {
        resourcepackrepository.reload();
        Collection<String> collection1 = Lists.newArrayList(collection);
        Collection<String> collection2 = savedata.getDataPackConfig().getDisabled();
        Iterator iterator = resourcepackrepository.getAvailableIds().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (!collection2.contains(s) && !collection1.contains(s)) {
                collection1.add(s);
            }
        }

        return collection1;
    }

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("reload").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
            MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
            ResourcePackRepository resourcepackrepository = minecraftserver.getPackRepository();
            SaveData savedata = minecraftserver.getWorldData();
            Collection<String> collection = resourcepackrepository.getSelectedIds();
            Collection<String> collection1 = discoverNewPacks(resourcepackrepository, savedata, collection);

            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.reload.success"), true);
            reloadPacks(collection1, commandlistenerwrapper);
            return 0;
        }));
    }
}
