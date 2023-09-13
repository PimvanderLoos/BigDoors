package net.minecraft.server.packs.repository;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
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
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.world.flag.FeatureFlagSet;

public class ResourcePackRepository {

    private final Set<ResourcePackSource> sources;
    private Map<String, ResourcePackLoader> available = ImmutableMap.of();
    private List<ResourcePackLoader> selected = ImmutableList.of();

    public ResourcePackRepository(ResourcePackSource... aresourcepacksource) {
        this.sources = ImmutableSet.copyOf(aresourcepacksource);
    }

    public void reload() {
        List<String> list = (List) this.selected.stream().map(ResourcePackLoader::getId).collect(ImmutableList.toImmutableList());

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
            });
        }

        return ImmutableMap.copyOf(map);
    }

    public void setSelected(Collection<String> collection) {
        this.selected = this.rebuildSelected(collection);
    }

    public boolean addPack(String s) {
        ResourcePackLoader resourcepackloader = (ResourcePackLoader) this.available.get(s);

        if (resourcepackloader != null && !this.selected.contains(resourcepackloader)) {
            List<ResourcePackLoader> list = Lists.newArrayList(this.selected);

            list.add(resourcepackloader);
            this.selected = list;
            return true;
        } else {
            return false;
        }
    }

    public boolean removePack(String s) {
        ResourcePackLoader resourcepackloader = (ResourcePackLoader) this.available.get(s);

        if (resourcepackloader != null && this.selected.contains(resourcepackloader)) {
            List<ResourcePackLoader> list = Lists.newArrayList(this.selected);

            list.remove(resourcepackloader);
            this.selected = list;
            return true;
        } else {
            return false;
        }
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

    public FeatureFlagSet getRequestedFeatureFlags() {
        return (FeatureFlagSet) this.getSelectedPacks().stream().map(ResourcePackLoader::getRequestedFeatures).reduce(FeatureFlagSet::join).orElse(FeatureFlagSet.of());
    }

    public Collection<ResourcePackLoader> getSelectedPacks() {
        return this.selected;
    }

    @Nullable
    public ResourcePackLoader getPack(String s) {
        return (ResourcePackLoader) this.available.get(s);
    }

    public boolean isAvailable(String s) {
        return this.available.containsKey(s);
    }

    public List<IResourcePack> openAllSelected() {
        return (List) this.selected.stream().map(ResourcePackLoader::open).collect(ImmutableList.toImmutableList());
    }
}
