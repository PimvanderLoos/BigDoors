package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.packs.repository.ResourcePackLoader;
import net.minecraft.server.packs.repository.ResourcePackRepository;

public class CommandDatapack {

    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.datapack.unknown", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.datapack.enable.failed", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.datapack.disable.failed", new Object[]{object});
    });
    private static final SuggestionProvider<CommandListenerWrapper> SELECTED_PACKS = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.suggest(((CommandListenerWrapper) commandcontext.getSource()).getServer().getPackRepository().getSelectedIds().stream().map(StringArgumentType::escapeIfRequired), suggestionsbuilder);
    };
    private static final SuggestionProvider<CommandListenerWrapper> UNSELECTED_PACKS = (commandcontext, suggestionsbuilder) -> {
        ResourcePackRepository resourcepackrepository = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPackRepository();
        Collection<String> collection = resourcepackrepository.getSelectedIds();

        return ICompletionProvider.suggest(resourcepackrepository.getAvailableIds().stream().filter((s) -> {
            return !collection.contains(s);
        }).map(StringArgumentType::escapeIfRequired), suggestionsbuilder);
    };

    public CommandDatapack() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("datapack").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("enable").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("name", StringArgumentType.string()).suggests(CommandDatapack.UNSELECTED_PACKS).executes((commandcontext) -> {
            return enablePack((CommandListenerWrapper) commandcontext.getSource(), getPack(commandcontext, "name", true), (list, resourcepackloader) -> {
                resourcepackloader.getDefaultPosition().insert(list, resourcepackloader, (resourcepackloader1) -> {
                    return resourcepackloader1;
                }, false);
            });
        })).then(net.minecraft.commands.CommandDispatcher.literal("after").then(net.minecraft.commands.CommandDispatcher.argument("existing", StringArgumentType.string()).suggests(CommandDatapack.SELECTED_PACKS).executes((commandcontext) -> {
            return enablePack((CommandListenerWrapper) commandcontext.getSource(), getPack(commandcontext, "name", true), (list, resourcepackloader) -> {
                list.add(list.indexOf(getPack(commandcontext, "existing", false)) + 1, resourcepackloader);
            });
        })))).then(net.minecraft.commands.CommandDispatcher.literal("before").then(net.minecraft.commands.CommandDispatcher.argument("existing", StringArgumentType.string()).suggests(CommandDatapack.SELECTED_PACKS).executes((commandcontext) -> {
            return enablePack((CommandListenerWrapper) commandcontext.getSource(), getPack(commandcontext, "name", true), (list, resourcepackloader) -> {
                list.add(list.indexOf(getPack(commandcontext, "existing", false)), resourcepackloader);
            });
        })))).then(net.minecraft.commands.CommandDispatcher.literal("last").executes((commandcontext) -> {
            return enablePack((CommandListenerWrapper) commandcontext.getSource(), getPack(commandcontext, "name", true), List::add);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("first").executes((commandcontext) -> {
            return enablePack((CommandListenerWrapper) commandcontext.getSource(), getPack(commandcontext, "name", true), (list, resourcepackloader) -> {
                list.add(0, resourcepackloader);
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("disable").then(net.minecraft.commands.CommandDispatcher.argument("name", StringArgumentType.string()).suggests(CommandDatapack.SELECTED_PACKS).executes((commandcontext) -> {
            return disablePack((CommandListenerWrapper) commandcontext.getSource(), getPack(commandcontext, "name", false));
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("list").executes((commandcontext) -> {
            return listPacks((CommandListenerWrapper) commandcontext.getSource());
        })).then(net.minecraft.commands.CommandDispatcher.literal("available").executes((commandcontext) -> {
            return listAvailablePacks((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.literal("enabled").executes((commandcontext) -> {
            return listEnabledPacks((CommandListenerWrapper) commandcontext.getSource());
        }))));
    }

    private static int enablePack(CommandListenerWrapper commandlistenerwrapper, ResourcePackLoader resourcepackloader, CommandDatapack.a commanddatapack_a) throws CommandSyntaxException {
        ResourcePackRepository resourcepackrepository = commandlistenerwrapper.getServer().getPackRepository();
        List<ResourcePackLoader> list = Lists.newArrayList(resourcepackrepository.getSelectedPacks());

        commanddatapack_a.apply(list, resourcepackloader);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.datapack.modify.enable", new Object[]{resourcepackloader.getChatLink(true)}), true);
        CommandReload.reloadPacks((Collection) list.stream().map(ResourcePackLoader::getId).collect(Collectors.toList()), commandlistenerwrapper);
        return list.size();
    }

    private static int disablePack(CommandListenerWrapper commandlistenerwrapper, ResourcePackLoader resourcepackloader) {
        ResourcePackRepository resourcepackrepository = commandlistenerwrapper.getServer().getPackRepository();
        List<ResourcePackLoader> list = Lists.newArrayList(resourcepackrepository.getSelectedPacks());

        list.remove(resourcepackloader);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.datapack.modify.disable", new Object[]{resourcepackloader.getChatLink(true)}), true);
        CommandReload.reloadPacks((Collection) list.stream().map(ResourcePackLoader::getId).collect(Collectors.toList()), commandlistenerwrapper);
        return list.size();
    }

    private static int listPacks(CommandListenerWrapper commandlistenerwrapper) {
        return listEnabledPacks(commandlistenerwrapper) + listAvailablePacks(commandlistenerwrapper);
    }

    private static int listAvailablePacks(CommandListenerWrapper commandlistenerwrapper) {
        ResourcePackRepository resourcepackrepository = commandlistenerwrapper.getServer().getPackRepository();

        resourcepackrepository.reload();
        Collection<? extends ResourcePackLoader> collection = resourcepackrepository.getSelectedPacks();
        Collection<? extends ResourcePackLoader> collection1 = resourcepackrepository.getAvailablePacks();
        List<ResourcePackLoader> list = (List) collection1.stream().filter((resourcepackloader) -> {
            return !collection.contains(resourcepackloader);
        }).collect(Collectors.toList());

        if (list.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.datapack.list.available.none"), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.datapack.list.available.success", new Object[]{list.size(), ChatComponentUtils.formatList(list, (resourcepackloader) -> {
                        return resourcepackloader.getChatLink(false);
                    })}), false);
        }

        return list.size();
    }

    private static int listEnabledPacks(CommandListenerWrapper commandlistenerwrapper) {
        ResourcePackRepository resourcepackrepository = commandlistenerwrapper.getServer().getPackRepository();

        resourcepackrepository.reload();
        Collection<? extends ResourcePackLoader> collection = resourcepackrepository.getSelectedPacks();

        if (collection.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.datapack.list.enabled.none"), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.datapack.list.enabled.success", new Object[]{collection.size(), ChatComponentUtils.formatList(collection, (resourcepackloader) -> {
                        return resourcepackloader.getChatLink(true);
                    })}), false);
        }

        return collection.size();
    }

    private static ResourcePackLoader getPack(CommandContext<CommandListenerWrapper> commandcontext, String s, boolean flag) throws CommandSyntaxException {
        String s1 = StringArgumentType.getString(commandcontext, s);
        ResourcePackRepository resourcepackrepository = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPackRepository();
        ResourcePackLoader resourcepackloader = resourcepackrepository.getPack(s1);

        if (resourcepackloader == null) {
            throw CommandDatapack.ERROR_UNKNOWN_PACK.create(s1);
        } else {
            boolean flag1 = resourcepackrepository.getSelectedPacks().contains(resourcepackloader);

            if (flag && flag1) {
                throw CommandDatapack.ERROR_PACK_ALREADY_ENABLED.create(s1);
            } else if (!flag && !flag1) {
                throw CommandDatapack.ERROR_PACK_ALREADY_DISABLED.create(s1);
            } else {
                return resourcepackloader;
            }
        }
    }

    private interface a {

        void apply(List<ResourcePackLoader> list, ResourcePackLoader resourcepackloader) throws CommandSyntaxException;
    }
}
