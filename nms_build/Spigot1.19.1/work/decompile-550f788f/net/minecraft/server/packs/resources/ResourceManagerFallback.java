package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import org.slf4j.Logger;

public class ResourceManagerFallback implements IResourceManager {

    static final Logger LOGGER = LogUtils.getLogger();
    protected final List<ResourceManagerFallback.c> fallbacks = Lists.newArrayList();
    final EnumResourcePackType type;
    private final String namespace;

    public ResourceManagerFallback(EnumResourcePackType enumresourcepacktype, String s) {
        this.type = enumresourcepacktype;
        this.namespace = s;
    }

    public void push(IResourcePack iresourcepack) {
        this.pushInternal(iresourcepack.getName(), iresourcepack, (Predicate) null);
    }

    public void push(IResourcePack iresourcepack, Predicate<MinecraftKey> predicate) {
        this.pushInternal(iresourcepack.getName(), iresourcepack, predicate);
    }

    public void pushFilterOnly(String s, Predicate<MinecraftKey> predicate) {
        this.pushInternal(s, (IResourcePack) null, predicate);
    }

    private void pushInternal(String s, @Nullable IResourcePack iresourcepack, @Nullable Predicate<MinecraftKey> predicate) {
        this.fallbacks.add(new ResourceManagerFallback.c(s, iresourcepack, predicate));
    }

    @Override
    public Set<String> getNamespaces() {
        return ImmutableSet.of(this.namespace);
    }

    @Override
    public Optional<IResource> getResource(MinecraftKey minecraftkey) {
        if (!this.isValidLocation(minecraftkey)) {
            return Optional.empty();
        } else {
            for (int i = this.fallbacks.size() - 1; i >= 0; --i) {
                ResourceManagerFallback.c resourcemanagerfallback_c = (ResourceManagerFallback.c) this.fallbacks.get(i);
                IResourcePack iresourcepack = resourcemanagerfallback_c.resources;

                if (iresourcepack != null && iresourcepack.hasResource(this.type, minecraftkey)) {
                    return Optional.of(new IResource(iresourcepack.getName(), this.createResourceGetter(minecraftkey, iresourcepack), this.createStackMetadataFinder(minecraftkey, i)));
                }

                if (resourcemanagerfallback_c.isFiltered(minecraftkey)) {
                    ResourceManagerFallback.LOGGER.warn("Resource {} not found, but was filtered by pack {}", minecraftkey, resourcemanagerfallback_c.name);
                    return Optional.empty();
                }
            }

            return Optional.empty();
        }
    }

    IResource.a<InputStream> createResourceGetter(MinecraftKey minecraftkey, IResourcePack iresourcepack) {
        return ResourceManagerFallback.LOGGER.isDebugEnabled() ? () -> {
            InputStream inputstream = iresourcepack.getResource(this.type, minecraftkey);

            return new ResourceManagerFallback.b(inputstream, minecraftkey, iresourcepack.getName());
        } : () -> {
            return iresourcepack.getResource(this.type, minecraftkey);
        };
    }

    private boolean isValidLocation(MinecraftKey minecraftkey) {
        return !minecraftkey.getPath().contains("..");
    }

    @Override
    public List<IResource> getResourceStack(MinecraftKey minecraftkey) {
        if (!this.isValidLocation(minecraftkey)) {
            return List.of();
        } else {
            List<ResourceManagerFallback.d> list = Lists.newArrayList();
            MinecraftKey minecraftkey1 = getMetadataLocation(minecraftkey);
            String s = null;
            Iterator iterator = this.fallbacks.iterator();

            while (iterator.hasNext()) {
                ResourceManagerFallback.c resourcemanagerfallback_c = (ResourceManagerFallback.c) iterator.next();

                if (resourcemanagerfallback_c.isFiltered(minecraftkey)) {
                    if (!list.isEmpty()) {
                        s = resourcemanagerfallback_c.name;
                    }

                    list.clear();
                } else if (resourcemanagerfallback_c.isFiltered(minecraftkey1)) {
                    list.forEach(ResourceManagerFallback.d::ignoreMeta);
                }

                IResourcePack iresourcepack = resourcemanagerfallback_c.resources;

                if (iresourcepack != null && iresourcepack.hasResource(this.type, minecraftkey)) {
                    list.add(new ResourceManagerFallback.d(minecraftkey, minecraftkey1, iresourcepack));
                }
            }

            if (list.isEmpty() && s != null) {
                ResourceManagerFallback.LOGGER.info("Resource {} was filtered by pack {}", minecraftkey, s);
            }

            return list.stream().map(ResourceManagerFallback.d::create).toList();
        }
    }

    @Override
    public Map<MinecraftKey, IResource> listResources(String s, Predicate<MinecraftKey> predicate) {
        Object2IntMap<MinecraftKey> object2intmap = new Object2IntOpenHashMap();
        int i = this.fallbacks.size();

        for (int j = 0; j < i; ++j) {
            ResourceManagerFallback.c resourcemanagerfallback_c = (ResourceManagerFallback.c) this.fallbacks.get(j);

            resourcemanagerfallback_c.filterAll(object2intmap.keySet());
            if (resourcemanagerfallback_c.resources != null) {
                Iterator iterator = resourcemanagerfallback_c.resources.getResources(this.type, this.namespace, s, predicate).iterator();

                while (iterator.hasNext()) {
                    MinecraftKey minecraftkey = (MinecraftKey) iterator.next();

                    object2intmap.put(minecraftkey, j);
                }
            }
        }

        Map<MinecraftKey, IResource> map = Maps.newTreeMap();
        ObjectIterator objectiterator = Object2IntMaps.fastIterable(object2intmap).iterator();

        while (objectiterator.hasNext()) {
            Entry<MinecraftKey> entry = (Entry) objectiterator.next();
            int k = entry.getIntValue();
            MinecraftKey minecraftkey1 = (MinecraftKey) entry.getKey();
            IResourcePack iresourcepack = ((ResourceManagerFallback.c) this.fallbacks.get(k)).resources;

            map.put(minecraftkey1, new IResource(iresourcepack.getName(), this.createResourceGetter(minecraftkey1, iresourcepack), this.createStackMetadataFinder(minecraftkey1, k)));
        }

        return map;
    }

    private IResource.a<ResourceMetadata> createStackMetadataFinder(MinecraftKey minecraftkey, int i) {
        return () -> {
            MinecraftKey minecraftkey1 = getMetadataLocation(minecraftkey);

            for (int j = this.fallbacks.size() - 1; j >= i; --j) {
                ResourceManagerFallback.c resourcemanagerfallback_c = (ResourceManagerFallback.c) this.fallbacks.get(j);
                IResourcePack iresourcepack = resourcemanagerfallback_c.resources;

                if (iresourcepack != null && iresourcepack.hasResource(this.type, minecraftkey1)) {
                    InputStream inputstream = iresourcepack.getResource(this.type, minecraftkey1);

                    ResourceMetadata resourcemetadata;

                    try {
                        resourcemetadata = ResourceMetadata.fromJsonStream(inputstream);
                    } catch (Throwable throwable) {
                        if (inputstream != null) {
                            try {
                                inputstream.close();
                            } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                            }
                        }

                        throw throwable;
                    }

                    if (inputstream != null) {
                        inputstream.close();
                    }

                    return resourcemetadata;
                }

                if (resourcemanagerfallback_c.isFiltered(minecraftkey1)) {
                    break;
                }
            }

            return ResourceMetadata.EMPTY;
        };
    }

    private static void applyPackFiltersToExistingResources(ResourceManagerFallback.c resourcemanagerfallback_c, Map<MinecraftKey, ResourceManagerFallback.a> map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry<MinecraftKey, ResourceManagerFallback.a> java_util_map_entry = (java.util.Map.Entry) iterator.next();
            MinecraftKey minecraftkey = (MinecraftKey) java_util_map_entry.getKey();
            ResourceManagerFallback.a resourcemanagerfallback_a = (ResourceManagerFallback.a) java_util_map_entry.getValue();

            if (resourcemanagerfallback_c.isFiltered(minecraftkey)) {
                iterator.remove();
            } else if (resourcemanagerfallback_c.isFiltered(resourcemanagerfallback_a.metadataLocation())) {
                resourcemanagerfallback_a.entries.forEach(ResourceManagerFallback.d::ignoreMeta);
            }
        }

    }

    private void listPackResources(ResourceManagerFallback.c resourcemanagerfallback_c, String s, Predicate<MinecraftKey> predicate, Map<MinecraftKey, ResourceManagerFallback.a> map) {
        IResourcePack iresourcepack = resourcemanagerfallback_c.resources;

        if (iresourcepack != null) {
            Iterator iterator = iresourcepack.getResources(this.type, this.namespace, s, predicate).iterator();

            while (iterator.hasNext()) {
                MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
                MinecraftKey minecraftkey1 = getMetadataLocation(minecraftkey);

                ((ResourceManagerFallback.a) map.computeIfAbsent(minecraftkey, (minecraftkey2) -> {
                    return new ResourceManagerFallback.a(minecraftkey1, Lists.newArrayList());
                })).entries().add(new ResourceManagerFallback.d(minecraftkey, minecraftkey1, iresourcepack));
            }

        }
    }

    @Override
    public Map<MinecraftKey, List<IResource>> listResourceStacks(String s, Predicate<MinecraftKey> predicate) {
        Map<MinecraftKey, ResourceManagerFallback.a> map = Maps.newHashMap();
        Iterator iterator = this.fallbacks.iterator();

        while (iterator.hasNext()) {
            ResourceManagerFallback.c resourcemanagerfallback_c = (ResourceManagerFallback.c) iterator.next();

            applyPackFiltersToExistingResources(resourcemanagerfallback_c, map);
            this.listPackResources(resourcemanagerfallback_c, s, predicate, map);
        }

        TreeMap<MinecraftKey, List<IResource>> treemap = Maps.newTreeMap();

        map.forEach((minecraftkey, resourcemanagerfallback_a) -> {
            treemap.put(minecraftkey, resourcemanagerfallback_a.createThunks());
        });
        return treemap;
    }

    @Override
    public Stream<IResourcePack> listPacks() {
        return this.fallbacks.stream().map((resourcemanagerfallback_c) -> {
            return resourcemanagerfallback_c.resources;
        }).filter(Objects::nonNull);
    }

    static MinecraftKey getMetadataLocation(MinecraftKey minecraftkey) {
        return new MinecraftKey(minecraftkey.getNamespace(), minecraftkey.getPath() + ".mcmeta");
    }

    private static record c(String name, @Nullable IResourcePack resources, @Nullable Predicate<MinecraftKey> filter) {

        public void filterAll(Collection<MinecraftKey> collection) {
            if (this.filter != null) {
                collection.removeIf(this.filter);
            }

        }

        public boolean isFiltered(MinecraftKey minecraftkey) {
            return this.filter != null && this.filter.test(minecraftkey);
        }
    }

    private class d {

        private final MinecraftKey location;
        private final MinecraftKey metadataLocation;
        private final IResourcePack source;
        private boolean shouldGetMeta = true;

        d(MinecraftKey minecraftkey, MinecraftKey minecraftkey1, IResourcePack iresourcepack) {
            this.source = iresourcepack;
            this.location = minecraftkey;
            this.metadataLocation = minecraftkey1;
        }

        public void ignoreMeta() {
            this.shouldGetMeta = false;
        }

        public IResource create() {
            String s = this.source.getName();

            return this.shouldGetMeta ? new IResource(s, ResourceManagerFallback.this.createResourceGetter(this.location, this.source), () -> {
                if (this.source.hasResource(ResourceManagerFallback.this.type, this.metadataLocation)) {
                    InputStream inputstream = this.source.getResource(ResourceManagerFallback.this.type, this.metadataLocation);

                    ResourceMetadata resourcemetadata;

                    try {
                        resourcemetadata = ResourceMetadata.fromJsonStream(inputstream);
                    } catch (Throwable throwable) {
                        if (inputstream != null) {
                            try {
                                inputstream.close();
                            } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                            }
                        }

                        throw throwable;
                    }

                    if (inputstream != null) {
                        inputstream.close();
                    }

                    return resourcemetadata;
                } else {
                    return ResourceMetadata.EMPTY;
                }
            }) : new IResource(s, ResourceManagerFallback.this.createResourceGetter(this.location, this.source));
        }
    }

    private static record a(MinecraftKey metadataLocation, List<ResourceManagerFallback.d> entries) {

        List<IResource> createThunks() {
            return this.entries().stream().map(ResourceManagerFallback.d::create).toList();
        }
    }

    private static class b extends FilterInputStream {

        private final String message;
        private boolean closed;

        public b(InputStream inputstream, MinecraftKey minecraftkey, String s) {
            super(inputstream);
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

            (new Exception()).printStackTrace(new PrintStream(bytearrayoutputstream));
            this.message = "Leaked resource: '" + minecraftkey + "' loaded from pack: '" + s + "'\n" + bytearrayoutputstream;
        }

        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                ResourceManagerFallback.LOGGER.warn(this.message);
            }

            super.finalize();
        }
    }
}
