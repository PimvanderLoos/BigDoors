package net.minecraft.server;

import com.google.common.base.Stopwatch;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceManager implements IReloadableResourceManager {

    private static final Logger a = LogManager.getLogger();
    private final Map<String, ResourceManagerFallback> b = Maps.newHashMap();
    private final List<IResourcePackListener> c = Lists.newArrayList();
    private final Set<String> d = Sets.newLinkedHashSet();
    private final EnumResourcePackType e;

    public ResourceManager(EnumResourcePackType enumresourcepacktype) {
        this.e = enumresourcepacktype;
    }

    public void a(IResourcePack iresourcepack) {
        ResourceManagerFallback resourcemanagerfallback;

        for (Iterator iterator = iresourcepack.a(this.e).iterator(); iterator.hasNext(); resourcemanagerfallback.a(iresourcepack)) {
            String s = (String) iterator.next();

            this.d.add(s);
            resourcemanagerfallback = (ResourceManagerFallback) this.b.get(s);
            if (resourcemanagerfallback == null) {
                resourcemanagerfallback = new ResourceManagerFallback(this.e);
                this.b.put(s, resourcemanagerfallback);
            }
        }

    }

    public IResource a(MinecraftKey minecraftkey) throws IOException {
        IResourceManager iresourcemanager = (IResourceManager) this.b.get(minecraftkey.b());

        if (iresourcemanager != null) {
            return iresourcemanager.a(minecraftkey);
        } else {
            throw new FileNotFoundException(minecraftkey.toString());
        }
    }

    public List<IResource> b(MinecraftKey minecraftkey) throws IOException {
        IResourceManager iresourcemanager = (IResourceManager) this.b.get(minecraftkey.b());

        if (iresourcemanager != null) {
            return iresourcemanager.b(minecraftkey);
        } else {
            throw new FileNotFoundException(minecraftkey.toString());
        }
    }

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
        this.d.clear();
    }

    public void a(List<IResourcePack> list) {
        this.b();
        ResourceManager.a.info("Reloading ResourceManager: {}", list.stream().map(IResourcePack::a).collect(Collectors.joining(", ")));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            IResourcePack iresourcepack = (IResourcePack) iterator.next();

            this.a(iresourcepack);
        }

        if (ResourceManager.a.isDebugEnabled()) {
            this.d();
        } else {
            this.c();
        }

    }

    public void a(IResourcePackListener iresourcepacklistener) {
        this.c.add(iresourcepacklistener);
        if (ResourceManager.a.isDebugEnabled()) {
            ResourceManager.a.info(this.b(iresourcepacklistener));
        } else {
            iresourcepacklistener.a(this);
        }

    }

    private void c() {
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            IResourcePackListener iresourcepacklistener = (IResourcePackListener) iterator.next();

            iresourcepacklistener.a(this);
        }

    }

    private void d() {
        ResourceManager.a.info("Reloading all resources! {} listeners to update.", this.c.size());
        List<String> list = Lists.newArrayList();
        Stopwatch stopwatch = Stopwatch.createStarted();
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            IResourcePackListener iresourcepacklistener = (IResourcePackListener) iterator.next();

            list.add(this.b(iresourcepacklistener));
        }

        stopwatch.stop();
        ResourceManager.a.info("----");
        ResourceManager.a.info("Complete resource reload took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        iterator = list.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            ResourceManager.a.info(s);
        }

        ResourceManager.a.info("----");
    }

    private String b(IResourcePackListener iresourcepacklistener) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        iresourcepacklistener.a(this);
        stopwatch.stop();
        return "Resource reload for " + iresourcepacklistener.getClass().getSimpleName() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms";
    }
}
