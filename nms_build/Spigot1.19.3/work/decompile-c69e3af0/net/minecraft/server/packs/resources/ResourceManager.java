package net.minecraft.server.packs.resources;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class ResourceManager implements IReloadableResourceManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, ResourceManagerFallback> namespacedManagers;
    private final List<IResourcePack> packs;

    public ResourceManager(EnumResourcePackType enumresourcepacktype, List<IResourcePack> list) {
        this.packs = List.copyOf(list);
        Map<String, ResourceManagerFallback> map = new HashMap();
        List<String> list1 = list.stream().flatMap((iresourcepack) -> {
            return iresourcepack.getNamespaces(enumresourcepacktype).stream();
        }).distinct().toList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            IResourcePack iresourcepack = (IResourcePack) iterator.next();
            ResourceFilterSection resourcefiltersection = this.getPackFilterSection(iresourcepack);
            Set<String> set = iresourcepack.getNamespaces(enumresourcepacktype);
            Predicate<MinecraftKey> predicate = resourcefiltersection != null ? (minecraftkey) -> {
                return resourcefiltersection.isPathFiltered(minecraftkey.getPath());
            } : null;
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                String s = (String) iterator1.next();
                boolean flag = set.contains(s);
                boolean flag1 = resourcefiltersection != null && resourcefiltersection.isNamespaceFiltered(s);

                if (flag || flag1) {
                    ResourceManagerFallback resourcemanagerfallback = (ResourceManagerFallback) map.get(s);

                    if (resourcemanagerfallback == null) {
                        resourcemanagerfallback = new ResourceManagerFallback(enumresourcepacktype, s);
                        map.put(s, resourcemanagerfallback);
                    }

                    if (flag && flag1) {
                        resourcemanagerfallback.push(iresourcepack, predicate);
                    } else if (flag) {
                        resourcemanagerfallback.push(iresourcepack);
                    } else {
                        resourcemanagerfallback.pushFilterOnly(iresourcepack.packId(), predicate);
                    }
                }
            }
        }

        this.namespacedManagers = map;
    }

    @Nullable
    private ResourceFilterSection getPackFilterSection(IResourcePack iresourcepack) {
        try {
            return (ResourceFilterSection) iresourcepack.getMetadataSection(ResourceFilterSection.TYPE);
        } catch (IOException ioexception) {
            ResourceManager.LOGGER.error("Failed to get filter section from pack {}", iresourcepack.packId());
            return null;
        }
    }

    @Override
    public Set<String> getNamespaces() {
        return this.namespacedManagers.keySet();
    }

    @Override
    public Optional<IResource> getResource(MinecraftKey minecraftkey) {
        IResourceManager iresourcemanager = (IResourceManager) this.namespacedManagers.get(minecraftkey.getNamespace());

        return iresourcemanager != null ? iresourcemanager.getResource(minecraftkey) : Optional.empty();
    }

    @Override
    public List<IResource> getResourceStack(MinecraftKey minecraftkey) {
        IResourceManager iresourcemanager = (IResourceManager) this.namespacedManagers.get(minecraftkey.getNamespace());

        return iresourcemanager != null ? iresourcemanager.getResourceStack(minecraftkey) : List.of();
    }

    @Override
    public Map<MinecraftKey, IResource> listResources(String s, Predicate<MinecraftKey> predicate) {
        checkTrailingDirectoryPath(s);
        Map<MinecraftKey, IResource> map = new TreeMap();
        Iterator iterator = this.namespacedManagers.values().iterator();

        while (iterator.hasNext()) {
            ResourceManagerFallback resourcemanagerfallback = (ResourceManagerFallback) iterator.next();

            map.putAll(resourcemanagerfallback.listResources(s, predicate));
        }

        return map;
    }

    @Override
    public Map<MinecraftKey, List<IResource>> listResourceStacks(String s, Predicate<MinecraftKey> predicate) {
        checkTrailingDirectoryPath(s);
        Map<MinecraftKey, List<IResource>> map = new TreeMap();
        Iterator iterator = this.namespacedManagers.values().iterator();

        while (iterator.hasNext()) {
            ResourceManagerFallback resourcemanagerfallback = (ResourceManagerFallback) iterator.next();

            map.putAll(resourcemanagerfallback.listResourceStacks(s, predicate));
        }

        return map;
    }

    private static void checkTrailingDirectoryPath(String s) {
        if (s.endsWith("/")) {
            throw new IllegalArgumentException("Trailing slash in path " + s);
        }
    }

    @Override
    public Stream<IResourcePack> listPacks() {
        return this.packs.stream();
    }

    @Override
    public void close() {
        this.packs.forEach(IResourcePack::close);
    }
}
