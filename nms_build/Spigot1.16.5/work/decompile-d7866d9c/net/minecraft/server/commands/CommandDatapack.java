package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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

    private static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.datapack.unknown", new Object[]{object});
    });
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.datapack.enable.failed", new Object[]{object});
    });
    private static final DynamicCommandExceptionType c = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.datapack.disable.failed", new Object[]{object});
    });
    private static final SuggestionProvider<CommandListenerWrapper> d = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.b(((CommandListenerWrapper) commandcontext.getSource()).getServer().getResourcePackRepository().d().stream().map(StringArgumentType::escapeIfRequired), suggestionsbuilder);
    };
    private static final SuggestionProvider<CommandListenerWrapper> e = (commandcontext, suggestionsbuilder) -> {
        ResourcePackRepository resourcepackrepository = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getResourcePackRepository();
        Collection<String> collection = resourcepackrepository.d();

        return ICompletionProvider.b(resourcepackrepository.b().stream().filter((s) -> {
            return !collection.contains(s);
        }).map(StringArgumentType::escapeIfRequired), suggestionsbuilder);
    };

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("datapack").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("enable").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("name", (ArgumentType) StringArgumentType.string()).suggests(CommandDatapack.e).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext, "name", true), (list, resourcepackloader) -> {
                resourcepackloader.h().a(list, resourcepackloader, (resourcepackloader1) -> {
                    return resourcepackloader1;
                }, false);
            });
        })).then(net.minecraft.commands.CommandDispatcher.a("after").then(net.minecraft.commands.CommandDispatcher.a("existing", (ArgumentType) StringArgumentType.string()).suggests(CommandDatapack.d).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext, "name", true), (list, resourcepackloader) -> {
                list.add(list.indexOf(a(commandcontext, "existing", false)) + 1, resourcepackloader);
            });
        })))).then(net.minecraft.commands.CommandDispatcher.a("before").then(net.minecraft.commands.CommandDispatcher.a("existing", (ArgumentType) StringArgumentType.string()).suggests(CommandDatapack.d).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext, "name", true), (list, resourcepackloader) -> {
                list.add(list.indexOf(a(commandcontext, "existing", false)), resourcepackloader);
            });
        })))).then(net.minecraft.commands.CommandDispatcher.a("last").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext, "name", true), List::add);
        }))).then(net.minecraft.commands.CommandDispatcher.a("first").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext, "name", true), (list, resourcepackloader) -> {
                list.add(0, resourcepackloader);
            });
        }))))).then(net.minecraft.commands.CommandDispatcher.a("disable").then(net.minecraft.commands.CommandDispatcher.a("name", (ArgumentType) StringArgumentType.string()).suggests(CommandDatapack.d).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), a(commandcontext, "name", false));
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("list").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        })).then(net.minecraft.commands.CommandDispatcher.a("available").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("enabled").executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource());
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, ResourcePackLoader resourcepackloader, CommandDatapack.a commanddatapack_a) throws CommandSyntaxException {
        ResourcePackRepository resourcepackrepository = commandlistenerwrapper.getServer().getResourcePackRepository();
        List<ResourcePackLoader> list = Lists.newArrayList(resourcepackrepository.e());

        commanddatapack_a.apply(list, resourcepackloader);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.datapack.modify.enable", new Object[]{resourcepackloader.a(true)}), true);
        CommandReload.a((Collection) list.stream().map(ResourcePackLoader::e).collect(Collectors.toList()), commandlistenerwrapper);
        return list.size();
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, ResourcePackLoader resourcepackloader) {
        ResourcePackRepository resourcepackrepository = commandlistenerwrapper.getServer().getResourcePackRepository();
        List<ResourcePackLoader> list = Lists.newArrayList(resourcepackrepository.e());

        list.remove(resourcepackloader);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.datapack.modify.disable", new Object[]{resourcepackloader.a(true)}), true);
        CommandReload.a((Collection) list.stream().map(ResourcePackLoader::e).collect(Collectors.toList()), commandlistenerwrapper);
        return list.size();
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) {
        return c(commandlistenerwrapper) + b(commandlistenerwrapper);
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper) {
        ResourcePackRepository resourcepackrepository = commandlistenerwrapper.getServer().getResourcePackRepository();

        resourcepackrepository.a();
        Collection<? extends ResourcePackLoader> collection = resourcepackrepository.e();
        Collection<? extends ResourcePackLoader> collection1 = resourcepackrepository.c();
        List<ResourcePackLoader> list = (List) collection1.stream().filter((resourcepackloader) -> {
            return !collection.contains(resourcepackloader);
        }).collect(Collectors.toList());

        if (list.isEmpty()) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.datapack.list.available.none"), false);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.datapack.list.available.success", new Object[]{list.size(), ChatComponentUtils.b(list, (resourcepackloader) -> {
                        return resourcepackloader.a(false);
                    })}), false);
        }

        return list.size();
    }

    private static int c(CommandListenerWrapper commandlistenerwrapper) {
        ResourcePackRepository resourcepackrepository = commandlistenerwrapper.getServer().getResourcePackRepository();

        resourcepackrepository.a();
        Collection<? extends ResourcePackLoader> collection = resourcepackrepository.e();

        if (collection.isEmpty()) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.datapack.list.enabled.none"), false);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.datapack.list.enabled.success", new Object[]{collection.size(), ChatComponentUtils.b(collection, (resourcepackloader) -> {
                        return resourcepackloader.a(true);
                    })}), false);
        }

        return collection.size();
    }

    private static ResourcePackLoader a(CommandContext<CommandListenerWrapper> commandcontext, String s, boolean flag) throws CommandSyntaxException {
        String s1 = StringArgumentType.getString(commandcontext, s);
        ResourcePackRepository resourcepackrepository = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getResourcePackRepository();
        ResourcePackLoader resourcepackloader = resourcepackrepository.a(s1);

        if (resourcepackloader == null) {
            throw CommandDatapack.a.create(s1);
        } else {
            boolean flag1 = resourcepackrepository.e().contains(resourcepackloader);

            if (flag && flag1) {
                throw CommandDatapack.b.create(s1);
            } else if (!flag && !flag1) {
                throw CommandDatapack.c.create(s1);
            } else {
                return resourcepackloader;
            }
        }
    }

    interface a {

        void apply(List<ResourcePackLoader> list, ResourcePackLoader resourcepackloader) throws CommandSyntaxException;
    }
}
