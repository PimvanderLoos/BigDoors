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

    public void reload() {
        List<String> list = (List) this.selected.stream().map(ResourcePackLoader::getId).collect(ImmutableList.toImmutableList());

        this.close();
        this.available = this.discoverAvailable();
        this.selected = this.rebuildSelected(list);
    }

    private Map<String, ResourcePackLoader> discoverAvailable() {
        Map<String, ResourcePackLoader> map = Maps.newTreeMap();
        Iterator iterator = this.sources.iterator();

        while (iterator.hasNext()) {
            ResourcePackSource resourcepacksource = (ResourcePackSource) iterator.next();

            resourcepacksource.loadPacks((resourcepackloader) -> {
                map.put(resourcepackloader.getId(), resourcepackloader);
            }, this.constructor);
        }

        return ImmutableMap.copyOf(map);
    }

    public void setSelected(Collection<String> collection) {
        this.selected = this.rebuildSelected(collection);
    }

    private List<ResourcePackLoader> rebuildSelected(Collection<String> collection) {
        List<ResourcePackLoader> list = (List) this.getAvailablePacks(collection).collect(Collectors.toList());
        Iterator iterator = this.available.values().iterator();

        while (iterator.hasNext()) {
            ResourcePackLoader resourcepackloader = (ResourcePackLoader) iterator.next();

            if (resourcepackloader.isRequired() && !list.contains(resourcepackloader)) {
                resourcepackloader.getDefaultPosition().insert(list, resourcepackloader, Functions.identity(), false);
            }
        }

        return ImmutableList.copyOf(list);
    }

    private Stream<ResourcePackLoader> getAvailablePacks(Collection<String> collection) {
        Stream stream = collection.stream();
        Map map = this.available;

        Objects.requireNonNull(this.available);
        return stream.map(map::get).filter(Objects::nonNull);
    }

    public Collection<String> getAvailableIds() {
        return this.available.keySet();
    }

    public Collection<ResourcePackLoader> getAvailablePacks() {
        return this.available.values();
    }

    public Collection<String> getSelectedIds() {
        return (Collection) this.selected.stream().map(ResourcePackLoader::getId).collect(ImmutableSet.toImmutableSet());
    }

    public Collection<ResourcePackLoader> getSelectedPacks() {
        return this.selected;
    }

    @Nullable
    public ResourcePackLoader getPack(String s) {
        return (ResourcePackLoader) this.available.get(s);
    }

    public void close() {
        this.available.values().forEach(ResourcePackLoader::close);
    }

    public boolean isAvailable(String s) {
        return this.available.containsKey(s);
    }

    public List<IResourcePack> openAllSelected() {
        return (List) this.selected.stream().map(ResourcePackLoader::open).collect(ImmutableList.toImmutableList());
    }
}
