package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import org.slf4j.Logger;

public class ResourceManagerFallback implements IResourceManager {

    static final Logger LOGGER = LogUtils.getLogger();
    protected final List<ResourceManagerFallback.d> fallbacks = Lists.newArrayList();
    private final EnumResourcePackType type;
    private final String namespace;

    public ResourceManagerFallback(EnumResourcePackType enumresourcepacktype, String s) {
        this.type = enumresourcepacktype;
        this.namespace = s;
    }

    public void push(IResourcePack iresourcepack) {
        this.pushInternal(iresourcepack.packId(), iresourcepack, (Predicate) null);
    }

    public void push(IResourcePack iresourcepack, Predicate<MinecraftKey> predicate) {
        this.pushInternal(iresourcepack.packId(), iresourcepack, predicate);
    }

    public void pushFilterOnly(String s, Predicate<MinecraftKey> predicate) {
        this.pushInternal(s, (IResourcePack) null, predicate);
    }

    private void pushInternal(String s, @Nullable IResourcePack iresourcepack, @Nullable Predicate<MinecraftKey> predicate) {
        this.fallbacks.add(new ResourceManagerFallback.d(s, iresourcepack, predicate));
    }

    @Override
    public Set<String> getNamespaces() {
        return ImmutableSet.of(this.namespace);
    }

    @Override
    public Optional<IResource> getResource(MinecraftKey minecraftkey) {
        for (int i = this.fallbacks.size() - 1; i >= 0; --i) {
            ResourceManagerFallback.d resourcemanagerfallback_d = (ResourceManagerFallback.d) this.fallbacks.get(i);
            IResourcePack iresourcepack = resourcemanagerfallback_d.resources;

            if (iresourcepack != null) {
                IoSupplier<InputStream> iosupplier = iresourcepack.getResource(this.type, minecraftkey);

                if (iosupplier != null) {
                    IoSupplier<ResourceMetadata> iosupplier1 = this.createStackMetadataFinder(minecraftkey, i);

                    return Optional.of(createResource(iresourcepack, minecraftkey, iosupplier, iosupplier1));
                }
            }

            if (resourcemanagerfallback_d.isFiltered(minecraftkey)) {
                ResourceManagerFallback.LOGGER.warn("Resource {} not found, but was filtered by pack {}", minecraftkey, resourcemanagerfallback_d.name);
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private static IResource createResource(IResourcePack iresourcepack, MinecraftKey minecraftkey, IoSupplier<InputStream> iosupplier, IoSupplier<ResourceMetadata> iosupplier1) {
        return new IResource(iresourcepack, wrapForDebug(minecraftkey, iresourcepack, iosupplier), iosupplier1);
    }

    private static IoSupplier<InputStream> wrapForDebug(MinecraftKey minecraftkey, IResourcePack iresourcepack, IoSupplier<InputStream> iosupplier) {
        return ResourceManagerFallback.LOGGER.isDebugEnabled() ? () -> {
            return new ResourceManagerFallback.c((InputStream) iosupplier.get(), minecraftkey, iresourcepack.packId());
        } : iosupplier;
    }

    @Override
    public List<IResource> getResourceStack(MinecraftKey minecraftkey) {
        MinecraftKey minecraftkey1 = getMetadataLocation(minecraftkey);
        List<IResource> list = new ArrayList();
        boolean flag = false;
        String s = null;

        for (int i = this.fallbacks.size() - 1; i >= 0; --i) {
            ResourceManagerFallback.d resourcemanagerfallback_d = (ResourceManagerFallback.d) this.fallbacks.get(i);
            IResourcePack iresourcepack = resourcemanagerfallback_d.resources;

            if (iresourcepack != null) {
                IoSupplier<InputStream> iosupplier = iresourcepack.getResource(this.type, minecraftkey);

                if (iosupplier != null) {
                    IoSupplier iosupplier1;

                    if (flag) {
                        iosupplier1 = ResourceMetadata.EMPTY_SUPPLIER;
                    } else {
                        iosupplier1 = () -> {
                            IoSupplier<InputStream> iosupplier2 = iresourcepack.getResource(this.type, minecraftkey1);

                            return iosupplier2 != null ? parseMetadata(iosupplier2) : ResourceMetadata.EMPTY;
                        };
                    }

                    list.add(new IResource(iresourcepack, iosupplier, iosupplier1));
                }
            }

            if (resourcemanagerfallback_d.isFiltered(minecraftkey)) {
                s = resourcemanagerfallback_d.name;
                break;
            }

            if (resourcemanagerfallback_d.isFiltered(minecraftkey1)) {
                flag = true;
            }
        }

        if (list.isEmpty() && s != null) {
            ResourceManagerFallback.LOGGER.warn("Resource {} not found, but was filtered by pack {}", minecraftkey, s);
        }

        return Lists.reverse(list);
    }

    private static boolean isMetadata(MinecraftKey minecraftkey) {
        return minecraftkey.getPath().endsWith(".mcmeta");
    }

    private static MinecraftKey getResourceLocationFromMetadata(MinecraftKey minecraftkey) {
        String s = minecraftkey.getPath().substring(0, minecraftkey.getPath().length() - ".mcmeta".length());

        return minecraftkey.withPath(s);
    }

    static MinecraftKey getMetadataLocation(MinecraftKey minecraftkey) {
        return minecraftkey.withPath(minecraftkey.getPath() + ".mcmeta");
    }

    @Override
    public Map<MinecraftKey, IResource> listResources(String s, Predicate<MinecraftKey> predicate) {
        Map<MinecraftKey, a> map = new HashMap();
        Map<MinecraftKey, a> map1 = new HashMap();
        int i = this.fallbacks.size();

        for (int j = 0; j < i; ++j) {
            ResourceManagerFallback.d resourcemanagerfallback_d = (ResourceManagerFallback.d) this.fallbacks.get(j);

            resourcemanagerfallback_d.filterAll(map.keySet());
            resourcemanagerfallback_d.filterAll(map1.keySet());
            IResourcePack iresourcepack = resourcemanagerfallback_d.resources;

            if (iresourcepack != null) {
                iresourcepack.listResources(this.type, this.namespace, s, (minecraftkey, iosupplier) -> {
                    record a(IResourcePack packResources, IoSupplier<InputStream> resource, int packIndex) {

                    }

                    if (isMetadata(minecraftkey)) {
                        if (predicate.test(getResourceLocationFromMetadata(minecraftkey))) {
                            map1.put(minecraftkey, new a(iresourcepack, iosupplier, j));
                        }
                    } else if (predicate.test(minecraftkey)) {
                        map.put(minecraftkey, new a(iresourcepack, iosupplier, j));
                    }

                });
            }
        }

        Map<MinecraftKey, IResource> map2 = Maps.newTreeMap();

        map.forEach((minecraftkey, a0) -> {
            MinecraftKey minecraftkey1 = getMetadataLocation(minecraftkey);
            a a1 = (a) map1.get(minecraftkey1);
            IoSupplier iosupplier;

            if (a1 != null && a1.packIndex >= a0.packIndex) {
                iosupplier = convertToMetadata(a1.resource);
            } else {
                iosupplier = ResourceMetadata.EMPTY_SUPPLIER;
            }

            map2.put(minecraftkey, createResource(a0.packResources, minecraftkey, a0.resource, iosupplier));
        });
        return map2;
    }

    private IoSupplier<ResourceMetadata> createStackMetadataFinder(MinecraftKey minecraftkey, int i) {
        return () -> {
            MinecraftKey minecraftkey1 = getMetadataLocation(minecraftkey);

            for (int j = this.fallbacks.size() - 1; j >= i; --j) {
                ResourceManagerFallback.d resourcemanagerfallback_d = (ResourceManagerFallback.d) this.fallbacks.get(j);
                IResourcePack iresourcepack = resourcemanagerfallback_d.resources;

                if (iresourcepack != null) {
                    IoSupplier<InputStream> iosupplier = iresourcepack.getResource(this.type, minecraftkey1);

                    if (iosupplier != null) {
                        return parseMetadata(iosupplier);
                    }
                }

                if (resourcemanagerfallback_d.isFiltered(minecraftkey1)) {
                    break;
                }
            }

            return ResourceMetadata.EMPTY;
        };
    }

    private static IoSupplier<ResourceMetadata> convertToMetadata(IoSupplier<InputStream> iosupplier) {
        return () -> {
            return parseMetadata(iosupplier);
        };
    }

    private static ResourceMetadata parseMetadata(IoSupplier<InputStream> iosupplier) throws IOException {
        InputStream inputstream = (InputStream) iosupplier.get();

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

    private static void applyPackFiltersToExistingResources(ResourceManagerFallback.d resourcemanagerfallback_d, Map<MinecraftKey, ResourceManagerFallback.b> map) {
        Iterator iterator = map.values().iterator();

        while (iterator.hasNext()) {
            ResourceManagerFallback.b resourcemanagerfallback_b = (ResourceManagerFallback.b) iterator.next();

            if (resourcemanagerfallback_d.isFiltered(resourcemanagerfallback_b.fileLocation)) {
                resourcemanagerfallback_b.fileSources.clear();
            } else if (resourcemanagerfallback_d.isFiltered(resourcemanagerfallback_b.metadataLocation())) {
                resourcemanagerfallback_b.metaSources.clear();
            }
        }

    }

    private void listPackResources(ResourceManagerFallback.d resourcemanagerfallback_d, String s, Predicate<MinecraftKey> predicate, Map<MinecraftKey, ResourceManagerFallback.b> map) {
        IResourcePack iresourcepack = resourcemanagerfallback_d.resources;

        if (iresourcepack != null) {
            iresourcepack.listResources(this.type, this.namespace, s, (minecraftkey, iosupplier) -> {
                if (isMetadata(minecraftkey)) {
                    MinecraftKey minecraftkey1 = getResourceLocationFromMetadata(minecraftkey);

                    if (!predicate.test(minecraftkey1)) {
                        return;
                    }

                    ((ResourceManagerFallback.b) map.computeIfAbsent(minecraftkey1, ResourceManagerFallback.b::new)).metaSources.put(iresourcepack, iosupplier);
                } else {
                    if (!predicate.test(minecraftkey)) {
                        return;
                    }

                    ((ResourceManagerFallback.b) map.computeIfAbsent(minecraftkey, ResourceManagerFallback.b::new)).fileSources.add(new ResourceManagerFallback.e(iresourcepack, iosupplier));
                }

            });
        }
    }

    @Override
    public Map<MinecraftKey, List<IResource>> listResourceStacks(String s, Predicate<MinecraftKey> predicate) {
        Map<MinecraftKey, ResourceManagerFallback.b> map = Maps.newHashMap();
        Iterator iterator = this.fallbacks.iterator();

        while (iterator.hasNext()) {
            ResourceManagerFallback.d resourcemanagerfallback_d = (ResourceManagerFallback.d) iterator.next();

            applyPackFiltersToExistingResources(resourcemanagerfallback_d, map);
            this.listPackResources(resourcemanagerfallback_d, s, predicate, map);
        }

        TreeMap<MinecraftKey, List<IResource>> treemap = Maps.newTreeMap();
        Iterator iterator1 = map.values().iterator();

        while (iterator1.hasNext()) {
            ResourceManagerFallback.b resourcemanagerfallback_b = (ResourceManagerFallback.b) iterator1.next();

            if (!resourcemanagerfallback_b.fileSources.isEmpty()) {
                List<IResource> list = new ArrayList();
                Iterator iterator2 = resourcemanagerfallback_b.fileSources.iterator();

                while (iterator2.hasNext()) {
                    ResourceManagerFallback.e resourcemanagerfallback_e = (ResourceManagerFallback.e) iterator2.next();
                    IResourcePack iresourcepack = resourcemanagerfallback_e.source;
                    IoSupplier<InputStream> iosupplier = (IoSupplier) resourcemanagerfallback_b.metaSources.get(iresourcepack);
                    IoSupplier<ResourceMetadata> iosupplier1 = iosupplier != null ? convertToMetadata(iosupplier) : ResourceMetadata.EMPTY_SUPPLIER;

                    list.add(createResource(iresourcepack, resourcemanagerfallback_b.fileLocation, resourcemanagerfallback_e.resource, iosupplier1));
                }

                treemap.put(resourcemanagerfallback_b.fileLocation, list);
            }
        }

        return treemap;
    }

    @Override
    public Stream<IResourcePack> listPacks() {
        return this.fallbacks.stream().map((resourcemanagerfallback_d) -> {
            return resourcemanagerfallback_d.resources;
        }).filter(Objects::nonNull);
    }

    private static record d(String name, @Nullable IResourcePack resources, @Nullable Predicate<MinecraftKey> filter) {

        public void filterAll(Collection<MinecraftKey> collection) {
            if (this.filter != null) {
                collection.removeIf(this.filter);
            }

        }

        public boolean isFiltered(MinecraftKey minecraftkey) {
            return this.filter != null && this.filter.test(minecraftkey);
        }
    }

    private static record b(MinecraftKey fileLocation, MinecraftKey metadataLocation, List<ResourceManagerFallback.e> fileSources, Map<IResourcePack, IoSupplier<InputStream>> metaSources) {

        b(MinecraftKey minecraftkey) {
            this(minecraftkey, ResourceManagerFallback.getMetadataLocation(minecraftkey), new ArrayList(), new Object2ObjectArrayMap());
        }
    }

    private static record e(IResourcePack source, IoSupplier<InputStream> resource) {

    }

    private static class c extends FilterInputStream {

        private final Supplier<String> message;
        private boolean closed;

        public c(InputStream inputstream, MinecraftKey minecraftkey, String s) {
            super(inputstream);
            Exception exception = new Exception("Stacktrace");

            this.message = () -> {
                StringWriter stringwriter = new StringWriter();

                exception.printStackTrace(new PrintWriter(stringwriter));
                return "Leaked resource: '" + minecraftkey + "' loaded from pack: '" + s + "'\n" + stringwriter;
            };
        }

        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                ResourceManagerFallback.LOGGER.warn("{}", this.message.get());
            }

            super.finalize();
        }
    }
}
