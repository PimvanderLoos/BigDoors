package net.minecraft.server.packs.repository;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;

public class ResourcePackRepository implements AutoCloseable {

    private final Set<ResourcePackSource> sources;
    private Map<String, ResourcePackLoader> available;
    private List<ResourcePackLoader> selected;
    private final ResourcePackLoader.a constructor;

    public ResourcePackRepository(ResourcePackLoader.a resourcepackloader_a, ResourcePackSource... aresourcepacksource) {
        this.available = ImmutableMap.of();
        this.selected = ImmutableList.of();
        this.constructor = resourcepackloader_a;
        this.sources = ImmutableSet.copyOf(aresourcepacksource);
    }

    public ResourcePackRepository(EnumResourcePackType enumresourcepacktype, ResourcePackSource... aresourcepacksource) {
        this((s, ichatbasecomponent, flag, supplier, resourcepackinfo, resourcepackloader_position, packsource) -> {
            return new ResourcePackLoader(s, ichatbasecomponent, flag, supplier, resourcepackinfo, enumresourcepacktype, resourcepackloader_position, packsource);
        }, aresourcepacksource);
    }

    public void a() {
        List<String> list = (List) this.selected.stream().map(ResourcePackLoader::e).collect(ImmutableList.toImmutableList());

        this.close();
        this.available = this.g();
        this.selected = this.b((Collection) list);
    }

    private Map<String, ResourcePackLoader> g() {
        Map<String, ResourcePackLoader> map = Maps.newTreeMap();
        Iterator iterator = this.sources.iterator();

        while (iterator.hasNext()) {
            ResourcePackSource resourcepacksource = (ResourcePackSource) iterator.next();

            resourcepacksource.a((resourcepackloader) -> {
                map.put(resourcepackloader.e(), resourcepackloader);
            }, this.constructor);
        }

        return ImmutableMap.copyOf(map);
    }

    public void a(Collection<String> collection) {
        this.selected = this.b(collection);
    }

    private List<ResourcePackLoader> b(Collection<String> collection) {
        List<ResourcePackLoader> list = (List) this.c(collection).collect(Collectors.toList());
        Iterator iterator = this.available.values().iterator();

        while (iterator.hasNext()) {
            ResourcePackLoader resourcepackloader = (ResourcePackLoader) iterator.next();

            if (resourcepackloader.f() && !list.contains(resourcepackloader)) {
                resourcepackloader.h().a(list, resourcepackloader, Functions.identity(), false);
            }
        }

        return ImmutableList.copyOf(list);
    }

    private Stream<ResourcePackLoader> c(Collection<String> collection) {
        Stream stream = collection.stream();
        Map map = this.available;

        Objects.requireNonNull(this.available);
        return stream.map(map::get).filter(Objects::nonNull);
    }

    public Collection<String> b() {
        return this.available.keySet();
    }

    public Collection<ResourcePackLoader> c() {
        return this.available.values();
    }

    public Collection<String> d() {
        return (Collection) this.selected.stream().map(ResourcePackLoader::e).collect(ImmutableSet.toImmutableSet());
    }

    public Collection<ResourcePackLoader> e() {
        return this.selected;
    }

    @Nullable
    public ResourcePackLoader a(String s) {
        return (ResourcePackLoader) this.available.get(s);
    }

    public void close() {
        this.available.values().forEach(ResourcePackLoader::close);
    }

    public boolean b(String s) {
        return this.available.containsKey(s);
    }

    public List<IResourcePack> f() {
        return (List) this.selected.stream().map(ResourcePackLoader::d).collect(ImmutableList.toImmutableList());
    }
}
