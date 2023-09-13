package net.minecraft.server.packs.resources;

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
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class ResourceManager implements IReloadableResourceManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, ResourceManagerFallback> namespacedPacks = Maps.newHashMap();
    private final List<IReloadListener> listeners = Lists.newArrayList();
    private final Set<String> namespaces = Sets.newLinkedHashSet();
    private final List<IResourcePack> packs = Lists.newArrayList();
    private final EnumResourcePackType type;

    public ResourceManager(EnumResourcePackType enumresourcepacktype) {
        this.type = enumresourcepacktype;
    }

    public void a(IResourcePack iresourcepack) {
        this.packs.add(iresourcepack);

        ResourceManagerFallback resourcemanagerfallback;

        for (Iterator iterator = iresourcepack.a(this.type).iterator(); iterator.hasNext(); resourcemanagerfallback.a(iresourcepack)) {
            String s = (String) iterator.next();

            this.namespaces.add(s);
            resourcemanagerfallback = (ResourceManagerFallback) this.namespacedPacks.get(s);
            if (resourcemanagerfallback == null) {
                resourcemanagerfallback = new ResourceManagerFallback(this.type, s);
                this.namespacedPacks.put(s, resourcemanagerfallback);
            }
        }

    }

    @Override
    public Set<String> a() {
        return this.namespaces;
    }

    @Override
    public IResource a(MinecraftKey minecraftkey) throws IOException {
        IResourceManager iresourcemanager = (IResourceManager) this.namespacedPacks.get(minecraftkey.getNamespace());

        if (iresourcemanager != null) {
            return iresourcemanager.a(minecraftkey);
        } else {
            throw new FileNotFoundException(minecraftkey.toString());
        }
    }

    @Override
    public boolean b(MinecraftKey minecraftkey) {
        IResourceManager iresourcemanager = (IResourceManager) this.namespacedPacks.get(minecraftkey.getNamespace());

        return iresourcemanager != null ? iresourcemanager.b(minecraftkey) : false;
    }

    @Override
    public List<IResource> c(MinecraftKey minecraftkey) throws IOException {
        IResourceManager iresourcemanager = (IResourceManager) this.namespacedPacks.get(minecraftkey.getNamespace());

        if (iresourcemanager != null) {
            return iresourcemanager.c(minecraftkey);
        } else {
            throw new FileNotFoundException(minecraftkey.toString());
        }
    }

    @Override
    public Collection<MinecraftKey> a(String s, Predicate<String> predicate) {
        Set<MinecraftKey> set = Sets.newHashSet();
        Iterator iterator = this.namespacedPacks.values().iterator();

        while (iterator.hasNext()) {
            ResourceManagerFallback resourcemanagerfallback = (ResourceManagerFallback) iterator.next();

            set.addAll(resourcemanagerfallback.a(s, predicate));
        }

        List<MinecraftKey> list = Lists.newArrayList(set);

        Collections.sort(list);
        return list;
    }

    private void c() {
        this.namespacedPacks.clear();
        this.namespaces.clear();
        this.packs.forEach(IResourcePack::close);
        this.packs.clear();
    }

    @Override
    public void close() {
        this.c();
    }

    @Override
    public void a(IReloadListener ireloadlistener) {
        this.listeners.add(ireloadlistener);
    }

    @Override
    public IReloadable a(Executor executor, Executor executor1, CompletableFuture<Unit> completablefuture, List<IResourcePack> list) {
        ResourceManager.LOGGER.info("Reloading ResourceManager: {}", new Supplier[]{() -> {
                    return list.stream().map(IResourcePack::a).collect(Collectors.joining(", "));
                }});
        this.c();
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

        return (IReloadable) (ResourceManager.LOGGER.isDebugEnabled() ? new ReloadableProfiled(this, Lists.newArrayList(this.listeners), executor, executor1, completablefuture) : Reloadable.a(this, Lists.newArrayList(this.listeners), executor, executor1, completablefuture));
    }

    @Override
    public Stream<IResourcePack> b() {
        return this.packs.stream();
    }

    private static class a implements IReloadable {

        private final ResourceManager.b exception;
        private final CompletableFuture<Unit> failedFuture;

        public a(ResourceManager.b resourcemanager_b) {
            this.exception = resourcemanager_b;
            this.failedFuture = new CompletableFuture();
            this.failedFuture.completeExceptionally(resourcemanager_b);
        }

        @Override
        public CompletableFuture<Unit> a() {
            return this.failedFuture;
        }

        @Override
        public float b() {
            return 0.0F;
        }

        @Override
        public boolean c() {
            return true;
        }

        @Override
        public void d() {
            throw this.exception;
        }
    }

    public static class b extends RuntimeException {

        private final IResourcePack pack;

        public b(IResourcePack iresourcepack, Throwable throwable) {
            super(iresourcepack.a(), throwable);
            this.pack = iresourcepack;
        }

        public IResourcePack a() {
            return this.pack;
        }
    }
}
