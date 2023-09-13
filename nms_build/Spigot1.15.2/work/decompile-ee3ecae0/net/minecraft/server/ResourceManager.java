package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceManager implements IReloadableResourceManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, ResourceManagerFallback> b = Maps.newHashMap();
    private final List<IReloadListener> c = Lists.newArrayList();
    private final List<IReloadListener> d = Lists.newArrayList();
    private final Set<String> e = Sets.newLinkedHashSet();
    private final EnumResourcePackType f;
    private final Thread g;

    public ResourceManager(EnumResourcePackType enumresourcepacktype, Thread thread) {
        this.f = enumresourcepacktype;
        this.g = thread;
    }

    public void a(IResourcePack iresourcepack) {
        ResourceManagerFallback resourcemanagerfallback;

        for (Iterator iterator = iresourcepack.a(this.f).iterator(); iterator.hasNext(); resourcemanagerfallback.a(iresourcepack)) {
            String s = (String) iterator.next();

            this.e.add(s);
            resourcemanagerfallback = (ResourceManagerFallback) this.b.get(s);
            if (resourcemanagerfallback == null) {
                resourcemanagerfallback = new ResourceManagerFallback(this.f, s);
                this.b.put(s, resourcemanagerfallback);
            }
        }

    }

    @Override
    public IResource a(MinecraftKey minecraftkey) throws IOException {
        IResourceManager iresourcemanager = (IResourceManager) this.b.get(minecraftkey.getNamespace());

        if (iresourcemanager != null) {
            return iresourcemanager.a(minecraftkey);
        } else {
            throw new FileNotFoundException(minecraftkey.toString());
        }
    }

    @Override
    public List<IResource> c(MinecraftKey minecraftkey) throws IOException {
        IResourceManager iresourcemanager = (IResourceManager) this.b.get(minecraftkey.getNamespace());

        if (iresourcemanager != null) {
            return iresourcemanager.c(minecraftkey);
        } else {
            throw new FileNotFoundException(minecraftkey.toString());
        }
    }

    @Override
    public Collection<MinecraftKey> a(String s, Predicate<String> predicate) {
        Set<MinecraftKey> set = Sets.newHashSet();
        Iterator iterator = this.b.values().iterator();

        while (iterator.hasNext()) {
            ResourceManagerFallback resourcemanagerfallback = (ResourceManagerFallback) iterator.next();

            set.addAll(resourcemanagerfallback.a(s, predicate));
        }

        List<MinecraftKey> list = Lists.newArrayList(set);

        Collections.sort(list);
        return list;
    }

    private void b() {
        this.b.clear();
        this.e.clear();
    }

    @Override
    public CompletableFuture<Unit> a(Executor executor, Executor executor1, List<IResourcePack> list, CompletableFuture<Unit> completablefuture) {
        IReloadable ireloadable = this.a(executor, executor1, completablefuture, list);

        return ireloadable.a();
    }

    @Override
    public void a(IReloadListener ireloadlistener) {
        this.c.add(ireloadlistener);
        this.d.add(ireloadlistener);
    }

    protected IReloadable b(Executor executor, Executor executor1, List<IReloadListener> list, CompletableFuture<Unit> completablefuture) {
        Object object;

        if (ResourceManager.LOGGER.isDebugEnabled()) {
            object = new ReloadableProfiled(this, Lists.newArrayList(list), executor, executor1, completablefuture);
        } else {
            object = Reloadable.a(this, Lists.newArrayList(list), executor, executor1, completablefuture);
        }

        this.d.clear();
        return (IReloadable) object;
    }

    public IReloadable a(Executor executor, Executor executor1, CompletableFuture<Unit> completablefuture, List<IResourcePack> list) {
        this.b();
        ResourceManager.LOGGER.info("Reloading ResourceManager: {}", list.stream().map(IResourcePack::a).collect(Collectors.joining(", ")));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            IResourcePack iresourcepack = (IResourcePack) iterator.next();

            try {
                this.a(iresourcepack);
            } catch (Exception exception) {
                ResourceManager.LOGGER.error("Failed to add resource pack {}", iresourcepack.a(), exception);
                return new ResourceManager.a(new ResourceManager.b(iresourcepack, exception));
            }
        }

        return this.b(executor, executor1, this.c, completablefuture);
    }

    static class a implements IReloadable {

        private final ResourceManager.b a;
        private final CompletableFuture<Unit> b;

        public a(ResourceManager.b resourcemanager_b) {
            this.a = resourcemanager_b;
            this.b = new CompletableFuture();
            this.b.completeExceptionally(resourcemanager_b);
        }

        @Override
        public CompletableFuture<Unit> a() {
            return this.b;
        }
    }

    public static class b extends RuntimeException {

        private final IResourcePack a;

        public b(IResourcePack iresourcepack, Throwable throwable) {
            super(iresourcepack.a(), throwable);
            this.a = iresourcepack;
        }
    }
}
