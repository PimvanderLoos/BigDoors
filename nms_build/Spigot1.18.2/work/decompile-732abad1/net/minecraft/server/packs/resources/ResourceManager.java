package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;

public class ResourceManager implements IReloadableResourceManager {

    private final Map<String, ResourceManagerFallback> namespacedManagers;
    private final List<IResourcePack> packs;

    public ResourceManager(EnumResourcePackType enumresourcepacktype, List<IResourcePack> list) {
        this.packs = List.copyOf(list);
        Map<String, ResourceManagerFallback> map = new HashMap();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            IResourcePack iresourcepack = (IResourcePack) iterator.next();
            Iterator iterator1 = iresourcepack.getNamespaces(enumresourcepacktype).iterator();

            while (iterator1.hasNext()) {
                String s = (String) iterator1.next();

                ((ResourceManagerFallback) map.computeIfAbsent(s, (s1) -> {
                    return new ResourceManagerFallback(enumresourcepacktype, s1);
                })).add(iresourcepack);
            }
        }

        this.namespacedManagers = map;
    }

    @Override
    public Set<String> getNamespaces() {
        return this.namespacedManagers.keySet();
    }

    @Override
    public IResource getResource(MinecraftKey minecraftkey) throws IOException {
        IResourceManager iresourcemanager = (IResourceManager) this.namespacedManagers.get(minecraftkey.getNamespace());

        if (iresourcemanager != null) {
            return iresourcemanager.getResource(minecraftkey);
        } else {
            throw new FileNotFoundException(minecraftkey.toString());
        }
    }

    @Override
    public boolean hasResource(MinecraftKey minecraftkey) {
        IResourceManager iresourcemanager = (IResourceManager) this.namespacedManagers.get(minecraftkey.getNamespace());

        return iresourcemanager != null ? iresourcemanager.hasResource(minecraftkey) : false;
    }

    @Override
    public List<IResource> getResources(MinecraftKey minecraftkey) throws IOException {
        IResourceManager iresourcemanager = (IResourceManager) this.namespacedManagers.get(minecraftkey.getNamespace());

        if (iresourcemanager != null) {
            return iresourcemanager.getResources(minecraftkey);
        } else {
            throw new FileNotFoundException(minecraftkey.toString());
        }
    }

    @Override
    public Collection<MinecraftKey> listResources(String s, Predicate<String> predicate) {
        Set<MinecraftKey> set = Sets.newHashSet();
        Iterator iterator = this.namespacedManagers.values().iterator();

        while (iterator.hasNext()) {
            ResourceManagerFallback resourcemanagerfallback = (ResourceManagerFallback) iterator.next();

            set.addAll(resourcemanagerfallback.listResources(s, predicate));
        }

        List<MinecraftKey> list = Lists.newArrayList(set);

        Collections.sort(list);
        return list;
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
