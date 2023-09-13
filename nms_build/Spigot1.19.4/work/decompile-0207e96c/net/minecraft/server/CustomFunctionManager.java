package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CustomFunction;
import net.minecraft.commands.ICommandListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.packs.resources.IReloadListener;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.tags.TagDataPack;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;
import org.slf4j.Logger;

public class CustomFunctionManager implements IReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = new FileToIdConverter("functions", ".mcfunction");
    private volatile Map<MinecraftKey, CustomFunction> functions = ImmutableMap.of();
    private final TagDataPack<CustomFunction> tagsLoader = new TagDataPack<>(this::getFunction, "tags/functions");
    private volatile Map<MinecraftKey, Collection<CustomFunction>> tags = Map.of();
    private final int functionCompilationLevel;
    private final CommandDispatcher<CommandListenerWrapper> dispatcher;

    public Optional<CustomFunction> getFunction(MinecraftKey minecraftkey) {
        return Optional.ofNullable((CustomFunction) this.functions.get(minecraftkey));
    }

    public Map<MinecraftKey, CustomFunction> getFunctions() {
        return this.functions;
    }

    public Collection<CustomFunction> getTag(MinecraftKey minecraftkey) {
        return (Collection) this.tags.getOrDefault(minecraftkey, List.of());
    }

    public Iterable<MinecraftKey> getAvailableTags() {
        return this.tags.keySet();
    }

    public CustomFunctionManager(int i, CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        this.functionCompilationLevel = i;
        this.dispatcher = commanddispatcher;
    }

    @Override
    public CompletableFuture<Void> reload(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller, GameProfilerFiller gameprofilerfiller1, Executor executor, Executor executor1) {
        CompletableFuture<Map<MinecraftKey, List<TagDataPack.a>>> completablefuture = CompletableFuture.supplyAsync(() -> {
            return this.tagsLoader.load(iresourcemanager);
        }, executor);
        CompletableFuture<Map<MinecraftKey, CompletableFuture<CustomFunction>>> completablefuture1 = CompletableFuture.supplyAsync(() -> {
            return CustomFunctionManager.LISTER.listMatchingResources(iresourcemanager);
        }, executor).thenCompose((map) -> {
            Map<MinecraftKey, CompletableFuture<CustomFunction>> map1 = Maps.newHashMap();
            CommandListenerWrapper commandlistenerwrapper = new CommandListenerWrapper(ICommandListener.NULL, Vec3D.ZERO, Vec2F.ZERO, (WorldServer) null, this.functionCompilationLevel, "", CommonComponents.EMPTY, (MinecraftServer) null, (Entity) null);
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<MinecraftKey, IResource> entry = (Entry) iterator.next();
                MinecraftKey minecraftkey = (MinecraftKey) entry.getKey();
                MinecraftKey minecraftkey1 = CustomFunctionManager.LISTER.fileToId(minecraftkey);

                map1.put(minecraftkey1, CompletableFuture.supplyAsync(() -> {
                    List<String> list = readLines((IResource) entry.getValue());

                    return CustomFunction.fromLines(minecraftkey1, this.dispatcher, commandlistenerwrapper, list);
                }, executor));
            }

            CompletableFuture<?>[] acompletablefuture = (CompletableFuture[]) map1.values().toArray(new CompletableFuture[0]);

            return CompletableFuture.allOf(acompletablefuture).handle((ovoid, throwable) -> {
                return map1;
            });
        });
        CompletableFuture completablefuture2 = completablefuture.thenCombine(completablefuture1, Pair::of);

        Objects.requireNonNull(ireloadlistener_a);
        return completablefuture2.thenCompose(ireloadlistener_a::wait).thenAcceptAsync((pair) -> {
            Map<MinecraftKey, CompletableFuture<CustomFunction>> map = (Map) pair.getSecond();
            Builder<MinecraftKey, CustomFunction> builder = ImmutableMap.builder();

            map.forEach((minecraftkey, completablefuture3) -> {
                completablefuture3.handle((customfunction, throwable) -> {
                    if (throwable != null) {
                        CustomFunctionManager.LOGGER.error("Failed to load function {}", minecraftkey, throwable);
                    } else {
                        builder.put(minecraftkey, customfunction);
                    }

                    return null;
                }).join();
            });
            this.functions = builder.build();
            this.tags = this.tagsLoader.build((Map) pair.getFirst());
        }, executor1);
    }

    private static List<String> readLines(IResource iresource) {
        try {
            BufferedReader bufferedreader = iresource.openAsReader();

            List list;

            try {
                list = bufferedreader.lines().toList();
            } catch (Throwable throwable) {
                if (bufferedreader != null) {
                    try {
                        bufferedreader.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (bufferedreader != null) {
                bufferedreader.close();
            }

            return list;
        } catch (IOException ioexception) {
            throw new CompletionException(ioexception);
        }
    }
}
