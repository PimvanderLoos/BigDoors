package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CustomFunction;
import net.minecraft.commands.ICommandListener;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.packs.resources.IReloadListener;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagDataPack;
import net.minecraft.tags.Tags;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomFunctionManager implements IReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FILE_EXTENSION = ".mcfunction";
    private static final int PATH_PREFIX_LENGTH = "functions/".length();
    private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
    private volatile Map<MinecraftKey, CustomFunction> functions = ImmutableMap.of();
    private final TagDataPack<CustomFunction> tagsLoader = new TagDataPack<>(this::a, "tags/functions");
    private volatile Tags<CustomFunction> tags = Tags.c();
    private final int functionCompilationLevel;
    private final CommandDispatcher<CommandListenerWrapper> dispatcher;

    public Optional<CustomFunction> a(MinecraftKey minecraftkey) {
        return Optional.ofNullable((CustomFunction) this.functions.get(minecraftkey));
    }

    public Map<MinecraftKey, CustomFunction> a() {
        return this.functions;
    }

    public Tags<CustomFunction> b() {
        return this.tags;
    }

    public Tag<CustomFunction> b(MinecraftKey minecraftkey) {
        return this.tags.b(minecraftkey);
    }

    public CustomFunctionManager(int i, CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        this.functionCompilationLevel = i;
        this.dispatcher = commanddispatcher;
    }

    @Override
    public CompletableFuture<Void> a(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller, GameProfilerFiller gameprofilerfiller1, Executor executor, Executor executor1) {
        CompletableFuture<Map<MinecraftKey, Tag.a>> completablefuture = CompletableFuture.supplyAsync(() -> {
            return this.tagsLoader.a(iresourcemanager);
        }, executor);
        CompletableFuture<Map<MinecraftKey, CompletableFuture<CustomFunction>>> completablefuture1 = CompletableFuture.supplyAsync(() -> {
            return iresourcemanager.a("functions", (s) -> {
                return s.endsWith(".mcfunction");
            });
        }, executor).thenCompose((collection) -> {
            Map<MinecraftKey, CompletableFuture<CustomFunction>> map = Maps.newHashMap();
            CommandListenerWrapper commandlistenerwrapper = new CommandListenerWrapper(ICommandListener.NULL, Vec3D.ZERO, Vec2F.ZERO, (WorldServer) null, this.functionCompilationLevel, "", ChatComponentText.EMPTY, (MinecraftServer) null, (Entity) null);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
                String s = minecraftkey.getKey();
                MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), s.substring(CustomFunctionManager.PATH_PREFIX_LENGTH, s.length() - CustomFunctionManager.PATH_SUFFIX_LENGTH));

                map.put(minecraftkey1, CompletableFuture.supplyAsync(() -> {
                    List<String> list = a(iresourcemanager, minecraftkey);

                    return CustomFunction.a(minecraftkey1, this.dispatcher, commandlistenerwrapper, list);
                }, executor));
            }

            CompletableFuture<?>[] acompletablefuture = (CompletableFuture[]) map.values().toArray(new CompletableFuture[0]);

            return CompletableFuture.allOf(acompletablefuture).handle((ovoid, throwable) -> {
                return map;
            });
        });
        CompletableFuture completablefuture2 = completablefuture.thenCombine(completablefuture1, Pair::of);

        Objects.requireNonNull(ireloadlistener_a);
        return completablefuture2.thenCompose(ireloadlistener_a::a).thenAcceptAsync((pair) -> {
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
            this.tags = this.tagsLoader.a((Map) pair.getFirst());
        }, executor1);
    }

    private static List<String> a(IResourceManager iresourcemanager, MinecraftKey minecraftkey) {
        try {
            IResource iresource = iresourcemanager.a(minecraftkey);

            List list;

            try {
                list = IOUtils.readLines(iresource.b(), StandardCharsets.UTF_8);
            } catch (Throwable throwable) {
                if (iresource != null) {
                    try {
                        iresource.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (iresource != null) {
                iresource.close();
            }

            return list;
        } catch (IOException ioexception) {
            throw new CompletionException(ioexception);
        }
    }
}
